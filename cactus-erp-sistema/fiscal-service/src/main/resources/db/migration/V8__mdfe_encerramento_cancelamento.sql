-- Fase 16, sub-fase 2 — eventos de Encerramento (110112) e Cancelamento (110111) do MDF-e.
-- Ver Javadoc de MdfeCabecalhoService#encerrar/#cancelar.

ALTER TABLE mdfe_cabecalho
    ADD COLUMN data_encerramento                   DATE         NULL AFTER mensagem_sefaz,
    ADD COLUMN municipio_encerramento_codigo_ibge  INT          NULL AFTER data_encerramento,
    ADD COLUMN protocolo_encerramento              VARCHAR(30)  NULL AFTER municipio_encerramento_codigo_ibge,
    ADD COLUMN data_cancelamento                   DATETIME(6)  NULL AFTER protocolo_encerramento,
    ADD COLUMN justificativa_cancelamento          VARCHAR(255) NULL AFTER data_cancelamento,
    ADD COLUMN protocolo_cancelamento              VARCHAR(30)  NULL AFTER justificativa_cancelamento;

-- Um MDF-e CANCELADO libera seus CT-e pra um novo manifesto (a viagem nunca aconteceu), então
-- "um CT-e em um MDF-e só" deixa de ser absoluto: a regra passa a ser "em no máximo um MDF-e
-- NÃO cancelado", checada na aplicação (MdfeCabecalhoRepository#buscarCteIdsJaVinculados).
-- O histórico do vínculo cancelado é preservado, nunca apagado.
-- A FK precisa de um índice próprio em cte_cabecalho_id antes de o UNIQUE poder ser removido.
CREATE INDEX idx_mdfe_documento_cte ON mdfe_documento_vinculado (cte_cabecalho_id);
ALTER TABLE mdfe_documento_vinculado DROP INDEX uk_mdfe_documento_cte;
