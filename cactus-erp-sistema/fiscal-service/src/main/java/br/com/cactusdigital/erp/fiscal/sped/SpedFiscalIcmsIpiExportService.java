package br.com.cactusdigital.erp.fiscal.sped;

import br.com.cactusdigital.erp.common.sped.SpedLineBuilder;
import br.com.cactusdigital.erp.fiscal.client.CadastroClient;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.entity.CteCabecalho;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronica;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronicaItem;
import br.com.cactusdigital.erp.fiscal.repository.CteCabecalhoRepository;
import br.com.cactusdigital.erp.fiscal.repository.NotaFiscalEletronicaRepository;
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
 * Export da EFD ICMS/IPI (SPED Fiscal) a partir de {@link NotaFiscalEletronica} (Bloco C)
 * e {@link CteCabecalho} (Bloco D) já emitidos por este serviço — Bloco E (apuração)
 * agrega os débitos de ICMS/IPI dos dois blocos anteriores.
 * <p>
 * <b>Recorte de escopo, deliberado e registrado</b> (mesmo espírito da simplificação já
 * documentada em {@code NotaFiscalEletronica}/{@code CteCabecalho} — cobrir o cenário
 * dominante, não as ~35/~25 tabelas satélite do {@code fenix.sql}):
 * <ul>
 *   <li>Só documentos {@code AUTORIZADA} entram no arquivo — {@code RASCUNHO}/
 *   {@code ENVIADA}/{@code REJEITADA}/{@code ERRO} nunca aparecem numa EFD real;</li>
 *   <li>Só SAÍDAS (vendas emitidas por este serviço) — este monorepo não importa XML de
 *   NF-e de ENTRADA (compra), gap já registrado desde a Fase 3 do {@code gestao-compras-service}
 *   no CLAUDE.md raiz — então Bloco C/D só tem {@code IND_OPER=1} (saída), nunca entrada;</li>
 *   <li>Blocos G (CIAP — crédito do ativo permanente), H (inventário físico) e K (controle
 *   de produção/estoque) **fora de escopo** — nenhum dado de origem existe pra alimentá-los
 *   neste serviço (CIAP não é rastreado, inventário/produção vivem no
 *   {@code controle-estoque-service}, não conectado ao {@code fiscal-service});</li>
 *   <li>Bloco E (apuração) só soma DÉBITOS (saídas) — sem crédito de entrada (mesmo motivo
 *   do corte acima), então {@code VL_ICMS_RECOLHER} nesta v1 é sempre igual ao total de
 *   débitos, nunca líquido de crédito real.</li>
 * </ul>
 * <b>Nível de confiança do layout</b>: mesmo padrão de honestidade do {@code
 * SpedContabilExportService} (ECD) — estrutura de blocos/registros confirmada por pesquisa
 * em múltiplas fontes (Guia Prático oficial da EFD-ICMS/IPI, Tecnospeed), mas o
 * preenchimento campo-a-campo NUNCA validado contra o validador PVA da Receita. O layout do
 * registro {@code C190} tem uma confirmação a mais que os demais: a ORDEM dos campos
 * agregados (CST_ICMS, CFOP, ALIQ_ICMS, VL_OPR, VL_BC_ICMS, VL_ICMS, ..., VL_IPI) bate
 * exatamente com {@code view_sped_c190} do próprio {@code fenix.sql}, um dado real do
 * domínio do projeto, não só pesquisa externa. Campos de classificação sem confirmação
 * (ex: {@code COD_VER}/{@code IND_PERFIL} do {@code 0000}) ficam deliberadamente em branco
 * — nunca um valor "chutado" que pareça oficial.
 */
@Service
@RequiredArgsConstructor
public class SpedFiscalIcmsIpiExportService {

    private final NotaFiscalEletronicaRepository notaFiscalEletronicaRepository;
    private final CteCabecalhoRepository cteCabecalhoRepository;
    private final CadastroClient cadastroClient;

