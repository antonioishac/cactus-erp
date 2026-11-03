package br.com.cactuserp.pdv.service.fiscal;

import br.com.cactuserp.pdv.entity.LojaConfiguracao;
import br.com.cactuserp.pdv.entity.PdvTotalTipoPagamento;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.DFModelo;
import com.fincatto.documentofiscal.DFPais;
import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.nfe.NFTipoEmissao;
import com.fincatto.documentofiscal.nfe400.classes.NFEndereco;
import com.fincatto.documentofiscal.nfe400.classes.NFFinalidade;
import com.fincatto.documentofiscal.nfe400.classes.NFModalidadeFrete;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoImpostoTributacaoICMS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoItemModalidadeBCICMS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaCOFINS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaPIS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaSituacaoOperacionalSimplesNacional;
import com.fincatto.documentofiscal.nfe400.classes.NFOrigem;
import com.fincatto.documentofiscal.nfe400.classes.NFProcessoEmissor;
import com.fincatto.documentofiscal.nfe400.classes.NFProdutoCompoeValorNota;
import com.fincatto.documentofiscal.nfe400.classes.NFRegimeTributario;
import com.fincatto.documentofiscal.nfe400.classes.NFTipo;
import com.fincatto.documentofiscal.nfe400.classes.NFTipoImpressao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIdentificadorLocalDestinoOperacao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIndicadorIEDestinatario;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIndicadorPresencaComprador;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFMeioPagamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfo;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoCartao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoDestinatario;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoEmitente;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoFormaPagamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoICMSTotal;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoIdentificacao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItem;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImposto;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoCOFINS;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoCOFINSAliquota;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoCOFINSOutrasOperacoes;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoICMS;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoICMS00;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoICMS40;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoICMS60;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoICMSSN102;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoICMSSN500;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoPIS;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoPISAliquota;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoPISOutrasOperacoes;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemProduto;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoPagamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoResponsavelTecnico;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoTotal;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoTransporte;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFOperacaoConsumidorFinal;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFTipoIntegracaoPagamento;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Converte um cupom finalizado do PDV numa NFC-e 4.00 (modelo 65) da biblioteca. Componente puro: sem rede, sem banco
 * — toda decisão de leiaute fica aqui e é testada contra o XSD oficial ({@code NfceDocumentoBuilderTest}).
 *
 * <h3>O que é emitido</h3>
 * <ul>
 *   <li>Venda presencial a consumidor final, operação interna (balcão). Destinatário só quando identificado (CPF/CNPJ).</li>
 *   <li>ICMS: CST 00/40/41/50/60 (Regime Normal) e CSOSN 102/103/300/400/500 (Simples). Os demais exigem ST/redução de
 *       base/crédito que o cadastro do PDV não tem: são recusados ({@link NfceConfiguracaoException}), nunca enviados
 *       errados. CFOP padrão 5102; 5405 para mercadoria com ICMS retido (CST 60/CSOSN 500).</li>
 *   <li>Pagamentos com o código {@code tPag} do tipo de pagamento e troco ({@code vPag - vTroco = vNF}, conciliação
 *       obrigatória da NT 2025.001). Cartão/PIX levam o grupo {@code card} com {@code tpIntegra=2} (POS não integrado).</li>
 *   <li>Contingência offline: {@code tpEmis=9}, {@code dhCont} e {@code xJust}.</li>
 * </ul>
 *
 * <h3>Limitações conscientes (BACKLOG)</h3>
 * <ul>
 *   <li><b>IBS/CBS (grupo UB)</b>: o cadastro do PDV ainda não recebe o cClassTrib do ERP. Emitente CRT 3 tem o grupo
 *       obrigatório desde 03/08/2026 — enquanto a sincronização não trouxer a classificação, a SEFAZ vai rejeitar.</li>
 *   <li>PIS/COFINS: o PDV não guarda o CST; alíquota maior que zero vai como CST 01, zero (ou Simples) como 49.</li>
 *   <li>Origem da mercadoria fixa em 0 (o produto do PDV não guarda a origem).</li>
 * </ul>
 */
