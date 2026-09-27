-- Empresa (multi-filial) + Pessoa (base) com especialização Física/Jurídica + Cliente.
--
-- Colunas de auditoria (data_criacao, data_atualizacao, criado_por, atualizado_por) em
-- toda tabela: preenchidas automaticamente pelo Spring Data JPA Auditing
-- (ver BaseEntity/SecurityAuditorAware em cactus-common).

CREATE TABLE empresa (
    id                      BIGINT       NOT NULL AUTO_INCREMENT,
    razao_social            VARCHAR(150) NOT NULL,
    nome_fantasia           VARCHAR(150) NULL,
    cnpj                    VARCHAR(14)  NOT NULL,
    inscricao_estadual      VARCHAR(20)  NULL,
    crt                     CHAR(1)      NULL COMMENT '1=Simples Nacional | 2=Simples Nacional excesso sublimite | 3=Regime Normal',
    email                   VARCHAR(250) NULL,
    site                    VARCHAR(250) NULL,
    contato                 VARCHAR(100) NULL,
    data_constituicao       DATE         NULL,
    tipo                    VARCHAR(10)  NULL COMMENT 'MATRIZ | FILIAL',
    tipo_estabelecimento    VARCHAR(20)  NOT NULL DEFAULT 'VAREJO' COMMENT 'VAREJO | RESTAURANTE | BAR | SUPERMERCADO | AUTOCENTER | OUTRO',
    inscricao_junta_comercial      VARCHAR(30) NULL,
    data_inscricao_junta_comercial DATE        NULL,
    cei                     VARCHAR(12)  NULL,
    codigo_cnae_principal   VARCHAR(7)   NULL,
    logradouro              VARCHAR(150) NULL,
    numero                  VARCHAR(10)  NULL,
    complemento             VARCHAR(100) NULL,
    bairro                  VARCHAR(100) NULL,
    cep                     VARCHAR(8)   NULL,
    municipio               VARCHAR(100) NULL,
    uf                      CHAR(2)      NULL,
    codigo_ibge_municipio   INT          NULL,
    codigo_ibge_uf          INT          NULL,
    telefone                VARCHAR(15)  NULL,
    situacao                CHAR(1)      NOT NULL DEFAULT 'A' COMMENT 'A=Ativo | I=Inativo',
    data_criacao            DATETIME(6)  NOT NULL,
    data_atualizacao        DATETIME(6)  NOT NULL,
    criado_por              VARCHAR(100) NULL,
    atualizado_por          VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_empresa_cnpj (cnpj),
    KEY idx_empresa_situacao (situacao)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE empresa_regime_tributario (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    empresa_id        BIGINT       NOT NULL,
    ano_calendario    INT          NOT NULL,
    crt               CHAR(1)      NOT NULL,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_empresa_regime_ano (empresa_id, ano_calendario),
    CONSTRAINT fk_empresa_regime_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- modo_comanda: CHAR(1) livre (ver ConfiguracaoOperacionalLoja.java pro porquê de não ser enum).
CREATE TABLE configuracao_operacional_loja (
    id                                  BIGINT        NOT NULL AUTO_INCREMENT,
    empresa_id                          BIGINT        NOT NULL,
    permite_mesas                       TINYINT(1)    NOT NULL DEFAULT 0,
    permite_comandas                    TINYINT(1)    NOT NULL DEFAULT 0,
    permite_delivery                    TINYINT(1)    NOT NULL DEFAULT 0,
    modo_comanda                        CHAR(1)       NULL DEFAULT 'V',
    permite_mesa_avulsa                 TINYINT(1)    NOT NULL DEFAULT 0,
    limite_desconto_item_percentual     DECIMAL(5,2)  NULL DEFAULT 5.00,
    limite_desconto_venda_percentual    DECIMAL(5,2)  NULL DEFAULT 10.00,
    limite_desconto_venda_valor         DECIMAL(18,6) NULL DEFAULT 50.000000,
    limite_acrescimo_item_percentual    DECIMAL(5,2)  NULL DEFAULT 2.00,
    limite_acrescimo_venda_percentual   DECIMAL(5,2)  NULL DEFAULT 2.00,
    permite_desconto_acima_limite       TINYINT(1)    NOT NULL DEFAULT 1,
    data_criacao                        DATETIME(6)   NOT NULL,
    data_atualizacao                    DATETIME(6)   NOT NULL,
    criado_por                          VARCHAR(100)  NULL,
    atualizado_por                      VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_operacional_empresa (empresa_id),
    CONSTRAINT fk_config_operacional_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Validado contra BANCO_PDV.md.PDV_TIPO_PAGAMENTO: "Dados devem ser importados da retaguarda".
CREATE TABLE forma_pagamento (
    id                      BIGINT       NOT NULL AUTO_INCREMENT,
    codigo                  VARCHAR(3)   NOT NULL,
    descricao               VARCHAR(20)  NOT NULL,
    icone                   VARCHAR(50)  NULL,
    tef                     TINYINT(1)   NOT NULL DEFAULT 0,
    imprime_vinculado       TINYINT(1)   NOT NULL DEFAULT 0,
    permite_troco           TINYINT(1)   NOT NULL DEFAULT 0,
    tef_tipo_gp             VARCHAR(20)  NOT NULL DEFAULT 'NENHUM',
    gera_parcelas           TINYINT(1)   NOT NULL DEFAULT 0,
    codigo_pagamento_nfce   VARCHAR(2)   NULL,
    situacao                CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao            DATETIME(6)  NOT NULL,
    data_atualizacao        DATETIME(6)  NOT NULL,
    criado_por              VARCHAR(100) NULL,
    atualizado_por          VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_forma_pagamento_codigo (codigo)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE pessoa (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    nome              VARCHAR(150) NOT NULL,
    tipo_pessoa       CHAR(1)      NOT NULL COMMENT 'F=Fisica | J=Juridica',
    email             VARCHAR(250) NULL,
    situacao          CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_pessoa_nome (nome),
    KEY idx_pessoa_tipo (tipo_pessoa),
    KEY idx_pessoa_situacao (situacao)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Chave primária compartilhada com pessoa (@OneToOne @MapsId): pessoa_fisica.id É pessoa.id.
CREATE TABLE pessoa_fisica (
    id                BIGINT       NOT NULL,
    cpf               VARCHAR(11)  NOT NULL,
    rg                VARCHAR(20)  NULL,
    orgao_rg          VARCHAR(20)  NULL,
    data_nascimento   DATE         NULL,
    data_emissao_rg   DATE         NULL,
    sexo              CHAR(1)      NULL,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pessoa_fisica_cpf (cpf),
    CONSTRAINT fk_pessoa_fisica_pessoa FOREIGN KEY (id) REFERENCES pessoa (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE pessoa_juridica (
    id                    BIGINT       NOT NULL,
    cnpj                  VARCHAR(14)  NOT NULL,
    nome_fantasia         VARCHAR(150) NULL,
    inscricao_estadual    VARCHAR(20)  NULL,
    inscricao_municipal   VARCHAR(20)  NULL,
    data_constituicao     DATE         NULL,
    data_criacao          DATETIME(6)  NOT NULL,
    data_atualizacao      DATETIME(6)  NOT NULL,
    criado_por            VARCHAR(100) NULL,
    atualizado_por        VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pessoa_juridica_cnpj (cnpj),
    CONSTRAINT fk_pessoa_juridica_pessoa FOREIGN KEY (id) REFERENCES pessoa (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE pessoa_endereco (
    id                     BIGINT       NOT NULL AUTO_INCREMENT,
    pessoa_id              BIGINT       NOT NULL,
    logradouro             VARCHAR(150) NULL,
    numero                 VARCHAR(10)  NULL,
    complemento            VARCHAR(100) NULL,
    bairro                 VARCHAR(100) NULL,
    cep                    VARCHAR(8)   NULL,
    municipio              VARCHAR(100) NULL,
    uf                     CHAR(2)      NULL,
    codigo_ibge_municipio  INT          NULL,
    codigo_ibge_uf         INT          NULL,
    principal              TINYINT(1)   NOT NULL DEFAULT 0,
    entrega                TINYINT(1)   NOT NULL DEFAULT 0,
    cobranca               TINYINT(1)   NOT NULL DEFAULT 0,
    correspondencia        TINYINT(1)   NOT NULL DEFAULT 0,
    data_criacao           DATETIME(6)  NOT NULL,
    data_atualizacao       DATETIME(6)  NOT NULL,
    criado_por             VARCHAR(100) NULL,
    atualizado_por         VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_pessoa_endereco_pessoa (pessoa_id),
    CONSTRAINT fk_pessoa_endereco_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE pessoa_contato (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    pessoa_id         BIGINT       NOT NULL,
    tipo_contato      VARCHAR(20)  NOT NULL COMMENT 'EMAIL | RESPONSAVEL | OUTRO',
    valor             VARCHAR(250) NOT NULL,
    principal         TINYINT(1)   NOT NULL DEFAULT 0,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_pessoa_contato_pessoa (pessoa_id),
    CONSTRAINT fk_pessoa_contato_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE pessoa_telefone (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    pessoa_id         BIGINT       NOT NULL,
    tipo_telefone     VARCHAR(20)  NOT NULL COMMENT 'FIXO | CELULAR | COMERCIAL | WHATSAPP',
    numero            VARCHAR(15)  NOT NULL,
    principal         TINYINT(1)   NOT NULL DEFAULT 0,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_pessoa_telefone_pessoa (pessoa_id),
    CONSTRAINT fk_pessoa_telefone_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE cliente (
    id                       BIGINT        NOT NULL AUTO_INCREMENT,
    pessoa_id                BIGINT        NOT NULL,
    url                      VARCHAR(250)  NULL,
    limite_credito           DECIMAL(18,6) NULL,
    taxa_desconto            DECIMAL(5,2)  NULL,
    fidelidade_aviso         VARCHAR(20)   NULL COMMENT 'QUANTIDADE | VALOR',
    fidelidade_quantidade    INT           NULL,
    fidelidade_valor         DECIMAL(18,6) NULL,
    data_cadastro     DATE          NOT NULL,
    situacao          CHAR(1)       NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_cliente_pessoa (pessoa_id),
    KEY idx_cliente_situacao (situacao),
    CONSTRAINT fk_cliente_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Cargo/Setor/Colaborador — validado contra fenix.sql (RH básico) e BANCO_PDV.md (comissão/entregador).

CREATE TABLE cargo (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    nome              VARCHAR(100)  NOT NULL,
    descricao         VARCHAR(250)  NULL,
    salario           DECIMAL(18,6) NULL,
    cbo_1994          VARCHAR(10)   NULL,
    cbo_2002          VARCHAR(10)   NULL,
    situacao          CHAR(1)       NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_cargo_nome (nome)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE setor (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    nome              VARCHAR(100)  NOT NULL,
    descricao         VARCHAR(250)  NULL,
    situacao          CHAR(1)       NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_setor_nome (nome)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE colaborador (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    pessoa_id           BIGINT        NOT NULL,
    cargo_id            BIGINT        NULL,
    setor_id            BIGINT        NULL,
    matricula           VARCHAR(10)   NULL,
    data_admissao       DATE          NULL,
    data_demissao       DATE          NULL,
    ctps_numero         VARCHAR(10)   NULL,
    ctps_serie          VARCHAR(10)   NULL,
    ctps_data_expedicao DATE          NULL,
    ctps_uf             CHAR(2)       NULL,
    comissao_vista      DECIMAL(18,6) NULL,
    comissao_prazo      DECIMAL(18,6) NULL,
    entregador_veiculo  VARCHAR(20)   NULL COMMENT 'CARRO | MOTO | BICICLETA | APLICATIVO',
    observacao          VARCHAR(250)  NULL,
    situacao            CHAR(1)       NOT NULL DEFAULT 'A',
    data_criacao        DATETIME(6)   NOT NULL,
    data_atualizacao    DATETIME(6)   NOT NULL,
    criado_por          VARCHAR(100)  NULL,
    atualizado_por      VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_colaborador_pessoa (pessoa_id),
    KEY idx_colaborador_cargo (cargo_id),
    KEY idx_colaborador_setor (setor_id),
    KEY idx_colaborador_situacao (situacao),
    CONSTRAINT fk_colaborador_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa (id),
    CONSTRAINT fk_colaborador_cargo FOREIGN KEY (cargo_id) REFERENCES cargo (id),
    CONSTRAINT fk_colaborador_setor FOREIGN KEY (setor_id) REFERENCES setor (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
