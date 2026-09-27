import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Card, Flex, Select, Table, Tag, Typography } from 'antd'
import dayjs from 'dayjs'
import { ColaboradorResumoCell } from './ResumoCells'
import { useComissoes, useVendedores } from './queries'
import { SITUACAO_COMISSAO_LABEL, TIPO_PAGAMENTO_VENDA_LABEL } from './types'
import type { ComissoesFiltro, VendaComissaoResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_SITUACAO: Record<string, string> = {
  PENDENTE: 'gold',
  PAGO: 'success',
  CANCELADO: 'error',
}

/** Só consulta — `VendaComissao` é gerada automaticamente por `VendaService#confirmar` (vendas-service), sem `POST`/`PUT` na API. */
export default function ComissoesListPage() {
  const navigate = useNavigate()
  const [filtro, setFiltro] = useState<ComissoesFiltro>({})
  const [pagina, setPagina] = useState(0)
  const { data, isLoading, isPlaceholderData } = useComissoes(filtro, pagina, TAMANHO_PAGINA)
  const { data: vendedores } = useVendedores(0, 100)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Comissões
      </Title>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <Select
            placeholder="Filtrar por vendedor"
            allowClear
            style={{ minWidth: 240 }}
            options={vendedores?.conteudo.map((v) => ({ value: v.id, label: v.comissaoPerfilNome ?? `Vendedor #${v.id}` }))}
            onChange={(valor) => {
              setFiltro({ vendedorId: valor })
              setPagina(0)
            }}
          />
        </Flex>

        <Table<VendaComissaoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(comissao) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/vendas/vendas/${comissao.vendaId}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} comissão(ões)`,
          }}
          columns={[
            { title: 'Venda', dataIndex: 'vendaId', render: (v: number) => `#${v}` },
            {
              title: 'Vendedor',
              key: 'vendedor',
              render: (_, comissao) => {
                const vendedor = vendedores?.conteudo.find((v) => v.id === comissao.vendedorId)
                return vendedor ? <ColaboradorResumoCell colaboradorId={vendedor.colaboradorId} /> : `#${comissao.vendedorId}`
              },
            },
            { title: 'Data', dataIndex: 'dataLancamento', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
            {
              title: 'Tipo',
              dataIndex: 'tipoContabil',
              render: (v: keyof typeof TIPO_PAGAMENTO_VENDA_LABEL) => TIPO_PAGAMENTO_VENDA_LABEL[v],
            },
            { title: 'Valor da venda', dataIndex: 'valorVenda', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Valor da comissão', dataIndex: 'valorComissao', align: 'right', render: (v: number) => <strong>R$ {v.toFixed(2)}</strong> },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (situacao: keyof typeof SITUACAO_COMISSAO_LABEL) => (
                <Tag color={COR_SITUACAO[situacao]}>{SITUACAO_COMISSAO_LABEL[situacao]}</Tag>
              ),
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