@Component
public class NfceDocumentoBuilder {

    /** Exigido pela SEFAZ no nome do destinatário em homologação (rejeição 598). */
    public static final String NOME_HOMOLOGACAO = "NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL";
    /** Exigido pela SEFAZ na descrição do primeiro item em homologação (NFC-e sem destinatário; rejeição 373). */
    public static final String DESCRICAO_HOMOLOGACAO = "NOTA FISCAL EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL";
    static final String VERSAO_EMISSOR = "CactusPDV 1.0";
    static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");
    private static final String CFOP_PADRAO = "5102";
    private static final String CFOP_SUBSTITUICAO = "5405";
    private static final Pattern GTIN_VALIDO = Pattern.compile("^([0-9]{8}|[0-9]{12,14})$");
    private static final Set<String> CSOSN_SEM_CREDITO = Set.of("102", "103", "300", "400");
    private static final Set<String> CST_ICMS_SEM_DESTAQUE = Set.of("40", "41", "50");
    private static final Set<String> TPAG_CARTAO_OU_PIX = Set.of("03", "04", "17");

    /**
     * Tudo que a NFC-e precisa, já resolvido pelo serviço (o builder não acessa banco).
     *
     * @param siglasUnidade sigla por {@code ID_PRODUTO_UNIDADE}; ausente vira "UN".
     * @param dataHoraContingencia/justificativaContingencia só com {@code tipoEmissao} offline.
     */
    public record DadosNfce(
            PdvVendaCabecalho cupom,
            LojaConfiguracao loja,
            List<PdvVendaDetalhe> itens,
            List<PdvTotalTipoPagamento> pagamentos,
            Map<Long, String> siglasUnidade,
            int serie,
            long numero,
            LocalDateTime dataHoraEmissao,
            boolean producao,
            NFTipoEmissao tipoEmissao,
            LocalDateTime dataHoraContingencia,
            String justificativaContingencia,
            FiscalPdvProperties.ResponsavelTecnico responsavelTecnico) {
    }

    public NFNota construir(DadosNfce dados) {
        LojaConfiguracao loja = dados.loja();
        validarLoja(loja);
        if (dados.itens().isEmpty()) {
            throw new NfceConfiguracaoException("Cupom #" + dados.cupom().getId() + " sem itens ativos.");
        }
        boolean simples = ehSimples(loja.getCrt());

        List<NFNotaInfoItem> itens = new ArrayList<>();
        int numeroItem = 1;
        for (PdvVendaDetalhe detalhe : dados.itens()) {
            itens.add(item(dados, detalhe, numeroItem, simples));
            numeroItem++;
        }
        NFNotaInfoICMSTotal totalIcms = totalIcms(itens);

        NFNotaInfo info = new NFNotaInfo();
        info.setVersao(new BigDecimal("4.00"));
        info.setIdentificacao(identificacao(dados));
        info.setEmitente(emitente(loja));
        destinatario(dados).ifPresent(info::setDestinatario);
        info.setItens(itens);
        NFNotaInfoTotal total = new NFNotaInfoTotal();
        total.setIcmsTotal(totalIcms);
        info.setTotal(total);
        NFNotaInfoTransporte transporte = new NFNotaInfoTransporte();
        transporte.setModalidadeFrete(NFModalidadeFrete.SEM_OCORRENCIA_TRANSPORTE);
        info.setTransporte(transporte);
        info.setPagamento(pagamento(dados, new BigDecimal(totalIcms.getValorTotalNFe())));
        responsavelTecnico(dados.responsavelTecnico()).ifPresent(info::setInformacaoResposavelTecnico);

        NFNota nota = new NFNota();
        nota.setInfo(info);
        return nota;
    }

