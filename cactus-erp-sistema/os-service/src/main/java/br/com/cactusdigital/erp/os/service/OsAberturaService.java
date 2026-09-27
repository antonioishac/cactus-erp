package br.com.cactusdigital.erp.os.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.os.dto.OsAberturaEquipamentoRequest;
import br.com.cactusdigital.erp.os.dto.OsAberturaEquipamentoResponse;
import br.com.cactusdigital.erp.os.dto.OsAberturaRequest;
import br.com.cactusdigital.erp.os.dto.OsAberturaResponse;
import br.com.cactusdigital.erp.os.dto.OsEvolucaoRequest;
import br.com.cactusdigital.erp.os.dto.OsEvolucaoResponse;
import br.com.cactusdigital.erp.os.dto.OsProdutoServicoRequest;
import br.com.cactusdigital.erp.os.dto.OsProdutoServicoResponse;
import br.com.cactusdigital.erp.os.entity.OsAbertura;
import br.com.cactusdigital.erp.os.entity.OsAberturaEquipamento;
import br.com.cactusdigital.erp.os.entity.OsEquipamento;
import br.com.cactusdigital.erp.os.entity.OsEvolucao;
import br.com.cactusdigital.erp.os.entity.OsProdutoServico;
import br.com.cactusdigital.erp.os.entity.OsStatus;
import br.com.cactusdigital.erp.os.entity.TipoItemOs;
import br.com.cactusdigital.erp.os.exception.OsErrorCode;
import br.com.cactusdigital.erp.os.mapper.OsMapper;
import br.com.cactusdigital.erp.os.repository.OsAberturaEquipamentoRepository;
import br.com.cactusdigital.erp.os.repository.OsAberturaRepository;
import br.com.cactusdigital.erp.os.repository.OsEvolucaoRepository;
import br.com.cactusdigital.erp.os.repository.OsProdutoServicoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ordem de Serviço — sem máquina de estados rígida (diferente de Venda): {@code OsStatus}
 * é um catálogo livre (ver Javadoc de {@link OsStatus}), então trocar de status é uma
 * ação simples (só valida que o status existe), não uma transição fixa RASCUNHO→X→Y.
 * {@code valorTotal} é recalculado a partir dos itens a cada adição/remoção, mesmo
 * padrão de {@code VendaService}/{@code VendaOrcamentoService}.
 */
@Service
@RequiredArgsConstructor
public class OsAberturaService {

    /** {@code LocalTime.toString()} omite segundos quando são zero (ex: "14:30" em vez
     * de "14:30:00") — achado real ao escrever este método: um {@code substring(0,8)}
     * ingênuo em cima disso quebraria com {@code StringIndexOutOfBoundsException}
     * sempre que o registro caísse num horário "redondo". Formatador explícito evita isso. */
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final OsAberturaRepository repository;
    private final OsAberturaEquipamentoRepository equipamentoVinculoRepository;
    private final OsProdutoServicoRepository itemRepository;
    private final OsEvolucaoRepository evolucaoRepository;
    private final OsStatusService osStatusService;
    private final OsEquipamentoService osEquipamentoService;
    private final OsMapper mapper;

    @Transactional(readOnly = true)
    public Page<OsAberturaResponse> listar(Long clienteId, Pageable pageable) {
        Page<OsAbertura> pagina = clienteId != null
                ? repository.findByClienteId(clienteId, pageable)
                : repository.findAll(pageable);
        hidratarColecoes(pagina);
        return pagina.map(mapper::toResponse);
    }

    /** 3 coleções irmãs (equipamentos/itens/evolucoes) — nenhuma combinação cabe numa
     * {@code JOIN FETCH} só, ver Javadoc de {@code OsAberturaRepository#hidratarEquipamentos}. */
    private void hidratarColecoes(Page<OsAbertura> pagina) {
        if (pagina.isEmpty()) {
            return;
        }
        List<Long> ids = pagina.getContent().stream().map(OsAbertura::getId).toList();
        repository.hidratarEquipamentos(ids);
        repository.hidratarItens(ids);
        repository.hidratarEvolucoes(ids);
    }

    @Transactional(readOnly = true)
    public OsAberturaResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public OsAberturaResponse criar(OsAberturaRequest request) {
        OsStatus status = osStatusService.buscarEntidade(request.osStatusId());
        OsAbertura osAbertura = OsAbertura.builder()
                .osStatus(status)
                .empresaId(request.empresaId())
                .clienteId(request.clienteId())
                .colaboradorId(request.colaboradorId())
                .numero(request.numero())
                .dataInicio(request.dataInicio() != null ? request.dataInicio() : LocalDate.now())
                .horaInicio(request.horaInicio())
                .dataPrevisao(request.dataPrevisao())
                .horaPrevisao(request.horaPrevisao())
                .nomeContato(request.nomeContato())
                .foneContato(request.foneContato())
                .observacaoCliente(request.observacaoCliente())
                .observacaoAbertura(request.observacaoAbertura())
                .build();
        return mapper.toResponse(repository.save(osAbertura));
    }

