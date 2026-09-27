package br.com.cactusdigital.erp.loja.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.loja.dto.VarianteProdutoLojaRequest;
import br.com.cactusdigital.erp.loja.dto.VarianteProdutoLojaResponse;
import br.com.cactusdigital.erp.loja.entity.AtributoCategoria;
import br.com.cactusdigital.erp.loja.entity.ProdutoLoja;
import br.com.cactusdigital.erp.loja.entity.TipoAtributo;
import br.com.cactusdigital.erp.loja.entity.VarianteAtributoValor;
import br.com.cactusdigital.erp.loja.entity.VarianteProdutoLoja;
import br.com.cactusdigital.erp.loja.exception.LojaVirtualErrorCode;
import br.com.cactusdigital.erp.loja.mapper.LojaVirtualMapper;
import br.com.cactusdigital.erp.loja.repository.VarianteProdutoLojaRepository;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Variante ("SKU") — ver Javadoc de {@link VarianteProdutoLoja}. Dono da validação que
 * faz o modelo Produto+Variante+Atributo funcionar de verdade: a composição de
 * atributos de uma variante precisa (1) só usar atributos da MESMA categoria do produto,
 * (2) só usar atributos com {@code geraVariacao=true}, (3) cobrir todo atributo
 * obrigatório da categoria, (4) respeitar as opções cadastradas quando o atributo é
 * {@code LISTA}.
 * <p>
 * <b>Simplificação registrada</b>: {@code valoresAtributos} só é definido na criação —
 * {@link #atualizar} só toca preço/estoque/peso/situação, não recompõe o SKU (mudar a
 * composição de atributos de uma variante já existente exigiria excluir e recriar).
 */
@Service
@RequiredArgsConstructor
public class VarianteProdutoLojaService {

    private final VarianteProdutoLojaRepository repository;
    private final ProdutoLojaService produtoLojaService;
    private final AtributoCategoriaService atributoCategoriaService;
    private final LojaVirtualMapper mapper;

    @Transactional(readOnly = true)
    public VarianteProdutoLojaResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public VarianteProdutoLojaResponse criar(Long produtoLojaId, VarianteProdutoLojaRequest request) {
        ProdutoLoja produto = produtoLojaService.buscarEntidade(produtoLojaId);
        Long lojaId = produto.getLojaId();
        if (repository.existsByProdutoLoja_LojaIdAndSku(lojaId, request.sku())) {
            throw new ConflictException(LojaVirtualErrorCode.SKU_JA_UTILIZADO);
        }

        List<AtributoCategoria> atributosGeradores = atributoCategoriaService.listarQueGeramVariacao(produto.getCategoriaLoja().getId());
        Set<Long> atributosInformados = new HashSet<>();

        VarianteProdutoLoja variante = VarianteProdutoLoja.builder()
                .produtoLoja(produto)
                .sku(request.sku())
                .gtin(request.gtin())
                .precoVenda(request.precoVenda())
                .precoPromocional(request.precoPromocional())
                .estoqueQuantidade(request.estoqueQuantidade() != null ? request.estoqueQuantidade() : BigDecimal.ZERO)
                .pesoKg(request.pesoKg())
                .ncm(request.ncm())
                .csosn(request.csosn())
                .build();

        if (request.valoresAtributos() != null) {
            for (var valorRequest : request.valoresAtributos()) {
                AtributoCategoria atributo = atributosGeradores.stream()
                        .filter(a -> a.getId().equals(valorRequest.atributoCategoriaId()))
                        .findFirst()
                        .orElseGet(() -> {
                            // Existe, mas não gera variação ou não é da categoria certa — distinguir os dois erros.
                            AtributoCategoria candidato = atributoCategoriaService.buscarEntidade(valorRequest.atributoCategoriaId());
                            if (!candidato.getCategoriaLoja().getId().equals(produto.getCategoriaLoja().getId())) {
                                throw new BusinessException(LojaVirtualErrorCode.ATRIBUTO_CATEGORIA_DIVERGENTE);
                            }
                            throw new BusinessException(LojaVirtualErrorCode.ATRIBUTO_NAO_GERA_VARIACAO);
                        });

                validarOpcaoSeLista(atributo, valorRequest.valor());
                variante.getValoresAtributos().add(VarianteAtributoValor.builder()
                        .varianteProdutoLoja(variante)
                        .atributoCategoria(atributo)
                        .valor(valorRequest.valor())
                        .build());
                atributosInformados.add(atributo.getId());
            }
        }

        for (AtributoCategoria atributo : atributosGeradores) {
            if (atributo.isObrigatorio() && !atributosInformados.contains(atributo.getId())) {
                throw new BusinessException(LojaVirtualErrorCode.ATRIBUTO_OBRIGATORIO_AUSENTE);
            }
        }

        VarianteProdutoLoja salva = repository.save(variante);
        produto.getVariantes().add(salva);
        return mapper.toResponse(salva);
    }

