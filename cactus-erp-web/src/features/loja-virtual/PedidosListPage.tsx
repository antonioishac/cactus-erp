import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Card, Flex, Input, Select, Table, Tag, Typography } from 'antd'
import dayjs from 'dayjs'
import ExportarRelatorioButtons from '@shared/ui/ExportarRelatorioButtons'
import SeletorLoja from './SeletorLoja'
import { exportarPedidosLojaPeriodo } from './api'
import { usePedidos } from './queries'
import { SITUACAO_PEDIDO_LABEL } from './types'
import type { PedidoResponse, SituacaoPedido } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_SITUACAO: Record<string, string> = {
  AGUARDANDO_PAGAMENTO: 'gold',
  PAGO: 'blue',
  EM_SEPARACAO: 'processing',
  ENVIADO: 'cyan',
  ENTREGUE: 'success',
  CANCELADO: 'default',
}

export default function PedidosListPage() {
  const navigate = useNavigate()
  const [lojaId, setLojaId] = useState<number | undefined>(undefined)
  const [pagina, setPagina] = useState(0)
  const [dataInicio, setDataInicio] = useState('')
  const [dataFim, setDataFim] = useState('')
  const [situacaoRelatorio, setSituacaoRelatorio] = useState<SituacaoPedido | undefined>(undefined)

  const { data, isLoading, isPlaceholderData } = usePedidos({ lojaId }, pagina, TAMANHO_PAGINA)
  const filtroRelatorioCompleto = !!dataInicio && !!dataFim

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Pedidos
      </Title>

      <Card>
        <Flex gap={12} wrap="wrap" align="center" style={{ marginBottom: 16 }}>
          <SeletorLoja
            value={lojaId}
            onChange={(v) => {
              setLojaId(v)
              setPagina(0)
            }}
          />
          <span>Relatório — período</span>
          <Input type="date" value={dataInicio} onChange={(e) => setDataInicio(e.target.value)} style={{ width: 160 }} />
          <span>até</span>
          <Input type="date" value={dataFim} onChange={(e) => setDataFim(e.target.value)} style={{ width: 160 }} />
          <Select
            allowClear
            style={{ width: 200 }}
            placeholder="Todas as situações"
            value={situacaoRelatorio}
            onChange={setSituacaoRelatorio}
            options={Object.entries(SITUACAO_PEDIDO_LABEL).map(([value, label]) => ({ value, label }))}
          />
          <ExportarRelatorioButtons
            disabled={!filtroRelatorioCompleto}
            motivoDesabilitado="Selecione o período"
            onExportar={(formato) => exportarPedidosLojaPeriodo(dataInicio, dataFim, lojaId, situacaoRelatorio, formato)}
          />
        </Flex>

        <Table<PedidoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(pedido) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/loja-virtual/pedidos/${pedido.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} pedido(s)`,
          }}
          columns={[
            { title: 'Pedido', dataIndex: 'id', render: (v: number) => `#${v}` },
            { title: 'Cliente', dataIndex: 'clienteNome' },
            { title: 'Data', dataIndex: 'dataCriacao', render: (v: string) => dayjs(v).format('DD/MM/YYYY HH:mm') },
            { title: 'Total', dataIndex: 'valorTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (v: keyof typeof SITUACAO_PEDIDO_LABEL) => <Tag color={COR_SITUACAO[v]}>{SITUACAO_PEDIDO_LABEL[v]}</Tag>,
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
