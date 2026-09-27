package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.ProdutoRequest;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoResponse;
import br.com.cactusdigital.erp.cadastro.dto.RecalcularTributacaoPadraoRequest;
import br.com.cactusdigital.erp.common.domain.Situacao;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProdutoService {

    Page<ProdutoResponse> listar(String nome, String codigoInterno, String gtin, Long produtoSubgrupoId,
            Long produtoMarcaId, Situacao situacao, Pageable pageable);

    /** {@code desde} = cursor de sincronização incremental, ver {@code ProdutoSpecifications}. */
    Page<ProdutoResponse> listar(String nome, String codigoInterno, String gtin, Long produtoSubgrupoId,
            Long produtoMarcaId, Situacao situacao, Instant desde, Pageable pageable);

    ProdutoResponse buscarPorId(Long id);

    ProdutoResponse criar(ProdutoRequest request);

    ProdutoResponse atualizar(Long id, ProdutoRequest request);

    void excluir(Long id);

    /** Resolve a tributação (grupo tributário do produto x operação x UF) e cacheia o resultado nos campos {@code *Padrao}. */
    ProdutoResponse recalcularTributacaoPadrao(Long id, RecalcularTributacaoPadraoRequest request);
}
