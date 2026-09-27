import { Skeleton } from 'antd'
import { useClienteResumo, useColaboradorResumo } from './queries'

/** Células de tabela reaproveitáveis dentro do módulo OS — resolvem ids soltos cross-serviço (clienteId/colaboradorId, sem FK real) pro nome de exibição. Não promovidas a `shared/` de propósito, mesmo critério já registrado em `features/compras/ResumoCells.tsx`. */
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
