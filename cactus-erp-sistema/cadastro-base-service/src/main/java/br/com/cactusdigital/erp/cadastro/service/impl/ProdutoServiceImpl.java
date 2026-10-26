package br.com.cactusdigital.erp.cadastro.service.impl;

import br.com.cactusdigital.erp.cadastro.dto.ProdutoFaixaPrecoDTO;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoImagemDTO;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoRequest;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoResponse;
import br.com.cactusdigital.erp.cadastro.dto.RecalcularTributacaoPadraoRequest;
import br.com.cactusdigital.erp.cadastro.dto.ResolverTributacaoRequest;
import br.com.cactusdigital.erp.cadastro.dto.ResolverTributacaoResponse;
import br.com.cactusdigital.erp.cadastro.entity.Cest;
import br.com.cactusdigital.erp.cadastro.entity.GrupoTributario;
import br.com.cactusdigital.erp.cadastro.entity.Ncm;
import br.com.cactusdigital.erp.cadastro.entity.Produto;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoFaixaPreco;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoImagem;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoMarca;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoSubgrupo;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoTipo;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoUnidade;
import br.com.cactusdigital.erp.cadastro.mapper.ProdutoMapper;
import br.com.cactusdigital.erp.cadastro.repository.CestRepository;
import br.com.cactusdigital.erp.cadastro.repository.GrupoTributarioRepository;
import br.com.cactusdigital.erp.cadastro.repository.NcmRepository;
import br.com.cactusdigital.erp.cadastro.repository.ProdutoMarcaRepository;
import br.com.cactusdigital.erp.cadastro.repository.ProdutoRepository;
import br.com.cactusdigital.erp.cadastro.repository.ProdutoSubgrupoRepository;
import br.com.cactusdigital.erp.cadastro.repository.ProdutoTipoRepository;
import br.com.cactusdigital.erp.cadastro.repository.ProdutoUnidadeRepository;
import br.com.cactusdigital.erp.cadastro.service.ProdutoService;
import br.com.cactusdigital.erp.cadastro.service.TributacaoResolucaoService;
import br.com.cactusdigital.erp.cadastro.specification.ProdutoSpecifications;
import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoUnidadeRepository produtoUnidadeRepository;
    private final ProdutoMarcaRepository produtoMarcaRepository;
    private final ProdutoSubgrupoRepository produtoSubgrupoRepository;
    private final ProdutoTipoRepository produtoTipoRepository;
    private final NcmRepository ncmRepository;
    private final CestRepository cestRepository;
    private final GrupoTributarioRepository grupoTributarioRepository;
    private final TributacaoResolucaoService tributacaoResolucaoService;
    private final ProdutoMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProdutoResponse> listar(String nome, String codigoInterno, String gtin, Long produtoSubgrupoId,
            Long produtoMarcaId, Situacao situacao, Pageable pageable) {
        return listar(nome, codigoInterno, gtin, produtoSubgrupoId, produtoMarcaId, situacao, null, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProdutoResponse> listar(String nome, String codigoInterno, String gtin, Long produtoSubgrupoId,
            Long produtoMarcaId, Situacao situacao, Instant desde, Pageable pageable) {
        var specification = ProdutoSpecifications.comFiltros(nome, codigoInterno, gtin, produtoSubgrupoId, produtoMarcaId, situacao, desde);
        Page<Produto> pagina = produtoRepository.findAll(specification, pageable);
        hidratarColecoes(pagina);
        return pagina.map(mapper::toResponse);
    }

    /** Evita N+1 nas 2 coleções (`faixasPreco`/`imagens`) que {@link ProdutoResponse}
     * expõe — ver Javadoc de {@code ProdutoRepository#hidratarFaixasPreco}. */
    private void hidratarColecoes(Page<Produto> pagina) {
        if (pagina.isEmpty()) {
            return;
        }
        List<Long> ids = pagina.getContent().stream().map(Produto::getId).toList();
        produtoRepository.hidratarFaixasPreco(ids);
        produtoRepository.hidratarImagens(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Override
    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        Produto produto = Produto.builder()
                .produtoUnidade(buscarUnidade(request.produtoUnidadeId()))
                .produtoMarca(request.produtoMarcaId() != null ? buscarMarca(request.produtoMarcaId()) : null)
                .produtoSubgrupo(buscarSubgrupo(request.produtoSubgrupoId()))
                .produtoTipo(request.produtoTipoId() != null ? buscarTipo(request.produtoTipoId()) : null)
                .gtin(request.gtin())
                .codigoInterno(request.codigoInterno())
                .nome(request.nome())
                .descricao(request.descricao())
                .descricaoPdv(request.descricaoPdv())
                .ncm(buscarNcm(request.ncmId()))
                .cest(request.cestId() != null ? buscarCest(request.cestId()) : null)
                .grupoTributario(buscarGrupoTributario(request.grupoTributarioId()))
                .tipoItemSped(request.tipoItemSped())
                .iat(request.iat())
                .ippt(request.ippt())
                .totalizadorParcial(request.totalizadorParcial())
                .codigoBalanca(request.codigoBalanca())
                .pafPSt(request.pafPSt())
                .valorCusto(request.valorCusto())
                .valorCompra(request.valorCompra())
                .valorVenda(request.valorVenda())
                .estoqueMinimo(request.estoqueMinimo())
                .estoqueMaximo(request.estoqueMaximo())
                .situacao(request.situacao())
                .build();

        aplicarFaixasPreco(produto, request.faixasPreco());
        aplicarImagens(produto, request.imagens());

        return mapper.toResponse(produtoRepository.save(produto));
    }

    @Override
    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = buscarEntidade(id);
        produto.setProdutoUnidade(buscarUnidade(request.produtoUnidadeId()));
        produto.setProdutoMarca(request.produtoMarcaId() != null ? buscarMarca(request.produtoMarcaId()) : null);
        produto.setProdutoSubgrupo(buscarSubgrupo(request.produtoSubgrupoId()));
        produto.setProdutoTipo(request.produtoTipoId() != null ? buscarTipo(request.produtoTipoId()) : null);
        produto.setGtin(request.gtin());
        produto.setCodigoInterno(request.codigoInterno());
        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setDescricaoPdv(request.descricaoPdv());
        produto.setNcm(buscarNcm(request.ncmId()));
        produto.setCest(request.cestId() != null ? buscarCest(request.cestId()) : null);
        produto.setGrupoTributario(buscarGrupoTributario(request.grupoTributarioId()));
        produto.setTipoItemSped(request.tipoItemSped());
        produto.setIat(request.iat());
        produto.setIppt(request.ippt());
        produto.setTotalizadorParcial(request.totalizadorParcial());
        produto.setCodigoBalanca(request.codigoBalanca());
        produto.setPafPSt(request.pafPSt());
        produto.setValorCusto(request.valorCusto());
        produto.setValorCompra(request.valorCompra());
        produto.setValorVenda(request.valorVenda());
        produto.setEstoqueMinimo(request.estoqueMinimo());
        produto.setEstoqueMaximo(request.estoqueMaximo());
        produto.setSituacao(request.situacao());

        aplicarFaixasPreco(produto, request.faixasPreco());
        aplicarImagens(produto, request.imagens());

        return mapper.toResponse(produto);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        produtoRepository.delete(buscarEntidade(id));
    }

    @Override
    @Transactional
    public ProdutoResponse recalcularTributacaoPadrao(Long id, RecalcularTributacaoPadraoRequest request) {
        Produto produto = buscarEntidade(id);

        ResolverTributacaoResponse resolvido = tributacaoResolucaoService.resolver(new ResolverTributacaoRequest(
                produto.getGrupoTributario().getId(), request.operacaoFiscalId(), request.ufDestino()));

        produto.setCstIcmsPadrao(resolvido.icms() != null ? resolvido.icms().cstIcms() : null);
        produto.setCsosnPadrao(resolvido.icms() != null ? resolvido.icms().csosn() : null);
        produto.setAliquotaIcmsPadrao(resolvido.icms() != null ? resolvido.icms().aliquota() : null);
        produto.setAliquotaIpiPadrao(resolvido.ipi() != null ? resolvido.ipi().aliquotaPercentual() : null);
        produto.setAliquotaPisPadrao(resolvido.pis() != null ? resolvido.pis().aliquotaPercentual() : null);
        produto.setAliquotaCofinsPadrao(resolvido.cofins() != null ? resolvido.cofins().aliquotaPercentual() : null);
        produto.setAliquotaIssqnPadrao(resolvido.iss() != null ? resolvido.iss().aliquotaPercentual() : null);
        produto.setCstIbsCbsPadrao(resolvido.ibsCbs() != null ? resolvido.ibsCbs().cst() : null);
        produto.setClassificacaoIbsCbsPadrao(resolvido.ibsCbs() != null ? resolvido.ibsCbs().classificacaoTributaria() : null);
        produto.setDataResolucaoPadrao(Instant.now());

        return mapper.toResponse(produto);
    }

    /** Mesmo padrão "clear e reconstrói" do {@link br.com.cactusdigital.erp.cadastro.service.PessoaAssembler}. */
    private void aplicarFaixasPreco(Produto produto, List<ProdutoFaixaPrecoDTO> faixasPreco) {
        produto.getFaixasPreco().clear();
        for (ProdutoFaixaPrecoDTO dto : faixasPreco != null ? faixasPreco : List.<ProdutoFaixaPrecoDTO>of()) {
            ProdutoFaixaPreco faixa = mapper.toEntity(dto);
            faixa.setProduto(produto);
            produto.getFaixasPreco().add(faixa);
        }
    }

    private void aplicarImagens(Produto produto, List<ProdutoImagemDTO> imagens) {
        produto.getImagens().clear();
        for (ProdutoImagemDTO dto : imagens != null ? imagens : List.<ProdutoImagemDTO>of()) {
            ProdutoImagem imagem = mapper.toEntity(dto);
            imagem.setProduto(produto);
            produto.getImagens().add(imagem);
        }
    }

    private Produto buscarEntidade(Long id) {
        return produtoRepository.findById(id).orElseThrow(() -> NotFoundException.of("Produto", id));
    }

    private ProdutoUnidade buscarUnidade(Long id) {
        return produtoUnidadeRepository.findById(id).orElseThrow(() -> NotFoundException.of("Unidade de produto", id));
    }

    private ProdutoMarca buscarMarca(Long id) {
        return produtoMarcaRepository.findById(id).orElseThrow(() -> NotFoundException.of("Marca de produto", id));
    }

    private ProdutoSubgrupo buscarSubgrupo(Long id) {
        return produtoSubgrupoRepository.findById(id).orElseThrow(() -> NotFoundException.of("Subgrupo de produto", id));
    }

    private ProdutoTipo buscarTipo(Long id) {
        return produtoTipoRepository.findById(id).orElseThrow(() -> NotFoundException.of("Tipo de produto", id));
    }

    private Ncm buscarNcm(Long id) {
        return ncmRepository.findById(id).orElseThrow(() -> NotFoundException.of("NCM", id));
    }

    private Cest buscarCest(Long id) {
        return cestRepository.findById(id).orElseThrow(() -> NotFoundException.of("CEST", id));
    }

    private GrupoTributario buscarGrupoTributario(Long id) {
        return grupoTributarioRepository.findById(id).orElseThrow(() -> NotFoundException.of("Grupo tributário", id));
    }
}
