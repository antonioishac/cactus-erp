import { createBrowserRouter, Navigate, Outlet, RouterProvider } from 'react-router-dom'
import LoginPage from '@features/auth/LoginPage'
import CallbackPage from '@features/auth/CallbackPage'
import LogoutPage from '@features/auth/LogoutPage'
import SilentRenewPage from '@features/auth/SilentRenewPage'
import DashboardPage from '@features/dashboard/DashboardPage'
import ClientesListPage from '@features/clientes/ClientesListPage'
import ClienteFormPage from '@features/clientes/ClienteFormPage'
import FornecedoresListPage from '@features/fornecedores/FornecedoresListPage'
import FornecedorFormPage from '@features/fornecedores/FornecedorFormPage'
import TransportadorasListPage from '@features/transportadoras/TransportadorasListPage'
import TransportadoraFormPage from '@features/transportadoras/TransportadoraFormPage'
import ColaboradoresListPage from '@features/colaboradores/ColaboradoresListPage'
import ColaboradorFormPage from '@features/colaboradores/ColaboradorFormPage'
import EmpresasListPage from '@features/empresas/EmpresasListPage'
import EmpresaFormPage from '@features/empresas/EmpresaFormPage'
import ProdutosListPage from '@features/produtos/ProdutosListPage'
import ProdutoFormPage from '@features/produtos/ProdutoFormPage'
import TabelasPrecoListPage from '@features/tabelas-preco/TabelasPrecoListPage'
import TabelaPrecoFormPage from '@features/tabelas-preco/TabelaPrecoFormPage'
import GruposTributariosListPage from '@features/tributacao/GruposTributariosListPage'
import OperacoesFiscaisListPage from '@features/tributacao/OperacoesFiscaisListPage'
import ConfiguracoesTributariasListPage from '@features/tributacao/ConfiguracoesTributariasListPage'
import ConfiguracaoTributariaFormPage from '@features/tributacao/ConfiguracaoTributariaFormPage'
import DepositosListPage from '@features/estoque/DepositosListPage'
import DepositoFormPage from '@features/estoque/DepositoFormPage'
import SaldosListPage from '@features/estoque/SaldosListPage'
import MovimentosListPage from '@features/estoque/MovimentosListPage'
import TransferenciasListPage from '@features/estoque/TransferenciasListPage'
import ConfiguracaoEstoquePage from '@features/estoque/ConfiguracaoEstoquePage'
import InventariosListPage from '@features/estoque/InventariosListPage'
import InventarioDetailPage from '@features/estoque/InventarioDetailPage'
import LotesListPage from '@features/estoque/LotesListPage'
import NumerosSerieListPage from '@features/estoque/NumerosSerieListPage'
import KitsPage from '@features/estoque/KitsPage'
import CurvaAbcPage from '@features/estoque/CurvaAbcPage'
import UsuariosListPage from '@features/seguranca/UsuariosListPage'
import UsuarioFormPage from '@features/seguranca/UsuarioFormPage'
import PapeisListPage from '@features/seguranca/PapeisListPage'
import PapelFormPage from '@features/seguranca/PapelFormPage'
import FuncionalidadesListPage from '@features/seguranca/FuncionalidadesListPage'
import ModulosListPage from '@features/seguranca/ModulosListPage'
import DocumentosListPage from '@features/ged/DocumentosListPage'
import DocumentoUploadPage from '@features/ged/DocumentoUploadPage'
import DocumentoDetailPage from '@features/ged/DocumentoDetailPage'
import PastasListPage from '@features/ged/PastasListPage'
import TiposDocumentoListPage from '@features/ged/TiposDocumentoListPage'
import RequisicoesListPage from '@features/compras/RequisicoesListPage'
import RequisicaoFormPage from '@features/compras/RequisicaoFormPage'
import RequisicaoDetailPage from '@features/compras/RequisicaoDetailPage'
import CotacaoDetailPage from '@features/compras/CotacaoDetailPage'
import PedidosListPage from '@features/compras/PedidosListPage'
import PedidoFormPage from '@features/compras/PedidoFormPage'
import PedidoDetailPage from '@features/compras/PedidoDetailPage'
import ConfiguracaoComprasPage from '@features/compras/ConfiguracaoComprasPage'
import EstoqueValorPage from '@features/bi/EstoqueValorPage'
import CurvaAbcIndicadorPage from '@features/bi/CurvaAbcIndicadorPage'
import ComprasFornecedorPage from '@features/bi/ComprasFornecedorPage'
import EtlExecucaoPage from '@features/bi/EtlExecucaoPage'
import VendedoresListPage from '@features/vendas/VendedoresListPage'
import VendedorFormPage from '@features/vendas/VendedorFormPage'
import ComissaoPerfisListPage from '@features/vendas/ComissaoPerfisListPage'
import ComissaoPerfilFormPage from '@features/vendas/ComissaoPerfilFormPage'
import CondicoesPagamentoListPage from '@features/vendas/CondicoesPagamentoListPage'
import CondicaoPagamentoFormPage from '@features/vendas/CondicaoPagamentoFormPage'
import NotasFiscaisTiposPage from '@features/vendas/NotasFiscaisTiposPage'
import OrcamentosListPage from '@features/vendas/OrcamentosListPage'
import OrcamentoFormPage from '@features/vendas/OrcamentoFormPage'
import OrcamentoDetailPage from '@features/vendas/OrcamentoDetailPage'
import VendasListPage from '@features/vendas/VendasListPage'
import RelatoriosVendasPage from '@features/vendas/RelatoriosVendasPage'
import VendaFormPage from '@features/vendas/VendaFormPage'
import VendaDetailPage from '@features/vendas/VendaDetailPage'
import ComissoesListPage from '@features/vendas/ComissoesListPage'
import NotasFiscaisListPage from '@features/fiscal/NotasFiscaisListPage'
import NotaFiscalDetailPage from '@features/fiscal/NotaFiscalDetailPage'
import CtesListPage from '@features/fiscal/CtesListPage'
import CteDetailPage from '@features/fiscal/CteDetailPage'
import NotasServicoListPage from '@features/fiscal/NotasServicoListPage'
import NotaServicoDetailPage from '@features/fiscal/NotaServicoDetailPage'
import ListasServicoMunicipalListPage from '@features/fiscal/ListasServicoMunicipalListPage'
import OsAberturasListPage from '@features/os/OsAberturasListPage'
import OsAberturaFormPage from '@features/os/OsAberturaFormPage'
import OsAberturaDetailPage from '@features/os/OsAberturaDetailPage'
import OsStatusListPage from '@features/os/OsStatusListPage'
import OsEquipamentosListPage from '@features/os/OsEquipamentosListPage'
import OportunidadesKanbanPage from '@features/crm/OportunidadesKanbanPage'
import OportunidadesListPage from '@features/crm/OportunidadesListPage'
import OportunidadeFormPage from '@features/crm/OportunidadeFormPage'
import LeadsListPage from '@features/crm/LeadsListPage'
import LeadFormPage from '@features/crm/LeadFormPage'
import EstagiosFunilListPage from '@features/crm/EstagiosFunilListPage'
import ContasReceberListPage from '@features/financeiro/ContasReceberListPage'
import ContaReceberFormPage from '@features/financeiro/ContaReceberFormPage'
import ContaReceberDetailPage from '@features/financeiro/ContaReceberDetailPage'
import ContasPagarListPage from '@features/financeiro/ContasPagarListPage'
import ContaPagarFormPage from '@features/financeiro/ContaPagarFormPage'
import ContaPagarDetailPage from '@features/financeiro/ContaPagarDetailPage'
import NaturezasFinanceirasListPage from '@features/financeiro/NaturezasFinanceirasListPage'
import TiposDocumentoFinanceiroListPage from '@features/financeiro/TiposDocumentoFinanceiroListPage'
import ConfiguracaoFinanceiroPage from '@features/financeiro/ConfiguracaoFinanceiroPage'
import ContasBancariasListPage from '@features/financeiro/ContasBancariasListPage'
import ContaBancariaFormPage from '@features/financeiro/ContaBancariaFormPage'
import MovimentosCaixaBancoListPage from '@features/financeiro/MovimentosCaixaBancoListPage'
import ChequesEmitidosListPage from '@features/financeiro/ChequesEmitidosListPage'
import ChequesRecebidosListPage from '@features/financeiro/ChequesRecebidosListPage'
import TransferenciasBancariasListPage from '@features/financeiro/TransferenciasBancariasListPage'
import AplicacoesFinanceirasListPage from '@features/financeiro/AplicacoesFinanceirasListPage'
import PosicaoCaixaPage from '@features/financeiro/PosicaoCaixaPage'
import FluxoCaixaPage from '@features/financeiro/FluxoCaixaPage'
import TitulosEmAbertoPage from '@features/financeiro/TitulosEmAbertoPage'
import ConciliacaoBancariaPage from '@features/financeiro/ConciliacaoBancariaPage'
import EdiBancarioPage from '@features/financeiro/EdiBancarioPage'
import { MODULE_NAV } from '@shared/ui/NavConfig'
import AppLayout from '@app/layout/AppLayout'
import RequireAuth from './RequireAuth'
import RequireAuthority from './RequireAuthority'
import NotFoundPage from './NotFoundPage'

