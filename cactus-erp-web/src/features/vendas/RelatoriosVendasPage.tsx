import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Card, Flex, Input, Select, Table, Tabs, Typography } from 'antd'
import dayjs from 'dayjs'
import AsyncSelect from '@shared/ui/AsyncSelect'
import ExportarRelatorioButtons from '@shared/ui/ExportarRelatorioButtons'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import { exportarComissoesPorVendedor, exportarProdutosMaisVendidos, exportarVendasPorPeriodo } from './api'
import { ClienteResumoCell, ColaboradorResumoCell } from './ResumoCells'
import {
  useBuscaClientesParaSelecao,
  useComissoesPorVendedor,
  useProdutosMaisVendidos,
  useVendasPorPeriodo,
  useVendedores,
} from './queries'
import type { ComissaoVendedorPeriodoResponse, ProdutoMaisVendidoResponse, VendaPeriodoItemResponse, VendedorResponse } from './types'

const { Title, Text } = Typography

const moeda = (v: number) => `R$ ${v.toFixed(2)}`

/** `vendedorId` é o id do `Vendedor` (vendas-service), não do colaborador — resolve o
 * nome pelo colaborador vinculado, mesmo critério de `ComissoesListPage`. */
function VendedorCell({ vendedorId, vendedores }: { vendedorId: number; vendedores: VendedorResponse[] | undefined }) {
  const vendedor = vendedores?.find((v) => v.id === vendedorId)
  return vendedor ? <ColaboradorResumoCell colaboradorId={vendedor.colaboradorId} /> : <>#{vendedorId}</>
}

/**
 * Os 3 relatórios do vendas-service (`RelatorioVendasController`), numa tela só com o
 * período compartilhado entre as abas. Cada aba consulta o JSON pra exibir e exporta
 * PDF/Excel com exatamente os mesmos filtros. Só vendas CONFIRMADA entram nos números.
 */
export default function RelatoriosVendasPage() {
  const hoje = dayjs()
  const [dataInicio, setDataInicio] = useState(hoje.startOf('month').format('YYYY-MM-DD'))
  const [dataFim, setDataFim] = useState(hoje.endOf('month').format('YYYY-MM-DD'))
  const { data: vendedores } = useVendedores(0, 100)
  const periodoValido = Boolean(dataInicio && dataFim)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Relatórios de Vendas
      </Title>

      <Card>
        <Flex gap={12} align="center" wrap="wrap" style={{ marginBottom: 8 }}>
          <span>Período</span>
          <Input type="date" value={dataInicio} onChange={(e) => setDataInicio(e.target.value)} style={{ width: 170 }} />
          <span>até</span>
          <Input type="date" value={dataFim} onChange={(e) => setDataFim(e.target.value)} style={{ width: 170 }} />
        </Flex>

        <Tabs
          items={[
            {
              key: 'vendas-periodo',
              label: 'Vendas por Período',
              children: (
                <VendasPorPeriodoAba dataInicio={dataInicio} dataFim={dataFim} habilitado={periodoValido} vendedores={vendedores?.conteudo} />
              ),
            },
            {
              key: 'produtos-mais-vendidos',
              label: 'Produtos Mais Vendidos',
              children: <ProdutosMaisVendidosAba dataInicio={dataInicio} dataFim={dataFim} habilitado={periodoValido} />,
            },
            {
              key: 'comissoes-vendedor',
              label: 'Comissões por Vendedor',
              children: (
                <ComissoesPorVendedorAba dataInicio={dataInicio} dataFim={dataFim} habilitado={periodoValido} vendedores={vendedores?.conteudo} />
              ),
            },
          ]}
        />
      </Card>
    </Flex>
  )
}

interface AbaProps {
  dataInicio: string
  dataFim: string
  habilitado: boolean
}

