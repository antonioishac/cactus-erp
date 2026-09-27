package br.com.cactusdigital.erp.os.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record OsAberturaResponse(
        Long id,
        Long osStatusId,
        String osStatusNome,
        Long empresaId,
        Long clienteId,
        Long colaboradorId,
        String numero,
        LocalDate dataInicio,
        String horaInicio,
        LocalDate dataPrevisao,
        String horaPrevisao,
        LocalDate dataFim,
        String horaFim,
        String nomeContato,
        String foneContato,
        String observacaoCliente,
        String observacaoAbertura,
        boolean orcamentoAprovado,
        Instant dataAprovacaoOrcamento,
        BigDecimal valorTotal,
        List<OsAberturaEquipamentoResponse> equipamentos,
        List<OsProdutoServicoResponse> itens,
        List<OsEvolucaoResponse> evolucoes) {
}
