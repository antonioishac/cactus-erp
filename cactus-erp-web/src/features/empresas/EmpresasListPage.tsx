import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Input, Table, Tag, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import SituacaoTag from '@shared/ui/SituacaoTag'
import { useEmpresas } from './queries'
import type { EmpresaResponse, EmpresasFiltro } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function EmpresasListPage() {
  const navigate = useNavigate()
  const [filtro, setFiltro] = useState<EmpresasFiltro>({})
  const [pagina, setPagina] = useState(0)

  const { data, isLoading, isPlaceholderData } = useEmpresas(filtro, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Empresas
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/cadastro-base/empresas/novo')}>
          Nova Empresa
        </Button>
      </Flex>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <Input.Search
            placeholder="Buscar por razão social ou fantasia"
            allowClear
            style={{ maxWidth: 320 }}
            onSearch={(valor) => {
              setFiltro({ nome: valor || undefined })
              setPagina(0)
            }}
          />
        </Flex>

        <Table<EmpresaResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(empresa) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/cadastro-base/empresas/${empresa.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} empresa(s)`,
          }}
          columns={[
            { title: 'Razão Social', dataIndex: 'razaoSocial' },
            { title: 'Nome Fantasia', dataIndex: 'nomeFantasia', render: (v) => v || '—' },
            { title: 'CNPJ', dataIndex: 'cnpj', render: (v) => v || '—' },
            {
              title: 'Tipo',
              dataIndex: 'tipo',
              render: (tipo?: string) => (tipo ? <Tag color={tipo === 'MATRIZ' ? 'blue' : 'default'}>{tipo === 'MATRIZ' ? 'Matriz' : 'Filial'}</Tag> : '—'),
            },
            { title: 'Cidade/UF', key: 'cidade', render: (_, empresa) => (empresa.municipio ? `${empresa.municipio}/${empresa.uf ?? ''}` : '—') },
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
