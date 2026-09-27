package br.com.cactusdigital.erp.sincronizacao.service;

import br.com.cactusdigital.erp.sincronizacao.dto.LoteSincronizacaoRequest;
import br.com.cactusdigital.erp.sincronizacao.dto.LoteSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.RegistroSincronizacaoItemRequest;
import br.com.cactusdigital.erp.sincronizacao.dto.ResultadoRegistroResponse;
import br.com.cactusdigital.erp.sincronizacao.entity.LoteSincronizacao;
import br.com.cactusdigital.erp.sincronizacao.entity.RegistroSincronizacao;
import br.com.cactusdigital.erp.sincronizacao.entity.StatusRegistroSincronizacao;
import br.com.cactusdigital.erp.sincronizacao.entity.TipoRegistroSincronizacao;
import br.com.cactusdigital.erp.sincronizacao.repository.LoteSincronizacaoRepository;
import br.com.cactusdigital.erp.sincronizacao.repository.RegistroSincronizacaoRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Recebe {@code POST /api/v1/sync/lotes} — valida o envelope, checa idempotência,
 * persiste em {@code RECEBIDO} (outbox — ver {@code OutboxRelayService} pra publicação).
 * Ver seção 5.2/5.3 do FASE6_SINCRONIZACAO_PDV.md.
 * <p>
 * <b>Escopo desta passada (registrado, não escondido)</b>: {@code payload} é aceito no
 * formato canônico do ERP (ex., pra {@code ESTOQUE_MOVIMENTO}: {@code produtoId}/
 * {@code depositoId}/{@code tipoMovimento}/{@code quantidade}/...,
 * mesmos nomes de {@code EstoqueMovimentoRequest}), não no dialeto de coluna do PDV
 * ({@code ID_PRODUTO}, tipo {@code E/S/A/P/D} etc.) — a tradução campo a campo do
 * formato real do PDV é um refinamento mecânico a fazer quando o bridge/adaptador do
 * lado do PDV existir de fato (mesma régua já usada pra balança autônoma, seção 7.2).
 * <p>
 * Validação estrutural do {@code payload} (não de negócio) delegada a
 * {@link RegistroPayloadValidator} — item que falha essa validação vira
 * {@code REJEITADO} na resposta, sem gerar {@link RegistroSincronizacao} nem consumir a
 * chave de idempotência (ver Javadoc daquela classe pro histórico do porquê).
 */
@Service
@RequiredArgsConstructor
public class LoteSincronizacaoPushService {

    private final LoteSincronizacaoRepository loteRepository;
    private final RegistroSincronizacaoRepository registroRepository;
    private final RegistroPayloadValidator payloadValidator;
    private final ObjectMapper objectMapper;

    @Transactional
    public LoteSincronizacaoResponse receber(LoteSincronizacaoRequest request) {
        List<ResultadoRegistroResponse> resultados = new ArrayList<>();
        int aceitos = 0;
        int rejeitados = 0;

        LoteSincronizacao lote = LoteSincronizacao.builder()
                .lojaId(request.lojaId())
                .dispositivoId(request.dispositivoId())
                .tipoRegistro(request.tipoRegistro())
                .totalRegistros(request.registros().size())
                .totalAceitos(0)
                .totalRejeitados(0)
                .build();
        lote = loteRepository.save(lote);

        for (RegistroSincronizacaoItemRequest item : request.registros()) {
            var erroValidacao = payloadValidator.validar(request.tipoRegistro(), item.payload());
            if (erroValidacao.isPresent()) {
                resultados.add(ResultadoRegistroResponse.rejeitado(item.idOrigem(), erroValidacao.get()));
                rejeitados++;
                continue;
            }

            String chave = chaveIdempotencia(request.lojaId(), request.dispositivoId(), request.tipoRegistro(), item.idOrigem());

            var existente = registroRepository.findByChaveIdempotencia(chave);
            if (existente.isPresent()) {
                // Reenvio duplicado (seção 5.3/8.3) — devolve o resultado já registrado, sem duplicar.
                resultados.add(ResultadoRegistroResponse.aceito(item.idOrigem(), chave));
                aceitos++;
                continue;
            }

            RegistroSincronizacao registro = RegistroSincronizacao.builder()
                    .lote(lote)
                    .lojaId(request.lojaId())
                    .dispositivoId(request.dispositivoId())
                    .tipoRegistro(request.tipoRegistro())
                    .idOrigem(item.idOrigem())
                    .chaveIdempotencia(chave)
                    .payload(objectMapper.writeValueAsString(item.payload()))
                    .status(StatusRegistroSincronizacao.RECEBIDO)
                    .build();
            registroRepository.save(registro);
            resultados.add(ResultadoRegistroResponse.aceito(item.idOrigem(), chave));
            aceitos++;
        }

        lote.setTotalAceitos(aceitos);
        lote.setTotalRejeitados(rejeitados);
        return new LoteSincronizacaoResponse(lote.getId(), resultados);
    }

    private static String chaveIdempotencia(Long lojaId, String dispositivoId, TipoRegistroSincronizacao tipoRegistro, String idOrigem) {
        return lojaId + ":" + dispositivoId + ":" + tipoRegistro + ":" + idOrigem;
    }
}