    /**
     * cNF determinístico por cupom/número/tipo de emissão (nunca igual ao nNF): reenviar o mesmo cupom gera a mesma
     * chave — a SEFAZ responde duplicidade em vez de aceitar um segundo documento com o mesmo número.
     */
    static String codigoNumerico(long idCupom, long numero, NFTipoEmissao tipoEmissao) {
        long semente = idCupom * 2_654_435_761L + numero * 40_503L + (tipoEmissao == NFTipoEmissao.CONTIGENCIA_OFFLINE ? 7 : 0);
        long valor = Math.floorMod(semente, 100_000_000L);
        String codigo = String.format("%08d", valor);
        return codigo.equals(String.format("%08d", numero % 100_000_000L)) ? String.format("%08d", (valor + 1) % 100_000_000L) : codigo;
    }

    private static void validarLoja(LojaConfiguracao loja) {
        if (loja == null) {
            throw new NfceConfiguracaoException("Configuração da loja (emitente) não cadastrada.");
        }
        List<String> faltando = new ArrayList<>();
        if (vazio(loja.getCnpj())) faltando.add("CNPJ");
        if (vazio(loja.getInscricaoEstadual())) faltando.add("inscrição estadual");
        if (vazio(loja.getRazaoSocial())) faltando.add("razão social");
        if (vazio(loja.getCrt())) faltando.add("CRT");
        if (vazio(loja.getUf())) faltando.add("UF");
        if (loja.getCodigoIbgeCidade() == null) faltando.add("código IBGE da cidade");
        if (vazio(loja.getLogradouro()) || vazio(loja.getBairro()) || vazio(loja.getCidade()) || vazio(loja.getCep())) faltando.add("endereço");
        if (!faltando.isEmpty()) {
            throw new NfceConfiguracaoException("Dados do emitente incompletos na configuração da loja: " + String.join(", ", faltando) + ".");
        }
    }

    private NFNotaInfoIdentificacao identificacao(DadosNfce dados) {
        NFNotaInfoIdentificacao id = new NFNotaInfoIdentificacao();
        id.setUf(DFUnidadeFederativa.valueOfCodigo(dados.loja().getUf()));
        id.setCodigoRandomico(codigoNumerico(dados.cupom().getId(), dados.numero(), dados.tipoEmissao()));
        id.setNaturezaOperacao("Venda");
        id.setModelo(DFModelo.NFCE);
        id.setSerie(String.valueOf(dados.serie()));
        id.setNumeroNota(String.valueOf(dados.numero()));
        id.setDataHoraEmissao(zonado(dados.dataHoraEmissao()));
        id.setTipo(NFTipo.SAIDA);
        id.setIdentificadorLocalDestinoOperacao(NFIdentificadorLocalDestinoOperacao.OPERACAO_INTERNA);
        id.setCodigoMunicipio(String.valueOf(dados.loja().getCodigoIbgeCidade()));
        id.setTipoImpressao(NFTipoImpressao.DANFE_NFCE);
        id.setTipoEmissao(dados.tipoEmissao());
        id.setAmbiente(dados.producao() ? DFAmbiente.PRODUCAO : DFAmbiente.HOMOLOGACAO);
        id.setFinalidade(NFFinalidade.NORMAL);
        id.setOperacaoConsumidorFinal(NFOperacaoConsumidorFinal.SIM);
        id.setIndicadorPresencaComprador(NFIndicadorPresencaComprador.OPERACAO_PRESENCIAL);
        id.setProgramaEmissor(NFProcessoEmissor.CONTRIBUINTE);
        id.setVersaoEmissor(VERSAO_EMISSOR);
        if (dados.tipoEmissao() == NFTipoEmissao.CONTIGENCIA_OFFLINE) {
            id.setDataHoraContigencia(zonado(dados.dataHoraContingencia()));
            id.setJustificativaEntradaContingencia(dados.justificativaContingencia());
        }
        return id;
    }

