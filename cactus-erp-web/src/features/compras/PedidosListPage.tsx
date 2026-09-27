import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Table, Tag, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { FornecedorResumoCell } from './ResumoCells'
import { useBuscaFornecedoresParaSelecao, usePedidos } from './queries'
import { ORIGEM_PEDIDO_LABEL, SITUACAO_PEDIDO_LABEL, TIPO_PEDIDO_LABEL } from './types'
import type { PedidoCompraResponse, PedidosFiltro } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_SITUACAO: Record<string, string> = {
  RASCUNHO: 'default',
  AGUARDANDO_APROVACAO: 'warning',
  ENVIADO: 'processing',
  PARCIALMENTE_RECEBIDO: 'gold',
  RECEBIDO_TOTAL: 'success',
  CANCELADO: 'error',
}

export default function PedidosListPage() {
  const navigate = useNavigate()
  const [filtro, setFiltro] = useState<PedidosFiltro>({})
  const [pagina, setPagina] = useState(0)
  const [termoFornecedor, setTermoFornecedor] = useState<string | undefined>(undefined)

  const { data: fornecedores, isLoading: carregandoFornecedores } = useBuscaFornecedoresParaSelecao(termoFornecedor)
  const { data, isLoading, isPlaceholderData } = usePedidos(filtro, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Pedidos de Compra
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/compras/pedidos/novo')}>
          Novo Pedido Avulso
        </Button>
      </Flex>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <AsyncSelect
            allowClear
            style={{ width: 320 }}
            placeholder="Filtrar por fornecedor"
            opcoes={fornecedores}
            carregando={carregandoFornecedores}
            termo={termoFornecedor}
            onTermoChange={setTermoFornecedor}
            toOption={(f) => ({ value: f.id, label: f.nome })}
            onChange={(fornecedorId: number | undefined) => {
              setFiltro((f) => ({ ...f, fornecedorId }))
              setPagina(0)
            }}
          />
        </Flex>

        <Table<PedidoCompraResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(pedido) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/compras/pedidos/${pedido.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} pedido(s)`,
          }}
          columns={[
            { title: 'Id', dataIndex: 'id', width: 80 },
            { title: 'Fornecedor', key: 'fornecedor', render: (_, p) => <FornecedorResumoCell fornecedorId={p.fornecedorId} /> },
            { title: 'Tipo', dataIndex: 'tipo', render: (tipo: keyof typeof TIPO_PEDIDO_LABEL) => TIPO_PEDIDO_LABEL[tipo] },
            { title: 'Origem', dataIndex: 'origemTipo', render: (origem: keyof typeof ORIGEM_PEDIDO_LABEL) => ORIGEM_PEDIDO_LABEL[origem] },
            { title: 'Valor total', dataIndex: 'valorTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (situacao: keyof typeof SITUACAO_PEDIDO_LABEL) => (
                <Tag color={COR_SITUACAO[situacao]}>{SITUACAO_PEDIDO_LABEL[situacao]}</Tag>
              ),
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
