import { useState } from 'react'
import { Button, Empty, Flex, Popconfirm, Space, Table, Tag } from 'antd'
import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import FaixaPrecoModal from '../modals/FaixaPrecoModal'
import type { ProdutoFaixaPrecoDTO } from '../types'

interface FaixasPrecoTabProps {
  faixas: ProdutoFaixaPrecoDTO[]
  onChange: (faixas: ProdutoFaixaPrecoDTO[]) => void
  somenteLeitura?: boolean
}

/**
 * Lista de `ProdutoFaixaPreco` — o mecanismo de "promoção por quantidade"
 * pedido explicitamente pro cadastro de Produto. Não usa `useEditableList`
 * (esse hook exige `principal: boolean`, que faixa de preço não tem) — estado
 * de índice em edição fica local aqui mesmo, mesmo espírito do hook
 * compartilhado, só sem o dedup de "principal".
 */
export default function FaixasPrecoTab({ faixas, onChange, somenteLeitura }: FaixasPrecoTabProps) {
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
  const salvar = (faixa: ProdutoFaixaPrecoDTO) => {
    onChange(indiceEdicao === null ? [...faixas, faixa] : faixas.map((f, i) => (i === indiceEdicao ? faixa : f)))
    setModalAberto(false)
  }
  const remover = (indice: number) => onChange(faixas.filter((_, i) => i !== indice))

  return (
    <Flex vertical gap={16}>
      {!somenteLeitura && (
        <Flex justify="flex-end">
          <Button type="primary" icon={<PlusOutlined />} onClick={abrirNovo}>
            Adicionar faixa de preço
          </Button>
        </Flex>
      )}

      <Table<ProdutoFaixaPrecoDTO>
        rowKey={(faixa) => faixas.indexOf(faixa)}
        dataSource={faixas}
        pagination={false}
        locale={{
          emptyText: (
            <Empty
              description="Nenhuma faixa de preço cadastrada — produto vendido só pelo valor de venda padrão"
              image={Empty.PRESENTED_IMAGE_SIMPLE}
            />
          ),
        }}
        columns={[
          { title: 'Qtd. mínima', dataIndex: 'quantidadeMinima', width: 110 },
          {
            title: 'Valor unitário',
            dataIndex: 'valorUnitario',
            render: (v: number) => `R$ ${v.toFixed(2)}`,
          },
          { title: 'Rótulo', dataIndex: 'rotulo', render: (v) => v || '—' },
          {
            title: 'Vigência',
            key: 'vigencia',
            render: (_, f) =>
              f.dataInicio || f.dataFim ? (
                <Tag color="gold">
                  {f.dataInicio ? dayjs(f.dataInicio).format('DD/MM/YYYY') : '—'} até{' '}
                  {f.dataFim ? dayjs(f.dataFim).format('DD/MM/YYYY') : '—'}
                </Tag>
              ) : (
                <Tag>Sempre vale</Tag>
              ),
          },
          {
            title: 'Situação',
            dataIndex: 'situacao',
            render: (v) => <Tag color={v === 'ATIVO' ? 'success' : 'default'}>{v === 'ATIVO' ? 'Ativo' : 'Inativo'}</Tag>,
          },
          ...(somenteLeitura
            ? []
            : [
                {
                  title: 'Ações',
                  key: 'acoes',
                  width: 100,
                  render: (_: unknown, __: ProdutoFaixaPrecoDTO, index: number) => (
                    <Space>
                      <Button type="text" icon={<EditOutlined />} onClick={() => abrirEdicao(index)} />
                      <Popconfirm title="Remover esta faixa de preço?" onConfirm={() => remover(index)}>
                        <Button type="text" danger icon={<DeleteOutlined />} />
                      </Popconfirm>
                    </Space>
                  ),
                },
              ]),
        ]}
      />

      {!somenteLeitura && (
        <FaixaPrecoModal
          open={modalAberto}
          valorInicial={indiceEdicao !== null ? faixas[indiceEdicao] : undefined}
          onCancel={() => setModalAberto(false)}
          onSalvar={salvar}
        />
      )}
    </Flex>
  )
}
