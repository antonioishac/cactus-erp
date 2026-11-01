package br.com.cactusdigital.erp.fiscal.sefaz;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.fiscal.client.dto.ClienteFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EnderecoClienteDTO;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronica;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronicaItem;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.DFModelo;
import com.fincatto.documentofiscal.DFPais;
import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.nfe.NFTipoEmissao;
import com.fincatto.documentofiscal.nfe400.classes.NFEndereco;
import com.fincatto.documentofiscal.nfe400.classes.NFFinalidade;
import com.fincatto.documentofiscal.nfe400.classes.NFModalidadeFrete;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoImpostoTributacaoIBSCBS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoImpostoTributacaoICMS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoItemModalidadeBCICMS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaCOFINS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaIPI;
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
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIndicadorIntermediador;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIndicadorPresencaComprador;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFMeioPagamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfo;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoDestinatario;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoEmitente;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoFormaPagamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoIBSCBSTot;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoICMSTotal;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoIdentificacao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItem;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImposto;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoCOFINS;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoCOFINSAliquota;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoCOFINSNaoTributavel;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoCOFINSOutrasOperacoes;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoIBSCBS;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoIBSCBSTIBS;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoICMS;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoICMS00;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoICMS40;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoICMS60;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoICMSSN102;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoICMSSN500;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoIPI;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoIPITributado;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoPIS;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoPISAliquota;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoPISNaoTributado;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoPISOutrasOperacoes;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemProduto;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoPagamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoResponsavelTecnico;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoTotal;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoTransportador;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoTransporte;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFOperacaoConsumidorFinal;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Converte a {@link NotaFiscalEletronica} do ERP no modelo NF-e 4.00 da biblioteca ({@link NFNota}), pronto para
 * assinar e transmitir. Componente puro (sem rede, sem banco): toda decisão de leiaute fica aqui e é testável.
 *
 * <h3>O que é emitido</h3>
 * <ul>
 *   <li>Venda de mercadoria ({@code natOp}/CFOP 5102 padrão do item), saída, finalidade normal, operação interna.</li>
 *   <li>ICMS: CST 00/40/41/50/60 (Regime Normal) e CSOSN 102/103/300/400/500 (Simples). Os demais exigem dados que o
 *       motor tributário ainda não entrega (ST, redução de base, crédito do Simples) e são <b>recusados</b>
 *       ({@code TRIBUTACAO_ITEM_NAO_SUPORTADA}) em vez de enviados errados.</li>
 *   <li>PIS/COFINS: tributado por alíquota (01/02), não tributado (04-09) e outras operações (49-99).</li>
 *   <li>IBS/CBS (grupo UB e W03) com os valores já calculados por {@code CalculadoraIbsCbs}.</li>
 * </ul>
 *
 * <h3>Simplificações conscientes</h3>
 * <ul>
 *   <li>Operação interestadual é recusada (CFOP 6xxx, DIFAL e partilha não implementados).</li>
 *   <li>Pagamento: um único {@code detPag} com {@code tPag=99} ("Outros") no valor da nota — a forma de pagamento da
 *       venda ainda não é traduzida para o código da SEFAZ (ver BACKLOG.md).</li>
 *   <li>Frete: com transportadora, {@code modFrete=0} (CIF); sem, {@code modFrete=9}.</li>
 * </ul>
 */
@Component
class NfeDocumentoBuilder {

    /** Exigido pela SEFAZ como nome do destinatário em homologação (rejeição 598 quando diferente). */
    static final String NOME_DESTINATARIO_HOMOLOGACAO = "NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL";
    static final String VERSAO_EMISSOR = "CactusERP 1.0";
    private static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");
    private static final String CFOP_PADRAO = "5102";
    private static final Pattern GTIN_VALIDO = Pattern.compile("^([0-9]{8}|[0-9]{12,14})$");
    private static final String SEM_GTIN = "SEM GTIN";
    private static final Set<String> CSOSN_SEM_CREDITO = Set.of("102", "103", "300", "400");
    private static final Set<String> CST_ICMS_SEM_DESTAQUE = Set.of("40", "41", "50");
    private static final Set<String> CST_PIS_COFINS_ALIQUOTA = Set.of("01", "02");
    private static final Set<String> CST_PIS_COFINS_NAO_TRIBUTADO = Set.of("04", "05", "06", "07", "08", "09");

    private final FiscalProperties properties;

    NfeDocumentoBuilder(FiscalProperties properties) {
        this.properties = properties;
    }

