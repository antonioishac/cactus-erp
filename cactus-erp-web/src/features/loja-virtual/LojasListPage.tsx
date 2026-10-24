import { useState } from 'react'
import { App, Button, Card, Flex, Popconfirm, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import SituacaoTag from '@shared/ui/SituacaoTag'
import LojaModal from './modals/LojaModal'
import { useCancelarAssinaturaLoja, useLojas, useReativarAssinaturaLoja } from './queries'
import { PLANO_ASSINATURA_LABEL, SITUACAO_ASSINATURA_LABEL } from './types'
import type { LojaResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_ASSINATURA: Record<string, string> = { ATIVA: 'success', INADIMPLENTE: 'gold', CANCELADA: 'default' }

export default function LojasListPage() {
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'LOJA_GERENCIAR')
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)
  const [lojaEmEdicao, setLojaEmEdicao] = useState<LojaResponse | undefined>(undefined)

  const { data, isLoading, isPlaceholderData } = useLojas(pagina, TAMANHO_PAGINA)
  const cancelarAssinatura = useCancelarAssinaturaLoja()
  const reativarAssinatura = useReativarAssinaturaLoja()

  async function handleCancelarAssinatura(id: number) {
    try {
      await cancelarAssinatura.mutateAsync(id)
      message.success('Assinatura cancelada.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao cancelar assinatura.')
    }
  }

  async function handleReativarAssinatura(id: number) {
    try {
      await reativarAssinatura.mutateAsync(id)
      message.success('Assinatura reativada.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao reativar assinatura.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Lojas
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar lojas'}>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            disabled={!podeGerenciar}
            onClick={() => {
              setLojaEmEdicao(undefined)
              setModalAberto(true)
            }}
          >
            Nova Loja
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Table<LojaResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} loja(s)`,
          }}
          columns={[
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Subdomínio', dataIndex: 'subdominio' },
            {
              title: 'Plano',
              dataIndex: 'planoAssinatura',
              render: (v: keyof typeof PLANO_ASSINATURA_LABEL) => PLANO_ASSINATURA_LABEL[v],
            },
            {
              title: 'Assinatura',
              dataIndex: 'situacaoAssinatura',
              render: (v: keyof typeof SITUACAO_ASSINATURA_LABEL) => (
                <Tag color={COR_ASSINATURA[v]}>{SITUACAO_ASSINATURA_LABEL[v]}</Tag>
              ),
            },
            { title: 'Situação', dataIndex: 'situacao', render: (v) => <SituacaoTag situacao={v} /> },
            ...(podeGerenciar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    width: 280,
                    render: (_: unknown, loja: LojaResponse) => (
                      <Space>
                        <Button
                          size="small"
                          onClick={() => {
                            setLojaEmEdicao(loja)
                            setModalAberto(true)
                          }}
                        >
                          Editar
                        </Button>
                        {loja.situacaoAssinatura === 'CANCELADA' ? (
                          <Popconfirm title="Reativar esta assinatura?" onConfirm={() => handleReativarAssinatura(loja.id)}>
                            <Button size="small">Reativar</Button>
                          </Popconfirm>
                        ) : (
                          <Popconfirm title="Cancelar esta assinatura?" onConfirm={() => handleCancelarAssinatura(loja.id)}>
                            <Button size="small" danger>
                              Cancelar assinatura
                            </Button>
                          </Popconfirm>
                        )}
                      </Space>
                    ),
                  },
                ]
              : []),
          ]}
        />
      </Card>

      <LojaModal open={modalAberto} valorInicial={lojaEmEdicao} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
