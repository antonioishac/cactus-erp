package br.com.cactusdigital.erp.estoque.service;

import br.com.cactusdigital.erp.estoque.dto.ConfiguracaoEstoqueEmpresaRequest;
import br.com.cactusdigital.erp.estoque.dto.ConfiguracaoEstoqueEmpresaResponse;
import br.com.cactusdigital.erp.estoque.entity.ConfiguracaoEstoqueEmpresa;
import br.com.cactusdigital.erp.estoque.entity.MetodoCusteio;
import br.com.cactusdigital.erp.estoque.mapper.EstoqueMapper;
import br.com.cactusdigital.erp.estoque.repository.ConfiguracaoEstoqueEmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Get-or-create por empresa: se a empresa nunca configurou nada, devolve um default em
 * memória (nunca persistido) em vez de 404 — toda empresa "tem" uma configuração de
 * estoque, mesmo que nunca tenha sido explicitamente salva.
 * <p>
 * {@code limiteAprovacaoAjuste=null} no default significa "sem limite configurado, todo
 * AJUSTE/PERDA passa sem aprovador" — mesma semântica que
 * {@code EstoqueMovimentoService#verificarAprovacaoSeNecessario} já assume quando não
 * encontra registro pra empresa (permissivo por padrão: quem não configurou nada não
 * fica bloqueado por engano). Nunca {@code BigDecimal.ZERO} aqui — isso sugeriria o
 * oposto (qualquer ajuste de valor positivo exigindo aprovador).
 */
@Service
@RequiredArgsConstructor
public class ConfiguracaoEstoqueEmpresaService {

    private final ConfiguracaoEstoqueEmpresaRepository repository;
    private final EstoqueMapper mapper;

    @Transactional(readOnly = true)
    public ConfiguracaoEstoqueEmpresaResponse buscarPorEmpresa(Long empresaId) {
        return repository.findByEmpresaId(empresaId)
                .map(mapper::toResponse)
                .orElseGet(() -> new ConfiguracaoEstoqueEmpresaResponse(null, empresaId, MetodoCusteio.MEDIA_PONDERADA, null));
    }

    @Transactional
    public ConfiguracaoEstoqueEmpresaResponse atualizar(Long empresaId, ConfiguracaoEstoqueEmpresaRequest request) {
        ConfiguracaoEstoqueEmpresa configuracao = repository.findByEmpresaId(empresaId)
                .orElseGet(() -> ConfiguracaoEstoqueEmpresa.builder().empresaId(empresaId).build());
        configuracao.setMetodoCusteio(request.metodoCusteio());
        configuracao.setLimiteAprovacaoAjuste(request.limiteAprovacaoAjuste());
        return mapper.toResponse(repository.save(configuracao));
    }
}
