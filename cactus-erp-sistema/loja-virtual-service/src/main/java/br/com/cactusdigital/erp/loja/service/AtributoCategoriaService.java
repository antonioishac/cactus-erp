package br.com.cactusdigital.erp.loja.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.loja.dto.AtributoCategoriaOpcaoRequest;
import br.com.cactusdigital.erp.loja.dto.AtributoCategoriaOpcaoResponse;
import br.com.cactusdigital.erp.loja.dto.AtributoCategoriaRequest;
import br.com.cactusdigital.erp.loja.dto.AtributoCategoriaResponse;
import br.com.cactusdigital.erp.loja.entity.AtributoCategoria;
import br.com.cactusdigital.erp.loja.entity.AtributoCategoriaOpcao;
import br.com.cactusdigital.erp.loja.mapper.LojaVirtualMapper;
import br.com.cactusdigital.erp.loja.repository.AtributoCategoriaOpcaoRepository;
import br.com.cactusdigital.erp.loja.repository.AtributoCategoriaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Schema de atributo dinâmico por categoria — ver Javadoc de {@link AtributoCategoria}.
 * {@code opcoes} (só relevantes pra {@code TipoAtributo.LISTA}) são geridas por
 * sub-recurso (POST/DELETE), não substituídas em bloco no {@code PUT} do atributo — mesmo
 * critério de sub-recurso já usado em toda entidade agregada do monorepo.
 */
@Service
@RequiredArgsConstructor
public class AtributoCategoriaService {

    private final AtributoCategoriaRepository repository;
    private final AtributoCategoriaOpcaoRepository opcaoRepository;
    private final CategoriaLojaService categoriaLojaService;
    private final LojaVirtualMapper mapper;

    @Transactional(readOnly = true)
    public Page<AtributoCategoriaResponse> listar(Long categoriaLojaId, Pageable pageable) {
        Page<AtributoCategoria> pagina = categoriaLojaId != null
                ? repository.findByCategoriaLoja_Id(categoriaLojaId, pageable)
                : repository.findAll(pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, AtributoCategoria::getId, repository::buscarComOpcoes, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public AtributoCategoriaResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    /** Lista de {@code AtributoCategoria} com {@code geraVariacao=true} de uma categoria — usado por {@code VarianteProdutoLojaService} pra validar a composição do SKU. */
    @Transactional(readOnly = true)
    List<AtributoCategoria> listarQueGeramVariacao(Long categoriaLojaId) {
        return repository.findByCategoriaLoja_IdOrderByOrdemAsc(categoriaLojaId).stream()
                .filter(AtributoCategoria::isGeraVariacao)
                .toList();
    }

    @Transactional
    public AtributoCategoriaResponse criar(AtributoCategoriaRequest request) {
        AtributoCategoria atributo = mapper.toEntity(request);
        atributo.setCategoriaLoja(categoriaLojaService.buscarEntidade(request.categoriaLojaId()));
        // Achado real (2026-10-11): mapper.toEntity() sobrescreve o @Builder.Default com null quando
        // request.ordem() vem ausente — mesmo bug já corrigido em CategoriaLojaService#criar, só que
        // esquecido aqui. Guard idêntico ao de lá, não um @Mapping(defaultValue) no mapper, pra
        // manter o mesmo padrão já em uso neste módulo.
        if (request.ordem() == null) {
            atributo.setOrdem(0);
        }
        if (request.opcoes() != null) {
            request.opcoes().forEach(o -> atributo.getOpcoes().add(
                    AtributoCategoriaOpcao.builder().atributoCategoria(atributo).valor(o.valor()).ordem(o.ordem() != null ? o.ordem() : 0).build()));
        }
        return mapper.toResponse(repository.save(atributo));
    }

    @Transactional
    public AtributoCategoriaResponse atualizar(Long id, AtributoCategoriaRequest request) {
        AtributoCategoria atributo = buscarEntidade(id);
        atributo.setNome(request.nome());
        atributo.setTipo(request.tipo());
        atributo.setObrigatorio(request.obrigatorio());
        atributo.setGeraVariacao(request.geraVariacao());
        if (request.ordem() != null) {
            atributo.setOrdem(request.ordem());
        }
        return mapper.toResponse(atributo);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    @Transactional
    public AtributoCategoriaOpcaoResponse adicionarOpcao(Long atributoId, AtributoCategoriaOpcaoRequest request) {
        AtributoCategoria atributo = buscarEntidade(atributoId);
        AtributoCategoriaOpcao opcao = opcaoRepository.save(AtributoCategoriaOpcao.builder()
                .atributoCategoria(atributo)
                .valor(request.valor())
                .ordem(request.ordem() != null ? request.ordem() : 0)
                .build());
        atributo.getOpcoes().add(opcao);
        return mapper.toResponse(opcao);
    }

    @Transactional
    public void removerOpcao(Long atributoId, Long opcaoId) {
        AtributoCategoriaOpcao opcao = opcaoRepository.findById(opcaoId)
                .filter(candidata -> candidata.getAtributoCategoria().getId().equals(atributoId))
                .orElseThrow(() -> NotFoundException.of("Opção do atributo", opcaoId));
        opcaoRepository.delete(opcao);
    }

    AtributoCategoria buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Atributo da categoria", id));
    }
}
