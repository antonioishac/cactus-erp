import { useState } from 'react'
import { App, Button, Card, Flex, Popconfirm, Space, Table, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import SituacaoTag from '@shared/ui/SituacaoTag'
import OperacaoFiscalModal from './modals/OperacaoFiscalModal'
import { useExcluirOperacaoFiscal, useOperacoesFiscaisPaginado } from './queries'
import type { OperacaoFiscalResponse } from './types'

const { Title, Text } = Typography
const TAMANHO_PAGINA = 10

export default function OperacoesFiscaisListPage() {
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeCriar = hasAuthority(authorities, 'CADASTRO_TRIBUTACAO_CRIAR')
  const podeEditar = hasAuthority(authorities, 'CADASTRO_TRIBUTACAO_EDITAR')
  const podeExcluir = hasAuthority(authorities, 'CADASTRO_TRIBUTACAO_EXCLUIR')
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)
  const [operacaoEmEdicao, setOperacaoEmEdicao] = useState<OperacaoFiscalResponse | undefined>(undefined)

  const { data, isLoading, isPlaceholderData } = useOperacoesFiscaisPaginado(pagina, TAMANHO_PAGINA)
  const excluir = useExcluirOperacaoFiscal()

  async function handleExcluir(id: number) {
    try {
      await excluir.mutateAsync(id)
      message.success('Operação fiscal excluída.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir operação fiscal.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <div>
          <Title level={3} style={{ margin: 0 }}>
            Operações Fiscais
          </Title>
          <Text type="secondary">Tipo de venda/operação (ex: "Venda a consumidor final", "Revenda", "Interestadual") — cada uma com seu CFOP.</Text>
        </div>
        <Tooltip title={podeCriar ? undefined : 'Você não tem permissão para criar operações fiscais'}>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            disabled={!podeCriar}
            onClick={() => {
              setOperacaoEmEdicao(undefined)
              setModalAberto(true)
            }}
          >
            Nova Operação Fiscal
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Table<OperacaoFiscalResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} operações fiscais`,
          }}
          columns={[
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'CFOP', dataIndex: 'cfopCodigo', render: (v) => v ?? '—' },
            { title: 'ISS configurado', dataIndex: 'issConfiguracao', render: (v) => (v ? 'Sim' : 'Não') },
            { title: 'Situação', dataIndex: 'situacao', render: (situacao) => <SituacaoTag situacao={situacao} /> },
            ...(podeEditar || podeExcluir
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    width: 160,
                    render: (_: unknown, operacao: OperacaoFiscalResponse) => (
                      <Space>
                        {podeEditar && (
                          <Button
                            size="small"
                            onClick={() => {
                              setOperacaoEmEdicao(operacao)
                              setModalAberto(true)
                            }}
                          >
                            Editar
                          </Button>
                        )}
                        {podeExcluir && (
                          <Popconfirm title="Excluir esta operação fiscal?" onConfirm={() => handleExcluir(operacao.id)}>
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

      <OperacaoFiscalModal open={modalAberto} valorInicial={operacaoEmEdicao} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
