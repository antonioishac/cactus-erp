import type { AxiosInstance } from 'axios'

/** Formatos de exportação de relatório — também é o sufixo do endpoint no backend
 * (`/relatorios/<nome>/pdf` | `/xlsx`, ver `common.report` no cactus-erp-sistema). */
export type FormatoRelatorio = 'pdf' | 'xlsx'

/**
 * Baixa um arquivo binário de qualquer serviço e dispara o download no navegador —
 * blob + link sintético, nome resolvido do `Content-Disposition` quando presente.
 * Único ponto do app que faz isso (GED, EDI Bancário e exportação de relatórios
 * PDF/Excel usam este helper).
 *
 * Com `responseType: 'blob'`, o corpo de uma resposta de erro também chega como Blob
 * — o `ProblemDetail` não é lido, então o `AppError` rejeitado traz o `status`/`kind`
 * certos mas uma mensagem genérica. Quem chama decide a mensagem pelo `kind`.
 */
export async function baixarArquivo(
  api: AxiosInstance,
  url: string,
  nomeSugerido: string,
  params?: Record<string, unknown>,
): Promise<void> {
  const { data, headers } = await api.get<Blob>(url, { params, responseType: 'blob' })
  const contentDisposition: string | undefined = headers['content-disposition']
  const match = contentDisposition?.match(/filename\*?=(?:UTF-8'')?"?([^";]+)"?/i)
  const nomeArquivo = match?.[1] ? decodeURIComponent(match[1]) : nomeSugerido

  const blobUrl = window.URL.createObjectURL(data)
  const link = document.createElement('a')
  link.href = blobUrl
  link.download = nomeArquivo
  document.body.appendChild(link)
  link.click()
  link.remove()
  window.URL.revokeObjectURL(blobUrl)
}
