package br.com.cactusdigital.erp.fiscal.service;

import br.com.cactusdigital.erp.fiscal.client.dto.ClienteFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.CteCabecalho;
import br.com.cactusdigital.erp.fiscal.entity.CteComponente;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

/**
 * Monta o payload {@code CTe.INI} (modelo 57, modal Rodoviário) e o comando ACBr Monitor
 * correspondente. Comando confirmado via pesquisa (acbr.sourceforge.io/ACBrMonitor/
 * CTECriarEnviarCTe.html, 2026-09-03): {@code CTE.CriarEnviarCTe(cIniCTe,[nLote],
 * [nImprimir],...)} — tiro único, gera XML + envia + autoriza, mesma família de comando
 * de {@code NFE.CriarEnviarNFe} (Fase 8) e MAIS confiável que {@code NFSe.AdicionarRPS}
 * (Fase 9, que é só o primeiro de dois passos incertos).
 *
 * <h3>Confiança do LAYOUT do INI em si (diferente da confiança do COMANDO)</h3>
 * O comando está confirmado; o detalhamento campo-a-campo de cada seção do
 * {@code CTe.INI} (nomes exatos de subseção pra {@code toma}/{@code ICMS} conforme o
 * regime) foi levantado por pesquisa geral (WebSearch,
 * acbr.sourceforge.io/ACBrMonitor/ModeloCTeINI.html) sem confirmação contra uma
 * instância real do ACBr Monitor — mesmo padrão de honestidade já usado em
 * {@code NfeIniBuilder}/{@code NfseIniBuilder}. Nunca chamado ao vivo nesta sessão (ver
 * decisão de segurança da Fase 8, reforçada na Fase 9).
 *
 * <h3>Simplificações conscientes, documentadas</h3>
 * <ul>
 *   <li>Só modal <b>Rodoviário</b> (seção {@code [Rodo]}/{@code [veic001]}) — ver Javadoc
 *       de {@link br.com.cactusdigital.erp.fiscal.entity.CteCabecalho} pro porquê.</li>
 *   <li>{@code tomador} só suporta {@code REMETENTE}(=emitente da venda) ou
 *       {@code DESTINATARIO}(=cliente da venda) — {@code EXPEDIDOR}/{@code RECEBEDOR}/
 *       {@code OUTROS} exigiriam uma 3ª parte que este desenho não modela (rejeitado em
 *       {@code CteCabecalhoService} antes de chegar aqui).</li>
 *   <li>Só operação intra-UF (mesma UF emitente/destinatário) — {@code CFOP} fixo em
 *       {@code 5353} (prestação de serviço de transporte, dentro do estado); CFOP
 *       interestadual (6353) fica fora de escopo, mesmo critério do NF-e.</li>
 *   <li>{@code [Rem]} é sempre preenchido com os dados do EMITENTE (a empresa vendedora
 *       — CT-e exige a seção mesmo quando remetente=emitente, que é o caso normal de
 *       venda com frete CIF/FOB direto).</li>
 *   <li>Grupo ICMS: só regime normal (CST 00/20/40/41/90), sem Simples Nacional (que no
 *       CT-e usa grupo {@code ICMSSN}, tributação diferente) — registrado, não
 *       implementado.</li>
 * </ul>
 */
@Component
public class CteIniBuilder {

    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final String CFOP_PADRAO = "5353";
    private static final String MODELO_CTE = "57";

    public String construirComando(CteCabecalho cte, EmpresaFiscalDTO emitente,
                                    ClienteFiscalDTO destinatario, ClienteFiscalDTO transportadora, int lote) {
        String ini = construirIniCte(cte, emitente, destinatario, transportadora);
        return "CTE.CriarEnviarCTe(" + ini + "," + lote + ",0,1)";
    }

