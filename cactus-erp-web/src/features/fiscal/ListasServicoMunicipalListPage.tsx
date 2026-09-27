import { useState } from 'react'
import { App, Button, Card, Flex, Popconfirm, Space, Table, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import ListaServicoMunicipalModal from './modals/ListaServicoMunicipalModal'
import { useExcluirListaServico, useListasServicoPaginado } from './queries'
import type { ListaServicoMunicipalResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function ListasServicoMunicipalListPage() {
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'FISCAL_GERENCIAR')
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)
  const [itemEmEdicao, setItemEmEdicao] = useState<ListaServicoMunicipalResponse | undefined>(undefined)

  const { data, isLoading, isPlaceholderData } = useListasServicoPaginado(pagina, TAMANHO_PAGINA)
  const excluir = useExcluirListaServico()

  async function handleExcluir(id: number) {
    try {
      await excluir.mutateAsync(id)
      message.success('Item da lista de serviço excluído.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir item.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Lista de Serviço Municipal
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar a lista de serviço'}>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            disabled={!podeGerenciar}
            onClick={() => {
              setItemEmEdicao(undefined)
              setModalAberto(true)
            }}
          >
            Novo Item
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Table<ListaServicoMunicipalResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} item(ns)`,
          }}
          columns={[
            { title: 'Código', dataIndex: 'codigo' },
            { title: 'Descrição', dataIndex: 'descricao' },
            { title: 'Código de tributação', dataIndex: 'codigoTributacaoMunicipio', render: (v) => v || '—' },
            {
              title: 'Alíquota ISS padrão',
              dataIndex: 'aliquotaIssPadrao',
              align: 'right',
              render: (v?: number) => (v != null ? `${v}%` : '—'),
            },
            ...(podeGerenciar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    width: 160,
                    render: (_: unknown, item: ListaServicoMunicipalResponse) => (
                      <Space>
                        <Button
                          size="small"
                          onClick={() => {
                            setItemEmEdicao(item)
                            setModalAberto(true)
                          }}
                        >
                          Editar
                        </Button>
                        <Popconfirm title="Excluir este item?" onConfirm={() => handleExcluir(item.id)}>
                          <Button size="small" danger>
                            Excluir
                          </Button>
                        </Popconfirm>
                      </Space>
                    ),
                  },
                ]
              : []),
          ]}
        />
      </Card>

      <ListaServicoMunicipalModal open={modalAberto} valorInicial={itemEmEdicao} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
