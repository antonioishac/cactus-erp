package br.com.cactusdigital.erp.seguranca.service;

import br.com.cactusdigital.erp.seguranca.dto.AtribuirFuncionalidadesRequest;
import br.com.cactusdigital.erp.seguranca.dto.PapelRequest;
import br.com.cactusdigital.erp.seguranca.dto.PapelResponse;
import br.com.cactusdigital.erp.seguranca.entity.Funcionalidade;
import br.com.cactusdigital.erp.seguranca.entity.Papel;
import br.com.cactusdigital.erp.seguranca.mapper.SegurancaMapper;
import br.com.cactusdigital.erp.seguranca.repository.FuncionalidadeRepository;
import br.com.cactusdigital.erp.seguranca.repository.PapelRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PapelService {

    private final PapelRepository papelRepository;
    private final FuncionalidadeRepository funcionalidadeRepository;
    private final SegurancaMapper mapper;

    /**
     * {@code funcionalidades} é `@ManyToMany` lazy — sem o `PaginatedFetchSupport`,
     * `pagina.map(mapper::toResponse)` dispara 1 query extra por papel da página pra
     * resolver a coleção (N+1 real, achado ao revisar a tela de Segurança, 2026-09-02;
     * mesmo padrão já corrigido em Cliente/Colaborador/Fornecedor/Produto/TabelaPreco/
     * ConfiguracaoTributaria/InventarioFisico).
     */
    @Transactional(readOnly = true)
    public Page<PapelResponse> listar(String nome, Pageable pageable) {
        Page<Papel> pagina = StringUtils.hasText(nome)
                ? papelRepository.findByNomeContainingIgnoreCase(nome, pageable)
                : papelRepository.findAll(pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, Papel::getId, papelRepository::findAllByIdComFuncionalidades, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PapelResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public PapelResponse criar(PapelRequest request) {
        Papel papel = Papel.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .situacao(request.situacao())
                .build();
        return mapper.toResponse(papelRepository.save(papel));
    }

    @Transactional
    public PapelResponse atualizar(Long id, PapelRequest request) {
        Papel papel = buscarEntidade(id);
        papel.setNome(request.nome());
        papel.setDescricao(request.descricao());
        papel.setSituacao(request.situacao());
        return mapper.toResponse(papel);
    }

    /** Substitui por completo o conjunto de funcionalidades do papel (PUT idempotente). */
    @Transactional
    public PapelResponse atribuirFuncionalidades(Long id, AtribuirFuncionalidadesRequest request) {
        Papel papel = buscarEntidade(id);
        List<Funcionalidade> funcionalidades = funcionalidadeRepository.findAllById(request.funcionalidadeIds());
        if (funcionalidades.size() != request.funcionalidadeIds().size()) {
            throw NotFoundException.of("Funcionalidade", "algum dos ids informados não existe");
        }
        papel.setFuncionalidades(new HashSet<>(funcionalidades));
        return mapper.toResponse(papel);
    }

    @Transactional
    public void excluir(Long id) {
        papelRepository.delete(buscarEntidade(id));
    }

    private Papel buscarEntidade(Long id) {
        return papelRepository.findById(id).orElseThrow(() -> NotFoundException.of("Papel", id));
    }
}
