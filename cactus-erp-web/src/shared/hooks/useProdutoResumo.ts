import { useQuery } from '@tanstack/react-query'
import { buscarProdutoResumo } from '@shared/api/produtoLookup'
import type { AppError } from '@shared/api/problemDetail'

/**
 * `staleTime` longo de propósito: nome/código de produto muda raramente, e
 * cada linha de uma tabela de Saldo/Movimento/Transferência dispara uma
 * dessas — o cache do TanStack Query dedupe por `produtoId` (mesma chave),
 * então N linhas com o mesmo produto viram 1 request só.
 *
 * `retry` desliga especificamente pra 404 — produtoId é referência solta
 * cross-serviço (Estoque não tem FK pro cadastro-base), então um id "órfão"
 * (nunca cadastrado de fato, só usado como número solto em dado de
 * teste/seed) é uma condição permanente, não transitória: repetir a mesma
 * chamada não muda o resultado (achado real ao vivo, 2026-09-02 — sem isso,
 * cada linha órfã gerava `1 (default global) + 1` tentativas de rede à toa).
 */
export function useProdutoResumo(produtoId: number | undefined) {
  return useQuery({
    queryKey: ['produto-resumo', produtoId],
    queryFn: () => buscarProdutoResumo(produtoId as number),
    enabled: produtoId !== undefined,
    staleTime: 5 * 60_000,
    retry: (failureCount, erro) => (erro as unknown as AppError).status !== 404 && failureCount < 1,
  })
}
