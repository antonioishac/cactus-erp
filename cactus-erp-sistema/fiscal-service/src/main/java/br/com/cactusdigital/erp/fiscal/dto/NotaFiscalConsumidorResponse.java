package br.com.cactusdigital.erp.fiscal.dto;

import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record NotaFiscalConsumidorResponse(
        Long id,
        Long pedidoId,
        Long lojaId,
        Long empresaId,
        Long clienteLojaId,
        String modelo,
        String serie,
        Long numero,
        String chaveAcesso,
        String protocoloAutorizacao,
        Instant dataEmissao,
        AmbienteFiscal ambiente,
        BigDecimal valorTotal,
        SituacaoNotaFiscal situacao,
        String mensagemSefaz,
        List<NotaFiscalConsumidorItemResponse> itens) {
}
