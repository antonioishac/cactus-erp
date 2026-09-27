import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Input, Table, Tag, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import SituacaoTag from '@shared/ui/SituacaoTag'
import { useTabelasPreco } from './queries'
import { TIPO_AJUSTE_PRECO_LABEL } from './types'
import type { TabelaPrecoResponse, TabelasPrecoFiltro } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function TabelasPrecoListPage() {
  const navigate = useNavigate()
  const [filtro, setFiltro] = useState<TabelasPrecoFiltro>({})
  const [pagina, setPagina] = useState(0)

  const { data, isLoading, isPlaceholderData } = useTabelasPreco(filtro, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Tabelas de Preço
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/cadastro-base/tabelas-preco/novo')}>
          Nova Tabela de Preço
        </Button>
      </Flex>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <Input.Search
            placeholder="Buscar por nome"
            allowClear
            style={{ maxWidth: 320 }}
            onSearch={(valor) => {
              setFiltro({ nome: valor || undefined })
              setPagina(0)
            }}
          />
        </Flex>

        <Table<TabelaPrecoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(tabela) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/cadastro-base/tabelas-preco/${tabela.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} tabela(s)`,
          }}
          columns={[
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Descrição', dataIndex: 'descricao', render: (v) => v || '—' },
            {
              title: 'Tipo de ajuste',
              dataIndex: 'tipoAjuste',
              render: (v: keyof typeof TIPO_AJUSTE_PRECO_LABEL) => (
                <Tag color={v === 'VALOR_FIXO' ? 'blue' : 'purple'}>{v === 'VALOR_FIXO' ? 'Valor fixo' : '% sobre venda'}</Tag>
              ),
            },
            { title: 'Produtos', key: 'produtos', render: (_, tabela) => tabela.produtos.length },
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
