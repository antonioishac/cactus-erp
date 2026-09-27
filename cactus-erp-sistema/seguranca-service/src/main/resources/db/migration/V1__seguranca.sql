-- Perfilamento (RBAC por filial): Modulo -> Funcionalidade -> Papel -> Usuario,
-- escopado por empresa via usuario_papel_empresa.
--
-- Extraído do cadastro-base-service pra um serviço próprio (seguranca-service):
-- validar um JWT não exige chamada síncrona ao emissor (o Resource Server cacheia a
-- chave pública via JWKS), e o catálogo de Funcionalidade é cross-cutting — protege
-- endpoint de qualquer módulo, não só cadastro base.
--
-- colaborador_id (usuario) e empresa_id (usuario_papel_empresa) são referências SOLTAS,
-- sem FOREIGN KEY: Colaborador e Empresa moram no cadastro-base-service, em outro
-- schema. Sem validação de existência na v1 (decisão registrada no roadmap) — ver
-- Javadoc de Usuario/UsuarioPapelEmpresa no código.

CREATE TABLE modulo (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    codigo            VARCHAR(30)  NOT NULL COMMENT 'SEGURANCA | CADASTRO_BASE | COMPRAS | ESTOQUE | GED | BI',
    nome              VARCHAR(100) NOT NULL,
    situacao          CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_modulo_codigo (codigo)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE funcionalidade (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    modulo_id         BIGINT       NOT NULL,
    codigo            VARCHAR(60)  NOT NULL COMMENT 'Padrao MODULO_RECURSO_ACAO, ex: CADASTRO_PRODUTO_CRIAR',
    nome              VARCHAR(150) NOT NULL,
    situacao          CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_funcionalidade_codigo (codigo),
    KEY idx_funcionalidade_modulo (modulo_id),
    CONSTRAINT fk_funcionalidade_modulo FOREIGN KEY (modulo_id) REFERENCES modulo (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE papel (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    nome              VARCHAR(60)  NOT NULL,
    descricao         VARCHAR(250) NULL,
    situacao          CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_papel_nome (nome)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Tabela de associação pura (@ManyToMany) — sem colunas de auditoria própria.
CREATE TABLE papel_funcionalidade (
    papel_id           BIGINT NOT NULL,
    funcionalidade_id  BIGINT NOT NULL,
    PRIMARY KEY (papel_id, funcionalidade_id),
    KEY idx_papel_funcionalidade_funcionalidade (funcionalidade_id),
    CONSTRAINT fk_papel_funcionalidade_papel FOREIGN KEY (papel_id) REFERENCES papel (id) ON DELETE CASCADE,
    CONSTRAINT fk_papel_funcionalidade_funcionalidade FOREIGN KEY (funcionalidade_id) REFERENCES funcionalidade (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- colaborador_id opcional e SEM FK (Colaborador mora no cadastro-base-service) — ver Usuario.java pro porquê.
CREATE TABLE usuario (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    colaborador_id      BIGINT       NULL,
    login               VARCHAR(60)  NOT NULL,
    senha_hash          VARCHAR(100) NOT NULL,
    nome                VARCHAR(150) NOT NULL,
    email               VARCHAR(250) NULL,
    ativo               TINYINT(1)   NOT NULL DEFAULT 1,
    bloqueado           TINYINT(1)   NOT NULL DEFAULT 0,
    data_ultimo_login   DATETIME(6)  NULL,
    data_criacao        DATETIME(6)  NOT NULL,
    data_atualizacao    DATETIME(6)  NOT NULL,
    criado_por          VARCHAR(100) NULL,
    atualizado_por      VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_login (login),
    KEY idx_usuario_ativo (ativo),
    KEY idx_usuario_colaborador (colaborador_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- empresa_id SEM FK (Empresa mora no cadastro-base-service) — ver UsuarioPapelEmpresa.java pro porquê.
CREATE TABLE usuario_papel_empresa (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    usuario_id        BIGINT       NOT NULL,
    papel_id          BIGINT       NOT NULL,
    empresa_id        BIGINT       NOT NULL,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_papel_empresa (usuario_id, papel_id, empresa_id),
    KEY idx_upe_usuario (usuario_id),
    KEY idx_upe_empresa (empresa_id),
    KEY idx_upe_papel (papel_id),
    CONSTRAINT fk_upe_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_upe_papel FOREIGN KEY (papel_id) REFERENCES papel (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
