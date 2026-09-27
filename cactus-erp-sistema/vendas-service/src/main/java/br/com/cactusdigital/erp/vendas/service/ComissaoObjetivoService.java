package br.com.cactusdigital.erp.vendas.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.vendas.dto.ComissaoObjetivoRequest;
import br.com.cactusdigital.erp.vendas.dto.ComissaoObjetivoResponse;
import br.com.cactusdigital.erp.vendas.entity.ComissaoObjetivo;
import br.com.cactusdigital.erp.vendas.entity.ComissaoPerfil;
import br.com.cactusdigital.erp.vendas.mapper.VendasMapper;
import br.com.cactusdigital.erp.vendas.repository.ComissaoObjetivoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Meta de comissão por produto dentro de um {@link ComissaoPerfil} — sub-recurso, sem listagem paginada própria (mesmo padrão de {@code PedidoCompraItem}). */
@Service
@RequiredArgsConstructor
public class ComissaoObjetivoService {

    private final ComissaoObjetivoRepository repository;
    private final ComissaoPerfilService comissaoPerfilService;
    private final VendasMapper mapper;

    @Transactional(readOnly = true)
    public List<ComissaoObjetivoResponse> listarPorPerfil(Long comissaoPerfilId) {
        return repository.findByComissaoPerfilId(comissaoPerfilId).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public ComissaoObjetivoResponse adicionar(Long comissaoPerfilId, ComissaoObjetivoRequest request) {
        ComissaoPerfil perfil = comissaoPerfilService.buscarEntidade(comissaoPerfilId);
        ComissaoObjetivo objetivo = ComissaoObjetivo.builder()
                .comissaoPerfil(perfil)
                .produtoId(request.produtoId())
                .codigo(request.codigo())
                .nome(request.nome())
                .descricao(request.descricao())
                .formaPagamento(request.formaPagamento())
                .taxaPagamento(request.taxaPagamento())
                .valorPagamento(request.valorPagamento())
                .valorMeta(request.valorMeta())
                .quantidade(request.quantidade())
                .build();
        return mapper.toResponse(repository.save(objetivo));
    }

    @Transactional
    public void remover(Long comissaoPerfilId, Long objetivoId) {
        ComissaoObjetivo objetivo = buscarEntidade(comissaoPerfilId, objetivoId);
        repository.delete(objetivo);
    }

    private ComissaoObjetivo buscarEntidade(Long comissaoPerfilId, Long objetivoId) {
        return repository.findById(objetivoId)
                .filter(candidato -> candidato.getComissaoPerfil().getId().equals(comissaoPerfilId))
                .orElseThrow(() -> NotFoundException.of("Objetivo de comissão", objetivoId));
    }
}
