package br.com.cactusdigital.erp.loja.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/** {@code valoresAtributos} precisa cobrir todo {@code AtributoCategoria.obrigatorio=true} com {@code geraVariacao=true} da categoria do produto — validado em {@code VarianteProdutoLojaService#criar}. */
public record VarianteProdutoLojaRequest(
        @NotBlank @Size(max = 50) String sku,
        @Size(max = 20) String gtin,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal precoVenda,
        BigDecimal precoPromocional,
        BigDecimal estoqueQuantidade,
        BigDecimal pesoKg,
        @Size(max = 8) String ncm,
        @Size(max = 3) String csosn,
        @Valid List<ValorAtributoRequest> valoresAtributos) {
}
