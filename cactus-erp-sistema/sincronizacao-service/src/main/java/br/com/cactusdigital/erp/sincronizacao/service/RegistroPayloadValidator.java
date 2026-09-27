package br.com.cactusdigital.erp.sincronizacao.service;

import br.com.cactusdigital.erp.sincronizacao.entity.TipoRegistroSincronizacao;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

/**
 * Validação estrutural de forma (não de negócio) do {@code payload} de cada item do
 * envelope — fecha o gap encontrado na validação ao vivo de 2026-08-31 (ver
 * FASE6_SINCRONIZACAO_PDV.md, seção 11.1): antes desta classe, {@code REJEITADO} do
 * contrato da seção 5.2 era código morto — qualquer JSON sintaticamente válido virava
 * {@code ACEITO}, e um payload com campo obrigatório faltando só falhava depois, de
 * forma assíncrona, no consumidor de domínio (esgotando retry até cair em DLQ/ERRO).
 * <p>
 * Deliberadamente superficial: checa só presença/não-nulidade dos campos que o
 * consumidor de domínio exige pra sequer tentar processar — não valida regra de
 * negócio (produto existir, depósito pertencer à empresa, etc.), isso continua sendo
 * responsabilidade do serviço dono (ver Javadoc de {@code LoteSincronizacaoPushService},
 * "não decide regra de negócio nenhuma"). Um item com erro de forma é rejeitado aqui,
 * sem gerar {@link br.com.cactusdigital.erp.sincronizacao.entity.RegistroSincronizacao}
 * nem consumir a chave de idempotência — o PDV corrige e reenvia com o mesmo
 * {@code idOrigem} sem problema.
 */
@Component
public class RegistroPayloadValidator {

    public Optional<String> validar(TipoRegistroSincronizacao tipoRegistro, JsonNode payload) {
        return switch (tipoRegistro) {
            case ESTOQUE_MOVIMENTO -> validarEstoqueMovimento(payload);
        };
    }

    private Optional<String> validarEstoqueMovimento(JsonNode payload) {
        List<String> ausentes = new ArrayList<>();
        exigir(payload, "produtoId", ausentes);
        exigir(payload, "depositoId", ausentes);
        exigir(payload, "tipoMovimento", ausentes);
        exigir(payload, "quantidade", ausentes);

        if (!ausentes.isEmpty()) {
            return Optional.of("Campo(s) obrigatório(s) ausente(s) no payload: " + String.join(", ", ausentes));
        }
        return Optional.empty();
    }

    private void exigir(JsonNode payload, String campo, List<String> ausentes) {
        JsonNode valor = payload.get(campo);
        if (valor == null || valor.isNull()) {
            ausentes.add(campo);
        }
    }
}