    @Transactional(readOnly = true)
    public String gerarEfdIcmsIpi(Long empresaId, LocalDate dataInicio, LocalDate dataFim) {
        EmpresaFiscalDTO empresa = cadastroClient.buscarEmpresa(empresaId);
        List<NotaFiscalEletronica> notas = notaFiscalEletronicaRepository.buscarAutorizadasNoPeriodoComItens(
                empresaId, inicioDoDia(dataInicio), fimExclusivo(dataFim));
        List<CteCabecalho> ctes = cteCabecalhoRepository.buscarAutorizadosNoPeriodoComComponentes(
                empresaId, inicioDoDia(dataInicio), fimExclusivo(dataFim));

        List<String> linhas = new ArrayList<>();
        linhas.add(registro0000(empresa, dataInicio, dataFim));
        linhas.add(new SpedLineBuilder().campo("0001").campo("0").build());

        adicionarBlocoC(linhas, notas);
        adicionarBlocoD(linhas, ctes);
        adicionarBlocoE(linhas, notas, ctes);

        linhas.add(registro9999(linhas.size() + 1));
        return String.join("\r\n", linhas) + "\r\n";
    }

    // ---- Bloco 0 — abertura -------------------------------------------------------------

    private static String registro0000(EmpresaFiscalDTO empresa, LocalDate dataInicio, LocalDate dataFim) {
        return new SpedLineBuilder()
                .campo("0000")
                .campo("") // COD_VER — versão do leiaute, não confirmada nesta sessão (ver Javadoc da classe)
                .campo("0") // COD_FIN — 0 = arquivo original
                .data(dataInicio)
                .data(dataFim)
                .campo(empresa.razaoSocial())
                .campo(empresa.cnpj())
                .campo(empresa.uf())
                .campo(empresa.inscricaoEstadual())
                .campo(empresa.codigoIbgeMunicipio())
                .campo("") // IM — inscrição municipal, não capturada em EmpresaFiscalDTO
                .campo("") // SUFRAMA
                .campo("") // IND_PERFIL — não confirmado
                .campo("") // IND_ATIV — não confirmado
                .build();
    }

    // ---- Bloco C — NF-e (saídas) ---------------------------------------------------------

    private void adicionarBlocoC(List<String> linhas, List<NotaFiscalEletronica> notas) {
        linhas.add(new SpedLineBuilder().campo("C001").campo(notas.isEmpty() ? "1" : "0").build());

        List<NotaFiscalEletronicaItem> todosItens = new ArrayList<>();
        for (NotaFiscalEletronica nota : notas) {
            linhas.add(registroC100(nota));
            for (NotaFiscalEletronicaItem item : nota.getItens()) {
                linhas.add(registroC170(item));
            }
            todosItens.addAll(nota.getItens());
        }
        agruparAnaliticoIcmsPorItens(todosItens).stream()
                .map(SpedFiscalIcmsIpiExportService::registroC190)
                .forEach(linhas::add);

        int linhasC100EC170 = notas.size() + todosItens.size();
        linhas.add(new SpedLineBuilder().campo("C990").campo(linhasC100EC170 + 2).build());
    }

