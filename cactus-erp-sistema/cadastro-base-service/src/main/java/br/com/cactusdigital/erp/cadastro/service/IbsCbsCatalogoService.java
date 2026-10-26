package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.AliquotaIbsCbsResponse;
import br.com.cactusdigital.erp.cadastro.dto.ClassificacaoTributariaIbsCbsResponse;
import br.com.cactusdigital.erp.cadastro.dto.CstIbsCbsResponse;
import br.com.cactusdigital.erp.cadastro.entity.AliquotaIbsCbsVigencia;
import br.com.cactusdigital.erp.cadastro.entity.ClassificacaoTributariaIbsCbs;
import br.com.cactusdigital.erp.cadastro.exception.CadastroErrorCode;
import br.com.cactusdigital.erp.cadastro.repository.AliquotaIbsCbsVigenciaRepository;
import br.com.cactusdigital.erp.cadastro.repository.ClassificacaoTributariaIbsCbsRepository;
import br.com.cactusdigital.erp.cadastro.repository.CstIbsCbsRepository;
import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Consulta dos catálogos oficiais do IBS/CBS e das alíquotas vigentes. Só leitura. */
@Service
@RequiredArgsConstructor
public class IbsCbsCatalogoService {

    private final CstIbsCbsRepository cstRepository;
    private final ClassificacaoTributariaIbsCbsRepository classificacaoRepository;
    private final AliquotaIbsCbsVigenciaRepository aliquotaRepository;
    private final RegrasClassificacaoIbsCbs regras;

    @Transactional(readOnly = true)
    public List<CstIbsCbsResponse> listarCst() {
        return cstRepository.findAll().stream()
                .sorted(Comparator.comparing(c -> c.getCodigo()))
                .map(c -> new CstIbsCbsResponse(c.getCodigo(), c.getDescricao(), c.isExigeTributacao(), c.isReducaoAliquota(),
                        c.isDiferimento(), c.isMonofasica(), regras.cstSuportado(c.getCodigo())))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ClassificacaoTributariaIbsCbsResponse> listarClassificacoes(String cst, String busca, Pageable pageable) {
        return classificacaoRepository.buscar(vazioParaNulo(cst), vazioParaNulo(busca), pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ClassificacaoTributariaIbsCbsResponse buscarClassificacao(String codigo) {
        return toResponse(buscarEntidade(codigo));
    }

    /** Alíquotas do ano da data informada. Ano sem alíquota em lei = 422 (nunca inventa). */
    @Transactional(readOnly = true)
    public AliquotaIbsCbsResponse aliquotasVigentes(LocalDate data) {
        AliquotaIbsCbsVigencia a = aliquotaRepository.findById((short) data.getYear())
                .orElseThrow(() -> new BusinessException(CadastroErrorCode.ALIQUOTA_IBS_CBS_NAO_DEFINIDA,
                        "Não há alíquota de IBS/CBS definida em lei para " + data.getYear() + " no ERP"));
        return new AliquotaIbsCbsResponse(a.getAno(), a.getAliquotaIbsUf(), a.getAliquotaIbsMunicipio(), a.getAliquotaCbs(), a.getFundamento());
    }

    ClassificacaoTributariaIbsCbs buscarEntidade(String codigo) {
        return classificacaoRepository.findById(codigo)
                .orElseThrow(() -> NotFoundException.of("Classificação tributária do IBS/CBS", codigo));
    }

    private ClassificacaoTributariaIbsCbsResponse toResponse(ClassificacaoTributariaIbsCbs c) {
        return new ClassificacaoTributariaIbsCbsResponse(c.getCodigo(), c.getCst().getCodigo(), c.getCst().isExigeTributacao(),
                c.getCst().isReducaoAliquota(), c.getDescricao(), c.getNomeReduzido(),
                c.getPercentualReducaoIbs(), c.getPercentualReducaoCbs(), c.isTributacaoRegular(), c.isAceitaNfe(), c.isAceitaNfce(),
                c.getUrlLegislacao(), c.getInicioVigencia(), c.getFimVigencia(), regras.motivoNaoSuportada(c).orElse(null));
    }

    private static String vazioParaNulo(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