    private static NFNotaInfoEmitente emitente(LojaConfiguracao loja) {
        NFEndereco endereco = new NFEndereco();
        endereco.setLogradouro(loja.getLogradouro());
        endereco.setNumero(vazio(loja.getNumero()) ? "S/N" : loja.getNumero());
        if (!vazio(loja.getComplemento())) {
            endereco.setComplemento(loja.getComplemento());
        }
        endereco.setBairro(loja.getBairro());
        endereco.setCodigoMunicipio(String.valueOf(loja.getCodigoIbgeCidade()));
        endereco.setDescricaoMunicipio(loja.getCidade());
        endereco.setUf(DFUnidadeFederativa.valueOfCodigo(loja.getUf()));
        endereco.setCep(digitos(loja.getCep()));
        endereco.setCodigoPais(DFPais.BRASIL);
        endereco.setDescricaoPais("BRASIL");
        if (!vazio(loja.getTelefone())) {
            endereco.setTelefone(digitos(loja.getTelefone()));
        }
        NFNotaInfoEmitente emitente = new NFNotaInfoEmitente();
        emitente.setCnpj(digitos(loja.getCnpj()));
        emitente.setRazaoSocial(loja.getRazaoSocial());
        if (!vazio(loja.getNomeFantasia())) {
            emitente.setNomeFantasia(loja.getNomeFantasia());
        }
        emitente.setEndereco(endereco);
        emitente.setInscricaoEstadual(digitos(loja.getInscricaoEstadual()));
        emitente.setRegimeTributario(NFRegimeTributario.valueOfCodigo(loja.getCrt()));
        return emitente;
    }

    /** NFC-e aceita consumidor não identificado; só vai o grupo quando há CPF/CNPJ. */
    private static java.util.Optional<NFNotaInfoDestinatario> destinatario(DadosNfce dados) {
        PdvVendaCabecalho cupom = dados.cupom();
        String documento = digitos(cupom.getCliente() != null ? cupom.getCliente().getCpfCnpj() : cupom.getCpfCnpjCliente());
        if (documento.length() != 11 && documento.length() != 14) {
            return java.util.Optional.empty();
        }
        NFNotaInfoDestinatario destinatario = new NFNotaInfoDestinatario();
        if (documento.length() == 14) {
            destinatario.setCnpj(documento);
        } else {
            destinatario.setCpf(documento);
        }
        String nome = cupom.getCliente() != null ? cupom.getCliente().getNome() : cupom.getNomeCliente();
        if (!dados.producao()) {
            destinatario.setRazaoSocial(NOME_HOMOLOGACAO);
        } else if (!vazio(nome)) {
            destinatario.setRazaoSocial(nome.length() > 60 ? nome.substring(0, 60) : nome);
        }
        destinatario.setIndicadorIEDestinatario(NFIndicadorIEDestinatario.NAO_CONTRIBUINTE);
        return java.util.Optional.of(destinatario);
    }

