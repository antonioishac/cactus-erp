package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.financeiro.dto.AplicacaoFinanceiraRequest;
import br.com.cactusdigital.erp.financeiro.dto.AplicacaoFinanceiraResponse;
import br.com.cactusdigital.erp.financeiro.dto.ResgatarAplicacaoRequest;
import br.com.cactusdigital.erp.financeiro.entity.AplicacaoFinanceira;
import br.com.cactusdigital.erp.financeiro.entity.OrigemMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.entity.SituacaoAplicacaoFinanceira;
import br.com.cactusdigital.erp.financeiro.entity.TipoMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.exception.FinanceiroErrorCode;
import br.com.cactusdigital.erp.financeiro.mapper.CaixaBancoMapper;
import br.com.cactusdigital.erp.financeiro.repository.AplicacaoFinanceiraRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aplicação financeira de curto prazo — ver Javadoc de {@link AplicacaoFinanceira}.
 * {@link #aplicar} debita a conta corrente na hora; {@link #resgatar} credita de volta,
 * podendo ser maior ou menor que o valor aplicado (rendimento positivo ou negativo).
 */
@Service
@RequiredArgsConstructor
public class AplicacaoFinanceiraService {

    private final AplicacaoFinanceiraRepository repository;
    private final MovimentoCaixaBancoService movimentoService;
    private final CaixaBancoMapper mapper;

    @Transactional(readOnly = true)
    public Page<AplicacaoFinanceiraResponse> listar(Long contaBancariaId, Pageable pageable) {
        Page<AplicacaoFinanceira> pagina = contaBancariaId != null
                ? repository.findByContaBancariaId(contaBancariaId, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public AplicacaoFinanceiraResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public AplicacaoFinanceiraResponse aplicar(AplicacaoFinanceiraRequest request) {
        LocalDate data = request.dataAplicacao() != null ? request.dataAplicacao() : LocalDate.now();
        AplicacaoFinanceira aplicacao = repository.save(AplicacaoFinanceira.builder()
                .contaBancariaId(request.contaBancariaId())
                .tipoAplicacao(request.tipoAplicacao())
                .descricao(request.descricao())
                .dataAplicacao(data)
                .valorAplicado(request.valorAplicado())
                .build());

        movimentoService.registrar(request.contaBancariaId(), TipoMovimentoCaixa.SAIDA, OrigemMovimentoCaixa.APLICACAO_FINANCEIRA,
                aplicacao.getId(), request.valorAplicado(), data,
                "Aplicação financeira " + request.tipoAplicacao() + " #" + aplicacao.getId(), null);

        return mapper.toResponse(aplicacao);
    }

    @Transactional
    public AplicacaoFinanceiraResponse resgatar(Long id, ResgatarAplicacaoRequest request) {
        AplicacaoFinanceira aplicacao = buscarEntidade(id);
        if (aplicacao.getSituacao() == SituacaoAplicacaoFinanceira.RESGATADA) {
            throw new ConflictException(FinanceiroErrorCode.APLICACAO_JA_RESGATADA);
        }

        LocalDate data = request.dataResgate() != null ? request.dataResgate() : LocalDate.now();
        movimentoService.registrar(aplicacao.getContaBancariaId(), TipoMovimentoCaixa.ENTRADA, OrigemMovimentoCaixa.RESGATE_APLICACAO,
                aplicacao.getId(), request.valorResgatado(), data,
                "Resgate aplicação financeira " + aplicacao.getTipoAplicacao() + " #" + aplicacao.getId(), null);

        aplicacao.setDataResgate(data);
        aplicacao.setValorResgatado(request.valorResgatado());
        aplicacao.setRendimento(request.valorResgatado().subtract(aplicacao.getValorAplicado()));
        aplicacao.setSituacao(SituacaoAplicacaoFinanceira.RESGATADA);

        return mapper.toResponse(aplicacao);
    }

    private AplicacaoFinanceira buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Aplicação financeira", id));
    }
}
