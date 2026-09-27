package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.ClienteRequest;
import br.com.cactusdigital.erp.cadastro.dto.ClienteResponse;
import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import br.com.cactusdigital.erp.common.domain.Situacao;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteService {

    Page<ClienteResponse> listar(String nome, String documento, TipoPessoa tipoPessoa, Situacao situacao, Pageable pageable);

    /** {@code desde} = cursor de sincronização incremental, ver {@code ClienteSpecifications}. */
    Page<ClienteResponse> listar(String nome, String documento, TipoPessoa tipoPessoa, Situacao situacao, Instant desde, Pageable pageable);

    ClienteResponse buscarPorId(Long id);

    ClienteResponse criar(ClienteRequest request);

    ClienteResponse atualizar(Long id, ClienteRequest request);

    void excluir(Long id);
}
