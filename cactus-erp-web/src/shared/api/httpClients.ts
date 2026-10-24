import axios, { type AxiosError, type AxiosInstance } from 'axios'
import { env } from '@shared/config/env'
import { normalizeError, type ApiProblemDetail } from './problemDetail'

/**
 * Injeção de dependência deliberada (em vez de importar `authStore` direto aqui):
 * evita um ciclo de import `httpClients -> authStore -> trocarEmpresa -> httpClients`
 * e mantém este módulo testável sem precisar montar o store zustand inteiro.
 * `AppProviders.tsx` chama {@link configureHttpAuth} uma vez, no boot do app,
 * ligando estas instâncias ao estado real de autenticação.
 */
let getEffectiveToken: () => string | null = () => null
let onUnauthorized: () => void = () => {}

export function configureHttpAuth(
  tokenProvider: () => string | null,
  unauthorizedHandler: () => void,
): void {
  getEffectiveToken = tokenProvider
  onUnauthorized = unauthorizedHandler
}

function createApiClient(baseURL: string): AxiosInstance {
  const instance = axios.create({ baseURL })

  instance.interceptors.request.use((config) => {
    // Só preenche Authorization se o chamador não tiver passado um explícito —
    // é assim que trocarEmpresa() consegue usar o token recém-renovado em vez do
    // effectiveToken (possivelmente da empresa antiga) durante a renovação silenciosa.
    if (!config.headers.Authorization) {
      const token = getEffectiveToken()
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
    }
    return config
  })

  instance.interceptors.response.use(
    (response) => response,
    (error: AxiosError<ApiProblemDetail>) => {
      const appError = normalizeError(error)
      if (appError.kind === 'unauthorized') {
        onUnauthorized()
      }
      return Promise.reject(appError)
    },
  )

  return instance
}

/** seguranca-service — Authorization Server + API de sessão (/api/v1/auth/**). */
export const segurancaApi = createApiClient(env.segurancaApiUrl)

/** cadastro-base-service — cadastros base. */
export const cadastroBaseApi = createApiClient(env.cadastroBaseApiUrl)

/** controle-estoque-service — depósitos, saldo, movimentos, transferências, inventário... */
export const estoqueApi = createApiClient(env.estoqueApiUrl)

/** gestao-documentos-service — GED: documentos, versões, pastas, tipos de documento. */
export const gedApi = createApiClient(env.gedApiUrl)

/** gestao-compras-service — requisição, cotação, pedido, recebimento de compra. */
export const comprasApi = createApiClient(env.comprasApiUrl)

/** business-intelligence-service — indicadores (snapshots do ETL) + disparo manual de ETL. */
export const biApi = createApiClient(env.biApiUrl)

/** vendas-service — Vendedor/Comissão/AFV, Condições de Pagamento, Orçamento, Venda/Frete. */
export const vendasApi = createApiClient(env.vendasApiUrl)

/** fiscal-service — NF-e/CT-e/NFS-e/NFC-e a partir de Venda/OS/Pedido (ponte ACBr). */
export const fiscalApi = createApiClient(env.fiscalApiUrl)

/** os-service — Ordem de Serviço: abertura, equipamentos, itens (peça/mão de obra), evolução. */
export const osApi = createApiClient(env.osApiUrl)

/** crm-service — Lead/Oportunidade (funil), Atividade/Interação, Estágios do Funil. */
export const crmApi = createApiClient(env.crmApiUrl)

/** financeiro-service — Contas a Receber/Pagar, Caixa e Bancos, Tesouraria, Conciliação, EDI Bancário. */
export const financeiroApi = createApiClient(env.financeiroApiUrl)

/** contabilidade-service — Plano de Contas, Centro de Custo, Lançamento Contábil, Balancete/Razão, SPED Contábil. */
export const contabilidadeApi = createApiClient(env.contabilidadeApiUrl)

/** loja-virtual-service — admin da plataforma: Lojas, Categorias/Atributos, Produtos/Variantes, Pedidos. */
export const lojaApi = createApiClient(env.lojaApiUrl)

/** sincronizacao-service — camada anticorrupção ERP↔PDV: lotes/registros de sincronização, reprocessamento. */
export const sincronizacaoApi = createApiClient(env.sincronizacaoApiUrl)
