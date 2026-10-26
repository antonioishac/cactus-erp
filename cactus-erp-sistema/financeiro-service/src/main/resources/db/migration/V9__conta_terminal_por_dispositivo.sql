-- Conta do terminal do PDV passa a ser única por empresa + DISPOSITIVO + terminal. O id do terminal
-- (PDV_CAIXA.ID) é local de cada banco de PDV: duas instalações na mesma loja podem ter um "Caixa 1"
-- cada, e com a chave antiga (empresa, terminal) o dinheiro das duas cairia na mesma conta.
-- Contas criadas antes desta migration ficam com pdv_dispositivo_id NULL e não são mais achadas pela
-- sincronização (a próxima mensagem do terminal cria a conta nova, com o dispositivo).
ALTER TABLE conta_bancaria
    ADD COLUMN pdv_dispositivo_id VARCHAR(30) NULL COMMENT 'Dispositivo (instalação) do PDV dono do terminal — junto com pdv_caixa_id' AFTER pdv_caixa_id,
    DROP INDEX uk_conta_bancaria_pdv_caixa,
    ADD UNIQUE KEY uk_conta_bancaria_pdv_terminal (empresa_id, pdv_dispositivo_id, pdv_caixa_id);
