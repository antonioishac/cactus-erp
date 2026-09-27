import { useState } from 'react'
import { App, Button, Card, Empty, Flex, Popconfirm, Space, Table, Tooltip, Typography } from 'antd'
import { ExportOutlined, PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import AsyncSelect from '@shared/ui/AsyncSelect'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import BaixarKitModal from './modals/BaixarKitModal'
import ComponenteKitModal from './modals/ComponenteKitModal'
import { useBuscaProdutosParaSelecao, useComponentesKit, useRemoverComponenteKit } from './queries'
import type { ProdutoKitComponenteResponse } from './types'

const { Title } = Typography

/**
 * BOM simples — não existe um "cadastro de kit" à parte: qualquer produto do
 * cadastro-base pode virar kit só por ganhar componentes aqui (ver
 * `ProdutoKitController`, escopado por `produtoKitId` na própria URL, sem
 * flag "éKit" em `Produto`). Por isso a tela começa com uma busca de produto,
 * não uma listagem.
 */
export default function KitsPage() {
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'ESTOQUE_GERENCIAR')
  const podeMovimentar = hasAuthority(authorities, 'ESTOQUE_MOVIMENTAR')
  const [termoProduto, setTermoProduto] = useState<string | undefined>(undefined)
  const [produtoKitId, setProdutoKitId] = useState<number | undefined>(undefined)
  const [modalComponenteAberto, setModalComponenteAberto] = useState(false)
  const [modalBaixarAberto, setModalBaixarAberto] = useState(false)

  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termoProduto)
  const { data: componentes, isLoading: carregandoComponentes } = useComponentesKit(produtoKitId)
  const removerComponente = useRemoverComponenteKit(produtoKitId ?? -1)

  async function handleRemover(componenteId: number) {
    try {
      await removerComponente.mutateAsync(componenteId)
      message.success('Componente removido do kit.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao remover componente.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Kits / Produtos Compostos
      </Title>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <AsyncSelect
            id="filtroProdutoId"
            style={{ width: 360 }}
            opcoes={produtos}
            carregando={carregandoProdutos}
            termo={termoProduto}
            onTermoChange={setTermoProduto}
            toOption={(p) => ({ value: p.id, label: `${p.codigoInterno} — ${p.nome}` })}
            onChange={setProdutoKitId}
          />
        </Flex>

        {produtoKitId === undefined ? (
          <Empty description="Busque o produto que será vendido como kit/conjunto" />
        ) : (
          <>
            <Flex justify="flex-end" gap={12} style={{ marginBottom: 16 }}>
              <Tooltip title={podeMovimentar ? undefined : 'Você não tem permissão para movimentar estoque'}>
                <Button icon={<ExportOutlined />} disabled={!podeMovimentar} onClick={() => setModalBaixarAberto(true)}>
                  Baixar Kit
                </Button>
              </Tooltip>
              <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar kits'}>
                <Button type="primary" icon={<PlusOutlined />} disabled={!podeGerenciar} onClick={() => setModalComponenteAberto(true)}>
                  Adicionar Componente
                </Button>
              </Tooltip>
            </Flex>

            <Table<ProdutoKitComponenteResponse>
              rowKey="id"
              loading={carregandoComponentes}
              dataSource={componentes ?? []}
              pagination={false}
              locale={{ emptyText: 'Nenhum componente cadastrado — este produto ainda não funciona como kit' }}
              columns={[
                {
                  title: 'Componente',
                  key: 'componente',
                  render: (_, c) => <ProdutoResumoCell produtoId={c.produtoComponenteId} />,
                },
                { title: 'Quantidade por kit', dataIndex: 'quantidade', align: 'right' },
                ...(podeGerenciar
                  ? [
                      {
                        title: 'Ações',
                        key: 'acoes',
                        width: 100,
                        render: (_: unknown, c: ProdutoKitComponenteResponse) => (
                          <Space>
                            <Popconfirm title="Remover este componente do kit?" onConfirm={() => handleRemover(c.id)}>
                              <Button size="small" danger>
                                Remover
                              </Button>
                            </Popconfirm>
                          </Space>
                        ),
                      },
                    ]
                  : []),
              ]}
            />
          </>
        )}
      </Card>

      {produtoKitId !== undefined && (
        <>
          <ComponenteKitModal open={modalComponenteAberto} produtoKitId={produtoKitId} onClose={() => setModalComponenteAberto(false)} />
          <BaixarKitModal open={modalBaixarAberto} produtoKitId={produtoKitId} onClose={() => setModalBaixarAberto(false)} />
        </>
      )}
    </Flex>
  )
}
