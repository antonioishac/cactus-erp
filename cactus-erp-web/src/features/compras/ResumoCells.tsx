import { Skeleton } from 'antd'
import { useColaboradorResumo, useFornecedorResumo } from './queries'

/** Células de tabela reaproveitáveis dentro do módulo Compras — resolvem ids soltos cross-serviço (colaboradorId/fornecedorId, sem FK real) pro nome de exibição. Não promovidas a `shared/` de propósito: só têm consumidor aqui, mesmo critério já registrado pra `ProdutoResumoCell` (que teve 2+ consumidores). */
export function ColaboradorResumoCell({ colaboradorId }: { colaboradorId: number }) {
  const { data, isLoading } = useColaboradorResumo(colaboradorId)
  if (isLoading) return <Skeleton.Input active size="small" style={{ width: 140 }} />
  return <>{data?.nome ?? `#${colaboradorId}`}</>
}

export function FornecedorResumoCell({ fornecedorId }: { fornecedorId: number }) {
  const { data, isLoading } = useFornecedorResumo(fornecedorId)
  if (isLoading) return <Skeleton.Input active size="small" style={{ width: 140 }} />
  return <>{data?.nome ?? `#${fornecedorId}`}</>
}
