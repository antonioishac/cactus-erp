import { useEffect, useState } from 'react'
import { Form, InputNumber, Modal } from 'antd'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useBuscaProdutosParaSelecao } from './queries'
import type { TabelaPrecoProdutoDTO, TipoAjustePreco } from './types'

interface ItemTabelaPrecoModalProps {
  open: boolean
  tipoAjuste: TipoAjustePreco
  valorInicial?: TabelaPrecoProdutoDTO
  itensJaAdicionados: number[]
  onCancel: () => void
  onSalvar: (item: TabelaPrecoProdutoDTO) => void
}

/**
 * Um item = um produto + seu ajuste de preço nesta tabela. O campo de ajuste
 * exibido (`valor` ou `percentualAjuste`) depende de `tipoAjuste`, que é
 * escolhido no cabeçalho da tabela (`TabelaPrecoFormPage`), não por item —
 * todos os itens de uma mesma tabela usam a mesma forma de ajuste.
 */
export default function ItemTabelaPrecoModal({
  open,
  tipoAjuste,
  valorInicial,
  itensJaAdicionados,
  onCancel,
  onSalvar,
}: ItemTabelaPrecoModalProps) {
  const [form] = Form.useForm<TabelaPrecoProdutoDTO>()
  const [termoProduto, setTermoProduto] = useState<string | undefined>(undefined)
  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termoProduto)

  /* oxlint-disable react-hooks/set-state-in-effect -- reabrir o modal (novo item ou edição) precisa resetar o termo de busca junto da hidratação do form */
  useEffect(() => {
    if (open) {
      form.setFieldsValue(valorInicial ?? {})
      setTermoProduto(undefined)
    }
  }, [open, valorInicial, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  const handleOk = () => {
    form.validateFields().then((valores) => {
      // produtoNome/produtoCodigoInterno normalmente só vêm preenchidos na
      // Response do backend (ver Javadoc de TabelaPrecoProdutoDTO) — sem
      // resolver aqui, a tabela de itens ficaria mostrando "—" pro produto
      // recém-adicionado até a tabela ser salva e recarregada (achado real
      // ao vivo, 2026-09-01). O backend ignora esses 2 campos na escrita,
      // então enviá-los não tem efeito colateral.
      const produtoSelecionado = produtos?.find((p) => p.id === valores.produtoId)
      onSalvar({
        ...valores,
        produtoNome: produtoSelecionado?.nome ?? valorInicial?.produtoNome,
        produtoCodigoInterno: produtoSelecionado?.codigoInterno ?? valorInicial?.produtoCodigoInterno,
      })
      form.resetFields()
    })
  }

  const idsBloqueados = itensJaAdicionados.filter((id) => id !== valorInicial?.produtoId)

  return (
    <Modal
      title={valorInicial ? 'Editar item' : 'Adicionar produto'}
      open={open}
      onCancel={onCancel}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      destroyOnHidden
    >
      <Form form={form} name="itemTabelaPrecoForm" layout="vertical">
        <Form.Item
          name="produtoId"
          label="Produto"
          rules={[
            { required: true, message: 'Selecione o produto' },
            {
              validator: (_, value) =>
                value != null && idsBloqueados.includes(value)
                  ? Promise.reject(new Error('Este produto já está nesta tabela'))
                  : Promise.resolve(),
            },
          ]}
        >
          <AsyncSelect
            disabled={!!valorInicial}
            opcoes={produtos}
            carregando={carregandoProdutos}
            termo={termoProduto}
            onTermoChange={setTermoProduto}
            toOption={(p) => ({ value: p.id, label: `${p.codigoInterno} — ${p.nome}` })}
            labelValorAtual={valorInicial ? `${valorInicial.produtoCodigoInterno ?? ''} — ${valorInicial.produtoNome ?? ''}` : undefined}
          />
        </Form.Item>

        {tipoAjuste === 'VALOR_FIXO' ? (
          <Form.Item name="valor" label="Valor fixo nesta tabela (R$)" rules={[{ required: true, message: 'Informe o valor' }]}>
            <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
          </Form.Item>
        ) : (
          <Form.Item
            name="percentualAjuste"
            label="Percentual de ajuste (%)"
            tooltip="Negativo = desconto, positivo = acréscimo sobre o valor de venda do produto."
            rules={[{ required: true, message: 'Informe o percentual' }]}
          >
            <InputNumber<number> style={{ width: '100%' }} precision={2} decimalSeparator="," />
          </Form.Item>
        )}
      </Form>
    </Modal>
  )
}