    NFNota construir(NotaFiscalEletronica nota, EmpresaFiscalDTO emitente, ClienteFiscalDTO destinatario,
                     ClienteFiscalDTO transportadora) {
        EnderecoClienteDTO enderecoDestinatario = destinatario.enderecos().stream()
                .filter(e -> e.codigoIbgeMunicipio() != null)
                .findFirst()
                .orElseThrow(() -> new BusinessException(FiscalErrorCode.DESTINATARIO_SEM_ENDERECO,
                        "Cliente #" + destinatario.id() + " (" + destinatario.nome() + ") sem endereço com código IBGE do município"));
        if (!emitente.uf().equalsIgnoreCase(enderecoDestinatario.uf())) {
            throw new BusinessException(FiscalErrorCode.OPERACAO_INTERESTADUAL_NAO_SUPORTADA,
                    "Emitente em " + emitente.uf() + ", destinatário em " + enderecoDestinatario.uf());
        }
        NFIndicadorIEDestinatario indicadorIe = temTexto(destinatario.inscricaoEstadual())
                ? NFIndicadorIEDestinatario.CONTRIBUINTE_ICMS
                : NFIndicadorIEDestinatario.NAO_CONTRIBUINTE;

        List<NFNotaInfoItem> itens = new ArrayList<>();
        for (NotaFiscalEletronicaItem item : nota.getItens()) {
            itens.add(item(item));
        }
        NFNotaInfoICMSTotal totalIcms = totalIcms(itens);

        NFNotaInfo info = new NFNotaInfo();
        info.setVersao(new BigDecimal("4.00"));
        info.setIdentificacao(identificacao(nota, emitente, indicadorIe));
        info.setEmitente(emitente(emitente));
        info.setDestinatario(destinatario(nota, destinatario, enderecoDestinatario, indicadorIe));
        info.setItens(itens);
        info.setTotal(total(nota, totalIcms));
        info.setTransporte(transporte(transportadora));
        info.setPagamento(pagamento(totalIcms.getValorTotalNFe()));
        responsavelTecnico().ifPresent(info::setInformacaoResposavelTecnico);

        NFNota nfNota = new NFNota();
        nfNota.setInfo(info);
        return nfNota;
    }

    /**
     * cNF determinístico por nota (nunca igual ao nNF, regra da SEFAZ): retransmitir a mesma nota depois de uma falha
     * de comunicação gera a mesma chave de acesso, e a SEFAZ responde "duplicidade" em vez de aceitar um segundo
     * documento com o mesmo número.
     */
    static String codigoNumerico(NotaFiscalEletronica nota) {
        long valor = Math.floorMod(nota.getId() * 2_654_435_761L, 100_000_000L);
        String codigo = String.format("%08d", valor);
        return codigo.equals(String.format("%08d", nota.getNumero() % 100_000_000L))
                ? String.format("%08d", (valor + 1) % 100_000_000L)
                : codigo;
    }

    private NFNotaInfoIdentificacao identificacao(NotaFiscalEletronica nota, EmpresaFiscalDTO emitente,
                                                 NFIndicadorIEDestinatario indicadorIe) {
        NFNotaInfoIdentificacao identificacao = new NFNotaInfoIdentificacao();
        identificacao.setUf(DFUnidadeFederativa.valueOfCodigo(emitente.uf()));
        identificacao.setCodigoRandomico(codigoNumerico(nota));
        identificacao.setNaturezaOperacao("Venda de mercadoria");
        identificacao.setModelo(DFModelo.NFE);
        identificacao.setSerie(nota.getSerie());
        identificacao.setNumeroNota(String.valueOf(nota.getNumero()));
        identificacao.setDataHoraEmissao(ZonedDateTime.ofInstant(nota.getDataEmissao(), FUSO_BRASILIA).truncatedTo(ChronoUnit.SECONDS));
        identificacao.setTipo(NFTipo.SAIDA);
        identificacao.setIdentificadorLocalDestinoOperacao(NFIdentificadorLocalDestinoOperacao.OPERACAO_INTERNA);
        identificacao.setCodigoMunicipio(String.valueOf(emitente.codigoIbgeMunicipio()));
        identificacao.setTipoImpressao(NFTipoImpressao.DANFE_NORMAL_RETRATO);
        identificacao.setTipoEmissao(NFTipoEmissao.EMISSAO_NORMAL);
        identificacao.setAmbiente(nota.getAmbiente() == AmbienteFiscal.PRODUCAO ? DFAmbiente.PRODUCAO : DFAmbiente.HOMOLOGACAO);
        identificacao.setFinalidade(NFFinalidade.NORMAL);
        // Não contribuinte é sempre consumidor final (rejeição 696); contribuinte com IE é tratado como revenda/insumo.
        identificacao.setOperacaoConsumidorFinal(indicadorIe == NFIndicadorIEDestinatario.NAO_CONTRIBUINTE
                ? NFOperacaoConsumidorFinal.SIM
                : NFOperacaoConsumidorFinal.NAO);
        // Venda do ERP (retaguarda) não é presencial de balcão; sem marketplace/intermediador.
        identificacao.setIndicadorPresencaComprador(NFIndicadorPresencaComprador.OPERACAO_NAO_PRESENCIAL_OUTROS);
        identificacao.setIndIntermed(NFIndicadorIntermediador.OPERACAO_SEM_INTERMEDIADOR);
        identificacao.setProgramaEmissor(NFProcessoEmissor.CONTRIBUINTE);
        identificacao.setVersaoEmissor(VERSAO_EMISSOR);
        return identificacao;
    }

