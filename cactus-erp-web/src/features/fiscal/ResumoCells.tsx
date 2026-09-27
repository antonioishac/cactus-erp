import { Skeleton } from 'antd'
import { useClienteResumo, useTransportadoraResumo } from './queries'

/** Resolve ids soltos cross-serviço (sem FK real) pro nome de exibição — mesmo critério de `features/vendas/ResumoCells.tsx`/`features/compras/ResumoCells.tsx`. */
export function ClienteResumoCell({ clienteId }: { clienteId: number }) {
  const { data, isLoading } = useClienteResumo(clienteId)
  if (isLoading) return <Skeleton.Input active size="small" style={{ width: 140 }} />
  return <>{data?.nome ?? `#${clienteId}`}</>
}

export function TransportadoraResumoCell({ transportadoraId }: { transportadoraId: number }) {
  const { data, isLoading } = useTransportadoraResumo(transportadoraId)
  if (isLoading) return <Skeleton.Input active size="small" style={{ width: 140 }} />
  return <>{data?.nome ?? `#${transportadoraId}`}</>
}
