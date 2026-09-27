package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import br.com.cactuserp.pdv.repository.PdvConfiguracaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link MarketingService} lê arquivo/pasta de verdade (não é lógica pura
 * como {@code EmissaoFiscalServiceTest}), então usa {@code @TempDir} em vez
 * de mocks de filesystem — cobre as regras de negócio (ativo/inativo,
 * pasta ausente, filtro de extensão, ordenação) sem precisar de contexto
 * Spring nem de um caixa/config reais no banco.
 */
class MarketingServiceTest {

    @TempDir
    Path pastaTemp;

    private PdvConfiguracaoRepository configuracaoRepository;
    private MarketingService service;
    private PdvCaixa caixa;

    @BeforeEach
    void setUp() {
        configuracaoRepository = mock(PdvConfiguracaoRepository.class);
        service = new MarketingService(configuracaoRepository);
        caixa = new PdvCaixa();
        caixa.setId(1L);
    }

    private PdvConfiguracao configAtiva(String caminho) {
        PdvConfiguracao config = new PdvConfiguracao();
        config.setMarketingAtivo("S");
        config.setCaminhoImagensMarketing(caminho);
        return config;
    }

    @Test
    @DisplayName("retorna vazio se caixa é null")
    void retornaVazioSeCaixaNulo() {
        assertThat(service.listarImagensAtivas(null)).isEmpty();
    }

    @Test
    @DisplayName("retorna vazio se não há PdvConfiguracao pro caixa")
    void retornaVazioSemConfiguracao() {
        when(configuracaoRepository.findFirstByCaixaId(1L)).thenReturn(Optional.empty());
        assertThat(service.listarImagensAtivas(caixa)).isEmpty();
    }

    @Test
    @DisplayName("retorna vazio se MARKETING_ATIVO não é 'S'")
    void retornaVazioSeMarketingInativo() throws IOException {
        Files.writeString(pastaTemp.resolve("banner.png"), "x");
        PdvConfiguracao config = new PdvConfiguracao();
        config.setMarketingAtivo("N");
        config.setCaminhoImagensMarketing(pastaTemp.toString());
        when(configuracaoRepository.findFirstByCaixaId(1L)).thenReturn(Optional.of(config));

        assertThat(service.listarImagensAtivas(caixa)).isEmpty();
    }

    @Test
    @DisplayName("retorna vazio se a pasta configurada não existe")
    void retornaVazioSePastaNaoExiste() {
        when(configuracaoRepository.findFirstByCaixaId(1L))
                .thenReturn(Optional.of(configAtiva(pastaTemp.resolve("nao-existe").toString())));

        assertThat(service.listarImagensAtivas(caixa)).isEmpty();
    }

    @Test
    @DisplayName("ignora arquivos que não são imagem e ordena por nome")
    void filtraEOrdenaImagens() throws IOException {
        Files.writeString(pastaTemp.resolve("02-promo.jpg"), "x");
        Files.writeString(pastaTemp.resolve("01-banner.png"), "x");
        Files.writeString(pastaTemp.resolve("leiame.txt"), "não é imagem");
        when(configuracaoRepository.findFirstByCaixaId(1L))
                .thenReturn(Optional.of(configAtiva(pastaTemp.toString())));

        List<String> imagens = service.listarImagensAtivas(caixa);

        assertThat(imagens).hasSize(2);
        assertThat(imagens.get(0)).endsWith("01-banner.png");
        assertThat(imagens.get(1)).endsWith("02-promo.jpg");
    }

    @Test
    @DisplayName("cada imagem vem como URI file: válida (não caminho cru do SO)")
    void retornaUriFileValida() throws IOException {
        Files.writeString(pastaTemp.resolve("banner.png"), "x");
        when(configuracaoRepository.findFirstByCaixaId(1L))
                .thenReturn(Optional.of(configAtiva(pastaTemp.toString())));

        String uri = service.listarImagensAtivas(caixa).get(0);

        assertThat(uri).startsWith("file:");
        assertThat(new File(java.net.URI.create(uri))).exists();
    }
}
