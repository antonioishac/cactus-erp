package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.ResolverTributacaoRequest;
import br.com.cactusdigital.erp.cadastro.dto.ResolverTributacaoResponse;
import br.com.cactusdigital.erp.cadastro.entity.ConfiguracaoTributaria;
import br.com.cactusdigital.erp.cadastro.entity.IcmsConfiguracao;
import br.com.cactusdigital.erp.cadastro.exception.CadastroErrorCode;
import br.com.cactusdigital.erp.cadastro.mapper.TributacaoMotorMapper;
import br.com.cactusdigital.erp.cadastro.repository.ConfiguracaoTributariaRepository;
import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resolve a tributação de fato: grupo do produto × operação da venda × UF do
 * destinatário → CST/CSOSN/alíquotas achatados, prontos pra sync com o cactus-pdv (ver
 * MAPA DE SINCRONIZAÇÃO CAMPO A CAMPO no CLAUDE.md). Equivalente ao cruzamento
 * {@code TRIBUT_CONFIGURA_OF_GT} × {@code TRIBUT_ICMS_UF} do {@code fenix.sql}.
 */
@Service
@RequiredArgsConstructor
public class TributacaoResolucaoService {

    private final ConfiguracaoTributariaRepository repository;
    private final TributacaoMotorMapper mapper;

    @Transactional(readOnly = true)
    public ResolverTributacaoResponse resolver(ResolverTributacaoRequest request) {
        ConfiguracaoTributaria configuracao = repository
                .buscarComIcmsPorGrupoEOperacao(request.grupoTributarioId(), request.operacaoFiscalId())
                .orElseThrow(() -> NotFoundException.of("Configuração tributária",
                        "grupo=" + request.grupoTributarioId() + ", operação=" + request.operacaoFiscalId()));

        IcmsConfiguracao icms = configuracao.getIcmsPorUf().stream()
                .filter(linha -> linha.getUfDestino().equalsIgnoreCase(request.ufDestino()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(CadastroErrorCode.TRIBUTACAO_UF_NAO_CONFIGURADA,
                        "Não há regra de ICMS configurada para a UF " + request.ufDestino()
                                + " nessa combinação de grupo tributário e operação fiscal"));

        var operacaoFiscal = configuracao.getOperacaoFiscal();

        return new ResolverTributacaoResponse(
                configuracao.getGrupoTributario().getId(),
                configuracao.getGrupoTributario().getNome(),
                operacaoFiscal.getId(),
                operacaoFiscal.getNome(),
                operacaoFiscal.getCfop() != null ? operacaoFiscal.getCfop().getCodigo() : null,
                request.ufDestino(),
                mapper.toDto(icms),
                configuracao.getPis() != null ? mapper.toDto(configuracao.getPis()) : null,
                configuracao.getCofins() != null ? mapper.toDto(configuracao.getCofins()) : null,
                configuracao.getIpi() != null ? mapper.toDto(configuracao.getIpi()) : null,
                operacaoFiscal.getIssConfiguracao() != null ? mapper.toDto(operacaoFiscal.getIssConfiguracao()) : null,
                mapper.toResolvido(configuracao.getIbsCbs()));
    }
}
