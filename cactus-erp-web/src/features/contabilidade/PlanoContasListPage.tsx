import { useState } from 'react'
import { App, Button, Card, Flex, Popconfirm, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import SituacaoTag from '@shared/ui/SituacaoTag'
import PlanoContaModal from './modals/PlanoContaModal'
import { useExcluirPlanoConta, usePlanoContas } from './queries'
import { NATUREZA_CONTA_LABEL, TIPO_CONTA_CONTABIL_LABEL } from './types'
import type { PlanoContaResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function PlanoContasListPage() {
  const { message } = App.useApp()
  const { empresaAtivaId, authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'CONTABIL_GERENCIAR')
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)
  const [contaEmEdicao, setContaEmEdicao] = useState<PlanoContaResponse | undefined>(undefined)

  const { data, isLoading, isPlaceholderData } = usePlanoContas(empresaAtivaId ?? -1, pagina, TAMANHO_PAGINA)
  const excluir = useExcluirPlanoConta()

  async function handleExcluir(id: number) {
    try {
      await excluir.mutateAsync(id)
      message.success('Conta excluída.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir conta.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Plano de Contas
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar o plano de contas'}>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            disabled={!podeGerenciar}
            onClick={() => {
              setContaEmEdicao(undefined)
              setModalAberto(true)
            }}
          >
            Nova Conta
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Table<PlanoContaResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} conta(s)`,
          }}
          columns={[
            { title: 'Código', dataIndex: 'codigo' },
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Conta pai', dataIndex: 'contaPaiNome', render: (v) => v || '— (raiz)' },
            { title: 'Tipo', dataIndex: 'tipo', render: (v: keyof typeof TIPO_CONTA_CONTABIL_LABEL) => TIPO_CONTA_CONTABIL_LABEL[v] },
            { title: 'Natureza', dataIndex: 'natureza', render: (v: keyof typeof NATUREZA_CONTA_LABEL) => NATUREZA_CONTA_LABEL[v] },
            {
              title: 'Analítica',
              dataIndex: 'contaAnalitica',
              render: (v: boolean) => <Tag color={v ? 'blue' : 'default'}>{v ? 'Analítica' : 'Sintética'}</Tag>,
            },
            { title: 'Situação', dataIndex: 'situacao', render: (v) => <SituacaoTag situacao={v} /> },
            ...(podeGerenciar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    width: 160,
                    render: (_: unknown, conta: PlanoContaResponse) => (
                      <Space>
                        <Button
                          size="small"
                          onClick={() => {
                            setContaEmEdicao(conta)
                            setModalAberto(true)
                          }}
                        >
                          Editar
                        </Button>
                        <Popconfirm title="Excluir esta conta?" onConfirm={() => handleExcluir(conta.id)}>
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

      <PlanoContaModal
        open={modalAberto}
        empresaId={empresaAtivaId ?? -1}
        valorInicial={contaEmEdicao}
        onClose={() => setModalAberto(false)}
      />
    </Flex>
  )
}
