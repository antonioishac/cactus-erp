import { useState } from 'react'
import { App, Button, Card, Flex, Popconfirm, Select, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import SeletorLoja from './SeletorLoja'
import AtributoCategoriaModal from './modals/AtributoCategoriaModal'
import GerenciarOpcoesModal from './modals/GerenciarOpcoesModal'
import { useAtributos, useExcluirAtributo, useTodasCategorias } from './queries'
import { TIPO_ATRIBUTO_LABEL } from './types'
import type { AtributoCategoriaResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function AtributosCategoriaListPage() {
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'LOJA_GERENCIAR')
  const [lojaId, setLojaId] = useState<number | undefined>(undefined)
  const [categoriaId, setCategoriaId] = useState<number | undefined>(undefined)
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)
  const [atributoEmEdicao, setAtributoEmEdicao] = useState<AtributoCategoriaResponse | undefined>(undefined)
  const [opcoesAtributoId, setOpcoesAtributoId] = useState<number | undefined>(undefined)

  const { data: categorias } = useTodasCategorias(lojaId)
  const { data, isLoading, isPlaceholderData } = useAtributos(categoriaId, pagina, TAMANHO_PAGINA)
  const excluir = useExcluirAtributo()

  const atributoComOpcoes = data?.conteudo.find((a) => a.id === opcoesAtributoId)

  async function handleExcluir(id: number) {
    try {
      await excluir.mutateAsync(id)
      message.success('Atributo excluído.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir atributo.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Atributos de Categoria
        </Title>
        <Tooltip
          title={
            !podeGerenciar
              ? 'Você não tem permissão para gerenciar atributos'
              : !categoriaId
                ? 'Selecione uma categoria primeiro'
                : undefined
          }
        >
          <Button
            type="primary"
            icon={<PlusOutlined />}
            disabled={!podeGerenciar || !categoriaId}
            onClick={() => {
              setAtributoEmEdicao(undefined)
              setModalAberto(true)
            }}
          >
            Novo Atributo
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Flex gap={12} style={{ marginBottom: 16 }}>
          <SeletorLoja
            value={lojaId}
            onChange={(v) => {
              setLojaId(v)
              setCategoriaId(undefined)
              setPagina(0)
            }}
          />
          <Select
            allowClear
            showSearch
            disabled={!lojaId}
            optionFilterProp="label"
            placeholder="Selecione a categoria"
            style={{ width: 260 }}
            value={categoriaId}
            onChange={(v) => {
              setCategoriaId(v)
              setPagina(0)
            }}
            options={categorias?.map((c) => ({ value: c.id, label: c.nome }))}
          />
        </Flex>

        <Table<AtributoCategoriaResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          locale={{ emptyText: categoriaId ? 'Nenhum atributo' : 'Selecione uma categoria pra ver os atributos' }}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} atributo(s)`,
          }}
          columns={[
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Tipo', dataIndex: 'tipo', render: (v: keyof typeof TIPO_ATRIBUTO_LABEL) => TIPO_ATRIBUTO_LABEL[v] },
            {
              title: 'Obrigatório',
              dataIndex: 'obrigatorio',
              render: (v: boolean) => <Tag color={v ? 'blue' : 'default'}>{v ? 'Sim' : 'Não'}</Tag>,
            },
            {
              title: 'Gera variação',
              dataIndex: 'geraVariacao',
              render: (v: boolean) => <Tag color={v ? 'green' : 'default'}>{v ? 'Sim' : 'Não'}</Tag>,
            },
            { title: 'Opções', key: 'opcoes', render: (_, a) => (a.tipo === 'LISTA' ? a.opcoes.length : '—') },
            {
              title: 'Ações',
              key: 'acoes',
              width: 260,
              render: (_: unknown, atributo: AtributoCategoriaResponse) => (
                <Space>
                  {atributo.tipo === 'LISTA' && (
                    <Button size="small" onClick={() => setOpcoesAtributoId(atributo.id)}>
                      Opções
                    </Button>
                  )}
                  {podeGerenciar && (
                    <>
                      <Button
                        size="small"
                        onClick={() => {
                          setAtributoEmEdicao(atributo)
                          setModalAberto(true)
                        }}
                      >
                        Editar
                      </Button>
                      <Popconfirm title="Excluir este atributo?" onConfirm={() => handleExcluir(atributo.id)}>
                        <Button size="small" danger>
                          Excluir
                        </Button>
                      </Popconfirm>
                    </>
                  )}
                </Space>
              ),
            },
          ]}
        />
      </Card>

      {categoriaId && (
        <AtributoCategoriaModal
          open={modalAberto}
          categoriaLojaId={categoriaId}
          valorInicial={atributoEmEdicao}
          onClose={() => setModalAberto(false)}
        />
      )}
      <GerenciarOpcoesModal
        open={opcoesAtributoId !== undefined}
        atributo={atributoComOpcoes}
        onClose={() => setOpcoesAtributoId(undefined)}
      />
    </Flex>
  )
}
