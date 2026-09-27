import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { App, Button, Card, Flex, Popconfirm, Space, Table, Tag, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useContasBancarias, useInativarContaBancaria, useReativarContaBancaria } from './queries'
import { TIPO_CONTA_BANCARIA_LABEL } from './types'
import type { ContaBancariaResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function ContasBancariasListPage() {
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { empresaAtivaId, authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'CAIXA_GERENCIAR')
  const [pagina, setPagina] = useState(0)
  const { data, isLoading, isPlaceholderData } = useContasBancarias(empresaAtivaId ?? undefined, pagina, TAMANHO_PAGINA)
  const inativar = useInativarContaBancaria()
  const reativar = useReativarContaBancaria()

  async function handleInativar(id: number) {
    try {
      await inativar.mutateAsync(id)
      message.success('Conta bancária inativada.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao inativar conta bancária.')
    }
  }

  async function handleReativar(id: number) {
    try {
      await reativar.mutateAsync(id)
      message.success('Conta bancária reativada.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao reativar conta bancária.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Contas Bancárias
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/financeiro/contas-bancarias/novo')}>
          Nova Conta
        </Button>
      </Flex>

      <Card>
        <Table<ContaBancariaResponse>
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
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Tipo', dataIndex: 'tipo', render: (v: keyof typeof TIPO_CONTA_BANCARIA_LABEL) => TIPO_CONTA_BANCARIA_LABEL[v] },
            { title: 'Agência/Número', key: 'agenciaNumero', render: (_, c) => [c.agencia, c.numero].filter(Boolean).join(' / ') || '—' },
            { title: 'Saldo atual', dataIndex: 'saldoAtual', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Situação', dataIndex: 'ativo', render: (v: boolean) => <Tag color={v ? 'success' : 'default'}>{v ? 'Ativa' : 'Inativa'}</Tag> },
            {
              title: 'Ações',
              key: 'acoes',
              width: 220,
              render: (_, conta) => (
                <Space>
                  <Button size="small" onClick={() => navigate(`/financeiro/contas-bancarias/${conta.id}`)}>
                    Editar
                  </Button>
                  {podeGerenciar &&
                    (conta.ativo ? (
                      <Popconfirm title="Inativar esta conta bancária?" onConfirm={() => handleInativar(conta.id)}>
                        <Button size="small" danger>
                          Inativar
                        </Button>
                      </Popconfirm>
                    ) : (
                      <Button size="small" onClick={() => handleReativar(conta.id)}>
                        Reativar
                      </Button>
                    ))}
                </Space>
              ),
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
