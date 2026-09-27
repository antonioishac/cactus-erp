import { useState } from 'react'
import { App, Button, Card, Flex, Popconfirm, Space, Table, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import SituacaoTag from '@shared/ui/SituacaoTag'
import OsStatusModal from './modals/OsStatusModal'
import { useExcluirOsStatus, useOsStatusPaginado } from './queries'
import type { OsStatusResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function OsStatusListPage() {
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'OS_GERENCIAR')
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)
  const [statusEmEdicao, setStatusEmEdicao] = useState<OsStatusResponse | undefined>(undefined)

  const { data, isLoading, isPlaceholderData } = useOsStatusPaginado(pagina, TAMANHO_PAGINA)
  const excluir = useExcluirOsStatus()

  async function handleExcluir(id: number) {
    try {
      await excluir.mutateAsync(id)
      message.success('Status excluído.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir status.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Status de OS
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar status de OS'}>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            disabled={!podeGerenciar}
            onClick={() => {
              setStatusEmEdicao(undefined)
              setModalAberto(true)
            }}
          >
            Novo Status
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Table<OsStatusResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} status`,
          }}
          columns={[
            { title: 'Código', dataIndex: 'codigo', render: (v) => v || '—' },
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Situação', dataIndex: 'situacao', render: (situacao) => <SituacaoTag situacao={situacao} /> },
            ...(podeGerenciar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    width: 160,
                    render: (_: unknown, status: OsStatusResponse) => (
                      <Space>
                        <Button
                          size="small"
                          onClick={() => {
                            setStatusEmEdicao(status)
                            setModalAberto(true)
                          }}
                        >
                          Editar
                        </Button>
                        <Popconfirm title="Excluir este status?" onConfirm={() => handleExcluir(status.id)}>
                          <Button size="small" danger>
                            Excluir
                          </Button>
                        </Popconfirm>
                      </Space>
                    ),
                  },
                ]
              : []),
          ]}
        />
      </Card>

      <OsStatusModal open={modalAberto} valorInicial={statusEmEdicao} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
