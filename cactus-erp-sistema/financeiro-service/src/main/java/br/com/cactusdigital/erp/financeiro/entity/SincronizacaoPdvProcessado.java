package br.com.cactusdigital.erp.financeiro.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Marca de "este registro do PDV já foi aplicado" — inserida na mesma transação do lançamento
 * financeiro, então reentrega do RabbitMQ nunca lança dinheiro em dobro. Não estende
 * {@code BaseEntity} de propósito: a chave é o {@code RegistroSincronizacao.id} do
 * sincronizacao-service, não um id gerado aqui.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sincronizacao_pdv_processado")
public class SincronizacaoPdvProcessado {

    @Id
    @Column(name = "registro_id")
    private Long registroId;

    @Column(name = "tipo_registro", nullable = false, length = 30)
    private String tipoRegistro;

    @Column(name = "data_processamento", nullable = false)
    private Instant dataProcessamento;
}
