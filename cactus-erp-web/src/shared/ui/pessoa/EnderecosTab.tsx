import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons'
import { Button, Empty, Flex, Popconfirm, Space, Table, Tag } from 'antd'
import EnderecoModal from './EnderecoModal'
import { useEditableList } from '@shared/hooks/useEditableList'
import type { EnderecoDTO } from '@shared/domain/pessoa'

interface EnderecosTabProps {
  enderecos: EnderecoDTO[]
  onChange: (enderecos: EnderecoDTO[]) => void
  somenteLeitura?: boolean
}

export default function EnderecosTab({ enderecos, onChange, somenteLeitura }: EnderecosTabProps) {
  const lista = useEditableList(enderecos, onChange)

  return (
    <Flex vertical gap={16}>
      {!somenteLeitura && (
        <Flex justify="flex-end">
          <Button type="primary" icon={<PlusOutlined />} onClick={lista.abrirNovo}>
            Adicionar endereço
          </Button>
        </Flex>
      )}

      <Table<EnderecoDTO>
        rowKey={(endereco) => enderecos.indexOf(endereco)}
        dataSource={enderecos}
        pagination={false}
        locale={{ emptyText: <Empty description="Nenhum endereço cadastrado" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
        columns={[
          {
            title: 'Endereço',
            key: 'logradouro',
            render: (_, e) => [e.logradouro, e.numero].filter(Boolean).join(', ') || '—',
          },
          { title: 'Bairro', dataIndex: 'bairro', render: (v) => v || '—' },
          {
            title: 'Cidade/UF',
            key: 'cidade',
            render: (_, e) => (e.municipio ? `${e.municipio}/${e.uf ?? ''}` : '—'),
          },
          { title: 'CEP', dataIndex: 'cep', render: (v) => v || '—' },
          {
            title: 'Papel',
            key: 'papel',
            render: (_, e) => (
              <Space size={4} wrap>
                {e.principal && <Tag color="success">Principal</Tag>}
                {e.entrega && <Tag color="blue">Entrega</Tag>}
                {e.cobranca && <Tag color="gold">Cobrança</Tag>}
                {e.correspondencia && <Tag>Correspondência</Tag>}
              </Space>
            ),
          },
          ...(somenteLeitura
            ? []
            : [
                {
                  title: 'Ações',
                  key: 'acoes',
                  width: 100,
                  render: (_: unknown, __: EnderecoDTO, index: number) => (
                    <Space>
                      <Button type="text" icon={<EditOutlined />} onClick={() => lista.abrirEdicao(index)} />
                      <Popconfirm title="Remover este endereço?" onConfirm={() => lista.remover(index)}>
                        <Button type="text" danger icon={<DeleteOutlined />} />
                      </Popconfirm>
                    </Space>
                  ),
                },
              ]),
        ]}
      />

      {!somenteLeitura && (
        <EnderecoModal
          open={lista.modalAberto}
          valorInicial={lista.valorEdicao}
          onCancel={lista.fechar}
          onSalvar={lista.salvar}
        />
      )}
    </Flex>
  )
}
