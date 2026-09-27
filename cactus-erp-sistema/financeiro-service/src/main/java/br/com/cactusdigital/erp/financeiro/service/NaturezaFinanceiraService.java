package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.financeiro.dto.NaturezaFinanceiraRequest;
import br.com.cactusdigital.erp.financeiro.dto.NaturezaFinanceiraResponse;
import br.com.cactusdigital.erp.financeiro.entity.NaturezaFinanceira;
import br.com.cactusdigital.erp.financeiro.mapper.CaixaBancoMapper;
import br.com.cactusdigital.erp.financeiro.repository.NaturezaFinanceiraRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NaturezaFinanceiraService {

    private final NaturezaFinanceiraRepository repository;
    private final CaixaBancoMapper mapper;

    @Transactional(readOnly = true)
    public List<NaturezaFinanceiraResponse> listarAtivasDaEmpresa(Long empresaId) {
        return repository.findByEmpresaIdAndAtivoTrue(empresaId).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public NaturezaFinanceiraResponse criar(NaturezaFinanceiraRequest request) {
        NaturezaFinanceira natureza = NaturezaFinanceira.builder()
                .empresaId(request.empresaId())
                .codigo(request.codigo())
                .descricao(request.descricao())
                .tipo(request.tipo())
                .build();
        return mapper.toResponse(repository.save(natureza));
    }

    @Transactional
    public NaturezaFinanceiraResponse atualizar(Long id, NaturezaFinanceiraRequest request) {
        NaturezaFinanceira natureza = buscarEntidade(id);
        natureza.setCodigo(request.codigo());
        natureza.setDescricao(request.descricao());
        natureza.setTipo(request.tipo());
        return mapper.toResponse(natureza);
    }

    @Transactional
    public void inativar(Long id) {
        buscarEntidade(id).setAtivo(false);
    }

    private NaturezaFinanceira buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Natureza financeira", id));
    }
}
