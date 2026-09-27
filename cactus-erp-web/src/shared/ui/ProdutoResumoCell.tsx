import { Skeleton } from 'antd'
import { useProdutoResumo } from '@shared/hooks/useProdutoResumo'

/** Célula de tabela reaproveitável — resolve `produtoId` solto (referência cross-serviço) pro código+nome do produto. */
export default function ProdutoResumoCell({ produtoId }: { produtoId: number }) {
  const { data, isLoading } = useProdutoResumo(produtoId)
  if (isLoading) return <Skeleton.Input active size="small" style={{ width: 160 }} />
  if (!data) return <>{`#${produtoId}`}</>
  return <>{`${data.codigoInterno} — ${data.nome}`}</>
}