    private NFNotaInfoItem item(DadosNfce dados, PdvVendaDetalhe detalhe, int numeroItem, boolean simples) {
        if (detalhe.getProduto() == null) {
            throw new NfceConfiguracaoException("Item " + numeroItem + " do cupom sem produto vinculado.");
        }
        String ncm = digitos(detalhe.getProduto().getCodigoNcm());
        if (ncm.length() != 8) {
            throw new NfceConfiguracaoException("Produto #" + detalhe.getProduto().getId() + " (" + detalhe.getProduto().getDescricao()
                    + ") sem NCM válido (8 dígitos).");
        }
        BigDecimal quantidade = detalhe.getQuantidade().setScale(4, RoundingMode.HALF_UP);
        BigDecimal unitario = unitario(detalhe.getValorUnitario());
        BigDecimal bruto = dinheiro(quantidade.multiply(unitario));
        BigDecimal desconto = dinheiro(detalhe.getValorDesconto());
        BigDecimal acrescimo = dinheiro(detalhe.getValorAcrescimo());
        String gtin = detalhe.getGtin() != null && GTIN_VALIDO.matcher(detalhe.getGtin()).matches() ? detalhe.getGtin() : "SEM GTIN";
        String unidade = dados.siglasUnidade().getOrDefault(detalhe.getProduto().getProdutoUnidade(), "UN");

        NFNotaInfoItemImpostoICMS icms = icms(detalhe, simples, numeroItem);
        boolean comIcmsRetido = icms.getIcms60() != null || icms.getIcmssn500() != null;

        NFNotaInfoItemProduto produto = new NFNotaInfoItemProduto();
        produto.setCodigo(String.valueOf(detalhe.getProduto().getId()));
        produto.setCodigoDeBarrasGtin(gtin);
        produto.setDescricao(numeroItem == 1 && !dados.producao() ? DESCRICAO_HOMOLOGACAO : descricao(detalhe));
        produto.setNcm(ncm);
        if (!vazio(detalhe.getProduto().getCodigoCest())) {
            produto.setCodigoEspecificadorSituacaoTributaria(digitos(detalhe.getProduto().getCodigoCest()));
        }
        produto.setCfop(detalhe.getCfop() != null ? String.valueOf(detalhe.getCfop()) : comIcmsRetido ? CFOP_SUBSTITUICAO : CFOP_PADRAO);
        produto.setUnidadeComercial(unidade);
        produto.setQuantidadeComercial(quantidade);
        produto.setValorUnitario(unitario);
        produto.setValorTotalBruto(bruto);
        produto.setCodigoDeBarrasGtinTributavel(gtin);
        produto.setUnidadeTributavel(unidade);
        produto.setQuantidadeTributavel(quantidade);
        produto.setValorUnitarioTributavel(unitario);
        if (desconto.signum() > 0) {
            produto.setValorDesconto(desconto);
        }
        if (acrescimo.signum() > 0) {
            produto.setValorOutrasDespesasAcessorias(acrescimo);
        }
        produto.setCompoeValorNota(NFProdutoCompoeValorNota.SIM);

        NFNotaInfoItemImposto imposto = new NFNotaInfoItemImposto();
        imposto.setIcms(icms);
        imposto.setPis(pis(detalhe, simples));
        imposto.setCofins(cofins(detalhe, simples));

        NFNotaInfoItem item = new NFNotaInfoItem();
        item.setNumeroItem(numeroItem);
        item.setProduto(produto);
        item.setImposto(imposto);
        return item;
    }

    private static NFNotaInfoItemImpostoICMS icms(PdvVendaDetalhe detalhe, boolean simples, int numeroItem) {
        NFNotaInfoItemImpostoICMS icms = new NFNotaInfoItemImpostoICMS();
        if (simples) {
            String csosn = detalhe.getProduto().getCsosn();
            if (csosn != null && CSOSN_SEM_CREDITO.contains(csosn.trim())) {
                NFNotaInfoItemImpostoICMSSN102 sn = new NFNotaInfoItemImpostoICMSSN102();
                sn.setOrigem(NFOrigem.NACIONAL);
                sn.setSituacaoOperacaoSN(NFNotaSituacaoOperacionalSimplesNacional.valueOfCodigo(csosn.trim()));
                icms.setIcmssn102(sn);
            } else if (csosn != null && "500".equals(csosn.trim())) {
                NFNotaInfoItemImpostoICMSSN500 sn = new NFNotaInfoItemImpostoICMSSN500();
                sn.setOrigem(NFOrigem.NACIONAL);
                sn.setSituacaoOperacaoSN(NFNotaSituacaoOperacionalSimplesNacional.CSOSN_500);
                icms.setIcmssn500(sn);
            } else {
                throw naoSuportado(detalhe, numeroItem, csosn == null ? "sem CSOSN cadastrado" : "CSOSN " + csosn
                        + " (exige crédito do Simples ou ICMS-ST, que o cadastro do PDV não tem)");
            }
            return icms;
        }
        String cst = detalhe.getCst() != null ? detalhe.getCst().trim() : null;
        if ("00".equals(cst)) {
            NFNotaInfoItemImpostoICMS00 icms00 = new NFNotaInfoItemImpostoICMS00();
            icms00.setOrigem(NFOrigem.NACIONAL);
            icms00.setSituacaoTributaria(NFNotaInfoImpostoTributacaoICMS.CST_00);
            icms00.setModalidadeBCICMS(NFNotaInfoItemModalidadeBCICMS.VALOR_OPERACAO);
            icms00.setValorBaseCalculo(dinheiro(detalhe.getValorBaseIcms()));
            icms00.setPercentualAliquota(percentual(detalhe.getTaxaIcms()));
            icms00.setValorTributo(dinheiro(detalhe.getValorIcms()));
            icms.setIcms00(icms00);
        } else if (cst != null && CST_ICMS_SEM_DESTAQUE.contains(cst)) {
            NFNotaInfoItemImpostoICMS40 icms40 = new NFNotaInfoItemImpostoICMS40();
            icms40.setOrigem(NFOrigem.NACIONAL);
            icms40.setSituacaoTributaria(NFNotaInfoImpostoTributacaoICMS.valueOfCodigo(cst));
            icms.setIcms40(icms40);
        } else if ("60".equals(cst)) {
            NFNotaInfoItemImpostoICMS60 icms60 = new NFNotaInfoItemImpostoICMS60();
            icms60.setOrigem(NFOrigem.NACIONAL);
            icms60.setSituacaoTributaria(NFNotaInfoImpostoTributacaoICMS.CST_60);
            icms.setIcms60(icms60);
        } else {
            throw naoSuportado(detalhe, numeroItem, cst == null ? "sem CST de ICMS cadastrado"
                    : "CST de ICMS " + cst + " (exige ST, redução de base ou diferimento, que o cadastro do PDV não tem)");
        }
        return icms;
    }

