package br.com.cactusdigital.erp.fiscal.dto;

import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record NotaFiscalServicoResponse(
        Long id,
        Long osAberturaId,
        Long empresaId,
        Long clienteId,
        String numeroRps,
        String serieRps,
        String numero,
        String codigoVerificacao,
        String competencia,
        Instant dataEmissao,
        AmbienteFiscal ambiente,
        BigDecimal valorServicos,
        BigDecimal valorIss,
        BigDecimal valorLiquido,
        SituacaoNotaFiscal situacao,
        String mensagemSefaz,
        List<NotaFiscalServicoItemResponse> itens) {
}
