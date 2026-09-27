import { Skeleton } from 'antd'
import { useClienteResumo, useColaboradorResumo, useTransportadoraResumo } from './queries'

/** Células de tabela reaproveitáveis dentro do módulo Vendas — resolvem ids soltos cross-serviço (sem FK real) pro nome de exibição. Mesmo critério de `features/compras/ResumoCells.tsx`. */
export function ClienteResumoCell({ clienteId }: { clienteId: number }) {
  const { data, isLoading } = useClienteResumo(clienteId)
  if (isLoading) return <Skeleton.Input active size="small" style={{ width: 140 }} />
  return <>{data?.nome ?? `#${clienteId}`}</>
}

export function ColaboradorResumoCell({ colaboradorId }: { colaboradorId: number }) {
  const { data, isLoading } = useColaboradorResumo(colaboradorId)
  if (isLoading) return <Skeleton.Input active size="small" style={{ width: 140 }} />
  return <>{data?.nome ?? `#${colaboradorId}`}</>
}

export function TransportadoraResumoCell({ transportadoraId }: { transportadoraId: number }) {
  const { data, isLoading } = useTransportadoraResumo(transportadoraId)
  if (isLoading) return <Skeleton.Input active size="small" style={{ width: 140 }} />
  return <>{data?.nome ?? `#${transportadoraId}`}</>
}
