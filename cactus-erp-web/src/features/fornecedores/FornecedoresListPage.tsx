import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Input, Select, Table, Typography } from 'antd'
import { PlusOutlined, ShopOutlined, UserOutlined } from '@ant-design/icons'
import SituacaoTag from '@shared/ui/SituacaoTag'
import ExportarRelatorioButtons from '@shared/ui/ExportarRelatorioButtons'
import { formatarDocumento } from '@shared/domain/pessoaUtils'
import { exportarFornecedoresCadastro } from './api'
import { useFornecedores } from './queries'
import type { FornecedorResponse, FornecedoresFiltro } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function FornecedoresListPage() {
  const navigate = useNavigate()
  const [filtro, setFiltro] = useState<FornecedoresFiltro>({})
  const [pagina, setPagina] = useState(0)

  const { data, isLoading, isPlaceholderData } = useFornecedores(filtro, pagina, TAMANHO_PAGINA)

  function atualizarFiltro(parcial: Partial<FornecedoresFiltro>) {
    setFiltro((atual) => ({ ...atual, ...parcial }))
    setPagina(0)
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Fornecedores
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/cadastro-base/fornecedores/novo')}>
          Novo Fornecedor
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
          <ExportarRelatorioButtons onExportar={(formato) => exportarFornecedoresCadastro(filtro, formato)} />
        </Flex>

        <Table<FornecedorResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(fornecedor) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/cadastro-base/fornecedores/${fornecedor.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} fornecedor(es)`,
          }}
          columns={[
            {
              title: 'Nome / Razão Social',
              dataIndex: 'nome',
              render: (nome: string, fornecedor) => (
                <Flex align="center" gap={8}>
                  {fornecedor.tipoPessoa === 'FISICA' ? <UserOutlined /> : <ShopOutlined />}
                  {nome}
                </Flex>
              ),
            },
            {
              title: 'Documento',
              key: 'documento',
              render: (_, fornecedor) => formatarDocumento(fornecedor.documento, fornecedor.tipoPessoa),
            },
            {
              title: 'Cidade/UF',
              key: 'cidade',
              render: (_, fornecedor) => {
                const principal = fornecedor.enderecos.find((e) => e.principal) ?? fornecedor.enderecos[0]
                return principal?.municipio ? `${principal.municipio}/${principal.uf ?? ''}` : '—'
              },
            },
            {
              title: 'Prazo de entrega',
              dataIndex: 'prazoEntregaDias',
              render: (dias?: number) => (dias != null ? `${dias} dia(s)` : '—'),
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
