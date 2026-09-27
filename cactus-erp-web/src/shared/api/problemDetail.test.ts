import { describe, expect, it } from 'vitest'
import type { AxiosError } from 'axios'
import { normalizeError, type ApiProblemDetail } from './problemDetail'

function axiosErrorComResposta(status: number, data: ApiProblemDetail): AxiosError<ApiProblemDetail> {
  return {
    isAxiosError: true,
    name: 'AxiosError',
    message: 'Request failed',
    toJSON: () => ({}),
    response: { status, data, statusText: '', headers: {}, config: {} as never },
  } as AxiosError<ApiProblemDetail>
}

function axiosErrorSemResposta(): AxiosError<ApiProblemDetail> {
  return {
    isAxiosError: true,
    name: 'AxiosError',
    message: 'Network Error',
    toJSON: () => ({}),
    response: undefined,
  } as AxiosError<ApiProblemDetail>
}

describe('normalizeError', () => {
  it('mapeia 401 para kind "unauthorized"', () => {
    const problem: ApiProblemDetail = {
      status: 401,
      title: 'Unauthorized',
      detail: 'Autenticação necessária',
      codigo: 'COMUM_NAO_AUTENTICADO',
      timestamp: '2026-08-31T12:00:00Z',
      traceId: 'trace-1',
    }
    const result = normalizeError(axiosErrorComResposta(401, problem))
    expect(result.kind).toBe('unauthorized')
    expect(result.codigo).toBe('COMUM_NAO_AUTENTICADO')
    expect(result.traceId).toBe('trace-1')
  })

  it('mapeia 403 para kind "forbidden"', () => {
    const problem: ApiProblemDetail = {
      status: 403,
      codigo: 'COMUM_ACESSO_NEGADO',
      timestamp: '2026-08-31T12:00:00Z',
    }
    expect(normalizeError(axiosErrorComResposta(403, problem)).kind).toBe('forbidden')
  })

  it('mapeia 409 para kind "conflict"', () => {
    const problem: ApiProblemDetail = {
      status: 409,
      codigo: 'SEGURANCA_USUARIO_PAPEL_EMPRESA_DUPLICADO',
      timestamp: '2026-08-31T12:00:00Z',
    }
    expect(normalizeError(axiosErrorComResposta(409, problem)).kind).toBe('conflict')
  })

  it('mapeia 422 para kind "validation"', () => {
    const problem: ApiProblemDetail = {
      status: 422,
      codigo: 'SEGURANCA_USUARIO_SEM_ACESSO_EMPRESA',
      timestamp: '2026-08-31T12:00:00Z',
    }
    expect(normalizeError(axiosErrorComResposta(422, problem)).kind).toBe('validation')
  })

  it('mapeia 400 com camposInvalidos para kind "validation" e preserva os campos', () => {
    const problem: ApiProblemDetail = {
      status: 400,
      codigo: 'COMUM_REQUISICAO_INVALIDA',
      timestamp: '2026-08-31T12:00:00Z',
      camposInvalidos: [{ campo: 'nome', mensagem: 'não pode estar em branco' }],
    }
    const result = normalizeError(axiosErrorComResposta(400, problem))
    expect(result.kind).toBe('validation')
    expect(result.camposInvalidos).toEqual([{ campo: 'nome', mensagem: 'não pode estar em branco' }])
  })

  it('mapeia 404 para kind "notFound"', () => {
    const problem: ApiProblemDetail = {
      status: 404,
      codigo: 'COMUM_RECURSO_NAO_ENCONTRADO',
      timestamp: '2026-08-31T12:00:00Z',
    }
    expect(normalizeError(axiosErrorComResposta(404, problem)).kind).toBe('notFound')
  })

  it('erro sem resposta (rede/CORS) vira kind "network"', () => {
    const result = normalizeError(axiosErrorSemResposta())
    expect(result.kind).toBe('network')
    expect(result.status).toBe(0)
  })
})