    private NFNotaInfoEmitente emitente(EmpresaFiscalDTO emitente) {
        NFEndereco endereco = endereco(emitente.logradouro(), emitente.numero(), emitente.complemento(), emitente.bairro(),
                emitente.codigoIbgeMunicipio(), emitente.municipio(), emitente.uf(), emitente.cep());
        if (temTexto(emitente.telefone())) {
            endereco.setTelefone(digitos(emitente.telefone()));
        }
        NFNotaInfoEmitente nfEmitente = new NFNotaInfoEmitente();
        nfEmitente.setCnpj(digitos(emitente.cnpj()));
        nfEmitente.setRazaoSocial(emitente.razaoSocial());
        if (temTexto(emitente.nomeFantasia())) {
            nfEmitente.setNomeFantasia(emitente.nomeFantasia());
        }
        nfEmitente.setEndereco(endereco);
        nfEmitente.setInscricaoEstadual(digitos(emitente.inscricaoEstadual()));
        nfEmitente.setRegimeTributario(NFRegimeTributario.valueOfCodigo(emitente.crt()));
        return nfEmitente;
    }

    private NFNotaInfoDestinatario destinatario(NotaFiscalEletronica nota, ClienteFiscalDTO destinatario,
                                               EnderecoClienteDTO e, NFIndicadorIEDestinatario indicadorIe) {
        NFNotaInfoDestinatario nfDestinatario = new NFNotaInfoDestinatario();
        String documento = digitos(destinatario.documento());
        if (documento.length() > 11) {
            nfDestinatario.setCnpj(documento);
        } else {
            nfDestinatario.setCpf(documento);
        }
        nfDestinatario.setRazaoSocial(nota.getAmbiente() == AmbienteFiscal.PRODUCAO ? destinatario.nome() : NOME_DESTINATARIO_HOMOLOGACAO);
        nfDestinatario.setEndereco(endereco(e.logradouro(), e.numero(), e.complemento(), e.bairro(),
                e.codigoIbgeMunicipio(), e.municipio(), e.uf(), e.cep()));
        nfDestinatario.setIndicadorIEDestinatario(indicadorIe);
        if (indicadorIe == NFIndicadorIEDestinatario.CONTRIBUINTE_ICMS) {
            nfDestinatario.setInscricaoEstadual(digitos(destinatario.inscricaoEstadual()));
        }
        if (temTexto(destinatario.email())) {
            nfDestinatario.setEmail(destinatario.email());
        }
        return nfDestinatario;
    }

    private NFEndereco endereco(String logradouro, String numero, String complemento, String bairro, Integer codigoMunicipio,
                                String municipio, String uf, String cep) {
        NFEndereco endereco = new NFEndereco();
        endereco.setLogradouro(logradouro);
        endereco.setNumero(temTexto(numero) ? numero : "S/N");
        if (temTexto(complemento)) {
            endereco.setComplemento(complemento);
        }
        endereco.setBairro(bairro);
        endereco.setCodigoMunicipio(String.valueOf(codigoMunicipio));
        endereco.setDescricaoMunicipio(municipio);
        endereco.setUf(DFUnidadeFederativa.valueOfCodigo(uf));
        endereco.setCep(digitos(cep));
        endereco.setCodigoPais(DFPais.BRASIL);
        endereco.setDescricaoPais("BRASIL");
        return endereco;
    }

