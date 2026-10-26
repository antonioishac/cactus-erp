package br.com.cactusdigital.erp.fiscal.dto;

import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record NotaFiscalEletronicaResponse(
        Long id,
        Long vendaId,
        Long empresaId,
        Long clienteId,
        Long transportadoraId,
        String modelo,
        String serie,
        Long numero,
        String chaveAcesso,
        String protocoloAutorizacao,
        Instant dataEmissao,
        AmbienteFiscal ambiente,
        BigDecimal valorTotal,
        BigDecimal valorBaseIbsCbs,
        BigDecimal valorIbsUf,
        BigDecimal valorIbsMunicipio,
        BigDecimal valorIbs,
        BigDecimal valorCbs,
        SituacaoNotaFiscal situacao,
        String mensagemSefaz,
        List<NotaFiscalEletronicaItemResponse> itens) {
}
