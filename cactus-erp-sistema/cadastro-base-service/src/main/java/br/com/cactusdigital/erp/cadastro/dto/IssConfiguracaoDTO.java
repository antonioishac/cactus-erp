package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.CodigoTributacaoIss;
import br.com.cactusdigital.erp.cadastro.entity.ModalidadeBaseCalculoIss;
import java.math.BigDecimal;

public record IssConfiguracaoDTO(
        ModalidadeBaseCalculoIss modalidadeBaseCalculo,
        BigDecimal porcentoBaseCalculo,
        BigDecimal aliquotaPercentual,
        BigDecimal aliquotaUnidade,
        BigDecimal valorPrecoMaximo,
        BigDecimal valorPautaFiscal,
        Integer itemListaServico,
        CodigoTributacaoIss codigoTributacao) {
}
