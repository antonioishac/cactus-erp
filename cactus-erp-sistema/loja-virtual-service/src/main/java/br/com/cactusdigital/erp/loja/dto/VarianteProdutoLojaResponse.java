package br.com.cactusdigital.erp.loja.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import java.math.BigDecimal;
import java.util.List;

public record VarianteProdutoLojaResponse(
        Long id,
        Long produtoLojaId,
        String sku,
        String gtin,
        BigDecimal precoVenda,
        BigDecimal precoPromocional,
        BigDecimal estoqueQuantidade,
        BigDecimal estoqueReservado,
        BigDecimal estoqueDisponivel,
        BigDecimal pesoKg,
        String ncm,
        String csosn,
        Situacao situacao,
        List<ValorAtributoResponse> valoresAtributos) {
}
