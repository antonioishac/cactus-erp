import { useNavigate, useParams } from 'react-router-dom'
import { App, Alert, Breadcrumb, Button, Card, Descriptions, Flex, Skeleton, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { SendOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { ClienteResumoCell, TransportadoraResumoCell } from './ResumoCells'
import { useCte, useEmitirCte } from './queries'
import { AMBIENTE_FISCAL_LABEL, SITUACAO_NOTA_FISCAL_LABEL, TIPO_TOMADOR_CTE_LABEL } from './types'
import type { CteComponenteResponse } from './types'

const { Title } = Typography

const COR_SITUACAO: Record<string, string> = {
  RASCUNHO: 'default',
  ENVIADA: 'processing',
  AUTORIZADA: 'success',
  REJEITADA: 'error',
  ERRO: 'error',
  CANCELADA: 'default',
}

/** Mesmo nível de honestidade da NF-e — emissão real via ACBr nunca validada contra a SEFAZ nesta sessão (bloqueio de certificado). */
export default function CteDetailPage() {
  const { id } = useParams<{ id: string }>()
  const cteId = Number(id)
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeEmitir = hasAuthority(authorities, 'FISCAL_EMITIR')

  const { data: cte, isLoading } = useCte(cteId)
  const emitir = useEmitirCte(cteId)

  async function handleEmitir() {
    try {
      await emitir.mutateAsync()
      message.success('Comando de emissão enviado.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao emitir CT-e.')
    }
  }

  if (isLoading || !cte) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 8 }} />
      </Card>
    )
  }

  const podeEmitirAgora = (cte.situacao === 'RASCUNHO' || cte.situacao === 'ERRO') && podeEmitir

  return (
    <Flex vertical gap={16}>
      <Breadcrumb items={[{ title: <a onClick={() => navigate('/fiscal/ctes')}>CT-e</a> }, { title: `CT-e #${cte.id}` }]} />

      <Card>
        <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
          <Space size="large">
            <Title level={4} style={{ margin: 0 }}>
              CT-e #{cte.id} — Venda #{cte.vendaId}
            </Title>
            <Tag color={COR_SITUACAO[cte.situacao]}>{SITUACAO_NOTA_FISCAL_LABEL[cte.situacao]}</Tag>
          </Space>
          <Tooltip title={podeEmitirAgora ? undefined : 'Sem permissão, ou o CT-e já foi enviado/autorizado'}>
            <Button type="primary" icon={<SendOutlined />} disabled={!podeEmitirAgora} loading={emitir.isPending} onClick={handleEmitir}>
              Emitir
            </Button>
          </Tooltip>
        </Flex>

        {cte.mensagemSefaz && (
          <Alert
            style={{ marginTop: 16 }}
            type={cte.situacao === 'AUTORIZADA' ? 'success' : cte.situacao === 'ERRO' ? 'error' : 'warning'}
            title="Retorno da SEFAZ/ACBr"
            description={cte.mensagemSefaz}
            showIcon
          />
        )}

        <Descriptions column={3} style={{ marginTop: 16 }}>
          <Descriptions.Item label="Cliente (destinatário)">
            <ClienteResumoCell clienteId={cte.clienteId} />
          </Descriptions.Item>
          <Descriptions.Item label="Transportadora">
            {cte.transportadoraId ? <TransportadoraResumoCell transportadoraId={cte.transportadoraId} /> : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Tomador">{TIPO_TOMADOR_CTE_LABEL[cte.tomador]}</Descriptions.Item>
          <Descriptions.Item label="Ambiente">{cte.ambiente ? AMBIENTE_FISCAL_LABEL[cte.ambiente] : '—'}</Descriptions.Item>
          <Descriptions.Item label="Modelo/Modal/Série/Número">
            {cte.numero ? `${cte.modelo}/${cte.modal}/${cte.serie}/${cte.numero}` : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Data de emissão">
            {cte.dataEmissao ? dayjs(cte.dataEmissao).format('DD/MM/YYYY HH:mm') : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="CFOP">{cte.cfop || '—'}</Descriptions.Item>
          <Descriptions.Item label="Placa/UF">{cte.placa ? `${cte.placa}/${cte.ufPlaca ?? ''}` : '—'}</Descriptions.Item>
          <Descriptions.Item label="Peso bruto / Volumes">
            {cte.pesoBruto != null ? `${cte.pesoBruto} kg` : '—'} / {cte.quantidadeVolume ?? '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Valor do serviço">R$ {cte.valorTotalServico.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="Valor da carga">
            {cte.valorTotalCarga != null ? `R$ ${cte.valorTotalCarga.toFixed(2)}` : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="ICMS (alíquota/valor)">
            {cte.aliquotaIcms != null ? `${cte.aliquotaIcms}% / R$ ${(cte.valorIcms ?? 0).toFixed(2)}` : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Chave de acesso" span={2}>
            {cte.chaveAcesso || '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Protocolo de autorização">{cte.protocoloAutorizacao || '—'}</Descriptions.Item>
          <Descriptions.Item label="Produto predominante" span={3}>
            {cte.produtoPredominante || '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Observações gerais" span={3}>
            {cte.observacoesGerais || '—'}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Card title="Componentes do valor da prestação">
        <Table<CteComponenteResponse>
          rowKey="id"
          dataSource={cte.componentes}
          pagination={false}
          locale={{ emptyText: 'Nenhum componente informado' }}
          columns={[
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Valor', dataIndex: 'valor', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
          ]}
        />
      </Card>
    </Flex>
  )
}
