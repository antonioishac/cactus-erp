-- Schema oficial do Spring Authorization Server 7.1.1, extraído diretamente de
-- org/springframework/security/oauth2/server/authorization/**/*.sql dentro do jar
-- spring-security-oauth2-authorization-server (não reescrito à mão) e adaptado só no
-- necessário para MySQL, conforme o próprio comentário dos scripts originais:
--   - 'blob' -> 'MEDIUMBLOB' (blob puro do MySQL tem só 64KB; tokens/atributos serializados
--     de authorization_code/access_token/refresh_token podem passar disso).
--   - JDBC URL já configurado com preserveInstants/connectionTimeZone=UTC (application.yml),
--     conforme recomendado pelos scripts originais para MySQL.

CREATE TABLE oauth2_registered_client (
    id                              VARCHAR(100)  NOT NULL,
    client_id                       VARCHAR(100)  NOT NULL,
    client_id_issued_at             TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    client_secret                   VARCHAR(200)  NULL,
    client_secret_expires_at        TIMESTAMP     NULL,
    client_name                     VARCHAR(200)  NOT NULL,
    client_authentication_methods   VARCHAR(1000) NOT NULL,
    authorization_grant_types       VARCHAR(1000) NOT NULL,
    redirect_uris                   VARCHAR(1000) NULL,
    post_logout_redirect_uris       VARCHAR(1000) NULL,
    scopes                          VARCHAR(1000) NOT NULL,
    client_settings                 VARCHAR(2000) NOT NULL,
    token_settings                  VARCHAR(2000) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE oauth2_authorization (
    id                              VARCHAR(100)  NOT NULL,
    registered_client_id            VARCHAR(100)  NOT NULL,
    principal_name                  VARCHAR(200)  NOT NULL,
    authorization_grant_type        VARCHAR(100)  NOT NULL,
    authorized_scopes               VARCHAR(1000) NULL,
    attributes                      MEDIUMBLOB    NULL,
    state                           VARCHAR(500)  NULL,
    authorization_code_value        MEDIUMBLOB    NULL,
    authorization_code_issued_at    TIMESTAMP     NULL,
    authorization_code_expires_at   TIMESTAMP     NULL,
    authorization_code_metadata     MEDIUMBLOB    NULL,
    access_token_value              MEDIUMBLOB    NULL,
    access_token_issued_at          TIMESTAMP     NULL,
    access_token_expires_at         TIMESTAMP     NULL,
    access_token_metadata           MEDIUMBLOB    NULL,
    access_token_type               VARCHAR(100)  NULL,
    access_token_scopes             VARCHAR(1000) NULL,
    oidc_id_token_value             MEDIUMBLOB    NULL,
    oidc_id_token_issued_at         TIMESTAMP     NULL,
    oidc_id_token_expires_at        TIMESTAMP     NULL,
    oidc_id_token_metadata          MEDIUMBLOB    NULL,
    refresh_token_value             MEDIUMBLOB    NULL,
    refresh_token_issued_at         TIMESTAMP     NULL,
    refresh_token_expires_at        TIMESTAMP     NULL,
    refresh_token_metadata          MEDIUMBLOB    NULL,
    user_code_value                 MEDIUMBLOB    NULL,
    user_code_issued_at             TIMESTAMP     NULL,
    user_code_expires_at            TIMESTAMP     NULL,
    user_code_metadata               MEDIUMBLOB   NULL,
    device_code_value               MEDIUMBLOB    NULL,
    device_code_issued_at           TIMESTAMP     NULL,
    device_code_expires_at          TIMESTAMP     NULL,
    device_code_metadata            MEDIUMBLOB    NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE oauth2_authorization_consent (
    registered_client_id  VARCHAR(100)  NOT NULL,
    principal_name        VARCHAR(200)  NOT NULL,
    authorities           VARCHAR(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
