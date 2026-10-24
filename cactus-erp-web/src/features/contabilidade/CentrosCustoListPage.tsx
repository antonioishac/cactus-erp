import { useState } from 'react'
import { App, Button, Card, Flex, Popconfirm, Space, Table, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import SituacaoTag from '@shared/ui/SituacaoTag'
import CentroCustoModal from './modals/CentroCustoModal'
import { useCentrosCusto, useExcluirCentroCusto } from './queries'
import type { CentroCustoResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function CentrosCustoListPage() {
  const { message } = App.useApp()
  const { empresaAtivaId, authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'CONTABIL_GERENCIAR')
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)
  const [centroEmEdicao, setCentroEmEdicao] = useState<CentroCustoResponse | undefined>(undefined)

  const { data, isLoading, isPlaceholderData } = useCentrosCusto(empresaAtivaId ?? -1, pagina, TAMANHO_PAGINA)
  const excluir = useExcluirCentroCusto()

  async function handleExcluir(id: number) {
    try {
      await excluir.mutateAsync(id)
      message.success('Centro de custo excluído.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir centro de custo.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Centros de Custo
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar centros de custo'}>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            disabled={!podeGerenciar}
            onClick={() => {
              setCentroEmEdicao(undefined)
              setModalAberto(true)
            }}
          >
            Novo Centro de Custo
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Table<CentroCustoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} centro(s) de custo`,
          }}
          columns={[
            { title: 'Código', dataIndex: 'codigo' },
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Situação', dataIndex: 'situacao', render: (v) => <SituacaoTag situacao={v} /> },
            ...(podeGerenciar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    width: 160,
                    render: (_: unknown, centro: CentroCustoResponse) => (
                      <Space>
                        <Button
                          size="small"
                          onClick={() => {
                            setCentroEmEdicao(centro)
                            setModalAberto(true)
                          }}
                        >
                          Editar
                        </Button>
                        <Popconfirm title="Excluir este centro de custo?" onConfirm={() => handleExcluir(centro.id)}>
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

      <CentroCustoModal
        open={modalAberto}
        empresaId={empresaAtivaId ?? -1}
        valorInicial={centroEmEdicao}
        onClose={() => setModalAberto(false)}
      />
    </Flex>
  )
}
