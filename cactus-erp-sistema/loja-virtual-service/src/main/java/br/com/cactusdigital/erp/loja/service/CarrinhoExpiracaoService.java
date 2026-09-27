package br.com.cactusdigital.erp.loja.service;

import br.com.cactusdigital.erp.loja.entity.Carrinho;
import br.com.cactusdigital.erp.loja.entity.ItemCarrinho;
import br.com.cactusdigital.erp.loja.entity.SituacaoCarrinho;
import br.com.cactusdigital.erp.loja.repository.CarrinhoRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Rede de segurança da reserva real (Fase 13, sub-fase 4) — libera a reserva de carrinho
 * {@code ABERTO} inativo há mais de {@code cactus.loja.carrinho.ttl-minutos} (default 30) e
 * marca {@code ABANDONADO}. Complementa {@code CarrinhoService#abandonar} (sinal explícito
 * do frontend): sem esta rotina, um comprador que fecha a aba sem avisar deixaria a
 * quantidade reservada pra sempre, indisponível pra outro comprador sem nunca virar venda.
 * <p>
 * Mesmo padrão de {@code business-intelligence-service.EtlService} ({@code @Scheduled} +
 * config externalizável) — sem snapshot/histórico, só um job de limpeza idempotente
 * (rodar de novo sobre o mesmo carrinho já {@code ABANDONADO} não faz nada, o filtro é por
 * {@code situacao=ABERTO}).
 */
@Service
@RequiredArgsConstructor
public class CarrinhoExpiracaoService {

    private static final Logger log = LoggerFactory.getLogger(CarrinhoExpiracaoService.class);

    private final CarrinhoRepository repository;
    private final VarianteProdutoLojaService varianteService;

    @Value("${cactus.loja.carrinho.ttl-minutos:30}")
    private long ttlMinutos;

    @Scheduled(cron = "${cactus.loja.carrinho.expiracao-cron:0 */5 * * * *}")
    @Transactional
    public void expirarCarrinhosInativos() {
        Instant limite = Instant.now().minus(ttlMinutos, ChronoUnit.MINUTES);
        var carrinhosExpirados = repository.findBySituacaoAndDataAtualizacaoBefore(SituacaoCarrinho.ABERTO, limite);
        for (Carrinho carrinho : carrinhosExpirados) {
            for (ItemCarrinho item : carrinho.getItens()) {
                varianteService.liberarReserva(item.getVarianteProdutoLoja().getId(), item.getQuantidade());
            }
            carrinho.setSituacao(SituacaoCarrinho.ABANDONADO);
        }
        if (!carrinhosExpirados.isEmpty()) {
            log.info("Carrinhos expirados por inatividade (>{}min): {}", ttlMinutos, carrinhosExpirados.size());
        }
    }
}
