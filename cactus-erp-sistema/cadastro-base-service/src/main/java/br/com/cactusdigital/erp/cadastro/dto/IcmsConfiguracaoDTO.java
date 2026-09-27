package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.Csosn;
import br.com.cactusdigital.erp.cadastro.entity.CstIcms;
import br.com.cactusdigital.erp.cadastro.entity.ModalidadeBaseCalculoIcms;
import br.com.cactusdigital.erp.cadastro.entity.ModalidadeBaseCalculoIcmsSt;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * ICMS de uma UF de destino específica — {@code cstIcms} (Regime Normal) e {@code csosn}
 * (Simples Nacional) são mutuamente exclusivos, mas a validação de qual preencher é regra
 * de negócio (depende do CRT da empresa vendedora), não deste DTO.
 */
public record IcmsConfiguracaoDTO(
        @NotBlank @Size(min = 2, max = 2) String ufDestino,
        CstIcms cstIcms,
        Csosn csosn,
        ModalidadeBaseCalculoIcms modalidadeBaseCalculo,
        BigDecimal aliquota,
        BigDecimal valorPauta,
        BigDecimal valorPrecoMaximo,
        BigDecimal mva,
        BigDecimal porcentoBaseCalculo,
        ModalidadeBaseCalculoIcmsSt modalidadeBaseCalculoSt,
        BigDecimal aliquotaInternaSt,
        BigDecimal aliquotaInterestadualSt,
        BigDecimal porcentoBaseCalculoSt,
        BigDecimal aliquotaIcmsSt,
        BigDecimal valorPautaSt,
        BigDecimal valorPrecoMaximoSt) {
}
