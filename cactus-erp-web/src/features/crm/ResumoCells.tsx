import { Skeleton } from 'antd'
import { useClienteResumo, useUsuarioResumo, useVendedorResumo } from './queries'

/** Célula reaproveitável dentro do módulo CRM — resolve `clienteId` solto cross-serviço (sem FK real) pro nome de exibição. Não promovida a `shared/` de propósito, mesmo critério já registrado em `features/compras/ResumoCells.tsx`. */
export function ClienteResumoCell({ clienteId }: { clienteId: number }) {
  const { data, isLoading } = useClienteResumo(clienteId)
  if (isLoading) return <Skeleton.Input active size="small" style={{ width: 140 }} />
  return <>{data?.nome ?? `#${clienteId}`}</>
}

/** `vendedorId` (vendas-service) — resolução em 2 hops, ver Javadoc de `VendedorResumo`. */
export function VendedorResumoCell({ vendedorId }: { vendedorId: number }) {
  const { data, isLoading } = useVendedorResumo(vendedorId)
  if (isLoading) return <Skeleton.Input active size="small" style={{ width: 140 }} />
  return <>{data?.nome ?? `#${vendedorId}`}</>
}

/** `responsavelId`/`usuarioId` (seguranca-service). */
export function UsuarioResumoCell({ usuarioId }: { usuarioId: number }) {
  const { data, isLoading } = useUsuarioResumo(usuarioId)
  if (isLoading) return <Skeleton.Input active size="small" style={{ width: 120 }} />
  return <>{data?.nome ?? `#${usuarioId}`}</>
}