    private static String registroC100(NotaFiscalEletronica nota) {
        BigDecimal valorBaseIcms = somar(nota.getItens(), NotaFiscalEletronicaItem::getValorBaseIcms);
        BigDecimal valorIcms = somar(nota.getItens(), NotaFiscalEletronicaItem::getValorIcms);
        BigDecimal valorIpi = somar(nota.getItens(), NotaFiscalEletronicaItem::getValorIpi);
        BigDecimal valorPis = somar(nota.getItens(), NotaFiscalEletronicaItem::getValorPis);
        BigDecimal valorCofins = somar(nota.getItens(), NotaFiscalEletronicaItem::getValorCofins);
        BigDecimal valorMercadoria = somar(nota.getItens(), NotaFiscalEletronicaItem::getValorTotal);
        LocalDate dataEmissao = dataDoInstant(nota.getDataEmissao());

        return new SpedLineBuilder()
                .campo("C100")
                .campo("1") // IND_OPER — 1 = saída (só vendas emitidas, ver disclaimer da classe)
                .campo("0") // IND_EMIT — 0 = emissão própria
                .campo("") // COD_PART (destinatário — cadastro de participantes fora de escopo desta v1)
                .campo("55") // COD_MOD
                .campo("00") // COD_SIT — 00 = documento regular
                .campo(nota.getSerie())
                .campo(nota.getNumero())
                .campo(nota.getChaveAcesso())
                .data(dataEmissao)
                .data(dataEmissao)
                .valor(nota.getValorTotal())
                .campo("0") // IND_PGTO — 0 = à vista (não rastreado por forma de pagamento nesta v1)
                .valor(BigDecimal.ZERO) // VL_DESC
                .valor(BigDecimal.ZERO) // VL_ABAT_NT
                .valor(valorMercadoria)
                .campo("0").valor(BigDecimal.ZERO).valor(BigDecimal.ZERO).valor(BigDecimal.ZERO) // IND_FRT/VL_FRT/VL_SEG/VL_OUT_DA
                .valor(valorBaseIcms)
                .valor(valorIcms)
                .valor(BigDecimal.ZERO).valor(BigDecimal.ZERO) // VL_BC_ICMS_ST/VL_ICMS_ST (sem ST rastreado nesta v1)
                .valor(valorIpi)
                .valor(valorPis)
                .valor(valorCofins)
                .valor(BigDecimal.ZERO).valor(BigDecimal.ZERO) // VL_PIS_ST/VL_COFINS_ST
                .build();
    }

    private static String registroC170(NotaFiscalEletronicaItem item) {
        return new SpedLineBuilder()
                .campo("C170")
                .campo(item.getNumeroItem())
                .campo(item.getProdutoId())
                .campo(item.getDescricao())
                .valor(item.getQuantidade())
                .campo(item.getUnidade())
                .valor(item.getValorTotal())
                .valor(item.getValorDesconto())
                .campo("0") // IND_MOV — 0 = movimenta estoque
                .campo(cstOuCsosn(item))
                .campo(item.getCfop())
                .campo("") // COD_NAT — natureza da operação, não capturada
                .valor(item.getValorBaseIcms())
                .valor(item.getAliquotaIcms())
                .valor(item.getValorIcms())
                .valor(BigDecimal.ZERO).valor(BigDecimal.ZERO).valor(BigDecimal.ZERO) // VL_BC_ICMS_ST/ALIQ_ST/VL_ICMS_ST
                .campo("") // CST_IPI — não capturado em NotaFiscalEletronicaItem nesta v1 (só alíquota/valor)
                .campo("") // COD_ENQ — enquadramento legal do IPI
                .valor(item.getValorBaseIcms()) // VL_BC_IPI — sem base própria de IPI capturada, aproxima pela base ICMS
                .valor(item.getAliquotaIpi())
                .valor(item.getValorIpi())
                .campo(item.getCstPis())
                .valor(item.getValorBaseIcms())
                .valor(item.getAliquotaPis())
                .valor(item.getValorPis())
                .campo(item.getCstCofins())
                .valor(item.getValorBaseIcms())
                .valor(item.getAliquotaCofins())
                .valor(item.getValorCofins())
                .campo("") // COD_CTA — vínculo com plano de contas (contabilidade-service, não integrado nesta v1)
                .valor(BigDecimal.ZERO) // VL_ABAT_NT
                .build();
    }

    private static String registroC190(LinhaAnaliticaIcms linha) {
        return new SpedLineBuilder()
                .campo("C190")
                .campo(linha.cstOuCsosn())
                .campo(linha.cfop())
                .valor(linha.aliquota())
                .valor(linha.valorOperacao())
                .valor(linha.baseCalculoIcms())
                .valor(linha.valorIcms())
                .valor(BigDecimal.ZERO) // VL_BC_ICMS_ST — sem ST rastreado nesta v1
                .valor(BigDecimal.ZERO) // VL_ICMS_ST
                .valor(BigDecimal.ZERO) // VL_RED_BC
                .valor(linha.valorIpi())
                .build();
    }

    // ---- Bloco D — CT-e (saídas) ----------------------------------------------------------

