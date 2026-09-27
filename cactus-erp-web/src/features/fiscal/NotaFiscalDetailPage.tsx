import { useNavigate, useParams } from 'react-router-dom'
import { App, Alert, Breadcrumb, Button, Card, Descriptions, Flex, Skeleton, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { SendOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import { ClienteResumoCell, TransportadoraResumoCell } from './ResumoCells'
import { useEmitirNotaFiscal, useNotaFiscal } from './queries'
import { AMBIENTE_FISCAL_LABEL, SITUACAO_NOTA_FISCAL_LABEL } from './types'
import type { NotaFiscalEletronicaItemResponse } from './types'

const { Title } = Typography

const COR_SITUACAO: Record<string, string> = {
  RASCUNHO: 'default',
  ENVIADA: 'processing',
  AUTORIZADA: 'success',
  REJEITADA: 'error',
  ERRO: 'error',
  CANCELADA: 'default',
}

/**
 * Emissão real via ACBr Monitor nunca foi validada contra a SEFAZ nesta sessão
 * (bloqueio de certificado, ver `BACKLOG.md`) — o botão "Emitir" existe e
 * chama o endpoint real, mas o comportamento em produção não foi exercitado
 * ao vivo. Mesmo nível de honestidade já documentado no backend.
 */
export default function NotaFiscalDetailPage() {
  const { id } = useParams<{ id: string }>()
  const notaId = Number(id)
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeEmitir = hasAuthority(authorities, 'FISCAL_EMITIR')

  const { data: nota, isLoading } = useNotaFiscal(notaId)
  const emitir = useEmitirNotaFiscal(notaId)

  async function handleEmitir() {
    try {
      await emitir.mutateAsync()
      message.success('Comando de emissão enviado.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao emitir NF-e.')
    }
  }

  if (isLoading || !nota) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 8 }} />
      </Card>
    )
  }

  const podeEmitirAgora = (nota.situacao === 'RASCUNHO' || nota.situacao === 'ERRO') && podeEmitir

  return (
    <Flex vertical gap={16}>
      <Breadcrumb items={[{ title: <a onClick={() => navigate('/fiscal/notas-fiscais')}>Notas Fiscais</a> }, { title: `NF-e #${nota.id}` }]} />

      <Card>
        <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
          <Space size="large">
            <Title level={4} style={{ margin: 0 }}>
              NF-e #{nota.id} — Venda #{nota.vendaId}
            </Title>
            <Tag color={COR_SITUACAO[nota.situacao]}>{SITUACAO_NOTA_FISCAL_LABEL[nota.situacao]}</Tag>
          </Space>
          <Tooltip title={podeEmitirAgora ? undefined : 'Sem permissão, ou a nota já foi enviada/autorizada'}>
            <Button type="primary" icon={<SendOutlined />} disabled={!podeEmitirAgora} loading={emitir.isPending} onClick={handleEmitir}>
              Emitir
            </Button>
          </Tooltip>
        </Flex>

        {nota.mensagemSefaz && (
          <Alert
            style={{ marginTop: 16 }}
            type={nota.situacao === 'AUTORIZADA' ? 'success' : nota.situacao === 'ERRO' ? 'error' : 'warning'}
            title="Retorno da SEFAZ/ACBr"
            description={nota.mensagemSefaz}
            showIcon
          />
        )}

        <Descriptions column={3} style={{ marginTop: 16 }}>
          <Descriptions.Item label="Cliente">
            <ClienteResumoCell clienteId={nota.clienteId} />
          </Descriptions.Item>
          <Descriptions.Item label="Transportadora">
            {nota.transportadoraId ? <TransportadoraResumoCell transportadoraId={nota.transportadoraId} /> : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Ambiente">{nota.ambiente ? AMBIENTE_FISCAL_LABEL[nota.ambiente] : '—'}</Descriptions.Item>
          <Descriptions.Item label="Modelo/Série/Número">
            {nota.numero ? `${nota.modelo}/${nota.serie}/${nota.numero}` : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Data de emissão">
            {nota.dataEmissao ? dayjs(nota.dataEmissao).format('DD/MM/YYYY HH:mm') : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Total">
            <strong>R$ {nota.valorTotal.toFixed(2)}</strong>
          </Descriptions.Item>
          <Descriptions.Item label="Chave de acesso" span={2}>
            {nota.chaveAcesso || '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Protocolo de autorização">{nota.protocoloAutorizacao || '—'}</Descriptions.Item>
        </Descriptions>
      </Card>

      <Card title="Itens">
        <Table<NotaFiscalEletronicaItemResponse>
          rowKey="id"
          dataSource={nota.itens}
          pagination={false}
          columns={[
            { title: 'Nº', dataIndex: 'numeroItem', width: 50 },
            { title: 'Produto', key: 'produto', render: (_, item) => <ProdutoResumoCell produtoId={item.produtoId} /> },
            { title: 'NCM', dataIndex: 'ncm', render: (v?: string) => v || '—' },
            { title: 'CFOP', dataIndex: 'cfop', render: (v?: string) => v || '—' },
            { title: 'CST/CSOSN', key: 'cst', render: (_, item) => item.cstIcms || item.csosn || '—' },
            { title: 'Quantidade', dataIndex: 'quantidade', align: 'right' },
            { title: 'Valor unitário', dataIndex: 'valorUnitario', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Valor total', dataIndex: 'valorTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
          ]}
        />
      </Card>
    </Flex>
  )
}