    private NFNotaInfoItem item(NotaFiscalEletronicaItem item) {
        BigDecimal quantidade = item.getQuantidade().setScale(4, RoundingMode.HALF_UP);
        BigDecimal valorUnitario = unitario(item.getValorUnitario());
        // vProd = qCom x vUnCom (rejeição 629). O valorTotal do item da venda já vem líquido do desconto.
        BigDecimal valorBruto = dinheiro(quantidade.multiply(valorUnitario));
        BigDecimal desconto = dinheiro(item.getValorDesconto());
        String gtin = item.getGtin() != null && GTIN_VALIDO.matcher(item.getGtin()).matches() ? item.getGtin() : SEM_GTIN;
        String unidade = temTexto(item.getUnidade()) ? item.getUnidade() : "UN";

        NFNotaInfoItemProduto produto = new NFNotaInfoItemProduto();
        produto.setCodigo(String.valueOf(item.getProdutoId()));
        produto.setCodigoDeBarrasGtin(gtin);
        produto.setDescricao(item.getDescricao());
        produto.setNcm(item.getNcm());
        produto.setCfop(temTexto(item.getCfop()) ? item.getCfop() : CFOP_PADRAO);
        produto.setUnidadeComercial(unidade);
        produto.setQuantidadeComercial(quantidade);
        produto.setValorUnitario(valorUnitario);
        produto.setValorTotalBruto(valorBruto);
        produto.setCodigoDeBarrasGtinTributavel(gtin);
        produto.setUnidadeTributavel(unidade);
        produto.setQuantidadeTributavel(quantidade);
        produto.setValorUnitarioTributavel(valorUnitario);
        if (desconto.signum() > 0) {
            produto.setValorDesconto(desconto);
        }
        produto.setCompoeValorNota(NFProdutoCompoeValorNota.SIM);

        NFNotaInfoItemImposto imposto = new NFNotaInfoItemImposto();
        imposto.setIcms(icms(item));
        ipi(item).ifPresent(imposto::setIpi);
        imposto.setPis(pis(item));
        imposto.setCofins(cofins(item));
        ibsCbs(item).ifPresent(imposto::setIbsCbs);

        NFNotaInfoItem nfItem = new NFNotaInfoItem();
        nfItem.setNumeroItem(item.getNumeroItem());
        nfItem.setProduto(produto);
        nfItem.setImposto(imposto);
        return nfItem;
    }

    private NFNotaInfoItemImpostoICMS icms(NotaFiscalEletronicaItem item) {
        NFNotaInfoItemImpostoICMS icms = new NFNotaInfoItemImpostoICMS();
        NFOrigem origem = NFOrigem.valueOfCodigo(temTexto(item.getOrigemMercadoria()) ? item.getOrigemMercadoria() : "0");
        if (temTexto(item.getCsosn())) {
            String csosn = item.getCsosn();
            if (CSOSN_SEM_CREDITO.contains(csosn)) {
                NFNotaInfoItemImpostoICMSSN102 sn = new NFNotaInfoItemImpostoICMSSN102();
                sn.setOrigem(origem);
                sn.setSituacaoOperacaoSN(NFNotaSituacaoOperacionalSimplesNacional.valueOfCodigo(csosn));
                icms.setIcmssn102(sn);
            } else if ("500".equals(csosn)) {
                NFNotaInfoItemImpostoICMSSN500 sn = new NFNotaInfoItemImpostoICMSSN500();
                sn.setOrigem(origem);
                sn.setSituacaoOperacaoSN(NFNotaSituacaoOperacionalSimplesNacional.CSOSN_500);
                icms.setIcmssn500(sn);
            } else {
                throw naoSuportada(item, "CSOSN " + csosn + " (exige crédito do Simples ou ICMS-ST, não calculados pelo motor tributário)");
            }
            return icms;
        }
        String cst = item.getCstIcms();
        if ("00".equals(cst)) {
            NFNotaInfoItemImpostoICMS00 icms00 = new NFNotaInfoItemImpostoICMS00();
            icms00.setOrigem(origem);
            icms00.setSituacaoTributaria(NFNotaInfoImpostoTributacaoICMS.CST_00);
            icms00.setModalidadeBCICMS(NFNotaInfoItemModalidadeBCICMS.VALOR_OPERACAO);
            icms00.setValorBaseCalculo(dinheiro(item.getValorBaseIcms()));
            icms00.setPercentualAliquota(percentual(item.getAliquotaIcms()));
            icms00.setValorTributo(dinheiro(item.getValorIcms()));
            icms.setIcms00(icms00);
        } else if (CST_ICMS_SEM_DESTAQUE.contains(cst)) {
            NFNotaInfoItemImpostoICMS40 icms40 = new NFNotaInfoItemImpostoICMS40();
            icms40.setOrigem(origem);
            icms40.setSituacaoTributaria(NFNotaInfoImpostoTributacaoICMS.valueOfCodigo(cst));
            icms.setIcms40(icms40);
        } else if ("60".equals(cst)) {
            NFNotaInfoItemImpostoICMS60 icms60 = new NFNotaInfoItemImpostoICMS60();
            icms60.setOrigem(origem);
            icms60.setSituacaoTributaria(NFNotaInfoImpostoTributacaoICMS.CST_60);
            icms.setIcms60(icms60);
        } else {
            throw naoSuportada(item, "CST de ICMS " + cst + " (exige ST, redução de base ou diferimento, não calculados pelo motor tributário)");
        }
        return icms;
    }

