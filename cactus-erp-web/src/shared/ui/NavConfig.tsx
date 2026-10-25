import type { ReactNode } from 'react'
import {
  AccountBookOutlined,
  BarChartOutlined,
  ContactsOutlined,
  DatabaseOutlined,
  DollarCircleOutlined,
  FileProtectOutlined,
  FileTextOutlined,
  SafetyOutlined,
  ShopOutlined,
  ShoppingCartOutlined,
  ShoppingOutlined,
  SwapOutlined,
  TeamOutlined,
  ToolOutlined,
} from '@ant-design/icons'

/**
 * Fonte única de verdade pra navegação dos 6 módulos de negócio — consumida
 * tanto pelo `AppSider` (filtra itens de menu por autoridade) quanto pelo
 * `AppRouter` (`RequireAuthority anyOf={entry.requiredAuthorities}`), pra nunca
 * ter duas listas de autoridade que podem divergir.
 *
 * IMPORTANTE — pendência registrada em CLAUDE.md: o mapeamento módulo→autoridade
 * abaixo é uma aproximação a partir dos exemplos de authority já vistos no
 * código (ClienteController, etc.), não uma conferência exaustiva contra o
 * catálogo real de `Funcionalidade` do seguranca-service. Reconciliar antes da
 * próxima entrega.
 */
/** Sub-item de menu dentro de um módulo (ex: Clientes/Fornecedores dentro de Cadastro Base) — vira submenu no `AppSider`, rota filha no `AppRouter`. */
export interface ModuleNavChildEntry {
  key: string
  label: string
  path: string
  requiredAuthorities: string[]
}

export interface ModuleNavEntry {
  key: string
  label: string
  path: string
  icon: ReactNode
  requiredAuthorities: string[]
  children?: ModuleNavChildEntry[]
}

