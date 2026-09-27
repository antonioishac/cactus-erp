package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import br.com.cactuserp.pdv.repository.PdvConfiguracaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Marketing na tela (fase 8) — slideshow de imagens promocionais exibido
 * na {@code TelaBloqueio} (idle da loja) quando configurado.
 *
 * <p>Usa as colunas {@code PDV_CONFIGURACAO.CAMINHO_IMAGENS_MARKETING}/
 * {@code MARKETING_ATIVO}, que já existiam no schema (ver {@code PDV.md})
 * desde antes da fase 8, mas não tinham nenhum código usando-as. Escopo
 * deliberadamente simples: lê arquivos de imagem de uma pasta local — sem
 * agendamento por dia/hora, sem métricas de exibição, sem upload pela UI
 * (a pasta é preenchida manualmente pelo lojista/suporte). Ver
 * {@code ROADMAP.md} fase 8.
 */
@Service
@Transactional(readOnly = true)
public class MarketingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketingService.class);

    private static final List<String> EXTENSOES_SUPORTADAS =
            List.of(".png", ".jpg", ".jpeg", ".gif", ".bmp");

    private static final FilenameFilter FILTRO_IMAGENS = (dir, nome) -> {
        String lower = nome.toLowerCase(Locale.ROOT);
        return EXTENSOES_SUPORTADAS.stream().anyMatch(lower::endsWith);
    };

    private final PdvConfiguracaoRepository configuracaoRepository;

    public MarketingService(PdvConfiguracaoRepository configuracaoRepository) {
        this.configuracaoRepository = configuracaoRepository;
    }

    /**
     * Lista as imagens de marketing pra exibir no slideshow, já como URI
     * {@code file:...} pronta pro construtor de {@code javafx.scene.image.Image}
     * (não usar {@link File#getAbsolutePath()} direto — no Windows gera
     * caminho com barra invertida, que não é uma URL válida), em ordem
     * alfabética (ordem previsível pro lojista, só renomeando os arquivos
     * "01-....png", "02-....png" etc).
     *
     * @return lista vazia (nunca {@code null}) se marketing não estiver
     *         ativo, não configurado, pasta inexistente ou sem imagens —
     *         nunca lança exceção, chamado a partir da tela de bloqueio,
     *         que não pode travar por causa disso.
     */
    public List<String> listarImagensAtivas(PdvCaixa caixa) {
        if (caixa == null || caixa.getId() == null) return List.of();

        PdvConfiguracao config = configuracaoRepository.findFirstByCaixaId(caixa.getId()).orElse(null);
        if (config == null || !config.marketingEstaAtivo()) return List.of();

        String caminho = config.getCaminhoImagensMarketing();
        if (caminho == null || caminho.isBlank()) {
            LOGGER.debug("Marketing ativo mas CAMINHO_IMAGENS_MARKETING não configurado para o caixa {}.",
                    caixa.getNome());
            return List.of();
        }

        File pasta = new File(caminho);
        if (!pasta.isDirectory()) {
            LOGGER.warn("Pasta de imagens de marketing não encontrada: '{}'", caminho);
            return List.of();
        }

        File[] arquivos = pasta.listFiles(FILTRO_IMAGENS);
        if (arquivos == null || arquivos.length == 0) {
            LOGGER.debug("Pasta de imagens de marketing '{}' sem imagens suportadas.", caminho);
            return List.of();
        }

        return Arrays.stream(arquivos)
                .sorted(Comparator.comparing(File::getName))
                .map(f -> f.toURI().toString())
                .toList();
    }
}
