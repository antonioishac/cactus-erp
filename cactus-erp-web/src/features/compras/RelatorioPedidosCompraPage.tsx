import { useState } from 'react'
import { Card, Flex, Input, Select, Table, Typography } from 'antd'
import AsyncSelect from '@shared/ui/AsyncSelect'
import ExportarRelatorioButtons from '@shared/ui/ExportarRelatorioButtons'
import { exportarPedidosCompraPeriodo, type PedidoCompraRelatorioItem } from './api'
import { useBuscaFornecedoresParaSelecao, usePedidosCompraPeriodo } from './queries'
import { SITUACAO_PEDIDO_LABEL, TIPO_PEDIDO_LABEL } from './types'
import type { SituacaoPedidoCompra } from './types'

const { Title, Text } = Typography

const moeda = (v: number) => `R$ ${v.toFixed(2)}`

export default function RelatorioPedidosCompraPage() {
  const [dataInicio, setDataInicio] = useState('')
  const [dataFim, setDataFim] = useState('')
  const [fornecedorId, setFornecedorId] = useState<number | undefined>(undefined)
  const [termoFornecedor, setTermoFornecedor] = useState<string | undefined>(undefined)
  const [situacao, setSituacao] = useState<SituacaoPedidoCompra | undefined>(undefined)

  const { data: fornecedores, isLoading: carregandoFornecedores } = useBuscaFornecedoresParaSelecao(termoFornecedor)
  const { data, isLoading } = usePedidosCompraPeriodo(dataInicio || undefined, dataFim || undefined, fornecedorId, situacao)
  const pedidos = data ?? []
  const filtroCompleto = !!dataInicio && !!dataFim
  const total = pedidos.reduce((soma, p) => soma + p.valorTotal, 0)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Pedidos de Compra por Período
      </Title>

      <Card>
        <Flex gap={12} align="center" wrap="wrap" style={{ marginBottom: 16 }}>
          <span>Data do pedido</span>
          <Input type="date" value={dataInicio} onChange={(e) => setDataInicio(e.target.value)} style={{ width: 160 }} />
          <span>até</span>
          <Input type="date" value={dataFim} onChange={(e) => setDataFim(e.target.value)} style={{ width: 160 }} />
          <AsyncSelect
            allowClear
            style={{ width: 260 }}
            placeholder="Todos os fornecedores"
            opcoes={fornecedores}
            carregando={carregandoFornecedores}
            termo={termoFornecedor}
            onTermoChange={setTermoFornecedor}
            toOption={(f) => ({ value: f.id, label: f.nome })}
            onChange={(v: number | undefined) => setFornecedorId(v)}
          />
          <Select
            allowClear
            style={{ width: 200 }}
            placeholder="Todas as situações"
            value={situacao}
            onChange={setSituacao}
            options={Object.entries(SITUACAO_PEDIDO_LABEL).map(([value, label]) => ({ value, label }))}
          />
          <ExportarRelatorioButtons
            disabled={!filtroCompleto}
            motivoDesabilitado="Selecione o período"
            onExportar={(formato) => exportarPedidosCompraPeriodo(dataInicio, dataFim, fornecedorId, situacao, formato)}
          />
        </Flex>

        <Table<PedidoCompraRelatorioItem>
          rowKey="pedidoId"
          loading={isLoading}
          dataSource={pedidos}
          pagination={false}
          locale={{ emptyText: 'Selecione o período pra listar os pedidos' }}
          columns={[
            { title: 'Pedido', dataIndex: 'pedidoId', render: (v: number) => `#${v}` },
            { title: 'Data', dataIndex: 'dataPedido' },
            { title: 'Fornecedor', dataIndex: 'fornecedorId', render: (v: number) => `#${v}` },
            { title: 'Tipo', dataIndex: 'tipo', render: (v: keyof typeof TIPO_PEDIDO_LABEL) => TIPO_PEDIDO_LABEL[v] },
            { title: 'Situação', dataIndex: 'situacao', render: (v: keyof typeof SITUACAO_PEDIDO_LABEL) => SITUACAO_PEDIDO_LABEL[v] },
            { title: 'Itens', dataIndex: 'quantidadeItens', align: 'right' },
            { title: 'Valor total', dataIndex: 'valorTotal', align: 'right', render: moeda },
          ]}
          summary={() =>
            pedidos.length > 0 && (
              <Table.Summary.Row>
                <Table.Summary.Cell index={0} colSpan={6}>
                  <Text strong>Total ({pedidos.length} pedido(s))</Text>
                </Table.Summary.Cell>
                <Table.Summary.Cell index={6} align="right">
                  <Text strong>{moeda(total)}</Text>
                </Table.Summary.Cell>
              </Table.Summary.Row>
            )
          }
        />
      </Card>
    </Flex>
  )
}
