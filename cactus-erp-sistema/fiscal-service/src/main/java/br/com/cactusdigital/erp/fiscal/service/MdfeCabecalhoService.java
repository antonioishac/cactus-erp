package br.com.cactusdigital.erp.fiscal.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.fiscal.AcbrMonitorClient;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.fiscal.client.CadastroClient;
import br.com.cactusdigital.erp.fiscal.client.dto.ClienteFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaConfiguracaoFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EnderecoClienteDTO;
import br.com.cactusdigital.erp.fiscal.dto.CancelarMdfeRequest;
import br.com.cactusdigital.erp.fiscal.dto.CteDisponivelMdfeResponse;
import br.com.cactusdigital.erp.fiscal.dto.EncerrarMdfeRequest;
import br.com.cactusdigital.erp.fiscal.dto.GerarMdfeRequest;
import java.time.Duration;
import br.com.cactusdigital.erp.fiscal.dto.MdfeCabecalhoResponse;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.CteCabecalho;
import br.com.cactusdigital.erp.fiscal.entity.MdfeCabecalho;
import br.com.cactusdigital.erp.fiscal.entity.MdfeDocumentoVinculado;
import br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import br.com.cactusdigital.erp.fiscal.mapper.FiscalMapper;
import br.com.cactusdigital.erp.fiscal.repository.CteCabecalhoRepository;
import br.com.cactusdigital.erp.fiscal.repository.MdfeCabecalhoRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Compõe e emite um MDF-e (modelo 58) agrupando N {@link CteCabecalho} já {@code AUTORIZADA}
 * da mesma empresa. Mesma máquina de estados de {@code CteCabecalhoService}: {@code RASCUNHO}
 * → (emitir) → {@code ENVIADA} → {@code AUTORIZADA}/{@code REJEITADA} ou {@code ERRO}.
 * <p>
 * <b>Numeração reservada por último</b>: toda validação (local e cross-serviço) roda antes de
 * {@code reservarProximoNumeroMdfe}, e o único passo depois dela é o {@code save} local — então
 * o número só "queima" se o próprio banco do fiscal-service falhar no commit. Por isso não usa o
 * {@code SagaOrchestrator} (diferente de NF-e, onde há chamadas remotas DEPOIS da reserva).
 * <p>
 * Encerramento ({@code MDFE.EncerrarMDFe}) e cancelamento ficam fora de escopo — ver Javadoc de
 * {@link MdfeCabecalho}.
 */
@Service
@RequiredArgsConstructor
public class MdfeCabecalhoService {

    private final MdfeCabecalhoRepository repository;
    private final CteCabecalhoRepository cteRepository;
    private final CadastroClient cadastroClient;
    private final AcbrMonitorClient acbrMonitorClient;
    private final MdfeIniBuilder iniBuilder;
    private final FiscalMapper mapper;

    @Value("${cactus.acbr.host}")
    private String acbrHost;

    @Value("${cactus.acbr.porta}")
    private int acbrPorta;

    @Transactional(readOnly = true)
    public Page<MdfeCabecalhoResponse> listar(Pageable pageable) {
        Page<MdfeCabecalho> pagina = repository.findAll(pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, MdfeCabecalho::getId, repository::buscarComDocumentos, mapper::toResponse);
    }

    /** Seletor da tela de geração — a mesma regra que {@link #gerar} valida, aplicada no banco. */
    @Transactional(readOnly = true)
    public List<CteDisponivelMdfeResponse> listarCtesDisponiveis(Long empresaId) {
        return cteRepository.buscarDisponiveisParaMdfe(empresaId);
    }

