package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.EmpresaConfiguracaoFiscalRequest;
import br.com.cactusdigital.erp.cadastro.dto.EmpresaConfiguracaoFiscalResponse;
import br.com.cactusdigital.erp.cadastro.dto.NumeroFiscalPuladoRequest;
import br.com.cactusdigital.erp.cadastro.dto.NumeroFiscalPuladoResponse;
import br.com.cactusdigital.erp.cadastro.entity.Empresa;
import br.com.cactusdigital.erp.cadastro.entity.EmpresaConfiguracaoFiscal;
import br.com.cactusdigital.erp.cadastro.entity.NumeroFiscalPulado;
import br.com.cactusdigital.erp.cadastro.exception.CadastroErrorCode;
import br.com.cactusdigital.erp.cadastro.mapper.EmpresaMapper;
import br.com.cactusdigital.erp.cadastro.repository.EmpresaConfiguracaoFiscalRepository;
import br.com.cactusdigital.erp.cadastro.repository.EmpresaRepository;
import br.com.cactusdigital.erp.cadastro.repository.NumeroFiscalPuladoRepository;
import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Config fiscal 1:1 por empresa — cria na primeira chamada de {@link #salvar}, atualiza depois (upsert). */
@Service
@RequiredArgsConstructor
public class EmpresaConfiguracaoFiscalService {

    private final EmpresaConfiguracaoFiscalRepository repository;
    private final EmpresaRepository empresaRepository;
    private final NumeroFiscalPuladoRepository numeroFiscalPuladoRepository;
    private final EmpresaMapper mapper;

    @Transactional(readOnly = true)
    public EmpresaConfiguracaoFiscalResponse buscarPorEmpresa(Long empresaId) {
        return repository.findByEmpresaId(empresaId)
                .map(mapper::toResponse)
                .orElseThrow(() -> NotFoundException.of("Configuração fiscal da empresa", empresaId));
    }

    @Transactional
    public EmpresaConfiguracaoFiscalResponse salvar(Long empresaId, EmpresaConfiguracaoFiscalRequest request) {
        EmpresaConfiguracaoFiscal entidade = repository.findByEmpresaId(empresaId).orElseGet(() -> {
            Empresa empresa = empresaRepository.findById(empresaId)
                    .orElseThrow(() -> NotFoundException.of("Empresa", empresaId));
            return EmpresaConfiguracaoFiscal.builder().empresa(empresa).build();
        });

        mapper.atualizar(request, entidade);
        return mapper.toResponse(repository.save(entidade));
    }

    /**
     * Reserva o próximo número de NF-e da série configurada — devolve o número
     * corrente (o que a chamada acabou de reservar) e incrementa o contador pro
     * próximo, atômico dentro da mesma transação. Consumido pelo {@code fiscal-service}
     * (Fase 8) ao compor uma NF-e a partir de uma Venda.
     */
    @Transactional
    public EmpresaConfiguracaoFiscalResponse reservarProximoNumeroNfe(Long empresaId) {
        EmpresaConfiguracaoFiscal entidade = repository.findByEmpresaId(empresaId)
                .orElseThrow(() -> NotFoundException.of("Configuração fiscal da empresa", empresaId));
        if (entidade.getSerieNfe() == null || entidade.getProximoNumeroNfe() == null) {
            throw new BusinessException(CadastroErrorCode.CONFIGURACAO_FISCAL_INCOMPLETA);
        }

        Long numeroReservado = entidade.getProximoNumeroNfe();
        entidade.setProximoNumeroNfe(numeroReservado + 1);
        EmpresaConfiguracaoFiscalResponse resposta = mapper.toResponse(repository.save(entidade));
        // Devolve o número JÁ reservado (antes do incremento), não o novo próximo.
        return new EmpresaConfiguracaoFiscalResponse(
                resposta.id(), resposta.empresaId(), resposta.tipoCertificado(), resposta.certificadoAlias(),
                resposta.certificadoValidade(), resposta.ambienteFiscal(), resposta.serieNfe(), numeroReservado,
                resposta.serieNfce(), resposta.proximoNumeroNfce(), resposta.serieNfse(), resposta.proximoNumeroNfse(),
                resposta.serieCte(), resposta.proximoNumeroCte(), resposta.serieMdfe(), resposta.proximoNumeroMdfe(),
                resposta.csc(), resposta.cscIdToken());
    }

    /**
     * Reserva o próximo número de CT-e da série configurada — mesmo padrão exato de
     * {@link #reservarProximoNumeroNfe}. Consumido pelo {@code fiscal-service} (Fase 11)
     * ao compor um CT-e a partir do frete de uma Venda.
     */
    @Transactional
    public EmpresaConfiguracaoFiscalResponse reservarProximoNumeroCte(Long empresaId) {
        EmpresaConfiguracaoFiscal entidade = repository.findByEmpresaId(empresaId)
                .orElseThrow(() -> NotFoundException.of("Configuração fiscal da empresa", empresaId));
        if (entidade.getSerieCte() == null || entidade.getProximoNumeroCte() == null) {
            throw new BusinessException(CadastroErrorCode.CONFIGURACAO_FISCAL_INCOMPLETA);
        }

        Long numeroReservado = entidade.getProximoNumeroCte();
        entidade.setProximoNumeroCte(numeroReservado + 1);
        EmpresaConfiguracaoFiscalResponse resposta = mapper.toResponse(repository.save(entidade));
        return new EmpresaConfiguracaoFiscalResponse(
                resposta.id(), resposta.empresaId(), resposta.tipoCertificado(), resposta.certificadoAlias(),
                resposta.certificadoValidade(), resposta.ambienteFiscal(), resposta.serieNfe(), resposta.proximoNumeroNfe(),
                resposta.serieNfce(), resposta.proximoNumeroNfce(), resposta.serieNfse(), resposta.proximoNumeroNfse(),
                resposta.serieCte(), numeroReservado, resposta.serieMdfe(), resposta.proximoNumeroMdfe(),
                resposta.csc(), resposta.cscIdToken());
    }

    /**
     * Reserva o próximo número de NFC-e da série configurada — mesmo padrão exato de
     * {@link #reservarProximoNumeroNfe}. Consumido pelo {@code fiscal-service} (Fase 13)
     * ao compor uma NFC-e a partir de um Pedido do {@code loja-virtual-service}.
     */
    @Transactional
    public EmpresaConfiguracaoFiscalResponse reservarProximoNumeroNfce(Long empresaId) {
        EmpresaConfiguracaoFiscal entidade = repository.findByEmpresaId(empresaId)
                .orElseThrow(() -> NotFoundException.of("Configuração fiscal da empresa", empresaId));
        if (entidade.getSerieNfce() == null || entidade.getProximoNumeroNfce() == null) {
            throw new BusinessException(CadastroErrorCode.CONFIGURACAO_FISCAL_INCOMPLETA);
        }

        Long numeroReservado = entidade.getProximoNumeroNfce();
        entidade.setProximoNumeroNfce(numeroReservado + 1);
        EmpresaConfiguracaoFiscalResponse resposta = mapper.toResponse(repository.save(entidade));
        return new EmpresaConfiguracaoFiscalResponse(
                resposta.id(), resposta.empresaId(), resposta.tipoCertificado(), resposta.certificadoAlias(),
                resposta.certificadoValidade(), resposta.ambienteFiscal(), resposta.serieNfe(), resposta.proximoNumeroNfe(),
                resposta.serieNfce(), numeroReservado, resposta.serieNfse(), resposta.proximoNumeroNfse(),
                resposta.serieCte(), resposta.proximoNumeroCte(), resposta.serieMdfe(), resposta.proximoNumeroMdfe(),
                resposta.csc(), resposta.cscIdToken());
    }

    /**
     * Reserva o próximo número de MDF-e da série configurada — mesmo padrão exato de
     * {@link #reservarProximoNumeroNfe}. Consumido pelo {@code fiscal-service} (Fase 16)
     * ao compor um MDF-e a partir de N CT-e já autorizados.
     */
    @Transactional
    public EmpresaConfiguracaoFiscalResponse reservarProximoNumeroMdfe(Long empresaId) {
        EmpresaConfiguracaoFiscal entidade = repository.findByEmpresaId(empresaId)
                .orElseThrow(() -> NotFoundException.of("Configuração fiscal da empresa", empresaId));
        if (entidade.getSerieMdfe() == null || entidade.getProximoNumeroMdfe() == null) {
            throw new BusinessException(CadastroErrorCode.CONFIGURACAO_FISCAL_INCOMPLETA);
        }

        Long numeroReservado = entidade.getProximoNumeroMdfe();
        entidade.setProximoNumeroMdfe(numeroReservado + 1);
        EmpresaConfiguracaoFiscalResponse resposta = mapper.toResponse(repository.save(entidade));
        return new EmpresaConfiguracaoFiscalResponse(
                resposta.id(), resposta.empresaId(), resposta.tipoCertificado(), resposta.certificadoAlias(),
                resposta.certificadoValidade(), resposta.ambienteFiscal(), resposta.serieNfe(), resposta.proximoNumeroNfe(),
                resposta.serieNfce(), resposta.proximoNumeroNfce(), resposta.serieNfse(), resposta.proximoNumeroNfse(),
                resposta.serieCte(), resposta.proximoNumeroCte(), resposta.serieMdfe(), numeroReservado,
                resposta.csc(), resposta.cscIdToken());
    }

    /**
     * Registra que um número já reservado (via {@code reservarProximoNumeroXxx}) nunca
     * chegou a virar um documento fiscal de verdade — chamado como compensação de saga
     * pelo {@code fiscal-service} (ver Javadoc de {@link NumeroFiscalPulado} pro porquê
     * de NÃO devolver/decrementar o contador). Puramente aditivo — nunca falha por
     * "duplicidade" (o mesmo número pode legitimamente aparecer aqui mais de uma vez se
     * tentativas repetidas de gerar o mesmo documento falharem todas).
     */
    @Transactional
    public NumeroFiscalPuladoResponse registrarNumeroPulado(Long empresaId, NumeroFiscalPuladoRequest request) {
        if (!empresaRepository.existsById(empresaId)) {
            throw NotFoundException.of("Empresa", empresaId);
        }
        NumeroFiscalPulado entidade = mapper.toEntity(request);
        entidade.setEmpresaId(empresaId);
        return mapper.toResponse(numeroFiscalPuladoRepository.save(entidade));
    }

    @Transactional(readOnly = true)
    public Page<NumeroFiscalPuladoResponse> listarNumerosPulados(Long empresaId, Pageable pageable) {
        return numeroFiscalPuladoRepository.findByEmpresaId(empresaId, pageable).map(mapper::toResponse);
    }
}
