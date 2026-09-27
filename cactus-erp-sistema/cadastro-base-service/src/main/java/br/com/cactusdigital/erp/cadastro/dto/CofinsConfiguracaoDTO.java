package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.CstPisCofins;
import br.com.cactusdigital.erp.cadastro.entity.ModalidadeBaseCalculo;
import java.math.BigDecimal;

public record CofinsConfiguracaoDTO(
        CstPisCofins cst,
        ModalidadeBaseCalculo modalidadeBaseCalculo,
        BigDecimal porcentoBaseCalculo,
        BigDecimal aliquotaPercentual,
        BigDecimal aliquotaUnidade,
        BigDecimal valorPrecoMaximo,
        BigDecimal valorPautaFiscal) {
}
