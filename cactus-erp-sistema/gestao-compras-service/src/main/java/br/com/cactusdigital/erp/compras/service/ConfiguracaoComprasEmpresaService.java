package br.com.cactusdigital.erp.compras.service;

import br.com.cactusdigital.erp.compras.dto.ConfiguracaoComprasEmpresaRequest;
import br.com.cactusdigital.erp.compras.dto.ConfiguracaoComprasEmpresaResponse;
import br.com.cactusdigital.erp.compras.entity.ConfiguracaoComprasEmpresa;
import br.com.cactusdigital.erp.compras.mapper.ComprasMapper;
import br.com.cactusdigital.erp.compras.repository.ConfiguracaoComprasEmpresaRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Get-or-create por empresa: se a filial nunca configurou nada, devolve um default em
 * memória (nunca persistido) em vez de 404 — toda empresa "tem" uma configuração de
 * compras, mesmo que nunca tenha sido explicitamente salva.
 * <p>
 * {@code limiteAprovacaoPedido=null} no default significa "sem limite configurado, todo
 * pedido de compra é enviado direto, sem exigir aprovação" — mesma semântica permissiva
 * já usada em {@code ConfiguracaoEstoqueEmpresa} no controle-estoque-service. Nunca
 * {@code BigDecimal.ZERO} aqui — isso sugeriria o oposto (qualquer pedido de valor
 * positivo exigindo aprovação).
 */
@Service
@RequiredArgsConstructor
public class ConfiguracaoComprasEmpresaService {

    private final ConfiguracaoComprasEmpresaRepository repository;
    private final ComprasMapper mapper;

    @Transactional(readOnly = true)
    public ConfiguracaoComprasEmpresaResponse buscarPorEmpresa(Long empresaId) {
        return repository.findByEmpresaId(empresaId)
                .map(mapper::toResponse)
                .orElseGet(() -> new ConfiguracaoComprasEmpresaResponse(null, empresaId, null));
    }

    @Transactional
    public ConfiguracaoComprasEmpresaResponse atualizar(Long empresaId, ConfiguracaoComprasEmpresaRequest request) {
        ConfiguracaoComprasEmpresa configuracao = repository.findByEmpresaId(empresaId)
                .orElseGet(() -> ConfiguracaoComprasEmpresa.builder().empresaId(empresaId).build());
        configuracao.setLimiteAprovacaoPedido(request.limiteAprovacaoPedido());
        return mapper.toResponse(repository.save(configuracao));
    }

    /** Usado por {@code PedidoCompraService#enviar} — {@code null} se a empresa nunca
     * configurou um limite (semântica permissiva, ver Javadoc da classe). */
    @Transactional(readOnly = true)
    BigDecimal limiteAprovacaoDaEmpresa(Long empresaId) {
        return repository.findByEmpresaId(empresaId)
                .map(ConfiguracaoComprasEmpresa::getLimiteAprovacaoPedido)
                .orElse(null);
    }
}
