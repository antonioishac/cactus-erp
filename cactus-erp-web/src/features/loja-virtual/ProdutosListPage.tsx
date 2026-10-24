import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Table, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import SituacaoTag from '@shared/ui/SituacaoTag'
import SeletorLoja from './SeletorLoja'
import { useProdutos } from './queries'
import type { ProdutoLojaResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function ProdutosListPage() {
  const navigate = useNavigate()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'LOJA_GERENCIAR')
  const [lojaId, setLojaId] = useState<number | undefined>(undefined)
  const [pagina, setPagina] = useState(0)

  const { data, isLoading, isPlaceholderData } = useProdutos(lojaId, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Produtos
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar produtos'}>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            disabled={!podeGerenciar}
            onClick={() => navigate('/loja-virtual/produtos/novo')}
          >
            Novo Produto
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Flex style={{ marginBottom: 16 }}>
          <SeletorLoja
            value={lojaId}
            onChange={(v) => {
              setLojaId(v)
              setPagina(0)
            }}
          />
        </Flex>

        <Table<ProdutoLojaResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(produto) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/loja-virtual/produtos/${produto.id}`),
          })}
          locale={{ emptyText: lojaId ? 'Nenhum produto' : 'Selecione uma loja (ou veja todos)' }}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} produto(s)`,
          }}
          columns={[
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Categoria', dataIndex: 'categoriaLojaNome' },
            { title: 'Marca', dataIndex: 'marca', render: (v) => v || '—' },
            { title: 'Variantes', key: 'variantes', render: (_, p) => p.variantes.length },
            { title: 'Situação', dataIndex: 'situacao', render: (v) => <SituacaoTag situacao={v} /> },
          ]}
        />
      </Card>
    </Flex>
  )
}