    @Transactional
    public VarianteProdutoLojaResponse atualizar(Long id, VarianteProdutoLojaRequest request) {
        // Lock desde já — este método pode tocar estoqueQuantidade, mesmo critério de
        // concorrência de reservar/liberar (ver Javadoc da entidade).
        VarianteProdutoLoja variante = buscarParaAtualizarEstoque(id);
        if (!variante.getSku().equals(request.sku())
                && repository.existsByProdutoLoja_LojaIdAndSkuAndIdNot(variante.getProdutoLoja().getLojaId(), request.sku(), id)) {
            throw new ConflictException(LojaVirtualErrorCode.SKU_JA_UTILIZADO);
        }
        variante.setSku(request.sku());
        variante.setGtin(request.gtin());
        variante.setPrecoVenda(request.precoVenda());
        variante.setPrecoPromocional(request.precoPromocional());
        if (request.estoqueQuantidade() != null) {
            if (request.estoqueQuantidade().compareTo(variante.getEstoqueReservado()) < 0) {
                throw new BusinessException(LojaVirtualErrorCode.ESTOQUE_MENOR_QUE_RESERVADO);
            }
            variante.setEstoqueQuantidade(request.estoqueQuantidade());
        }
        variante.setPesoKg(request.pesoKg());
        variante.setNcm(request.ncm());
        variante.setCsosn(request.csosn());
        return mapper.toResponse(variante);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    /**
     * Reserva {@code quantidade} pra uma variante — só sucede se
     * {@code estoqueDisponivel} (quantidade − já reservado) comportar o pedido; lock
     * pessimista impede duas chamadas concorrentes de passarem juntas na mesma checagem
     * (ver Javadoc de {@link VarianteProdutoLoja}). Devolve a própria entidade já travada,
     * pra o chamador reaproveitar (preço/nome) sem uma segunda leitura sem lock.
     */
    @Transactional
    VarianteProdutoLoja reservar(Long id, BigDecimal quantidade) {
        VarianteProdutoLoja variante = buscarParaAtualizarEstoque(id);
        if (variante.getEstoqueDisponivel().compareTo(quantidade) < 0) {
            throw new BusinessException(LojaVirtualErrorCode.ESTOQUE_INSUFICIENTE);
        }
        variante.setEstoqueReservado(variante.getEstoqueReservado().add(quantidade));
        return variante;
    }

    /**
     * Ajusta a reserva por um delta (positivo = reservar mais, validado contra
     * disponibilidade; negativo = liberar) — usado quando a quantidade de um item de
     * carrinho já existente é alterada, em vez de remover+readicionar a reserva inteira.
     */
    @Transactional
    void ajustarReserva(Long id, BigDecimal delta) {
        if (delta.signum() == 0) {
            return;
        }
        VarianteProdutoLoja variante = buscarParaAtualizarEstoque(id);
        if (delta.signum() > 0 && variante.getEstoqueDisponivel().compareTo(delta) < 0) {
            throw new BusinessException(LojaVirtualErrorCode.ESTOQUE_INSUFICIENTE);
        }
        variante.setEstoqueReservado(variante.getEstoqueReservado().add(delta));
    }

    /**
     * Libera {@code quantidade} reservada sem tocar o estoque físico — usado quando um
     * item sai do carrinho (removido, carrinho abandonado/expirado) ou um Pedido ainda
     * {@code AGUARDANDO_PAGAMENTO} é cancelado (o estoque físico nunca tinha sido debitado
     * pra esse item, só reservado).
     */
    @Transactional
    void liberarReserva(Long id, BigDecimal quantidade) {
        VarianteProdutoLoja variante = buscarParaAtualizarEstoque(id);
        variante.setEstoqueReservado(variante.getEstoqueReservado().subtract(quantidade));
    }

    /**
     * Debita o estoque físico de verdade e libera a reserva correspondente na mesma
     * chamada — usado só na confirmação de pagamento de um {@code Pedido}
     * ({@code PedidoService#confirmarPagamento}), o único ponto em que a reserva "vira"
     * consumo real.
     */
    @Transactional
    void confirmarConsumo(Long id, BigDecimal quantidade) {
        VarianteProdutoLoja variante = buscarParaAtualizarEstoque(id);
        variante.setEstoqueQuantidade(variante.getEstoqueQuantidade().subtract(quantidade));
        variante.setEstoqueReservado(variante.getEstoqueReservado().subtract(quantidade));
    }

    /**
     * Devolve {@code quantidade} ao estoque físico — usado quando um {@code Pedido} já
     * {@code PAGO}/{@code EM_SEPARACAO} (estoque físico já debitado por
     * {@link #confirmarConsumo}) é cancelado.
     */
    @Transactional
    void devolverEstoque(Long id, BigDecimal quantidade) {
        VarianteProdutoLoja variante = buscarParaAtualizarEstoque(id);
        variante.setEstoqueQuantidade(variante.getEstoqueQuantidade().add(quantidade));
    }

    private VarianteProdutoLoja buscarParaAtualizarEstoque(Long id) {
        return repository.buscarParaAtualizarEstoque(id).orElseThrow(() -> NotFoundException.of("Variante do produto", id));
    }

    private void validarOpcaoSeLista(AtributoCategoria atributo, String valor) {
        if (atributo.getTipo() != TipoAtributo.LISTA) {
            return;
        }
        boolean valido = atributo.getOpcoes().stream().anyMatch(o -> o.getValor().equals(valor));
        if (!valido) {
            throw new BusinessException(LojaVirtualErrorCode.OPCAO_INVALIDA_PARA_ATRIBUTO_LISTA);
        }
    }

    VarianteProdutoLoja buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Variante do produto", id));
    }
}
