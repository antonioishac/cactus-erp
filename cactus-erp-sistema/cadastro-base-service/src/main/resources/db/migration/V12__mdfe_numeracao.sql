-- Numeração de MDF-e (modelo 58) na configuração fiscal por filial — mesmo padrão exato
-- de serie_cte/proximo_numero_cte, consumido pelo fiscal-service (Fase 16 do roadmap).

ALTER TABLE `empresa_configuracao_fiscal`
    ADD COLUMN `serie_mdfe` VARCHAR(3) NULL AFTER `proximo_numero_cte`,
    ADD COLUMN `proximo_numero_mdfe` BIGINT NULL AFTER `serie_mdfe`;
