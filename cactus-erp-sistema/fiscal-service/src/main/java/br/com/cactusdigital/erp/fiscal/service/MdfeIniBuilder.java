package br.com.cactusdigital.erp.fiscal.service;

import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.MdfeCabecalho;
import br.com.cactusdigital.erp.fiscal.entity.MdfeDocumentoVinculado;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Monta o payload {@code MDFe.INI} (modelo 58, modal Rodoviário) e o comando ACBr Monitor.
 * Comando confirmado por pesquisa (acbr.sourceforge.io/ACBrMonitor/MDFeCriarEnviarMDFe.html):
 * {@code MDFE.CriarEnviarMDFe(cIniMDFe,[nLote],[nImprimir],...)} — tiro único, mesma família
 * de {@code NFE.CriarEnviarNFe}/{@code CTE.CriarEnviarCTe}.
 *
 * <h3>Confiança do LAYOUT do INI (diferente da do COMANDO)</h3>
 * Os nomes de seção/chave abaixo ({@code [ide]}/{@code [CARR001]}/{@code [emit]}/{@code [Rodo]}/
 * {@code [veicTracao]}/{@code [moto001]}/{@code [DESC001]}/{@code [infCTe001001]}/{@code [tot]})
 * seguem o vocabulário oficial do leiaute MDF-e 3.00 (tags do XML) e o padrão de numeração de
 * seção do ACBr já usado em {@code CteIniBuilder}, mas não foram conferidos contra um
 * {@code MDFe.INI} de referência lido na íntegra nem contra uma instância real do ACBr Monitor —
 * mesmo padrão de honestidade de {@code NfeIniBuilder}/{@code CteIniBuilder}. Nunca chamado ao
 * vivo (decisão de segurança das Fases 8/9/11: certificado de produção configurado).
 *
 * <h3>Simplificações, ver Javadoc de {@link MdfeCabecalho}</h3>
 * Só intra-UF ({@code UFIni=UFFim}, sem {@code [infPercurso]}), um município de carregamento
 * (= emitente), um veículo de tração, um condutor; {@code tpEmit=1} (prestador de serviço de
 * transporte — os documentos vinculados são CT-e próprios); {@code tpRod=06}/{@code tpCar=02}
 * (Outros/Fechada-Baú) como default fixo, sem campo próprio ainda.
 */
@Component
public class MdfeIniBuilder {

    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String construirComando(MdfeCabecalho mdfe, EmpresaFiscalDTO emitente, int lote) {
        return "MDFE.CriarEnviarMDFe(" + construirIniMdfe(mdfe, emitente) + "," + lote + ",0)";
    }

    /**
     * Evento de Encerramento — {@code MDFE.EncerrarMDFe(nChaveMDFE,dtEnc,cMunicipioDescarga,[nCNPJ],[nProtocolo])},
     * assinatura confirmada em acbr.sourceforge.io/ACBrMonitor/MDFeEncerrarMDFe.html (data em dd/MM/yyyy).
     * O protocolo de autorização vai sempre: a doc o exige para MDF-e antigo e ele não custa nada nos demais.
     */
    public String construirComandoEncerramento(MdfeCabecalho mdfe, String cnpjEmitente) {
        return "MDFE.EncerrarMDFe(" + String.join(",",
                aspas(mdfe.getChaveAcesso()),
                aspas(mdfe.getDataEncerramento().format(FORMATO_DATA)),
                aspas(str(mdfe.getMunicipioEncerramentoCodigoIbge())),
                aspas(cnpjEmitente),
                aspas(mdfe.getProtocoloAutorizacao())) + ")";
    }

    /**
     * Evento de Cancelamento — {@code MDFE.CancelarMDFe(nChaveMDFE,nDados,[nCNPJ])}, assinatura confirmada
     * em acbr.sourceforge.io/ACBrMonitor/MDFeCancelarMDFe.html. A justificativa é texto livre, então vai
     * entre aspas (vírgula nela quebraria a lista de parâmetros) e com aspas internas removidas.
     */
    public String construirComandoCancelamento(MdfeCabecalho mdfe, String cnpjEmitente) {
        return "MDFE.CancelarMDFe(" + String.join(",",
                aspas(mdfe.getChaveAcesso()),
                aspas(mdfe.getJustificativaCancelamento()),
                aspas(cnpjEmitente)) + ")";
    }

    private String aspas(String valor) {
        return "\"" + (valor == null ? "" : valor.replace("\"", "")) + "\"";
    }

    public String construirIniMdfe(MdfeCabecalho mdfe, EmpresaFiscalDTO emitente) {
        StringBuilder ini = new StringBuilder();
        secaoIdentificacao(ini, mdfe, emitente);
        secaoCarregamento(ini, mdfe);
        secaoEmitente(ini, emitente);
        secaoRodoviario(ini, mdfe);
        secaoDescarga(ini, mdfe);
        secaoTotais(ini, mdfe);
        return ini.toString();
    }