    private Optional<NFNotaInfoItemImpostoIPI> ipi(NotaFiscalEletronicaItem item) {
        if (item.getAliquotaIpi() == null) {
            return Optional.empty();
        }
        NFNotaInfoItemImpostoIPITributado tributado = new NFNotaInfoItemImpostoIPITributado();
        boolean comAliquota = item.getAliquotaIpi().signum() > 0;
        tributado.setSituacaoTributaria(NFNotaInfoSituacaoTributariaIPI.valueOfCodigo(comAliquota ? "50" : "99"));
        tributado.setValorBaseCalculo(dinheiro(item.getValorTotal()));
        tributado.setPercentualAliquota(percentual(item.getAliquotaIpi()));
        tributado.setValorTributo(dinheiro(item.getValorIpi()));
        NFNotaInfoItemImpostoIPI ipi = new NFNotaInfoItemImpostoIPI();
        ipi.setCodigoEnquadramento("999"); // 999 = tributação normal, sem enquadramento legal específico
        ipi.setTributado(tributado);
        return Optional.of(ipi);
    }

    private NFNotaInfoItemImpostoPIS pis(NotaFiscalEletronicaItem item) {
        String cst = temTexto(item.getCstPis()) ? item.getCstPis() : "01";
        NFNotaInfoSituacaoTributariaPIS situacao = NFNotaInfoSituacaoTributariaPIS.valueOfCodigo(cst);
        NFNotaInfoItemImpostoPIS pis = new NFNotaInfoItemImpostoPIS();
        if (CST_PIS_COFINS_ALIQUOTA.contains(cst)) {
            NFNotaInfoItemImpostoPISAliquota aliquota = new NFNotaInfoItemImpostoPISAliquota();
            aliquota.setSituacaoTributaria(situacao);
            aliquota.setValorBaseCalculo(dinheiro(item.getValorTotal()));
            aliquota.setPercentualAliquota(percentual(item.getAliquotaPis()));
            aliquota.setValorTributo(dinheiro(item.getValorPis()));
            pis.setAliquota(aliquota);
        } else if (CST_PIS_COFINS_NAO_TRIBUTADO.contains(cst)) {
            NFNotaInfoItemImpostoPISNaoTributado naoTributado = new NFNotaInfoItemImpostoPISNaoTributado();
            naoTributado.setSituacaoTributaria(situacao);
            pis.setNaoTributado(naoTributado);
        } else if ("03".equals(cst)) {
            throw naoSuportada(item, "CST de PIS 03 (tributação por quantidade)");
        } else {
            NFNotaInfoItemImpostoPISOutrasOperacoes outras = new NFNotaInfoItemImpostoPISOutrasOperacoes();
            outras.setSituacaoTributaria(situacao);
            outras.setValorBaseCalculo(dinheiro(item.getAliquotaPis() != null ? item.getValorTotal() : BigDecimal.ZERO));
            outras.setPercentualAliquota(percentual(item.getAliquotaPis()));
            outras.setValorTributo(dinheiro(item.getValorPis()));
            pis.setOutrasOperacoes(outras);
        }
        return pis;
    }

