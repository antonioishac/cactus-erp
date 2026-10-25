-- Faturamento diário por empresa × canal (B2B = venda_cabecalho, PDV = venda_pdv),
-- agregado pelo ETL a partir de cactus_vendas. Ticket médio NÃO é persistido: é
-- valor_liquido / quantidade_vendas, calculado na leitura (somar médias de dias
-- diferentes daria um número errado; somar numerador e denominador, não).
CREATE TABLE faturamento_diario (
    id                 BIGINT        NOT NULL AUTO_INCREMENT,
    data_referencia    DATE          NOT NULL COMMENT 'Dia da venda (PDV: dia LOCAL da loja)',
    empresa_id         BIGINT        NOT NULL,
    canal              VARCHAR(3)    NOT NULL COMMENT 'B2B | PDV',
    quantidade_vendas  INT           NOT NULL,
    valor_bruto        DECIMAL(18,6) NOT NULL COMMENT 'B2B: valor_total | PDV: valor_final',
    valor_devolvido    DECIMAL(18,6) NOT NULL COMMENT 'Só PDV — B2B não tem devolução ainda',
    valor_liquido      DECIMAL(18,6) NOT NULL COMMENT 'valor_bruto − valor_devolvido',
    data_criacao       DATETIME(6)   NOT NULL,
    data_atualizacao   DATETIME(6)   NOT NULL,
    criado_por         VARCHAR(100)  NULL,
    atualizado_por     VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_faturamento_diario (data_referencia, empresa_id, canal),
    KEY idx_faturamento_diario_empresa (empresa_id, data_referencia)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
