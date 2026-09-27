package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.financeiro.dto.TipoDocumentoFinanceiroRequest;
import br.com.cactusdigital.erp.financeiro.dto.TipoDocumentoFinanceiroResponse;
import br.com.cactusdigital.erp.financeiro.entity.TipoDocumentoFinanceiro;
import br.com.cactusdigital.erp.financeiro.mapper.CaixaBancoMapper;
import br.com.cactusdigital.erp.financeiro.repository.TipoDocumentoFinanceiroRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TipoDocumentoFinanceiroService {

    private final TipoDocumentoFinanceiroRepository repository;
    private final CaixaBancoMapper mapper;

    @Transactional(readOnly = true)
    public List<TipoDocumentoFinanceiroResponse> listarAtivos() {
        return repository.findByAtivoTrue().stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public TipoDocumentoFinanceiroResponse criar(TipoDocumentoFinanceiroRequest request) {
        TipoDocumentoFinanceiro tipo = TipoDocumentoFinanceiro.builder()
                .codigo(request.codigo())
                .descricao(request.descricao())
                .build();
        return mapper.toResponse(repository.save(tipo));
    }

    @Transactional
    public void inativar(Long id) {
        buscarEntidade(id).setAtivo(false);
    }

    private TipoDocumentoFinanceiro buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Tipo de documento financeiro", id));
    }
}