    private static NFNotaInfoItemImpostoPIS pis(PdvVendaDetalhe detalhe, boolean simples) {
        NFNotaInfoItemImpostoPIS pis = new NFNotaInfoItemImpostoPIS();
        BigDecimal taxa = detalhe.getTaxaPis();
        if (!simples && taxa != null && taxa.signum() > 0) {
            NFNotaInfoItemImpostoPISAliquota aliquota = new NFNotaInfoItemImpostoPISAliquota();
            aliquota.setSituacaoTributaria(NFNotaInfoSituacaoTributariaPIS.valueOfCodigo("01"));
            aliquota.setValorBaseCalculo(dinheiro(detalhe.getValorTotal()));
            aliquota.setPercentualAliquota(percentual(taxa));
            aliquota.setValorTributo(dinheiro(detalhe.getValorPis()));
            pis.setAliquota(aliquota);
        } else {
            NFNotaInfoItemImpostoPISOutrasOperacoes outras = new NFNotaInfoItemImpostoPISOutrasOperacoes();
            outras.setSituacaoTributaria(NFNotaInfoSituacaoTributariaPIS.valueOfCodigo("49"));
            outras.setValorBaseCalculo(BigDecimal.ZERO.setScale(2));
            outras.setPercentualAliquota(BigDecimal.ZERO.setScale(4));
            outras.setValorTributo(BigDecimal.ZERO.setScale(2));
            pis.setOutrasOperacoes(outras);
        }
        return pis;
    }

    private static NFNotaInfoItemImpostoCOFINS cofins(PdvVendaDetalhe detalhe, boolean simples) {
        NFNotaInfoItemImpostoCOFINS cofins = new NFNotaInfoItemImpostoCOFINS();
        BigDecimal taxa = detalhe.getTaxaCofins();
        if (!simples && taxa != null && taxa.signum() > 0) {
            NFNotaInfoItemImpostoCOFINSAliquota aliquota = new NFNotaInfoItemImpostoCOFINSAliquota();
            aliquota.setSituacaoTributaria(NFNotaInfoSituacaoTributariaCOFINS.valueOfCodigo("01"));
            aliquota.setValorBaseCalculo(dinheiro(detalhe.getValorTotal()));
            aliquota.setPercentualAliquota(percentual(taxa));
            aliquota.setValor(dinheiro(detalhe.getValorCofins()));
            cofins.setAliquota(aliquota);
        } else {
            NFNotaInfoItemImpostoCOFINSOutrasOperacoes outras = new NFNotaInfoItemImpostoCOFINSOutrasOperacoes();
            outras.setSituacaoTributaria(NFNotaInfoSituacaoTributariaCOFINS.valueOfCodigo("49"));
            outras.setValorBaseCalculo(BigDecimal.ZERO.setScale(2));
            outras.setPercentualCOFINS(BigDecimal.ZERO.setScale(4));
            outras.setValorCOFINS(BigDecimal.ZERO.setScale(2));
            cofins.setOutrasOperacoes(outras);
        }
        return cofins;
    }

