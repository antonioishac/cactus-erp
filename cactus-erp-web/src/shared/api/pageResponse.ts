/**
 * Envelope de paginação usado por toda listagem do cactus-erp-sistema — espelha
 * `common/.../rest/PageResponse.java` (não é o `Page<T>` padrão do Spring Data,
 * os nomes de campo são em português). Escalado agora, sem consumidor real ainda
 * nesta entrega (auth + shell) — a primeira tela de listagem (Clientes, próxima
 * entrega) é quem usa isso de fato.
 */
export interface PageResponse<T> {
  conteudo: T[]
  paginaAtual: number
  tamanhoPagina: number
  totalRegistros: number
  totalPaginas: number
  primeiraPagina: boolean
  ultimaPagina: boolean
}
