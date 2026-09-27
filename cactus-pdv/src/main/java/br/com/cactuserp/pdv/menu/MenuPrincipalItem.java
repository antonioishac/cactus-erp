package br.com.cactuserp.pdv.menu;

import br.com.cactuserp.pdv.entity.NivelAutorizacao;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Catálogo de todos os itens disponíveis no Menu Principal (F12 do PDV).
 *
 * <p>Cada item carrega:
 * <ul>
 *   <li><b>secao</b>: agrupamento visual (CAIXA, CUPOM, SISTEMA)</li>
 *   <li><b>rotulo</b>: texto exibido no botão</li>
 *   <li><b>iconLiteral</b>: nome do ícone Ikonli (Material Design 2)</li>
 *   <li><b>corIcone</b>: cor do ícone em hexadecimal</li>
 *   <li><b>atalho</b>: tecla numérica de atalho ('1' a '9' ou '0')</li>
 *   <li><b>niveisPermitidos</b>: níveis que podem CLICAR no botão.</li>
 *   <li><b>exigeAutorizacaoGerencial</b>: pede senha mesmo se clicável.</li>
 * </ul>
 */
public enum MenuPrincipalItem {

    // === CAIXA ============================================================
    FECHAR_MOVIMENTO(
            Secao.CAIXA, "Fechar Movimento", "mdi2l-lock", "#f0997b", '1',
            EnumSet.of(NivelAutorizacao.G, NivelAutorizacao.S, NivelAutorizacao.O),
            true
    ),
    FECHAMENTO_TEMPORARIO(
            Secao.CAIXA, "Fech. Temporário", "mdi2p-pause-circle", "#fac775", '2',
            EnumSet.of(NivelAutorizacao.G, NivelAutorizacao.S, NivelAutorizacao.O),
            false
    ),
    TROCAR_OPERADOR(
            Secao.CAIXA, "Trocar Operador", "mdi2a-account-multiple", "#b5d4f4", '3',
            EnumSet.of(NivelAutorizacao.G, NivelAutorizacao.S, NivelAutorizacao.O),
            true
    ),
    CONFERENCIA_CAIXA(
            Secao.CAIXA, "Conferência", "mdi2c-calculator", "#9fe1cb", '4',
            EnumSet.of(NivelAutorizacao.G, NivelAutorizacao.S, NivelAutorizacao.O),
            false
    ),
    ABRIR_MOVIMENTO(
            Secao.CAIXA, "Abrir Movimento", "mdi2l-lock-open", "#a8e6a3", '5',
            EnumSet.of(NivelAutorizacao.G, NivelAutorizacao.S, NivelAutorizacao.O),
            false
    ),

    // === CUPOM ============================================================
    REIMPRIMIR_CUPOM(
            Secao.CUPOM, "Reimprimir", "mdi2p-printer", "#cecbf6", '6',
            EnumSet.of(NivelAutorizacao.G, NivelAutorizacao.S, NivelAutorizacao.O),
            false
    ),
    CANCELAR_CUPOM_ANTERIOR(
            Secao.CUPOM, "Canc. Anterior", "mdi2r-receipt-text-remove", "#f7c1c1", '7',
            EnumSet.of(NivelAutorizacao.G, NivelAutorizacao.S, NivelAutorizacao.O),
            true
    ),
    CONSULTAR_CUPOM(
            Secao.CUPOM, "Consultar Cupom", "mdi2m-magnify", "#b5d4f4", '8',
            EnumSet.of(NivelAutorizacao.G, NivelAutorizacao.S, NivelAutorizacao.O),
            false
    ),
    CONSULTA_PRODUTO(
            Secao.CUPOM, "Consulta Produto", "mdi2p-package-variant", "#9fe1cb", '9',
            EnumSet.of(NivelAutorizacao.G, NivelAutorizacao.S, NivelAutorizacao.O),
            false
    ),

    // === SISTEMA ==========================================================
    BLOQUEAR(
            Secao.SISTEMA, "Bloquear", "mdi2l-lock-outline", "#fac775", 'B',
            EnumSet.of(NivelAutorizacao.G, NivelAutorizacao.S, NivelAutorizacao.O),
            false
    ),
    CONFIGURACOES(
            Secao.SISTEMA, "Configurações", "mdi2c-cog", "#b5d4f4", 'C',
            EnumSet.of(NivelAutorizacao.G),
            false
    ),
    SAIR(
            Secao.SISTEMA, "Sair / Logoff", "mdi2l-logout", "#f0997b", '0',
            EnumSet.of(NivelAutorizacao.G, NivelAutorizacao.S, NivelAutorizacao.O),
            false
    );

    public enum Secao {
        CAIXA("Caixa"),
        CUPOM("Cupom"),
        SISTEMA("Sistema");

        private final String rotulo;
        Secao(String rotulo) { this.rotulo = rotulo; }
        public String getRotulo() { return rotulo; }
    }

    private final Secao secao;
    private final String rotulo;
    private final String iconLiteral;
    private final String corIcone;
    private final char atalho;
    private final Set<NivelAutorizacao> niveisPermitidos;
    private final boolean exigeAutorizacaoGerencial;

    MenuPrincipalItem(Secao secao, String rotulo, String iconLiteral,
                      String corIcone, char atalho,
                      Set<NivelAutorizacao> niveisPermitidos,
                      boolean exigeAutorizacaoGerencial) {
        this.secao = secao;
        this.rotulo = rotulo;
        this.iconLiteral = iconLiteral;
        this.corIcone = corIcone;
        this.atalho = atalho;
        this.niveisPermitidos = niveisPermitidos;
        this.exigeAutorizacaoGerencial = exigeAutorizacaoGerencial;
    }

    public Secao getSecao() { return secao; }
    public String getRotulo() { return rotulo; }
    public String getIconLiteral() { return iconLiteral; }
    public String getCorIcone() { return corIcone; }
    public char getAtalho() { return atalho; }
    public Set<NivelAutorizacao> getNiveisPermitidos() { return niveisPermitidos; }
    public boolean exigeAutorizacaoGerencial() { return exigeAutorizacaoGerencial; }

    public boolean exibeSeloGerencial() {
        return !niveisPermitidos.contains(NivelAutorizacao.O)
                || exigeAutorizacaoGerencial;
    }

    public boolean permiteNivel(NivelAutorizacao nivel) {
        if (nivel == null) return false;
        return niveisPermitidos.contains(nivel);
    }

    public static MenuPrincipalItem porAtalho(char tecla) {
        for (MenuPrincipalItem item : values()) {
            if (item.atalho == tecla) return item;
        }
        return null;
    }

    public static List<MenuPrincipalItem> daSecao(Secao secao) {
        return Arrays.stream(values())
                .filter(item -> item.secao == secao)
                .collect(Collectors.toList());
    }
}