    private NFNotaInfoItemImpostoCOFINS cofins(NotaFiscalEletronicaItem item) {
        String cst = temTexto(item.getCstCofins()) ? item.getCstCofins() : "01";
        NFNotaInfoSituacaoTributariaCOFINS situacao = NFNotaInfoSituacaoTributariaCOFINS.valueOfCodigo(cst);
        NFNotaInfoItemImpostoCOFINS cofins = new NFNotaInfoItemImpostoCOFINS();
        if (CST_PIS_COFINS_ALIQUOTA.contains(cst)) {
            NFNotaInfoItemImpostoCOFINSAliquota aliquota = new NFNotaInfoItemImpostoCOFINSAliquota();
            aliquota.setSituacaoTributaria(situacao);
            aliquota.setValorBaseCalculo(dinheiro(item.getValorTotal()));
            aliquota.setPercentualAliquota(percentual(item.getAliquotaCofins()));
            aliquota.setValor(dinheiro(item.getValorCofins()));
            cofins.setAliquota(aliquota);
        } else if (CST_PIS_COFINS_NAO_TRIBUTADO.contains(cst)) {
            NFNotaInfoItemImpostoCOFINSNaoTributavel naoTributavel = new NFNotaInfoItemImpostoCOFINSNaoTributavel();
            naoTributavel.setSituacaoTributaria(situacao);
            cofins.setNaoTributavel(naoTributavel);
        } else if ("03".equals(cst)) {
            throw naoSuportada(item, "CST de COFINS 03 (tributação por quantidade)");
        } else {
            NFNotaInfoItemImpostoCOFINSOutrasOperacoes outras = new NFNotaInfoItemImpostoCOFINSOutrasOperacoes();
            outras.setSituacaoTributaria(situacao);
            outras.setValorBaseCalculo(dinheiro(item.getAliquotaCofins() != null ? item.getValorTotal() : BigDecimal.ZERO));
            outras.setPercentualCOFINS(percentual(item.getAliquotaCofins()));
            outras.setValorCOFINS(dinheiro(item.getValorCofins()));
            cofins.setOutrasOperacoes(outras);
        }
        return cofins;
    }

    /**
     * Grupo UB. Isenção/imunidade leva só CST + cClassTrib; com base, leva {@code gIBSCBS} com IBS UF, IBS município e
     * CBS, cada um com {@code gRed} quando há redução de alíquota.
     */
    private Optional<NFNotaInfoItemImpostoIBSCBS> ibsCbs(NotaFiscalEletronicaItem item) {
        if (item.getCstIbsCbs() == null) {
            return Optional.empty();
        }
        NFNotaInfoItemImpostoIBSCBS ibsCbs = new NFNotaInfoItemImpostoIBSCBS();
        ibsCbs.setCst(NFNotaInfoImpostoTributacaoIBSCBS.valueOfCodigo(item.getCstIbsCbs()));
        ibsCbs.setcClassTrib(item.getClassificacaoIbsCbs());
        if (item.getValorBaseIbsCbs() == null) {
            return Optional.of(ibsCbs);
        }
        NFNotaInfoItemImpostoIBSCBSTIBS grupo = new NFNotaInfoItemImpostoIBSCBSTIBS();
        grupo.setVBC(dinheiro(item.getValorBaseIbsCbs()));

        NFNotaInfoItemImpostoIBSCBSTIBS.GIBSUF uf = new NFNotaInfoItemImpostoIBSCBSTIBS.GIBSUF();
        uf.setPIBSUF(percentual(item.getAliquotaIbsUf()));
        reducao(item.getPercentualReducaoIbs(), item.getAliquotaEfetivaIbsUf()).ifPresent(uf::setGRed);
        uf.setVIBSUF(dinheiro(item.getValorIbsUf()));
        grupo.setGIBSUF(uf);

        NFNotaInfoItemImpostoIBSCBSTIBS.GIBSMun municipio = new NFNotaInfoItemImpostoIBSCBSTIBS.GIBSMun();
        municipio.setPIBSMun(percentual(item.getAliquotaIbsMunicipio()));
        reducao(item.getPercentualReducaoIbs(), item.getAliquotaEfetivaIbsMunicipio()).ifPresent(municipio::setGRed);
        municipio.setVIBSMun(dinheiro(item.getValorIbsMunicipio()));
        grupo.setGIBSMun(municipio);

        grupo.setVIBS(dinheiro(item.getValorIbs()));

        NFNotaInfoItemImpostoIBSCBSTIBS.GCBS cbs = new NFNotaInfoItemImpostoIBSCBSTIBS.GCBS();
        cbs.setPCBS(percentual(item.getAliquotaCbs()));
        reducao(item.getPercentualReducaoCbs(), item.getAliquotaEfetivaCbs()).ifPresent(cbs::setGRed);
        cbs.setVCBS(dinheiro(item.getValorCbs()));
        grupo.setGCBS(cbs);

        ibsCbs.setGIBSCBS(grupo);
        return Optional.of(ibsCbs);
    }

