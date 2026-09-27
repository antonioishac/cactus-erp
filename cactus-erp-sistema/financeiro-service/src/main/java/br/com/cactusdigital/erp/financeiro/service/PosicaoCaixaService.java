package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.financeiro.dto.PosicaoCaixaResponse;
import br.com.cactusdigital.erp.financeiro.dto.PosicaoCaixaTipoResponse;
import br.com.cactusdigital.erp.financeiro.entity.ContaBancaria;
import br.com.cactusdigital.erp.financeiro.entity.TipoContaBancaria;
import br.com.cactusdigital.erp.financeiro.repository.ContaBancariaRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Posição de caixa consolidada — soma {@code saldoAtual} de todas as {@link ContaBancaria}
 * ATIVAS da empresa, agrupado por {@link TipoContaBancaria}. Sem snapshot persistido,
 * calculado sob demanda (mesmo critério de {@code CurvaAbcService}/{@code FluxoCaixaService}
 * — o saldo já vive corrigido em cada conta, não precisa agregação pesada).
 */
@Service
@RequiredArgsConstructor
public class PosicaoCaixaService {

    private final ContaBancariaRepository repository;

    @Transactional(readOnly = true)
    public PosicaoCaixaResponse consultar(Long empresaId) {
        List<ContaBancaria> contas = repository.findByEmpresaIdAndAtivoTrue(empresaId);

        Map<TipoContaBancaria, List<ContaBancaria>> porTipo = contas.stream()
                .collect(Collectors.groupingBy(ContaBancaria::getTipo));

        List<PosicaoCaixaTipoResponse> resumoPorTipo = porTipo.entrySet().stream()
                .map(entry -> new PosicaoCaixaTipoResponse(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream().map(ContaBancaria::getSaldoAtual).reduce(BigDecimal.ZERO, BigDecimal::add)))
                .sorted(Comparator.comparing(PosicaoCaixaTipoResponse::tipo))
                .toList();

        BigDecimal saldoGeral = contas.stream().map(ContaBancaria::getSaldoAtual).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PosicaoCaixaResponse(saldoGeral, resumoPorTipo);
    }
}
