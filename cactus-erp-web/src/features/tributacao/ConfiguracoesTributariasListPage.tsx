import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { App, Button, Card, Flex, Popconfirm, Select, Space, Table, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useConfiguracoesTributariasPaginado, useExcluirConfiguracaoTributaria, useTodasOperacoesFiscais, useTodosGruposTributarios } from './queries'
import type { ConfiguracaoTributariaResponse } from './types'

const { Title, Text } = Typography
const TAMANHO_PAGINA = 10

/** A célula da matriz Grupo Tributário × Operação Fiscal — ver ANÁLISE TRIBUTÁRIA no CLAUDE.md raiz. */
export default function ConfiguracoesTributariasListPage() {
  const { message } = App.useApp()
  const navigate = useNavigate()
  const { authorities } = useAuth()
  const podeCriar = hasAuthority(authorities, 'CADASTRO_TRIBUTACAO_CRIAR')
  const podeExcluir = hasAuthority(authorities, 'CADASTRO_TRIBUTACAO_EXCLUIR')
  const [pagina, setPagina] = useState(0)
  const [grupoFiltro, setGrupoFiltro] = useState<number | undefined>(undefined)
  const [operacaoFiltro, setOperacaoFiltro] = useState<number | undefined>(undefined)

  const { data: grupos } = useTodosGruposTributarios()
  const { data: operacoes } = useTodasOperacoesFiscais()
  const { data, isLoading, isPlaceholderData } = useConfiguracoesTributariasPaginado(pagina, TAMANHO_PAGINA, grupoFiltro, operacaoFiltro)
  const excluir = useExcluirConfiguracaoTributaria()

  async function handleExcluir(id: number) {
    try {
      await excluir.mutateAsync(id)
      message.success('Configuração tributária excluída.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir configuração tributária.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <div>
          <Title level={3} style={{ margin: 0 }}>
            Configurações Tributárias
          </Title>
          <Text type="secondary">A célula Grupo Tributário × Operação Fiscal — onde a alíquota de fato é resolvida (ICMS por UF de destino, PIS/COFINS/IPI).</Text>
        </div>
        <Tooltip title={podeCriar ? undefined : 'Você não tem permissão para criar configurações tributárias'}>
          <Button type="primary" icon={<PlusOutlined />} disabled={!podeCriar} onClick={() => navigate('/cadastro-base/configuracoes-tributarias/novo')}>
            Nova Configuração
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Space style={{ marginBottom: 16 }} wrap>
          <Select
            allowClear
            placeholder="Filtrar por Grupo Tributário"
            style={{ width: 260 }}
            showSearch
            optionFilterProp="label"
            options={grupos?.map((g) => ({ value: g.id, label: g.nome }))}
            value={grupoFiltro}
            onChange={(v) => {
              setGrupoFiltro(v)
              setPagina(0)
            }}
          />
          <Select
            allowClear
            placeholder="Filtrar por Operação Fiscal"
            style={{ width: 260 }}
            showSearch
            optionFilterProp="label"
            options={operacoes?.map((o) => ({ value: o.id, label: o.nome }))}
            value={operacaoFiltro}
            onChange={(v) => {
              setOperacaoFiltro(v)
              setPagina(0)
            }}
          />
        </Space>

        <Table<ConfiguracaoTributariaResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} configurações`,
          }}
          columns={[
            { title: 'Grupo Tributário', dataIndex: 'grupoTributarioNome' },
            { title: 'Operação Fiscal', dataIndex: 'operacaoFiscalNome' },
            { title: 'UFs configuradas', dataIndex: 'icmsPorUf', render: (v: ConfiguracaoTributariaResponse['icmsPorUf']) => v.length },
            { title: 'PIS/COFINS/IPI', key: 'tributos', render: (_, c) => [c.pis && 'PIS', c.cofins && 'COFINS', c.ipi && 'IPI'].filter(Boolean).join(', ') || '—' },
            {
              title: 'Ações',
              key: 'acoes',
              width: 160,
              render: (_: unknown, configuracao: ConfiguracaoTributariaResponse) => (
                <Space>
                  <Button size="small" onClick={() => navigate(`/cadastro-base/configuracoes-tributarias/${configuracao.id}`)}>
                    Abrir
                  </Button>
                  {podeExcluir && (
                    <Popconfirm title="Excluir esta configuração tributária?" onConfirm={() => handleExcluir(configuracao.id)}>
                      <Button size="small" danger>
                        Excluir
                      </Button>
                    </Popconfirm>
                  )}
                </Space>
              ),
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
