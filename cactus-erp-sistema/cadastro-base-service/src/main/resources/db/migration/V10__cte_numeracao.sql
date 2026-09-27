-- Numeração de CT-e (modelo 57) na configuração fiscal por filial — mesmo padrão exato
-- de serie_nfe/proximo_numero_nfe, consumido pelo fiscal-service (Fase 11 do roadmap).

ALTER TABLE `empresa_configuracao_fiscal`
    ADD COLUMN `serie_cte` VARCHAR(3) NULL AFTER `proximo_numero_nfse`,
    ADD COLUMN `proximo_numero_cte` BIGINT NULL AFTER `serie_cte`;