const cadastroBaseNav = MODULE_NAV.find((entry) => entry.key === 'cadastro-base')
if (!cadastroBaseNav) {
  throw new Error("NavConfig sem entrada 'cadastro-base' — AppRouter depende dela pra montar as rotas de Clientes.")
}

const estoqueNav = MODULE_NAV.find((entry) => entry.key === 'estoque')
if (!estoqueNav) {
  throw new Error("NavConfig sem entrada 'estoque' — AppRouter depende dela pra montar as rotas de Depósitos.")
}

const segurancaNav = MODULE_NAV.find((entry) => entry.key === 'seguranca')
if (!segurancaNav) {
  throw new Error("NavConfig sem entrada 'seguranca' — AppRouter depende dela pra montar as rotas de Usuários.")
}

const gedNav = MODULE_NAV.find((entry) => entry.key === 'ged')
if (!gedNav) {
  throw new Error("NavConfig sem entrada 'ged' — AppRouter depende dela pra montar as rotas de Documentos.")
}

const comprasNav = MODULE_NAV.find((entry) => entry.key === 'compras')
if (!comprasNav) {
  throw new Error("NavConfig sem entrada 'compras' — AppRouter depende dela pra montar as rotas de Requisições/Pedidos.")
}

const biNav = MODULE_NAV.find((entry) => entry.key === 'bi')
if (!biNav) {
  throw new Error("NavConfig sem entrada 'bi' — AppRouter depende dela pra montar as rotas de indicadores.")
}

