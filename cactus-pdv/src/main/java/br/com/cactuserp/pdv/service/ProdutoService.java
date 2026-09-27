package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.Produto;
import br.com.cactuserp.pdv.entity.ProdutoUnidade;
import br.com.cactuserp.pdv.repository.ProdutoRepository;
import br.com.cactuserp.pdv.repository.ProdutoUnidadeRepository;
import br.com.cactuserp.pdv.service.dto.PesquisarProdutoDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoUnidadeRepository produtoUnidadeRepository;

    public ProdutoService(ProdutoRepository produtoRepository,
                          ProdutoUnidadeRepository produtoUnidadeRepository) {
        this.produtoRepository = produtoRepository;
        this.produtoUnidadeRepository = produtoUnidadeRepository;
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> buscarPorCodigo(String codigo) {
        return produtoRepository.findByGtin(codigo);
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public Optional<Produto> buscarPorCodigoBalanca(Integer codigo) {
        return produtoRepository.findByCodigoBalanca(codigo);
    }

    public List<Produto> buscarPorDescricao(String descricao) {
        return produtoRepository.buscarPorDescricao(descricao);
    }

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }

    public boolean existePorCodigo(String codigo) {
        return produtoRepository.existsByGtin(codigo);
    }

    /**
     * Monta a listagem da tela de pesquisa (F11, {@code PesquisaProdutoController})
     * — todo produto ativo se {@code filtroDescricao} vier vazio, ou filtrado
     * por descrição. <b>Ponto único</b> de resolução do "código de volta pro
     * PDV" (GTIN, senão código de balança) — antes essa lógica estava
     * duplicada e inconsistente entre a carga inicial e a busca em si, o que
     * fazia produtos vendidos por peso (sem GTIN) ficarem sem código
     * selecionável assim que o operador digitava algo pra filtrar.
     *
     * <p>Produto sem código nenhum (GTIN e balança nulos) continua na
     * lista com {@code codigo=null} — quem decide o que fazer com isso é
     * {@code PesquisaProdutoController}/{@code Pdv4Controller}: se
     * {@link PesquisarProdutoDTO#isVendidoPorPeso()}, dá pra selecionar
     * mesmo assim (pesa ao vivo, resolve pelo {@link PesquisarProdutoDTO#getId()}).
     */
    public List<PesquisarProdutoDTO> paraPesquisa(String filtroDescricao) {
        List<Produto> produtos = (filtroDescricao == null || filtroDescricao.isBlank())
                ? produtoRepository.findAll()
                : produtoRepository.buscarPorDescricao(filtroDescricao);
        return produtos.stream().map(this::paraDto).toList();
    }

    private PesquisarProdutoDTO paraDto(Produto produto) {
        String codigo = produto.getGtin() != null && !produto.getGtin().isBlank()
                ? produto.getGtin()
                : (produto.getCodigoBalanca() != null ? String.valueOf(produto.getCodigoBalanca()) : null);

        String descricao = produto.getDescricaoPdv() != null && !produto.getDescricaoPdv().isBlank()
                ? produto.getDescricaoPdv()
                : produto.getNome();

        ProdutoUnidade unidade = produto.getProdutoUnidade() != null
                ? produtoUnidadeRepository.findById(produto.getProdutoUnidade()).orElse(null)
                : null;

        return new PesquisarProdutoDTO(produto.getId(), codigo, descricao,
                unidade != null ? unidade.getSigla() : null,
                produto.getValorVenda(),
                unidade != null && unidade.ehUnidadeDePeso());
    }
}