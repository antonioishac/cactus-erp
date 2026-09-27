import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Input, Select, Table, Typography } from 'antd'
import { PlusOutlined, ShopOutlined, UserOutlined } from '@ant-design/icons'
import SituacaoTag from '@shared/ui/SituacaoTag'
import { formatarDocumento } from '@shared/domain/pessoaUtils'
import { useCargos, useColaboradores, useSetores } from './queries'
import type { ColaboradorResponse, ColaboradoresFiltro } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function ColaboradoresListPage() {
  const navigate = useNavigate()
  const [filtro, setFiltro] = useState<ColaboradoresFiltro>({})
  const [pagina, setPagina] = useState(0)

  const { data, isLoading, isPlaceholderData } = useColaboradores(filtro, pagina, TAMANHO_PAGINA)
  const { data: cargos } = useCargos()
  const { data: setores } = useSetores()

  function atualizarFiltro(parcial: Partial<ColaboradoresFiltro>) {
    setFiltro((atual) => ({ ...atual, ...parcial }))
    setPagina(0)
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Colaboradores
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/cadastro-base/colaboradores/novo')}>
          Novo Colaborador
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
            placeholder="Cargo"
            allowClear
            showSearch
            optionFilterProp="label"
            style={{ width: 180 }}
            options={cargos?.map((c) => ({ value: c.id, label: c.nome }))}
            onChange={(valor) => atualizarFiltro({ cargoId: valor })}
          />
          <Select
            placeholder="Setor"
            allowClear
            showSearch
            optionFilterProp="label"
            style={{ width: 180 }}
            options={setores?.map((s) => ({ value: s.id, label: s.nome }))}
            onChange={(valor) => atualizarFiltro({ setorId: valor })}
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

        <Table<ColaboradorResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(colaborador) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/cadastro-base/colaboradores/${colaborador.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} colaborador(es)`,
          }}
          columns={[
            {
              title: 'Nome',
              dataIndex: 'nome',
              render: (nome: string, colaborador) => (
                <Flex align="center" gap={8}>
                  {colaborador.tipoPessoa === 'FISICA' ? <UserOutlined /> : <ShopOutlined />}
                  {nome}
                </Flex>
              ),
            },
            {
              title: 'Documento',
              key: 'documento',
              render: (_, colaborador) => formatarDocumento(colaborador.documento, colaborador.tipoPessoa),
            },
            { title: 'Cargo', dataIndex: 'cargoNome', render: (v) => v || '—' },
            { title: 'Setor', dataIndex: 'setorNome', render: (v) => v || '—' },
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