const vendasNav = MODULE_NAV.find((entry) => entry.key === 'vendas')
if (!vendasNav) {
  throw new Error("NavConfig sem entrada 'vendas' — AppRouter depende dela pra montar as rotas de Orçamentos/Vendas.")
}

const fiscalNav = MODULE_NAV.find((entry) => entry.key === 'fiscal')
if (!fiscalNav) {
  throw new Error("NavConfig sem entrada 'fiscal' — AppRouter depende dela pra montar as rotas de Notas Fiscais/CT-e.")
}

const osNav = MODULE_NAV.find((entry) => entry.key === 'os')
if (!osNav) {
  throw new Error("NavConfig sem entrada 'os' — AppRouter depende dela pra montar as rotas de Ordem de Serviço.")
}

const crmNav = MODULE_NAV.find((entry) => entry.key === 'crm')
if (!crmNav) {
  throw new Error("NavConfig sem entrada 'crm' — AppRouter depende dela pra montar as rotas de Leads/Oportunidades.")
}

const financeiroNav = MODULE_NAV.find((entry) => entry.key === 'financeiro')
if (!financeiroNav) {
  throw new Error("NavConfig sem entrada 'financeiro' — AppRouter depende dela pra montar as rotas de Contas a Receber/Pagar.")
}

const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  { path: '/callback', element: <CallbackPage /> },
  { path: '/logout', element: <LogoutPage /> },
  { path: '/silent-renew', element: <SilentRenewPage /> },
  {
    path: '/',
    element: (
      <RequireAuth>
        <AppLayout />
      </RequireAuth>
    ),
    children: [
      { index: true, element: <DashboardPage /> },
      {
        path: cadastroBaseNav.path.slice(1),
        element: (
          <RequireAuthority anyOf={cadastroBaseNav.requiredAuthorities}>
            <Outlet />
          </RequireAuthority>
        ),
        children: [
          // Todos os 6 sub-cadastros (Clientes/Fornecedores/Colaboradores/
          // Empresas/Produtos/Tabelas de Preço) já têm tela — este index é só
          // um redirect de conveniência, não uma landing própria do módulo.
          { index: true, element: <Navigate to="clientes" replace /> },
          { path: 'clientes', element: <ClientesListPage /> },
          { path: 'clientes/novo', element: <ClienteFormPage /> },
          { path: 'clientes/:id', element: <ClienteFormPage /> },
          { path: 'fornecedores', element: <FornecedoresListPage /> },
          { path: 'fornecedores/novo', element: <FornecedorFormPage /> },
          { path: 'fornecedores/:id', element: <FornecedorFormPage /> },
          { path: 'transportadoras', element: <TransportadorasListPage /> },
          { path: 'transportadoras/novo', element: <TransportadoraFormPage /> },
          { path: 'transportadoras/:id', element: <TransportadoraFormPage /> },
          { path: 'colaboradores', element: <ColaboradoresListPage /> },
          { path: 'colaboradores/novo', element: <ColaboradorFormPage /> },
          { path: 'colaboradores/:id', element: <ColaboradorFormPage /> },
          { path: 'empresas', element: <EmpresasListPage /> },
          { path: 'empresas/novo', element: <EmpresaFormPage /> },
          { path: 'empresas/:id', element: <EmpresaFormPage /> },
          { path: 'produtos', element: <ProdutosListPage /> },
          { path: 'produtos/novo', element: <ProdutoFormPage /> },
          { path: 'produtos/:id', element: <ProdutoFormPage /> },
          { path: 'tabelas-preco', element: <TabelasPrecoListPage /> },
          { path: 'tabelas-preco/novo', element: <TabelaPrecoFormPage /> },
          { path: 'tabelas-preco/:id', element: <TabelaPrecoFormPage /> },
          { path: 'grupos-tributarios', element: <GruposTributariosListPage /> },
          { path: 'operacoes-fiscais', element: <OperacoesFiscaisListPage /> },
          { path: 'configuracoes-tributarias', element: <ConfiguracoesTributariasListPage /> },
          { path: 'configuracoes-tributarias/novo', element: <ConfiguracaoTributariaFormPage /> },
          { path: 'configuracoes-tributarias/:id', element: <ConfiguracaoTributariaFormPage /> },
        ],
      },
      {
        path: estoqueNav.path.slice(1),
        element: (
          <RequireAuthority anyOf={estoqueNav.requiredAuthorities}>
            <Outlet />
          </RequireAuthority>
        ),
        children: [
          // Fase A (2026-09-02): Depósitos/Saldos/Movimentos/Transferências/
          // Configuração. Fase B (2026-09-02): Inventário Físico/Lote/Número
          // de Série/Kit/Curva ABC.
          { index: true, element: <Navigate to="depositos" replace /> },
          { path: 'depositos', element: <DepositosListPage /> },
          { path: 'depositos/novo', element: <DepositoFormPage /> },
          { path: 'depositos/:id', element: <DepositoFormPage /> },
          { path: 'saldos', element: <SaldosListPage /> },
          { path: 'movimentos', element: <MovimentosListPage /> },
          { path: 'transferencias', element: <TransferenciasListPage /> },
          { path: 'inventarios', element: <InventariosListPage /> },
          { path: 'inventarios/:id', element: <InventarioDetailPage /> },
          { path: 'lotes', element: <LotesListPage /> },
          { path: 'numeros-serie', element: <NumerosSerieListPage /> },
          { path: 'kits', element: <KitsPage /> },
          { path: 'curva-abc', element: <CurvaAbcPage /> },
          { path: 'configuracao', element: <ConfiguracaoEstoquePage /> },
        ],
      },
      {
        path: segurancaNav.path.slice(1),
        element: (
          <RequireAuthority anyOf={segurancaNav.requiredAuthorities}>
            <Outlet />
          </RequireAuthority>
        ),
        children: [
          { index: true, element: <Navigate to="usuarios" replace /> },
          { path: 'usuarios', element: <UsuariosListPage /> },
          { path: 'usuarios/novo', element: <UsuarioFormPage /> },
          { path: 'usuarios/:id', element: <UsuarioFormPage /> },
          { path: 'papeis', element: <PapeisListPage /> },
          { path: 'papeis/novo', element: <PapelFormPage /> },
          { path: 'papeis/:id', element: <PapelFormPage /> },
          { path: 'funcionalidades', element: <FuncionalidadesListPage /> },
          { path: 'modulos', element: <ModulosListPage /> },
        ],
      },
      {
        path: gedNav.path.slice(1),
        element: (
          <RequireAuthority anyOf={gedNav.requiredAuthorities}>
            <Outlet />
          </RequireAuthority>
        ),
        children: [
          { index: true, element: <Navigate to="documentos" replace /> },
          { path: 'documentos', element: <DocumentosListPage /> },
          { path: 'documentos/novo', element: <DocumentoUploadPage /> },
          { path: 'documentos/:id', element: <DocumentoDetailPage /> },
          { path: 'pastas', element: <PastasListPage /> },
          { path: 'tipos-documento', element: <TiposDocumentoListPage /> },
        ],
      },
      {
        path: comprasNav.path.slice(1),
        element: (
          <RequireAuthority anyOf={comprasNav.requiredAuthorities}>
            <Outlet />
          </RequireAuthority>
        ),
        children: [
          // Cotações e Recebimentos não têm listagem própria no menu — só
          // existem no contexto de uma Requisição/Pedido pai (mesmo padrão de
          // Transferências/Inventários no Estoque), então só ganham rota de
          // detalhe aqui, sem entrada em `compras-*` do NavConfig.
          { index: true, element: <Navigate to="requisicoes" replace /> },
          { path: 'requisicoes', element: <RequisicoesListPage /> },
          { path: 'requisicoes/novo', element: <RequisicaoFormPage /> },
          { path: 'requisicoes/:id', element: <RequisicaoDetailPage /> },
          { path: 'cotacoes/:id', element: <CotacaoDetailPage /> },
          { path: 'pedidos', element: <PedidosListPage /> },
          { path: 'pedidos/novo', element: <PedidoFormPage /> },
          { path: 'pedidos/:id', element: <PedidoDetailPage /> },
          { path: 'configuracao', element: <ConfiguracaoComprasPage /> },
        ],
      },
      {
        path: biNav.path.slice(1),
        element: (
          <RequireAuthority anyOf={biNav.requiredAuthorities}>
            <Outlet />
          </RequireAuthority>
        ),
        children: [
          { index: true, element: <Navigate to="estoque-valor" replace /> },
          { path: 'estoque-valor', element: <EstoqueValorPage /> },
          { path: 'curva-abc', element: <CurvaAbcIndicadorPage /> },
          { path: 'compras-fornecedor', element: <ComprasFornecedorPage /> },
          { path: 'etl', element: <EtlExecucaoPage /> },
        ],
      },
      {
        path: vendasNav.path.slice(1),
        element: (
          <RequireAuthority anyOf={vendasNav.requiredAuthorities}>
            <Outlet />
          </RequireAuthority>
        ),
        children: [
          // Fase A (2026-09-05): Vendedores/Perfis de Comissão, Condições de
          // Pagamento, Notas Fiscais (Tipo/Modelo), Orçamento→Venda→Frete/
          // Comissão. Fase B (registrada no BACKLOG, não construída ainda):
          // AFV (VendedorRota/VendedorMeta), ComissaoObjetivo.
          { index: true, element: <Navigate to="orcamentos" replace /> },
          { path: 'orcamentos', element: <OrcamentosListPage /> },
          { path: 'orcamentos/novo', element: <OrcamentoFormPage /> },
          { path: 'orcamentos/:id', element: <OrcamentoDetailPage /> },
          { path: 'vendas', element: <VendasListPage /> },
          { path: 'vendas/novo', element: <VendaFormPage /> },
          { path: 'vendas/:id', element: <VendaDetailPage /> },
          { path: 'vendedores', element: <VendedoresListPage /> },
          { path: 'vendedores/novo', element: <VendedorFormPage /> },
          { path: 'vendedores/:id', element: <VendedorFormPage /> },
          { path: 'comissao-perfis', element: <ComissaoPerfisListPage /> },
          { path: 'comissao-perfis/novo', element: <ComissaoPerfilFormPage /> },
          { path: 'comissao-perfis/:id', element: <ComissaoPerfilFormPage /> },
          { path: 'condicoes-pagamento', element: <CondicoesPagamentoListPage /> },
          { path: 'condicoes-pagamento/novo', element: <CondicaoPagamentoFormPage /> },
          { path: 'condicoes-pagamento/:id', element: <CondicaoPagamentoFormPage /> },
          { path: 'notas-fiscais-tipos', element: <NotasFiscaisTiposPage /> },
          { path: 'relatorios', element: <RelatoriosVendasPage /> },
          { path: 'comissoes', element: <ComissoesListPage /> },
        ],
      },
      {
        path: fiscalNav.path.slice(1),
        element: (
          <RequireAuthority anyOf={fiscalNav.requiredAuthorities}>
            <Outlet />
          </RequireAuthority>
        ),
        children: [
          // NF-e/CT-e nascem a partir de uma Venda `CONFIRMADA` (botões na
          // `VendaDetailPage`, `features/vendas`) — sem form de criação aqui.
          { index: true, element: <Navigate to="notas-fiscais" replace /> },
          { path: 'notas-fiscais', element: <NotasFiscaisListPage /> },
          { path: 'notas-fiscais/:id', element: <NotaFiscalDetailPage /> },
          { path: 'ctes', element: <CtesListPage /> },
          { path: 'ctes/:id', element: <CteDetailPage /> },
          { path: 'notas-servico', element: <NotasServicoListPage /> },
          { path: 'notas-servico/:id', element: <NotaServicoDetailPage /> },
          { path: 'listas-servico', element: <ListasServicoMunicipalListPage /> },
        ],
      },
      {
        path: osNav.path.slice(1),
        element: (
          <RequireAuthority anyOf={osNav.requiredAuthorities}>
            <Outlet />
          </RequireAuthority>
        ),
        children: [
          { index: true, element: <Navigate to="aberturas" replace /> },
          { path: 'aberturas', element: <OsAberturasListPage /> },
          { path: 'aberturas/novo', element: <OsAberturaFormPage /> },
          { path: 'aberturas/:id', element: <OsAberturaDetailPage /> },
          { path: 'status', element: <OsStatusListPage /> },
          { path: 'equipamentos', element: <OsEquipamentosListPage /> },
        ],
      },
      {
        path: crmNav.path.slice(1),
        element: (
          <RequireAuthority anyOf={crmNav.requiredAuthorities}>
            <Outlet />
          </RequireAuthority>
        ),
        children: [
          { index: true, element: <Navigate to="funil" replace /> },
          { path: 'funil', element: <OportunidadesKanbanPage /> },
          { path: 'oportunidades', element: <OportunidadesListPage /> },
          { path: 'oportunidades/novo', element: <OportunidadeFormPage /> },
          { path: 'oportunidades/:id', element: <OportunidadeFormPage /> },
          { path: 'leads', element: <LeadsListPage /> },
          { path: 'leads/novo', element: <LeadFormPage /> },
          { path: 'leads/:id', element: <LeadFormPage /> },
          { path: 'estagios-funil', element: <EstagiosFunilListPage /> },
        ],
      },
      {
        path: financeiroNav.path.slice(1),
        element: (
          <RequireAuthority anyOf={financeiroNav.requiredAuthorities}>
            <Outlet />
          </RequireAuthority>
        ),
        children: [
          // Fase A (2026-09-07): Contas a Receber/Pagar + catálogos (Natureza/Tipo de
          // Documento) + Configuração Financeira. Fase B (2026-09-07): Caixa e Bancos/
          // Cheques/Tesouraria/Fluxo de Caixa/Conciliação Bancária/EDI Bancário.
          { index: true, element: <Navigate to="contas-receber" replace /> },
          { path: 'contas-receber', element: <ContasReceberListPage /> },
          { path: 'contas-receber/novo', element: <ContaReceberFormPage /> },
          { path: 'contas-receber/:id', element: <ContaReceberDetailPage /> },
          { path: 'contas-pagar', element: <ContasPagarListPage /> },
          { path: 'contas-pagar/novo', element: <ContaPagarFormPage /> },
          { path: 'contas-pagar/:id', element: <ContaPagarDetailPage /> },
          { path: 'naturezas-financeiras', element: <NaturezasFinanceirasListPage /> },
          { path: 'tipos-documento', element: <TiposDocumentoFinanceiroListPage /> },
          { path: 'configuracao', element: <ConfiguracaoFinanceiroPage /> },
          { path: 'contas-bancarias', element: <ContasBancariasListPage /> },
          { path: 'contas-bancarias/novo', element: <ContaBancariaFormPage /> },
          { path: 'contas-bancarias/:id', element: <ContaBancariaFormPage /> },
          { path: 'movimentos', element: <MovimentosCaixaBancoListPage /> },
          { path: 'cheques-emitidos', element: <ChequesEmitidosListPage /> },
          { path: 'cheques-recebidos', element: <ChequesRecebidosListPage /> },
          { path: 'transferencias', element: <TransferenciasBancariasListPage /> },
          { path: 'aplicacoes', element: <AplicacoesFinanceirasListPage /> },
          { path: 'posicao-caixa', element: <PosicaoCaixaPage /> },
          { path: 'fluxo-caixa', element: <FluxoCaixaPage /> },
          { path: 'titulos-em-aberto', element: <TitulosEmAbertoPage /> },
          { path: 'conciliacao-bancaria', element: <ConciliacaoBancariaPage /> },
          { path: 'edi-bancario', element: <EdiBancarioPage /> },
        ],
      },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
])

export default function AppRouter() {
  return <RouterProvider router={router} />
}
