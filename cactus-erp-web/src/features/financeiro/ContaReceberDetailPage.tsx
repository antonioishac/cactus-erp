import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Breadcrumb, Button, Card, Descriptions, Flex, Popconfirm, Skeleton, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { DollarOutlined, EditOutlined, SwapOutlined, UndoOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import AtualizarClassificacaoModal from './modals/AtualizarClassificacaoModal'
import MotivoAcaoModal from './modals/MotivoAcaoModal'
import RegistrarBaixaModal from './modals/RegistrarBaixaModal'
import RenegociarTituloModal from './modals/RenegociarTituloModal'
import { ClienteResumoCell } from './ResumoCells'
import {
  useAtualizarClassificacaoContaReceber,
  useCancelarContaReceber,
  useContaReceber,
  useEstornarBaixaContaReceber,
  useRegistrarBaixaContaReceber,
  useRenegociarContaReceber,
} from './queries'
import { ORIGEM_CONTA_RECEBER_LABEL, SITUACAO_TITULO_LABEL } from './types'
import type { BaixaResponse } from './types'

const { Title } = Typography

const COR_SITUACAO: Record<string, string> = {
  ABERTA: 'default',
  PARCIAL: 'gold',
  PAGA: 'success',
  CANCELADA: 'error',
  RENEGOCIADA: 'purple',
}

export default function ContaReceberDetailPage() {
  const { id } = useParams<{ id: string }>()
  const contaId = Number(id)
  const navigate = useNavigate()
  const { authorities } = useAuth()
  const podeLancar = hasAuthority(authorities, 'FINANCEIRO_LANCAR')
  const podeGerenciar = hasAuthority(authorities, 'FINANCEIRO_GERENCIAR')

  const [modalBaixaAberto, setModalBaixaAberto] = useState(false)
  const [modalCancelarAberto, setModalCancelarAberto] = useState(false)
  const [modalRenegociarAberto, setModalRenegociarAberto] = useState(false)
  const [modalClassificacaoAberto, setModalClassificacaoAberto] = useState(false)
  const [baixaEmEstorno, setBaixaEmEstorno] = useState<BaixaResponse | null>(null)

  const { data: conta, isLoading } = useContaReceber(contaId)
  const registrarBaixa = useRegistrarBaixaContaReceber(contaId)
  const estornarBaixa = useEstornarBaixaContaReceber(contaId)
  const cancelar = useCancelarContaReceber(contaId)
  const renegociar = useRenegociarContaReceber(contaId)
  const atualizarClassificacao = useAtualizarClassificacaoContaReceber(contaId)

  async function handleCancelar(motivo: string) {
    await cancelar.mutateAsync({ motivo })
  }

  async function handleEstornar(motivo: string) {
    if (!baixaEmEstorno) return
    await estornarBaixa.mutateAsync({ baixaId: baixaEmEstorno.id, request: { motivo } })
  }

  if (isLoading || !conta) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 8 }} />
      </Card>
    )
  }

  const emAberto = conta.situacao === 'ABERTA' || conta.situacao === 'PARCIAL'
  const temBaixaAtiva = conta.baixas.some((b) => !b.estornado)
  const podeReceber = emAberto && podeLancar
  const podeCancelarAgora = emAberto && !temBaixaAtiva && podeGerenciar
  const podeRenegociarAgora = emAberto && podeGerenciar

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[{ title: <a onClick={() => navigate('/financeiro/contas-receber')}>Contas a Receber</a> }, { title: `Título #${conta.id}` }]}
      />

      <Card>
        <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
          <Space size="large">
            <Title level={4} style={{ margin: 0 }}>
              Título #{conta.id}
            </Title>
            <Tag color={COR_SITUACAO[conta.situacao]}>{SITUACAO_TITULO_LABEL[conta.situacao]}</Tag>
            {conta.vencido && <Tag color="volcano">Vencido há {conta.diasAtraso} dia(s)</Tag>}
            <Tag>{ORIGEM_CONTA_RECEBER_LABEL[conta.origemTipo]}</Tag>
          </Space>
          <Space wrap>
            <Tooltip title={podeLancar ? undefined : 'Sem permissão para lançar/classificar'}>
              <Button icon={<EditOutlined />} disabled={!podeLancar} onClick={() => setModalClassificacaoAberto(true)}>
                Classificação
              </Button>
            </Tooltip>
            {emAberto && (
              <Tooltip title={podeReceber ? undefined : 'Sem permissão para registrar baixa'}>
                <Button type="primary" icon={<DollarOutlined />} disabled={!podeReceber} onClick={() => setModalBaixaAberto(true)}>
                  Registrar Baixa
                </Button>
              </Tooltip>
            )}
            {emAberto && (
              <Tooltip title={podeRenegociarAgora ? undefined : 'Sem permissão para renegociar'}>
                <Button icon={<SwapOutlined />} disabled={!podeRenegociarAgora} onClick={() => setModalRenegociarAberto(true)}>
                  Renegociar
                </Button>
              </Tooltip>
            )}
            {emAberto && (
              <Tooltip
                title={
                  !podeGerenciar
                    ? 'Sem permissão para cancelar'
                    : temBaixaAtiva
                      ? 'Estorne toda baixa ativa antes de cancelar'
                      : undefined
                }
              >
                <Button danger disabled={!podeCancelarAgora} onClick={() => setModalCancelarAberto(true)}>
                  Cancelar
                </Button>
              </Tooltip>
            )}
          </Space>
        </Flex>

        <Descriptions column={3} style={{ marginTop: 16 }}>
          <Descriptions.Item label="Cliente">
            <ClienteResumoCell clienteId={conta.clienteId} />
          </Descriptions.Item>
          <Descriptions.Item label="Documento">{conta.numeroDocumento || '—'}</Descriptions.Item>
          <Descriptions.Item label="Emissão">{dayjs(conta.dataEmissao).format('DD/MM/YYYY')}</Descriptions.Item>
          <Descriptions.Item label="Vencimento">{dayjs(conta.dataVencimento).format('DD/MM/YYYY')}</Descriptions.Item>
          <Descriptions.Item label="Valor original">R$ {conta.valorOriginal.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="Valor pago">R$ {conta.valorPago.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="Saldo">
            <strong>R$ {conta.valorSaldo.toFixed(2)}</strong>
          </Descriptions.Item>
          <Descriptions.Item label="Multa (atraso)">R$ {conta.valorMulta.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="Juros (atraso)">R$ {conta.valorJuros.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="Valor atualizado">
            <strong>R$ {conta.valorAtualizado.toFixed(2)}</strong>
          </Descriptions.Item>
          <Descriptions.Item label="Conta bancária prevista">{conta.contaBancariaId ? `#${conta.contaBancariaId}` : '—'}</Descriptions.Item>
          <Descriptions.Item label="Desconto até">
            {conta.descontoAte ? `${dayjs(conta.descontoAte).format('DD/MM/YYYY')} (${conta.percentualDesconto ?? 0}%)` : '—'}
          </Descriptions.Item>
          {conta.motivoEncerramento && (
            <Descriptions.Item label="Motivo do encerramento" span={3}>
              {conta.motivoEncerramento}
            </Descriptions.Item>
          )}
          <Descriptions.Item label="Descrição" span={3}>
            {conta.descricao || '—'}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Card title="Baixas">
        <Table<BaixaResponse>
          rowKey="id"
          dataSource={conta.baixas}
          pagination={false}
          locale={{ emptyText: 'Nenhuma baixa registrada ainda' }}
          columns={[
            { title: 'Data', dataIndex: 'dataBaixa', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
            { title: 'Valor', dataIndex: 'valorBaixa', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Desconto', dataIndex: 'valorDesconto', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Conta bancária', dataIndex: 'contaBancariaId', render: (v?: number) => (v ? `#${v}` : '—') },
            { title: 'Observação', dataIndex: 'observacao', render: (v?: string) => v || '—' },
            {
              title: 'Situação',
              key: 'situacao',
              render: (_, baixa) =>
                baixa.estornado ? (
                  <Tooltip title={baixa.motivoEstorno}>
                    <Tag color="error">Estornada {baixa.dataEstorno && `em ${dayjs(baixa.dataEstorno).format('DD/MM/YYYY')}`}</Tag>
                  </Tooltip>
                ) : (
                  <Tag color="success">Ativa</Tag>
                ),
            },
            ...(podeLancar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    render: (_: unknown, baixa: BaixaResponse) =>
                      !baixa.estornado && (
                        <Popconfirm
                          title="Estornar esta baixa?"
                          description="Você poderá informar o motivo na próxima etapa."
                          onConfirm={() => setBaixaEmEstorno(baixa)}
                        >
                          <Button size="small" icon={<UndoOutlined />} danger>
                            Estornar
                          </Button>
                        </Popconfirm>
                      ),
                  },
                ]
              : []),
          ]}
        />
      </Card>

      <RegistrarBaixaModal
        open={modalBaixaAberto}
        onClose={() => setModalBaixaAberto(false)}
        onSubmit={(request) => registrarBaixa.mutateAsync(request)}
        submitting={registrarBaixa.isPending}
        valorSaldo={conta.valorSaldo}
        descontoAte={conta.descontoAte}
      />
      <MotivoAcaoModal
        open={modalCancelarAberto}
        title="Cancelar título"
        okText="Cancelar título"
        okDanger
        onClose={() => setModalCancelarAberto(false)}
        onSubmit={handleCancelar}
        submitting={cancelar.isPending}
        successMessage="Título cancelado."
      />
      <MotivoAcaoModal
        open={baixaEmEstorno !== null}
        title="Estornar baixa"
        okText="Estornar"
        okDanger
        onClose={() => setBaixaEmEstorno(null)}
        onSubmit={handleEstornar}
        submitting={estornarBaixa.isPending}
        successMessage="Baixa estornada."
      />
      <RenegociarTituloModal
        open={modalRenegociarAberto}
        onClose={() => setModalRenegociarAberto(false)}
        onSubmit={(request) => renegociar.mutateAsync(request)}
        submitting={renegociar.isPending}
        valorSaldo={conta.valorSaldo}
      />
      <AtualizarClassificacaoModal
        open={modalClassificacaoAberto}
        onClose={() => setModalClassificacaoAberto(false)}
        onSubmit={(request) => atualizarClassificacao.mutateAsync(request)}
        submitting={atualizarClassificacao.isPending}
        tipoNatureza="RECEITA"
        valoresAtuais={
          conta.naturezaFinanceiraId && conta.tipoDocumentoId
            ? {
                naturezaFinanceiraId: conta.naturezaFinanceiraId,
                tipoDocumentoId: conta.tipoDocumentoId,
                contaBancariaId: conta.contaBancariaId,
                descontoAte: conta.descontoAte,
                percentualDesconto: conta.percentualDesconto,
                percentualMultaAtraso: conta.percentualMultaAtraso,
                percentualJurosDiaAtraso: conta.percentualJurosDiaAtraso,
              }
            : undefined
        }
      />
    </Flex>
  )
}
