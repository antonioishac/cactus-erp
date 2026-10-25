-- Fase 6-B (2026-10-24): caixa e fiado do PDV chegando ao financeiro via sincronizacao-service.
-- Ver Javadoc de FinanceiroPdvService.

-- Uma conta CAIXA_INTERNO por terminal do PDV, criada no 1º envio daquele terminal.
-- pdv_caixa_id = PDV_CAIXA.ID (id local da loja) — único por empresa; NULL nas contas comuns
-- (o UNIQUE do MySQL permite vários NULL, então contas que não são de terminal não colidem).
ALTER TABLE conta_bancaria
    ADD COLUMN pdv_caixa_id BIGINT NULL AFTER proximo_nosso_numero,
    ADD UNIQUE KEY uk_conta_bancaria_pdv_caixa (empresa_id, pdv_caixa_id);

-- Idempotência do consumidor: o RabbitMQ entrega "pelo menos uma vez", e dinheiro lançado em
-- dobro é inaceitável. Cada RegistroSincronizacao (id do sincronizacao-service) só é aplicado
-- uma vez — a linha é inserida na MESMA transação do lançamento, então ou os dois existem ou
-- nenhum. Reentrega encontra a linha e só reconfirma.
CREATE TABLE sincronizacao_pdv_processado (
    registro_id        BIGINT       NOT NULL,
    tipo_registro      VARCHAR(30)  NOT NULL,
    data_processamento DATETIME(6)  NOT NULL,
    PRIMARY KEY (registro_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
