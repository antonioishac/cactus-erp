import { useState } from 'react'
import { Card, Flex, InputNumber, Table, Tag, Typography } from 'antd'
import dayjs from 'dayjs'
import { useLotesSincronizacao } from './queries'
import type { LoteSincronizacaoResumoResponse, LotesFiltro } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

/** Um lote agrupa N registros enviados numa única chamada `POST /sync/lotes` do PDV —
 * resumo de aceito/rejeitado por envio, diferente da auditoria registro-a-registro. */
export default function LotesSincronizacaoListPage() {
  const [filtro, setFiltro] = useState<LotesFiltro>({})
  const [pagina, setPagina] = useState(0)

  const { data, isLoading, isPlaceholderData } = useLotesSincronizacao(filtro, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Lotes de Sincronização
      </Title>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <InputNumber
            placeholder="Filtrar por loja (id)"
            min={1}
            onChange={(v) => {
              setFiltro((f) => ({ ...f, lojaId: v ?? undefined }))
              setPagina(0)
            }}
          />
        </Flex>

        <Table<LoteSincronizacaoResumoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} lote(s)`,
          }}
          columns={[
            { title: 'Data', dataIndex: 'dataCriacao', render: (v: string) => dayjs(v).format('DD/MM/YYYY HH:mm') },
            { title: 'Loja', dataIndex: 'lojaId', render: (v: number) => `#${v}` },
            { title: 'Dispositivo', dataIndex: 'dispositivoId' },
            { title: 'Tipo', dataIndex: 'tipoRegistro' },
            { title: 'Total', dataIndex: 'totalRegistros', align: 'right' },
            {
              title: 'Aceitos',
              dataIndex: 'totalAceitos',
              align: 'right',
              render: (v: number) => <Tag color="success">{v}</Tag>,
            },
            {
              title: 'Rejeitados',
              dataIndex: 'totalRejeitados',
              align: 'right',
              render: (v: number) => (v > 0 ? <Tag color="error">{v}</Tag> : v),
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
