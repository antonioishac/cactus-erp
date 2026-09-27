package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.financeiro.dto.BancoResponse;
import br.com.cactusdigital.erp.financeiro.mapper.CaixaBancoMapper;
import br.com.cactusdigital.erp.financeiro.repository.BancoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Catálogo global de bancos (código FEBRABAN) — só leitura via API, seedado por migration. */
@Service
@RequiredArgsConstructor
public class BancoService {

    private final BancoRepository repository;
    private final CaixaBancoMapper mapper;

    @Transactional(readOnly = true)
    public List<BancoResponse> listar() {
        return repository.findAllByOrderByCodigoAsc().stream().map(mapper::toResponse).toList();
    }
}
