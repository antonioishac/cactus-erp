package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.ColaboradorRequest;
import br.com.cactusdigital.erp.cadastro.dto.ColaboradorResponse;
import br.com.cactusdigital.erp.common.domain.Situacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ColaboradorService {

    Page<ColaboradorResponse> listar(String nome, String documento, Long cargoId, Long setorId, Situacao situacao, Pageable pageable);

    ColaboradorResponse buscarPorId(Long id);

    ColaboradorResponse criar(ColaboradorRequest request);

    ColaboradorResponse atualizar(Long id, ColaboradorRequest request);

    void excluir(Long id);
}
