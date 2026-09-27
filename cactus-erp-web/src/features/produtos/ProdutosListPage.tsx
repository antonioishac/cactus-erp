import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Input, Table, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import SituacaoTag from '@shared/ui/SituacaoTag'
import { useProdutos } from './queries'
import type { ProdutoResponse, ProdutosFiltro } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function ProdutosListPage() {
  const navigate = useNavigate()
  const [filtro, setFiltro] = useState<ProdutosFiltro>({})
  const [pagina, setPagina] = useState(0)

  const { data, isLoading, isPlaceholderData } = useProdutos(filtro, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Produtos
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/cadastro-base/produtos/novo')}>
          Novo Produto
        </Button>
      </Flex>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <Input.Search
            placeholder="Buscar por nome"
            allowClear
            style={{ maxWidth: 320 }}
            onSearch={(valor) => {
              setFiltro((f) => ({ ...f, nome: valor || undefined }))
              setPagina(0)
            }}
          />
          <Input.Search
            placeholder="Buscar por código interno"
            allowClear
            style={{ maxWidth: 260 }}
            onSearch={(valor) => {
              setFiltro((f) => ({ ...f, codigoInterno: valor || undefined }))
              setPagina(0)
            }}
          />
          <Input.Search
            placeholder="Buscar por GTIN"
            allowClear
            style={{ maxWidth: 220 }}
            onSearch={(valor) => {
              setFiltro((f) => ({ ...f, gtin: valor || undefined }))
              setPagina(0)
            }}
          />
        </Flex>

        <Table<ProdutoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(produto) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/cadastro-base/produtos/${produto.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} produto(s)`,
          }}
          columns={[
            { title: 'Código', dataIndex: 'codigoInterno', width: 120 },
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Subgrupo', dataIndex: 'produtoSubgrupoNome', render: (v) => v || '—' },
            { title: 'Marca', dataIndex: 'produtoMarcaNome', render: (v) => v || '—' },
            {
              title: 'Valor de venda',
              dataIndex: 'valorVenda',
              render: (v?: number) => (v != null ? `R$ ${v.toFixed(2)}` : '—'),
            },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (situacao) => <SituacaoTag situacao={situacao} />,
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