    private void secaoIdentificacao(StringBuilder ini, MdfeCabecalho mdfe, EmpresaFiscalDTO emitente) {
        secao(ini, "ide");
        chave(ini, "cUF", str(emitente.codigoIbgeUf()));
        chave(ini, "tpAmb", mdfe.getAmbiente() == AmbienteFiscal.PRODUCAO ? "1" : "2");
        chave(ini, "tpEmit", "1");
        chave(ini, "mod", mdfe.getModelo());
        chave(ini, "serie", mdfe.getSerie());
        chave(ini, "nMDF", str(mdfe.getNumero()));
        chave(ini, "cMDF", String.format("%08d", mdfe.getId() % 100_000_000));
        chave(ini, "modal", "1"); // 1 = Rodoviário (no XML do MDF-e o modal é 1 dígito)
        chave(ini, "dhEmi", (mdfe.getDataEmissao() != null
                ? LocalDateTime.ofInstant(mdfe.getDataEmissao(), ZoneId.systemDefault())
                : LocalDateTime.now()).format(FORMATO_DATA_HORA));
        chave(ini, "tpEmis", "1");
        chave(ini, "procEmi", "0");
        chave(ini, "verProc", "CactusERP 1.0");
        chave(ini, "UFIni", mdfe.getUf());
        chave(ini, "UFFim", mdfe.getUf());
        if (mdfe.getDataInicioViagem() != null) {
            chave(ini, "dhIniViagem", mdfe.getDataInicioViagem().format(FORMATO_DATA) + " 00:00:00");
        }
    }

    private void secaoCarregamento(StringBuilder ini, MdfeCabecalho mdfe) {
        secao(ini, "CARR001");
        chave(ini, "cMunCarrega", str(mdfe.getMunicipioCarregamentoCodigoIbge()));
        chave(ini, "xMunCarrega", mdfe.getMunicipioCarregamentoNome());
    }

    private void secaoEmitente(StringBuilder ini, EmpresaFiscalDTO emitente) {
        secao(ini, "emit");
        chave(ini, "CNPJCPF", emitente.cnpj());
        chave(ini, "IE", emitente.inscricaoEstadual());
        chave(ini, "xNome", emitente.razaoSocial());
        chave(ini, "xFant", emitente.nomeFantasia());
        chave(ini, "xLgr", emitente.logradouro());
        chave(ini, "nro", emitente.numero());
        chave(ini, "xCpl", emitente.complemento());
        chave(ini, "xBairro", emitente.bairro());
        chave(ini, "cMun", str(emitente.codigoIbgeMunicipio()));
        chave(ini, "xMun", emitente.municipio());
        chave(ini, "CEP", emitente.cep());
        chave(ini, "UF", emitente.uf());
        chave(ini, "fone", emitente.telefone());
    }

    private void secaoRodoviario(StringBuilder ini, MdfeCabecalho mdfe) {
        secao(ini, "Rodo");
        chave(ini, "RNTRC", mdfe.getRntrc());

        secao(ini, "veicTracao");
        chave(ini, "placa", mdfe.getVeiculoPlaca());
        chave(ini, "RENAVAM", mdfe.getVeiculoRenavam());
        chave(ini, "tara", str(mdfe.getVeiculoTaraKg()));
        chave(ini, "capKG", str(mdfe.getVeiculoCapacidadeKg()));
        chave(ini, "tpRod", "06");
        chave(ini, "tpCar", "02");
        chave(ini, "UF", mdfe.getVeiculoUf());

        secao(ini, "moto001");
        chave(ini, "xNome", mdfe.getCondutorNome());
        chave(ini, "CPF", mdfe.getCondutorCpf());
    }

    /** Um {@code [DESCnnn]} por município de descarga distinto, com os CT-e daquele município em {@code [infCTennnmmm]}. */
    private void secaoDescarga(StringBuilder ini, MdfeCabecalho mdfe) {
        Map<Integer, List<MdfeDocumentoVinculado>> porMunicipio = new LinkedHashMap<>();
        mdfe.getDocumentos().forEach(d -> porMunicipio
                .computeIfAbsent(d.getMunicipioDescargaCodigoIbge(), k -> new java.util.ArrayList<>()).add(d));

        int indiceMunicipio = 1;
        for (List<MdfeDocumentoVinculado> documentos : porMunicipio.values()) {
            String prefixo = pad(indiceMunicipio++);
            secao(ini, "DESC" + prefixo);
            chave(ini, "cMunDescarga", str(documentos.get(0).getMunicipioDescargaCodigoIbge()));
            chave(ini, "xMunDescarga", documentos.get(0).getMunicipioDescargaNome());
            int indiceCte = 1;
            for (MdfeDocumentoVinculado documento : documentos) {
                secao(ini, "infCTe" + prefixo + pad(indiceCte++));
                chave(ini, "chCTe", documento.getCteCabecalho().getChaveAcesso());
            }
        }
    }

    private void secaoTotais(StringBuilder ini, MdfeCabecalho mdfe) {
        secao(ini, "tot");
        chave(ini, "qCTe", str(mdfe.getDocumentos().size()));
        chave(ini, "vCarga", str(mdfe.getValorTotalCarga()));
        chave(ini, "cUnid", mdfe.getUnidadeMedidaCarga());
        chave(ini, "qCarga", str(mdfe.getPesoBrutoTotal()));
    }

    // Helpers — mesmo padrão de CteIniBuilder

    private void secao(StringBuilder ini, String nome) {
        ini.append('[').append(nome).append(']').append("\r\n");
    }

    private void chave(StringBuilder ini, String nome, String valor) {
        ini.append(nome).append('=').append(valor != null ? valor : "").append("\r\n");
    }

    private String pad(int numero) {
        return String.format("%03d", numero);
    }

    private String str(Object valor) {
        return valor == null ? "" : valor.toString();
    }

    private String str(BigDecimal valor) {
        return valor == null ? "0" : valor.toPlainString();
    }
}
