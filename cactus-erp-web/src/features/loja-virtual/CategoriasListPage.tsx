import { useState } from 'react'
import { App, Button, Card, Flex, Popconfirm, Space, Table, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import SituacaoTag from '@shared/ui/SituacaoTag'
import SeletorLoja from './SeletorLoja'
import CategoriaModal from './modals/CategoriaModal'
import { useCategorias, useExcluirCategoria } from './queries'
import type { CategoriaLojaResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function CategoriasListPage() {
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'LOJA_GERENCIAR')
  const [lojaId, setLojaId] = useState<number | undefined>(undefined)
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)
  const [categoriaEmEdicao, setCategoriaEmEdicao] = useState<CategoriaLojaResponse | undefined>(undefined)

  const { data, isLoading, isPlaceholderData } = useCategorias(lojaId, pagina, TAMANHO_PAGINA)
  const excluir = useExcluirCategoria()

  async function handleExcluir(id: number) {
    try {
      await excluir.mutateAsync(id)
      message.success('Categoria excluída.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir categoria.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Categorias
        </Title>
        <Tooltip
          title={
            !podeGerenciar
              ? 'Você não tem permissão para gerenciar categorias'
              : !lojaId
                ? 'Selecione uma loja primeiro'
                : undefined
          }
        >
          <Button
            type="primary"
            icon={<PlusOutlined />}
            disabled={!podeGerenciar || !lojaId}
            onClick={() => {
              setCategoriaEmEdicao(undefined)
              setModalAberto(true)
            }}
          >
            Nova Categoria
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

        <Table<CategoriaLojaResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          locale={{ emptyText: lojaId ? 'Nenhuma categoria' : 'Selecione uma loja pra ver as categorias' }}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} categoria(s)`,
          }}
          columns={[
            { title: 'Nome', dataIndex: 'nome' },
            {
              title: 'Categoria pai',
              key: 'categoriaPai',
              render: (_, c) =>
                c.categoriaPaiId
                  ? data?.conteudo.find((x) => x.id === c.categoriaPaiId)?.nome ?? `#${c.categoriaPaiId}`
                  : '— (raiz)',
            },
            { title: 'Ordem', dataIndex: 'ordem' },
            { title: 'Situação', dataIndex: 'situacao', render: (v) => <SituacaoTag situacao={v} /> },
            ...(podeGerenciar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    width: 160,
                    render: (_: unknown, categoria: CategoriaLojaResponse) => (
                      <Space>
                        <Button
                          size="small"
                          onClick={() => {
                            setCategoriaEmEdicao(categoria)
                            setModalAberto(true)
                          }}
                        >
                          Editar
                        </Button>
                        <Popconfirm title="Excluir esta categoria?" onConfirm={() => handleExcluir(categoria.id)}>
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

      {lojaId && (
        <CategoriaModal open={modalAberto} lojaId={lojaId} valorInicial={categoriaEmEdicao} onClose={() => setModalAberto(false)} />
      )}
    </Flex>
  )
}
