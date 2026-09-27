package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.CstIpi;
import br.com.cactusdigital.erp.cadastro.entity.ModalidadeBaseCalculo;
import java.math.BigDecimal;

public record IpiConfiguracaoDTO(
        CstIpi cst,
        ModalidadeBaseCalculo modalidadeBaseCalculo,
        BigDecimal porcentoBaseCalculo,
        BigDecimal aliquotaPercentual,
        BigDecimal aliquotaUnidade,
        BigDecimal valorPrecoMaximo,
        BigDecimal valorPautaFiscal) {
}
