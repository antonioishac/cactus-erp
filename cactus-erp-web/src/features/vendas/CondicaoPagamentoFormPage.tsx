import { useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import {
  App,
  Breadcrumb,
  Button,
  Card,
  Col,
  Flex,
  Form,
  Input,
  InputNumber,
  Row,
  Select,
  Skeleton,
  Space,
  Switch,
  Tooltip,
  Typography,
} from 'antd'
import { ArrowLeftOutlined, DeleteOutlined, PlusOutlined, SaveOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useAtualizarCondicaoPagamento, useCondicaoPagamento, useCriarCondicaoPagamento } from './queries'
import { TIPO_PAGAMENTO_VENDA_LABEL } from './types'
import type { VendaCondicoesPagamentoRequest } from './types'

const { Title } = Typography

const VALORES_PADRAO_NOVO: Partial<VendaCondicoesPagamentoRequest> = { situacao: 'ATIVO', parcelas: [] }

/**
 * `VendaCondicoesPagamentoRequest.parcelas` substitui a lista inteira a cada
 * `PUT` (mesmo padrão de `ProdutoFaixaPrecoDTO` no cadastro-base-service) —
 * `Form.List` inline, mesmo molde de "itens" em `RequisicaoFormPage`
 * (gestao-compras-service), só que aqui as linhas não têm produto/quantidade,
 * são número da parcela/dias/taxa.
 */
export default function CondicaoPagamentoFormPage() {
  const { id } = useParams<{ id: string }>()
  const condicaoId = id ? Number(id) : undefined
  const modoEdicao = condicaoId !== undefined
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<VendaCondicoesPagamentoRequest>()

  const { authorities } = useAuth()
  const podeSalvar = hasAuthority(authorities, 'VENDAS_GERENCIAR')

  const { data: condicao, isLoading: carregando } = useCondicaoPagamento(condicaoId)
  const criar = useCriarCondicaoPagamento()
  const atualizar = useAtualizarCondicaoPagamento(condicaoId ?? -1)
  const salvando = criar.isPending || atualizar.isPending

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (condicao) {
      form.setFieldsValue(condicao)
    }
  }, [condicao, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    try {
      if (modoEdicao) {
        await atualizar.mutateAsync(valores)
        message.success('Condição de pagamento atualizada com sucesso.')
      } else {
        await criar.mutateAsync(valores)
        message.success('Condição de pagamento criada com sucesso.')
      }
      navigate('/vendas/condicoes-pagamento')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar condição de pagamento.')
    }
  }

  if (modoEdicao && carregando) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 6 }} />
      </Card>
    )
  }

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate('/vendas/condicoes-pagamento')}>Condições de Pagamento</a> },
          { title: modoEdicao ? condicao?.nome : 'Nova condição' },
        ]}
      />

      <Card>
        <Form form={form} layout="vertical" initialValues={VALORES_PADRAO_NOVO} disabled={!podeSalvar}>
          <Flex justify="space-between" align="center" wrap="wrap" gap={16} style={{ marginBottom: 24 }}>
            <Title level={4} style={{ margin: 0 }}>
              {modoEdicao ? 'Editar Condição de Pagamento' : 'Nova Condição de Pagamento'}
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
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/vendas/condicoes-pagamento')}>
                Cancelar
              </Button>
              <Tooltip title={podeSalvar ? undefined : 'Você não tem permissão para gerenciar condições de pagamento'}>
                <Button type="primary" icon={<SaveOutlined />} loading={salvando} disabled={!podeSalvar} onClick={handleSalvar}>
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          <Row gutter={12}>
            <Col span={12}>
              <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 50 }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="vistaPrazo" label="À vista/A prazo">
                <Select allowClear options={Object.entries(TIPO_PAGAMENTO_VENDA_LABEL).map(([value, label]) => ({ value, label }))} />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="prazoMedio" label="Prazo médio (dias)">
                <InputNumber style={{ width: '100%' }} min={0} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={24}>
              <Form.Item name="descricao" label="Descrição" rules={[{ max: 250 }]}>
                <Input.TextArea rows={2} maxLength={250} showCount />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={6}>
              <Form.Item name="faturamentoMinimo" label="Faturamento mínimo (R$)">
                <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="faturamentoMaximo" label="Faturamento máximo (R$)">
                <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="indiceCorrecao" label="Índice de correção (%)">
                <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="diasTolerancia" label="Dias de tolerância">
                <InputNumber style={{ width: '100%' }} min={0} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={6}>
              <Form.Item name="valorTolerancia" label="Valor de tolerância (R$)">
                <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
          </Row>

          <Title level={5}>Parcelas</Title>
          <Form.List name="parcelas">
            {(fields, { add, remove }) => (
              <Flex vertical gap={8}>
                {fields.map((field, index) => (
                  <Row gutter={12} key={field.key} align="middle">
                    <Col span={6}>
                      <Form.Item
                        name={[field.name, 'parcela']}
                        initialValue={index + 1}
                        rules={[{ required: true, message: 'Nº da parcela' }]}
                        style={{ marginBottom: 8 }}
                      >
                        <InputNumber style={{ width: '100%' }} min={1} placeholder="Nº parcela" />
                      </Form.Item>
                    </Col>
                    <Col span={8}>
                      <Form.Item name={[field.name, 'dias']} style={{ marginBottom: 8 }}>
                        <InputNumber style={{ width: '100%' }} min={0} placeholder="Dias após a venda" />
                      </Form.Item>
                    </Col>
                    <Col span={8}>
                      <Form.Item name={[field.name, 'taxa']} style={{ marginBottom: 8 }}>
                        <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," placeholder="Taxa (%)" />
                      </Form.Item>
                    </Col>
                    <Col span={2}>
                      <Button type="text" danger icon={<DeleteOutlined />} onClick={() => remove(field.name)} />
                    </Col>
                  </Row>
                ))}
                <Flex>
                  <Button icon={<PlusOutlined />} onClick={() => add({ parcela: fields.length + 1 })}>
                    Adicionar parcela
                  </Button>
                </Flex>
              </Flex>
            )}
          </Form.List>
        </Form>
      </Card>
    </Flex>
  )
}
