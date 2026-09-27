import { useState } from 'react'
import { App, Button, Card, Flex, Popconfirm, Space, Table, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import PastaModal from './modals/PastaModal'
import { useExcluirPasta, usePastas } from './queries'
import type { PastaDocumentoResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function PastasListPage() {
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'GED_GERENCIAR')
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)
  const [pastaEmEdicao, setPastaEmEdicao] = useState<PastaDocumentoResponse | undefined>(undefined)

  const { data, isLoading, isPlaceholderData } = usePastas(pagina, TAMANHO_PAGINA)
  const excluir = useExcluirPasta()

  async function handleExcluir(id: number) {
    try {
      await excluir.mutateAsync(id)
      message.success('Pasta excluída.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir pasta.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Pastas
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar pastas'}>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            disabled={!podeGerenciar}
            onClick={() => {
              setPastaEmEdicao(undefined)
              setModalAberto(true)
            }}
          >
            Nova Pasta
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Table<PastaDocumentoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} pasta(s)`,
          }}
          columns={[
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Descrição', dataIndex: 'descricao', render: (v) => v || '—' },
            { title: 'Pasta pai', dataIndex: 'pastaPaiNome', render: (v) => v || '— (raiz)' },
            ...(podeGerenciar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    width: 120,
                    render: (_: unknown, pasta: PastaDocumentoResponse) => (
                      <Space>
                        <Button
                          size="small"
                          onClick={() => {
                            setPastaEmEdicao(pasta)
                            setModalAberto(true)
                          }}
                        >
                          Editar
                        </Button>
                        <Popconfirm title="Excluir esta pasta?" onConfirm={() => handleExcluir(pasta.id)}>
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

      <PastaModal open={modalAberto} valorInicial={pastaEmEdicao} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
