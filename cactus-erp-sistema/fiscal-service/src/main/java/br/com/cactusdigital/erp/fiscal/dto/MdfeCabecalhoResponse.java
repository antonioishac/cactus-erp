package br.com.cactusdigital.erp.fiscal.dto;

import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record MdfeCabecalhoResponse(
        Long id,
        Long empresaId,
        String modelo,
        String modal,
        String serie,
        Long numero,
        String chaveAcesso,
        String protocoloAutorizacao,
        Instant dataEmissao,
        AmbienteFiscal ambiente,
        String uf,
        String rntrc,
        String veiculoPlaca,
        String veiculoUf,
        String veiculoRenavam,
        BigDecimal veiculoTaraKg,
        BigDecimal veiculoCapacidadeKg,
        String condutorNome,
        String condutorCpf,
        Integer municipioCarregamentoCodigoIbge,
        String municipioCarregamentoNome,
        LocalDate dataInicioViagem,
        BigDecimal valorTotalCarga,
        BigDecimal pesoBrutoTotal,
        String unidadeMedidaCarga,
        SituacaoNotaFiscal situacao,
        String mensagemSefaz,
        LocalDate dataEncerramento,
        Integer municipioEncerramentoCodigoIbge,
        String protocoloEncerramento,
        Instant dataCancelamento,
        String justificativaCancelamento,
        String protocoloCancelamento,
        List<MdfeDocumentoVinculadoResponse> documentos) {
}
