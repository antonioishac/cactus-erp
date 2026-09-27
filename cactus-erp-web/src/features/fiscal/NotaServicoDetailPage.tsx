import { useNavigate, useParams } from 'react-router-dom'
import { App, Alert, Breadcrumb, Button, Card, Descriptions, Flex, Skeleton, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { SendOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { ClienteResumoCell } from './ResumoCells'
import { useEmitirNotaServico, useNotaServico } from './queries'
import { AMBIENTE_FISCAL_LABEL, SITUACAO_NOTA_FISCAL_LABEL } from './types'
import type { NotaFiscalServicoItemResponse } from './types'

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
 * Confiança bem menor que NF-e/CT-e (ver Javadoc de `NotaFiscalServicoService`
 * no backend) — o fluxo de emissão real (`NFSe.AdicionarRPS` + envio do lote)
 * nunca foi validado contra um ACBr Monitor real, nem sequer a seção `[NFSe]`
 * do `.ini` local está configurada. O botão "Emitir" existe e chama o
 * endpoint real, mas nada além da composição foi exercitado de verdade.
 */
export default function NotaServicoDetailPage() {
  const { id } = useParams<{ id: string }>()
  const notaId = Number(id)
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeEmitir = hasAuthority(authorities, 'FISCAL_EMITIR')

  const { data: nota, isLoading } = useNotaServico(notaId)
  const emitir = useEmitirNotaServico(notaId)

  async function handleEmitir() {
    try {
      await emitir.mutateAsync()
      message.success('Comando de emissão enviado.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao emitir NFS-e.')
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
      <Breadcrumb items={[{ title: <a onClick={() => navigate('/fiscal/notas-servico')}>Notas de Serviço</a> }, { title: `NFS-e #${nota.id}` }]} />

      <Card>
        <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
          <Space size="large">
            <Title level={4} style={{ margin: 0 }}>
              NFS-e #{nota.id} — OS #{nota.osAberturaId}
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
            title="Retorno da prefeitura/ACBr"
            description={nota.mensagemSefaz}
            showIcon
          />
        )}

        <Descriptions column={3} style={{ marginTop: 16 }}>
          <Descriptions.Item label="Cliente">
            <ClienteResumoCell clienteId={nota.clienteId} />
          </Descriptions.Item>
          <Descriptions.Item label="Ambiente">{nota.ambiente ? AMBIENTE_FISCAL_LABEL[nota.ambiente] : '—'}</Descriptions.Item>
          <Descriptions.Item label="Competência">{nota.competencia || '—'}</Descriptions.Item>
          <Descriptions.Item label="RPS">{nota.numeroRps ? `${nota.serieRps ?? ''}/${nota.numeroRps}` : '—'}</Descriptions.Item>
          <Descriptions.Item label="Número/Verificação">
            {nota.numero ? `${nota.numero} / ${nota.codigoVerificacao ?? '—'}` : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Valor dos serviços">
            <strong>R$ {nota.valorServicos.toFixed(2)}</strong>
          </Descriptions.Item>
          <Descriptions.Item label="Valor do ISS">R$ {nota.valorIss.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="Valor líquido">{nota.valorLiquido != null ? `R$ ${nota.valorLiquido.toFixed(2)}` : '—'}</Descriptions.Item>
        </Descriptions>
      </Card>

      <Card title="Itens">
        <Table<NotaFiscalServicoItemResponse>
          rowKey="id"
          dataSource={nota.itens}
          pagination={false}
          columns={[
            { title: 'Lista de serviço', dataIndex: 'listaServicoDescricao' },
            { title: 'Discriminação', dataIndex: 'discriminacao', render: (v) => v || '—' },
            { title: 'Cód. tributação', dataIndex: 'codigoTributacaoMunicipio', render: (v) => v || '—' },
            { title: 'Valor dos serviços', dataIndex: 'valorServicos', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Alíquota ISS', dataIndex: 'aliquotaIss', align: 'right', render: (v?: number) => (v != null ? `${v}%` : '—') },
            { title: 'Valor do ISS', dataIndex: 'valorIss', align: 'right', render: (v?: number) => (v != null ? `R$ ${v.toFixed(2)}` : '—') },
          ]}
        />
      </Card>
    </Flex>
  )
}
