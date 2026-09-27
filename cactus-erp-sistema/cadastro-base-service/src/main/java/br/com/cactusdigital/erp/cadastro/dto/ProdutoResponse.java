package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.Csosn;
import br.com.cactusdigital.erp.cadastro.entity.CstIcms;
import br.com.cactusdigital.erp.cadastro.entity.IndicadorArredondamentoTruncamento;
import br.com.cactusdigital.erp.cadastro.entity.IndicadorProprioTerceiro;
import br.com.cactusdigital.erp.cadastro.entity.TipoItemSped;
import br.com.cactusdigital.erp.common.domain.Situacao;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ProdutoResponse(
        Long id,
        Long produtoUnidadeId,
        String produtoUnidadeSigla,
        Long produtoMarcaId,
        String produtoMarcaNome,
        Long produtoSubgrupoId,
        String produtoSubgrupoNome,
        Long produtoGrupoId,
        String produtoGrupoNome,
        Long produtoTipoId,
        String produtoTipoDescricao,
        String gtin,
        String codigoInterno,
        String nome,
        String descricao,
        String descricaoPdv,

        Long ncmId,
        String ncmCodigo,
        Long cestId,
        String cestCodigo,
        Long grupoTributarioId,
        String grupoTributarioNome,
        TipoItemSped tipoItemSped,
        IndicadorArredondamentoTruncamento iat,
        IndicadorProprioTerceiro ippt,
        String totalizadorParcial,
        Integer codigoBalanca,
        String pafPSt,

        BigDecimal valorCusto,
        BigDecimal valorCompra,
        BigDecimal valorVenda,
        BigDecimal estoqueMinimo,
        BigDecimal estoqueMaximo,

        CstIcms cstIcmsPadrao,
        Csosn csosnPadrao,
        BigDecimal aliquotaIcmsPadrao,
        BigDecimal aliquotaIpiPadrao,
        BigDecimal aliquotaPisPadrao,
        BigDecimal aliquotaCofinsPadrao,
        BigDecimal aliquotaIssqnPadrao,
        Instant dataResolucaoPadrao,

        Situacao situacao,
        List<ProdutoFaixaPrecoDTO> faixasPreco,
        List<ProdutoImagemDTO> imagens,
        Instant dataCriacao,
        Instant dataAtualizacao) {
}
