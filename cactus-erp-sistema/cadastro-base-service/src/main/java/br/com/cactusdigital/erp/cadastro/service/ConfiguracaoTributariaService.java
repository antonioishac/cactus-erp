package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.CofinsConfiguracaoDTO;
import br.com.cactusdigital.erp.cadastro.dto.ConfiguracaoTributariaRequest;
import br.com.cactusdigital.erp.cadastro.dto.ConfiguracaoTributariaResponse;
import br.com.cactusdigital.erp.cadastro.dto.IcmsConfiguracaoDTO;
import br.com.cactusdigital.erp.cadastro.dto.IpiConfiguracaoDTO;
import br.com.cactusdigital.erp.cadastro.dto.IbsCbsConfiguracaoRequest;
import br.com.cactusdigital.erp.cadastro.dto.PisConfiguracaoDTO;
import br.com.cactusdigital.erp.cadastro.entity.CofinsConfiguracao;
import br.com.cactusdigital.erp.cadastro.entity.ConfiguracaoTributaria;
import br.com.cactusdigital.erp.cadastro.entity.GrupoTributario;
import br.com.cactusdigital.erp.cadastro.entity.IcmsConfiguracao;
import br.com.cactusdigital.erp.cadastro.entity.IpiConfiguracao;
import br.com.cactusdigital.erp.cadastro.entity.OperacaoFiscal;
import br.com.cactusdigital.erp.cadastro.entity.ClassificacaoTributariaIbsCbs;
import br.com.cactusdigital.erp.cadastro.entity.IbsCbsConfiguracao;
import br.com.cactusdigital.erp.cadastro.entity.PisConfiguracao;
import br.com.cactusdigital.erp.cadastro.exception.CadastroErrorCode;
import br.com.cactusdigital.erp.cadastro.mapper.TributacaoMotorMapper;
import br.com.cactusdigital.erp.cadastro.repository.ConfiguracaoTributariaRepository;
import br.com.cactusdigital.erp.cadastro.repository.GrupoTributarioRepository;
import br.com.cactusdigital.erp.cadastro.repository.OperacaoFiscalRepository;
import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD da célula {@link GrupoTributario} × {@link OperacaoFiscal} — validado contra
 * {@code fenix.sql.TRIBUT_CONFIGURA_OF_GT}. Gerencia {@link IcmsConfiguracao} (lista,
 * 1 por UF de destino) e {@link PisConfiguracao}/{@link CofinsConfiguracao}/
 * {@link IpiConfiguracao} (1:1 cada) como um agregado só, igual Pessoa+Endereco.
 */
@Service
@RequiredArgsConstructor
public class ConfiguracaoTributariaService {

    private final ConfiguracaoTributariaRepository repository;
    private final GrupoTributarioRepository grupoTributarioRepository;
    private final OperacaoFiscalRepository operacaoFiscalRepository;
    private final IbsCbsCatalogoService ibsCbsCatalogoService;
    private final RegrasClassificacaoIbsCbs regrasIbsCbs;
    private final TributacaoMotorMapper mapper;

