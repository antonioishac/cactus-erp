import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Alert, Breadcrumb, Button, Card, Descriptions, Flex, Skeleton, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { CheckCircleOutlined, SendOutlined, StopOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import CancelarMdfeModal from './modals/CancelarMdfeModal'
import EncerrarMdfeModal from './modals/EncerrarMdfeModal'
import { useEmitirMdfe, useMdfe } from './queries'
import { AMBIENTE_FISCAL_LABEL, COR_SITUACAO_MDFE, SITUACAO_NOTA_FISCAL_LABEL } from './types'
import type { MdfeDocumentoVinculadoResponse } from './types'

const { Title } = Typography
const PRAZO_CANCELAMENTO_HORAS = 24

/** Emissão/encerramento/cancelamento via ACBr — nunca validados contra SEFAZ real (mesmo bloqueio de certificado de NF-e/CT-e). */
export default function MdfeDetailPage() {
  const { id } = useParams<{ id: string }>()
  const mdfeId = Number(id)
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeEmitir = hasAuthority(authorities, 'FISCAL_EMITIR')
  const podeGerenciar = hasAuthority(authorities, 'FISCAL_GERENCIAR')
  const [encerrarAberto, setEncerrarAberto] = useState(false)
  const [cancelarAberto, setCancelarAberto] = useState(false)

  const { data: mdfe, isLoading } = useMdfe(mdfeId)
  const emitir = useEmitirMdfe(mdfeId)

  async function handleEmitir() {
    try {
      await emitir.mutateAsync()
      message.success('Comando de emissão enviado.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao emitir MDF-e.')
    }
  }

  if (isLoading || !mdfe) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 8 }} />
      </Card>
    )
  }

  const autorizada = mdfe.situacao === 'AUTORIZADA'
  const podeEmitirAgora = (mdfe.situacao === 'RASCUNHO' || mdfe.situacao === 'ERRO') && podeEmitir
  const dentroDoPrazo =
    mdfe.dataEmissao != null && dayjs().diff(dayjs(mdfe.dataEmissao), 'hour', true) < PRAZO_CANCELAMENTO_HORAS
  const motivoSemCancelar = !podeGerenciar
    ? 'Sem permissão (FISCAL_GERENCIAR)'
    : !autorizada
      ? 'Só MDF-e autorizado pode ser cancelado'
      : !dentroDoPrazo
        ? 'Prazo de 24h após a autorização expirado — encerre o MDF-e'
        : undefined
  const motivoSemEncerrar = !podeEmitir ? 'Sem permissão (FISCAL_EMITIR)' : !autorizada ? 'Só MDF-e autorizado pode ser encerrado' : undefined
  const ultimoMunicipio = mdfe.documentos.at(-1)?.municipioDescargaNome

  return (
    <Flex vertical gap={16}>
      <Breadcrumb items={[{ title: <a onClick={() => navigate('/fiscal/mdfes')}>MDF-e</a> }, { title: `MDF-e #${mdfe.id}` }]} />

      <Card>
        <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
          <Space size="large">
            <Title level={4} style={{ margin: 0 }}>
              MDF-e #{mdfe.id}
              {mdfe.numero ? ` — ${mdfe.serie}/${mdfe.numero}` : ''}
            </Title>
            <Tag color={COR_SITUACAO_MDFE[mdfe.situacao]}>{SITUACAO_NOTA_FISCAL_LABEL[mdfe.situacao]}</Tag>
          </Space>
          <Space wrap>
            <Tooltip title={podeEmitirAgora ? undefined : 'Sem permissão, ou o MDF-e já foi enviado/autorizado'}>
              <Button type="primary" icon={<SendOutlined />} disabled={!podeEmitirAgora} loading={emitir.isPending} onClick={handleEmitir}>
                Emitir
              </Button>
            </Tooltip>
            <Tooltip title={motivoSemEncerrar}>
              <Button icon={<CheckCircleOutlined />} disabled={motivoSemEncerrar != null} onClick={() => setEncerrarAberto(true)}>
                Encerrar
              </Button>
            </Tooltip>
            <Tooltip title={motivoSemCancelar}>
              <Button danger icon={<StopOutlined />} disabled={motivoSemCancelar != null} onClick={() => setCancelarAberto(true)}>
                Cancelar
              </Button>
            </Tooltip>
          </Space>
        </Flex>

        {autorizada && (
          <Alert
            style={{ marginTop: 16 }}
            type="warning"
            showIcon
            title="Viagem em aberto"
            description="Encerre o MDF-e assim que a viagem terminar — um manifesto aberto impede novos MDF-e do mesmo veículo na SEFAZ."
          />
        )}
        {mdfe.mensagemSefaz && (
          <Alert
            style={{ marginTop: 16 }}
            type={mdfe.situacao === 'ERRO' || mdfe.situacao === 'REJEITADA' ? 'error' : 'success'}
            title="Retorno da SEFAZ/ACBr"
            description={mdfe.mensagemSefaz}
            showIcon
          />
        )}

        <Descriptions column={3} style={{ marginTop: 16 }}>
          <Descriptions.Item label="Ambiente">{mdfe.ambiente ? AMBIENTE_FISCAL_LABEL[mdfe.ambiente] : '—'}</Descriptions.Item>
          <Descriptions.Item label="UF da viagem">{mdfe.uf}</Descriptions.Item>
          <Descriptions.Item label="Início da viagem">
            {mdfe.dataInicioViagem ? dayjs(mdfe.dataInicioViagem).format('DD/MM/YYYY') : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Carregamento">{mdfe.municipioCarregamentoNome ?? '—'}</Descriptions.Item>
          <Descriptions.Item label="Veículo">
            {mdfe.veiculoPlaca ? `${mdfe.veiculoPlaca}/${mdfe.veiculoUf ?? ''}` : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Tara / Capacidade">
            {mdfe.veiculoTaraKg != null ? `${mdfe.veiculoTaraKg} kg` : '—'} /{' '}
            {mdfe.veiculoCapacidadeKg != null ? `${mdfe.veiculoCapacidadeKg} kg` : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Condutor">{mdfe.condutorNome ?? '—'}</Descriptions.Item>
          <Descriptions.Item label="CPF do condutor">{mdfe.condutorCpf ?? '—'}</Descriptions.Item>
          <Descriptions.Item label="RNTRC">{mdfe.rntrc ?? '—'}</Descriptions.Item>
          <Descriptions.Item label="Valor da carga">
            {mdfe.valorTotalCarga != null ? `R$ ${mdfe.valorTotalCarga.toFixed(2)}` : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Peso bruto">{mdfe.pesoBrutoTotal != null ? `${mdfe.pesoBrutoTotal} kg` : '—'}</Descriptions.Item>
          <Descriptions.Item label="Data de emissão">
            {mdfe.dataEmissao ? dayjs(mdfe.dataEmissao).format('DD/MM/YYYY HH:mm') : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Chave de acesso" span={2}>
            {mdfe.chaveAcesso || '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Protocolo de autorização">{mdfe.protocoloAutorizacao || '—'}</Descriptions.Item>
          {mdfe.situacao === 'ENCERRADA' && (
            <>
              <Descriptions.Item label="Encerrado em">
                {mdfe.dataEncerramento ? dayjs(mdfe.dataEncerramento).format('DD/MM/YYYY') : '—'}
              </Descriptions.Item>
              <Descriptions.Item label="Município (IBGE)">{mdfe.municipioEncerramentoCodigoIbge ?? '—'}</Descriptions.Item>
              <Descriptions.Item label="Protocolo do encerramento">{mdfe.protocoloEncerramento ?? '—'}</Descriptions.Item>
            </>
          )}
          {mdfe.situacao === 'CANCELADA' && (
            <>
              <Descriptions.Item label="Cancelado em">
                {mdfe.dataCancelamento ? dayjs(mdfe.dataCancelamento).format('DD/MM/YYYY HH:mm') : '—'}
              </Descriptions.Item>
              <Descriptions.Item label="Protocolo do cancelamento" span={2}>
                {mdfe.protocoloCancelamento ?? '—'}
              </Descriptions.Item>
              <Descriptions.Item label="Justificativa" span={3}>
                {mdfe.justificativaCancelamento ?? '—'}
              </Descriptions.Item>
            </>
          )}
        </Descriptions>
      </Card>

      <Card title={`CT-e vinculados (${mdfe.documentos.length})`}>
        <Table<MdfeDocumentoVinculadoResponse>
          rowKey="id"
          dataSource={mdfe.documentos}
          pagination={false}
          onRow={(doc) => ({ style: { cursor: 'pointer' }, onClick: () => navigate(`/fiscal/ctes/${doc.cteCabecalhoId}`) })}
          columns={[
            { title: 'CT-e', key: 'cte', render: (_, d) => (d.cteNumero ? `${d.cteSerie}/${d.cteNumero}` : `#${d.cteCabecalhoId}`) },
            { title: 'Chave de acesso', dataIndex: 'cteChaveAcesso', render: (v?: string) => v ?? '—' },
            { title: 'Descarga', key: 'descarga', render: (_, d) => d.municipioDescargaNome ?? '—' },
          ]}
        />
      </Card>

      <EncerrarMdfeModal
        open={encerrarAberto}
        mdfeId={mdfe.id}
        dataInicioViagem={mdfe.dataInicioViagem}
        municipioPadraoNome={ultimoMunicipio}
        onClose={() => setEncerrarAberto(false)}
      />
      <CancelarMdfeModal open={cancelarAberto} mdfeId={mdfe.id} onClose={() => setCancelarAberto(false)} />
    </Flex>
  )
}
