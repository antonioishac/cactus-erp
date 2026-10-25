-- Cancelamento de venda do PDV já fechada e já sincronizada (tipo de registro CANCELAMENTO_VENDA_PDV).
-- A venda continua sendo a mesma linha: só muda para CANCELADA e ganha os dados do cancelamento.
ALTER TABLE venda_pdv
    ADD COLUMN registro_cancelamento_id      BIGINT       NULL COMMENT 'RegistroSincronizacao.id do cancelamento — idempotência' AFTER valor_dinheiro_liquido,
    ADD COLUMN data_hora_cancelamento        DATETIME(6)  NULL AFTER registro_cancelamento_id,
    ADD COLUMN pdv_operador_cancelamento_id  BIGINT       NULL AFTER data_hora_cancelamento,
    ADD COLUMN pdv_gerente_cancelamento_id   BIGINT       NULL COMMENT 'Gerente que autorizou (sempre obrigatório no PDV)' AFTER pdv_operador_cancelamento_id,
    ADD COLUMN justificativa_cancelamento    VARCHAR(255) NULL AFTER pdv_gerente_cancelamento_id,
    ADD COLUMN protocolo_cancelamento        VARCHAR(30)  NULL COMMENT 'Protocolo do evento de cancelamento da NFC-e (nulo se não havia documento fiscal autorizado)' AFTER justificativa_cancelamento,
    ADD UNIQUE KEY uk_venda_pdv_registro_cancelamento (registro_cancelamento_id);
