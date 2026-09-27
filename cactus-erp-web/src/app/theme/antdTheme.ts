import type { ThemeConfig } from 'antd'

/**
 * Paleta derivada da marca Cactus Digital (verde-limão do ícone + carvão escuro
 * do fundo do logo/mockup original). Modo claro apenas nesta entrega — ver
 * CLAUDE.md, "Decisões técnicas travadas", para o porquê do dark mode completo
 * ficar adiado. O Sider escuro é a única superfície "dark" deliberada, e já
 * conversa com a identidade visual da marca.
 */
export const CACTUS_GREEN = '#8cc63f'
export const CACTUS_DARK = '#262825'

export const cactusTheme: ThemeConfig = {
  token: {
    colorPrimary: CACTUS_GREEN,
    colorLink: CACTUS_GREEN,
    borderRadius: 6,
    fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
  },
  components: {
    Layout: {
      siderBg: CACTUS_DARK,
      headerBg: '#ffffff',
      bodyBg: '#f5f6f5',
      // Sem isso o Ant Design usa o default triggerBg='#002140' (azul-marinho) pro
      // botão de recolher/expandir o menu — destoava completamente do tema
      // escuro/verde (achado real, reportado pelo usuário: "esse botão azul").
      // Flush com o siderBg (sem bloco visível, só o ícone "<" em verde) fica mais
      // discreto/premium do que um botão de cor sólida diferente.
      triggerBg: CACTUS_DARK,
      triggerColor: CACTUS_GREEN,
    },
    Menu: {
      darkItemBg: CACTUS_DARK,
      darkItemSelectedBg: CACTUS_GREEN,
      darkItemSelectedColor: '#1a1a1a',
      darkItemColor: 'rgba(255,255,255,0.75)',
      darkItemHoverColor: '#ffffff',
      // NOTA: `subMenuItemSelectedColor` (o token que "deveria" controlar a
      // cor do texto do item PAI de um submenu aberto contendo a rota ativa)
      // não tem efeito aqui — o Ant Design remapeia esse token internamente
      // pra `darkItemSelectedColor` dentro do próprio gerador de estilo do
      // Menu (`menu/style/index.js`, função de derivação do tema dark),
      // sobrescrevendo qualquer valor configurado via ThemeConfig. Como
      // `darkItemSelectedColor` (#1a1a1a) foi escolhido pro texto do item
      // FOLHA (que tem fundo verde por trás — `darkItemSelectedBg`), o mesmo
      // valor aplicado ao item PAI (sem fundo verde) fica ilegível sobre o
      // fundo escuro do Sider — achado real, reportado pelo usuário
      // ("Business Intelligence" ilegível com o submenu aberto). Sem um
      // token próprio configurável pra esse caso, a correção é via CSS
      // direto — ver `src/index.css`, regra `.ant-menu-submenu-selected`.
    },
    Table: {
      cellPaddingBlock: 8,
    },
  },
}