    private void adicionarBlocoD(List<String> linhas, List<CteCabecalho> ctes) {
        linhas.add(new SpedLineBuilder().campo("D001").campo(ctes.isEmpty() ? "1" : "0").build());

        ctes.stream().map(SpedFiscalIcmsIpiExportService::registroD100).forEach(linhas::add);
        agruparAnaliticoIcmsPorCtes(ctes).stream()
                .map(SpedFiscalIcmsIpiExportService::registroD190)
                .forEach(linhas::add);

        linhas.add(new SpedLineBuilder().campo("D990").campo(ctes.size() + 2).build());
    }

    private static String registroD100(CteCabecalho cte) {
        LocalDate dataEmissao = dataDoInstant(cte.getDataEmissao());
        return new SpedLineBuilder()
                .campo("D100")
                .campo("1") // IND_OPER — 1 = prestação (saída)
                .campo("0") // IND_EMIT — 0 = emissão própria
                .campo("") // COD_PART
                .campo("57") // COD_MOD
                .campo("00") // COD_SIT
                .campo(cte.getSerie())
                .campo(cte.getNumero())
                .campo(cte.getChaveAcesso())
                .data(dataEmissao)
                .data(dataEmissao)
                .campo("") // TP_CT-e — tipo (normal/complementar/anulação), sempre Normal nesta v1
                .campo("") // CHV_CTE_REF
                .valor(cte.getValorTotalServico())
                .valor(BigDecimal.ZERO) // VL_DESC
                .campo("") // IND_FRT
                .valor(cte.getValorTotalServico())
                .valor(cte.getBaseCalculoIcms())
                .valor(cte.getValorIcms())
                .valor(BigDecimal.ZERO) // VL_NT
                .build();
    }

    private static String registroD190(LinhaAnaliticaIcms linha) {
        return new SpedLineBuilder()
                .campo("D190")
                .campo(linha.cstOuCsosn())
                .campo(linha.cfop())
                .valor(linha.aliquota())
                .valor(linha.valorOperacao())
                .valor(linha.baseCalculoIcms())
                .valor(linha.valorIcms())
                .valor(BigDecimal.ZERO) // VL_ISS — não se aplica a CT-e (fica só como padding estrutural)
                .valor(BigDecimal.ZERO) // VL_BC_ICMS_ST
                .build();
    }

    // ---- Bloco E — apuração (só débitos de saída, ver disclaimer da classe) --------------

    private void adicionarBlocoE(List<String> linhas, List<NotaFiscalEletronica> notas, List<CteCabecalho> ctes) {
        linhas.add(new SpedLineBuilder().campo("E001").campo("0").build());

        BigDecimal debitoIcmsNfe = notas.stream()
                .flatMap(n -> n.getItens().stream())
                .map(NotaFiscalEletronicaItem::getValorIcms)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal debitoIcmsCte = somar(ctes, CteCabecalho::getValorIcms);
        BigDecimal totalDebitoIcms = debitoIcmsNfe.add(debitoIcmsCte);

        BigDecimal totalIpi = notas.stream()
                .flatMap(n -> n.getItens().stream())
                .map(NotaFiscalEletronicaItem::getValorIpi)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        linhas.add(new SpedLineBuilder().campo("E100").build());
        linhas.add(new SpedLineBuilder()
                .campo("E110")
                .valor(totalDebitoIcms) // VL_TOT_DEBITOS
                .valor(BigDecimal.ZERO).valor(totalDebitoIcms) // VL_AJ_DEBITOS/VL_TOT_AJ_DEBITOS
                .valor(BigDecimal.ZERO) // VL_ESTORNOS_CRED
                .valor(BigDecimal.ZERO).valor(BigDecimal.ZERO).valor(BigDecimal.ZERO) // VL_TOT_CREDITOS/VL_AJ_CREDITOS/VL_TOT_AJ_CREDITOS (sem entrada rastreada, ver disclaimer)
                .valor(BigDecimal.ZERO) // VL_ESTORNOS_DEB
                .valor(BigDecimal.ZERO) // VL_SLD_CREDOR_ANT
                .valor(totalDebitoIcms) // VL_SLD_APURADO
                .valor(BigDecimal.ZERO) // VL_TOT_DED
                .valor(totalDebitoIcms) // VL_ICMS_RECOLHER
                .valor(BigDecimal.ZERO) // VL_SLD_CREDOR_TRANSPORTAR
                .campo("") // DEB_ESP
                .build());

        linhas.add(new SpedLineBuilder().campo("E200").build());
        linhas.add(new SpedLineBuilder()
                .campo("E210")
                .campo("0") // IND_APUR — 0 = mensal
                .valor(BigDecimal.ZERO) // VL_SD_ANT
                .valor(totalIpi) // VL_DEB
                .valor(BigDecimal.ZERO) // VL_CRED (sem entrada rastreada)
                .valor(BigDecimal.ZERO).valor(BigDecimal.ZERO) // VL_OD/VL_OC
                .valor(BigDecimal.ZERO) // VL_SC
                .valor(totalIpi) // VL_SD_APUR — sem crédito de entrada, igual ao débito
                .valor(totalIpi) // VL_TOT_DEB
                .valor(BigDecimal.ZERO) // VL_TOT_CRED
                .build());

        linhas.add(new SpedLineBuilder().campo("E990").campo("6").build());
    }

