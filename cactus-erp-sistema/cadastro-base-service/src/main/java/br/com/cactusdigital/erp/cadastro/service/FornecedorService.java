package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.FornecedorRequest;
import br.com.cactusdigital.erp.cadastro.dto.FornecedorResponse;
import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import br.com.cactusdigital.erp.common.domain.Situacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FornecedorService {

    Page<FornecedorResponse> listar(String nome, String documento, TipoPessoa tipoPessoa, Situacao situacao, Pageable pageable);

    FornecedorResponse buscarPorId(Long id);

    FornecedorResponse criar(FornecedorRequest request);

    FornecedorResponse atualizar(Long id, FornecedorRequest request);

    void excluir(Long id);
}
