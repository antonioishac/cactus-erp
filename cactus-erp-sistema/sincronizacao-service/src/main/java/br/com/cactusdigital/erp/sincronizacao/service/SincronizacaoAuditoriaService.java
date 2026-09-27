package br.com.cactusdigital.erp.sincronizacao.service;

import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.sincronizacao.dto.LoteSincronizacaoResumoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.RegistroSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.entity.LoteSincronizacao;
import br.com.cactusdigital.erp.sincronizacao.entity.RegistroSincronizacao;
import br.com.cactusdigital.erp.sincronizacao.entity.StatusRegistroSincronizacao;
import br.com.cactusdigital.erp.sincronizacao.exception.SincronizacaoErrorCode;
import br.com.cactusdigital.erp.sincronizacao.repository.LoteSincronizacaoRepository;
import br.com.cactusdigital.erp.sincronizacao.repository.RegistroSincronizacaoRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ledger de auditoria de sincronização — consulta e reprocessamento manual. Ver seção
 * 4.2 (item 3) e 6.4 do FASE6_SINCRONIZACAO_PDV.md.
 */
@Service
@RequiredArgsConstructor
public class SincronizacaoAuditoriaService {

    private final LoteSincronizacaoRepository loteRepository;
    private final RegistroSincronizacaoRepository registroRepository;

    @Transactional(readOnly = true)
    public Page<LoteSincronizacaoResumoResponse> listarLotes(Long lojaId, Pageable pageable) {
        Page<LoteSincronizacao> pagina = lojaId != null ? loteRepository.findByLojaId(lojaId, pageable) : loteRepository.findAll(pageable);
        return pagina.map(SincronizacaoAuditoriaService::paraResumo);
    }

    @Transactional(readOnly = true)
    public Page<RegistroSincronizacaoResponse> listarRegistros(StatusRegistroSincronizacao status, Long lojaId, Pageable pageable) {
        return registroRepository.buscar(status, lojaId, pageable).map(SincronizacaoAuditoriaService::paraResponse);
    }

    /**
     * Devolve o registro pra {@code RECEBIDO} — não republica direto no RabbitMQ aqui,
     * reaproveita o mesmo caminho do {@link OutboxRelayService} no próximo ciclo (mesma
     * lógica de publicação, sem duplicar). Ação nominal: {@code usuarioId} de quem
     * reprocessou vem do JWT no controller, nunca do corpo da requisição — mesmo padrão
     * de rastreabilidade nominal já usado em aprovação de ajuste/pedido.
     */
    @Transactional
    public RegistroSincronizacaoResponse reprocessar(Long registroId, Long usuarioId) {
        RegistroSincronizacao registro = registroRepository.findById(registroId)
                .orElseThrow(() -> NotFoundException.of("Registro de sincronização", registroId));
        if (registro.getStatus() != StatusRegistroSincronizacao.ERRO) {
            throw new ConflictException(SincronizacaoErrorCode.REGISTRO_NAO_ESTA_EM_ERRO);
        }
        registro.setStatus(StatusRegistroSincronizacao.RECEBIDO);
        registro.setUsuarioReprocessouId(usuarioId);
        registro.setDataReprocessamento(Instant.now());
        registroRepository.save(registro);
        return paraResponse(registro);
    }

    private static LoteSincronizacaoResumoResponse paraResumo(LoteSincronizacao lote) {
        return new LoteSincronizacaoResumoResponse(
                lote.getId(), lote.getLojaId(), lote.getDispositivoId(), lote.getTipoRegistro().name(),
                lote.getTotalRegistros(), lote.getTotalAceitos(), lote.getTotalRejeitados(), lote.getDataCriacao());
    }

    private static RegistroSincronizacaoResponse paraResponse(RegistroSincronizacao registro) {
        return new RegistroSincronizacaoResponse(
                registro.getId(), registro.getLote().getId(), registro.getLojaId(), registro.getDispositivoId(),
                registro.getTipoRegistro().name(), registro.getIdOrigem(), registro.getChaveIdempotencia(),
                registro.getStatus().name(), registro.getTentativas(), registro.getMotivoErro(),
                registro.getDataCriacao(), registro.getDataAtualizacao());
    }
}
