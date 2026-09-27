import { useState } from 'react'
import { App, Alert, Button, Card, Col, Flex, Row, Select, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { UploadOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import ConciliarManualModal from './modals/ConciliarManualModal'
import GerarAjusteModal from './modals/GerarAjusteModal'
import ImportarExtratoModal from './modals/ImportarExtratoModal'
import {
  useConciliarAutomatico,
  useConciliarManual,
  useContasBancariasAtivas,
  useDesconciliarItem,
  useGerarAjusteConciliacao,
  useImportacoesExtrato,
  useItensImportacao,
  usePendenciasConciliacao,
} from './queries'
import type { ExtratoBancarioItemResponse, ExtratoBancarioImportacaoResponse, MovimentoCaixaBancoResponse } from './types'

const { Title } = Typography

/** Linha expandida de "Importações de extrato" — inclui os itens JÁ conciliados
 * (as duas tabelas de "Pendências" acima só mostram o que ainda está pendente,
 * então "desconciliar" um item precisa de outro lugar pra listar o que já foi
 * conciliado; reaproveita o mesmo `useItensImportacao` já usado no EDI Bancário
 * pra padrão de expandable). Componente próprio por causa do hook por linha,
 * mesmo critério de `LinhaItemRecebimento` (gestao-compras-service). */
function ItensImportacaoExpandidos({
  importacaoId,
  podeMovimentar,
  onDesconciliar,
  desconciliando,
}: {
  importacaoId: number
  podeMovimentar: boolean
  onDesconciliar: (itemId: number) => void
  desconciliando: boolean
}) {
  const { data: itens, isLoading } = useItensImportacao(importacaoId, 0)

  return (
    <Table<ExtratoBancarioItemResponse>
      rowKey="id"
      size="small"
      loading={isLoading}
      dataSource={itens?.conteudo ?? []}
      pagination={false}
      locale={{ emptyText: 'Nenhum item nesta importação' }}
      columns={[
        { title: 'Data', dataIndex: 'dataMovimento', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
        { title: 'Histórico', dataIndex: 'historico', render: (v?: string) => v || '—' },
        { title: 'Valor', dataIndex: 'valor', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
        {
          title: 'Situação',
          dataIndex: 'conciliado',
          render: (v: boolean) => (v ? <Tag color="success">Conciliado</Tag> : <Tag color="warning">Pendente</Tag>),
        },
        ...(podeMovimentar
          ? [
              {
                title: 'Ações',
                key: 'acoes',
                render: (_: unknown, item: ExtratoBancarioItemResponse) =>
                  item.conciliado ? (
                    <Button size="small" loading={desconciliando} onClick={() => onDesconciliar(item.id)}>
                      Desconciliar
                    </Button>
                  ) : null,
              },
            ]
          : []),
      ]}
    />
  )
}

/** Página escopada por conta bancária (Select no topo) — pendências e histórico de
 * importação são sempre relativos a UMA conta por vez, mesmo padrão de "Detail contextual"
 * já usado em Transferências/Inventários do Estoque. */
export default function ConciliacaoBancariaPage() {
  const { message } = App.useApp()
  const { empresaAtivaId, authorities } = useAuth()
  const podeMovimentar = hasAuthority(authorities, 'CAIXA_MOVIMENTAR')
  const [contaBancariaId, setContaBancariaId] = useState<number | undefined>(undefined)
  const [modalImportarAberto, setModalImportarAberto] = useState(false)
  const [itemEmConciliacaoManual, setItemEmConciliacaoManual] = useState<ExtratoBancarioItemResponse | null>(null)
  const [itemEmAjuste, setItemEmAjuste] = useState<ExtratoBancarioItemResponse | null>(null)

  const { data: contasBancarias } = useContasBancariasAtivas(empresaAtivaId ?? undefined)
  const { data: importacoes, isLoading: carregandoImportacoes } = useImportacoesExtrato(contaBancariaId, 0, 5)
  const { data: pendencias, isLoading: carregandoPendencias } = usePendenciasConciliacao(contaBancariaId)
  const conciliarAuto = useConciliarAutomatico(contaBancariaId ?? -1)
  const conciliarManualMut = useConciliarManual(contaBancariaId ?? -1)
  const gerarAjuste = useGerarAjusteConciliacao(contaBancariaId ?? -1)
  const desconciliar = useDesconciliarItem(contaBancariaId ?? -1)

  async function handleDesconciliar(itemId: number) {
    try {
      await desconciliar.mutateAsync(itemId)
      message.success('Item desconciliado.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao desconciliar item.')
    }
  }

  async function handleConciliarAutomatico() {
    try {
      const resultado = await conciliarAuto.mutateAsync()
      message.success(`${resultado.quantidadeConciliada} conciliado(s) automaticamente, ${resultado.quantidadePendente} continuam pendente(s).`)
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao conciliar automaticamente.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
        <Title level={3} style={{ margin: 0 }}>
          Conciliação Bancária
        </Title>
        <Select
          style={{ width: 280 }}
          placeholder="Selecione uma conta bancária"
          options={contasBancarias?.map((c) => ({ value: c.id, label: c.nome }))}
          value={contaBancariaId}
          onChange={setContaBancariaId}
        />
      </Flex>

      {!contaBancariaId ? (
        <Alert type="info" showIcon title="Selecione uma conta bancária" description="A conciliação é sempre feita conta por conta." />
      ) : (
        <>
          <Card
            title="Importações de extrato"
            extra={
              <Tooltip title={podeMovimentar ? undefined : 'Sem permissão para movimentar caixa'}>
                <Button type="primary" icon={<UploadOutlined />} disabled={!podeMovimentar} onClick={() => setModalImportarAberto(true)}>
                  Importar Extrato (OFX)
                </Button>
              </Tooltip>
            }
          >
            <Table<ExtratoBancarioImportacaoResponse>
              rowKey="id"
              size="small"
              loading={carregandoImportacoes}
              dataSource={importacoes?.conteudo ?? []}
              pagination={false}
              locale={{ emptyText: 'Nenhuma importação ainda' }}
              expandable={{
                expandedRowRender: (importacao) => (
                  <ItensImportacaoExpandidos
                    importacaoId={importacao.id}
                    podeMovimentar={podeMovimentar}
                    onDesconciliar={handleDesconciliar}
                    desconciliando={desconciliar.isPending}
                  />
                ),
              }}
              columns={[
                { title: 'Arquivo', dataIndex: 'nomeArquivo', render: (v?: string) => v || '—' },
                { title: 'Itens', dataIndex: 'quantidadeItens', align: 'right' },
                { title: 'Duplicados (dedup)', dataIndex: 'quantidadeDuplicados', align: 'right' },
              ]}
            />
          </Card>

          <Card
            title="Pendências de conciliação"
            extra={
              <Tooltip title={podeMovimentar ? undefined : 'Sem permissão para movimentar caixa'}>
                <Button disabled={!podeMovimentar} loading={conciliarAuto.isPending} onClick={handleConciliarAutomatico}>
                  Conciliar Automaticamente
                </Button>
              </Tooltip>
            }
          >
            <Row gutter={16}>
              <Col span={12}>
                <Typography.Text strong>Itens do extrato ainda sem lançamento interno</Typography.Text>
                <Table<ExtratoBancarioItemResponse>
                  rowKey="id"
                  size="small"
                  style={{ marginTop: 8 }}
                  loading={carregandoPendencias}
                  dataSource={pendencias?.itensExtratoPendentes ?? []}
                  pagination={false}
                  locale={{ emptyText: 'Nenhum item pendente' }}
                  columns={[
                    { title: 'Data', dataIndex: 'dataMovimento', render: (v: string) => dayjs(v).format('DD/MM') },
                    { title: 'Histórico', dataIndex: 'historico', render: (v?: string) => v || '—' },
                    {
                      title: 'Valor',
                      dataIndex: 'valor',
                      align: 'right',
                      render: (v: number) => <Tag color={v >= 0 ? 'success' : 'error'}>R$ {v.toFixed(2)}</Tag>,
                    },
                    ...(podeMovimentar
                      ? [
                          {
                            title: 'Ações',
                            key: 'acoes',
                            render: (_: unknown, item: ExtratoBancarioItemResponse) => (
                              <Space size={4}>
                                <Button size="small" onClick={() => setItemEmConciliacaoManual(item)}>
                                  Conciliar
                                </Button>
                                <Button size="small" onClick={() => setItemEmAjuste(item)}>
                                  Gerar Ajuste
                                </Button>
                              </Space>
                            ),
                          },
                        ]
                      : []),
                  ]}
                />
              </Col>
              <Col span={12}>
                <Typography.Text strong>Movimentos internos ainda sem confirmação do extrato</Typography.Text>
                <Table<MovimentoCaixaBancoResponse>
                  rowKey="id"
                  size="small"
                  style={{ marginTop: 8 }}
                  loading={carregandoPendencias}
                  dataSource={pendencias?.movimentosPendentes ?? []}
                  pagination={false}
                  locale={{ emptyText: 'Nenhum movimento pendente' }}
                  columns={[
                    { title: 'Data', dataIndex: 'dataMovimento', render: (v: string) => dayjs(v).format('DD/MM') },
                    { title: 'Histórico', dataIndex: 'historico' },
                    { title: 'Valor', dataIndex: 'valor', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
                  ]}
                />
              </Col>
            </Row>
          </Card>

        </>
      )}

      {contaBancariaId && (
        <>
          <ImportarExtratoModal open={modalImportarAberto} contaBancariaId={contaBancariaId} onClose={() => setModalImportarAberto(false)} />
          <ConciliarManualModal
            open={itemEmConciliacaoManual !== null}
            onClose={() => setItemEmConciliacaoManual(null)}
            submitting={conciliarManualMut.isPending}
            movimentosPendentes={pendencias?.movimentosPendentes ?? []}
            onSubmit={(movimentoCaixaBancoId) => {
              if (!itemEmConciliacaoManual) return Promise.resolve()
              return conciliarManualMut.mutateAsync({ itemId: itemEmConciliacaoManual.id, request: { movimentoCaixaBancoId } })
            }}
          />
          <GerarAjusteModal
            open={itemEmAjuste !== null}
            onClose={() => setItemEmAjuste(null)}
            submitting={gerarAjuste.isPending}
            onSubmit={(request) => {
              if (!itemEmAjuste) return Promise.resolve()
              return gerarAjuste.mutateAsync({ itemId: itemEmAjuste.id, request })
            }}
          />
        </>
      )}
    </Flex>
  )
}
