import { useState } from 'react'
import { App, Button, Card, Flex, InputNumber, Popconfirm, Select, Table, Tag, Tooltip, Typography } from 'antd'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useRegistrosSincronizacao, useReprocessarRegistro } from './queries'
import { STATUS_REGISTRO_LABEL } from './types'
import type { RegistroSincronizacaoResponse, RegistrosFiltro, StatusRegistroSincronizacao } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_STATUS: Record<StatusRegistroSincronizacao, string> = {
  RECEBIDO: 'default',
  PUBLICADO: 'processing',
  PROCESSADO: 'success',
  ERRO: 'error',
}

/** Auditoria do canal PDV→ERP — só consulta + reprocessamento de registro em `ERRO`
 * (nunca criação/edição manual de payload, ver Javadoc de `SincronizacaoAuditoriaController`). */
export default function RegistrosSincronizacaoListPage() {
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'SINCRONIZACAO_GERENCIAR')
  const [filtro, setFiltro] = useState<RegistrosFiltro>({})
  const [pagina, setPagina] = useState(0)

  const { data, isLoading, isPlaceholderData } = useRegistrosSincronizacao(filtro, pagina, TAMANHO_PAGINA)
  const reprocessar = useReprocessarRegistro()

  async function handleReprocessar(id: number) {
    try {
      await reprocessar.mutateAsync(id)
      message.success('Registro reenviado pra fila — volta pra "Recebido".')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao reprocessar registro.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Registros de Sincronização
      </Title>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <Select
            allowClear
            style={{ width: 240 }}
            placeholder="Todos os status"
            onChange={(v: StatusRegistroSincronizacao | undefined) => {
              setFiltro((f) => ({ ...f, status: v }))
              setPagina(0)
            }}
            options={Object.entries(STATUS_REGISTRO_LABEL).map(([value, label]) => ({ value, label }))}
          />
          <InputNumber
            placeholder="Filtrar por loja (id)"
            min={1}
            onChange={(v) => {
              setFiltro((f) => ({ ...f, lojaId: v ?? undefined }))
              setPagina(0)
            }}
          />
        </Flex>

        <Table<RegistroSincronizacaoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} registro(s)`,
          }}
          columns={[
            { title: 'Data', dataIndex: 'dataCriacao', render: (v: string) => dayjs(v).format('DD/MM/YYYY HH:mm') },
            { title: 'Loja', dataIndex: 'lojaId', render: (v: number) => `#${v}` },
            { title: 'Dispositivo', dataIndex: 'dispositivoId' },
            { title: 'Tipo', dataIndex: 'tipoRegistro' },
            { title: 'Id origem', dataIndex: 'idOrigem' },
            {
              title: 'Status',
              dataIndex: 'status',
              render: (v: StatusRegistroSincronizacao) => <Tag color={COR_STATUS[v]}>{STATUS_REGISTRO_LABEL[v]}</Tag>,
            },
            { title: 'Tentativas', dataIndex: 'tentativas', align: 'right' },
            { title: 'Motivo do erro', dataIndex: 'motivoErro', render: (v?: string) => v || '—', ellipsis: true },
            ...(podeGerenciar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    width: 140,
                    render: (_: unknown, registro: RegistroSincronizacaoResponse) =>
                      registro.status === 'ERRO' ? (
                        <Popconfirm title="Reprocessar este registro?" onConfirm={() => handleReprocessar(registro.id)}>
                          <Button size="small">Reprocessar</Button>
                        </Popconfirm>
                      ) : (
                        <Tooltip title="Só registros em erro podem ser reprocessados">
                          <Button size="small" disabled>
                            Reprocessar
                          </Button>
                        </Tooltip>
                      ),
                  },
                ]
              : []),
          ]}
        />
      </Card>
    </Flex>
  )
}
