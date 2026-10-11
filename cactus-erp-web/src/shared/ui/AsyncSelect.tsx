import { useMemo, useState } from 'react'
import { Select, Spin } from 'antd'
import type { SelectProps } from 'antd'

interface AsyncSelectProps<T> extends Omit<SelectProps, 'options' | 'onSearch' | 'filterOption'> {
  /** Resultado já buscado (ex: `useBuscaNcm(termo)`) — este componente não faz fetch sozinho, só controla o termo digitado. */
  opcoes: T[] | undefined
  carregando: boolean
  termo: string | undefined
  onTermoChange: (termo: string | undefined) => void
  toOption: (item: T) => { value: number; label: string }
  /** Rótulo já resolvido pelo backend pro valor atual (ex: `ncmCodigo`) — evita um Select vazio antes da 1ª busca, em modo edição. */
  labelValorAtual?: string
}

/**
 * Select com busca assíncrona server-side (debounce simples via `onSearch`) —
 * usado pra catálogos grandes demais pra carregar inteiro num Select comum
 * (NCM tem ~10.000 códigos oficiais). Diferente do padrão "carrega 200 e
 * cacheia" usado em Cargo/Setor/Contador/Produto-Grupo etc. (catálogos
 * pequenos, por empresa) — aqui cada tecla dispara uma busca nova (`enabled`
 * da query já exige 2+ caracteres, ver `useBuscaNcm`/`useBuscaCest`).
 */
export default function AsyncSelect<T>({
  opcoes,
  carregando,
  termo,
  onTermoChange,
  toOption,
  labelValorAtual,
  value,
  placeholder,
  ...rest
}: AsyncSelectProps<T>) {
  const [digitando, setDigitando] = useState(false)

  const options = useMemo(() => {
    const base = (opcoes ?? []).map(toOption)
    // Garante que o valor selecionado apareça como opção mesmo antes de
    // qualquer busca (ex: abrir um produto existente já com NCM resolvido).
    if (value != null && labelValorAtual && !base.some((o) => o.value === value)) {
      return [{ value: value as number, label: labelValorAtual }, ...base]
    }
    return base
  }, [opcoes, toOption, value, labelValorAtual])

  return (
    <Select
      {...rest}
      value={value}
      showSearch
      filterOption={false}
      notFoundContent={digitando && carregando ? <Spin size="small" /> : null}
      onSearch={(texto) => {
        setDigitando(true)
        onTermoChange(texto || undefined)
      }}
      onBlur={() => setDigitando(false)}
      options={options}
      loading={carregando}
      // `placeholder` do chamador (ex: "Todos os clientes" num filtro) tem prioridade;
      // sem ele, mantém a dica padrão de busca.
      placeholder={termo === undefined ? (placeholder ?? 'Digite para buscar…') : undefined}
    />
  )
}
