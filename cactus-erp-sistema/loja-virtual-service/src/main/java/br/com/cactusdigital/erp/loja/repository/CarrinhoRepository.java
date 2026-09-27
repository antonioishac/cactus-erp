package br.com.cactusdigital.erp.loja.repository;

import br.com.cactusdigital.erp.loja.entity.Carrinho;
import br.com.cactusdigital.erp.loja.entity.SituacaoCarrinho;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {

    Optional<Carrinho> findByToken(String token);

    /** Usado por {@code CarrinhoExpiracaoService} pra achar carrinho abandonado por inatividade (aba fechada sem aviso, sem chamar {@code /abandonar} explicitamente). */
    List<Carrinho> findBySituacaoAndDataAtualizacaoBefore(SituacaoCarrinho situacao, Instant limite);
}
