import { useState } from 'react'
import { Alert, App, Button, Card, Flex, Table, Tag, Tooltip, Typography } from 'antd'
import { DownloadOutlined, StopOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import InutilizarNumeracaoModal from './modals/InutilizarNumeracaoModal'
import { baixarXmlInutilizacaoNfe } from './api'
import { useInutilizacoesNfe } from './queries'
import { AMBIENTE_FISCAL_LABEL, SITUACAO_INUTILIZACAO_LABEL } from './types'
import type { InutilizacaoNumeracaoNfeResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

/**
 * Pedidos de inutilização de numeração de NF-e da empresa ativa — homologados e rejeitados (trilha de auditoria, nada se
 * apaga). Inutilizar é a regularização legal de número reservado que nunca virou documento.
 */
export default function InutilizacoesNfeListPage() {
  const { message } = App.useApp()
  const { empresaAtivaId, authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'FISCAL_GERENCIAR')
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)

  const { data, isLoading, isPlaceholderData } = useInutilizacoesNfe(empresaAtivaId ?? undefined, pagina, TAMANHO_PAGINA)

  async function handleBaixarXml(id: number) {
    try {
      await baixarXmlInutilizacaoNfe(id)
    } catch {
      message.error('Não foi possível baixar o XML.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Inutilização de Numeração (NF-e)
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Sem permissão (FISCAL_GERENCIAR)'}>
          <Button
            type="primary"
            icon={<StopOutlined />}
            disabled={!podeGerenciar || empresaAtivaId == null}
            onClick={() => setModalAberto(true)}
          >
            Inutilizar Faixa
          </Button>
        </Tooltip>
      </Flex>

      <Alert
        type="info"
        showIcon
        title="Quando usar"
        description="Números de NF-e reservados que nunca viraram nota (ex.: falha na composição) precisam ser inutilizados na SEFAZ. Só é aceito número já reservado e que não pertença a nenhuma NF-e do sistema. Homologada, a faixa regulariza os números pulados no cadastro da empresa. Em AC, BA e SP, desde 01/11/2026, o pedido é dispensado (Ajuste SINIEF 40/2026): o salto só é registrado."
      />

      <Card>
        <Table<InutilizacaoNumeracaoNfeResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} pedido(s)`,
          }}
          columns={[
            { title: 'Série', dataIndex: 'serie', width: 80 },
            {
              title: 'Faixa',
              key: 'faixa',
              render: (_, item) => (item.numeroInicial === item.numeroFinal ? item.numeroInicial : `${item.numeroInicial} a ${item.numeroFinal}`),
            },
            { title: 'Ambiente', dataIndex: 'ambiente', render: (v: InutilizacaoNumeracaoNfeResponse['ambiente']) => AMBIENTE_FISCAL_LABEL[v] },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (v: InutilizacaoNumeracaoNfeResponse['situacao']) => (
                <Tag color={v === 'HOMOLOGADA' ? 'success' : v === 'DISPENSADA' ? 'blue' : 'error'}>{SITUACAO_INUTILIZACAO_LABEL[v]}</Tag>
              ),
            },
            { title: 'Protocolo', dataIndex: 'protocolo', render: (v) => v || '—' },
            { title: 'Retorno da SEFAZ', dataIndex: 'mensagemSefaz', ellipsis: true, render: (v) => v || '—' },
            { title: 'Justificativa', dataIndex: 'justificativa', ellipsis: true },
            {
              title: 'Pedido em',
              dataIndex: 'dataCriacao',
              render: (v: string) => dayjs(v).format('DD/MM/YYYY HH:mm'),
            },
            {
              title: 'XML',
              key: 'xml',
              width: 70,
              render: (_, item) =>
                item.situacao === 'DISPENSADA' ? (
                  '—'
                ) : (
                  <Button size="small" icon={<DownloadOutlined />} aria-label="Baixar XML" onClick={() => handleBaixarXml(item.id)} />
                ),
            },
          ]}
        />
      </Card>

      {empresaAtivaId != null && (
        <InutilizarNumeracaoModal open={modalAberto} empresaId={empresaAtivaId} onClose={() => setModalAberto(false)} />
      )}
    </Flex>
  )
}