function VendasPorPeriodoAba({ dataInicio, dataFim, habilitado, vendedores }: AbaProps & { vendedores: VendedorResponse[] | undefined }) {
  const navigate = useNavigate()
  const [vendedorId, setVendedorId] = useState<number | undefined>(undefined)
  const [clienteId, setClienteId] = useState<number | undefined>(undefined)
  const [termoCliente, setTermoCliente] = useState<string | undefined>(undefined)
  const { data: clientes, isLoading: carregandoClientes } = useBuscaClientesParaSelecao(termoCliente)

  const filtro = { dataInicio, dataFim, vendedorId, clienteId }
  const { data, isLoading } = useVendasPorPeriodo(filtro, habilitado)
  const vendas = data ?? []

  return (
    <Flex vertical gap={12}>
      <Flex gap={12} align="center" wrap="wrap">
        <Select
          allowClear
          placeholder="Todos os vendedores"
          style={{ width: 240 }}
          value={vendedorId}
          onChange={setVendedorId}
          options={vendedores?.map((v) => ({ value: v.id, label: v.comissaoPerfilNome ?? `Vendedor #${v.id}` }))}
        />
        <AsyncSelect
          allowClear
          placeholder="Todos os clientes"
          style={{ width: 260 }}
          value={clienteId}
          onChange={(v) => setClienteId(v as number | undefined)}
          opcoes={clientes}
          carregando={carregandoClientes}
          termo={termoCliente}
          onTermoChange={setTermoCliente}
          toOption={(c) => ({ value: c.id, label: c.nome })}
        />
        <ExportarRelatorioButtons
          disabled={!habilitado}
          motivoDesabilitado="Informe o período"
          onExportar={(formato) => exportarVendasPorPeriodo(filtro, formato)}
        />
      </Flex>
      <Table<VendaPeriodoItemResponse>
        rowKey="vendaId"
        size="small"
        loading={isLoading && habilitado}
        dataSource={vendas}
        pagination={false}
        locale={{ emptyText: 'Nenhuma venda confirmada no período' }}
        onRow={(v) => ({ style: { cursor: 'pointer' }, onClick: () => navigate(`/vendas/vendas/${v.vendaId}`) })}
        columns={[
          { title: 'Venda', dataIndex: 'vendaId', width: 90, render: (v: number) => `#${v}` },
          { title: 'Data', dataIndex: 'dataVenda', width: 120, render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
          { title: 'Vendedor', key: 'vendedor', render: (_, v) => <VendedorCell vendedorId={v.vendedorId} vendedores={vendedores} /> },
          { title: 'Cliente', key: 'cliente', render: (_, v) => <ClienteResumoCell clienteId={v.clienteId} /> },
          { title: 'Valor total', dataIndex: 'valorTotal', align: 'right', render: moeda },
        ]}
        summary={() =>
          vendas.length > 0 && (
            <Table.Summary.Row>
              <Table.Summary.Cell index={0} colSpan={4}>
                <Text strong>Total ({vendas.length} venda(s))</Text>
              </Table.Summary.Cell>
              <Table.Summary.Cell index={4} align="right">
                <Text strong>{moeda(vendas.reduce((soma, v) => soma + v.valorTotal, 0))}</Text>
              </Table.Summary.Cell>
            </Table.Summary.Row>
          )
        }
      />
    </Flex>
  )
}

function ProdutosMaisVendidosAba({ dataInicio, dataFim, habilitado }: AbaProps) {
  const { data, isLoading } = useProdutosMaisVendidos(dataInicio, dataFim, habilitado)

  return (
    <Flex vertical gap={12}>
      <Flex justify="flex-end">
        <ExportarRelatorioButtons
          disabled={!habilitado}
          motivoDesabilitado="Informe o período"
          onExportar={(formato) => exportarProdutosMaisVendidos(dataInicio, dataFim, formato)}
        />
      </Flex>
      <Table<ProdutoMaisVendidoResponse>
        rowKey="produtoId"
        size="small"
        loading={isLoading && habilitado}
        dataSource={data ?? []}
        pagination={false}
        locale={{ emptyText: 'Nenhum produto vendido no período' }}
        columns={[
          { title: '#', key: 'posicao', width: 60, render: (_, __, indice) => `${indice + 1}º` },
          { title: 'Produto', key: 'produto', render: (_, p) => <ProdutoResumoCell produtoId={p.produtoId} /> },
          { title: 'Quantidade', dataIndex: 'quantidadeTotal', align: 'right', render: (v: number) => v.toLocaleString('pt-BR') },
          { title: 'Valor total', dataIndex: 'valorTotal', align: 'right', render: moeda },
        ]}
      />
    </Flex>
  )
}

function ComissoesPorVendedorAba({ dataInicio, dataFim, habilitado, vendedores }: AbaProps & { vendedores: VendedorResponse[] | undefined }) {
  const { data, isLoading } = useComissoesPorVendedor(dataInicio, dataFim, habilitado)
  const comissoes = data ?? []

  return (
    <Flex vertical gap={12}>
      <Flex justify="space-between" align="center" wrap="wrap" gap={12}>
        <Text type="secondary">Agrupado pela data de lançamento da comissão, não pela data da venda.</Text>
        <ExportarRelatorioButtons
          disabled={!habilitado}
          motivoDesabilitado="Informe o período"
          onExportar={(formato) => exportarComissoesPorVendedor(dataInicio, dataFim, formato)}
        />
      </Flex>
      <Table<ComissaoVendedorPeriodoResponse>
        rowKey="vendedorId"
        size="small"
        loading={isLoading && habilitado}
        dataSource={comissoes}
        pagination={false}
        locale={{ emptyText: 'Nenhuma comissão lançada no período' }}
        columns={[
          { title: 'Vendedor', key: 'vendedor', render: (_, c) => <VendedorCell vendedorId={c.vendedorId} vendedores={vendedores} /> },
          { title: 'Vendas', dataIndex: 'quantidadeVendas', align: 'right' },
          { title: 'Comissão total', dataIndex: 'valorComissaoTotal', align: 'right', render: moeda },
        ]}
        summary={() =>
          comissoes.length > 0 && (
            <Table.Summary.Row>
              <Table.Summary.Cell index={0}>
                <Text strong>Total</Text>
              </Table.Summary.Cell>
              <Table.Summary.Cell index={1} align="right">
                <Text strong>{comissoes.reduce((soma, c) => soma + c.quantidadeVendas, 0)}</Text>
              </Table.Summary.Cell>
              <Table.Summary.Cell index={2} align="right">
                <Text strong>{moeda(comissoes.reduce((soma, c) => soma + c.valorComissaoTotal, 0))}</Text>
              </Table.Summary.Cell>
            </Table.Summary.Row>
          )
        }
      />
    </Flex>
  )
}
