package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import br.com.cactuserp.pdv.entity.PdvConfiguracaoBalanca;
import br.com.cactuserp.pdv.entity.Produto;
import br.com.cactuserp.pdv.entity.ProdutoUnidade;
import br.com.cactuserp.pdv.repository.PdvConfiguracaoBalancaRepository;
import br.com.cactuserp.pdv.repository.PdvConfiguracaoRepository;
import br.com.cactuserp.pdv.repository.ProdutoRepository;
import br.com.cactuserp.pdv.repository.ProdutoUnidadeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Cobre os dois métodos novos (2026-08-23, cenário "pesagem ao vivo" —
 * ver {@code ROADMAP.md} fase 7 / {@code CLAUDE.md}): {@link BalancaService#vendidoPorPeso}
 * (decide se um produto pode ser pesado) e {@link BalancaService#lerPesoAoVivo}
 * (delega pra {@link BalancaSerialService}, mockado aqui — a leitura serial
 * de verdade continua sem hardware pra validar).
 */
class BalancaServiceTest {

    private PdvConfiguracaoRepository configuracaoRepository;
    private PdvConfiguracaoBalancaRepository configuracaoBalancaRepository;
    private ProdutoUnidadeRepository produtoUnidadeRepository;
    private BalancaSerialService balancaSerialService;
    private BalancaService service;
    private PdvCaixa caixa;

    @BeforeEach
    void setUp() {
        configuracaoRepository = mock(PdvConfiguracaoRepository.class);
        configuracaoBalancaRepository = mock(PdvConfiguracaoBalancaRepository.class);
        ProdutoRepository produtoRepository = mock(ProdutoRepository.class);
        produtoUnidadeRepository = mock(ProdutoUnidadeRepository.class);
        balancaSerialService = mock(BalancaSerialService.class);

        service = new BalancaService(configuracaoRepository, configuracaoBalancaRepository,
                produtoRepository, produtoUnidadeRepository, new BalancaCodigoDecoder(), balancaSerialService);

        caixa = new PdvCaixa();
        caixa.setId(1L);
    }

    private Produto produtoComUnidade(Long idUnidade) {
        Produto produto = new Produto();
        produto.setId(10L);
        produto.setNome("Banana Prata");
        produto.setProdutoUnidade(idUnidade);
        return produto;
    }

    private ProdutoUnidade unidade(String sigla) {
        ProdutoUnidade unidade = new ProdutoUnidade();
        unidade.setSigla(sigla);
        return unidade;
    }

    // ====== vendidoPorPeso =================================================
    // Regressão (2026-08-23): vendidoPorPeso usava PODE_FRACIONAR, que
    // também é 'S' pra LT/ML (fracionável, mas não é unidade de MASSA) —
    // qualquer produto vendido por litro tentava ler a balança física e
    // caía no erro "balança não encontrada". Corrigido pra checar a sigla
    // (só KG/GR), ver ProdutoUnidade#ehUnidadeDePeso.

    @Test
    @DisplayName("vendidoPorPeso: false se produto é null")
    void vendidoPorPesoFalseSeProdutoNulo() {
        assertThat(service.vendidoPorPeso(null)).isFalse();
    }

    @Test
    @DisplayName("vendidoPorPeso: false se produto não tem unidade cadastrada")
    void vendidoPorPesoFalseSemUnidade() {
        assertThat(service.vendidoPorPeso(produtoComUnidade(null))).isFalse();
    }

    @Test
    @DisplayName("vendidoPorPeso: true pra unidade de massa (KG)")
    void vendidoPorPesoTrueParaKg() {
        when(produtoUnidadeRepository.findById(2L)).thenReturn(Optional.of(unidade("KG")));
        assertThat(service.vendidoPorPeso(produtoComUnidade(2L))).isTrue();
    }

    @Test
    @DisplayName("vendidoPorPeso: true pra unidade de massa (GR)")
    void vendidoPorPesoTrueParaGr() {
        when(produtoUnidadeRepository.findById(7L)).thenReturn(Optional.of(unidade("GR")));
        assertThat(service.vendidoPorPeso(produtoComUnidade(7L))).isTrue();
    }

    @Test
    @DisplayName("vendidoPorPeso: false pra LT — fracionável, mas não é unidade de massa (bug real corrigido)")
    void vendidoPorPesoFalseParaLitro() {
        when(produtoUnidadeRepository.findById(3L)).thenReturn(Optional.of(unidade("LT")));
        assertThat(service.vendidoPorPeso(produtoComUnidade(3L))).isFalse();
    }

    @Test
    @DisplayName("vendidoPorPeso: false pra ML — mesmo motivo do LT")
    void vendidoPorPesoFalseParaMl() {
        when(produtoUnidadeRepository.findById(8L)).thenReturn(Optional.of(unidade("ML")));
        assertThat(service.vendidoPorPeso(produtoComUnidade(8L))).isFalse();
    }

    @Test
    @DisplayName("vendidoPorPeso: false pra unidade não fracionável (ex: UN)")
    void vendidoPorPesoFalseParaUn() {
        when(produtoUnidadeRepository.findById(1L)).thenReturn(Optional.of(unidade("UN")));
        assertThat(service.vendidoPorPeso(produtoComUnidade(1L))).isFalse();
    }

    @Test
    @DisplayName("vendidoPorPeso: false se o ID da unidade não existe na tabela (FK órfã)")
    void vendidoPorPesoFalseSeUnidadeNaoEncontrada() {
        when(produtoUnidadeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThat(service.vendidoPorPeso(produtoComUnidade(99L))).isFalse();
    }

    // ====== lerPesoAoVivo ===================================================

    @Test
    @DisplayName("lerPesoAoVivo: vazio se caixa é null")
    void lerPesoAoVivoVazioSeCaixaNulo() {
        assertThat(service.lerPesoAoVivo(null)).isEmpty();
    }

    @Test
    @DisplayName("lerPesoAoVivo: vazio se caixa não tem PdvConfiguracao")
    void lerPesoAoVivoVazioSemConfiguracao() {
        when(configuracaoRepository.findFirstByCaixaId(1L)).thenReturn(Optional.empty());
        assertThat(service.lerPesoAoVivo(caixa)).isEmpty();
    }

    @Test
    @DisplayName("lerPesoAoVivo: vazio se não há balança serial configurada (porta em branco)")
    void lerPesoAoVivoVazioSemPortaConfigurada() {
        PdvConfiguracao config = new PdvConfiguracao();
        config.setId(5L);
        when(configuracaoRepository.findFirstByCaixaId(1L)).thenReturn(Optional.of(config));
        when(configuracaoBalancaRepository.findFirstByConfiguracaoId(5L)).thenReturn(Optional.empty());

        assertThat(service.lerPesoAoVivo(caixa)).isEmpty();
    }

    @Test
    @DisplayName("lerPesoAoVivo: delega pro BalancaSerialService quando há balança configurada")
    void lerPesoAoVivoDelegaParaLeituraSerial() {
        PdvConfiguracao config = new PdvConfiguracao();
        config.setId(5L);
        PdvConfiguracaoBalanca configBalanca = new PdvConfiguracaoBalanca();
        configBalanca.setPorta("COM3");

        when(configuracaoRepository.findFirstByCaixaId(1L)).thenReturn(Optional.of(config));
        when(configuracaoBalancaRepository.findFirstByConfiguracaoId(5L)).thenReturn(Optional.of(configBalanca));
        when(balancaSerialService.lerPesoAtual(configBalanca)).thenReturn(new BigDecimal("0.750"));

        assertThat(service.lerPesoAoVivo(caixa)).contains(new BigDecimal("0.750"));
    }
}
