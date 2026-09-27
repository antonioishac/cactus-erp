package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.financeiro.dto.TransferenciaBancariaRequest;
import br.com.cactusdigital.erp.financeiro.dto.TransferenciaBancariaResponse;
import br.com.cactusdigital.erp.financeiro.entity.OrigemMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.entity.TipoMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.entity.TransferenciaBancaria;
import br.com.cactusdigital.erp.financeiro.exception.FinanceiroErrorCode;
import br.com.cactusdigital.erp.financeiro.mapper.CaixaBancoMapper;
import br.com.cactusdigital.erp.financeiro.repository.TransferenciaBancariaRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transferência entre 2 {@code ContaBancaria} da própria empresa — ver Javadoc de
 * {@link TransferenciaBancaria} pra decisão de simplificação (sem estado "em trânsito").
 */
@Service
@RequiredArgsConstructor
public class TransferenciaBancariaService {

    private final TransferenciaBancariaRepository repository;
    private final MovimentoCaixaBancoService movimentoService;
    private final CaixaBancoMapper mapper;

    @Transactional(readOnly = true)
    public TransferenciaBancariaResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Page<TransferenciaBancariaResponse> listar(Long contaBancariaId, Pageable pageable) {
        Page<TransferenciaBancaria> pagina = contaBancariaId != null
                ? repository.findPorConta(contaBancariaId, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    /** Debita a origem e credita o destino atomicamente (mesma transação) — se qualquer
     * lado falhar (ex: conta inativa), a transferência inteira sofre rollback, nenhuma
     * das duas contas fica mexida pela metade. */
    @Transactional
    public TransferenciaBancariaResponse transferir(TransferenciaBancariaRequest request) {
        if (request.contaOrigemId().equals(request.contaDestinoId())) {
            throw new BusinessException(FinanceiroErrorCode.CONTA_ORIGEM_IGUAL_DESTINO);
        }

        LocalDate data = request.dataTransferencia() != null ? request.dataTransferencia() : LocalDate.now();
        TransferenciaBancaria transferencia = repository.save(TransferenciaBancaria.builder()
                .contaOrigemId(request.contaOrigemId())
                .contaDestinoId(request.contaDestinoId())
                .dataTransferencia(data)
                .valor(request.valor())
                .historico(request.historico())
                .usuarioId(request.usuarioId())
                .build());

        String historico = request.historico() != null ? request.historico() : "Transferência #" + transferencia.getId();
        movimentoService.registrar(request.contaOrigemId(), TipoMovimentoCaixa.TRANSFERENCIA_SAIDA, OrigemMovimentoCaixa.TRANSFERENCIA,
                transferencia.getId(), request.valor(), data, historico, request.usuarioId());
        movimentoService.registrar(request.contaDestinoId(), TipoMovimentoCaixa.TRANSFERENCIA_ENTRADA, OrigemMovimentoCaixa.TRANSFERENCIA,
                transferencia.getId(), request.valor(), data, historico, request.usuarioId());

        return mapper.toResponse(transferencia);
    }

    private TransferenciaBancaria buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Transferência bancária", id));
    }
}
