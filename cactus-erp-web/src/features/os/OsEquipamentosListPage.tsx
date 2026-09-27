import { useState } from 'react'
import { App, Button, Card, Flex, Popconfirm, Space, Table, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import SituacaoTag from '@shared/ui/SituacaoTag'
import OsEquipamentoModal from './modals/OsEquipamentoModal'
import { useExcluirOsEquipamento, useOsEquipamentosPaginado } from './queries'
import type { OsEquipamentoResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function OsEquipamentosListPage() {
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'OS_GERENCIAR')
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)
  const [equipamentoEmEdicao, setEquipamentoEmEdicao] = useState<OsEquipamentoResponse | undefined>(undefined)

  const { data, isLoading, isPlaceholderData } = useOsEquipamentosPaginado(pagina, TAMANHO_PAGINA)
  const excluir = useExcluirOsEquipamento()

  async function handleExcluir(id: number) {
    try {
      await excluir.mutateAsync(id)
      message.success('Tipo de equipamento excluído.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir tipo de equipamento.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Tipos de Equipamento
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar tipos de equipamento'}>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            disabled={!podeGerenciar}
            onClick={() => {
              setEquipamentoEmEdicao(undefined)
              setModalAberto(true)
            }}
          >
            Novo Tipo
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Table<OsEquipamentoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} tipo(s) de equipamento`,
          }}
          columns={[
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Descrição', dataIndex: 'descricao', render: (v) => v || '—' },
            { title: 'Situação', dataIndex: 'situacao', render: (situacao) => <SituacaoTag situacao={situacao} /> },
            ...(podeGerenciar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    width: 160,
                    render: (_: unknown, equipamento: OsEquipamentoResponse) => (
                      <Space>
                        <Button
                          size="small"
                          onClick={() => {
                            setEquipamentoEmEdicao(equipamento)
                            setModalAberto(true)
                          }}
                        >
                          Editar
                        </Button>
                        <Popconfirm title="Excluir este tipo de equipamento?" onConfirm={() => handleExcluir(equipamento.id)}>
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

      <OsEquipamentoModal open={modalAberto} valorInicial={equipamentoEmEdicao} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
