package br.com.cactusdigital.erp.fiscal.dto;

import br.com.cactusdigital.erp.fiscal.entity.TipoTomadorCte;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * A venda precisa ter frete definido ({@code VendaFrete}, com transportadora) — a
 * carga/destinatário/CIF-FOB já vêm da venda, aqui só o que é específico do serviço de
 * transporte em si (o {@code fiscal-service} não conhece preço de frete).
 */
public record GerarCteDeVendaRequest(
        @NotNull TipoTomadorCte tomador,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal valorTotalServico,
        BigDecimal aliquotaIcms,
        @Size(max = 60) String produtoPredominante,
        String observacoesGerais,
        /** 0-Não; 1-Sim. Default "0" (carga fracionada) quando não informado. */
        @Size(max = 1) String indicadorLotacao,
        LocalDate dataPrevistaEntrega,
        String ciot,
        @Valid List<GerarCteComponenteRequest> componentes) {
}
