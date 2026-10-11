package br.com.cactusdigital.erp.os.repository;

import br.com.cactusdigital.erp.os.entity.OsAbertura;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OsAberturaRepository extends JpaRepository<OsAbertura, Long> {

    Page<OsAbertura> findByClienteId(Long clienteId, Pageable pageable);

    /**
     * Variante não paginada pra relatório — filtro por {@code dataInicio} (OS sem data de
     * início nunca entra, mesmo critério de exclusão natural já usado em outros
     * relatórios por período), opcionalmente por status/empresa. {@code JOIN FETCH} de
     * {@code osStatus} (singular, resolve {@code osStatusNome}) combinado com UMA das 3
     * coleções irmãs ({@code equipamentos}) na mesma query — as outras 2
     * ({@code itens}/{@code evolucoes}) continuam hidratadas à parte, mesmo critério já
     * documentado no Javadoc de {@link #hidratarEquipamentos}.
     */
    @Query("""
            SELECT DISTINCT o FROM OsAbertura o LEFT JOIN FETCH o.osStatus LEFT JOIN FETCH o.equipamentos
            WHERE o.dataInicio BETWEEN :inicio AND :fim
            AND (:osStatusId IS NULL OR o.osStatus.id = :osStatusId)
            AND (:empresaId IS NULL OR o.empresaId = :empresaId)
            """)
    List<OsAbertura> buscarParaRelatorio(
            @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim,
            @Param("osStatusId") Long osStatusId, @Param("empresaId") Long empresaId);

    /**
     * {@code OsAbertura} tem 3 coleções irmãs (equipamentos/itens/evolucoes) — nenhuma
     * combinação delas cabe numa {@code JOIN FETCH} só ({@code MultipleBagFetchException}),
     * mesma situação já resolvida pra {@code Pessoa} (enderecos/contatos/telefones) no
     * cadastro-base-service: 3 métodos "hidratar por lado", chamados em sequência só
     * pelo efeito colateral de popular o persistence context antes do mapeamento pro DTO.
     */
    @Query("SELECT DISTINCT o FROM OsAbertura o LEFT JOIN FETCH o.equipamentos WHERE o.id IN :ids")
    List<OsAbertura> hidratarEquipamentos(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT o FROM OsAbertura o LEFT JOIN FETCH o.itens WHERE o.id IN :ids")
    List<OsAbertura> hidratarItens(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT o FROM OsAbertura o LEFT JOIN FETCH o.evolucoes WHERE o.id IN :ids")
    List<OsAbertura> hidratarEvolucoes(@Param("ids") List<Long> ids);
}