    public String construirIniCte(CteCabecalho cte, EmpresaFiscalDTO emitente,
                                   ClienteFiscalDTO destinatario, ClienteFiscalDTO transportadora) {
        StringBuilder ini = new StringBuilder();

        secaoInfCte(ini);
        secaoIdentificacao(ini, cte, emitente, destinatario);
        secaoEmitente(ini, emitente);
        secaoRemetente(ini, emitente);
        secaoDestinatario(ini, destinatario);
        secaoRodoviario(ini, cte, transportadora);
        secaoValores(ini, cte);
        secaoComponentes(ini, cte);
        secaoIcms(ini, cte);
        secaoCarga(ini, cte);

        return ini.toString();
    }

    private void secaoInfCte(StringBuilder ini) {
        secao(ini, "infCte");
        chave(ini, "versao", "3.00");
    }

    private void secaoIdentificacao(StringBuilder ini, CteCabecalho cte, EmpresaFiscalDTO emitente, ClienteFiscalDTO destinatario) {
        secao(ini, "ide");
        chave(ini, "cCT", String.format("%08d", cte.getId() % 100_000_000));
        chave(ini, "CFOP", cte.getCfop() != null ? cte.getCfop() : CFOP_PADRAO);
        chave(ini, "natOp", cte.getNaturezaOperacao());
        chave(ini, "forPag", "0"); // 0 = Pago
        chave(ini, "mod", MODELO_CTE);
        chave(ini, "serie", cte.getSerie());
        chave(ini, "nCT", str(cte.getNumero()));
        chave(ini, "dhEmi", cte.getDataEmissao() != null
                ? LocalDateTime.ofInstant(cte.getDataEmissao(), java.time.ZoneId.systemDefault()).format(FORMATO_DATA_HORA)
                : LocalDateTime.now().format(FORMATO_DATA_HORA));
        chave(ini, "tpImp", "1");  // 1 = Retrato
        chave(ini, "tpEmis", "1"); // 1 = Normal
        chave(ini, "tpAmb", cte.getAmbiente() == AmbienteFiscal.PRODUCAO ? "1" : "2");
        chave(ini, "tpCTe", "0");  // 0 = Normal — ver limitação no Javadoc da classe
        chave(ini, "procEmi", "0");
        chave(ini, "verProc", "CactusERP 1.0");
        chave(ini, "modal", cte.getModal());
        chave(ini, "tpServ", "0"); // 0 = Normal
        chave(ini, "cMunIni", str(emitente.codigoIbgeMunicipio()));
        chave(ini, "UFIni", emitente.uf());
        chave(ini, "cMunFim", enderecoDestino(destinatario) != null ? str(enderecoDestino(destinatario).codigoIbgeMunicipio()) : "");
        chave(ini, "UFFim", enderecoDestino(destinatario) != null ? enderecoDestino(destinatario).uf() : "");
        chave(ini, "retira", "1"); // 1 = Não (simplificação — sem cenário de retirada em local diferente)
        chave(ini, "toma", cte.getTomador().codigo());
    }

