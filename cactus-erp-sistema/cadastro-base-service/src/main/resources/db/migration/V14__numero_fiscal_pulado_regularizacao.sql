-- Fecha o ciclo do ledger de números pulados: o fiscal-service, ao ter uma inutilização homologada pela SEFAZ,
-- marca os números da faixa como regularizados (protocolo + data). Pendente = data_regularizacao NULL.
ALTER TABLE numero_fiscal_pulado
    ADD COLUMN protocolo_inutilizacao VARCHAR(20) NULL COMMENT 'nProt da inutilização homologada' AFTER motivo,
    ADD COLUMN data_regularizacao     DATETIME(6) NULL AFTER protocolo_inutilizacao,
    ADD KEY idx_numero_fiscal_pulado_faixa (empresa_id, tipo_documento, serie, numero);
