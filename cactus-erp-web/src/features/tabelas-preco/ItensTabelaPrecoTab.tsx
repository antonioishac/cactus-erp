import { useState } from 'react'
import { Button, Empty, Flex, Popconfirm, Space, Table } from 'antd'
import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons'
import ItemTabelaPrecoModal from './ItemTabelaPrecoModal'
import type { TabelaPrecoProdutoDTO, TipoAjustePreco } from './types'

interface ItensTabelaPrecoTabProps {
  itens: TabelaPrecoProdutoDTO[]
  tipoAjuste: TipoAjustePreco
  onChange: (itens: TabelaPrecoProdutoDTO[]) => void
  somenteLeitura?: boolean
}

export default function ItensTabelaPrecoTab({ itens, tipoAjuste, onChange, somenteLeitura }: ItensTabelaPrecoTabProps) {
  const [modalAberto, setModalAberto] = useState(false)
  const [indiceEdicao, setIndiceEdicao] = useState<number | null>(null)

  const abrirNovo = () => {
    setIndiceEdicao(null)
    setModalAberto(true)
  }
  const abrirEdicao = (indice: number) => {
    setIndiceEdicao(indice)
    setModalAberto(true)
  }
  const salvar = (item: TabelaPrecoProdutoDTO) => {
    onChange(indiceEdicao === null ? [...itens, item] : itens.map((it, i) => (i === indiceEdicao ? item : it)))
    setModalAberto(false)
  }
  const remover = (indice: number) => onChange(itens.filter((_, i) => i !== indice))

  return (
    <Flex vertical gap={16}>
      {!somenteLeitura && (
        <Flex justify="flex-end">
          <Button type="primary" icon={<PlusOutlined />} onClick={abrirNovo}>
            Adicionar produto
          </Button>
        </Flex>
      )}

      <Table<TabelaPrecoProdutoDTO>
        rowKey={(item) => itens.indexOf(item)}
        dataSource={itens}
        pagination={false}
        locale={{ emptyText: <Empty description="Nenhum produto nesta tabela de preço" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
        columns={[
          { title: 'Código', dataIndex: 'produtoCodigoInterno', width: 120, render: (v) => v || '—' },
          { title: 'Produto', dataIndex: 'produtoNome', render: (v) => v || '—' },
          tipoAjuste === 'VALOR_FIXO'
            ? { title: 'Valor', dataIndex: 'valor', render: (v?: number) => (v != null ? `R$ ${v.toFixed(2)}` : '—') }
            : {
                title: 'Ajuste',
                dataIndex: 'percentualAjuste',
                render: (v?: number) => (v != null ? `${v > 0 ? '+' : ''}${v.toFixed(2)}%` : '—'),
              },
          ...(somenteLeitura
            ? []
            : [
                {
                  title: 'Ações',
                  key: 'acoes',
                  width: 100,
                  render: (_: unknown, __: TabelaPrecoProdutoDTO, index: number) => (
                    <Space>
                      <Button type="text" icon={<EditOutlined />} onClick={() => abrirEdicao(index)} />
                      <Popconfirm title="Remover este produto da tabela?" onConfirm={() => remover(index)}>
                        <Button type="text" danger icon={<DeleteOutlined />} />
                      </Popconfirm>
                    </Space>
                  ),
                },
              ]),
        ]}
      />

      {!somenteLeitura && (
        <ItemTabelaPrecoModal
          open={modalAberto}
          tipoAjuste={tipoAjuste}
          valorInicial={indiceEdicao !== null ? itens[indiceEdicao] : undefined}
          itensJaAdicionados={itens.map((i) => i.produtoId)}
          onCancel={() => setModalAberto(false)}
          onSalvar={salvar}
        />
      )}
    </Flex>
  )
}
