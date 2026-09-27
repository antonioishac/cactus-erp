import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons'
import { Button, Empty, Flex, Popconfirm, Space, Table, Tag } from 'antd'
import ContatoModal from './ContatoModal'
import { useEditableList } from '@shared/hooks/useEditableList'
import { TIPO_CONTATO_LABEL } from '@shared/domain/pessoaUtils'
import type { ContatoDTO } from '@shared/domain/pessoa'

interface ContatosTabProps {
  contatos: ContatoDTO[]
  onChange: (contatos: ContatoDTO[]) => void
  somenteLeitura?: boolean
}

export default function ContatosTab({ contatos, onChange, somenteLeitura }: ContatosTabProps) {
  const lista = useEditableList(contatos, onChange)

  return (
    <Flex vertical gap={16}>
      {!somenteLeitura && (
        <Flex justify="flex-end">
          <Button type="primary" icon={<PlusOutlined />} onClick={lista.abrirNovo}>
            Adicionar contato
          </Button>
        </Flex>
      )}

      <Table<ContatoDTO>
        rowKey={(contato) => contatos.indexOf(contato)}
        dataSource={contatos}
        pagination={false}
        locale={{ emptyText: <Empty description="Nenhum contato cadastrado" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
        columns={[
          { title: 'Tipo', dataIndex: 'tipoContato', render: (v) => TIPO_CONTATO_LABEL[v] ?? v },
          { title: 'Valor', dataIndex: 'valor' },
          {
            title: 'Principal',
            dataIndex: 'principal',
            width: 100,
            render: (v) => (v ? <Tag color="success">Principal</Tag> : null),
          },
          ...(somenteLeitura
            ? []
            : [
                {
                  title: 'Ações',
                  key: 'acoes',
                  width: 100,
                  render: (_: unknown, __: ContatoDTO, index: number) => (
                    <Space>
                      <Button type="text" icon={<EditOutlined />} onClick={() => lista.abrirEdicao(index)} />
                      <Popconfirm title="Remover este contato?" onConfirm={() => lista.remover(index)}>
                        <Button type="text" danger icon={<DeleteOutlined />} />
                      </Popconfirm>
                    </Space>
                  ),
                },
              ]),
        ]}
      />

      {!somenteLeitura && (
        <ContatoModal
          open={lista.modalAberto}
          valorInicial={lista.valorEdicao}
          onCancel={lista.fechar}
          onSalvar={lista.salvar}
        />
      )}
    </Flex>
  )
}
