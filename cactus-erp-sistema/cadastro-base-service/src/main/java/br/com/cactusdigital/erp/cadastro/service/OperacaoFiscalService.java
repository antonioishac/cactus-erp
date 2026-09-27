package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.OperacaoFiscalRequest;
import br.com.cactusdigital.erp.cadastro.dto.OperacaoFiscalResponse;
import br.com.cactusdigital.erp.cadastro.entity.Cfop;
import br.com.cactusdigital.erp.cadastro.entity.IssConfiguracao;
import br.com.cactusdigital.erp.cadastro.entity.OperacaoFiscal;
import br.com.cactusdigital.erp.cadastro.mapper.TributacaoMotorMapper;
import br.com.cactusdigital.erp.cadastro.repository.CfopRepository;
import br.com.cactusdigital.erp.cadastro.repository.OperacaoFiscalRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * CRUD do tipo de operação de venda ({@link OperacaoFiscal}) — validado contra
 * {@code fenix.sql.TRIBUT_OPERACAO_FISCAL}. {@code issConfiguracao} é gerenciada como
 * parte do agregado (igual Pessoa+Endereco), sem controller próprio pro filho.
 */
@Service
@RequiredArgsConstructor
public class OperacaoFiscalService {

    private final OperacaoFiscalRepository repository;
    private final CfopRepository cfopRepository;
    private final TributacaoMotorMapper mapper;

    @Transactional(readOnly = true)
    public Page<OperacaoFiscalResponse> listar(String nome, Pageable pageable) {
        Page<OperacaoFiscal> pagina = StringUtils.hasText(nome)
                ? repository.findByNomeContainingIgnoreCase(nome, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public OperacaoFiscalResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public OperacaoFiscalResponse criar(OperacaoFiscalRequest request) {
        OperacaoFiscal entidade = mapper.toEntity(request);
        entidade.setCfop(resolverCfop(request.cfopId()));
        aplicarIss(entidade, request);
        return mapper.toResponse(repository.save(entidade));
    }

    @Transactional
    public OperacaoFiscalResponse atualizar(Long id, OperacaoFiscalRequest request) {
        OperacaoFiscal entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        entidade.setCfop(resolverCfop(request.cfopId()));
        aplicarIss(entidade, request);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    /**
     * {@code orphanRemoval=true} em {@link OperacaoFiscal#getIssConfiguracao()} cuida de
     * remover a linha antiga quando o request não traz mais ISS (operação deixou de ser
     * de serviço); criar/atualizar em cima da mesma instância evita perder o id existente.
     */
    private void aplicarIss(OperacaoFiscal entidade, OperacaoFiscalRequest request) {
        if (request.issConfiguracao() == null) {
            entidade.setIssConfiguracao(null);
            return;
        }
        IssConfiguracao iss = entidade.getIssConfiguracao() != null ? entidade.getIssConfiguracao() : new IssConfiguracao();
        mapper.atualizar(request.issConfiguracao(), iss);
        iss.setOperacaoFiscal(entidade);
        entidade.setIssConfiguracao(iss);
    }

    private OperacaoFiscal buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Operação fiscal", id));
    }

    private Cfop resolverCfop(Long cfopId) {
        return cfopId != null ? cfopRepository.findById(cfopId).orElseThrow(() -> NotFoundException.of("CFOP", cfopId)) : null;
    }
}
