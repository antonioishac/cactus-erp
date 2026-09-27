-- Módulo Comercial (Fase 11) — Emissão de CT-e (modelo 57, modal Rodoviário) a partir
-- do frete de uma Venda, via ACBr Monitor. Validado campo a campo contra o fenix.sql
-- (CTE_CABECALHO, 73 colunas + ~25 tabelas satélite), pragmaticamente simplificado pro
-- cenário dominante de mercado — ver Javadoc de CteCabecalho/CteIniBuilder e CLAUDE.md
-- pro detalhamento completo do que ficou fora de escopo.
--
-- venda_id é referência SOLTA pro vendas-service; empresa_id/cliente_id/transportadora_id
-- são soltas pro cadastro-base-service. Sem FK cross-schema, mesmo padrão do monorepo.

CREATE TABLE cte_cabecalho (
    id                      BIGINT        NOT NULL AUTO_INCREMENT,
    venda_id                BIGINT        NOT NULL,
    empresa_id              BIGINT        NOT NULL,
    cliente_id              BIGINT        NOT NULL,
    transportadora_id       BIGINT        NOT NULL,
    modelo                  VARCHAR(2)    NOT NULL DEFAULT '57',
    modal                   VARCHAR(2)    NOT NULL DEFAULT '01' COMMENT '01=Rodoviário — único modal suportado nesta fase',
    serie                   VARCHAR(3)    NULL,
    numero                  BIGINT        NULL,
    chave_acesso            VARCHAR(44)   NULL,
    protocolo_autorizacao   VARCHAR(30)   NULL,
    data_emissao            DATETIME(6)   NULL,
    ambiente                VARCHAR(15)   NULL COMMENT 'PRODUCAO | HOMOLOGACAO',
    tomador                 VARCHAR(15)   NOT NULL COMMENT 'REMETENTE | EXPEDIDOR | RECEBEDOR | DESTINATARIO | OUTROS — só REMETENTE/DESTINATARIO suportados nesta fase',
    natureza_operacao       VARCHAR(60)   NOT NULL DEFAULT 'Prestação de serviço de transporte',
    cfop                    VARCHAR(4)    NULL,
    produto_predominante    VARCHAR(60)   NULL,
    observacoes_gerais      TEXT          NULL,
    rntrc                   VARCHAR(8)    NULL,
    data_prevista_entrega   DATE          NULL,
    indicador_lotacao       CHAR(1)       NULL COMMENT '0-Não; 1-Sim',
    ciot                    VARCHAR(20)   NULL,
    placa                   VARCHAR(7)    NULL,
    uf_placa                CHAR(2)       NULL,
    peso_bruto              DECIMAL(18,6) NULL,
    quantidade_volume       DECIMAL(18,6) NULL,
    valor_total_servico     DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_receber           DECIMAL(18,6) NULL,
    valor_total_carga       DECIMAL(18,6) NULL,
    cst                     VARCHAR(2)    NULL,
    base_calculo_icms       DECIMAL(18,6) NULL,
    aliquota_icms           DECIMAL(18,6) NULL,
    valor_icms              DECIMAL(18,6) NULL,
    situacao                VARCHAR(15)   NOT NULL COMMENT 'RASCUNHO | ENVIADA | AUTORIZADA | REJEITADA | ERRO | CANCELADA',
    mensagem_sefaz          VARCHAR(500)  NULL,
    data_criacao            DATETIME(6)   NOT NULL,
    data_atualizacao        DATETIME(6)   NOT NULL,
    criado_por              VARCHAR(100)  NULL,
    atualizado_por          VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_cte_venda (venda_id),
    KEY idx_cte_empresa (empresa_id),
    KEY idx_cte_cliente (cliente_id),
    KEY idx_cte_transportadora (transportadora_id),
    KEY idx_cte_situacao (situacao)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE cte_componente (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    cte_cabecalho_id    BIGINT        NOT NULL,
    nome                VARCHAR(15)   NOT NULL COMMENT 'Ex: FRETE PESO, FRETE VALOR, SEC/CAT, ADEME, AGENDAMENTO',
    valor               DECIMAL(18,6) NOT NULL,
    data_criacao        DATETIME(6)   NOT NULL,
    data_atualizacao    DATETIME(6)   NOT NULL,
    criado_por          VARCHAR(100)  NULL,
    atualizado_por      VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_cte_componente_cabecalho (cte_cabecalho_id),
    CONSTRAINT fk_cte_componente_cabecalho FOREIGN KEY (cte_cabecalho_id) REFERENCES cte_cabecalho (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
