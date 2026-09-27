import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Input, Select, Table, Typography } from 'antd'
import { CarOutlined, PlusOutlined, ShopOutlined, UserOutlined } from '@ant-design/icons'
import SituacaoTag from '@shared/ui/SituacaoTag'
import { formatarDocumento } from '@shared/domain/pessoaUtils'
import { useTransportadoras } from './queries'
import type { TransportadoraResponse, TransportadorasFiltro } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function TransportadorasListPage() {
  const navigate = useNavigate()
  const [filtro, setFiltro] = useState<TransportadorasFiltro>({})
  const [pagina, setPagina] = useState(0)

  const { data, isLoading, isPlaceholderData } = useTransportadoras(filtro, pagina, TAMANHO_PAGINA)

  function atualizarFiltro(parcial: Partial<TransportadorasFiltro>) {
    setFiltro((atual) => ({ ...atual, ...parcial }))
    setPagina(0)
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          <CarOutlined style={{ marginRight: 8 }} />
          Transportadoras
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/cadastro-base/transportadoras/novo')}>
          Nova Transportadora
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

        <Table<TransportadoraResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(transportadora) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/cadastro-base/transportadoras/${transportadora.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} transportadora(s)`,
          }}
          columns={[
            {
              title: 'Nome / Razão Social',
              dataIndex: 'nome',
              render: (nome: string, transportadora) => (
                <Flex align="center" gap={8}>
                  {transportadora.tipoPessoa === 'FISICA' ? <UserOutlined /> : <ShopOutlined />}
                  {nome}
                </Flex>
              ),
            },
            {
              title: 'Documento',
              key: 'documento',
              render: (_, transportadora) => formatarDocumento(transportadora.documento, transportadora.tipoPessoa),
            },
            {
              title: 'RNTRC',
              dataIndex: 'rntrc',
              render: (rntrc?: string) => rntrc || '—',
            },
            {
              title: 'Cidade/UF',
              key: 'cidade',
              render: (_, transportadora) => {
                const principal = transportadora.enderecos.find((e) => e.principal) ?? transportadora.enderecos[0]
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
