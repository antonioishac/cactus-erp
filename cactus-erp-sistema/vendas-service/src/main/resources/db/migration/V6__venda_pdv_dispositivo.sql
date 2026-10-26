-- Dispositivo (instalação) do PDV que enviou a venda. O id do terminal é local de cada banco de PDV,
-- então o terminal só é identificado de verdade por dispositivo + terminal (ver financeiro V9).
ALTER TABLE venda_pdv
    ADD COLUMN pdv_dispositivo_id VARCHAR(30) NULL COMMENT 'dispositivoId do envio' AFTER pdv_caixa_id;
