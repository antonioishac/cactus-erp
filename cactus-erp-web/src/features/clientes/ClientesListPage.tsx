import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Input, Select, Table, Typography } from 'antd'
import { PlusOutlined, ShopOutlined, UserOutlined } from '@ant-design/icons'
import SituacaoTag from '@shared/ui/SituacaoTag'
import { formatarDocumento } from '@shared/domain/pessoaUtils'
import { useClientes } from './queries'
import type { ClienteResponse, ClientesFiltro } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function ClientesListPage() {
  const navigate = useNavigate()
  const [filtro, setFiltro] = useState<ClientesFiltro>({})
  const [pagina, setPagina] = useState(0)

  const { data, isLoading, isPlaceholderData } = useClientes(filtro, pagina, TAMANHO_PAGINA)

  function atualizarFiltro(parcial: Partial<ClientesFiltro>) {
    setFiltro((atual) => ({ ...atual, ...parcial }))
    setPagina(0)
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Clientes
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/cadastro-base/clientes/novo')}>
          Novo Cliente
        </Button>
      </Flex>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <Input.Search
            placeholder="Buscar por nome ou documento"
            allowClear
            style={{ maxWidth: 320 }}
            onSearch={(valor) => atualizarFiltro({ nome: valor || undefined, documento: valor || undefined })}
          />
          <Select
            placeholder="Tipo"
            allowClear
            style={{ width: 160 }}
            options={[
              { value: 'FISICA', label: 'Pessoa Física' },
              { value: 'JURIDICA', label: 'Pessoa Jurídica' },
            ]}
            onChange={(valor) => atualizarFiltro({ tipoPessoa: valor })}
          />
          <Select
            placeholder="Situação"
            allowClear
            style={{ width: 160 }}
            options={[
              { value: 'ATIVO', label: 'Ativo' },
              { value: 'INATIVO', label: 'Inativo' },
            ]}
            onChange={(valor) => atualizarFiltro({ situacao: valor })}
          />
        </Flex>

        <Table<ClienteResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(cliente) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/cadastro-base/clientes/${cliente.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} cliente(s)`,
          }}
          columns={[
            {
              title: 'Nome / Razão Social',
              dataIndex: 'nome',
              render: (nome: string, cliente) => (
                <Flex align="center" gap={8}>
                  {cliente.tipoPessoa === 'FISICA' ? <UserOutlined /> : <ShopOutlined />}
                  {nome}
                </Flex>
              ),
            },
            {
              title: 'Documento',
              key: 'documento',
              render: (_, cliente) => formatarDocumento(cliente.documento, cliente.tipoPessoa),
            },
            {
              title: 'Cidade/UF',
              key: 'cidade',
              render: (_, cliente) => {
                const principal = cliente.enderecos.find((e) => e.principal) ?? cliente.enderecos[0]
                return principal?.municipio ? `${principal.municipio}/${principal.uf ?? ''}` : '—'
              },
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
