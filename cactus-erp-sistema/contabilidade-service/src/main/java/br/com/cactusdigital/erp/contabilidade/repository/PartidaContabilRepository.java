package br.com.cactusdigital.erp.contabilidade.repository;

import br.com.cactusdigital.erp.contabilidade.entity.PartidaContabil;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Consultas de agregação usadas por {@code RelatorioContabilService} (Balancete/Razão) e
 * por {@code SpedContabilExportService} (registros {@code I150}/{@code I155} da ECD, que
 * exigem saldo ANTES do período além do movimento dentro dele). Cada linha do
 * {@code Object[]} devolvido por {@link #somarDebitoCredito} é
 * {@code [Long contaId, BigDecimal totalDebito, BigDecimal totalCredito]}.
 *
 * <p>Deliberadamente SEM filtro por {@code situacao} do lançamento — um lançamento
 * {@code ESTORNADO} continua sendo um movimento real que aconteceu (ver Javadoc de
 * {@code LancamentoContabil}: "o original nunca é apagado/alterado, só marcado
 * ESTORNADO"), e seu estorno é uma transação NOVA que o cancela, não um "desfazer" que
 * apaga história. Excluir só o original (achado real, corrigido 2026-10-11: um teste ao
 * vivo criando e estornando um lançamento no mesmo período mostrou saldo não voltando a
 * zero) deixava a reversão sozinha no cálculo, sem a ponta que ela deveria cancelar.
 * Incluir os dois sempre faz o par somar exatamente zero quando ambos caem no período
 * consultado — e cada um conta corretamente no período em que de fato ocorreu quando
 * caem em períodos diferentes.
 */
public interface PartidaContabilRepository extends JpaRepository<PartidaContabil, Long> {

    boolean existsByContaId(Long contaId);

    @Query("""
            SELECT p.conta.id,
                   COALESCE(SUM(CASE WHEN p.tipoPartida = br.com.cactusdigital.erp.contabilidade.entity.TipoPartida.DEBITO THEN p.valor END), 0),
                   COALESCE(SUM(CASE WHEN p.tipoPartida = br.com.cactusdigital.erp.contabilidade.entity.TipoPartida.CREDITO THEN p.valor END), 0)
            FROM PartidaContabil p
            WHERE p.conta.empresaId = :empresaId
              AND p.lancamentoContabil.dataLancamento BETWEEN :dataInicio AND :dataFim
            GROUP BY p.conta.id
            """)
    List<Object[]> somarDebitoCredito(
            @Param("empresaId") Long empresaId, @Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);

    /** Mesma agregação, mas de tudo ANTES de {@code dataLimite} — usada pra compor o saldo de abertura do período (registro {@code I150} da ECD). */
    @Query("""
            SELECT p.conta.id,
                   COALESCE(SUM(CASE WHEN p.tipoPartida = br.com.cactusdigital.erp.contabilidade.entity.TipoPartida.DEBITO THEN p.valor END), 0),
                   COALESCE(SUM(CASE WHEN p.tipoPartida = br.com.cactusdigital.erp.contabilidade.entity.TipoPartida.CREDITO THEN p.valor END), 0)
            FROM PartidaContabil p
            WHERE p.conta.empresaId = :empresaId
              AND p.lancamentoContabil.dataLancamento < :dataLimite
            GROUP BY p.conta.id
            """)
    List<Object[]> somarDebitoCreditoAntesDe(@Param("empresaId") Long empresaId, @Param("dataLimite") LocalDate dataLimite);

    @Query("""
            SELECT p FROM PartidaContabil p
            JOIN FETCH p.lancamentoContabil l
            WHERE p.conta.id = :contaId
              AND l.dataLancamento BETWEEN :dataInicio AND :dataFim
            ORDER BY l.dataLancamento ASC, p.id ASC
            """)
    List<PartidaContabil> buscarPorContaEPeriodo(
            @Param("contaId") Long contaId, @Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
}
