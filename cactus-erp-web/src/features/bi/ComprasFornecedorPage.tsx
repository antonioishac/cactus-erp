import { useState } from 'react'
import { Card, DatePicker, Flex, Table, Typography } from 'antd'
import dayjs, { type Dayjs } from 'dayjs'
import { useComprasFornecedor } from './queries'
import type { ComprasFornecedorFiltro, ComprasFornecedorIndicadorResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

/** Agregado mensal — snapshot gerado pelo ETL a partir de `pedido_compra` do gestao-compras-service (ver Javadoc de `IndicadoresController`). */
export default function ComprasFornecedorPage() {
  const [mesReferencia, setMesReferencia] = useState<Dayjs>(dayjs())
  const [pagina, setPagina] = useState(0)

  const filtro: ComprasFornecedorFiltro = { anoMes: mesReferencia.format('YYYY-MM') }
  const { data, isLoading, isPlaceholderData } = useComprasFornecedor(filtro, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Compras por Fornecedor
      </Title>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <DatePicker
            picker="month"
            value={mesReferencia}
            format="MM/YYYY"
            allowClear={false}
            onChange={(data) => {
              setMesReferencia(data ?? dayjs())
              setPagina(0)
            }}
          />
        </Flex>

        <Table<ComprasFornecedorIndicadorResponse>
          rowKey={(item) => `${item.anoMes}-${item.fornecedorId}`}
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} fornecedor(es)`,
          }}
          locale={{ emptyText: 'Nenhum snapshot pra este mês — rode o ETL em Business Intelligence > Execução do ETL' }}
          columns={[
            { title: 'Fornecedor', key: 'fornecedor', render: (_, item) => item.fornecedorNome ?? `#${item.fornecedorId}` },
            { title: 'Qtd. de pedidos', dataIndex: 'quantidadePedidos', align: 'right' },
            {
              title: 'Valor total comprado',
              dataIndex: 'valorTotalComprado',
              align: 'right',
              render: (v: number) => `R$ ${v.toFixed(2)}`,
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
