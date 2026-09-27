package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Contador responsável — validado contra {@code fenix.sql.CONTADOR}, pra troca de
 * arquivos com a contabilidade (SPED, etc). N:1 com {@link Empresa} (um escritório de
 * contabilidade atende várias empresas — não é um dado exclusivo de uma filial).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "contador")
public class Contador extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    /** Registro no Conselho Regional de Contabilidade (ex: "SP-123456/O-3"). */
    @Column(name = "crc", nullable = false, length = 20)
    private String crc;

    @Column(name = "email", length = 250)
    private String email;

    @Column(name = "telefone", length = 15)
    private String telefone;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
