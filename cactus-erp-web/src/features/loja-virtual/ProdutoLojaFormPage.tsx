import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Col, Flex, Form, Input, Popconfirm, Row, Select, Skeleton, Space, Table, Tooltip, Typography } from 'antd'
import { ArrowLeftOutlined, PlusOutlined, SaveOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import SituacaoTag from '@shared/ui/SituacaoTag'
import SeletorLoja from './SeletorLoja'
import VarianteModal from './modals/VarianteModal'
import { useAtualizarProduto, useCriarProduto, useExcluirVariante, useProduto, useTodasCategorias, useTodosAtributos } from './queries'
import type { ProdutoLojaRequest, VarianteProdutoLojaResponse } from './types'

const { Title } = Typography

export default function ProdutoLojaFormPage() {
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { id } = useParams()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'LOJA_GERENCIAR')
  const [form] = Form.useForm<ProdutoLojaRequest>()
  const [lojaId, setLojaId] = useState<number | undefined>(undefined)
  const [modalVarianteAberto, setModalVarianteAberto] = useState(false)
  const [varianteEmEdicao, setVarianteEmEdicao] = useState<VarianteProdutoLojaResponse | undefined>(undefined)

  const produtoId = id ? Number(id) : undefined
  const modoEdicao = produtoId !== undefined
  const { data: produto, isLoading } = useProduto(produtoId)
  const lojaIdEfetivo = modoEdicao ? produto?.lojaId : lojaId
  const { data: categorias } = useTodasCategorias(lojaIdEfetivo)
  const { data: atributos } = useTodosAtributos(produto?.categoriaLojaId)
  const atributosVariacao = atributos?.filter((a) => a.geraVariacao) ?? []

  const criar = useCriarProduto()
  const atualizar = useAtualizarProduto()
  const excluirVariante = useExcluirVariante(produtoId ?? -1)

  if (modoEdicao && isLoading) {
    return <Skeleton active />
  }

  async function handleSalvar() {
    const valores = await form.validateFields()
    try {
      if (produtoId && produto) {
        await atualizar.mutateAsync({ id: produtoId, request: { ...valores, lojaId: produto.lojaId } })
        message.success('Produto atualizado.')
      } else {
        if (!lojaId) {
          message.error('Selecione a loja.')
          return
        }
        const criado = await criar.mutateAsync({ ...valores, lojaId })
        message.success('Produto criado.')
        navigate(`/loja-virtual/produtos/${criado.id}`)
      }
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar produto.')
    }
  }

  async function handleExcluirVariante(varianteId: number) {
    try {
      await excluirVariante.mutateAsync(varianteId)
      message.success('Variante excluída.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir variante.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate('/loja-virtual/produtos')}>Produtos</a> },
          { title: modoEdicao ? produto?.nome ?? '' : 'Novo produto' },
        ]}
      />

      <Card>
        <Form
          form={form}
          name="produtoLojaForm"
          layout="vertical"
          disabled={!podeGerenciar}
          initialValues={produto}
        >
          <Flex justify="space-between" align="center" style={{ marginBottom: 16 }}>
            <Title level={4} style={{ margin: 0 }}>
              {modoEdicao ? produto?.nome : 'Novo Produto'}
            </Title>
            <Space>
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/loja-virtual/produtos')}>
                Voltar
              </Button>
              <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar produtos'}>
                <Button
                  type="primary"
                  icon={<SaveOutlined />}
                  loading={criar.isPending || atualizar.isPending}
                  disabled={!podeGerenciar}
                  onClick={handleSalvar}
                >
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          <Row gutter={12}>
            {!modoEdicao && (
              <Col span={8}>
                <Form.Item label="Loja" required>
                  <SeletorLoja value={lojaId} onChange={setLojaId} />
                </Form.Item>
              </Col>
            )}
            <Col span={modoEdicao ? 12 : 10}>
              <Form.Item name="categoriaLojaId" label="Categoria" rules={[{ required: true, message: 'Selecione a categoria' }]}>
                <Select
                  showSearch
                  disabled={!lojaIdEfetivo}
                  optionFilterProp="label"
                  options={categorias?.map((c) => ({ value: c.id, label: c.nome }))}
                />
              </Form.Item>
            </Col>
            <Col span={modoEdicao ? 12 : 6}>
              <Form.Item name="marca" label="Marca" rules={[{ max: 60 }]}>
                <Input />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={24}>
              <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 150 }]}>
                <Input />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={24}>
              <Form.Item name="descricao" label="Descrição" rules={[{ max: 2000 }]}>
                <Input.TextArea rows={3} maxLength={2000} showCount />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Card>

      {modoEdicao && produto && (
        <Card
          title="Variantes"
          extra={
            <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar variantes'}>
              <Button
                icon={<PlusOutlined />}
                disabled={!podeGerenciar}
                onClick={() => {
                  setVarianteEmEdicao(undefined)
                  setModalVarianteAberto(true)
                }}
              >
                Nova Variante
              </Button>
            </Tooltip>
          }
        >
          <Table<VarianteProdutoLojaResponse>
            rowKey="id"
            dataSource={produto.variantes}
            pagination={false}
            columns={[
              { title: 'SKU', dataIndex: 'sku' },
              {
                title: 'Composição',
                key: 'composicao',
                render: (_, v) => v.valoresAtributos.map((a) => `${a.atributoCategoriaNome}=${a.valor}`).join(', ') || '—',
              },
              {
                title: 'Preço',
                dataIndex: 'precoVenda',
                align: 'right',
                render: (v: number, variante) =>
                  variante.precoPromocional ? (
                    <span>
                      <del>R$ {v.toFixed(2)}</del> R$ {variante.precoPromocional.toFixed(2)}
                    </span>
                  ) : (
                    `R$ ${v.toFixed(2)}`
                  ),
              },
              { title: 'Estoque disponível', dataIndex: 'estoqueDisponivel', align: 'right' },
              { title: 'Situação', dataIndex: 'situacao', render: (v) => <SituacaoTag situacao={v} /> },
              ...(podeGerenciar
                ? [
                    {
                      title: 'Ações',
                      key: 'acoes',
                      width: 160,
                      render: (_: unknown, variante: VarianteProdutoLojaResponse) => (
                        <Space>
                          <Button
                            size="small"
                            onClick={() => {
                              setVarianteEmEdicao(variante)
                              setModalVarianteAberto(true)
                            }}
                          >
                            Editar
                          </Button>
                          <Popconfirm title="Excluir esta variante?" onConfirm={() => handleExcluirVariante(variante.id)}>
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
      )}

      {produtoId && (
        <VarianteModal
          open={modalVarianteAberto}
          produtoId={produtoId}
          atributosVariacao={atributosVariacao}
          valorInicial={varianteEmEdicao}
          onClose={() => setModalVarianteAberto(false)}
        />
      )}
    </Flex>
  )
}
