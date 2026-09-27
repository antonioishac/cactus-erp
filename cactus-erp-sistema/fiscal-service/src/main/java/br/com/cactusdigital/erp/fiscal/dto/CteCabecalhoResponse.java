package br.com.cactusdigital.erp.fiscal.dto;

import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal;
import br.com.cactusdigital.erp.fiscal.entity.TipoTomadorCte;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record CteCabecalhoResponse(
        Long id,
        Long vendaId,
        Long empresaId,
        Long clienteId,
        Long transportadoraId,
        String modelo,
        String modal,
        String serie,
        Long numero,
        String chaveAcesso,
        String protocoloAutorizacao,
        Instant dataEmissao,
        AmbienteFiscal ambiente,
        TipoTomadorCte tomador,
        String naturezaOperacao,
        String cfop,
        String produtoPredominante,
        String observacoesGerais,
        String rntrc,
        LocalDate dataPrevistaEntrega,
        String indicadorLotacao,
        String ciot,
        String placa,
        String ufPlaca,
        BigDecimal pesoBruto,
        BigDecimal quantidadeVolume,
        BigDecimal valorTotalServico,
        BigDecimal valorReceber,
        BigDecimal valorTotalCarga,
        String cst,
        BigDecimal baseCalculoIcms,
        BigDecimal aliquotaIcms,
        BigDecimal valorIcms,
        SituacaoNotaFiscal situacao,
        String mensagemSefaz,
        List<CteComponenteResponse> componentes) {
}
