package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.IndicadorArredondamentoTruncamento;
import br.com.cactusdigital.erp.cadastro.entity.IndicadorProprioTerceiro;
import br.com.cactusdigital.erp.cadastro.entity.TipoItemSped;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record ProdutoRequest(
        @NotNull Long produtoUnidadeId,
        Long produtoMarcaId,
        @NotNull Long produtoSubgrupoId,
        Long produtoTipoId,
        @Size(max = 14) String gtin,
        @NotBlank @Size(max = 50) String codigoInterno,
        @NotBlank @Size(max = 150) String nome,
        @Size(max = 250) String descricao,
        @Size(max = 30) String descricaoPdv,

        @NotNull Long ncmId,
        Long cestId,
        @NotNull Long grupoTributarioId,
        TipoItemSped tipoItemSped,
        IndicadorArredondamentoTruncamento iat,
        IndicadorProprioTerceiro ippt,
        @Size(max = 10) String totalizadorParcial,
        Integer codigoBalanca,
        @Size(max = 1) String pafPSt,

        @DecimalMin(value = "0", inclusive = true) BigDecimal valorCusto,
        @DecimalMin(value = "0", inclusive = true) BigDecimal valorCompra,
        @DecimalMin(value = "0", inclusive = true) BigDecimal valorVenda,
        @DecimalMin(value = "0", inclusive = true) BigDecimal estoqueMinimo,
        @DecimalMin(value = "0", inclusive = true) BigDecimal estoqueMaximo,

        @NotNull Situacao situacao,
        @Valid List<ProdutoFaixaPrecoDTO> faixasPreco,
        @Valid List<ProdutoImagemDTO> imagens) {
}
