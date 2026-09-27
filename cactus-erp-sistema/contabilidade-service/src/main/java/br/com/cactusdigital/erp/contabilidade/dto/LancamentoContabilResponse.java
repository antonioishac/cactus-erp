package br.com.cactusdigital.erp.contabilidade.dto;

import br.com.cactusdigital.erp.contabilidade.entity.OrigemLancamentoContabil;
import br.com.cactusdigital.erp.contabilidade.entity.SituacaoLancamentoContabil;
import java.time.LocalDate;
import java.util.List;

public record LancamentoContabilResponse(
        Long id,
        Long empresaId,
        LocalDate dataLancamento,
        String historico,
        String numeroDocumento,
        OrigemLancamentoContabil origemTipo,
        Long origemId,
        SituacaoLancamentoContabil situacao,
        Long estornoDeId,
        Long estornadoPorId,
        List<PartidaContabilResponse> partidas) {
}
