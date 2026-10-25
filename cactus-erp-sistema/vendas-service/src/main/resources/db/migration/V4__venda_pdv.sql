-- Fase 6-B, itens 5-7 (2026-10-24): venda de balcão do PDV, com pagamento fracionado e devolução.
-- Entidade PRÓPRIA (decisão do usuário), não VendaCabecalho: o shape do balcão é outro — operador e
-- terminal em vez de vendedor, várias formas de pagamento, documento fiscal (NFC-e/SAT) já emitido no PDV.
-- Ver Javadoc de VendaPdv/VendaPdvService.
--
-- Estoque: a venda NÃO baixa estoque aqui. O PDV já grava a saída (S) e a reentrada da devolução (D)
-- no próprio ESTOQUE_MOVIMENTO, que chega ao controle-estoque-service por conta própria — baixar pelos
-- dois caminhos contaria cada item duas vezes.
--
-- Referências soltas (sem FK de banco, outros serviços): empresa_id, cliente_id, colaborador_id,
-- produto_id, forma_pagamento_id. pdv_* são ids LOCAIS do PDV daquela loja.

CREATE TABLE venda_pdv (
    id                          BIGINT        NOT NULL AUTO_INCREMENT,
    empresa_id                  BIGINT        NOT NULL,
    registro_sincronizacao_id   BIGINT        NOT NULL COMMENT 'RegistroSincronizacao.id — chave de idempotência do consumidor',
    id_origem                   VARCHAR(100)  NOT NULL COMMENT 'idOrigem enviado pelo PDV — a devolução referencia a venda por ele',
    pdv_caixa_id                BIGINT        NOT NULL,
    pdv_caixa_nome              VARCHAR(60)   NULL,
    pdv_operador_id             BIGINT        NULL,
    cliente_id                  BIGINT        NULL,
    colaborador_id              BIGINT        NULL,
    nome_cliente                VARCHAR(100)  NULL,
    cpf_cnpj_cliente            VARCHAR(14)   NULL,
    data_hora_venda             DATETIME(6)   NOT NULL,
    origem_venda                VARCHAR(10)   NOT NULL COMMENT 'BALCAO | MESA | COMANDA | DELIVERY | EXTERNA',
    situacao                    VARCHAR(10)   NOT NULL COMMENT 'FECHADA | CANCELADA',
    tipo_operacao_fiscal        VARCHAR(3)    NULL COMMENT 'NFC | SAT | MFE | REC',
    status_fiscal               VARCHAR(1)    NULL COMMENT 'P=Pendente | E=Emitido | N=Não configurado',
    chave_acesso_fiscal         VARCHAR(44)   NULL,
    protocolo_autorizacao       VARCHAR(30)   NULL,
    coo                         INT           NULL,
    ccf                         INT           NULL,
    valor_total_produtos        DECIMAL(18,6) NOT NULL,
    valor_desconto              DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_acrescimo             DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_final                 DECIMAL(18,6) NOT NULL,
    valor_recebido              DECIMAL(18,6) NOT NULL,
    valor_troco                 DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_base_icms             DECIMAL(18,6) NULL,
    valor_icms                  DECIMAL(18,6) NULL,
    valor_pis                   DECIMAL(18,6) NULL,
    valor_cofins                DECIMAL(18,6) NULL,
    valor_issqn                 DECIMAL(18,6) NULL,
    valor_dinheiro_liquido      DECIMAL(18,6) NOT NULL DEFAULT 0 COMMENT 'Dinheiro − troco: o que entrou de fato na gaveta (lançado no caixa do terminal)',
    data_criacao                DATETIME(6)   NOT NULL,
    data_atualizacao            DATETIME(6)   NOT NULL,
    criado_por                  VARCHAR(100)  NULL,
    atualizado_por              VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_venda_pdv_registro (registro_sincronizacao_id),
    UNIQUE KEY uk_venda_pdv_origem (empresa_id, id_origem),
    KEY idx_venda_pdv_data (empresa_id, data_hora_venda),
    KEY idx_venda_pdv_caixa (empresa_id, pdv_caixa_id),
    KEY idx_venda_pdv_cliente (cliente_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE venda_pdv_item (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    venda_pdv_id          BIGINT        NOT NULL,
    numero_item           INT           NOT NULL,
    produto_id            BIGINT        NOT NULL,
    gtin                  VARCHAR(14)   NULL,
    quantidade            DECIMAL(18,6) NOT NULL,
    valor_unitario        DECIMAL(18,6) NOT NULL,
    valor_desconto        DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_acrescimo       DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_total           DECIMAL(18,6) NOT NULL,
    cfop                  INT           NULL,
    cst                   VARCHAR(3)    NULL,
    aliquota_icms         DECIMAL(18,6) NULL,
    valor_icms            DECIMAL(18,6) NULL,
    valor_pis             DECIMAL(18,6) NULL,
    valor_cofins          DECIMAL(18,6) NULL,
    cancelado             TINYINT(1)    NOT NULL DEFAULT 0,
    quantidade_devolvida  DECIMAL(18,6) NOT NULL DEFAULT 0,
    data_criacao          DATETIME(6)   NOT NULL,
    data_atualizacao      DATETIME(6)   NOT NULL,
    criado_por            VARCHAR(100)  NULL,
    atualizado_por        VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_venda_pdv_item (venda_pdv_id, numero_item),
    KEY idx_venda_pdv_item_produto (produto_id),
    CONSTRAINT fk_venda_pdv_item_venda FOREIGN KEY (venda_pdv_id) REFERENCES venda_pdv (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Pagamento fracionado (PDV_TOTAL_TIPO_PAGAMENTO): N formas na mesma venda.
CREATE TABLE venda_pdv_pagamento (
    id                      BIGINT        NOT NULL AUTO_INCREMENT,
    venda_pdv_id            BIGINT        NOT NULL,
    forma_pagamento_id      BIGINT        NULL COMMENT 'FormaPagamento do cadastro-base (o PDV importa PDV_TIPO_PAGAMENTO do ERP)',
    codigo_pagamento_nfce   VARCHAR(2)    NOT NULL COMMENT 'tPag da SEFAZ: 01 dinheiro, 03 crédito, 04 débito, 05 crédito loja, 17 PIX...',
    descricao               VARCHAR(30)   NULL,
    valor                   DECIMAL(18,6) NOT NULL,
    nsu                     VARCHAR(30)   NULL,
    rede                    VARCHAR(10)   NULL,
    cartao_debito_credito   VARCHAR(1)    NULL COMMENT 'D | C',
    estornado               TINYINT(1)    NOT NULL DEFAULT 0,
    data_criacao            DATETIME(6)   NOT NULL,
    data_atualizacao        DATETIME(6)   NOT NULL,
    criado_por              VARCHAR(100)  NULL,
    atualizado_por          VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_venda_pdv_pagamento_venda (venda_pdv_id),
    KEY idx_venda_pdv_pagamento_codigo (codigo_pagamento_nfce),
    CONSTRAINT fk_venda_pdv_pagamento_venda FOREIGN KEY (venda_pdv_id) REFERENCES venda_pdv (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Devolução pós-venda (PDV_DEVOLUCAO). Sempre com gerente autorizador (regra do PDV).
CREATE TABLE venda_pdv_devolucao (
    id                            BIGINT        NOT NULL AUTO_INCREMENT,
    venda_pdv_id                  BIGINT        NOT NULL,
    venda_pdv_item_id             BIGINT        NOT NULL,
    registro_sincronizacao_id     BIGINT        NOT NULL,
    quantidade_devolvida          DECIMAL(18,6) NOT NULL,
    valor_devolvido               DECIMAL(18,6) NOT NULL,
    motivo                        VARCHAR(250)  NULL,
    pdv_operador_id               BIGINT        NULL,
    pdv_gerente_autorizador_id    BIGINT        NOT NULL,
    data_hora_devolucao           DATETIME(6)   NOT NULL,
    data_criacao                  DATETIME(6)   NOT NULL,
    data_atualizacao              DATETIME(6)   NOT NULL,
    criado_por                    VARCHAR(100)  NULL,
    atualizado_por                VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_venda_pdv_devolucao_registro (registro_sincronizacao_id),
    KEY idx_venda_pdv_devolucao_venda (venda_pdv_id),
    CONSTRAINT fk_venda_pdv_devolucao_venda FOREIGN KEY (venda_pdv_id) REFERENCES venda_pdv (id),
    CONSTRAINT fk_venda_pdv_devolucao_item FOREIGN KEY (venda_pdv_item_id) REFERENCES venda_pdv_item (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
