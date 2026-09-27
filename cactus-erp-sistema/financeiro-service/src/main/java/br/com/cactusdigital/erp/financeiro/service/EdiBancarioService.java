package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.financeiro.cnab240.Cnab240ParseException;
import br.com.cactusdigital.erp.financeiro.cnab240.Cnab240RemessaBuilder;
import br.com.cactusdigital.erp.financeiro.cnab240.Cnab240RetornoParser;
import br.com.cactusdigital.erp.financeiro.cnab240.Cnab240RetornoRegistro;
import br.com.cactusdigital.erp.financeiro.cnab240.OcorrenciaRetornoCnab240;
import br.com.cactusdigital.erp.financeiro.dto.BaixaRequest;
import br.com.cactusdigital.erp.financeiro.dto.GerarRemessaRequest;
import br.com.cactusdigital.erp.financeiro.dto.ItemRemessaRequest;
import br.com.cactusdigital.erp.financeiro.dto.RemessaBancariaResponse;
import br.com.cactusdigital.erp.financeiro.dto.RetornoBancarioResponse;
import br.com.cactusdigital.erp.financeiro.entity.Banco;
import br.com.cactusdigital.erp.financeiro.entity.ContaBancaria;
import br.com.cactusdigital.erp.financeiro.entity.ContaReceber;
import br.com.cactusdigital.erp.financeiro.entity.RemessaBancaria;
import br.com.cactusdigital.erp.financeiro.entity.RemessaBancariaItem;
import br.com.cactusdigital.erp.financeiro.entity.RetornoBancario;
import br.com.cactusdigital.erp.financeiro.entity.RetornoBancarioItem;
import br.com.cactusdigital.erp.financeiro.entity.SituacaoTitulo;
import br.com.cactusdigital.erp.financeiro.exception.FinanceiroErrorCode;
import br.com.cactusdigital.erp.financeiro.mapper.EdiBancarioMapper;
import br.com.cactusdigital.erp.financeiro.repository.BancoRepository;
import br.com.cactusdigital.erp.financeiro.repository.ContaBancariaRepository;
import br.com.cactusdigital.erp.financeiro.repository.RemessaBancariaItemRepository;
import br.com.cactusdigital.erp.financeiro.repository.RemessaBancariaRepository;
import br.com.cactusdigital.erp.financeiro.repository.RetornoBancarioItemRepository;
import br.com.cactusdigital.erp.financeiro.repository.RetornoBancarioRepository;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * EDI Bancário — geração de remessa CNAB 240 (registro de título em carteira de cobrança)
 * e leitura de retorno CNAB 240 (baixa automática por nosso número). Só CNAB 240 (decisão
 * confirmada com o usuário — padrão FEBRABAN atual, CNAB 400 fica de fora). Ver Javadoc de
 * {@code Cnab240RemessaBuilder}/{@code Cnab240RetornoParser} pro nível de confiança do
 * layout.
 */
@Service
@RequiredArgsConstructor
public class EdiBancarioService {

    private final RemessaBancariaRepository remessaRepository;
    private final RemessaBancariaItemRepository remessaItemRepository;
    private final RetornoBancarioRepository retornoRepository;
    private final RetornoBancarioItemRepository retornoItemRepository;
    private final ContaBancariaRepository contaBancariaRepository;
    private final ContaBancariaService contaBancariaService;
    private final BancoRepository bancoRepository;
    private final ContaReceberService contaReceberService;
    private final PlatformTransactionManager transactionManager;
    private final EdiBancarioMapper mapper;

