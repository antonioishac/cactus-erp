package br.com.cactuserp.pdv.service.fiscal;

import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import br.com.cactuserp.pdv.entity.StatusFiscal;
import br.com.cactuserp.pdv.repository.PdvConfiguracaoRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Reserva o número da NFC-e na série do terminal. Transação própria e curta ({@code REQUIRES_NEW}) com lock na linha
 * de configuração do caixa: o contador e o cupom são gravados juntos e confirmados antes de qualquer chamada à SEFAZ —
 * se o processo cair no meio do envio, o cupom já está com o número (e a mesma data de emissão), e o reprocessamento
 * refaz o documento idêntico. Nunca devolve número ao contador: número sem documento vai para
 * {@code PDV_NFCE_NUMERO_PENDENTE}.
 */
@Service
public class NumeracaoNfceService {

    private final PdvConfiguracaoRepository configuracaoRepository;
    private final JdbcTemplate jdbc;

    public NumeracaoNfceService(PdvConfiguracaoRepository configuracaoRepository, JdbcTemplate jdbc) {
        this.configuracaoRepository = configuracaoRepository;
        this.jdbc = jdbc;
    }

    /**
     * Reserva o próximo número e o grava no cupom (série, número, tipo de emissão, data de emissão, status PENDENTE).
     *
     * @param tipoEmissao "1" normal ou "9" contingência offline
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public NumeroNfce reservarParaCupom(Long caixaId, Long cupomId, String tipoEmissao, LocalDateTime dataHoraEmissao) {
        PdvConfiguracao terminal = configuracaoRepository.travarPorCaixa(caixaId)
                .orElseThrow(() -> new NfceConfiguracaoException("Terminal (caixa #" + caixaId + ") sem configuração de PDV."));
        Integer serie = terminal.getNfceSerie();
        Long proximo = terminal.getNfceProximoNumero();
        if (serie == null || serie < 0 || serie > 999) {
            throw new NfceConfiguracaoException("Série de NFC-e do terminal não configurada (PDV_CONFIGURACAO.NFCE_SERIE, 0 a 999).");
        }
        if (proximo == null || proximo < 1 || proximo > 999_999_999L) {
            throw new NfceConfiguracaoException("Próximo número de NFC-e do terminal não configurado (PDV_CONFIGURACAO.NFCE_PROXIMO_NUMERO).");
        }
        terminal.setNfceProximoNumero(proximo + 1);
        configuracaoRepository.save(terminal);
        jdbc.update("UPDATE PDV_VENDA_CABECALHO SET NFCE_SERIE = ?, NFCE_NUMERO = ?, NFCE_TIPO_EMISSAO = ?, NFCE_DATA_HORA_EMISSAO = ?, "
                + "STATUS_FISCAL = ?, CHAVE_ACESSO_FISCAL = NULL WHERE ID = ?", serie, proximo, tipoEmissao, dataHoraEmissao,
                StatusFiscal.PENDENTE, cupomId);
        return new NumeroNfce(serie, proximo);
    }

    public record NumeroNfce(int serie, long numero) {
    }
}
