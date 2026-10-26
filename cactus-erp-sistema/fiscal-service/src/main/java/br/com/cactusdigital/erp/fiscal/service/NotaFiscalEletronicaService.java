package br.com.cactusdigital.erp.fiscal.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.fiscal.AcbrMonitorClient;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.common.saga.SagaOrchestrator;
import br.com.cactusdigital.erp.common.saga.SagaStep;
import br.com.cactusdigital.erp.fiscal.client.CadastroClient;
import br.com.cactusdigital.erp.fiscal.client.VendaClient;
import br.com.cactusdigital.erp.fiscal.client.dto.AliquotaIbsCbsDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.ClassificacaoIbsCbsDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.ClienteFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaConfiguracaoFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.ProdutoFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.VendaClienteDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.VendaItemClienteDTO;
import br.com.cactusdigital.erp.fiscal.dto.NotaFiscalEletronicaResponse;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronica;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronicaItem;
import br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import br.com.cactusdigital.erp.fiscal.mapper.FiscalMapper;
import br.com.cactusdigital.erp.fiscal.repository.NotaFiscalEletronicaRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Compõe e emite uma NF-e a partir de uma {@code VendaCabecalho} (vendas-service, Fase 7)
 * já {@code CONFIRMADA}. Máquina de estados: {@code RASCUNHO} (composta, ainda não
 * enviada) → (emitir) → {@code ENVIADA} → {@code AUTORIZADA}/{@code REJEITADA} (retorno
 * real da SEFAZ, via ACBr) ou {@code ERRO} (falha de transporte/comunicação — não é o
 * mesmo que uma rejeição fiscal).
 * <p>
 * <b>Composição orquestrada via {@link SagaOrchestrator} (terceira aplicação do padrão,
 * ver Javadoc lá)</b>: {@link #gerarDeVenda} reserva o número da NF-e ANTES de montar os
 * itens — se a montagem de algum item falhar depois (produto não encontrado, falha de
 * comunicação), o número já foi reservado (mutação remota, em transação própria do
 * cadastro-base-service) e ficaria "queimado" sem nenhuma nota associada, sem nenhum
 * rastro além de um log — era exatamente esse o gap registrado no BACKLOG.md
 * ("numeração de NF-e pode pular"). A compensação aqui é DELIBERADAMENTE diferente das
 * duas aplicações anteriores (vendas-service/gestao-compras-service): NÃO tenta devolver/
 * decrementar o número reservado (ver Javadoc de {@code cadastro-base-service.NumeroFiscalPulado}
 * pro porquê disso ser arriscado — risco de colisão com reserva concorrente, e a
 * legislação já exige inutilização formal, não reuso silencioso) — em vez disso, registra
 * o gap como consultável ({@code CadastroClient#registrarNumeroNfePulado}), transformando
 * uma perda silenciosa em um registro auditável tanto no {@code fiscal-service}
 * (`SagaExecucao`) quanto no {@code cadastro-base-service} (`NumeroFiscalPulado`).
 * <p>
 * <b>Cancelamento fica fora de escopo desta fase</b> — cancelamento de NF-e tem regra
 * fiscal própria (janela de tempo pós-autorização, evento específico
 * {@code NFE.Cancelar} no ACBr, geração de XML de cancelamento) que não foi construída
 * ainda; registrado no CLAUDE.md como próximo passo, não improvisado aqui.
 */
@Service
@RequiredArgsConstructor
public class NotaFiscalEletronicaService {

    private final NotaFiscalEletronicaRepository repository;
    private final VendaClient vendaClient;
    private final CadastroClient cadastroClient;
    private final AcbrMonitorClient acbrMonitorClient;
    private final NfeIniBuilder iniBuilder;
    private final SagaOrchestrator sagaOrchestrator;
    private final SagaAuditoriaService sagaAuditoriaService;
    private final FiscalMapper mapper;
    private final CalculadoraIbsCbs calculadoraIbsCbs;

    /** CRT do emitente que exige IBS/CBS na NF-e hoje (NT 2025.002-RTC: obrigatório para o Regime Normal desde
     * 03/08/2026; Simples Nacional/MEI aguardam NT futura e seguem sem o grupo). */
    private static final String CRT_REGIME_NORMAL = "3";
    /** "Ano de emissão" das alíquotas de IBS/CBS é o do horário oficial de Brasília. */
    private static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");

    @Value("${cactus.acbr.host}")
    private String acbrHost;

    @Value("${cactus.acbr.porta}")
    private int acbrPorta;

    @Transactional(readOnly = true)
    public Page<NotaFiscalEletronicaResponse> listar(Pageable pageable) {
        Page<NotaFiscalEletronica> pagina = repository.findAll(pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, NotaFiscalEletronica::getId, repository::buscarComItens, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public NotaFiscalEletronicaResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    /**
     * Compõe a NF-e (RASCUNHO) a partir da Venda — busca venda (vendas-service) +
     * cliente/produtos/empresa/configuração fiscal/transportadora (cadastro-base-service),
     * resolve tributação a partir do "resolvido padrão" já cacheado em cada
     * {@code Produto} (Fase 1b). Orquestra, via {@link SagaOrchestrator}, a reserva do
     * número da NF-e seguida da montagem de cada item — ver Javadoc da classe pro porquê
     * e pro que a compensação faz de diferente aqui (registra o gap, não tenta reverter o
     * número). Não chama o ACBr Monitor ainda — isso só acontece em {@link #emitir}.
     */
    @Transactional
    public NotaFiscalEletronicaResponse gerarDeVenda(Long vendaId) {
        if (repository.existsByVendaId(vendaId)) {
            throw new ConflictException(FiscalErrorCode.VENDA_JA_TEM_NOTA_FISCAL);
        }

        VendaClienteDTO venda = vendaClient.buscarVenda(vendaId);
        if (!"CONFIRMADA".equals(venda.situacao())) {
            throw new BusinessException(FiscalErrorCode.VENDA_SITUACAO_INVALIDA_PARA_NOTA_FISCAL);
        }

        // Validação de existência de cliente/transportadora acontece por efeito colateral
        // de CadastroClient (lança FALHA_INTEGRACAO_CADASTRO se não existirem) — dado de
        // fato é buscado de novo em emitir(), pra garantir informação fresca no momento
        // real da emissão (cliente pode ter sido editado nesse meio-tempo).
        cadastroClient.buscarCliente(venda.clienteId());
        if (venda.transportadoraId() != null) {
            cadastroClient.buscarTransportadora(venda.transportadoraId());
        }

        // Produtos e IBS/CBS são lidos e validados ANTES de reservar o número da NF-e: um produto inexistente
        // ou sem classificação de IBS/CBS (obrigatória no Regime Normal) é recusado sem queimar numeração.
        Map<Long, ProdutoFiscalDTO> produtos = new LinkedHashMap<>();
        for (VendaItemClienteDTO itemVenda : venda.itens()) {
            produtos.computeIfAbsent(itemVenda.produtoId(), cadastroClient::buscarProduto);
        }
        boolean regimeNormal = CRT_REGIME_NORMAL.equals(cadastroClient.buscarEmpresa(venda.empresaId()).crt());
        Map<String, ClassificacaoIbsCbsDTO> classificacoes = regimeNormal ? validarIbsCbs(produtos.values()) : Map.of();
        AliquotaIbsCbsDTO aliquotas = regimeNormal ? cadastroClient.buscarAliquotasIbsCbs(LocalDate.now(FUSO_BRASILIA)) : null;

        NotaFiscalEletronica nota = NotaFiscalEletronica.builder()
                .vendaId(venda.id())
                .empresaId(venda.empresaId())
                .clienteId(venda.clienteId())
                .transportadoraId(venda.transportadoraId())
                .valorTotal(venda.valorTotal())
                .situacao(SituacaoNotaFiscal.RASCUNHO)
                .build();

        sagaOrchestrator.executar("GERAR_NOTA_FISCAL_ELETRONICA", nota,
                construirPassosComposicao(nota, venda, produtos, classificacoes, aliquotas), new NotaFiscalSagaAuditor(sagaAuditoriaService));
        calculadoraIbsCbs.totalizar(nota);

        return mapper.toResponse(repository.save(nota));
    }

    private List<SagaStep<NotaFiscalEletronica>> construirPassosComposicao(NotaFiscalEletronica nota, VendaClienteDTO venda,
            Map<Long, ProdutoFiscalDTO> produtos, Map<String, ClassificacaoIbsCbsDTO> classificacoes, AliquotaIbsCbsDTO aliquotas) {
        List<SagaStep<NotaFiscalEletronica>> passos = new ArrayList<>();
        passos.add(new SagaStep<NotaFiscalEletronica>() {
            @Override
            public String nome() {
                return "reservar-numero-nfe";
            }

            @Override
            public void executar(NotaFiscalEletronica n) {
                EmpresaConfiguracaoFiscalDTO configFiscal = cadastroClient.reservarProximoNumeroNfe(venda.empresaId());
                n.setSerie(configFiscal.serieNfe());
                n.setNumero(configFiscal.proximoNumeroNfe());
                n.setAmbiente("PRODUCAO".equalsIgnoreCase(configFiscal.ambienteFiscal()) ? AmbienteFiscal.PRODUCAO : AmbienteFiscal.HOMOLOGACAO);
            }

            @Override
            public void compensar(NotaFiscalEletronica n) {
                cadastroClient.registrarNumeroNfePulado(venda.empresaId(), n.getSerie(), n.getNumero(),
                        "Falha ao montar a NF-e da venda #" + venda.id() + " após reservar o número (compensação de saga)");
            }
        });

        int numeroItem = 1;
        for (VendaItemClienteDTO itemVenda : venda.itens()) {
            int numeroItemFinal = numeroItem++;
            passos.add(new SagaStep<NotaFiscalEletronica>() {
                @Override
                public String nome() {
                    return "montar-item-" + numeroItemFinal;
                }

                @Override
                public void executar(NotaFiscalEletronica n) {
                    ProdutoFiscalDTO produto = produtos.get(itemVenda.produtoId());
                    NotaFiscalEletronicaItem item = montarItem(n, numeroItemFinal, itemVenda, produto);
                    if (aliquotas != null) {
                        calculadoraIbsCbs.calcularItem(item, classificacoes.get(produto.classificacaoIbsCbsPadrao()), aliquotas);
                    }
                    n.getItens().add(item);
                }
            });
        }
        return passos;
    }

    /**
     * Monta o comando {@code NFE.CriarEnviarNFe} (via {@link NfeIniBuilder}) e chama o
     * ACBr Monitor. Nunca deixa a nota em {@code RASCUNHO} indefinidamente: sucesso vira
     * {@code AUTORIZADA}, rejeição fiscal vira {@code REJEITADA}, falha de comunicação
     * vira {@code ERRO} — todas persistidas, nenhuma lança exceção que impeça rastrear o
     * que aconteceu.
     */
    @Transactional
    public NotaFiscalEletronicaResponse emitir(Long id) {
        NotaFiscalEletronica nota = buscarEntidade(id);
        if (nota.getSituacao() != SituacaoNotaFiscal.RASCUNHO && nota.getSituacao() != SituacaoNotaFiscal.ERRO) {
            throw new ConflictException(FiscalErrorCode.NOTA_FISCAL_SITUACAO_INVALIDA);
        }

        EmpresaFiscalDTO emitente = cadastroClient.buscarEmpresa(nota.getEmpresaId());
        ClienteFiscalDTO destinatario = cadastroClient.buscarCliente(nota.getClienteId());
        ClienteFiscalDTO transportadora = nota.getTransportadoraId() != null
                ? cadastroClient.buscarTransportadora(nota.getTransportadoraId())
                : null;

        nota.setDataEmissao(Instant.now());
        if (CRT_REGIME_NORMAL.equals(emitente.crt())) {
            recalcularIbsCbs(nota);
        }
        String comando = iniBuilder.construirComando(nota, emitente, destinatario, transportadora, 1);

        nota.setSituacao(SituacaoNotaFiscal.ENVIADA);
        try {
            String resposta = acbrMonitorClient.enviarComando(acbrHost, acbrPorta, comando);
            if (acbrMonitorClient.respostaComErro(resposta)) {
                nota.setSituacao(SituacaoNotaFiscal.REJEITADA);
                nota.setMensagemSefaz(truncar(resposta, 500));
                return mapper.toResponse(nota);
            }
            nota.setSituacao(SituacaoNotaFiscal.AUTORIZADA);
            nota.setChaveAcesso(extrair(resposta, "ChDFe", 44));
            nota.setProtocoloAutorizacao(extrair(resposta, "NProt", 30));
            nota.setMensagemSefaz(truncar("Autorizada.", 500));
            return mapper.toResponse(nota);
        } catch (IOException ex) {
            nota.setSituacao(SituacaoNotaFiscal.ERRO);
            nota.setMensagemSefaz(truncar("Falha de comunicação com o ACBr Monitor: " + ex.getMessage(), 500));
            return mapper.toResponse(nota);
        }
    }

    /**
     * Toda classificação de IBS/CBS dos produtos, já validada: produto sem classificação (o "resolvido padrão" não
     * foi recalculado, ou a célula tributária não tem IBS/CBS) e classificação que o ERP ainda não emite são
     * recusados aqui — o IBS/CBS é obrigatório por lei na NF-e do Regime Normal, emitir sem ele seria emitir errado.
     */
    private Map<String, ClassificacaoIbsCbsDTO> validarIbsCbs(Iterable<ProdutoFiscalDTO> produtos) {
        Map<String, ClassificacaoIbsCbsDTO> classificacoes = new HashMap<>();
        for (ProdutoFiscalDTO produto : produtos) {
            String codigo = produto.classificacaoIbsCbsPadrao();
            if (codigo == null) {
                throw new BusinessException(FiscalErrorCode.PRODUTO_SEM_CLASSIFICACAO_IBS_CBS,
                        "Produto #" + produto.id() + " (" + produto.nome() + ") sem classificação de IBS/CBS: configure o cClassTrib na "
                                + "configuração tributária do grupo do produto e recalcule a tributação padrão dele");
            }
            ClassificacaoIbsCbsDTO classificacao = classificacoes.computeIfAbsent(codigo, cadastroClient::buscarClassificacaoIbsCbs);
            if (classificacao.motivoNaoSuportada() != null) {
                throw new BusinessException(FiscalErrorCode.CLASSIFICACAO_IBS_CBS_NAO_SUPORTADA,
                        "Produto #" + produto.id() + ", cClassTrib " + codigo + ": " + classificacao.motivoNaoSuportada());
            }
        }
        return classificacoes;
    }

    /** Refaz o IBS/CBS com as alíquotas do ano da emissão (um rascunho composto em dezembro pode ser emitido em janeiro). */
    private void recalcularIbsCbs(NotaFiscalEletronica nota) {
        AliquotaIbsCbsDTO aliquotas = cadastroClient.buscarAliquotasIbsCbs(LocalDate.ofInstant(nota.getDataEmissao(), FUSO_BRASILIA));
        Map<String, ClassificacaoIbsCbsDTO> classificacoes = new HashMap<>();
        for (NotaFiscalEletronicaItem item : nota.getItens()) {
            if (item.getClassificacaoIbsCbs() == null) {
                throw new BusinessException(FiscalErrorCode.PRODUTO_SEM_CLASSIFICACAO_IBS_CBS,
                        "Item " + item.getNumeroItem() + " da NF-e sem classificação de IBS/CBS (rascunho composto antes da obrigatoriedade): "
                                + "gere a NF-e de novo a partir da venda");
            }
            calculadoraIbsCbs.calcularItem(item,
                    classificacoes.computeIfAbsent(item.getClassificacaoIbsCbs(), cadastroClient::buscarClassificacaoIbsCbs), aliquotas);
        }
        calculadoraIbsCbs.totalizar(nota);
    }

    private NotaFiscalEletronicaItem montarItem(NotaFiscalEletronica nota, int numeroItem, VendaItemClienteDTO itemVenda, ProdutoFiscalDTO produto) {
        BigDecimal aliquotaIcms = produto.aliquotaIcmsPadrao();
        BigDecimal valorBaseIcms = itemVenda.valorTotal();
        BigDecimal valorIcms = aliquotaIcms != null
                ? valorBaseIcms.multiply(aliquotaIcms).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                : null;
        BigDecimal aliquotaPis = produto.aliquotaPisPadrao();
        BigDecimal valorPis = aliquotaPis != null
                ? itemVenda.valorTotal().multiply(aliquotaPis).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                : null;
        BigDecimal aliquotaCofins = produto.aliquotaCofinsPadrao();
        BigDecimal valorCofins = aliquotaCofins != null
                ? itemVenda.valorTotal().multiply(aliquotaCofins).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                : null;

        return NotaFiscalEletronicaItem.builder()
                .notaFiscal(nota)
                .numeroItem(numeroItem)
                .produtoId(produto.id())
                .descricao(produto.descricaoPdv() != null ? produto.descricaoPdv() : produto.nome())
                .gtin(produto.gtin())
                .ncm(produto.ncmCodigo())
                .unidade(produto.produtoUnidadeSigla())
                .quantidade(itemVenda.quantidade())
                .valorUnitario(itemVenda.valorUnitario())
                .valorTotal(itemVenda.valorTotal())
                .valorDesconto(itemVenda.valorDesconto())
                .cstIcms(codigoSemPrefixo(produto.cstIcmsPadrao(), "CST_"))
                .csosn(codigoSemPrefixo(produto.csosnPadrao(), "CSOSN_"))
                .aliquotaIcms(aliquotaIcms)
                .valorBaseIcms(valorBaseIcms)
                .valorIcms(valorIcms)
                .aliquotaPis(aliquotaPis)
                .valorPis(valorPis)
                .aliquotaCofins(aliquotaCofins)
                .valorCofins(valorCofins)
                .build();
    }

    /**
     * {@code ProdutoResponse.cstIcmsPadrao}/{@code csosnPadrao} (cadastro-base-service)
     * são tipados como o enum Java {@code CstIcms}/{@code Csosn} — Jackson serializa
     * como o <b>nome</b> da constante ({@code "CST_60"}, {@code "CSOSN_101"}), não o
     * {@code codigo()} de fato ({@code "60"}, {@code "101"}) que a NF-e/ACBr esperam.
     * Achado real ao validar ao vivo (2026-09-03): sem isso, o valor ("CST_60") nem
     * cabia na coluna {@code VARCHAR(3)} — corrigido tirando o prefixo aqui, não
     * alargando a coluna, porque o valor certo pra persistir/emitir é mesmo só o código.
     */
    private String codigoSemPrefixo(String valorEnum, String prefixo) {
        if (valorEnum == null) {
            return null;
        }
        return valorEnum.startsWith(prefixo) ? valorEnum.substring(prefixo.length()) : valorEnum;
    }

    private String truncar(String texto, int max) {
        if (texto == null) {
            return null;
        }
        return texto.length() <= max ? texto : texto.substring(0, max);
    }

    private String extrair(String texto, String chave, int maxLength) {
        if (texto == null) {
            return null;
        }
        var matcher = java.util.regex.Pattern.compile("(?im)^\\s*" + chave + "\\s*=\\s*(.+?)\\s*$").matcher(texto);
        return matcher.find() ? truncar(matcher.group(1), maxLength) : null;
    }

    NotaFiscalEletronica buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Nota fiscal eletrônica", id));
    }
}
