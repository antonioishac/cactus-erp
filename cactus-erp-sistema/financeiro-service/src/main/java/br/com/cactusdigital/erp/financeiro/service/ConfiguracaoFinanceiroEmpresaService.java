package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.financeiro.dto.ConfiguracaoFinanceiroEmpresaRequest;
import br.com.cactusdigital.erp.financeiro.dto.ConfiguracaoFinanceiroEmpresaResponse;
import br.com.cactusdigital.erp.financeiro.entity.ConfiguracaoFinanceiroEmpresa;
import br.com.cactusdigital.erp.financeiro.mapper.FinanceiroMapper;
import br.com.cactusdigital.erp.financeiro.repository.ConfiguracaoFinanceiroEmpresaRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Get-or-create por empresa: se a filial nunca configurou nada, devolve um default em
 * memória (nunca persistido) em vez de 404 — mesmo padrão de
 * {@code ConfiguracaoComprasEmpresaService} (gestao-compras-service). Percentuais nulos
 * no default significam "sem cobrança de atraso configurada" — ver Javadoc de
 * {@code ConfiguracaoFinanceiroEmpresa}.
 */
@Service
@RequiredArgsConstructor
public class ConfiguracaoFinanceiroEmpresaService {

    private final ConfiguracaoFinanceiroEmpresaRepository repository;
    private final FinanceiroMapper mapper;

    @Transactional(readOnly = true)
    public ConfiguracaoFinanceiroEmpresaResponse buscarPorEmpresa(Long empresaId) {
        return repository.findByEmpresaId(empresaId)
                .map(mapper::toResponse)
                .orElseGet(() -> new ConfiguracaoFinanceiroEmpresaResponse(null, empresaId, null, null));
    }

    @Transactional
    public ConfiguracaoFinanceiroEmpresaResponse atualizar(Long empresaId, ConfiguracaoFinanceiroEmpresaRequest request) {
        ConfiguracaoFinanceiroEmpresa configuracao = repository.findByEmpresaId(empresaId)
                .orElseGet(() -> ConfiguracaoFinanceiroEmpresa.builder().empresaId(empresaId).build());
        configuracao.setPercentualMultaAtraso(request.percentualMultaAtraso());
        configuracao.setPercentualJurosDiaAtraso(request.percentualJurosDiaAtraso());
        return mapper.toResponse(repository.save(configuracao));
    }

    /** Usado por {@code ContaReceberService}/{@code ContaPagarService} pra decorar a
     * resposta com juros/multa calculados — {@code null} se a empresa nunca configurou
     * (semântica permissiva, ver Javadoc da classe). */
    @Transactional(readOnly = true)
    BigDecimal percentualMultaDaEmpresa(Long empresaId) {
        return repository.findByEmpresaId(empresaId)
                .map(ConfiguracaoFinanceiroEmpresa::getPercentualMultaAtraso)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    BigDecimal percentualJurosDiaDaEmpresa(Long empresaId) {
        return repository.findByEmpresaId(empresaId)
                .map(ConfiguracaoFinanceiroEmpresa::getPercentualJurosDiaAtraso)
                .orElse(null);
    }
}
