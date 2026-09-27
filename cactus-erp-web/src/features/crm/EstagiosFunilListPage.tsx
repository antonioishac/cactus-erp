import { useState } from 'react'
import { App, Button, Card, Flex, Popconfirm, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import SituacaoTag from '@shared/ui/SituacaoTag'
import EstagioFunilModal from './modals/EstagioFunilModal'
import { useEstagiosFunilPaginado, useExcluirEstagioFunil } from './queries'
import { TIPO_ENCERRAMENTO_FUNIL_LABEL } from './types'
import type { EstagioFunilResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_TIPO_ENCERRAMENTO: Record<string, string> = {
  ABERTO: 'default',
  GANHO: 'success',
  PERDIDO: 'error',
}

export default function EstagiosFunilListPage() {
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'CRM_GERENCIAR')
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)
  const [estagioEmEdicao, setEstagioEmEdicao] = useState<EstagioFunilResponse | undefined>(undefined)

  const { data, isLoading, isPlaceholderData } = useEstagiosFunilPaginado(pagina, TAMANHO_PAGINA)
  const excluir = useExcluirEstagioFunil()

  async function handleExcluir(id: number) {
    try {
      await excluir.mutateAsync(id)
      message.success('Estágio excluído.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir estágio.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Estágios do Funil
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar o funil'}>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            disabled={!podeGerenciar}
            onClick={() => {
              setEstagioEmEdicao(undefined)
              setModalAberto(true)
            }}
          >
            Novo Estágio
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Table<EstagioFunilResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} estágio(s)`,
          }}
          columns={[
            { title: 'Ordem', dataIndex: 'ordem', width: 80 },
            { title: 'Código', dataIndex: 'codigo', render: (v?: string) => v || '—' },
            { title: 'Nome', dataIndex: 'nome' },
            {
              title: 'Tipo de encerramento',
              dataIndex: 'tipoEncerramento',
              render: (v: keyof typeof TIPO_ENCERRAMENTO_FUNIL_LABEL) => (
                <Tag color={COR_TIPO_ENCERRAMENTO[v]}>{TIPO_ENCERRAMENTO_FUNIL_LABEL[v]}</Tag>
              ),
            },
            { title: 'Situação', dataIndex: 'situacao', render: (situacao) => <SituacaoTag situacao={situacao} /> },
            ...(podeGerenciar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    width: 160,
                    render: (_: unknown, estagio: EstagioFunilResponse) => (
                      <Space>
                        <Button
                          size="small"
                          onClick={() => {
                            setEstagioEmEdicao(estagio)
                            setModalAberto(true)
                          }}
                        >
                          Editar
                        </Button>
                        <Popconfirm title="Excluir este estágio?" onConfirm={() => handleExcluir(estagio.id)}>
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

      <EstagioFunilModal open={modalAberto} valorInicial={estagioEmEdicao} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
