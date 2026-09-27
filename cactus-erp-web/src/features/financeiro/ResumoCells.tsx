import { Skeleton } from 'antd'
import { useClienteResumo, useContaBancaria, useFornecedorResumo } from './queries'

/** Células de tabela reaproveitáveis dentro do módulo Financeiro — resolvem ids soltos
 * cross-serviço (sem FK real) pro nome de exibição. Mesmo critério de
 * `features/vendas/ResumoCells.tsx`/`features/compras/ResumoCells.tsx`. */
export function ClienteResumoCell({ clienteId }: { clienteId: number }) {
  const { data, isLoading } = useClienteResumo(clienteId)
  if (isLoading) return <Skeleton.Input active size="small" style={{ width: 140 }} />
  return <>{data?.nome ?? `#${clienteId}`}</>
}

export function FornecedorResumoCell({ fornecedorId }: { fornecedorId: number }) {
  const { data, isLoading } = useFornecedorResumo(fornecedorId)
  if (isLoading) return <Skeleton.Input active size="small" style={{ width: 140 }} />
  return <>{data?.nome ?? `#${fornecedorId}`}</>
}

/** Diferente das duas acima, `ContaBancaria` é do PRÓPRIO `financeiro-service` (sem hop
 * cross-serviço) — mesmo assim ganha célula própria porque os ids aparecem soltos em
 * várias tabelas (Movimentos/Cheques/Transferências/Aplicações). */
export function ContaBancariaResumoCell({ contaBancariaId }: { contaBancariaId: number }) {
  const { data, isLoading } = useContaBancaria(contaBancariaId)
  if (isLoading) return <Skeleton.Input active size="small" style={{ width: 140 }} />
  return <>{data?.nome ?? `#${contaBancariaId}`}</>
}
