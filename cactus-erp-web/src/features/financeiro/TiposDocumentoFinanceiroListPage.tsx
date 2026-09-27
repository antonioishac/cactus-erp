import { useState } from 'react'
import { App, Button, Card, Flex, Popconfirm, Space, Table, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import TipoDocumentoFinanceiroModal from './modals/TipoDocumentoFinanceiroModal'
import { useInativarTipoDocumentoFinanceiro, useTiposDocumentoFinanceiro } from './queries'
import type { TipoDocumentoFinanceiroResponse } from './types'

const { Title } = Typography

/** Catálogo GLOBAL (não por empresa) — sem paginação (`GET` devolve lista simples). */
export default function TiposDocumentoFinanceiroListPage() {
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'FINANCEIRO_GERENCIAR')
  const [modalAberto, setModalAberto] = useState(false)

  const { data, isLoading } = useTiposDocumentoFinanceiro()
  const inativar = useInativarTipoDocumentoFinanceiro()

  async function handleInativar(id: number) {
    try {
      await inativar.mutateAsync(id)
      message.success('Tipo de documento inativado.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao inativar tipo de documento.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Tipos de Documento Financeiro
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar tipos de documento'}>
          <Button type="primary" icon={<PlusOutlined />} disabled={!podeGerenciar} onClick={() => setModalAberto(true)}>
            Novo Tipo
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Table<TipoDocumentoFinanceiroResponse>
          rowKey="id"
          loading={isLoading}
          dataSource={data ?? []}
          pagination={false}
          columns={[
            { title: 'Código', dataIndex: 'codigo' },
            { title: 'Descrição', dataIndex: 'descricao' },
            ...(podeGerenciar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    width: 120,
                    render: (_: unknown, tipo: TipoDocumentoFinanceiroResponse) => (
                      <Space>
                        <Popconfirm title="Inativar este tipo de documento?" onConfirm={() => handleInativar(tipo.id)}>
                          <Button size="small" danger>
                            Inativar
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

      <TipoDocumentoFinanceiroModal open={modalAberto} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
