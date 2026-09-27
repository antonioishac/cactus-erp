import type { AxiosError } from 'axios'

/**
 * Formato de erro padrão de toda API do Cactus ERP: RFC 7807 `ProblemDetail`
 * (`application/problem+json`) mais extensões próprias do backend. Ver
 * `common/.../exception/ProblemDetailFactory.java` e `GlobalExceptionHandler` no
 * `cactus-erp-sistema` — este tipo espelha exatamente o JSON que eles produzem.
 */
export interface CampoInvalido {
  campo: string
  mensagem: string
}

export interface ApiProblemDetail {
  type?: string
  title?: string
  status: number
  detail?: string
  instance?: string
  codigo: string
  timestamp: string
  traceId?: string
  camposInvalidos?: CampoInvalido[]
}

export type AppErrorKind =
  | 'unauthorized' // 401
  | 'forbidden' // 403
  | 'validation' // 400/422 com camposInvalidos
  | 'conflict' // 409
  | 'notFound' // 404
  | 'network' // sem resposta do servidor (backend fora do ar, CORS bloqueado, etc)
  | 'unknown'

/** Erro normalizado que o resto do app consome — nunca lida com AxiosError cru. */
export interface AppError {
  status: number
  codigo: string
  message: string
  traceId?: string
  camposInvalidos?: CampoInvalido[]
  kind: AppErrorKind
}

function kindFromStatus(status: number, camposInvalidos?: CampoInvalido[]): AppErrorKind {
  if (camposInvalidos && camposInvalidos.length > 0) return 'validation'
  switch (status) {
    case 401:
      return 'unauthorized'
    case 403:
      return 'forbidden'
    case 404:
      return 'notFound'
    case 409:
      return 'conflict'
    case 422:
      return 'validation'
    default:
      return 'unknown'
  }
}

/**
 * Converte um erro do Axios (falha de rede, ou resposta com corpo `ProblemDetail`)
 * num {@link AppError} consistente. Todo interceptor de resposta das instâncias em
 * {@link httpClients} passa por aqui — nenhum outro lugar do app deve inspecionar
 * `AxiosError`/`error.response.data` diretamente.
 */
export function normalizeError(error: AxiosError<ApiProblemDetail>): AppError {
  if (!error.response) {
    return {
      status: 0,
      codigo: 'ERRO_REDE',
      message: 'Não foi possível conectar ao servidor. Verifique sua conexão ou tente novamente.',
      kind: 'network',
    }
  }

  const { status, data } = error.response

  if (data && typeof data === 'object' && 'codigo' in data) {
    return {
      status,
      codigo: data.codigo,
      message: data.detail ?? data.title ?? 'Ocorreu um erro inesperado.',
      traceId: data.traceId,
      camposInvalidos: data.camposInvalidos,
      kind: kindFromStatus(status, data.camposInvalidos),
    }
  }

  return {
    status,
    codigo: 'ERRO_DESCONHECIDO',
    message: 'Ocorreu um erro inesperado.',
    kind: kindFromStatus(status),
  }
}
