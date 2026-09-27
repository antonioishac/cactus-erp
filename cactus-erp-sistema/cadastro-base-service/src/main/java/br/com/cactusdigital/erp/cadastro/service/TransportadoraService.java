package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.TransportadoraRequest;
import br.com.cactusdigital.erp.cadastro.dto.TransportadoraResponse;
import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import br.com.cactusdigital.erp.common.domain.Situacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransportadoraService {

    Page<TransportadoraResponse> listar(String nome, String documento, TipoPessoa tipoPessoa, Situacao situacao, Pageable pageable);

    TransportadoraResponse buscarPorId(Long id);

    TransportadoraResponse criar(TransportadoraRequest request);

    TransportadoraResponse atualizar(Long id, TransportadoraRequest request);

    void excluir(Long id);
}
