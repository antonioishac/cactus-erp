-- Estorno de baixa (2026-09-05) — fecha a pendência registrada desde a criação do
-- financeiro-service ("cancelamento de título com baixa já registrada é bloqueado,
-- exigiria esse fluxo"). A baixa original nunca é apagada/alterada em valor/data/forma de
-- pagamento (auditoria) — só ganha as 3 colunas abaixo, que a tiram do cálculo de
-- valorPago/valorSaldo/situacao do título a partir do momento do estorno.

ALTER TABLE baixa_conta_receber
    ADD COLUMN estornado      TINYINT(1)   NOT NULL DEFAULT 0 AFTER usuario_id,
    ADD COLUMN data_estorno   DATE         NULL     AFTER estornado,
    ADD COLUMN motivo_estorno VARCHAR(250) NULL     AFTER data_estorno;

ALTER TABLE baixa_conta_pagar
    ADD COLUMN estornado      TINYINT(1)   NOT NULL DEFAULT 0 AFTER usuario_id,
    ADD COLUMN data_estorno   DATE         NULL     AFTER estornado,
    ADD COLUMN motivo_estorno VARCHAR(250) NULL     AFTER data_estorno;
