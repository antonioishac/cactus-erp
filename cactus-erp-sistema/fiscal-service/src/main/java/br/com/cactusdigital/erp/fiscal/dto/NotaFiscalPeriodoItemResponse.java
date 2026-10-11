package br.com.cactusdigital.erp.fiscal.dto;

import br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Linha unificada do relatório "notas emitidas por período" — achata os 4 tipos de
 * documento fiscal do módulo (NF-e/NFC-e/NFS-e/CT-e, cada um com seu próprio shape de
 * resposta rico) num formato comum pra uma grade só. {@code tipoDocumento} é um literal
 * fixo ("NFE"/"NFCE"/"NFSE"/"CTE"), não um enum — não existe um tipo de domínio que
 * unifique os 4 documentos, só esta projeção de relatório.
 */
public record NotaFiscalPeriodoItemResponse(
        String tipoDocumento,
        String numero,
        String serie,
        Instant dataEmissao,
        SituacaoNotaFiscal situacao,
        BigDecimal valorTotal) {
}
