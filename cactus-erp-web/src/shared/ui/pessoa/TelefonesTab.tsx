import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons'
import { Button, Empty, Flex, Popconfirm, Space, Table, Tag } from 'antd'
import TelefoneModal from './TelefoneModal'
import { useEditableList } from '@shared/hooks/useEditableList'
import { TIPO_TELEFONE_LABEL } from '@shared/domain/pessoaUtils'
import type { TelefoneDTO } from '@shared/domain/pessoa'

interface TelefonesTabProps {
  telefones: TelefoneDTO[]
  onChange: (telefones: TelefoneDTO[]) => void
  somenteLeitura?: boolean
}

export default function TelefonesTab({ telefones, onChange, somenteLeitura }: TelefonesTabProps) {
  const lista = useEditableList(telefones, onChange)

  return (
    <Flex vertical gap={16}>
      {!somenteLeitura && (
        <Flex justify="flex-end">
          <Button type="primary" icon={<PlusOutlined />} onClick={lista.abrirNovo}>
            Adicionar telefone
          </Button>
        </Flex>
      )}

      <Table<TelefoneDTO>
        rowKey={(telefone) => telefones.indexOf(telefone)}
        dataSource={telefones}
        pagination={false}
        locale={{ emptyText: <Empty description="Nenhum telefone cadastrado" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
        columns={[
          { title: 'Tipo', dataIndex: 'tipoTelefone', render: (v) => TIPO_TELEFONE_LABEL[v] ?? v },
          { title: 'Número', dataIndex: 'numero' },
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
                  render: (_: unknown, __: TelefoneDTO, index: number) => (
                    <Space>
                      <Button type="text" icon={<EditOutlined />} onClick={() => lista.abrirEdicao(index)} />
                      <Popconfirm title="Remover este telefone?" onConfirm={() => lista.remover(index)}>
                        <Button type="text" danger icon={<DeleteOutlined />} />
                      </Popconfirm>
                    </Space>
                  ),
                },
              ]),
        ]}
      />

      {!somenteLeitura && (
        <TelefoneModal
          open={lista.modalAberto}
          valorInicial={lista.valorEdicao}
          onCancel={lista.fechar}
          onSalvar={lista.salvar}
        />
      )}
    </Flex>
  )
}
