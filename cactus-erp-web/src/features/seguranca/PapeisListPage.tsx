import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Input, Table, Tag, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import SituacaoTag from '@shared/ui/SituacaoTag'
import { usePapeis } from './queries'
import type { PapelResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function PapeisListPage() {
  const navigate = useNavigate()
  const [nome, setNome] = useState<string | undefined>(undefined)
  const [pagina, setPagina] = useState(0)

  const { data, isLoading, isPlaceholderData } = usePapeis(nome, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Papéis
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/seguranca/papeis/novo')}>
          Novo Papel
        </Button>
      </Flex>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <Input.Search
            placeholder="Buscar por nome"
            allowClear
            style={{ maxWidth: 320 }}
            onSearch={(valor) => {
              setNome(valor || undefined)
              setPagina(0)
            }}
          />
        </Flex>

        <Table<PapelResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(papel) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/seguranca/papeis/${papel.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} papel(éis)`,
          }}
          columns={[
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Descrição', dataIndex: 'descricao', render: (v) => v || '—' },
            {
              title: 'Funcionalidades',
              key: 'funcionalidades',
              render: (_, p) => <Tag>{p.funcionalidades.length}</Tag>,
            },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (situacao) => <SituacaoTag situacao={situacao} />,
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
