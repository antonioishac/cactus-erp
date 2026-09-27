package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.*;
import br.com.cactuserp.pdv.repository.ContaAbertaRepository;
import br.com.cactuserp.pdv.repository.LojaConfiguracaoRepository;
import br.com.cactuserp.pdv.repository.MesaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de gestão de contas abertas (mesas, comandas, delivery).
 *
 * <p>Esta classe é a fachada principal do módulo food/comanda no PDV.
 * Reúne as operações comuns: abrir, lançar item, cancelar item, listar,
 * fechar (sem ainda integrar com cupom — essa parte vem na Fase 1.2).
 *
 * <p><b>Escopo desta Fase 1.0:</b> backend completo (entidades + service
 * funcional). Telas e integração com cupom virão nas próximas sub-fases.
 */
@Service
@Transactional(readOnly = true)
public class ContaAbertaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContaAbertaService.class);
    private static final DateTimeFormatter HORA_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final ContaAbertaRepository contaRepository;
    private final MesaRepository mesaRepository;
    private final LojaConfiguracaoRepository lojaConfigRepository;

    public ContaAbertaService(ContaAbertaRepository contaRepository,
                              MesaRepository mesaRepository,
                              LojaConfiguracaoRepository lojaConfigRepository) {
        this.contaRepository = contaRepository;
        this.mesaRepository = mesaRepository;
        this.lojaConfigRepository = lojaConfigRepository;
    }

    // ====================================================================
    //  Configuração
    // ====================================================================

    public LojaConfiguracao obterConfiguracao() {
        return lojaConfigRepository.buscarConfiguracao()
                .orElseThrow(() -> new IllegalStateException(
                        "Configuração da loja não encontrada. "
                                + "Execute o seed inicial."));
    }

    // ====================================================================
    //  Listagens
    // ====================================================================

    /** Mesas cadastradas e ativas (cadastro do back-office). */
    public List<Mesa> listarMesasAtivas() {
        return mesaRepository.findBySituacaoOrderByNumeroAsc("A");
    }

    /** Contas ativas — qualquer tipo. Útil pra busca geral no PDV. */
    public List<ContaAberta> listarContasAtivas() {
        return contaRepository.listarAtivas();
    }

    /** Contas ativas de um tipo específico (mesa/comanda/delivery). */
    public List<ContaAberta> listarContasAtivasPorTipo(TipoContaAberta tipo) {
        return contaRepository.listarAtivasPorTipo(tipo);
    }

    /** Conta ativa de uma mesa específica (vazio se mesa está livre). */
    public Optional<ContaAberta> buscarContaAtivaPorMesa(Mesa mesa) {
        return contaRepository.buscarContaAtivaPorMesa(mesa);
    }

    /** Conta ativa por código de comanda. */
    public Optional<ContaAberta> buscarContaAtivaPorComanda(String codigoComanda) {
        if (codigoComanda == null || codigoComanda.isBlank()) {
            return Optional.empty();
        }
        return contaRepository.buscarContaAtivaPorComanda(codigoComanda.trim());
    }

    /** Conta completa com itens carregados (pra tela de detalhe). */
    public Optional<ContaAberta> buscarComItens(Long idConta) {
        return contaRepository.buscarComItens(idConta);
    }

    // ====================================================================
    //  Abertura de conta
    // ====================================================================

    /**
     * Abre uma conta de MESA. Lança erro se a mesa já tiver conta ativa.
     */
    @Transactional
    public ContaAberta abrirContaMesa(Integer numeroMesa, PdvOperador operadorAbertura) {
        if (operadorAbertura == null) {
            throw new IllegalArgumentException("Operador é obrigatório.");
        }
        if (numeroMesa == null) {
            throw new IllegalArgumentException("Número da mesa é obrigatório.");
        }

        LojaConfiguracao conf = obterConfiguracao();
        if (!conf.isPermiteMesas()) {
            throw new IllegalStateException(
                    "Esta loja não tem mesas habilitadas na configuração.");
        }

        // Procura a mesa. Se não achar e permiteMesaAvulsa=N, erro.
        // Se permiteMesaAvulsa=S, mesa "avulsa" = sem cadastro
        // (mas nesse caso não vincula a Mesa; só usa o número como referência
        //  informativa via observacao).
        Optional<Mesa> mesaOpt = mesaRepository
                .findFirstByNumeroAndSituacao(numeroMesa, "A");

        Mesa mesa = null;
        if (mesaOpt.isPresent()) {
            mesa = mesaOpt.get();
            // Já tem conta ativa nessa mesa?
            if (contaRepository.buscarContaAtivaPorMesa(mesa).isPresent()) {
                throw new IllegalStateException(
                        "Mesa " + numeroMesa + " já está ocupada.");
            }
        } else if (!conf.isPermiteMesaAvulsa()) {
            throw new IllegalArgumentException(
                    "Mesa " + numeroMesa + " não cadastrada. "
                            + "Esta loja não permite mesas avulsas.");
        }

        ContaAberta conta = new ContaAberta();
        conta.setTipo(TipoContaAberta.M);
        conta.setStatus(StatusContaAberta.A);
        conta.setMesa(mesa);
        conta.setOperadorAbertura(operadorAbertura);
        conta.setDataAbertura(LocalDate.now());
        conta.setHoraAbertura(LocalTime.now().format(HORA_FMT));

        if (mesa == null) {
            conta.setObservacao("Mesa avulsa #" + numeroMesa
                    + " (não cadastrada)");
        }

        ContaAberta salva = contaRepository.save(conta);
        LOGGER.info("Conta de mesa #{} aberta (id={}, operador={})",
                numeroMesa, salva.getId(),
                operadorAbertura.getColaborador().getNome());
        return salva;
    }

    /**
     * Abre uma conta de COMANDA.
     *
     * @param codigoComanda código físico digitado pelo operador (modo F),
     *                      OU null pra gerar virtual (modo V)
     */
    @Transactional
    public ContaAberta abrirContaComanda(String codigoComanda,
                                         PdvOperador operadorAbertura) {
        if (operadorAbertura == null) {
            throw new IllegalArgumentException("Operador é obrigatório.");
        }

        LojaConfiguracao conf = obterConfiguracao();
        if (!conf.isPermiteComandas()) {
            throw new IllegalStateException(
                    "Esta loja não tem comandas habilitadas na configuração.");
        }

        String codigoFinal;
        if (codigoComanda != null && !codigoComanda.isBlank()) {
            // Físico (ou virtual digitado manualmente)
            codigoFinal = codigoComanda.trim();
            if (contaRepository.buscarContaAtivaPorComanda(codigoFinal).isPresent()) {
                throw new IllegalStateException(
                        "Comanda '" + codigoFinal + "' já está em uso.");
            }
        } else {
            if (!conf.isComandaVirtual()) {
                throw new IllegalArgumentException(
                        "Informe o código da comanda física.");
            }
            // Gera código virtual: timestamp curto. Posteriormente
            // pode evoluir pra sequência por loja.
            codigoFinal = "V" + System.currentTimeMillis();
        }

        ContaAberta conta = new ContaAberta();
        conta.setTipo(TipoContaAberta.C);
        conta.setStatus(StatusContaAberta.A);
        conta.setCodigoComanda(codigoFinal);
        conta.setOperadorAbertura(operadorAbertura);
        conta.setDataAbertura(LocalDate.now());
        conta.setHoraAbertura(LocalTime.now().format(HORA_FMT));

        ContaAberta salva = contaRepository.save(conta);
        LOGGER.info("Comanda '{}' aberta (id={}, operador={})",
                codigoFinal, salva.getId(),
                operadorAbertura.getColaborador().getNome());
        return salva;
    }

    /**
     * Abre uma conta de DELIVERY. Endereço de entrega é obrigatório.
     */
    @Transactional
    public ContaAberta abrirContaDelivery(PdvOperador operadorAbertura,
                                          Cliente cliente,
                                          String nomeAvulso,
                                          String enderecoEntrega,
                                          String numeroEntrega,
                                          String complementoEntrega,
                                          String bairroEntrega,
                                          String cidadeEntrega,
                                          String ufEntrega,
                                          String cepEntrega,
                                          String telefoneContato,
                                          BigDecimal valorTaxaEntrega) {
        if (operadorAbertura == null) {
            throw new IllegalArgumentException("Operador é obrigatório.");
        }
        if (enderecoEntrega == null || enderecoEntrega.isBlank()) {
            throw new IllegalArgumentException(
                    "Endereço de entrega é obrigatório no delivery.");
        }

        LojaConfiguracao conf = obterConfiguracao();
        if (!conf.isPermiteDelivery()) {
            throw new IllegalStateException(
                    "Esta loja não tem delivery habilitado na configuração.");
        }

        ContaAberta conta = new ContaAberta();
        conta.setTipo(TipoContaAberta.D);
        conta.setStatus(StatusContaAberta.A);
        conta.setOperadorAbertura(operadorAbertura);
        conta.setDataAbertura(LocalDate.now());
        conta.setHoraAbertura(LocalTime.now().format(HORA_FMT));

        // Numero do pedido = timestamp simples por enquanto.
        // Posteriormente pode evoluir pra sequência (PED-0001).
        conta.setNumeroPedido("P" + System.currentTimeMillis());

        conta.setCliente(cliente);
        conta.setNomeClienteAvulso(nomeAvulso);

        conta.setEnderecoEntrega(enderecoEntrega);
        conta.setNumeroEntrega(numeroEntrega);
        conta.setComplementoEntrega(complementoEntrega);
        conta.setBairroEntrega(bairroEntrega);
        conta.setCidadeEntrega(cidadeEntrega);
        conta.setUfEntrega(ufEntrega);
        conta.setCepEntrega(cepEntrega);
        conta.setTelefoneContato(telefoneContato);

        conta.setValorTaxaEntrega(valorTaxaEntrega != null
                ? valorTaxaEntrega : BigDecimal.ZERO);

        ContaAberta salva = contaRepository.save(conta);
        LOGGER.info("Pedido delivery '{}' aberto (id={}, operador={})",
                salva.getNumeroPedido(), salva.getId(),
                operadorAbertura.getColaborador().getNome());
        return salva;
    }

    // ====================================================================
    //  Itens
    // ====================================================================

    /**
     * Lança um item na conta. Atualiza o total.
     *
     * @param conta            conta ativa
     * @param produto          produto cadastrado
     * @param quantidade       quantidade (deve ser positiva)
     * @param valorUnitario    preço unitário (default = produto.valorVenda)
     * @param observacao       opcional ("sem cebola", "bem passado", etc)
     * @param operadorLancamento quem lançou
     */
    @Transactional
    public ContaAbertaItem lancarItem(ContaAberta conta,
                                      Produto produto,
                                      BigDecimal quantidade,
                                      BigDecimal valorUnitario,
                                      String observacao,
                                      PdvOperador operadorLancamento) {
        if (conta == null) throw new IllegalArgumentException("Conta obrigatória.");
        if (produto == null) throw new IllegalArgumentException("Produto obrigatório.");
        if (operadorLancamento == null) throw new IllegalArgumentException("Operador obrigatório.");
        if (quantidade == null || quantidade.signum() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }
        if (!conta.estaAberta()) {
            throw new IllegalStateException(
                    "Conta não está aberta para receber itens (status: "
                            + conta.getStatus() + ").");
        }

        if (valorUnitario == null) {
            valorUnitario = produto.getValorVenda() != null
                    ? produto.getValorVenda() : BigDecimal.ZERO;
        }

        BigDecimal total = valorUnitario.multiply(quantidade)
                .setScale(2, RoundingMode.HALF_UP);

        // Sequência: próximo item da conta
        int proximo = conta.getItens().size() + 1;

        ContaAbertaItem item = new ContaAbertaItem();
        item.setProduto(produto);
        item.setItem(proximo);
        item.setGtin(produto.getGtin());
        item.setDescricao(produto.getDescricaoPdv() != null
                ? produto.getDescricaoPdv() : produto.getDescricao());
        item.setQuantidade(quantidade);
        item.setValorUnitario(valorUnitario);
        item.setValorTotal(total);
        item.setSituacao(SituacaoItemConta.A);
        item.setObservacao(observacao);
        item.setOperadorLancamento(operadorLancamento);
        item.setDataLancamento(LocalDate.now());
        item.setHoraLancamento(LocalTime.now().format(HORA_FMT));

        conta.adicionarItem(item);
        recalcularTotais(conta);
        contaRepository.save(conta);

        LOGGER.info("Item lançado em conta #{}: {} x{} = R$ {}",
                conta.getId(), item.getDescricao(),
                item.getQuantidade(), item.getValorTotal());
        return item;
    }

    /**
     * Cancela um item da conta (situação A → C). Mantém auditoria.
     */
    @Transactional
    public void cancelarItem(ContaAbertaItem item,
                             PdvOperador operadorCancelamento,
                             String motivo) {
        if (item == null) throw new IllegalArgumentException("Item obrigatório.");
        if (operadorCancelamento == null) {
            throw new IllegalArgumentException("Operador obrigatório.");
        }
        if (item.getSituacao() == SituacaoItemConta.C) {
            throw new IllegalStateException("Item já está cancelado.");
        }

        item.setSituacao(SituacaoItemConta.C);
        item.setOperadorCancelamento(operadorCancelamento);
        item.setDataCancelamento(LocalDate.now());
        item.setHoraCancelamento(LocalTime.now().format(HORA_FMT));
        item.setMotivoCancelamento(motivo);

        ContaAberta conta = item.getContaAberta();
        recalcularTotais(conta);
        contaRepository.save(conta);

        LOGGER.info("Item #{} cancelado da conta #{}",
                item.getId(), conta.getId());
    }

    // ====================================================================
    //  Cancelamento / cleanup
    // ====================================================================

    /**
     * Cancela a conta toda (status A/E → X). Bloqueia se já foi paga.
     */
    @Transactional
    public void cancelarConta(ContaAberta conta, PdvOperador operador, String motivo) {
        if (conta == null) throw new IllegalArgumentException("Conta obrigatória.");
        if (conta.getStatus() == StatusContaAberta.F) {
            throw new IllegalStateException(
                    "Não é possível cancelar conta já fechada.");
        }
        if (conta.getStatus() == StatusContaAberta.X) {
            throw new IllegalStateException("Conta já está cancelada.");
        }

        conta.setStatus(StatusContaAberta.X);
        conta.setOperadorFechamento(operador);
        conta.setDataFechamento(LocalDate.now());
        conta.setHoraFechamento(LocalTime.now().format(HORA_FMT));
        if (motivo != null && !motivo.isBlank()) {
            String obs = conta.getObservacao();
            conta.setObservacao((obs != null ? obs + " | " : "")
                    + "Cancelada: " + motivo);
        }
        contaRepository.save(conta);
        LOGGER.info("Conta #{} cancelada por {}",
                conta.getId(),
                operador != null ? operador.getColaborador().getNome() : "?");
    }

    // ====================================================================
    //  Helpers internos
    // ====================================================================

    /**
     * Recalcula valor total da conta a partir dos itens ATIVOS.
     * Soma taxa de entrega (delivery) e aplica desconto/acréscimo
     * da conta inteira (se houver — campos prontos pra Fase 1.x).
     */
    private void recalcularTotais(ContaAberta conta) {
        BigDecimal total = BigDecimal.ZERO;
        for (ContaAbertaItem it : conta.getItens()) {
            if (it.getSituacao() == SituacaoItemConta.A && it.getValorTotal() != null) {
                total = total.add(it.getValorTotal());
            }
        }
        // Aplica desconto/acréscimo se houver
        if (conta.getValorDesconto() != null) total = total.subtract(conta.getValorDesconto());
        if (conta.getValorAcrescimo() != null) total = total.add(conta.getValorAcrescimo());
        if (conta.getValorTaxaEntrega() != null) total = total.add(conta.getValorTaxaEntrega());

        conta.setValorTotal(total.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Atualiza dados editáveis da conta (cliente avulso, observação,
     * cliente cadastrado). Útil quando o operador adiciona/altera essas
     * informações depois da abertura.
     *
     * <p>NÃO altera totais nem itens — só os metadados.
     */
    @Transactional
    public ContaAberta atualizarObservacaoOuCliente(ContaAberta conta) {
        if (conta == null) throw new IllegalArgumentException("Conta obrigatória.");
        return contaRepository.save(conta);
    }

    /**
     * Marca a conta como "em atendimento no caixa" — operador importou
     * pro PDV. Status A → E. Bloqueia novos lançamentos no Salão/Comanda.
     */
    @Transactional
    public ContaAberta marcarEmAtendimento(ContaAberta conta) {
        if (conta == null) throw new IllegalArgumentException("Conta obrigatória.");
        if (conta.getStatus() != StatusContaAberta.A) {
            throw new IllegalStateException(
                    "Apenas contas em status A (Aberta) podem ir para atendimento. "
                            + "Status atual: " + conta.getStatus());
        }
        conta.setStatus(StatusContaAberta.E);
        ContaAberta salva = contaRepository.save(conta);
        LOGGER.info("Conta #{} em atendimento no caixa", salva.getId());
        return salva;
    }

    /**
     * Libera a conta de volta — operador cancelou ou desistiu do
     * pagamento no caixa. Status E → A. Permite que o garçom continue
     * lançando itens.
     */
    @Transactional
    public ContaAberta liberarConta(ContaAberta conta) {
        if (conta == null) throw new IllegalArgumentException("Conta obrigatória.");
        if (conta.getStatus() != StatusContaAberta.E) {
            // Não dá erro pra ser idempotente — se já voltou pra A, ok
            LOGGER.warn("liberarConta chamada em conta #{} status {}, ignorando",
                    conta.getId(), conta.getStatus());
            return conta;
        }
        conta.setStatus(StatusContaAberta.A);
        ContaAberta salva = contaRepository.save(conta);
        LOGGER.info("Conta #{} liberada (voltou para A)", salva.getId());
        return salva;
    }

    /**
     * Vincula a venda concluída à conta. Status E → F.
     * Chamado pelo VendaService.finalizarVenda quando há conta importada.
     *
     * @param conta            conta em atendimento
     * @param idVendaCabecalho id do cupom finalizado
     * @param operadorCaixa    operador que finalizou no caixa
     */
    @Transactional
    public ContaAberta vincularVendaConcluida(ContaAberta conta,
                                              Long idVendaCabecalho,
                                              PdvOperador operadorCaixa) {
        if (conta == null) throw new IllegalArgumentException("Conta obrigatória.");
        if (idVendaCabecalho == null) throw new IllegalArgumentException("idVendaCabecalho obrigatório.");
        if (conta.getStatus() != StatusContaAberta.E) {
            throw new IllegalStateException(
                    "Apenas contas em atendimento (E) podem ser vinculadas. "
                            + "Status atual: " + conta.getStatus());
        }
        conta.setStatus(StatusContaAberta.F);
        conta.setIdPdvVendaCabecalho(idVendaCabecalho);
        conta.setOperadorFechamento(operadorCaixa);
        conta.setDataFechamento(java.time.LocalDate.now());
        conta.setHoraFechamento(java.time.LocalTime.now().format(HORA_FMT));
        ContaAberta salva = contaRepository.save(conta);
        LOGGER.info("Conta #{} fechada e vinculada à venda #{}",
                salva.getId(), idVendaCabecalho);
        return salva;
    }
}