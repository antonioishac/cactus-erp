package br.com.cactusdigital.erp.loja.repository;

import br.com.cactusdigital.erp.loja.entity.VarianteProdutoLoja;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VarianteProdutoLojaRepository extends JpaRepository<VarianteProdutoLoja, Long> {

    boolean existsByProdutoLoja_LojaIdAndSku(Long lojaId, String sku);

    boolean existsByProdutoLoja_LojaIdAndSkuAndIdNot(Long lojaId, String sku, Long id);

    /**
     * Lock pessimista pra serializar concorrência sobre {@code estoqueQuantidade}/
     * {@code estoqueReservado} — mesmo critério de dinheiro já usado em
     * {@code financeiro-service.ContaBancariaRepository#buscarParaAtualizarSaldo}: sem
     * isso, dois carrinhos concorrentes poderiam ambos passar na checagem de
     * disponibilidade pra mesma variante (lost-update). Usado só dentro de
     * {@code VarianteProdutoLojaService}.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM VarianteProdutoLoja v WHERE v.id = :id")
    Optional<VarianteProdutoLoja> buscarParaAtualizarEstoque(@Param("id") Long id);
}