    /** W02 somado do que vai no XML. vNF = vProd − vDesc + vOutro (rejeição 610 se divergir). */
    private static NFNotaInfoICMSTotal totalIcms(List<NFNotaInfoItem> itens) {
        BigDecimal base = BigDecimal.ZERO, icms = BigDecimal.ZERO, produtos = BigDecimal.ZERO, descontos = BigDecimal.ZERO,
                outros = BigDecimal.ZERO, pis = BigDecimal.ZERO, cofins = BigDecimal.ZERO;
        for (NFNotaInfoItem item : itens) {
            NFNotaInfoItemProduto p = item.getProduto();
            produtos = produtos.add(new BigDecimal(p.getValorTotalBruto()));
            if (p.getValorDesconto() != null) descontos = descontos.add(new BigDecimal(p.getValorDesconto()));
            if (p.getValorOutrasDespesasAcessorias() != null) outros = outros.add(new BigDecimal(p.getValorOutrasDespesasAcessorias()));
            NFNotaInfoItemImpostoICMS00 icms00 = item.getImposto().getIcms().getIcms00();
            if (icms00 != null) {
                base = base.add(new BigDecimal(icms00.getValorBaseCalculo()));
                icms = icms.add(new BigDecimal(icms00.getValorTributo()));
            }
            NFNotaInfoItemImpostoPIS itemPis = item.getImposto().getPis();
            pis = pis.add(new BigDecimal(itemPis.getAliquota() != null ? itemPis.getAliquota().getValorTributo() : itemPis.getOutrasOperacoes().getValorTributo()));
            NFNotaInfoItemImpostoCOFINS itemCofins = item.getImposto().getCofins();
            cofins = cofins.add(new BigDecimal(itemCofins.getAliquota() != null ? itemCofins.getAliquota().getValor() : itemCofins.getOutrasOperacoes().getValorCOFINS()));
        }
        NFNotaInfoICMSTotal total = new NFNotaInfoICMSTotal();
        total.setBaseCalculoICMS(base);
        total.setValorTotalICMS(icms);
        total.setValorICMSDesonerado(BigDecimal.ZERO);
        total.setValorTotalFundoCombatePobreza(BigDecimal.ZERO);
        total.setBaseCalculoICMSST(BigDecimal.ZERO);
        total.setValorTotalICMSST(BigDecimal.ZERO);
        total.setValorTotalFundoCombatePobrezaST(BigDecimal.ZERO);
        total.setValorTotalFundoCombatePobrezaSTRetido(BigDecimal.ZERO);
        total.setValorTotalDosProdutosServicos(produtos);
        total.setValorTotalFrete(BigDecimal.ZERO);
        total.setValorTotalSeguro(BigDecimal.ZERO);
        total.setValorTotalDesconto(descontos);
        total.setValorTotalII(BigDecimal.ZERO);
        total.setValorTotalIPI(BigDecimal.ZERO);
        total.setValorTotalIPIDevolvido(BigDecimal.ZERO);
        total.setValorPIS(pis);
        total.setValorCOFINS(cofins);
        total.setOutrasDespesasAcessorias(outros);
        total.setValorTotalNFe(produtos.subtract(descontos).add(outros));
        return total;
    }

