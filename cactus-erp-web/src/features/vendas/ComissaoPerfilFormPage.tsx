import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Col, Flex, Form, Input, Popconfirm, Row, Skeleton, Space, Switch, Table, Tooltip, Typography } from 'antd'
import { ArrowLeftOutlined, PlusOutlined, SaveOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import AdicionarObjetivoComissaoModal from './modals/AdicionarObjetivoComissaoModal'
import {
  useAtualizarComissaoPerfil,
  useComissaoPerfil,
  useCriarComissaoPerfil,
  useObjetivosComissaoPerfil,
  useRemoverObjetivoComissaoPerfil,
} from './queries'
import { TIPO_PAGAMENTO_VENDA_LABEL } from './types'
import type { ComissaoObjetivoResponse, ComissaoPerfilRequest } from './types'

const { Title } = Typography

const VALORES_PADRAO_NOVO: Partial<ComissaoPerfilRequest> = { situacao: 'ATIVO' }

/**
 * `ComissaoPerfilRequest` é flat (1 `Form`) — objetivos de comissão (meta por
 * produto) são sub-recurso, só aparecem em modo edição (precisam do id já
 * existente), mesmo critério de Rota/Metas em `VendedorFormPage`.
 */
export default function ComissaoPerfilFormPage() {
  const { id } = useParams<{ id: string }>()
  const perfilId = id ? Number(id) : undefined
  const modoEdicao = perfilId !== undefined
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<ComissaoPerfilRequest>()

  const { authorities } = useAuth()
  const podeSalvar = hasAuthority(authorities, 'VENDAS_GERENCIAR')

  const { data: perfil, isLoading: carregando } = useComissaoPerfil(perfilId)
  const criar = useCriarComissaoPerfil()
  const atualizar = useAtualizarComissaoPerfil()
  const salvando = criar.isPending || atualizar.isPending

  const [modalObjetivoAberto, setModalObjetivoAberto] = useState(false)
  const { data: objetivos } = useObjetivosComissaoPerfil(perfilId ?? -1)
  const removerObjetivo = useRemoverObjetivoComissaoPerfil(perfilId ?? -1)

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (perfil) {
      form.setFieldsValue(perfil)
    }
  }, [perfil, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    try {
      if (modoEdicao) {
        await atualizar.mutateAsync({ id: perfilId, request: valores })
        message.success('Perfil de comissão atualizado com sucesso.')
      } else {
        await criar.mutateAsync(valores)
        message.success('Perfil de comissão criado com sucesso.')
      }
      navigate('/vendas/comissao-perfis')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar perfil de comissão.')
    }
  }

  async function handleRemoverObjetivo(objetivoId: number) {
    try {
      await removerObjetivo.mutateAsync(objetivoId)
      message.success('Objetivo de comissão removido.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao remover objetivo.')
    }
  }

  if (modoEdicao && carregando) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 4 }} />
      </Card>
    )
  }

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate('/vendas/comissao-perfis')}>Perfis de Comissão</a> },
          { title: modoEdicao ? `Perfil #${perfilId}` : 'Novo perfil' },
        ]}
      />

      <Card>
        <Form form={form} layout="vertical" initialValues={VALORES_PADRAO_NOVO} disabled={!podeSalvar}>
          <Flex justify="space-between" align="center" wrap="wrap" gap={16} style={{ marginBottom: 24 }}>
            <Title level={4} style={{ margin: 0 }}>
              {modoEdicao ? 'Editar Perfil de Comissão' : 'Novo Perfil de Comissão'}
            </Title>
            <Space size="large">
              <Form.Item
                name="situacao"
                valuePropName="checked"
                noStyle
                normalize={(v: boolean) => (v ? 'ATIVO' : 'INATIVO')}
                getValueProps={(v: string) => ({ checked: v === 'ATIVO' })}
              >
                <Switch checkedChildren="Ativo" unCheckedChildren="Inativo" />
              </Form.Item>
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/vendas/comissao-perfis')}>
                Cancelar
              </Button>
              <Tooltip title={podeSalvar ? undefined : 'Você não tem permissão para gerenciar perfis de comissão'}>
                <Button type="primary" icon={<SaveOutlined />} loading={salvando} disabled={!podeSalvar} onClick={handleSalvar}>
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          <Row gutter={12}>
            <Col span={6}>
              <Form.Item name="codigo" label="Código" rules={[{ max: 3 }]}>
                <Input maxLength={3} />
              </Form.Item>
            </Col>
            <Col span={18}>
              <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]}>
                <Input placeholder="Ex: Vendedor Balcão, Representante Externo..." />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Card>

      {modoEdicao && (
        <Card
          title="Objetivos de Comissão (meta por produto)"
          extra={
            podeSalvar && (
              <Button icon={<PlusOutlined />} onClick={() => setModalObjetivoAberto(true)}>
                Adicionar Objetivo
              </Button>
            )
          }
        >
          <Table<ComissaoObjetivoResponse>
            rowKey="id"
            dataSource={objetivos}
            pagination={false}
            locale={{ emptyText: 'Nenhum objetivo cadastrado ainda — a comissão padrão do vendedor se aplica a todos os produtos' }}
            columns={[
              { title: 'Código', dataIndex: 'codigo', width: 80, render: (v?: string) => v || '—' },
              { title: 'Nome', dataIndex: 'nome' },
              { title: 'Produto', key: 'produto', render: (_, item) => <ProdutoResumoCell produtoId={item.produtoId} /> },
              {
                title: 'Forma de pagamento',
                dataIndex: 'formaPagamento',
                render: (v?: keyof typeof TIPO_PAGAMENTO_VENDA_LABEL) => (v ? TIPO_PAGAMENTO_VENDA_LABEL[v] : '—'),
              },
              { title: 'Taxa (%)', dataIndex: 'taxaPagamento', align: 'right', render: (v?: number) => (v != null ? `${v}%` : '—') },
              { title: 'Valor fixo (R$)', dataIndex: 'valorPagamento', align: 'right', render: (v?: number) => (v != null ? `R$ ${v.toFixed(2)}` : '—') },
              ...(podeSalvar
                ? [
                    {
                      title: 'Ações',
                      key: 'acoes',
                      width: 100,
                      render: (_: unknown, item: ComissaoObjetivoResponse) => (
                        <Popconfirm title="Remover este objetivo?" onConfirm={() => handleRemoverObjetivo(item.id)}>
                          <Button size="small" danger>
                            Remover
                          </Button>
                        </Popconfirm>
                      ),
                    },
                  ]
                : []),
            ]}
          />
        </Card>
      )}

      {modoEdicao && (
        <AdicionarObjetivoComissaoModal open={modalObjetivoAberto} comissaoPerfilId={perfilId} onClose={() => setModalObjetivoAberto(false)} />
      )}
    </Flex>
  )
}