    @Transactional(readOnly = true)
    public MdfeCabecalhoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarComDocumentos(id));
    }

    @Transactional
    public MdfeCabecalhoResponse gerar(GerarMdfeRequest request) {
        if (new HashSet<>(request.cteIds()).size() != request.cteIds().size()) {
            throw new BusinessException(FiscalErrorCode.MDFE_CTE_DUPLICADO_NA_REQUISICAO);
        }
        List<CteCabecalho> ctes = cteRepository.findAllById(request.cteIds());
        if (ctes.size() != request.cteIds().size()) {
            Long ausente = request.cteIds().stream()
                    .filter(id -> ctes.stream().noneMatch(c -> c.getId().equals(id)))
                    .findFirst().orElseThrow();
            throw NotFoundException.of("CT-e", ausente);
        }
        for (CteCabecalho cte : ctes) {
            if (cte.getSituacao() != SituacaoNotaFiscal.AUTORIZADA) {
                throw new BusinessException(FiscalErrorCode.MDFE_CTE_NAO_AUTORIZADO,
                        "CT-e #" + cte.getId() + " está " + cte.getSituacao() + " — precisa estar AUTORIZADA");
            }
            if (!cte.getEmpresaId().equals(request.empresaId())) {
                throw new BusinessException(FiscalErrorCode.MDFE_CTE_DE_OUTRA_EMPRESA,
                        "CT-e #" + cte.getId() + " é da empresa #" + cte.getEmpresaId()
                                + ", mas o MDF-e foi pedido para a empresa #" + request.empresaId());
            }
        }
        List<Long> jaVinculados = repository.buscarCteIdsJaVinculados(request.cteIds());
        if (!jaVinculados.isEmpty()) {
            throw new ConflictException(FiscalErrorCode.MDFE_CTE_JA_VINCULADO,
                    "CT-e já vinculado(s) a outro MDF-e: " + jaVinculados);
        }

        EmpresaFiscalDTO emitente = cadastroClient.buscarEmpresa(request.empresaId());
        if (emitente.uf() == null || emitente.codigoIbgeMunicipio() == null) {
            throw new BusinessException(FiscalErrorCode.MDFE_EMITENTE_SEM_ENDERECO);
        }
        Map<Long, EnderecoClienteDTO> enderecoPorCliente = new HashMap<>();
        for (CteCabecalho cte : ctes) {
            EnderecoClienteDTO endereco = enderecoPorCliente.computeIfAbsent(cte.getClienteId(), this::enderecoDescarga);
            if (endereco == null || endereco.codigoIbgeMunicipio() == null) {
                throw new BusinessException(FiscalErrorCode.MDFE_DESTINATARIO_SEM_ENDERECO,
                        "Destinatário (cliente #" + cte.getClienteId() + ") do CT-e #" + cte.getId() + " sem município IBGE");
            }
            if (!Objects.equals(endereco.uf(), emitente.uf())) {
                throw new BusinessException(FiscalErrorCode.MDFE_VIAGEM_INTERESTADUAL_NAO_SUPORTADA,
                        "CT-e #" + cte.getId() + " tem destino em " + endereco.uf() + ", emitente em " + emitente.uf());
            }
        }

        EmpresaConfiguracaoFiscalDTO configFiscal = cadastroClient.reservarProximoNumeroMdfe(request.empresaId());

        MdfeCabecalho mdfe = MdfeCabecalho.builder()
                .empresaId(request.empresaId())
                .serie(configFiscal.serieMdfe())
                .numero(configFiscal.proximoNumeroMdfe())
                .ambiente("PRODUCAO".equalsIgnoreCase(configFiscal.ambienteFiscal()) ? AmbienteFiscal.PRODUCAO : AmbienteFiscal.HOMOLOGACAO)
                .uf(emitente.uf())
                .rntrc(request.rntrc() != null ? request.rntrc() : ctes.get(0).getRntrc())
                .veiculoPlaca(request.veiculoPlaca())
                .veiculoUf(request.veiculoUf() != null ? request.veiculoUf() : emitente.uf())
                .veiculoRenavam(request.veiculoRenavam())
                .veiculoTaraKg(request.veiculoTaraKg())
                .veiculoCapacidadeKg(request.veiculoCapacidadeKg())
                .condutorNome(request.condutorNome())
                .condutorCpf(request.condutorCpf())
                .municipioCarregamentoCodigoIbge(emitente.codigoIbgeMunicipio())
                .municipioCarregamentoNome(emitente.municipio())
                .dataInicioViagem(request.dataInicioViagem() != null ? request.dataInicioViagem() : LocalDate.now())
                .valorTotalCarga(somar(ctes, CteCabecalho::getValorTotalCarga))
                .pesoBrutoTotal(somar(ctes, CteCabecalho::getPesoBruto))
                .situacao(SituacaoNotaFiscal.RASCUNHO)
                .build();

        // Ordem do request preservada (findAllById não garante ordem).
        for (Long cteId : request.cteIds()) {
            CteCabecalho cte = ctes.stream().filter(c -> c.getId().equals(cteId)).findFirst().orElseThrow();
            EnderecoClienteDTO endereco = enderecoPorCliente.get(cte.getClienteId());
            mdfe.getDocumentos().add(MdfeDocumentoVinculado.builder()
                    .mdfeCabecalho(mdfe)
                    .cteCabecalho(cte)
                    .municipioDescargaCodigoIbge(endereco.codigoIbgeMunicipio())
                    .municipioDescargaNome(endereco.municipio())
                    .build());
        }
        return mapper.toResponse(repository.save(mdfe));
    }

    /** Monta o comando {@code MDFE.CriarEnviarMDFe} e chama o ACBr Monitor. Nunca invocado ao vivo — ver Javadoc de {@link MdfeIniBuilder}. */
    @Transactional
    public MdfeCabecalhoResponse emitir(Long id) {
        MdfeCabecalho mdfe = buscarComDocumentos(id);
        if (mdfe.getSituacao() != SituacaoNotaFiscal.RASCUNHO && mdfe.getSituacao() != SituacaoNotaFiscal.ERRO) {
            throw new ConflictException(FiscalErrorCode.NOTA_FISCAL_SITUACAO_INVALIDA);
        }
        EmpresaFiscalDTO emitente = cadastroClient.buscarEmpresa(mdfe.getEmpresaId());

        mdfe.setDataEmissao(Instant.now());
        String comando = iniBuilder.construirComando(mdfe, emitente, 1);

        mdfe.setSituacao(SituacaoNotaFiscal.ENVIADA);
        try {
            String resposta = acbrMonitorClient.enviarComando(acbrHost, acbrPorta, comando);
            if (acbrMonitorClient.respostaComErro(resposta)) {
                mdfe.setSituacao(SituacaoNotaFiscal.REJEITADA);
                mdfe.setMensagemSefaz(truncar(resposta, 500));
                return mapper.toResponse(mdfe);
            }
            mdfe.setSituacao(SituacaoNotaFiscal.AUTORIZADA);
            mdfe.setChaveAcesso(extrair(resposta, "ChMDFe", 44));
            mdfe.setProtocoloAutorizacao(extrair(resposta, "NProt", 30));
            mdfe.setMensagemSefaz("Autorizado.");
            return mapper.toResponse(mdfe);
        } catch (IOException ex) {
            mdfe.setSituacao(SituacaoNotaFiscal.ERRO);
            mdfe.setMensagemSefaz(truncar("Falha de comunicação com o ACBr Monitor: " + ex.getMessage(), 500));
            return mapper.toResponse(mdfe);
        }
    }

    /**
     * Registra o evento de Encerramento (110112) — legalmente obrigatório ao fim da viagem; um MDF-e
     * aberto impede novos manifestos do mesmo veículo/UF na SEFAZ. Diferente de {@link #emitir}, falha
     * aqui NUNCA muda a situação: o MDF-e continua {@code AUTORIZADA} (o documento em si segue válido) e
     * o erro volta ao chamador — rejeição da SEFAZ como 422, falha de comunicação como 502.
     */
    @Transactional
    public MdfeCabecalhoResponse encerrar(Long id, EncerrarMdfeRequest request) {
        MdfeCabecalho mdfe = buscarComDocumentos(id);
        exigirAutorizada(mdfe);

        LocalDate data = request != null && request.dataEncerramento() != null ? request.dataEncerramento() : LocalDate.now();
        if (data.isAfter(LocalDate.now())
                || (mdfe.getDataInicioViagem() != null && data.isBefore(mdfe.getDataInicioViagem()))) {
            throw new BusinessException(FiscalErrorCode.MDFE_DATA_ENCERRAMENTO_INVALIDA);
        }
        Integer municipio = request != null && request.municipioCodigoIbge() != null
                ? request.municipioCodigoIbge()
                : mdfe.getDocumentos().get(mdfe.getDocumentos().size() - 1).getMunicipioDescargaCodigoIbge();

        mdfe.setDataEncerramento(data);
        mdfe.setMunicipioEncerramentoCodigoIbge(municipio);
        String cnpj = cadastroClient.buscarEmpresa(mdfe.getEmpresaId()).cnpj();
        String resposta = enviarEvento(iniBuilder.construirComandoEncerramento(mdfe, cnpj));

        mdfe.setProtocoloEncerramento(extrair(resposta, "NProt", 30));
        mdfe.setMensagemSefaz(truncar(extrair(resposta, "XMotivo", 500), 500));
        mdfe.setSituacao(SituacaoNotaFiscal.ENCERRADA);
        return mapper.toResponse(mdfe);
    }

    /**
     * Registra o evento de Cancelamento (110111). Só em até 24h da autorização e antes do encerramento
     * (regra da SEFAZ; depois disso o caminho é encerrar). O cancelamento libera os CT-e do manifesto pra
     * um novo MDF-e (ver {@code MdfeCabecalhoRepository#buscarCteIdsJaVinculados}). Falha não muda a
     * situação, mesmo critério de {@link #encerrar}.
     */
    @Transactional
    public MdfeCabecalhoResponse cancelar(Long id, CancelarMdfeRequest request) {
        MdfeCabecalho mdfe = buscarComDocumentos(id);
        exigirAutorizada(mdfe);
        if (mdfe.getDataEmissao() == null || mdfe.getDataEmissao().plus(Duration.ofHours(24)).isBefore(Instant.now())) {
            throw new BusinessException(FiscalErrorCode.MDFE_PRAZO_CANCELAMENTO_EXPIRADO);
        }

        mdfe.setJustificativaCancelamento(request.justificativa().strip());
        String cnpj = cadastroClient.buscarEmpresa(mdfe.getEmpresaId()).cnpj();
        String resposta = enviarEvento(iniBuilder.construirComandoCancelamento(mdfe, cnpj));

        mdfe.setDataCancelamento(Instant.now());
        mdfe.setProtocoloCancelamento(extrair(resposta, "NProt", 30));
        mdfe.setMensagemSefaz(truncar(extrair(resposta, "XMotivo", 500), 500));
        mdfe.setSituacao(SituacaoNotaFiscal.CANCELADA);
        return mapper.toResponse(mdfe);
    }

    private void exigirAutorizada(MdfeCabecalho mdfe) {
        if (mdfe.getSituacao() != SituacaoNotaFiscal.AUTORIZADA) {
            throw new ConflictException(FiscalErrorCode.MDFE_EVENTO_SITUACAO_INVALIDA,
                    "MDF-e #" + mdfe.getId() + " está " + mdfe.getSituacao() + " — eventos só em MDF-e AUTORIZADA");
        }
    }

    /**
     * Evento só é aceito com {@code CStat=135} ("Evento registrado e vinculado ao MDF-e"). Um {@code OK:}
     * do ACBr com outro {@code CStat} ainda é rejeição da SEFAZ — o ACBr responde OK quando a comunicação
     * funcionou, não quando o evento foi aceito.
     */
    private String enviarEvento(String comando) {
        String resposta;
        try {
            resposta = acbrMonitorClient.enviarComando(acbrHost, acbrPorta, comando);
        } catch (IOException ex) {
            throw new BusinessException(FiscalErrorCode.FALHA_COMUNICACAO_ACBR,
                    "Falha de comunicação com o ACBr Monitor: " + ex.getMessage());
        }
        if (acbrMonitorClient.respostaComErro(resposta) || !"135".equals(extrair(resposta, "CStat", 3))) {
            String motivo = extrair(resposta, "XMotivo", 400);
            throw new BusinessException(FiscalErrorCode.SEFAZ_REJEITOU_DOCUMENTO,
                    truncar(motivo != null ? "SEFAZ: " + motivo : "Resposta do ACBr: " + resposta, 500));
        }
        return resposta;
    }

    private EnderecoClienteDTO enderecoDescarga(Long clienteId) {
        ClienteFiscalDTO cliente = cadastroClient.buscarCliente(clienteId);
        return cliente.enderecos() != null && !cliente.enderecos().isEmpty() ? cliente.enderecos().get(0) : null;
    }

    private BigDecimal somar(List<CteCabecalho> ctes, java.util.function.Function<CteCabecalho, BigDecimal> campo) {
        return ctes.stream().map(campo).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private MdfeCabecalho buscarComDocumentos(Long id) {
        return repository.buscarComDocumentos(List.of(id)).stream().findFirst()
                .orElseThrow(() -> NotFoundException.of("MDF-e", id));
    }

    private String truncar(String texto, int max) {
        if (texto == null) {
            return null;
        }
        return texto.length() <= max ? texto : texto.substring(0, max);
    }

    private String extrair(String texto, String chave, int maxLength) {
        if (texto == null) {
            return null;
        }
        var matcher = java.util.regex.Pattern.compile("(?im)^\\s*" + chave + "\\s*=\\s*(.+?)\\s*$").matcher(texto);
        return matcher.find() ? truncar(matcher.group(1), maxLength) : null;
    }
}
