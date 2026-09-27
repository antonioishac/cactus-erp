package br.com.cactusdigital.erp.financeiro.mapper;

import br.com.cactusdigital.erp.financeiro.dto.BaixaResponse;
import br.com.cactusdigital.erp.financeiro.dto.ConfiguracaoFinanceiroEmpresaResponse;
import br.com.cactusdigital.erp.financeiro.dto.ContaPagarResponse;
import br.com.cactusdigital.erp.financeiro.dto.ContaReceberResponse;
import br.com.cactusdigital.erp.financeiro.entity.BaixaContaPagar;
import br.com.cactusdigital.erp.financeiro.entity.BaixaContaReceber;
import br.com.cactusdigital.erp.financeiro.entity.ConfiguracaoFinanceiroEmpresa;
import br.com.cactusdigital.erp.financeiro.entity.ContaPagar;
import br.com.cactusdigital.erp.financeiro.entity.ContaReceber;
import br.com.cactusdigital.erp.financeiro.entity.SituacaoTitulo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * {@code percentualMultaAtraso}/{@code percentualJurosDiaAtraso} vêm de
 * {@code ConfiguracaoFinanceiroEmpresa} (resolvida no service, por {@code empresaId} do
 * título) e são passados explicitamente pro mapper — não é responsabilidade do mapper
 * buscar configuração, só calcular em cima do que recebe (mesmo motivo de
 * {@code NfceIniBuilder}/etc. sempre receberem os DTOs já resolvidos, nunca chamarem
 * outro serviço por conta própria).
 * <p>
 * {@code diasAtraso}/{@code valorMulta}/{@code valorJuros}/{@code valorAtualizado} são
 * calculados (não persistidos) toda vez que a resposta é montada — mesmo espírito de
 * {@code vencido}: nunca fica "desatualizado" porque nunca foi de fato gravado.
 * {@code valorMulta} é aplicado uma vez sobre {@code valorSaldo} assim que vence;
 * {@code valorJuros} é {@code valorSaldo × percentualJurosDiaAtraso × diasAtraso}. Sem
 * configuração (percentual nulo) ou título não vencido, ambos são {@code ZERO} e
 * {@code valorAtualizado == valorSaldo}.
 */
@Mapper(componentModel = "spring")
public interface FinanceiroMapper {

    BaixaResponse toResponse(BaixaContaReceber baixa);

    BaixaResponse toResponse(BaixaContaPagar baixa);

    ConfiguracaoFinanceiroEmpresaResponse toResponse(ConfiguracaoFinanceiroEmpresa configuracao);

    @Mapping(target = "vencido", expression = "java(vencido(conta.getDataVencimento(), conta.getSituacao()))")
    @Mapping(target = "diasAtraso", expression = "java(diasAtraso(conta.getDataVencimento(), conta.getSituacao()))")
    @Mapping(target = "valorMulta", expression = "java(valorMulta(conta.getValorSaldo(), conta.getDataVencimento(), conta.getSituacao(), percentualMultaAtraso))")
    @Mapping(target = "valorJuros", expression = "java(valorJuros(conta.getValorSaldo(), conta.getDataVencimento(), conta.getSituacao(), percentualJurosDiaAtraso))")
    @Mapping(target = "valorAtualizado", expression = "java(conta.getValorSaldo()"
            + ".add(valorMulta(conta.getValorSaldo(), conta.getDataVencimento(), conta.getSituacao(), percentualMultaAtraso))"
            + ".add(valorJuros(conta.getValorSaldo(), conta.getDataVencimento(), conta.getSituacao(), percentualJurosDiaAtraso)))")
    ContaReceberResponse toResponse(ContaReceber conta, BigDecimal percentualMultaAtraso, BigDecimal percentualJurosDiaAtraso);

    @Mapping(target = "vencido", expression = "java(vencido(conta.getDataVencimento(), conta.getSituacao()))")
    @Mapping(target = "diasAtraso", expression = "java(diasAtraso(conta.getDataVencimento(), conta.getSituacao()))")
    @Mapping(target = "valorMulta", expression = "java(valorMulta(conta.getValorSaldo(), conta.getDataVencimento(), conta.getSituacao(), percentualMultaAtraso))")
    @Mapping(target = "valorJuros", expression = "java(valorJuros(conta.getValorSaldo(), conta.getDataVencimento(), conta.getSituacao(), percentualJurosDiaAtraso))")
    @Mapping(target = "valorAtualizado", expression = "java(conta.getValorSaldo()"
            + ".add(valorMulta(conta.getValorSaldo(), conta.getDataVencimento(), conta.getSituacao(), percentualMultaAtraso))"
            + ".add(valorJuros(conta.getValorSaldo(), conta.getDataVencimento(), conta.getSituacao(), percentualJurosDiaAtraso)))")
    ContaPagarResponse toResponse(ContaPagar conta, BigDecimal percentualMultaAtraso, BigDecimal percentualJurosDiaAtraso);

    default boolean vencido(LocalDate dataVencimento, SituacaoTitulo situacao) {
        return dataVencimento != null
                && dataVencimento.isBefore(LocalDate.now())
                && situacao != SituacaoTitulo.PAGA
                && situacao != SituacaoTitulo.CANCELADA;
    }

    default Integer diasAtraso(LocalDate dataVencimento, SituacaoTitulo situacao) {
        if (!vencido(dataVencimento, situacao)) {
            return null;
        }
        return (int) ChronoUnit.DAYS.between(dataVencimento, LocalDate.now());
    }

    default BigDecimal valorMulta(BigDecimal valorSaldo, LocalDate dataVencimento, SituacaoTitulo situacao, BigDecimal percentualMultaAtraso) {
        if (!vencido(dataVencimento, situacao) || percentualMultaAtraso == null) {
            return BigDecimal.ZERO;
        }
        return valorSaldo.multiply(percentualMultaAtraso).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
    }

    default BigDecimal valorJuros(BigDecimal valorSaldo, LocalDate dataVencimento, SituacaoTitulo situacao, BigDecimal percentualJurosDiaAtraso) {
        Integer dias = diasAtraso(dataVencimento, situacao);
        if (dias == null || percentualJurosDiaAtraso == null) {
            return BigDecimal.ZERO;
        }
        return valorSaldo.multiply(percentualJurosDiaAtraso)
                .multiply(BigDecimal.valueOf(dias))
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
    }
}