    private Optional<NFNotaInfoItemImpostoIBSCBSTIBS.GRed> reducao(BigDecimal percentualReducao, BigDecimal aliquotaEfetiva) {
        if (percentualReducao == null) {
            return Optional.empty();
        }
        NFNotaInfoItemImpostoIBSCBSTIBS.GRed reducao = new NFNotaInfoItemImpostoIBSCBSTIBS.GRed();
        reducao.setPRedAliq(percentual(percentualReducao));
        reducao.setPAliqEfet(percentual(aliquotaEfetiva));
        return Optional.of(reducao);
    }

    /** Grupo W02, somado a partir dos itens já montados (o que vai no XML), não dos valores brutos da entidade. */
    private NFNotaInfoICMSTotal totalIcms(List<NFNotaInfoItem> itens) {
        BigDecimal baseIcms = BigDecimal.ZERO;
        BigDecimal valorIcms = BigDecimal.ZERO;
        BigDecimal produtos = BigDecimal.ZERO;
        BigDecimal descontos = BigDecimal.ZERO;
        BigDecimal ipi = BigDecimal.ZERO;
        BigDecimal pis = BigDecimal.ZERO;
        BigDecimal cofins = BigDecimal.ZERO;
        for (NFNotaInfoItem item : itens) {
            NFNotaInfoItemProduto produto = item.getProduto();
            produtos = produtos.add(new BigDecimal(produto.getValorTotalBruto()));
            if (produto.getValorDesconto() != null) {
                descontos = descontos.add(new BigDecimal(produto.getValorDesconto()));
            }
            NFNotaInfoItemImposto imposto = item.getImposto();
            NFNotaInfoItemImpostoICMS00 icms00 = imposto.getIcms().getIcms00();
            if (icms00 != null) {
                baseIcms = baseIcms.add(new BigDecimal(icms00.getValorBaseCalculo()));
                valorIcms = valorIcms.add(new BigDecimal(icms00.getValorTributo()));
            }
            if (imposto.getIpi() != null && imposto.getIpi().getTributado() != null) {
                ipi = ipi.add(new BigDecimal(imposto.getIpi().getTributado().getValorTributo()));
            }
            pis = pis.add(valorPis(imposto.getPis()));
            cofins = cofins.add(valorCofins(imposto.getCofins()));
        }
        NFNotaInfoICMSTotal total = new NFNotaInfoICMSTotal();
        total.setBaseCalculoICMS(baseIcms);
        total.setValorTotalICMS(valorIcms);
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
        total.setValorTotalIPI(ipi);
        total.setValorTotalIPIDevolvido(BigDecimal.ZERO);
        total.setValorPIS(pis);
        total.setValorCOFINS(cofins);
        total.setOutrasDespesasAcessorias(BigDecimal.ZERO);
        // vNF = vProd - vDesc + vIPI (+ ST/frete/seguro/outros, todos zero aqui) — rejeição 610 se divergir.
        total.setValorTotalNFe(produtos.subtract(descontos).add(ipi));
        return total;
    }

    private static BigDecimal valorPis(NFNotaInfoItemImpostoPIS pis) {
        if (pis.getAliquota() != null) {
            return new BigDecimal(pis.getAliquota().getValorTributo());
        }
        return pis.getOutrasOperacoes() != null ? new BigDecimal(pis.getOutrasOperacoes().getValorTributo()) : BigDecimal.ZERO;
    }

    private static BigDecimal valorCofins(NFNotaInfoItemImpostoCOFINS cofins) {
        if (cofins.getAliquota() != null) {
            return new BigDecimal(cofins.getAliquota().getValor());
        }
        return cofins.getOutrasOperacoes() != null ? new BigDecimal(cofins.getOutrasOperacoes().getValorCOFINS()) : BigDecimal.ZERO;
    }

    private NFNotaInfoTotal total(NotaFiscalEletronica nota, NFNotaInfoICMSTotal totalIcms) {
        NFNotaInfoTotal total = new NFNotaInfoTotal();
        total.setIcmsTotal(totalIcms);
        if (nota.getValorBaseIbsCbs() != null) {
            total.setIbscbsTot(totalIbsCbs(nota));
        }
        return total;
    }

