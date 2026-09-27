package br.com.cactusdigital.erp.vendas.service;

import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.vendas.dto.VendedorRequest;
import br.com.cactusdigital.erp.vendas.dto.VendedorResponse;
import br.com.cactusdigital.erp.vendas.entity.ComissaoPerfil;
import br.com.cactusdigital.erp.vendas.entity.Vendedor;
import br.com.cactusdigital.erp.vendas.exception.VendasErrorCode;
import br.com.cactusdigital.erp.vendas.mapper.VendasMapper;
import br.com.cactusdigital.erp.vendas.repository.VendedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Vendedor — fecha o enriquecimento registrado como pendente na ANÁLISE: HIERARQUIA DE
 * USUÁRIOS do CLAUDE.md (ver Javadoc de {@link Vendedor}). {@code colaboradorId} é
 * referência SOLTA pro cadastro-base-service (sem validação de existência na v1).
 */
@Service
@RequiredArgsConstructor
public class VendedorService {

    private final VendedorRepository repository;
    private final ComissaoPerfilService comissaoPerfilService;
    private final VendasMapper mapper;

    @Transactional(readOnly = true)
    public Page<VendedorResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public VendedorResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public VendedorResponse criar(VendedorRequest request) {
        if (repository.existsByColaboradorId(request.colaboradorId())) {
            throw new ConflictException(VendasErrorCode.VENDEDOR_JA_CADASTRADO_PARA_COLABORADOR);
        }
        Vendedor vendedor = Vendedor.builder()
                .colaboradorId(request.colaboradorId())
                .comissaoPerfil(resolverComissaoPerfil(request.comissaoPerfilId()))
                .comissaoPercentual(request.comissaoPercentual())
                .metaVenda(request.metaVenda())
                .situacao(request.situacao())
                .build();
        return mapper.toResponse(repository.save(vendedor));
    }

    @Transactional
    public VendedorResponse atualizar(Long id, VendedorRequest request) {
        Vendedor vendedor = buscarEntidade(id);
        if (!vendedor.getColaboradorId().equals(request.colaboradorId()) && repository.existsByColaboradorId(request.colaboradorId())) {
            throw new ConflictException(VendasErrorCode.VENDEDOR_JA_CADASTRADO_PARA_COLABORADOR);
        }
        vendedor.setColaboradorId(request.colaboradorId());
        vendedor.setComissaoPerfil(resolverComissaoPerfil(request.comissaoPerfilId()));
        vendedor.setComissaoPercentual(request.comissaoPercentual());
        vendedor.setMetaVenda(request.metaVenda());
        vendedor.setSituacao(request.situacao());
        return mapper.toResponse(vendedor);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private ComissaoPerfil resolverComissaoPerfil(Long comissaoPerfilId) {
        return comissaoPerfilId != null ? comissaoPerfilService.buscarEntidade(comissaoPerfilId) : null;
    }

    Vendedor buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Vendedor", id));
    }
}
