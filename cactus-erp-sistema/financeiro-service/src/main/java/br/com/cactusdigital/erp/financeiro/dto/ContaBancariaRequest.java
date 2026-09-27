package br.com.cactusdigital.erp.financeiro.dto;

import br.com.cactusdigital.erp.financeiro.entity.TipoContaBancaria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * {@code bancoId} obrigatório quando {@code tipo != CAIXA_INTERNO} — validado no service
 * (não dá pra expressar essa regra condicional só com Bean Validation declarativo).
 * {@code codigoConvenioCobranca}/{@code codigoCedenteCobranca}/{@code carteiraCobranca}
 * (2026-09-07, EDI Bancário) são opcionais — só preenchidos quando a conta tem convênio
 * de cobrança registrada configurado, ver {@code Cnab240RemessaBuilder}.
 */
public record ContaBancariaRequest(
        @NotNull Long empresaId,
        Long bancoId,
        @NotNull TipoContaBancaria tipo,
        @NotBlank @Size(max = 100) String nome,
        @Size(max = 20) String agencia,
        @Size(max = 2) String agenciaDigito,
        @Size(max = 20) String numero,
        @Size(max = 2) String numeroDigito,
        @Size(max = 150) String cedenteNome,
        @Size(max = 20) String cedenteDocumento,
        @Size(max = 20) String codigoConvenioCobranca,
        @Size(max = 20) String codigoCedenteCobranca,
        @Size(max = 5) String carteiraCobranca,
        @NotNull LocalDate dataSaldoInicial,
        @NotNull BigDecimal saldoInicial) {
}
