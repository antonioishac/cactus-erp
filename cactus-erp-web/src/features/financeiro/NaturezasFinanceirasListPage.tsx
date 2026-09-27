import { useState } from 'react'
import { App, Alert, Button, Card, Flex, Popconfirm, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import NaturezaFinanceiraModal from './modals/NaturezaFinanceiraModal'
import { useInativarNaturezaFinanceira, useNaturezasFinanceiras } from './queries'
import { TIPO_NATUREZA_FINANCEIRA_LABEL } from './types'
import type { NaturezaFinanceiraResponse } from './types'

const { Title } = Typography

const COR_TIPO: Record<string, string> = { RECEITA: 'success', DESPESA: 'error' }

/** Escopado pela empresa ativa da sessão (sem Select na tela) — mesmo padrão de
 * `ConfiguracaoEstoquePage`. Sem paginação (`GET` devolve lista simples, não `Page`). */
export default function NaturezasFinanceirasListPage() {
  const { message } = App.useApp()
  const { empresaAtivaId, authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'FINANCEIRO_GERENCIAR')
  const [modalAberto, setModalAberto] = useState(false)
  const [naturezaEmEdicao, setNaturezaEmEdicao] = useState<NaturezaFinanceiraResponse | undefined>(undefined)

  const { data, isLoading } = useNaturezasFinanceiras(empresaAtivaId ?? undefined)
  const inativar = useInativarNaturezaFinanceira(empresaAtivaId ?? -1)

  async function handleInativar(id: number) {
    try {
      await inativar.mutateAsync(id)
      message.success('Natureza financeira inativada.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao inativar natureza financeira.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Naturezas Financeiras
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar naturezas financeiras'}>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            disabled={!podeGerenciar}
            onClick={() => {
              setNaturezaEmEdicao(undefined)
              setModalAberto(true)
            }}
          >
            Nova Natureza
          </Button>
        </Tooltip>
      </Flex>

      <Alert
        type="info"
        showIcon
        title="Vale pra empresa ativa da sessão"
        description="Categoriza um título como Receita ou Despesa — troque de empresa no cabeçalho pra gerenciar outra filial."
      />

      <Card>
        <Table<NaturezaFinanceiraResponse>
          rowKey="id"
          loading={isLoading}
          dataSource={data ?? []}
          pagination={false}
          columns={[
            { title: 'Código', dataIndex: 'codigo' },
            { title: 'Descrição', dataIndex: 'descricao' },
            {
              title: 'Tipo',
              dataIndex: 'tipo',
              render: (tipo: keyof typeof TIPO_NATUREZA_FINANCEIRA_LABEL) => (
                <Tag color={COR_TIPO[tipo]}>{TIPO_NATUREZA_FINANCEIRA_LABEL[tipo]}</Tag>
              ),
            },
            ...(podeGerenciar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    width: 200,
                    render: (_: unknown, natureza: NaturezaFinanceiraResponse) => (
                      <Space>
                        <Button
                          size="small"
                          onClick={() => {
                            setNaturezaEmEdicao(natureza)
                            setModalAberto(true)
                          }}
                        >
                          Editar
                        </Button>
                        <Popconfirm title="Inativar esta natureza financeira?" onConfirm={() => handleInativar(natureza.id)}>
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

      <NaturezaFinanceiraModal
        open={modalAberto}
        empresaId={empresaAtivaId ?? -1}
        valorInicial={naturezaEmEdicao}
        onClose={() => setModalAberto(false)}
      />
    </Flex>
  )
}
