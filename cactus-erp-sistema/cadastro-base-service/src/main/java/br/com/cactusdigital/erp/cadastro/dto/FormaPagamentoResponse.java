package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.TefTipoGp;
import br.com.cactusdigital.erp.common.domain.Situacao;
import java.time.Instant;

public record FormaPagamentoResponse(
        Long id,
        String codigo,
        String descricao,
        String icone,
        boolean tef,
        boolean imprimeVinculado,
        boolean permiteTroco,
        TefTipoGp tefTipoGp,
        boolean geraParcelas,
        String codigoPagamentoNfce,
        Situacao situacao,
        Instant dataAtualizacao) {
}