    /**
     * BUG REAL corrigido (achado ao validar a tela do frontend, 2026-09-13): esta versão
     * ignorava {@code grupoTributarioId}/{@code operacaoFiscalId} e sempre chamava
     * {@code findAll} — o filtro documentado na Swagger nunca era de fato aplicado.
     */
    @Transactional(readOnly = true)
    public Page<ConfiguracaoTributariaResponse> listar(Long grupoTributarioId, Long operacaoFiscalId, Pageable pageable) {
        Page<ConfiguracaoTributaria> pagina;
        if (grupoTributarioId != null && operacaoFiscalId != null) {
            pagina = repository.findByGrupoTributarioIdAndOperacaoFiscalId(grupoTributarioId, operacaoFiscalId, pageable);
        } else if (grupoTributarioId != null) {
            pagina = repository.findByGrupoTributarioId(grupoTributarioId, pageable);
        } else if (operacaoFiscalId != null) {
            pagina = repository.findByOperacaoFiscalId(operacaoFiscalId, pageable);
        } else {
            pagina = repository.findAll(pageable);
        }
        if (!pagina.isEmpty()) {
            repository.hidratarColecoes(pagina.getContent().stream().map(ConfiguracaoTributaria::getId).toList());
        }
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ConfiguracaoTributariaResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ConfiguracaoTributariaResponse criar(ConfiguracaoTributariaRequest request) {
        if (repository.existsByGrupoTributarioIdAndOperacaoFiscalId(request.grupoTributarioId(), request.operacaoFiscalId())) {
            throw new ConflictException(CadastroErrorCode.CONFIGURACAO_TRIBUTARIA_DUPLICADA);
        }

        ConfiguracaoTributaria entidade = ConfiguracaoTributaria.builder()
                .grupoTributario(resolverGrupoTributario(request.grupoTributarioId()))
                .operacaoFiscal(resolverOperacaoFiscal(request.operacaoFiscalId()))
                .build();

        aplicarIcms(entidade, request.icmsPorUf());
        aplicarPis(entidade, request.pis());
        aplicarCofins(entidade, request.cofins());
        aplicarIpi(entidade, request.ipi());
        aplicarIbsCbs(entidade, request.ibsCbs());

        return mapper.toResponse(repository.save(entidade));
    }

    @Transactional
    public ConfiguracaoTributariaResponse atualizar(Long id, ConfiguracaoTributariaRequest request) {
        ConfiguracaoTributaria entidade = buscarEntidadeComIcms(id);

        boolean mudouCelula = !Objects.equals(entidade.getGrupoTributario().getId(), request.grupoTributarioId())
                || !Objects.equals(entidade.getOperacaoFiscal().getId(), request.operacaoFiscalId());
        if (mudouCelula && repository.existsByGrupoTributarioIdAndOperacaoFiscalId(request.grupoTributarioId(), request.operacaoFiscalId())) {
            throw new ConflictException(CadastroErrorCode.CONFIGURACAO_TRIBUTARIA_DUPLICADA);
        }

        entidade.setGrupoTributario(resolverGrupoTributario(request.grupoTributarioId()));
        entidade.setOperacaoFiscal(resolverOperacaoFiscal(request.operacaoFiscalId()));

        aplicarIcms(entidade, request.icmsPorUf());
        aplicarPis(entidade, request.pis());
        aplicarCofins(entidade, request.cofins());
        aplicarIpi(entidade, request.ipi());
        aplicarIbsCbs(entidade, request.ibsCbs());

        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    /**
     * Sincroniza as linhas de ICMS por UF casando pela UF: a linha que já existe é ATUALIZADA no lugar, UF nova
     * é incluída e só a UF que saiu da requisição é removida (orphanRemoval, mesma coleção gerenciada).
     *
     * <p>Bug corrigido (2026-10-25): a versão anterior limpava a lista e recriava todas as linhas. O Hibernate
     * executa os INSERTs antes dos DELETEs dos órfãos no flush, então a linha nova de "SP" colidia com a antiga
     * na chave única {@code uk_icms_config_uf} — TODO {@code PUT} de configuração com ICMS falhava com 409.
     */
    private void aplicarIcms(ConfiguracaoTributaria entidade, List<IcmsConfiguracaoDTO> icmsPorUf) {
        List<IcmsConfiguracaoDTO> novas = icmsPorUf != null ? icmsPorUf : List.of();
        java.util.Set<String> ufsNovas = novas.stream().map(dto -> dto.ufDestino().toUpperCase()).collect(java.util.stream.Collectors.toSet());
        entidade.getIcmsPorUf().removeIf(existente -> !ufsNovas.contains(existente.getUfDestino().toUpperCase()));
        for (IcmsConfiguracaoDTO dto : novas) {
            IcmsConfiguracao icms = entidade.getIcmsPorUf().stream()
                    .filter(existente -> existente.getUfDestino().equalsIgnoreCase(dto.ufDestino()))
                    .findFirst()
                    .orElse(null);
            if (icms != null) {
                mapper.atualizar(dto, icms);
            } else {
                icms = mapper.toEntity(dto);
                icms.setConfiguracaoTributaria(entidade);
                entidade.getIcmsPorUf().add(icms);
            }
        }
    }

    private void aplicarPis(ConfiguracaoTributaria entidade, PisConfiguracaoDTO dto) {
        if (dto == null) {
            entidade.setPis(null);
            return;
        }
        PisConfiguracao pis = entidade.getPis() != null ? entidade.getPis() : new PisConfiguracao();
        mapper.atualizar(dto, pis);
        pis.setConfiguracaoTributaria(entidade);
        entidade.setPis(pis);
    }

    private void aplicarCofins(ConfiguracaoTributaria entidade, CofinsConfiguracaoDTO dto) {
        if (dto == null) {
            entidade.setCofins(null);
            return;
        }
        CofinsConfiguracao cofins = entidade.getCofins() != null ? entidade.getCofins() : new CofinsConfiguracao();
        mapper.atualizar(dto, cofins);
        cofins.setConfiguracaoTributaria(entidade);
        entidade.setCofins(cofins);
    }

    private void aplicarIpi(ConfiguracaoTributaria entidade, IpiConfiguracaoDTO dto) {
        if (dto == null) {
            entidade.setIpi(null);
            return;
        }
        IpiConfiguracao ipi = entidade.getIpi() != null ? entidade.getIpi() : new IpiConfiguracao();
        mapper.atualizar(dto, ipi);
        ipi.setConfiguracaoTributaria(entidade);
        entidade.setIpi(ipi);
    }

    /**
     * Só aceita classificação que o ERP sabe emitir e que está vigente hoje — a recusa vem aqui, na
     * configuração, e não na hora da NF-e (ver {@link RegrasClassificacaoIbsCbs}).
     */
    private void aplicarIbsCbs(ConfiguracaoTributaria entidade, IbsCbsConfiguracaoRequest dto) {
        if (dto == null) {
            entidade.setIbsCbs(null);
            return;
        }
        ClassificacaoTributariaIbsCbs classificacao = ibsCbsCatalogoService.buscarEntidade(dto.classificacaoTributaria());
        regrasIbsCbs.motivoNaoSuportada(classificacao).ifPresent(motivo -> {
            throw new BusinessException(CadastroErrorCode.CLASSIFICACAO_IBS_CBS_NAO_SUPORTADA,
                    "cClassTrib " + classificacao.getCodigo() + ": " + motivo);
        });
        if (!classificacao.vigenteEm(LocalDate.now())) {
            throw new BusinessException(CadastroErrorCode.CLASSIFICACAO_IBS_CBS_FORA_DE_VIGENCIA,
                    "cClassTrib " + classificacao.getCodigo() + " fora de vigência (vigente de " + classificacao.getInicioVigencia()
                            + (classificacao.getFimVigencia() != null ? " até " + classificacao.getFimVigencia() : "") + ")");
        }
        IbsCbsConfiguracao ibsCbs = entidade.getIbsCbs() != null ? entidade.getIbsCbs() : new IbsCbsConfiguracao();
        ibsCbs.setClassificacaoTributaria(classificacao);
        ibsCbs.setConfiguracaoTributaria(entidade);
        entidade.setIbsCbs(ibsCbs);
    }

    private ConfiguracaoTributaria buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Configuração tributária", id));
    }

    private ConfiguracaoTributaria buscarEntidadeComIcms(Long id) {
        return repository.buscarComIcmsPorId(id).orElseThrow(() -> NotFoundException.of("Configuração tributária", id));
    }

    private GrupoTributario resolverGrupoTributario(Long id) {
        return grupoTributarioRepository.findById(id).orElseThrow(() -> NotFoundException.of("Grupo tributário", id));
    }

    private OperacaoFiscal resolverOperacaoFiscal(Long id) {
        return operacaoFiscalRepository.findById(id).orElseThrow(() -> NotFoundException.of("Operação fiscal", id));
    }
}
