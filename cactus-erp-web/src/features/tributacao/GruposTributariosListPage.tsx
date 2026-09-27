import { useState } from 'react'
import { App, Button, Card, Flex, Popconfirm, Space, Table, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import SituacaoTag from '@shared/ui/SituacaoTag'
import GrupoTributarioModal from './modals/GrupoTributarioModal'
import { useExcluirGrupoTributario, useGruposTributariosPaginado } from './queries'
import type { GrupoTributarioResponse } from './types'

const { Title, Text } = Typography
const TAMANHO_PAGINA = 10

export default function GruposTributariosListPage() {
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeCriar = hasAuthority(authorities, 'CADASTRO_TRIBUTACAO_CRIAR')
  const podeEditar = hasAuthority(authorities, 'CADASTRO_TRIBUTACAO_EDITAR')
  const podeExcluir = hasAuthority(authorities, 'CADASTRO_TRIBUTACAO_EXCLUIR')
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)
  const [grupoEmEdicao, setGrupoEmEdicao] = useState<GrupoTributarioResponse | undefined>(undefined)

  const { data, isLoading, isPlaceholderData } = useGruposTributariosPaginado(pagina, TAMANHO_PAGINA)
  const excluir = useExcluirGrupoTributario()

  async function handleExcluir(id: number) {
    try {
      await excluir.mutateAsync(id)
      message.success('Grupo tributário excluído.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir grupo tributário.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <div>
          <Title level={3} style={{ margin: 0 }}>
            Grupos Tributários
          </Title>
          <Text type="secondary">Perfil tributário do produto (ex: "Revenda sujeita a ICMS-ST") — combinado com uma Operação Fiscal define a alíquota.</Text>
        </div>
        <Tooltip title={podeCriar ? undefined : 'Você não tem permissão para criar grupos tributários'}>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            disabled={!podeCriar}
            onClick={() => {
              setGrupoEmEdicao(undefined)
              setModalAberto(true)
            }}
          >
            Novo Grupo Tributário
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Table<GrupoTributarioResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} grupos tributários`,
          }}
          columns={[
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Origem da mercadoria', dataIndex: 'origemMercadoria' },
            { title: 'Situação', dataIndex: 'situacao', render: (situacao) => <SituacaoTag situacao={situacao} /> },
            ...(podeEditar || podeExcluir
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    width: 160,
                    render: (_: unknown, grupo: GrupoTributarioResponse) => (
                      <Space>
                        {podeEditar && (
                          <Button
                            size="small"
                            onClick={() => {
                              setGrupoEmEdicao(grupo)
                              setModalAberto(true)
                            }}
                          >
                            Editar
                          </Button>
                        )}
                        {podeExcluir && (
                          <Popconfirm title="Excluir este grupo tributário?" onConfirm={() => handleExcluir(grupo.id)}>
                            <Button size="small" danger>
                              Excluir
                            </Button>
                          </Popconfirm>
                        )}
                      </Space>
                    ),
                  },
                ]
              : []),
          ]}
        />
      </Card>

      <GrupoTributarioModal open={modalAberto} valorInicial={grupoEmEdicao} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
