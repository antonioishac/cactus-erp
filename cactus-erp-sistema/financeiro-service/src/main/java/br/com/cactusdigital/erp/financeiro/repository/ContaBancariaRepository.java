package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.ContaBancaria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, Long> {

    Page<ContaBancaria> findByEmpresaId(Long empresaId, Pageable pageable);

    List<ContaBancaria> findByEmpresaIdAndAtivoTrue(Long empresaId);

    Optional<ContaBancaria> findByEmpresaIdAndPdvDispositivoIdAndPdvCaixaId(Long empresaId, String pdvDispositivoId, Long pdvCaixaId);

    /** Cria a conta do terminal se ainda não existe, resolvendo a corrida pela chave única
     * {@code uk_conta_bancaria_pdv_terminal} (o {@code UPDATE id = id} é no-op proposital). Ver
     * {@code ContaTerminalPdvService}. Datas em UTC, como o restante do schema. */
    @Modifying
    @Query(value = """
            INSERT INTO conta_bancaria (empresa_id, tipo, nome, pdv_dispositivo_id, pdv_caixa_id, data_saldo_inicial,
                                        saldo_inicial, saldo_atual, ativo, data_criacao, data_atualizacao)
            VALUES (:empresaId, :tipo, :nome, :dispositivoId, :pdvCaixaId, UTC_DATE(), 0, 0, 1, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6))
            ON DUPLICATE KEY UPDATE id = id
            """, nativeQuery = true)
    void criarContaTerminalSeAusente(@Param("empresaId") Long empresaId, @Param("dispositivoId") String dispositivoId,
                                     @Param("pdvCaixaId") Long pdvCaixaId, @Param("nome") String nome, @Param("tipo") String tipo);

    /** Mesma busca com leitura travada (leitura ATUAL) — enxerga a conta criada por outra transação depois da
     * primeira leitura desta. Ver {@code ContaTerminalPdvService}. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ContaBancaria c WHERE c.empresaId = :empresaId AND c.pdvDispositivoId = :dispositivoId AND c.pdvCaixaId = :pdvCaixaId")
    Optional<ContaBancaria> buscarTerminalTravado(@Param("empresaId") Long empresaId, @Param("dispositivoId") String dispositivoId,
                                                  @Param("pdvCaixaId") Long pdvCaixaId);

    /** Lock pessimista pra serializar concorrência sobre {@code saldoAtual} — dinheiro,
     * não é aceitável um lost-update entre duas baixas simultâneas na mesma conta. Usado
     * só dentro de {@code MovimentoCaixaBancoService#registrar}. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ContaBancaria c WHERE c.id = :id")
    Optional<ContaBancaria> buscarParaAtualizarSaldo(@Param("id") Long id);

    /** Saldo atual numa leitura travada e NATIVA: não passa pelo contexto de persistência, então nunca devolve um
     * saldo em memória lido antes do lock (ver {@code MovimentoCaixaBancoService#registrar}). */
    @Query(value = "SELECT saldo_atual FROM conta_bancaria WHERE id = :id FOR UPDATE", nativeQuery = true)
    java.math.BigDecimal lerSaldoTravado(@Param("id") Long id);
}
