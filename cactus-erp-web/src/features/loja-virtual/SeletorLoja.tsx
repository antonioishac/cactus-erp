import { Select } from 'antd'
import { useTodasLojas } from './queries'

interface Props {
  value: number | undefined
  onChange: (lojaId: number | undefined) => void
  placeholder?: string
}

/** Seletor de loja (tenant) reaproveitado por Categorias/Produtos/Pedidos — este módulo é administração
 * da plataforma, não escopado pela empresa ativa da sessão como o resto do app. */
export default function SeletorLoja({ value, onChange, placeholder }: Props) {
  const { data: lojas, isLoading } = useTodasLojas()
  return (
    <Select
      allowClear
      showSearch
      loading={isLoading}
      optionFilterProp="label"
      placeholder={placeholder ?? 'Selecione a loja'}
      style={{ width: 260 }}
      value={value}
      onChange={onChange}
      options={lojas?.map((l) => ({ value: l.id, label: l.nome }))}
    />
  )
}