    @Transactional
    public OsAberturaResponse trocarStatus(Long id, Long osStatusId) {
        OsAbertura osAbertura = buscarEntidade(id);
        osAbertura.setOsStatus(osStatusService.buscarEntidade(osStatusId));
        return mapper.toResponse(osAbertura);
    }

    @Transactional
    public OsAberturaResponse aprovarOrcamento(Long id) {
        OsAbertura osAbertura = buscarEntidade(id);
        if (osAbertura.isOrcamentoAprovado()) {
            throw new ConflictException(OsErrorCode.ORCAMENTO_JA_APROVADO);
        }
        osAbertura.setOrcamentoAprovado(true);
        osAbertura.setDataAprovacaoOrcamento(Instant.now());
        return mapper.toResponse(osAbertura);
    }

    @Transactional
    public OsAberturaResponse encerrar(Long id) {
        OsAbertura osAbertura = buscarEntidade(id);
        osAbertura.setDataFim(LocalDate.now());
        return mapper.toResponse(osAbertura);
    }

    @Transactional
    public OsAberturaEquipamentoResponse adicionarEquipamento(Long osAberturaId, OsAberturaEquipamentoRequest request) {
        OsAbertura osAbertura = buscarEntidade(osAberturaId);
        OsEquipamento equipamento = osEquipamentoService.buscarEntidade(request.osEquipamentoId());
        OsAberturaEquipamento vinculo = equipamentoVinculoRepository.save(OsAberturaEquipamento.builder()
                .osAbertura(osAbertura)
                .osEquipamento(equipamento)
                .numeroSerie(request.numeroSerie())
                .tipoCobertura(request.tipoCobertura())
                .build());
        osAbertura.getEquipamentos().add(vinculo);
        return mapper.toResponse(vinculo);
    }

    @Transactional
    public void removerEquipamento(Long osAberturaId, Long vinculoId) {
        OsAberturaEquipamento vinculo = equipamentoVinculoRepository.findById(vinculoId)
                .filter(candidato -> candidato.getOsAbertura().getId().equals(osAberturaId))
                .orElseThrow(() -> NotFoundException.of("Equipamento da OS", vinculoId));
        equipamentoVinculoRepository.delete(vinculo);
    }

    @Transactional
    public OsProdutoServicoResponse adicionarItem(Long osAberturaId, OsProdutoServicoRequest request) {
        OsAbertura osAbertura = buscarEntidade(osAberturaId);
        if (request.tipo() == TipoItemOs.PRODUTO && request.produtoId() == null) {
            throw new BusinessException(OsErrorCode.ITEM_PRODUTO_SEM_PRODUTO_ID);
        }

        BigDecimal subtotal = request.quantidade().multiply(request.valorUnitario());
        BigDecimal desconto = calcularValorDesconto(subtotal, request.taxaDesconto());

        OsProdutoServico item = itemRepository.save(OsProdutoServico.builder()
                .osAbertura(osAbertura)
                .tipo(request.tipo())
                .produtoId(request.produtoId())
                .complemento(request.complemento())
                .quantidade(request.quantidade())
                .valorUnitario(request.valorUnitario())
                .taxaDesconto(request.taxaDesconto())
                .valorSubtotal(subtotal)
                .valorDesconto(desconto)
                .valorTotal(subtotal.subtract(desconto))
                .build());
        osAbertura.getItens().add(item);
        recalcularTotal(osAbertura);
        return mapper.toResponse(item);
    }

    @Transactional
    public void removerItem(Long osAberturaId, Long itemId) {
        OsAbertura osAbertura = buscarEntidade(osAberturaId);
        OsProdutoServico item = itemRepository.findById(itemId)
                .filter(candidato -> candidato.getOsAbertura().getId().equals(osAberturaId))
                .orElseThrow(() -> NotFoundException.of("Item da OS", itemId));
        osAbertura.getItens().remove(item);
        itemRepository.delete(item);
        recalcularTotal(osAbertura);
    }

    @Transactional
    public OsEvolucaoResponse adicionarEvolucao(Long osAberturaId, OsEvolucaoRequest request) {
        OsAbertura osAbertura = buscarEntidade(osAberturaId);
        OsEvolucao evolucao = evolucaoRepository.save(OsEvolucao.builder()
                .osAbertura(osAbertura)
                .dataRegistro(LocalDate.now())
                .horaRegistro(LocalTime.now().format(FORMATO_HORA))
                .observacao(request.observacao())
                .enviarEmail(request.enviarEmail())
                .build());
        osAbertura.getEvolucoes().add(evolucao);
        return mapper.toResponse(evolucao);
    }

    private void recalcularTotal(OsAbertura osAbertura) {
        BigDecimal total = osAbertura.getItens().stream()
                .map(OsProdutoServico::getValorTotal)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        osAbertura.setValorTotal(total);
    }

    private BigDecimal calcularValorDesconto(BigDecimal valorSubtotal, BigDecimal taxaDesconto) {
        if (taxaDesconto == null || taxaDesconto.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return valorSubtotal.multiply(taxaDesconto).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
    }

    OsAbertura buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Ordem de serviço", id));
    }
}