    /**
     * {@code detPag} por forma usada; troco = soma dos pagamentos − vNF (o PDV grava o valor recebido em cada forma).
     * Diferença de centavos de arredondamento entre o total do PDV e o vNF do XML é absorvida no troco, nunca no vPag.
     */
    private static NFNotaInfoPagamento pagamento(DadosNfce dados, BigDecimal valorNota) {
        List<NFNotaInfoFormaPagamento> formas = new ArrayList<>();
        BigDecimal recebido = BigDecimal.ZERO;
        for (PdvTotalTipoPagamento pagamento : dados.pagamentos()) {
            if ("S".equalsIgnoreCase(pagamento.getEstorno())) {
                continue;
            }
            String tPag = pagamento.getPdvTipoPagamento() != null ? pagamento.getPdvTipoPagamento().getCodigoPagamentoNfce() : null;
            tPag = vazio(tPag) ? "99" : tPag.trim();
            BigDecimal valor = dinheiro(pagamento.getValor());
            NFNotaInfoFormaPagamento forma = new NFNotaInfoFormaPagamento()
                    .setMeioPagamento(NFMeioPagamento.valueOfCodigo(tPag))
                    .setValorPagamento(valor);
            if ("99".equals(tPag)) {
                forma.setDescricaoMeioPagamento(pagamento.getPdvTipoPagamento() != null && !vazio(pagamento.getPdvTipoPagamento().getDescricao())
                        ? pagamento.getPdvTipoPagamento().getDescricao() : "Outros");
            }
            if (TPAG_CARTAO_OU_PIX.contains(tPag)) {
                NFNotaInfoCartao cartao = new NFNotaInfoCartao();
                cartao.setTipoIntegracao(NFTipoIntegracaoPagamento.SEPARADO);
                forma.setCartao(cartao);
            }
            formas.add(forma);
            recebido = recebido.add(valor);
        }
        if (formas.isEmpty()) {
            throw new NfceConfiguracaoException("Cupom #" + dados.cupom().getId() + " sem pagamentos registrados.");
        }
        BigDecimal troco = recebido.subtract(valorNota);
        if (troco.signum() < 0) {
            throw new NfceConfiguracaoException("Pagamentos (R$ " + recebido + ") menores que o total da NFC-e (R$ " + valorNota + ").");
        }
        NFNotaInfoPagamento pag = new NFNotaInfoPagamento().setDetalhamentoFormasPagamento(formas);
        if (troco.signum() > 0) {
            pag.setValorTroco(troco);
        }
        return pag;
    }

    private static java.util.Optional<NFNotaInfoResponsavelTecnico> responsavelTecnico(FiscalPdvProperties.ResponsavelTecnico rt) {
        if (rt == null || vazio(rt.cnpj())) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(new NFNotaInfoResponsavelTecnico()
                .setCnpj(digitos(rt.cnpj()))
                .setContatoNome(rt.contato())
                .setEmail(rt.email())
                .setTelefone(digitos(rt.telefone())));
    }

    private static String descricao(PdvVendaDetalhe detalhe) {
        String pdv = detalhe.getProduto().getDescricaoPdv();
        String texto = !vazio(pdv) ? pdv : detalhe.getProduto().getDescricao();
        if (vazio(texto)) texto = detalhe.getProduto().getNome();
        texto = vazio(texto) ? "ITEM" : texto.trim();
        return texto.length() > 120 ? texto.substring(0, 120) : texto;
    }

    private static NfceConfiguracaoException naoSuportado(PdvVendaDetalhe detalhe, int numeroItem, String motivo) {
        return new NfceConfiguracaoException("Item " + numeroItem + " (produto #" + detalhe.getProduto().getId() + "): " + motivo + ".");
    }

    static boolean ehSimples(String crt) {
        return "1".equals(crt) || "2".equals(crt) || "4".equals(crt);
    }

    private static ZonedDateTime zonado(LocalDateTime dataHora) {
        return dataHora.atZone(FUSO_BRASILIA).truncatedTo(ChronoUnit.SECONDS);
    }

    // A biblioteca recusa casas decimais além do leiaute (lança, não arredonda): todo valor passa por aqui.

    private static BigDecimal dinheiro(BigDecimal valor) {
        return (valor == null ? BigDecimal.ZERO : valor).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal percentual(BigDecimal valor) {
        return (valor == null ? BigDecimal.ZERO : valor).setScale(4, RoundingMode.HALF_UP);
    }

    private static BigDecimal unitario(BigDecimal valor) {
        BigDecimal semZeros = valor.stripTrailingZeros();
        return valor.setScale(Math.min(Math.max(semZeros.scale(), 2), 10), RoundingMode.HALF_UP);
    }

    private static String digitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
