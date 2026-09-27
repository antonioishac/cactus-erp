package br.com.cactusdigital.erp.fiscal.sped;

import br.com.cactusdigital.erp.common.sped.SpedLineBuilder;
import br.com.cactusdigital.erp.fiscal.client.CadastroClient;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronica;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronicaItem;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalServico;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalServicoItem;
import br.com.cactusdigital.erp.fiscal.repository.NotaFiscalEletronicaRepository;
import br.com.cactusdigital.erp.fiscal.repository.NotaFiscalServicoRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Export da EFD Contribuições (PIS/Pasep, COFINS) a partir de {@link NotaFiscalEletronica}
 * (Bloco C — mercadoria) e {@link NotaFiscalServico} (Bloco A — serviço) já emitidos por
 * este serviço. Mesmo mecanismo (`SpedLineBuilder`) e mesmo nível de honestidade já
 * registrados em {@code SpedFiscalIcmsIpiExportService} (EFD ICMS/IPI) e
 * {@code SpedContabilExportService} (ECD) — não repetido aqui em detalhe, ver Javadoc
 * daquelas classes pro padrão geral.
 * <p>
 * <b>Recorte de escopo específico desta EFD, além do já registrado nas irmãs</b>:
 * <ul>
 *   <li>Sem Bloco D (serviços de transporte/comunicação — {@code CteCabecalho} não tem
 *   campos de PIS/COFINS capturados, só ICMS) nem Bloco F (demais operações) — ambos fora
 *   de escopo, sem dado de origem;</li>
 *   <li>Bloco A (NFS-e) não tem PIS/COFINS por item — {@code NotaFiscalServicoItem} só
 *   captura ISS (Fase 9 do CLAUDE.md raiz nunca modelou PIS/COFINS de serviço) — os campos
 *   de contribuição do {@code A170} ficam zerados/em branco, registrado explicitamente, não
 *   escondido;</li>
 *   <li>Regime não-cumulativo assumido pra todo o Bloco M (sem rastreio de regime cumulativo
 *   × não-cumulativo por empresa) — simplificação deliberada, mesmo padrão de
 *   "não modelar o que não tem fonte real" já usado no resto do módulo;</li>
 *   <li>Sem créditos de entrada ({@code M100}/{@code M105}) nem Bloco P (CPRB) — mesmo corte
 *   já registrado na EFD ICMS/IPI (sem XML de compra importado).</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class SpedContribuicoesExportService {

    private final NotaFiscalEletronicaRepository notaFiscalEletronicaRepository;
    private final NotaFiscalServicoRepository notaFiscalServicoRepository;
    private final CadastroClient cadastroClient;

    @Transactional(readOnly = true)
    public String gerarEfdContribuicoes(Long empresaId, LocalDate dataInicio, LocalDate dataFim) {
        EmpresaFiscalDTO empresa = cadastroClient.buscarEmpresa(empresaId);
        List<NotaFiscalEletronica> notas = notaFiscalEletronicaRepository.buscarAutorizadasNoPeriodoComItens(
                empresaId, inicioDoDia(dataInicio), fimExclusivo(dataFim));
        List<NotaFiscalServico> notasServico = notaFiscalServicoRepository.buscarAutorizadasNoPeriodoComItens(
                empresaId, inicioDoDia(dataInicio), fimExclusivo(dataFim));

        List<String> linhas = new ArrayList<>();
        linhas.add(registro0000(empresa, dataInicio, dataFim));
        linhas.add(new SpedLineBuilder().campo("0001").campo("0").build());

        adicionarBlocoA(linhas, notasServico);
        List<NotaFiscalEletronicaItem> itensMercadoria = adicionarBlocoC(linhas, notas);
        adicionarBlocoM(linhas, itensMercadoria);

        linhas.add(registro9999(linhas.size() + 1));
        return String.join("\r\n", linhas) + "\r\n";
    }

    // ---- Bloco 0 — abertura -------------------------------------------------------------

    private static String registro0000(EmpresaFiscalDTO empresa, LocalDate dataInicio, LocalDate dataFim) {
        return new SpedLineBuilder()
                .campo("0000")
                .campo("") // COD_VER — não confirmado nesta sessão
                .campo("0") // TIPO_ESCRIT — 0 = original
                .campo("0") // IND_SIT_ESP
                .campo("0") // NUM_REC_ANTERIOR
                .data(dataInicio)
                .data(dataFim)
                .campo(empresa.razaoSocial())
                .campo(empresa.cnpj())
                .campo(empresa.uf())
                .campo(empresa.codigoIbgeMunicipio())
                .campo("") // IM
                .campo("") // IND_NAT_PJ — não confirmado
                .campo("") // IND_ATIV — não confirmado
                .build();
    }

    // ---- Bloco A — NFS-e (serviço) --------------------------------------------------------

    private void adicionarBlocoA(List<String> linhas, List<NotaFiscalServico> notasServico) {
        linhas.add(new SpedLineBuilder().campo("A001").campo(notasServico.isEmpty() ? "1" : "0").build());

        int linhasA170 = 0;
        for (NotaFiscalServico nota : notasServico) {
            linhas.add(registroA100(nota));
            for (NotaFiscalServicoItem item : nota.getItens()) {
                linhas.add(registroA170(item));
                linhasA170++;
            }
        }

        linhas.add(new SpedLineBuilder().campo("A990").campo(notasServico.size() + linhasA170 + 2).build());
    }

    private static String registroA100(NotaFiscalServico nota) {
        LocalDate dataEmissao = dataDoInstant(nota.getDataEmissao());
        return new SpedLineBuilder()
                .campo("A100")
                .campo("1") // IND_OPER — 1 = prestação
                .campo("0") // IND_EMIT — 0 = emissão própria
                .campo("") // COD_PART
                .campo("00") // COD_SIT
                .campo(nota.getSerieRps())
                .campo("") // SUB — subsérie
                .campo(nota.getNumero())
                .data(dataEmissao)
                .data(dataEmissao)
                .valor(nota.getValorServicos())
                .campo("0") // IND_PGTO
                .valor(BigDecimal.ZERO) // VL_DESC
                .build();
    }

    private static String registroA170(NotaFiscalServicoItem item) {
        return new SpedLineBuilder()
                .campo("A170")
                .campo("1")
                .campo("") // COD_ITEM — sem catálogo de item de serviço avulso do lista_servico_id
                .campo(item.getDiscriminacao())
                .valor(item.getValorServicos())
                .valor(BigDecimal.ZERO) // VL_DESC
                .campo("") // NAT_BC_CRED
                .campo("") // CST_PIS — não capturado, ver disclaimer da classe
                .valor(BigDecimal.ZERO) // VL_BC_PIS
                .valor(BigDecimal.ZERO).valor(BigDecimal.ZERO) // ALIQ_PIS/VL_PIS
                .campo("") // CST_COFINS
                .valor(BigDecimal.ZERO).valor(BigDecimal.ZERO).valor(BigDecimal.ZERO) // VL_BC_COFINS/ALIQ_COFINS/VL_COFINS
                .build();
    }

    // ---- Bloco C — NF-e (mercadoria) ------------------------------------------------------

    /** Devolve os itens de todas as notas do período — reaproveitados pelo Bloco M (apuração). */
    private List<NotaFiscalEletronicaItem> adicionarBlocoC(List<String> linhas, List<NotaFiscalEletronica> notas) {
        linhas.add(new SpedLineBuilder().campo("C001").campo(notas.isEmpty() ? "1" : "0").build());

        List<NotaFiscalEletronicaItem> todosItens = new ArrayList<>();
        for (NotaFiscalEletronica nota : notas) {
            linhas.add(registroC100(nota));
            for (NotaFiscalEletronicaItem item : nota.getItens()) {
                linhas.add(registroC170(item));
            }
            todosItens.addAll(nota.getItens());
        }

        linhas.add(new SpedLineBuilder().campo("C990").campo(notas.size() + todosItens.size() + 2).build());
        return todosItens;
    }

    private static String registroC100(NotaFiscalEletronica nota) {
        BigDecimal valorPis = somar(nota.getItens(), NotaFiscalEletronicaItem::getValorPis);
        BigDecimal valorCofins = somar(nota.getItens(), NotaFiscalEletronicaItem::getValorCofins);
        LocalDate dataEmissao = dataDoInstant(nota.getDataEmissao());

        return new SpedLineBuilder()
                .campo("C100")
                .campo("1") // IND_OPER — 1 = saída
                .campo("0") // IND_EMIT — 0 = emissão própria
                .campo("") // COD_PART
                .campo("55") // COD_MOD
                .campo("00") // COD_SIT
                .campo(nota.getSerie())
                .campo(nota.getNumero())
                .campo(nota.getChaveAcesso())
                .data(dataEmissao)
                .data(dataEmissao)
                .valor(nota.getValorTotal())
                .campo("0") // IND_PGTO
                .valor(BigDecimal.ZERO) // VL_DESC
                .valor(BigDecimal.ZERO) // VL_ABAT_NT
                .valor(valorPis)
                .valor(valorCofins)
                .build();
    }

    private static String registroC170(NotaFiscalEletronicaItem item) {
        return new SpedLineBuilder()
                .campo("C170")
                .campo(item.getNumeroItem())
                .campo(item.getProdutoId())
                .campo(item.getDescricao())
                .valor(item.getValorTotal())
                .valor(item.getValorDesconto())
                .campo("") // NAT_BC_CRED
                .campo(item.getCstPis())
                .valor(item.getValorBaseIcms()) // VL_BC_PIS — sem base própria de PIS capturada, aproxima pela base ICMS
                .valor(item.getAliquotaPis())
                .valor(item.getValorPis())
                .campo(item.getCstCofins())
                .valor(item.getValorBaseIcms())
                .valor(item.getAliquotaCofins())
                .valor(item.getValorCofins())
                .campo("") // COD_CTA
                .build();
    }

    // ---- Bloco M — apuração da contribuição (regime não-cumulativo assumido) -------------

    /** {@code M001} abre o bloco M inteiro (uma vez só — PIS/COFINS são seções DENTRO dele, não blocos próprios); {@code M990} fecha, contando todas as linhas M emitidas. */
    private void adicionarBlocoM(List<String> linhas, List<NotaFiscalEletronicaItem> itens) {
        BigDecimal totalPis = somar(itens, NotaFiscalEletronicaItem::getValorPis);
        BigDecimal totalCofins = somar(itens, NotaFiscalEletronicaItem::getValorCofins);
        List<LinhaApuracaoContribuicao> porCstPis = agruparPorCst(itens, NotaFiscalEletronicaItem::getCstPis, NotaFiscalEletronicaItem::getValorPis);
        List<LinhaApuracaoContribuicao> porCstCofins = agruparPorCst(itens, NotaFiscalEletronicaItem::getCstCofins, NotaFiscalEletronicaItem::getValorCofins);

        int linhasBlocoM = 1; // M001

        linhas.add(new SpedLineBuilder().campo("M001").campo(itens.isEmpty() ? "1" : "0").build());
        linhas.add(new SpedLineBuilder()
                .campo("M200")
                .valor(totalPis) // VL_TOT_CONT_NC_PER
                .valor(BigDecimal.ZERO) // VL_TOT_CRED_DESC — sem crédito de entrada
                .valor(BigDecimal.ZERO).valor(BigDecimal.ZERO).valor(BigDecimal.ZERO) // VL_TOT_CONT_NC_DEV/VL_RET_NC/VL_OUT_DED_NC
                .valor(totalPis) // VL_CONT_NC_REC
                .build());
        linhasBlocoM++;
        for (LinhaApuracaoContribuicao linha : porCstPis) {
            linhas.add(registroM210(linha));
            linhasBlocoM++;
        }

        linhas.add(new SpedLineBuilder()
                .campo("M600")
                .valor(totalCofins) // VL_TOT_CONT_NC_PER
                .valor(BigDecimal.ZERO) // VL_TOT_CRED_DESC
                .valor(BigDecimal.ZERO).valor(BigDecimal.ZERO).valor(BigDecimal.ZERO)
                .valor(totalCofins) // VL_CONT_NC_REC
                .build());
        linhasBlocoM++;
        for (LinhaApuracaoContribuicao linha : porCstCofins) {
            linhas.add(registroM610(linha));
            linhasBlocoM++;
        }

        linhasBlocoM++; // o próprio M990
        linhas.add(new SpedLineBuilder().campo("M990").campo(linhasBlocoM).build());
    }

    private static String registroM210(LinhaApuracaoContribuicao linha) {
        return new SpedLineBuilder()
                .campo("M210")
                .campo(linha.cst())
                .valor(linha.baseCalculo()) // VL_REC_BRT — aproximação: usa a mesma base
                .valor(linha.baseCalculo()) // VL_BC_CONT
                .valor(linha.valorContribuicao())
                .build();
    }

    private static String registroM610(LinhaApuracaoContribuicao linha) {
        return new SpedLineBuilder()
                .campo("M610")
                .campo(linha.cst())
                .valor(linha.baseCalculo())
                .valor(linha.baseCalculo())
                .valor(linha.valorContribuicao())
                .build();
    }

    // ---- helpers ---------------------------------------------------------------------------

    private static List<LinhaApuracaoContribuicao> agruparPorCst(
            List<NotaFiscalEletronicaItem> itens,
            Function<NotaFiscalEletronicaItem, String> extratorCst,
            Function<NotaFiscalEletronicaItem, BigDecimal> extratorValor) {
        return itens.stream()
                .collect(Collectors.groupingBy(item -> valorOuVazio(extratorCst.apply(item))))
                .entrySet().stream()
                .map(entrada -> new LinhaApuracaoContribuicao(
                        entrada.getKey(),
                        somar(entrada.getValue(), NotaFiscalEletronicaItem::getValorBaseIcms),
                        somar(entrada.getValue(), extratorValor)))
                .sorted(Comparator.comparing(LinhaApuracaoContribuicao::cst))
                .toList();
    }

    private static String valorOuVazio(String valor) {
        return valor != null ? valor : "";
    }

    private static <T> BigDecimal somar(List<T> itens, Function<T, BigDecimal> extrator) {
        return itens.stream().map(extrator).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static LocalDate dataDoInstant(Instant instant) {
        return instant == null ? null : instant.atZone(ZoneOffset.UTC).toLocalDate();
    }

    private static Instant inicioDoDia(LocalDate data) {
        return data.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    private static Instant fimExclusivo(LocalDate data) {
        return data.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    private static String registro9999(int quantidadeLinhas) {
        return new SpedLineBuilder().campo("9999").campo(quantidadeLinhas).build();
    }
}
