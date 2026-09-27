package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.TabelaPrecoProdutoDTO;
import br.com.cactusdigital.erp.cadastro.dto.TabelaPrecoRequest;
import br.com.cactusdigital.erp.cadastro.dto.TabelaPrecoResponse;
import br.com.cactusdigital.erp.cadastro.entity.Produto;
import br.com.cactusdigital.erp.cadastro.entity.TabelaPreco;
import br.com.cactusdigital.erp.cadastro.entity.TabelaPrecoProduto;
import br.com.cactusdigital.erp.cadastro.mapper.TabelaPrecoMapper;
import br.com.cactusdigital.erp.cadastro.repository.ProdutoRepository;
import br.com.cactusdigital.erp.cadastro.repository.TabelaPrecoRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * CRUD de {@link TabelaPreco} — mecanismo de precificação por canal/perfil, complementar
 * a {@code ProdutoFaixaPreco} (ver Javadoc de {@link TabelaPreco}). Lista de produtos
 * gerenciada como agregado (clear e reconstrói), mesmo padrão usado em
 * {@code ConfiguracaoTributariaService}/{@code PessoaAssembler} pras outras coleções
 * com {@code orphanRemoval=true}.
 */
@Service
@RequiredArgsConstructor
public class TabelaPrecoService {

    private final TabelaPrecoRepository repository;
    private final ProdutoRepository produtoRepository;
    private final TabelaPrecoMapper mapper;

    @Transactional(readOnly = true)
    public Page<TabelaPrecoResponse> listar(String nome, Pageable pageable) {
        Page<TabelaPreco> pagina = StringUtils.hasText(nome)
                ? repository.findByNomeContainingIgnoreCase(nome, pageable)
                : repository.findAll(pageable);
        if (!pagina.isEmpty()) {
            repository.hidratarProdutos(pagina.getContent().stream().map(TabelaPreco::getId).toList());
        }
        return pagina.map(mapper::toResponse);
    }

    /** {@code desde} = cursor de sincronização incremental, ver {@code TabelaPrecoRepository#buscarDesde}. */
    @Transactional(readOnly = true)
    public Page<TabelaPrecoResponse> listar(Instant desde, Pageable pageable) {
        Page<TabelaPreco> pagina = repository.buscarDesde(desde, pageable);
        if (!pagina.isEmpty()) {
            repository.hidratarProdutos(pagina.getContent().stream().map(TabelaPreco::getId).toList());
        }
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TabelaPrecoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidadeComProdutos(id));
    }

    @Transactional
    public TabelaPrecoResponse criar(TabelaPrecoRequest request) {
        TabelaPreco entidade = mapper.toEntity(request);
        aplicarProdutos(entidade, request.produtos());
        return mapper.toResponse(repository.save(entidade));
    }

    @Transactional
    public TabelaPrecoResponse atualizar(Long id, TabelaPrecoRequest request) {
        TabelaPreco entidade = buscarEntidadeComProdutos(id);
        mapper.atualizar(request, entidade);
        aplicarProdutos(entidade, request.produtos());
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private void aplicarProdutos(TabelaPreco entidade, List<TabelaPrecoProdutoDTO> produtos) {
        entidade.getProdutos().clear();
        for (TabelaPrecoProdutoDTO dto : produtos != null ? produtos : List.<TabelaPrecoProdutoDTO>of()) {
            TabelaPrecoProduto item = mapper.toEntity(dto);
            item.setTabelaPreco(entidade);
            item.setProduto(buscarProduto(dto.produtoId()));
            entidade.getProdutos().add(item);
        }
    }

    private TabelaPreco buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Tabela de preço", id));
    }

    private TabelaPreco buscarEntidadeComProdutos(Long id) {
        return repository.buscarComProdutosPorId(id).orElseThrow(() -> NotFoundException.of("Tabela de preço", id));
    }

    private Produto buscarProduto(Long id) {
        return produtoRepository.findById(id).orElseThrow(() -> NotFoundException.of("Produto", id));
    }
}
