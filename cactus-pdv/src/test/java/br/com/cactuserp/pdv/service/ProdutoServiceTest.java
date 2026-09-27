package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.Produto;
import br.com.cactuserp.pdv.entity.ProdutoUnidade;
import br.com.cactuserp.pdv.entity.SimNao;
import br.com.cactuserp.pdv.repository.ProdutoRepository;
import br.com.cactuserp.pdv.repository.ProdutoUnidadeRepository;
import br.com.cactuserp.pdv.service.dto.PesquisarProdutoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Regressão do bug relatado ao testar (2026-08-23): produto vendido por
 * peso sem GTIN nem código de balança (ex: "Banana Nanica", cadastro real
 * usado no teste manual) ficava com código {@code null}/vazio assim que o
 * operador filtrava a busca por nome. {@link ProdutoService#paraPesquisa}
 * é o ponto único que decide o código de volta — {@code codigo=null} +
 * {@code vendidoPorPeso=true} é o sinal que {@code PesquisaProdutoController}/
 * {@code Pdv4Controller} usam pra saber que dá pra selecionar mesmo assim
 * (pesa ao vivo em vez de precisar de um código).
 */
class ProdutoServiceTest {

    private ProdutoRepository produtoRepository;
    private ProdutoUnidadeRepository produtoUnidadeRepository;
    private ProdutoService service;

    @BeforeEach
    void setUp() {
        produtoRepository = mock(ProdutoRepository.class);
        produtoUnidadeRepository = mock(ProdutoUnidadeRepository.class);
        service = new ProdutoService(produtoRepository, produtoUnidadeRepository);
    }

    private Produto banana() {
        Produto produto = new Produto();
        produto.setId(19L);
        produto.setNome("Banana Nanica");
        produto.setDescricaoPdv("BANANA NANICA");
        produto.setGtin(null);
        produto.setCodigoBalanca(null);
        produto.setProdutoUnidade(2L);
        produto.setValorVenda(new BigDecimal("5.99"));
        return produto;
    }

    private ProdutoUnidade kg() {
        ProdutoUnidade unidade = new ProdutoUnidade();
        unidade.setSigla("KG");
        unidade.setPodeFracionar(SimNao.S);
        return unidade;
    }

    @Test
    @DisplayName("produto sem GTIN e sem código de balança: código vem null, mas continua listado")
    void produtoSemCodigoContinuaListado() {
        when(produtoRepository.buscarPorDescricao("banana")).thenReturn(List.of(banana()));
        when(produtoUnidadeRepository.findById(2L)).thenReturn(Optional.of(kg()));

        List<PesquisarProdutoDTO> resultado = service.paraPesquisa("banana");

        assertThat(resultado).hasSize(1);
        PesquisarProdutoDTO dto = resultado.get(0);
        assertThat(dto.getCodigo()).isNull();
        assertThat(dto.semCodigoResolvivel()).isTrue();
        assertThat(dto.getId()).isEqualTo(19L);
        assertThat(dto.isVendidoPorPeso()).isTrue();
        assertThat(dto.getUnidade()).isEqualTo("KG");
    }

    @Test
    @DisplayName("carga inicial (filtro null) e busca por nome usam a MESMA resolução de código")
    void cargaInicialEBuscaConsistentes() {
        when(produtoRepository.findAll()).thenReturn(List.of(banana()));
        when(produtoRepository.buscarPorDescricao("banana")).thenReturn(List.of(banana()));
        when(produtoUnidadeRepository.findById(2L)).thenReturn(Optional.of(kg()));

        PesquisarProdutoDTO daCargaInicial = service.paraPesquisa(null).get(0);
        PesquisarProdutoDTO daBusca = service.paraPesquisa("banana").get(0);

        assertThat(daCargaInicial.getCodigo()).isEqualTo(daBusca.getCodigo());
        assertThat(daCargaInicial.isVendidoPorPeso()).isEqualTo(daBusca.isVendidoPorPeso());
    }

    @Test
    @DisplayName("produto com GTIN e não vendido por peso: código presente, semCodigoResolvivel=false")
    void produtoNormalTemCodigoResolvivel() {
        Produto refrigerante = new Produto();
        refrigerante.setId(30L);
        refrigerante.setNome("Refrigerante Lata");
        refrigerante.setGtin("7891234567890");
        refrigerante.setProdutoUnidade(1L);
        refrigerante.setValorVenda(new BigDecimal("4.50"));

        ProdutoUnidade un = new ProdutoUnidade();
        un.setSigla("UN");
        un.setPodeFracionar(SimNao.N);

        when(produtoRepository.buscarPorDescricao("refri")).thenReturn(List.of(refrigerante));
        when(produtoUnidadeRepository.findById(1L)).thenReturn(Optional.of(un));

        PesquisarProdutoDTO dto = service.paraPesquisa("refri").get(0);

        assertThat(dto.getCodigo()).isEqualTo("7891234567890");
        assertThat(dto.semCodigoResolvivel()).isFalse();
        assertThat(dto.isVendidoPorPeso()).isFalse();
    }

    @Test
    @DisplayName("produto vendido por litro (LT): fracionável mas NÃO é vendidoPorPeso (bug real corrigido)")
    void produtoVendidoPorLitroNaoEhVendidoPorPeso() {
        Produto oleo = new Produto();
        oleo.setId(40L);
        oleo.setNome("Óleo de Soja a Granel");
        oleo.setGtin("7899999999999");
        oleo.setProdutoUnidade(3L);
        oleo.setValorVenda(new BigDecimal("8.90"));

        ProdutoUnidade lt = new ProdutoUnidade();
        lt.setSigla("LT");
        lt.setPodeFracionar(SimNao.S); // fracionável — mas isso NÃO significa "pesável"

        when(produtoRepository.buscarPorDescricao("oleo")).thenReturn(List.of(oleo));
        when(produtoUnidadeRepository.findById(3L)).thenReturn(Optional.of(lt));

        PesquisarProdutoDTO dto = service.paraPesquisa("oleo").get(0);

        assertThat(dto.getUnidade()).isEqualTo("LT");
        assertThat(dto.isVendidoPorPeso()).isFalse();
    }

    @Test
    @DisplayName("mistura produto pesado sem código e produto normal com código na mesma busca")
    void mixDeProdutosNaMesmaBusca() {
        Produto refrigerante = new Produto();
        refrigerante.setId(30L);
        refrigerante.setNome("Refrigerante Lata");
        refrigerante.setGtin("7891234567890");
        refrigerante.setProdutoUnidade(1L);
        refrigerante.setValorVenda(new BigDecimal("4.50"));

        ProdutoUnidade un = new ProdutoUnidade();
        un.setSigla("UN");
        un.setPodeFracionar(SimNao.N);

        when(produtoRepository.buscarPorDescricao("a")).thenReturn(List.of(banana(), refrigerante));
        when(produtoUnidadeRepository.findById(2L)).thenReturn(Optional.of(kg()));
        when(produtoUnidadeRepository.findById(1L)).thenReturn(Optional.of(un));

        List<PesquisarProdutoDTO> resultado = service.paraPesquisa("a");

        assertThat(resultado).hasSize(2);
        assertThat(resultado).anySatisfy(dto -> {
            assertThat(dto.getId()).isEqualTo(19L);
            assertThat(dto.semCodigoResolvivel()).isTrue();
            assertThat(dto.isVendidoPorPeso()).isTrue();
        });
        assertThat(resultado).anySatisfy(dto -> {
            assertThat(dto.getId()).isEqualTo(30L);
            assertThat(dto.semCodigoResolvivel()).isFalse();
            assertThat(dto.isVendidoPorPeso()).isFalse();
        });
    }
}
