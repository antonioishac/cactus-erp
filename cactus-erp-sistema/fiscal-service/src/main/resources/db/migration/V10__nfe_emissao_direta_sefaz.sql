-- Emissão direta à SEFAZ (biblioteca wmixvideo/nfe), sem o ACBr Monitor.
-- cStat do retorno fica numa coluna própria (consulta/relatório de rejeições sem parsear texto).
-- O XML processado (nfeProc: NF-e assinada + protocolo) é guarda obrigatória por 5 anos e pesa dezenas de KB:
-- fica numa tabela 1:1 separada, para a listagem/paginação de notas nunca carregá-lo.
ALTER TABLE nota_fiscal_eletronica
    ADD COLUMN codigo_status_sefaz VARCHAR(3) NULL COMMENT 'cStat do último retorno da SEFAZ' AFTER mensagem_sefaz,
    MODIFY COLUMN situacao VARCHAR(15) NOT NULL COMMENT 'RASCUNHO | ENVIADA | AUTORIZADA | DENEGADA | REJEITADA | ERRO | CANCELADA',
    ADD KEY idx_nota_fiscal_chave_acesso (chave_acesso);

CREATE TABLE nota_fiscal_eletronica_xml (
    nota_fiscal_id    BIGINT       NOT NULL,
    xml_processado    MEDIUMTEXT   NOT NULL COMMENT 'nfeProc (NF-e assinada + protNFe), como devolvido pela SEFAZ',
    data_criacao      DATETIME(6)  NOT NULL,
    PRIMARY KEY (nota_fiscal_id),
    CONSTRAINT fk_nfe_xml_nota FOREIGN KEY (nota_fiscal_id) REFERENCES nota_fiscal_eletronica (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
