-- Configuração de juros/multa por atraso, por empresa (2026-09-05) — fecha a pendência
-- "juros/multa por atraso calculados automaticamente" registrada desde a criação do
-- financeiro-service. Percentuais nulos = sem cobrança (semântica permissiva, mesmo
-- padrão de `configuracao_compras_empresa.limite_aprovacao_pedido`).

CREATE TABLE configuracao_financeiro_empresa (
    id                          BIGINT        NOT NULL AUTO_INCREMENT,
    empresa_id                  BIGINT        NOT NULL,
    percentual_multa_atraso     DECIMAL(8,4)  NULL,
    percentual_juros_dia_atraso DECIMAL(8,4)  NULL,
    data_criacao                DATETIME(6)   NOT NULL,
    data_atualizacao            DATETIME(6)   NOT NULL,
    criado_por                  VARCHAR(100)  NULL,
    atualizado_por              VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_configuracao_financeiro_empresa (empresa_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