export const MODULE_NAV: ModuleNavEntry[] = [
  {
    key: 'cadastro-base',
    label: 'Cadastro Base',
    path: '/cadastro-base',
    icon: <TeamOutlined />,
    requiredAuthorities: [
      'CADASTRO_CLIENTE_CONSULTAR',
      'CADASTRO_FORNECEDOR_CONSULTAR',
      'CADASTRO_TRANSPORTADORA_CONSULTAR',
      'CADASTRO_PRODUTO_CONSULTAR',
      'CADASTRO_EMPRESA_CONSULTAR',
      'CADASTRO_COLABORADOR_CONSULTAR',
      'CADASTRO_TRIBUTACAO_CONSULTAR',
    ],
    children: [
      {
        key: 'cadastro-base-clientes',
        label: 'Clientes',
        path: '/cadastro-base/clientes',
        requiredAuthorities: ['CADASTRO_CLIENTE_CONSULTAR'],
      },
      {
        key: 'cadastro-base-fornecedores',
        label: 'Fornecedores',
        path: '/cadastro-base/fornecedores',
        requiredAuthorities: ['CADASTRO_FORNECEDOR_CONSULTAR'],
      },
      {
        key: 'cadastro-base-transportadoras',
        label: 'Transportadoras',
        path: '/cadastro-base/transportadoras',
        requiredAuthorities: ['CADASTRO_TRANSPORTADORA_CONSULTAR'],
      },
      {
        key: 'cadastro-base-colaboradores',
        label: 'Colaboradores',
        path: '/cadastro-base/colaboradores',
        requiredAuthorities: ['CADASTRO_COLABORADOR_CONSULTAR'],
      },
      {
        key: 'cadastro-base-empresas',
        label: 'Empresas',
        path: '/cadastro-base/empresas',
        requiredAuthorities: ['CADASTRO_EMPRESA_CONSULTAR'],
      },
      {
        key: 'cadastro-base-produtos',
        label: 'Produtos',
        path: '/cadastro-base/produtos',
        requiredAuthorities: ['CADASTRO_PRODUTO_CONSULTAR'],
      },
      {
        key: 'cadastro-base-tabelas-preco',
        label: 'Tabelas de Preço',
        path: '/cadastro-base/tabelas-preco',
        requiredAuthorities: ['CADASTRO_PRODUTO_CONSULTAR'],
      },
      {
        key: 'cadastro-base-grupos-tributarios',
        label: 'Grupos Tributários',
        path: '/cadastro-base/grupos-tributarios',
        requiredAuthorities: ['CADASTRO_TRIBUTACAO_CONSULTAR'],
      },
      {
        key: 'cadastro-base-operacoes-fiscais',
        label: 'Operações Fiscais',
        path: '/cadastro-base/operacoes-fiscais',
        requiredAuthorities: ['CADASTRO_TRIBUTACAO_CONSULTAR'],
      },
      {
        key: 'cadastro-base-configuracoes-tributarias',
        label: 'Configurações Tributárias',
        path: '/cadastro-base/configuracoes-tributarias',
        requiredAuthorities: ['CADASTRO_TRIBUTACAO_CONSULTAR'],
      },
    ],
  },
  {
    key: 'estoque',
    label: 'Estoque',
    path: '/estoque',
    icon: <DatabaseOutlined />,
    requiredAuthorities: ['ESTOQUE_CONSULTAR', 'ESTOQUE_MOVIMENTAR', 'ESTOQUE_TRANSFERIR', 'ESTOQUE_INVENTARIO_GERENCIAR', 'ESTOQUE_GERENCIAR'],
    children: [
      {
        key: 'estoque-depositos',
        label: 'Depósitos',
        path: '/estoque/depositos',
        requiredAuthorities: ['ESTOQUE_CONSULTAR'],
      },
      {
        key: 'estoque-saldos',
        label: 'Saldos',
        path: '/estoque/saldos',
        requiredAuthorities: ['ESTOQUE_CONSULTAR'],
      },
      {
        key: 'estoque-movimentos',
        label: 'Movimentos',
        path: '/estoque/movimentos',
        requiredAuthorities: ['ESTOQUE_CONSULTAR'],
      },
      {
        key: 'estoque-transferencias',
        label: 'Transferências',
        path: '/estoque/transferencias',
        requiredAuthorities: ['ESTOQUE_CONSULTAR'],
      },
      {
        key: 'estoque-inventarios',
        label: 'Inventários',
        path: '/estoque/inventarios',
        requiredAuthorities: ['ESTOQUE_CONSULTAR'],
      },
      {
        key: 'estoque-lotes',
        label: 'Lotes',
        path: '/estoque/lotes',
        requiredAuthorities: ['ESTOQUE_CONSULTAR'],
      },
      {
        key: 'estoque-numeros-serie',
        label: 'Números de Série',
        path: '/estoque/numeros-serie',
        requiredAuthorities: ['ESTOQUE_CONSULTAR'],
      },
      {
        key: 'estoque-kits',
        label: 'Kits',
        path: '/estoque/kits',
        requiredAuthorities: ['ESTOQUE_CONSULTAR'],
      },
      {
        key: 'estoque-ficha-movimentacao',
        label: 'Ficha de Movimentação',
        path: '/estoque/ficha-movimentacao',
        requiredAuthorities: ['ESTOQUE_CONSULTAR'],
      },
      {
        key: 'estoque-curva-abc',
        label: 'Curva ABC',
        path: '/estoque/curva-abc',
        requiredAuthorities: ['ESTOQUE_CONSULTAR'],
      },
      {
        key: 'estoque-configuracao',
        label: 'Configuração',
        path: '/estoque/configuracao',
        requiredAuthorities: ['ESTOQUE_CONSULTAR'],
      },
    ],
  },
  {
    key: 'compras',
    label: 'Compras',
    path: '/compras',
    icon: <ShoppingOutlined />,
    requiredAuthorities: ['COMPRAS_CONSULTAR', 'COMPRAS_REQUISITAR', 'COMPRAS_COTAR', 'COMPRAS_APROVAR', 'COMPRAS_RECEBER', 'COMPRAS_GERENCIAR'],
    children: [
      {
        key: 'compras-requisicoes',
        label: 'Requisições',
        path: '/compras/requisicoes',
        requiredAuthorities: ['COMPRAS_CONSULTAR'],
      },
      {
        key: 'compras-pedidos',
        label: 'Pedidos',
        path: '/compras/pedidos',
        requiredAuthorities: ['COMPRAS_CONSULTAR'],
      },
      {
        key: 'compras-relatorios',
        label: 'Relatórios',
        path: '/compras/relatorios',
        requiredAuthorities: ['COMPRAS_CONSULTAR'],
      },
      {
        key: 'compras-configuracao',
        label: 'Configuração',
        path: '/compras/configuracao',
        requiredAuthorities: ['COMPRAS_CONSULTAR'],
      },
    ],
  },
  {
    key: 'vendas',
    label: 'Vendas',
    path: '/vendas',
    icon: <ShoppingCartOutlined />,
    requiredAuthorities: ['VENDAS_CONSULTAR', 'VENDAS_ORCAR', 'VENDAS_VENDER', 'VENDAS_CANCELAR', 'VENDAS_GERENCIAR'],
    children: [
      {
        key: 'vendas-orcamentos',
        label: 'Orçamentos',
        path: '/vendas/orcamentos',
        requiredAuthorities: ['VENDAS_CONSULTAR'],
      },
      {
        key: 'vendas-vendas',
        label: 'Vendas',
        path: '/vendas/vendas',
        requiredAuthorities: ['VENDAS_CONSULTAR'],
      },
      {
        key: 'vendas-pdv',
        label: 'Vendas do PDV',
        path: '/vendas/vendas-pdv',
        requiredAuthorities: ['VENDAS_CONSULTAR'],
      },
      {
        key: 'vendas-vendedores',
        label: 'Vendedores',
        path: '/vendas/vendedores',
        requiredAuthorities: ['VENDAS_CONSULTAR'],
      },
      {
        key: 'vendas-comissao-perfis',
        label: 'Perfis de Comissão',
        path: '/vendas/comissao-perfis',
        requiredAuthorities: ['VENDAS_CONSULTAR'],
      },
      {
        key: 'vendas-condicoes-pagamento',
        label: 'Condições de Pagamento',
        path: '/vendas/condicoes-pagamento',
        requiredAuthorities: ['VENDAS_CONSULTAR'],
      },
      {
        key: 'vendas-comissoes',
        label: 'Comissões',
        path: '/vendas/comissoes',
        requiredAuthorities: ['VENDAS_CONSULTAR'],
      },
      {
        key: 'vendas-notas-fiscais-tipos',
        label: 'Tipos de Nota Fiscal',
        path: '/vendas/notas-fiscais-tipos',
        requiredAuthorities: ['VENDAS_CONSULTAR'],
      },
      {
        key: 'vendas-relatorios',
        label: 'Relatórios',
        path: '/vendas/relatorios',
        requiredAuthorities: ['VENDAS_CONSULTAR'],
      },
    ],
  },
  {
    key: 'crm',
    label: 'CRM',
    path: '/crm',
    icon: <ContactsOutlined />,
    requiredAuthorities: ['CRM_CONSULTAR', 'CRM_PROSPECTAR', 'CRM_GERENCIAR'],
    children: [
      {
        key: 'crm-funil',
        label: 'Funil de Vendas',
        path: '/crm/funil',
        requiredAuthorities: ['CRM_CONSULTAR'],
      },
      {
        key: 'crm-oportunidades',
        label: 'Oportunidades',
        path: '/crm/oportunidades',
        requiredAuthorities: ['CRM_CONSULTAR'],
      },
      {
        key: 'crm-leads',
        label: 'Leads',
        path: '/crm/leads',
        requiredAuthorities: ['CRM_CONSULTAR'],
      },
      {
        key: 'crm-estagios-funil',
        label: 'Estágios do Funil',
        path: '/crm/estagios-funil',
        requiredAuthorities: ['CRM_CONSULTAR'],
      },
    ],
  },
  {
    key: 'os',
    label: 'Ordem de Serviço',
    path: '/os',
    icon: <ToolOutlined />,
    requiredAuthorities: ['OS_CONSULTAR', 'OS_ABRIR', 'OS_GERENCIAR'],
    children: [
      {
        key: 'os-aberturas',
        label: 'Ordens de Serviço',
        path: '/os/aberturas',
        requiredAuthorities: ['OS_CONSULTAR'],
      },
      {
        key: 'os-status',
        label: 'Status de OS',
        path: '/os/status',
        requiredAuthorities: ['OS_CONSULTAR'],
      },
      {
        key: 'os-relatorios',
        label: 'Relatórios',
        path: '/os/relatorios',
        requiredAuthorities: ['OS_CONSULTAR'],
      },
      {
        key: 'os-equipamentos',
        label: 'Tipos de Equipamento',
        path: '/os/equipamentos',
        requiredAuthorities: ['OS_CONSULTAR'],
      },
    ],
  },
  {
    key: 'fiscal',
    label: 'Fiscal',
    path: '/fiscal',
    icon: <FileProtectOutlined />,
    requiredAuthorities: ['FISCAL_CONSULTAR', 'FISCAL_EMITIR', 'FISCAL_GERENCIAR'],
    children: [
      {
        key: 'fiscal-notas-fiscais',
        label: 'Notas Fiscais',
        path: '/fiscal/notas-fiscais',
        requiredAuthorities: ['FISCAL_CONSULTAR'],
      },
      {
        key: 'fiscal-ctes',
        label: 'CT-e',
        path: '/fiscal/ctes',
        requiredAuthorities: ['FISCAL_CONSULTAR'],
      },
      {
        key: 'fiscal-mdfes',
        label: 'MDF-e',
        path: '/fiscal/mdfes',
        requiredAuthorities: ['FISCAL_CONSULTAR'],
      },
      {
        key: 'fiscal-notas-servico',
        label: 'Notas de Serviço (NFS-e)',
        path: '/fiscal/notas-servico',
        requiredAuthorities: ['FISCAL_CONSULTAR'],
      },
      {
        key: 'fiscal-notas-periodo',
        label: 'Notas por Período',
        path: '/fiscal/notas-periodo',
        requiredAuthorities: ['FISCAL_CONSULTAR'],
      },
      {
        key: 'fiscal-listas-servico',
        label: 'Lista de Serviço Municipal',
        path: '/fiscal/listas-servico',
        requiredAuthorities: ['FISCAL_CONSULTAR'],
      },
    ],
  },
  {
    key: 'ged',
    label: 'Gestão de Documentos',
    path: '/ged',
    icon: <FileTextOutlined />,
    requiredAuthorities: ['GED_CONSULTAR', 'GED_GERENCIAR'],
    children: [
      {
        key: 'ged-documentos',
        label: 'Documentos',
        path: '/ged/documentos',
        requiredAuthorities: ['GED_CONSULTAR'],
      },
      {
        key: 'ged-pastas',
        label: 'Pastas',
        path: '/ged/pastas',
        requiredAuthorities: ['GED_CONSULTAR'],
      },
      {
        key: 'ged-tipos-documento',
        label: 'Tipos de Documento',
        path: '/ged/tipos-documento',
        requiredAuthorities: ['GED_CONSULTAR'],
      },
    ],
  },
  {
    key: 'bi',
    label: 'Business Intelligence',
    path: '/bi',
    icon: <BarChartOutlined />,
    requiredAuthorities: ['BI_CONSULTAR', 'BI_GERENCIAR'],
    children: [
      {
        key: 'bi-faturamento',
        label: 'Faturamento',
        path: '/bi/faturamento',
        requiredAuthorities: ['BI_CONSULTAR'],
      },
      {
        key: 'bi-estoque-valor',
        label: 'Valor de Estoque',
        path: '/bi/estoque-valor',
        requiredAuthorities: ['BI_CONSULTAR'],
      },
      {
        key: 'bi-curva-abc',
        label: 'Curva ABC',
        path: '/bi/curva-abc',
        requiredAuthorities: ['BI_CONSULTAR'],
      },
      {
        key: 'bi-compras-fornecedor',
        label: 'Compras por Fornecedor',
        path: '/bi/compras-fornecedor',
        requiredAuthorities: ['BI_CONSULTAR'],
      },
      {
        key: 'bi-etl',
        label: 'Execução do ETL',
        path: '/bi/etl',
        requiredAuthorities: ['BI_GERENCIAR'],
      },
    ],
  },
  {
    key: 'financeiro',
    label: 'Financeiro',
    path: '/financeiro',
    icon: <DollarCircleOutlined />,
    requiredAuthorities: ['FINANCEIRO_CONSULTAR', 'FINANCEIRO_LANCAR', 'FINANCEIRO_GERENCIAR', 'CAIXA_CONSULTAR', 'CAIXA_MOVIMENTAR', 'CAIXA_GERENCIAR'],
    children: [
      // Fase A (2026-09-07): Contas a Receber/Pagar + catálogos (Natureza/Tipo de Documento) +
      // Configuração Financeira (authority FINANCEIRO_*). Fase B (2026-09-07): Caixa e
      // Bancos/Cheques/Tesouraria/Fluxo de Caixa/Conciliação Bancária/EDI Bancário — todos sob
      // CAIXA_* (mesmo módulo RBAC `FINANCEIRO` do backend, authority reaproveitada).
      {
        key: 'financeiro-contas-receber',
        label: 'Contas a Receber',
        path: '/financeiro/contas-receber',
        requiredAuthorities: ['FINANCEIRO_CONSULTAR'],
      },
      {
        key: 'financeiro-contas-pagar',
        label: 'Contas a Pagar',
        path: '/financeiro/contas-pagar',
        requiredAuthorities: ['FINANCEIRO_CONSULTAR'],
      },
      {
        key: 'financeiro-naturezas',
        label: 'Naturezas Financeiras',
        path: '/financeiro/naturezas-financeiras',
        requiredAuthorities: ['FINANCEIRO_CONSULTAR'],
      },
      {
        key: 'financeiro-tipos-documento',
        label: 'Tipos de Documento',
        path: '/financeiro/tipos-documento',
        requiredAuthorities: ['FINANCEIRO_CONSULTAR'],
      },
      {
        key: 'financeiro-configuracao',
        label: 'Configuração',
        path: '/financeiro/configuracao',
        requiredAuthorities: ['FINANCEIRO_CONSULTAR'],
      },
      {
        key: 'financeiro-contas-bancarias',
        label: 'Contas Bancárias',
        path: '/financeiro/contas-bancarias',
        requiredAuthorities: ['CAIXA_CONSULTAR'],
      },
      {
        key: 'financeiro-movimentos',
        label: 'Movimentos',
        path: '/financeiro/movimentos',
        requiredAuthorities: ['CAIXA_CONSULTAR'],
      },
      {
        key: 'financeiro-cheques-emitidos',
        label: 'Cheques Emitidos',
        path: '/financeiro/cheques-emitidos',
        requiredAuthorities: ['CAIXA_CONSULTAR'],
      },
      {
        key: 'financeiro-cheques-recebidos',
        label: 'Cheques Recebidos',
        path: '/financeiro/cheques-recebidos',
        requiredAuthorities: ['CAIXA_CONSULTAR'],
      },
      {
        key: 'financeiro-transferencias',
        label: 'Transferências',
        path: '/financeiro/transferencias',
        requiredAuthorities: ['CAIXA_CONSULTAR'],
      },
      {
        key: 'financeiro-aplicacoes',
        label: 'Aplicações Financeiras',
        path: '/financeiro/aplicacoes',
        requiredAuthorities: ['CAIXA_CONSULTAR'],
      },
      {
        key: 'financeiro-posicao-caixa',
        label: 'Posição de Caixa',
        path: '/financeiro/posicao-caixa',
        requiredAuthorities: ['CAIXA_CONSULTAR'],
      },
      {
        key: 'financeiro-fluxo-caixa',
        label: 'Fluxo de Caixa',
        path: '/financeiro/fluxo-caixa',
        requiredAuthorities: ['FINANCEIRO_CONSULTAR'],
      },
      {
        key: 'financeiro-titulos-em-aberto',
        label: 'Títulos em Aberto',
        path: '/financeiro/titulos-em-aberto',
        requiredAuthorities: ['FINANCEIRO_CONSULTAR'],
      },
      {
        key: 'financeiro-conciliacao',
        label: 'Conciliação Bancária',
        path: '/financeiro/conciliacao-bancaria',
        requiredAuthorities: ['CAIXA_CONSULTAR'],
      },
      {
        key: 'financeiro-edi',
        label: 'EDI Bancário',
        path: '/financeiro/edi-bancario',
        requiredAuthorities: ['CAIXA_CONSULTAR'],
      },
    ],
  },
  {
    key: 'contabilidade',
    label: 'Contabilidade',
    path: '/contabilidade',
    icon: <AccountBookOutlined />,
    requiredAuthorities: ['CONTABIL_CONSULTAR', 'CONTABIL_LANCAR', 'CONTABIL_GERENCIAR'],
    children: [
      {
        key: 'contabilidade-plano-contas',
        label: 'Plano de Contas',
        path: '/contabilidade/plano-contas',
        requiredAuthorities: ['CONTABIL_CONSULTAR'],
      },
      {
        key: 'contabilidade-centros-custo',
        label: 'Centros de Custo',
        path: '/contabilidade/centros-custo',
        requiredAuthorities: ['CONTABIL_CONSULTAR'],
      },
      {
        key: 'contabilidade-lancamentos',
        label: 'Lançamentos Contábeis',
        path: '/contabilidade/lancamentos',
        requiredAuthorities: ['CONTABIL_CONSULTAR'],
      },
      {
        key: 'contabilidade-balancete',
        label: 'Balancete',
        path: '/contabilidade/balancete',
        requiredAuthorities: ['CONTABIL_CONSULTAR'],
      },
      {
        key: 'contabilidade-razao',
        label: 'Razão',
        path: '/contabilidade/razao',
        requiredAuthorities: ['CONTABIL_CONSULTAR'],
      },
    ],
  },
  {
    key: 'loja-virtual',
    label: 'Loja Virtual',
    path: '/loja-virtual',
    icon: <ShopOutlined />,
    requiredAuthorities: ['LOJA_CONSULTAR', 'LOJA_GERENCIAR'],
    children: [
      {
        key: 'loja-virtual-lojas',
        label: 'Lojas',
        path: '/loja-virtual/lojas',
        requiredAuthorities: ['LOJA_CONSULTAR'],
      },
      {
        key: 'loja-virtual-categorias',
        label: 'Categorias',
        path: '/loja-virtual/categorias',
        requiredAuthorities: ['LOJA_CONSULTAR'],
      },
      {
        key: 'loja-virtual-atributos',
        label: 'Atributos de Categoria',
        path: '/loja-virtual/atributos',
        requiredAuthorities: ['LOJA_CONSULTAR'],
      },
      {
        key: 'loja-virtual-produtos',
        label: 'Produtos',
        path: '/loja-virtual/produtos',
        requiredAuthorities: ['LOJA_CONSULTAR'],
      },
      {
        key: 'loja-virtual-pedidos',
        label: 'Pedidos',
        path: '/loja-virtual/pedidos',
        requiredAuthorities: ['LOJA_CONSULTAR'],
      },
    ],
  },
  {
    key: 'sincronizacao',
    label: 'Sincronização PDV',
    path: '/sincronizacao',
    icon: <SwapOutlined />,
    requiredAuthorities: ['SINCRONIZACAO_CONSULTAR', 'SINCRONIZACAO_GERENCIAR', 'SEGURANCA_GERENCIAR'],
    children: [
      {
        key: 'sincronizacao-registros',
        label: 'Registros',
        path: '/sincronizacao/registros',
        requiredAuthorities: ['SINCRONIZACAO_CONSULTAR'],
      },
      {
        key: 'sincronizacao-lotes',
        label: 'Lotes',
        path: '/sincronizacao/lotes',
        requiredAuthorities: ['SINCRONIZACAO_CONSULTAR'],
      },
      {
        key: 'sincronizacao-clientes-loja',
        label: 'Clientes de Loja (OAuth2)',
        path: '/sincronizacao/clientes-loja',
        requiredAuthorities: ['SEGURANCA_GERENCIAR'],
      },
    ],
  },
  {
    key: 'seguranca',
    label: 'Segurança',
    path: '/seguranca',
    icon: <SafetyOutlined />,
    requiredAuthorities: ['SEGURANCA_CONSULTAR', 'SEGURANCA_GERENCIAR'],
    children: [
      {
        key: 'seguranca-usuarios',
        label: 'Usuários',
        path: '/seguranca/usuarios',
        requiredAuthorities: ['SEGURANCA_CONSULTAR'],
      },
      {
        key: 'seguranca-papeis',
        label: 'Papéis',
        path: '/seguranca/papeis',
        requiredAuthorities: ['SEGURANCA_CONSULTAR'],
      },
      {
        key: 'seguranca-funcionalidades',
        label: 'Funcionalidades',
        path: '/seguranca/funcionalidades',
        requiredAuthorities: ['SEGURANCA_CONSULTAR'],
      },
      {
        key: 'seguranca-modulos',
        label: 'Módulos',
        path: '/seguranca/modulos',
        requiredAuthorities: ['SEGURANCA_CONSULTAR'],
      },
    ],
  },
]