    /** Grupo W03. Sem diferimento, devolução de tributo nem crédito presumido nesta fase: zeros. */
    private NFNotaInfoIBSCBSTot totalIbsCbs(NotaFiscalEletronica nota) {
        NFNotaInfoIBSCBSTot.GIBS.GIBSUF uf = new NFNotaInfoIBSCBSTot.GIBS.GIBSUF();
        uf.setVDif(BigDecimal.ZERO);
        uf.setVDevTrib(BigDecimal.ZERO);
        uf.setVIBSUF(dinheiro(nota.getValorIbsUf()));

        NFNotaInfoIBSCBSTot.GIBS.GIBSMun municipio = new NFNotaInfoIBSCBSTot.GIBS.GIBSMun();
        municipio.setVDif(BigDecimal.ZERO);
        municipio.setVDevTrib(BigDecimal.ZERO);
        municipio.setVIBSMun(dinheiro(nota.getValorIbsMunicipio()));

        NFNotaInfoIBSCBSTot.GIBS ibs = new NFNotaInfoIBSCBSTot.GIBS();
        ibs.setGIBSUF(uf);
        ibs.setGIBSMun(municipio);
        ibs.setVIBS(dinheiro(nota.getValorIbs()));
        ibs.setVCredPres(BigDecimal.ZERO);
        ibs.setVCredPresCondSus(BigDecimal.ZERO);

        NFNotaInfoIBSCBSTot.GCBS cbs = new NFNotaInfoIBSCBSTot.GCBS();
        cbs.setVDif(BigDecimal.ZERO);
        cbs.setVDevTrib(BigDecimal.ZERO);
        cbs.setVCBS(dinheiro(nota.getValorCbs()));
        cbs.setVCredPres(BigDecimal.ZERO);
        cbs.setVCredPresCondSus(BigDecimal.ZERO);

        NFNotaInfoIBSCBSTot total = new NFNotaInfoIBSCBSTot();
        total.setVBCIBSCBS(dinheiro(nota.getValorBaseIbsCbs()));
        total.setGIBS(ibs);
        total.setGCBS(cbs);
        return total;
    }

    private NFNotaInfoTransporte transporte(ClienteFiscalDTO transportadora) {
        NFNotaInfoTransporte transporte = new NFNotaInfoTransporte();
        if (transportadora == null) {
            transporte.setModalidadeFrete(NFModalidadeFrete.SEM_OCORRENCIA_TRANSPORTE);
            return transporte;
        }
        transporte.setModalidadeFrete(NFModalidadeFrete.CONTRATACAO_POR_CONTA_DO_REMETENTE);
        NFNotaInfoTransportador transportador = new NFNotaInfoTransportador();
        String documento = digitos(transportadora.documento());
        if (documento.length() > 11) {
            transportador.setCnpj(documento);
        } else if (!documento.isEmpty()) {
            transportador.setCpf(documento);
        }
        transportador.setRazaoSocial(transportadora.nome());
        if (!transportadora.enderecos().isEmpty()) {
            EnderecoClienteDTO endereco = transportadora.enderecos().get(0);
            transportador.setNomeMunicipio(endereco.municipio());
            transportador.setUf(DFUnidadeFederativa.valueOfCodigo(endereco.uf()));
        }
        transporte.setTransportador(transportador);
        return transporte;
    }

    private NFNotaInfoPagamento pagamento(String valorNota) {
        NFNotaInfoFormaPagamento forma = new NFNotaInfoFormaPagamento()
                .setMeioPagamento(NFMeioPagamento.OUTRO)
                .setDescricaoMeioPagamento("Conforme condicao comercial da venda")
                .setValorPagamento(new BigDecimal(valorNota));
        return new NFNotaInfoPagamento().setDetalhamentoFormasPagamento(List.of(forma));
    }

    private Optional<NFNotaInfoResponsavelTecnico> responsavelTecnico() {
        FiscalProperties.ResponsavelTecnico responsavel = properties.responsavelTecnico();
        if (responsavel == null || !temTexto(responsavel.cnpj())) {
            return Optional.empty();
        }
        return Optional.of(new NFNotaInfoResponsavelTecnico()
                .setCnpj(digitos(responsavel.cnpj()))
                .setContatoNome(responsavel.contato())
                .setEmail(responsavel.email())
                .setTelefone(digitos(responsavel.telefone())));
    }

    private static BusinessException naoSuportada(NotaFiscalEletronicaItem item, String motivo) {
        return new BusinessException(FiscalErrorCode.TRIBUTACAO_ITEM_NAO_SUPORTADA,
                "Item " + item.getNumeroItem() + " (produto #" + item.getProdutoId() + "): " + motivo);
    }

    // Os setters da biblioteca recusam casas decimais além do leiaute (lançam, não arredondam): todo valor passa por aqui.

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

    private static boolean temTexto(String valor) {
        return valor != null && !valor.isBlank();
    }
}
