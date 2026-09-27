-- Controle de Estoque do Cactus ERP: depósito multi-filial, saldo por produto×depósito,
-- ledger de movimentação (append-only — todo saldo muda exclusivamente por aqui, nunca
-- UPDATE direto), lote/validade, número de série, transferência, inventário físico,
-- kit/produto composto e custeio (média ponderada/FIFO).
--
-- produto_id/empresa_id são referências SOLTAS pro cadastro-base-service (outro schema);
-- usuario_id/aprovador_id são referências soltas pro seguranca-service. Sem FK, sem
-- validação de existência na v1 — mesmo padrão já adotado na extração do seguranca-service.

CREATE TABLE deposito (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    empresa_id        BIGINT       NOT NULL,
    codigo            VARCHAR(20)  NOT NULL,
    nome              VARCHAR(100) NOT NULL,
    tipo              VARCHAR(20)  NOT NULL COMMENT 'PRINCIPAL | FILIAL | CD | TRANSITO',
    situacao          CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_deposito_empresa_codigo (empresa_id, codigo),
    KEY idx_deposito_empresa (empresa_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE lote (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    produto_id        BIGINT       NOT NULL,
    codigo            VARCHAR(60)  NOT NULL,
    data_fabricacao   DATE         NULL,
    data_validade     DATE         NULL,
    situacao          CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_lote_produto_codigo (produto_id, codigo),
    KEY idx_lote_produto (produto_id),
    KEY idx_lote_validade (data_validade)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE numero_serie (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    produto_id        BIGINT       NOT NULL,
    numero_serie      VARCHAR(100) NOT NULL,
    deposito_id       BIGINT       NULL COMMENT 'Depósito atual — NULL quando já vendido/baixado',
    situacao          VARCHAR(20)  NOT NULL COMMENT 'DISPONIVEL | VENDIDO | EM_TRANSITO | BAIXADO',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_numero_serie_produto (produto_id, numero_serie),
    KEY idx_numero_serie_deposito (deposito_id),
    CONSTRAINT fk_numero_serie_deposito FOREIGN KEY (deposito_id) REFERENCES deposito (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Sempre o agregado por produto×depósito (soma de todos os lotes, se houver) — nunca um
-- campo flat no Produto do cadastro-base.
CREATE TABLE saldo_estoque (
    id                       BIGINT        NOT NULL AUTO_INCREMENT,
    produto_id               BIGINT        NOT NULL,
    deposito_id              BIGINT        NOT NULL,
    quantidade               DECIMAL(18,6) NOT NULL DEFAULT 0,
    quantidade_reservada     DECIMAL(18,6) NOT NULL DEFAULT 0,
    custo_medio_ponderado    DECIMAL(18,6) NOT NULL DEFAULT 0,
    data_ultima_movimentacao DATETIME(6)   NULL,
    data_criacao             DATETIME(6)   NOT NULL,
    data_atualizacao         DATETIME(6)   NOT NULL,
    criado_por               VARCHAR(100)  NULL,
    atualizado_por           VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_saldo_estoque_produto_deposito (produto_id, deposito_id),
    KEY idx_saldo_estoque_deposito (deposito_id),
    CONSTRAINT fk_saldo_estoque_deposito FOREIGN KEY (deposito_id) REFERENCES deposito (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Detalhe por lote quando o produto é lote-controlado; soma bate com saldo_estoque.quantidade.
-- Tabela separada de saldo_estoque de propósito: UNIQUE(produto,deposito) sem lote não
-- convive bem com UNIQUE(...,lote_id) quando lote_id é opcional (NULL não garante
-- unicidade em MySQL).
CREATE TABLE saldo_estoque_lote (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    produto_id        BIGINT        NOT NULL,
    deposito_id       BIGINT        NOT NULL,
    lote_id           BIGINT        NOT NULL,
    quantidade        DECIMAL(18,6) NOT NULL DEFAULT 0,
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_saldo_estoque_lote (produto_id, deposito_id, lote_id),
    KEY idx_sel_deposito (deposito_id),
    KEY idx_sel_lote (lote_id),
    CONSTRAINT fk_sel_deposito FOREIGN KEY (deposito_id) REFERENCES deposito (id),
    CONSTRAINT fk_sel_lote FOREIGN KEY (lote_id) REFERENCES lote (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Ledger append-only. tipo_movimento: ENTRADA/SAIDA/AJUSTE/PERDA/DEVOLUCAO validados
-- contra o TIPO_MOVIMENTO real do ESTOQUE_MOVIMENTO do cactus-pdv;
-- TRANSFERENCIA_SAIDA/TRANSFERENCIA_ENTRADA são adição do ERP pro multi-depósito (o PDV
-- não tem esse conceito). direcao_ajuste só é preenchido quando tipo_movimento=AJUSTE
-- (correção pode somar ou subtrair, diferente dos demais tipos que têm efeito fixo).
CREATE TABLE estoque_movimento (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    produto_id          BIGINT        NOT NULL,
    deposito_id         BIGINT        NOT NULL,
    lote_id             BIGINT        NULL,
    numero_serie_id     BIGINT        NULL,
    tipo_movimento      VARCHAR(25)   NOT NULL,
    direcao_ajuste      VARCHAR(12)   NULL COMMENT 'ACRESCIMO | DECRESCIMO — só quando tipo_movimento=AJUSTE',
    quantidade          DECIMAL(18,6) NOT NULL COMMENT 'Sempre positivo; o efeito é definido por tipo_movimento/direcao_ajuste',
    quantidade_anterior DECIMAL(18,6) NOT NULL,
    quantidade_nova     DECIMAL(18,6) NOT NULL,
    custo_unitario      DECIMAL(18,6) NULL COMMENT 'Só relevante em ENTRADA — usado pro recálculo de custo médio/FIFO',
    origem_tipo         VARCHAR(25)   NOT NULL COMMENT 'COMPRA | VENDA_PDV | AJUSTE_MANUAL | TRANSFERENCIA | INVENTARIO | DEVOLUCAO_PDV | KIT',
    origem_id           BIGINT        NULL COMMENT 'Id do registro que originou o movimento (ex: TransferenciaEstoque, InventarioFisicoItem)',
    usuario_id          BIGINT        NULL COMMENT 'Nulo quando o movimento nasce de chamada serviço-a-serviço (Client Credentials, ex: gestao-compras-service confirmando recebimento) — o ator humano responsável, se houver, é rastreável via origem_tipo/origem_id no serviço de origem',
    aprovador_id        BIGINT        NULL COMMENT 'Obrigatório quando o valor do ajuste/perda excede o limite configurado (ConfiguracaoEstoqueEmpresa)',
    observacao          VARCHAR(250)  NULL,
    data_movimento      DATETIME(6)   NOT NULL,
    data_criacao        DATETIME(6)   NOT NULL,
    data_atualizacao    DATETIME(6)   NOT NULL,
    criado_por          VARCHAR(100)  NULL,
    atualizado_por      VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_movimento_produto_deposito (produto_id, deposito_id),
    KEY idx_movimento_data (data_movimento),
    KEY idx_movimento_lote (lote_id),
    KEY idx_movimento_numero_serie (numero_serie_id),
    CONSTRAINT fk_movimento_deposito FOREIGN KEY (deposito_id) REFERENCES deposito (id),
    CONSTRAINT fk_movimento_lote FOREIGN KEY (lote_id) REFERENCES lote (id),
    CONSTRAINT fk_movimento_numero_serie FOREIGN KEY (numero_serie_id) REFERENCES numero_serie (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- enviar() gera TRANSFERENCIA_SAIDA no depósito origem; receber() gera
-- TRANSFERENCIA_ENTRADA no destino. A quantidade "em trânsito" mora nesta própria linha
-- (situacao=EM_TRANSITO), sem depósito virtual fake.
CREATE TABLE transferencia_estoque (
    id                      BIGINT        NOT NULL AUTO_INCREMENT,
    produto_id              BIGINT        NOT NULL,
    deposito_origem_id      BIGINT        NOT NULL,
    deposito_destino_id     BIGINT        NOT NULL,
    lote_id                 BIGINT        NULL,
    quantidade              DECIMAL(18,6) NOT NULL,
    custo_unitario          DECIMAL(18,6) NULL COMMENT 'Capturado do custo médio ponderado da origem no enviar() — usado pra atualizar o custo médio do destino no receber()',
    situacao                VARCHAR(20)   NOT NULL COMMENT 'SOLICITADA | EM_TRANSITO | CONCLUIDA | CANCELADA',
    usuario_envio_id        BIGINT        NULL,
    data_envio              DATETIME(6)   NULL,
    usuario_recebimento_id  BIGINT        NULL,
    data_recebimento        DATETIME(6)   NULL,
    observacao              VARCHAR(250)  NULL,
    data_criacao            DATETIME(6)   NOT NULL,
    data_atualizacao        DATETIME(6)   NOT NULL,
    criado_por              VARCHAR(100)  NULL,
    atualizado_por          VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_transferencia_origem (deposito_origem_id),
    KEY idx_transferencia_destino (deposito_destino_id),
    KEY idx_transferencia_situacao (situacao),
    CONSTRAINT fk_transferencia_origem FOREIGN KEY (deposito_origem_id) REFERENCES deposito (id),
    CONSTRAINT fk_transferencia_destino FOREIGN KEY (deposito_destino_id) REFERENCES deposito (id),
    CONSTRAINT fk_transferencia_lote FOREIGN KEY (lote_id) REFERENCES lote (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE inventario_fisico (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    empresa_id        BIGINT       NOT NULL,
    deposito_id       BIGINT       NOT NULL,
    tipo              VARCHAR(15)  NOT NULL COMMENT 'CEGA | POR_SALDO',
    situacao          VARCHAR(15)  NOT NULL COMMENT 'ABERTO | EM_CONTAGEM | FINALIZADO | CANCELADO',
    responsavel_id    BIGINT       NOT NULL,
    data_abertura     DATETIME(6)  NOT NULL,
    data_fechamento   DATETIME(6)  NULL,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_inventario_deposito (deposito_id),
    KEY idx_inventario_situacao (situacao),
    CONSTRAINT fk_inventario_deposito FOREIGN KEY (deposito_id) REFERENCES deposito (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- quantidade_sistema é o snapshot na abertura (escondido do contador no app cliente
-- quando inventario.tipo=CEGA — a API não esconde o campo, a decisão de UI fica pro
-- consumidor). finalizar() do cabeçalho gera AJUSTE automático (origem_tipo=INVENTARIO)
-- pra cada item com divergência != 0.
CREATE TABLE inventario_fisico_item (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    inventario_id       BIGINT        NOT NULL,
    produto_id          BIGINT        NOT NULL,
    lote_id             BIGINT        NULL,
    quantidade_sistema  DECIMAL(18,6) NOT NULL,
    quantidade_contada  DECIMAL(18,6) NULL,
    contador_id         BIGINT        NULL,
    data_contagem       DATETIME(6)   NULL,
    data_criacao        DATETIME(6)   NOT NULL,
    data_atualizacao    DATETIME(6)   NOT NULL,
    criado_por          VARCHAR(100)  NULL,
    atualizado_por      VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_inventario_item_inventario (inventario_id),
    KEY idx_inventario_item_produto (produto_id),
    CONSTRAINT fk_inventario_item_inventario FOREIGN KEY (inventario_id) REFERENCES inventario_fisico (id) ON DELETE CASCADE,
    CONSTRAINT fk_inventario_item_lote FOREIGN KEY (lote_id) REFERENCES lote (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Kit/produto composto (BOM simples) — produto_kit_id é vendido como conjunto e baixa
-- estoque de cada componente (ver endpoint de baixa em cascata).
CREATE TABLE produto_kit_componente (
    id                     BIGINT        NOT NULL AUTO_INCREMENT,
    produto_kit_id         BIGINT        NOT NULL,
    produto_componente_id  BIGINT        NOT NULL,
    quantidade             DECIMAL(18,6) NOT NULL,
    data_criacao           DATETIME(6)   NOT NULL,
    data_atualizacao       DATETIME(6)   NOT NULL,
    criado_por             VARCHAR(100)  NULL,
    atualizado_por         VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_kit_componente (produto_kit_id, produto_componente_id),
    KEY idx_kit_produto (produto_kit_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Uma linha por empresa. limite_aprovacao_ajuste: acima desse valor (quantidade ×
-- custo_unitario de um AJUSTE/PERDA), estoque_movimento.aprovador_id deixa de ser
-- opcional — mesmo padrão "aprovação de superior acima de limiar" já registrado na
-- ANÁLISE: HIERARQUIA DE USUÁRIOS do CLAUDE.md.
CREATE TABLE configuracao_estoque_empresa (
    id                       BIGINT        NOT NULL AUTO_INCREMENT,
    empresa_id               BIGINT        NOT NULL,
    metodo_custeio           VARCHAR(20)   NOT NULL DEFAULT 'MEDIA_PONDERADA' COMMENT 'MEDIA_PONDERADA | FIFO',
    limite_aprovacao_ajuste  DECIMAL(18,6) NOT NULL DEFAULT 0,
    data_criacao             DATETIME(6)   NOT NULL,
    data_atualizacao         DATETIME(6)   NOT NULL,
    criado_por               VARCHAR(100)  NULL,
    atualizado_por           VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_estoque_empresa (empresa_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Só usada quando configuracao_estoque_empresa.metodo_custeio=FIFO. Consumida em ordem
-- (mais antiga primeiro) nas saídas; custo médio ponderado é o default e não usa esta
-- tabela — o saldo já guarda custo_medio_ponderado diretamente.
CREATE TABLE camada_custo_fifo (
    id                     BIGINT        NOT NULL AUTO_INCREMENT,
    produto_id             BIGINT        NOT NULL,
    deposito_id            BIGINT        NOT NULL,
    lote_id                BIGINT        NULL,
    quantidade_restante    DECIMAL(18,6) NOT NULL,
    custo_unitario         DECIMAL(18,6) NOT NULL,
    data_entrada           DATETIME(6)   NOT NULL,
    movimento_entrada_id   BIGINT        NOT NULL,
    data_criacao           DATETIME(6)   NOT NULL,
    data_atualizacao       DATETIME(6)   NOT NULL,
    criado_por             VARCHAR(100)  NULL,
    atualizado_por         VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_fifo_produto_deposito (produto_id, deposito_id, data_entrada),
    CONSTRAINT fk_fifo_deposito FOREIGN KEY (deposito_id) REFERENCES deposito (id),
    CONSTRAINT fk_fifo_lote FOREIGN KEY (lote_id) REFERENCES lote (id),
    CONSTRAINT fk_fifo_movimento_entrada FOREIGN KEY (movimento_entrada_id) REFERENCES estoque_movimento (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