    @Transactional(readOnly = true)
    public Page<RemessaBancariaResponse> listarRemessas(Long contaBancariaId, Pageable pageable) {
        Page<RemessaBancaria> pagina = contaBancariaId != null
                ? remessaRepository.findByContaBancariaId(contaBancariaId, pageable)
                : remessaRepository.findAll(pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(pagina, RemessaBancaria::getId, remessaRepository::buscarComItens, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public RemessaBancariaResponse buscarRemessaPorId(Long id) {
        return mapper.toResponse(buscarRemessaEntidade(id));
    }

    @Transactional(readOnly = true)
    public String baixarArquivoRemessa(Long id) {
        return buscarRemessaEntidade(id).getConteudoArquivo();
    }

    /**
     * Gera 1 remessa (1 lote) com N títulos — reserva um "nosso número" novo por título
     * (ver {@code ContaBancariaService#reservarProximoNossoNumero}), snapshot dos dados do
     * sacado informados no request (ver Javadoc de {@code RemessaBancariaItem}) e monta o
     * arquivo CNAB240 completo via {@code Cnab240RemessaBuilder}. Só título
     * {@code ABERTA}/{@code PARCIAL} pode ser incluído.
     */
    @Transactional
    public RemessaBancariaResponse gerarRemessa(GerarRemessaRequest request) {
        ContaBancaria conta = contaBancariaRepository.findById(request.contaBancariaId())
                .orElseThrow(() -> NotFoundException.of("Conta bancária", request.contaBancariaId()));
        Banco banco = bancoRepository.findById(conta.getBancoId())
                .orElseThrow(() -> NotFoundException.of("Banco da conta bancária", conta.getBancoId()));

        long numeroSequencialArquivo = remessaRepository.countByContaBancariaId(conta.getId()) + 1;

        RemessaBancaria remessa = remessaRepository.save(RemessaBancaria.builder()
                .contaBancariaId(conta.getId())
                .empresaId(request.empresaId())
                .dataGeracao(LocalDate.now())
                .numeroSequencialArquivo(numeroSequencialArquivo)
                .quantidadeTitulos(request.itens().size())
                .valorTotal(BigDecimal.ZERO)
                .conteudoArquivo("")
                .build());

        List<RemessaBancariaItem> itens = new ArrayList<>();
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ItemRemessaRequest itemRequest : request.itens()) {
            ContaReceber contaReceber = contaReceberService.buscarEntidade(itemRequest.contaReceberId());
            if (contaReceber.getSituacao() != SituacaoTitulo.ABERTA && contaReceber.getSituacao() != SituacaoTitulo.PARCIAL) {
                throw new ConflictException(FinanceiroErrorCode.TITULO_NAO_APTO_PARA_REMESSA);
            }
            long nossoNumero = contaBancariaService.reservarProximoNossoNumero(conta.getId());
            // zero-padded nas mesmas 11 posições do campo CNAB240 (segmento P) — achado
            // real ao validar ao vivo: o retorno traz o nosso número SEMPRE com zero à
            // esquerda ("00000000003"), gravar sem padding aqui faria o match por
            // nossoNumero falhar sempre no processamento do retorno.
            String nossoNumeroFormatado = String.format("%011d", nossoNumero);

            RemessaBancariaItem item = remessaItemRepository.save(RemessaBancariaItem.builder()
                    .remessa(remessa)
                    .contaReceberId(contaReceber.getId())
                    .nossoNumero(nossoNumeroFormatado)
                    .numeroDocumento(contaReceber.getNumeroDocumento())
                    .dataVencimento(contaReceber.getDataVencimento())
                    .valorTitulo(contaReceber.getValorSaldo())
                    .sacadoNumeroInscricao(itemRequest.sacadoNumeroInscricao())
                    .sacadoNome(itemRequest.sacadoNome())
                    .sacadoLogradouro(itemRequest.sacadoLogradouro())
                    .sacadoBairro(itemRequest.sacadoBairro())
                    .sacadoCep(itemRequest.sacadoCep())
                    .sacadoCidade(itemRequest.sacadoCidade())
                    .sacadoUf(itemRequest.sacadoUf())
                    .build());
            itens.add(item);
            valorTotal = valorTotal.add(item.getValorTitulo());
        }

        String conteudoArquivo;
        try {
            conteudoArquivo = Cnab240RemessaBuilder.gerar(conta, banco, remessa, itens);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new BusinessException(FinanceiroErrorCode.CONFIGURACAO_COBRANCA_INCOMPATIVEL_COM_LAYOUT, e.getMessage());
        }
        remessa.setConteudoArquivo(conteudoArquivo);
        remessa.setValorTotal(valorTotal);
        remessa.setItens(itens);

        return mapper.toResponse(remessa);
    }

    @Transactional(readOnly = true)
    public Page<RetornoBancarioResponse> listarRetornos(Long contaBancariaId, Pageable pageable) {
        Page<RetornoBancario> pagina = contaBancariaId != null
                ? retornoRepository.findByContaBancariaId(contaBancariaId, pageable)
                : retornoRepository.findAll(pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(pagina, RetornoBancario::getId, retornoRepository::buscarComItens, mapper::toResponse);
    }

    /**
     * Parseia o retorno e, pra cada par T+U, resolve o título por {@code nossoNumero}
     * (contra o que uma remessa nossa já registrou) e dispara baixa automática só quando
     * {@link OcorrenciaRetornoCnab240#geraBaixaAutomatica} — os demais códigos (entrada
     * confirmada/rejeitada, baixa por decurso) ficam só registrados, sem ação automática
     * (ver Javadoc de {@code OcorrenciaRetornoCnab240}, decisão deliberadamente
     * conservadora). Falha ao gerar uma baixa individual (ex: valor não bate com o saldo
     * do título) não derruba o import inteiro — o item fica marcado com o erro em
     * {@code observacao}, pra revisão manual.
     *
     * <p><b>Achado real, deliberadamente sem {@code @Transactional} no método inteiro</b>
     * (validado ao vivo — lock wait timeout reproduzido de forma determinística, não um
     * artefato de conexão presa entre sessões): o INSERT de {@code retorno_bancario}
     * referencia {@code conta_bancaria_id} por FK de banco — o InnoDB toma um lock
     * COMPARTILHADO na linha pai ({@code conta_bancaria}) pra garantir que ela não seja
     * apagada até o commit, e mantém esse lock pela vida inteira da transação. Se este
     * método fosse {@code @Transactional} (envolvendo o loop inteiro), esse lock
     * compartilhado ficaria aberto exatamente enquanto {@link #tentarBaixaAutomaticaEmTransacaoPropria}
     * (transação PRÓPRIA, {@code REQUIRES_NEW}) tenta um lock EXCLUSIVO na mesma linha via
     * {@code ContaBancariaRepository#buscarParaAtualizarSaldo} — autotravamento: a
     * transação externa não está esperando o banco (está rodando código Java, bloqueada
     * de forma síncrona na chamada aninhada), então o MySQL nunca detecta isso como
     * deadlock, só estoura {@code innodb_lock_wait_timeout} (50s) e derruba a baixa com
     * "Lock wait timeout exceeded". Sem {@code @Transactional} aqui, o INSERT do cabeçalho
     * do retorno comita sozinho (cada chamada de repositório Spring Data já é transacional
     * por padrão) ANTES do loop começar, liberando o lock compartilhado a tempo — cada
     * baixa/item processado continua tão atômico quanto antes (só não atômico junto do
     * cabeçalho, o que nunca foi a garantia que este fluxo prometia).
     */
    public RetornoBancarioResponse importarRetorno(Long contaBancariaId, String nomeArquivo, byte[] conteudo) {
        if (!contaBancariaRepository.existsById(contaBancariaId)) {
            throw NotFoundException.of("Conta bancária", contaBancariaId);
        }

        List<Cnab240RetornoRegistro> registros;
        try {
            registros = Cnab240RetornoParser.parse(new String(conteudo, StandardCharsets.ISO_8859_1));
        } catch (Cnab240ParseException e) {
            throw new BusinessException(FinanceiroErrorCode.ARQUIVO_CNAB240_INVALIDO, e.getMessage());
        }

        RetornoBancario retorno = retornoRepository.save(RetornoBancario.builder()
                .contaBancariaId(contaBancariaId)
                .nomeArquivo(nomeArquivo)
                .quantidadeRegistros(registros.size())
                .build());

        // Achado real: o segmento P (remessa) grava nosso_numero em 11 posições, o
        // segmento T (retorno) em 20 — o MESMO número vem com quantidades de zero à
        // esquerda diferentes em cada arquivo. Comparação de string crua nunca bateria;
        // normaliza os dois lados pro valor numérico antes de casar.
        List<String> nossosNumerosNormalizados = registros.stream()
                .map(r -> normalizarNossoNumero(r.nossoNumero()))
                .toList();
        Map<String, Long> contaReceberPorNossoNumero = remessaItemRepository.findByNossoNumeroIn(nossosNumerosNormalizados).stream()
                .collect(Collectors.toMap(item -> normalizarNossoNumero(item.getNossoNumero()), RemessaBancariaItem::getContaReceberId, (a, b) -> a));

        List<RetornoBancarioItem> itens = new ArrayList<>();
        for (Cnab240RetornoRegistro registro : registros) {
            Long contaReceberId = contaReceberPorNossoNumero.get(normalizarNossoNumero(registro.nossoNumero()));
            boolean processado = false;
            String observacao = null;

            if (contaReceberId == null) {
                observacao = "Nosso número não encontrado em nenhuma remessa gerada por este ERP";
            } else if (OcorrenciaRetornoCnab240.geraBaixaAutomatica(registro.codigoMovimento())) {
                try {
                    tentarBaixaAutomaticaEmTransacaoPropria(contaReceberId, contaBancariaId, registro);
                    processado = true;
                } catch (RuntimeException e) {
                    // observacao é VARCHAR(250) — mensagem de exceção técnica pode vir
                    // mais longa que isso (achado real: DataIntegrityViolationException
                    // ao tentar gravar a observação de uma falha, ironicamente estourando
                    // a mesma classe de erro que a análise de fenix.sql já tinha alertado
                    // em outras colunas). Trunca defensivamente, nunca deixa a gravação do
                    // REGISTRO DA FALHA falhar por conta do tamanho da própria mensagem.
                    observacao = truncar("Falha ao gerar baixa automática: " + e.getMessage(), 250);
                }
            }

            itens.add(retornoItemRepository.save(RetornoBancarioItem.builder()
                    .retorno(retorno)
                    .nossoNumero(registro.nossoNumero())
                    .numeroDocumento(registro.numeroDocumento())
                    .codigoMovimento(registro.codigoMovimento())
                    .dataOcorrencia(registro.dataOcorrencia())
                    .valorTitulo(registro.valorTitulo())
                    .valorPago(registro.valorPago())
                    .contaReceberId(contaReceberId)
                    .processado(processado)
                    .observacao(observacao)
                    .build()));
        }

        // achado real: reconsultar via buscarComItens() logo depois de salvar os itens
        // com um repositório à parte não refletia a coleção na mesma transação (a
        // resposta do POST vinha com itens=[] mesmo com o dado já correto no banco,
        // confirmado via GET subsequente) — setar a coleção explicitamente evita
        // depender de flush/refresh do Hibernate no meio da mesma transação.
        retorno.setItens(itens);
        return mapper.toResponse(retorno);
    }

    /**
     * Roda {@code registrarBaixa} numa transação PRÓPRIA ({@code REQUIRES_NEW}), isolada
     * da transação do import inteiro — achado real ao validar ao vivo: capturar a
     * exceção de um método {@code @Transactional} aninhado com propagação padrão
     * ({@code REQUIRED}, mesma transação) NÃO desfaz o marcador de rollback-only que o
     * Spring já grava assim que a exceção é lançada lá dentro; ao tentar continuar
     * processando (e comitar) o resto do arquivo na mesma transação, o commit falhava com
     * {@code UnexpectedRollbackException} mesmo com o {@code catch} local "engolindo" o
     * erro. Com transação própria, uma baixa que falha (ex: título já quitado por um
     * retorno duplicado) sofre rollback SÓ dela — o resto do import continua saudável.
     */
    private void tentarBaixaAutomaticaEmTransacaoPropria(Long contaReceberId, Long contaBancariaId, Cnab240RetornoRegistro registro) {
        TransactionTemplate transacaoPropria = new TransactionTemplate(transactionManager);
        transacaoPropria.setPropagationBehavior(org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transacaoPropria.executeWithoutResult(status -> contaReceberService.registrarBaixa(contaReceberId, new BaixaRequest(
                registro.dataOcorrencia(), registro.valorPago(), null, null, contaBancariaId, null, null,
                "Baixa automática via retorno CNAB240 (nosso número " + registro.nossoNumero() + ")", null)));
    }

    /** Nosso número é um valor numérico — remove zeros à esquerda e reformata nas mesmas
     * 11 posições usadas na gravação da remessa (ver {@link #gerarRemessa}), pra que o
     * valor de 20 posições que vem no segmento T do retorno bata com o de 11 posições
     * gravado a partir do segmento P. {@code null}/não numérico devolvido como veio (não
     * deveria acontecer com um retorno bem formado, mas não lança exceção por causa disso). */
    private String normalizarNossoNumero(String bruto) {
        if (bruto == null || bruto.isBlank()) {
            return bruto;
        }
        try {
            return String.format("%011d", Long.parseLong(bruto));
        } catch (NumberFormatException e) {
            return bruto;
        }
    }

    private String truncar(String valor, int tamanhoMaximo) {
        return valor.length() > tamanhoMaximo ? valor.substring(0, tamanhoMaximo) : valor;
    }

    private RemessaBancaria buscarRemessaEntidade(Long id) {
        return remessaRepository.findById(id).orElseThrow(() -> NotFoundException.of("Remessa bancária", id));
    }
}
