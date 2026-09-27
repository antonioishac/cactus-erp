import { useState } from 'react'
import { Button, Card, Flex, Select, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import NovaAplicacaoModal from './modals/NovaAplicacaoModal'
import ResgatarAplicacaoModal from './modals/ResgatarAplicacaoModal'
import { ContaBancariaResumoCell } from './ResumoCells'
import { useAplicacoesFinanceiras, useContasBancariasAtivas, useResgatarAplicacao } from './queries'
import { SITUACAO_APLICACAO_FINANCEIRA_LABEL, TIPO_APLICACAO_FINANCEIRA_LABEL } from './types'
import type { AplicacaoFinanceiraResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function AplicacoesFinanceirasListPage() {
  const { empresaAtivaId, authorities } = useAuth()
  const podeMovimentar = hasAuthority(authorities, 'CAIXA_MOVIMENTAR')
  const [contaBancariaId, setContaBancariaId] = useState<number | undefined>(undefined)
  const [pagina, setPagina] = useState(0)
  const [modalNovaAberto, setModalNovaAberto] = useState(false)
  const [aplicacaoEmResgate, setAplicacaoEmResgate] = useState<AplicacaoFinanceiraResponse | null>(null)

  const { data: contasBancarias } = useContasBancariasAtivas(empresaAtivaId ?? undefined)
  const { data, isLoading, isPlaceholderData } = useAplicacoesFinanceiras(contaBancariaId, pagina, TAMANHO_PAGINA)
  const resgatar = useResgatarAplicacao()

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
        <Title level={3} style={{ margin: 0 }}>
          Aplicações Financeiras
        </Title>
        <Flex gap={12} align="center">
          <Select
            allowClear
            style={{ width: 260 }}
            placeholder="Todas as contas"
            options={contasBancarias?.map((c) => ({ value: c.id, label: c.nome }))}
            value={contaBancariaId}
            onChange={(v) => {
              setContaBancariaId(v)
              setPagina(0)
            }}
          />
          <Tooltip title={podeMovimentar ? undefined : 'Você não tem permissão para movimentar caixa'}>
            <Button type="primary" icon={<PlusOutlined />} disabled={!podeMovimentar} onClick={() => setModalNovaAberto(true)}>
              Nova Aplicação
            </Button>
          </Tooltip>
        </Flex>
      </Flex>

      <Card>
        <Table<AplicacaoFinanceiraResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} aplicação(ões)`,
          }}
          columns={[
            { title: 'Conta origem', key: 'conta', render: (_, a) => <ContaBancariaResumoCell contaBancariaId={a.contaBancariaId} /> },
            { title: 'Tipo', dataIndex: 'tipoAplicacao', render: (v: keyof typeof TIPO_APLICACAO_FINANCEIRA_LABEL) => TIPO_APLICACAO_FINANCEIRA_LABEL[v] },
            { title: 'Descrição', dataIndex: 'descricao', render: (v?: string) => v || '—' },
            { title: 'Data aplicação', dataIndex: 'dataAplicacao', render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY') : '—') },
            { title: 'Valor aplicado', dataIndex: 'valorAplicado', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Valor resgatado', dataIndex: 'valorResgatado', align: 'right', render: (v?: number) => (v != null ? `R$ ${v.toFixed(2)}` : '—') },
            {
              title: 'Rendimento',
              dataIndex: 'rendimento',
              align: 'right',
              render: (v?: number) => (v != null ? <Tag color={v >= 0 ? 'success' : 'error'}>R$ {v.toFixed(2)}</Tag> : '—'),
            },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (v: keyof typeof SITUACAO_APLICACAO_FINANCEIRA_LABEL) => (
                <Tag color={v === 'ATIVA' ? 'processing' : 'default'}>{SITUACAO_APLICACAO_FINANCEIRA_LABEL[v]}</Tag>
              ),
            },
            ...(podeMovimentar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    render: (_: unknown, aplicacao: AplicacaoFinanceiraResponse) =>
                      aplicacao.situacao === 'ATIVA' && (
                        <Button size="small" onClick={() => setAplicacaoEmResgate(aplicacao)}>
                          Resgatar
                        </Button>
                      ),
                  },
                ]
              : []),
          ]}
        />
      </Card>

      <NovaAplicacaoModal open={modalNovaAberto} onClose={() => setModalNovaAberto(false)} />
      {aplicacaoEmResgate && (
        <ResgatarAplicacaoModal
          open={aplicacaoEmResgate !== null}
          onClose={() => setAplicacaoEmResgate(null)}
          submitting={resgatar.isPending}
          valorAplicado={aplicacaoEmResgate.valorAplicado}
          onSubmit={(request) => resgatar.mutateAsync({ id: aplicacaoEmResgate.id, request })}
        />
      )}
    </Flex>
  )
}