    // ---- helpers ---------------------------------------------------------------------------

    private static List<LinhaAnaliticaIcms> agruparAnaliticoIcmsPorItens(List<NotaFiscalEletronicaItem> itens) {
        return itens.stream()
                .collect(Collectors.groupingBy(item -> new ChaveAnalitica(cstOuCsosn(item), item.getCfop(), valorOuZero(item.getAliquotaIcms()))))
                .entrySet().stream()
                .map(entrada -> new LinhaAnaliticaIcms(
                        entrada.getKey().cstOuCsosn(), entrada.getKey().cfop(), entrada.getKey().aliquota(),
                        somar(entrada.getValue(), NotaFiscalEletronicaItem::getValorTotal),
                        somar(entrada.getValue(), NotaFiscalEletronicaItem::getValorBaseIcms),
                        somar(entrada.getValue(), NotaFiscalEletronicaItem::getValorIcms),
                        somar(entrada.getValue(), NotaFiscalEletronicaItem::getValorIpi)))
                .sorted(Comparator.comparing(LinhaAnaliticaIcms::cfop).thenComparing(LinhaAnaliticaIcms::cstOuCsosn))
                .toList();
    }

    private static List<LinhaAnaliticaIcms> agruparAnaliticoIcmsPorCtes(List<CteCabecalho> ctes) {
        return ctes.stream()
                .collect(Collectors.groupingBy(cte -> new ChaveAnalitica(valorOuVazio(cte.getCst()), cfopOuPadrao(cte), valorOuZero(cte.getAliquotaIcms()))))
                .entrySet().stream()
                .map(entrada -> new LinhaAnaliticaIcms(
                        entrada.getKey().cstOuCsosn(), entrada.getKey().cfop(), entrada.getKey().aliquota(),
                        somar(entrada.getValue(), CteCabecalho::getValorTotalServico),
                        somar(entrada.getValue(), CteCabecalho::getBaseCalculoIcms),
                        somar(entrada.getValue(), CteCabecalho::getValorIcms),
                        BigDecimal.ZERO))
                .sorted(Comparator.comparing(LinhaAnaliticaIcms::cfop).thenComparing(LinhaAnaliticaIcms::cstOuCsosn))
                .toList();
    }

    /** Chave de agrupamento — CST/CSOSN + CFOP + alíquota — usada tanto pra itens de NF-e quanto pra CT-e. */
    private record ChaveAnalitica(String cstOuCsosn, String cfop, BigDecimal aliquota) {
    }

    private static String cstOuCsosn(NotaFiscalEletronicaItem item) {
        return item.getCstIcms() != null ? item.getCstIcms() : valorOuVazio(item.getCsosn());
    }

    private static String cfopOuPadrao(CteCabecalho cte) {
        return cte.getCfop() != null ? cte.getCfop() : "5353";
    }

    private static String valorOuVazio(String valor) {
        return valor != null ? valor : "";
    }

    private static BigDecimal valorOuZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
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
