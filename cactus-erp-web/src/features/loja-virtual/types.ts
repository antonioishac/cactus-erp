import type { Situacao } from '@shared/domain/situacao'

// --- Enums ---

export type PlanoAssinatura = 'BASICO' | 'INTERMEDIARIO' | 'PREMIUM'
export const PLANO_ASSINATURA_LABEL: Record<PlanoAssinatura, string> = {
  BASICO: 'Básico',
  INTERMEDIARIO: 'Intermediário',
  PREMIUM: 'Premium',
}

export type SituacaoAssinatura = 'ATIVA' | 'INADIMPLENTE' | 'CANCELADA'
export const SITUACAO_ASSINATURA_LABEL: Record<SituacaoAssinatura, string> = {
  ATIVA: 'Ativa',
  INADIMPLENTE: 'Inadimplente',
  CANCELADA: 'Cancelada',
}

/** `LISTA` exige opções pré-cadastradas; `TEXTO`/`NUMERO` aceitam qualquer valor digitado na variante. */
export type TipoAtributo = 'TEXTO' | 'NUMERO' | 'LISTA'
export const TIPO_ATRIBUTO_LABEL: Record<TipoAtributo, string> = {
  TEXTO: 'Texto',
  NUMERO: 'Número',
  LISTA: 'Lista (opções fixas)',
}

export type FormaPagamentoLoja = 'PIX' | 'CARTAO_CREDITO' | 'BOLETO'
export const FORMA_PAGAMENTO_LOJA_LABEL: Record<FormaPagamentoLoja, string> = {
  PIX: 'Pix',
  CARTAO_CREDITO: 'Cartão de Crédito',
  BOLETO: 'Boleto',
}

/** AGUARDANDO_PAGAMENTO→PAGO→EM_SEPARACAO→ENVIADO→ENTREGUE; CANCELADO só antes de ENVIADO. */
export type SituacaoPedido = 'AGUARDANDO_PAGAMENTO' | 'PAGO' | 'EM_SEPARACAO' | 'ENVIADO' | 'ENTREGUE' | 'CANCELADO'
export const SITUACAO_PEDIDO_LABEL: Record<SituacaoPedido, string> = {
  AGUARDANDO_PAGAMENTO: 'Aguardando pagamento',
  PAGO: 'Pago',
  EM_SEPARACAO: 'Em separação',
  ENVIADO: 'Enviado',
  ENTREGUE: 'Entregue',
  CANCELADO: 'Cancelado',
}

// --- Loja ---

export interface LojaRequest {
  nome: string
  razaoSocial?: string
  cnpj?: string
  empresaId?: number
  subdominio: string
  emailContato?: string
  telefone?: string
  planoAssinatura?: PlanoAssinatura
}

export interface LojaResponse {
  id: number
  nome: string
  razaoSocial?: string
  cnpj?: string
  empresaId?: number
  subdominio: string
  emailContato?: string
  telefone?: string
  planoAssinatura: PlanoAssinatura
  situacaoAssinatura: SituacaoAssinatura
  dataInicioAssinatura?: string
  situacao: Situacao
}

// --- Categoria da Loja ---

export interface CategoriaLojaRequest {
  lojaId: number
  categoriaPaiId?: number
  nome: string
  ordem?: number
}

export interface CategoriaLojaResponse {
  id: number
  lojaId: number
  categoriaPaiId?: number
  nome: string
  ordem: number
  situacao: Situacao
}

// --- Atributo de Categoria ---

export interface AtributoCategoriaOpcaoRequest {
  valor: string
  ordem?: number
}

export interface AtributoCategoriaOpcaoResponse {
  id: number
  valor: string
  ordem: number
}

export interface AtributoCategoriaRequest {
  categoriaLojaId: number
  nome: string
  tipo: TipoAtributo
  obrigatorio: boolean
  geraVariacao: boolean
  ordem?: number
  opcoes?: AtributoCategoriaOpcaoRequest[]
}

export interface AtributoCategoriaResponse {
  id: number
  categoriaLojaId: number
  nome: string
  tipo: TipoAtributo
  obrigatorio: boolean
  geraVariacao: boolean
  ordem: number
  opcoes: AtributoCategoriaOpcaoResponse[]
}

// --- Produto da Loja ---

export interface ProdutoLojaRequest {
  lojaId: number
  categoriaLojaId: number
  nome: string
  descricao?: string
  marca?: string
}

export interface ProdutoLojaResponse {
  id: number
  lojaId: number
  categoriaLojaId: number
  categoriaLojaNome: string
  nome: string
  descricao?: string
  marca?: string
  situacao: Situacao
  variantes: VarianteProdutoLojaResponse[]
}

// --- Variante do Produto ---

export interface ValorAtributoRequest {
  atributoCategoriaId: number
  valor: string
}

export interface ValorAtributoResponse {
  atributoCategoriaId: number
  atributoCategoriaNome: string
  valor: string
}

export interface VarianteProdutoLojaRequest {
  sku: string
  gtin?: string
  precoVenda: number
  precoPromocional?: number
  estoqueQuantidade?: number
  pesoKg?: number
  ncm?: string
  csosn?: string
  valoresAtributos?: ValorAtributoRequest[]
}

export interface VarianteProdutoLojaResponse {
  id: number
  produtoLojaId: number
  sku: string
  gtin?: string
  precoVenda: number
  precoPromocional?: number
  estoqueQuantidade: number
  estoqueReservado: number
  estoqueDisponivel: number
  pesoKg?: number
  ncm?: string
  csosn?: string
  situacao: Situacao
  valoresAtributos: ValorAtributoResponse[]
}

// --- Pedido ---

export interface ItemPedidoResponse {
  id: number
  varianteProdutoLojaId: number
  skuSnapshot: string
  nomeSnapshot: string
  ncmSnapshot?: string
  csosnSnapshot?: string
  quantidade: number
  precoUnitario: number
  valorTotal: number
}

export interface PedidoResponse {
  id: number
  lojaId: number
  clienteLojaId: number
  clienteNome: string
  clienteEmail: string
  clienteCpf?: string
  carrinhoId: number
  logradouroEntrega?: string
  numeroEntrega?: string
  complementoEntrega?: string
  bairroEntrega?: string
  cepEntrega?: string
  municipioEntrega?: string
  ufEntrega?: string
  formaPagamento: FormaPagamentoLoja
  valorSubtotal: number
  valorFrete: number
  valorTotal: number
  situacao: SituacaoPedido
  dataPagamento?: string
  motivoCancelamento?: string
  itens: ItemPedidoResponse[]
  dataCriacao: string
}

export interface CancelarPedidoRequest {
  motivo: string
}

export interface PedidosFiltro {
  lojaId?: number
}
