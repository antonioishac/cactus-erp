import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Input, Table, Tag, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useUsuarios } from './queries'
import type { UsuarioResponse, UsuariosFiltro } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function UsuariosListPage() {
  const navigate = useNavigate()
  const [filtro, setFiltro] = useState<UsuariosFiltro>({})
  const [pagina, setPagina] = useState(0)

  const { data, isLoading, isPlaceholderData } = useUsuarios(filtro, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Usuários
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/seguranca/usuarios/novo')}>
          Novo Usuário
        </Button>
      </Flex>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <Input.Search
            placeholder="Buscar por login ou nome"
            allowClear
            style={{ maxWidth: 320 }}
            onSearch={(valor) => {
              setFiltro({ busca: valor || undefined })
              setPagina(0)
            }}
          />
        </Flex>

        <Table<UsuarioResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(usuario) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/seguranca/usuarios/${usuario.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} usuário(s)`,
          }}
          columns={[
            { title: 'Login', dataIndex: 'login' },
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'E-mail', dataIndex: 'email', render: (v) => v || '—' },
            {
              title: 'Situação',
              key: 'situacao',
              render: (_, u) => (
                <Flex gap={4}>
                  <Tag color={u.ativo ? 'success' : 'default'}>{u.ativo ? 'Ativo' : 'Inativo'}</Tag>
                  {u.bloqueado && <Tag color="error">Bloqueado</Tag>}
                </Flex>
              ),
            },
            {
              title: 'Último login',
              dataIndex: 'dataUltimoLogin',
              render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY HH:mm') : '—'),
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
