package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.Cliente;
import br.com.cactuserp.pdv.entity.ClienteFiadoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteFiadoLancamentoRepository extends JpaRepository<ClienteFiadoLancamento, Long> {

    /** Histórico de lançamentos de um cliente, mais recente primeiro. */
    List<ClienteFiadoLancamento> findByClienteOrderByIdDesc(Cliente cliente);

    /** Idempotência: evita gerar duas cobranças de fiado pra mesma venda. */
    boolean existsByVendaCabecalhoIdAndTipoLancamento(
            Long idVendaCabecalho, br.com.cactuserp.pdv.entity.TipoLancamentoFiado tipo);
}