    private void secaoEmitente(StringBuilder ini, EmpresaFiscalDTO emitente) {
        secao(ini, "emit");
        chave(ini, "CNPJ", emitente.cnpj());
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

    /** Remetente = próprio emitente (empresa vendedora) — ver Javadoc da classe. */
    private void secaoRemetente(StringBuilder ini, EmpresaFiscalDTO emitente) {
        secao(ini, "rem");
        chave(ini, "CNPJCPF", emitente.cnpj());
        chave(ini, "IE", emitente.inscricaoEstadual());
        chave(ini, "xNome", emitente.razaoSocial());
        chave(ini, "xLgr", emitente.logradouro());
        chave(ini, "nro", emitente.numero());
        chave(ini, "xBairro", emitente.bairro());
        chave(ini, "cMun", str(emitente.codigoIbgeMunicipio()));
        chave(ini, "xMun", emitente.municipio());
        chave(ini, "CEP", emitente.cep());
        chave(ini, "UF", emitente.uf());
    }

    private void secaoDestinatario(StringBuilder ini, ClienteFiscalDTO destinatario) {
        secao(ini, "dest");
        String documento = destinatario.documento();
        if (documento != null && !documento.isBlank()) {
            chave(ini, "CNPJCPF", documento);
        }
        chave(ini, "IE", destinatario.inscricaoEstadual());
        chave(ini, "xNome", destinatario.nome());
        var endereco = enderecoDestino(destinatario);
        if (endereco != null) {
            chave(ini, "xLgr", endereco.logradouro());
            chave(ini, "nro", endereco.numero());
            chave(ini, "xBairro", endereco.bairro());
            chave(ini, "cMun", str(endereco.codigoIbgeMunicipio()));
            chave(ini, "xMun", endereco.municipio());
            chave(ini, "CEP", endereco.cep());
            chave(ini, "UF", endereco.uf());
        }
    }

    private void secaoRodoviario(StringBuilder ini, CteCabecalho cte, ClienteFiscalDTO transportadora) {
        secao(ini, "Rodo");
        chave(ini, "RNTRC", cte.getRntrc() != null ? cte.getRntrc() : (transportadora != null ? transportadora.rntrc() : null));
        if (cte.getDataPrevistaEntrega() != null) {
            chave(ini, "dPrev", cte.getDataPrevistaEntrega().toString());
        }
        chave(ini, "lota", cte.getIndicadorLotacao() != null ? cte.getIndicadorLotacao() : "0");

        if (cte.getPlaca() != null) {
            secao(ini, "veic001");
            chave(ini, "placa", cte.getPlaca());
            chave(ini, "UF", cte.getUfPlaca());
        }
    }

    private void secaoValores(StringBuilder ini, CteCabecalho cte) {
        secao(ini, "vPrest");
        chave(ini, "vTPrest", str(cte.getValorTotalServico()));
        chave(ini, "vRec", str(cte.getValorReceber() != null ? cte.getValorReceber() : cte.getValorTotalServico()));
    }

    private void secaoComponentes(StringBuilder ini, CteCabecalho cte) {
        int numero = 1;
        for (CteComponente componente : cte.getComponentes()) {
            secao(ini, "Comp" + pad(numero++));
            chave(ini, "xNome", componente.getNome());
            chave(ini, "vComp", str(componente.getValor()));
        }
    }

    /** Só regime normal (CST 00/20/40/41/90) — Simples Nacional (ICMSSN) fora de escopo, ver Javadoc da classe. */
    private void secaoIcms(StringBuilder ini, CteCabecalho cte) {
        String cst = cte.getCst() != null ? cte.getCst() : "90";
        secao(ini, "ICMS" + cst);
        chave(ini, "CST", cst);
        chave(ini, "vBC", str(cte.getBaseCalculoIcms()));
        chave(ini, "pICMS", str(cte.getAliquotaIcms()));
        chave(ini, "vICMS", str(cte.getValorIcms()));
    }

    private void secaoCarga(StringBuilder ini, CteCabecalho cte) {
        secao(ini, "infCarga");
        chave(ini, "vCarga", str(cte.getValorTotalCarga()));
        chave(ini, "proPred", cte.getProdutoPredominante());

        if (cte.getPesoBruto() != null) {
            secao(ini, "infQ001");
            chave(ini, "cUnid", "01"); // 01 = KG
            chave(ini, "tpMed", "PESO BRUTO");
            chave(ini, "qCarga", str(cte.getPesoBruto()));
        }
    }

    private br.com.cactusdigital.erp.fiscal.client.dto.EnderecoClienteDTO enderecoDestino(ClienteFiscalDTO destinatario) {
        return destinatario.enderecos() != null && !destinatario.enderecos().isEmpty() ? destinatario.enderecos().get(0) : null;
    }

    // ====================================================================
    //  Helpers — mesmo padrão exato de NfeIniBuilder/cactus-pdv.AcbrComandoFiscalBuilder
    // ====================================================================

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
