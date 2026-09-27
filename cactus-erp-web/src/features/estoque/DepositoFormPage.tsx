import { useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Col, Flex, Form, Input, Row, Select, Skeleton, Space, Switch, Tooltip } from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useAtualizarDeposito, useCriarDeposito, useDeposito } from './queries'
import { TIPO_DEPOSITO_LABEL } from './types'
import type { DepositoRequest } from './types'

const VALORES_PADRAO_NOVO: Partial<DepositoRequest> = { tipo: 'FILIAL', situacao: 'ATIVO' }

export default function DepositoFormPage() {
  const { id } = useParams<{ id: string }>()
  const depositoId = id ? Number(id) : undefined
  const modoEdicao = depositoId !== undefined
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<DepositoRequest>()

  const { empresaAtivaId, authorities } = useAuth()
  const podeSalvar = hasAuthority(authorities, 'ESTOQUE_GERENCIAR')

  const { data: deposito, isLoading: carregando } = useDeposito(depositoId)
  const criar = useCriarDeposito()
  const atualizar = useAtualizarDeposito(depositoId ?? -1)
  const salvando = criar.isPending || atualizar.isPending

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (deposito) {
      form.setFieldsValue(deposito)
    }
  }, [deposito, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    const request: DepositoRequest = { ...valores, empresaId: deposito?.empresaId ?? empresaAtivaId ?? 0 }

    try {
      if (modoEdicao) {
        await atualizar.mutateAsync(request)
        message.success('Depósito atualizado com sucesso.')
      } else {
        await criar.mutateAsync(request)
        message.success('Depósito criado com sucesso.')
      }
      navigate('/estoque/depositos')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar depósito.')
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
          { title: <a onClick={() => navigate('/estoque/depositos')}>Depósitos</a> },
          { title: modoEdicao ? deposito?.nome : 'Novo depósito' },
        ]}
      />

      <Card>
        <Form form={form} layout="vertical" initialValues={VALORES_PADRAO_NOVO} disabled={!podeSalvar}>
          <Flex justify="flex-end" wrap="wrap" gap={16} style={{ marginBottom: 24 }}>
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
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/estoque/depositos')}>
                Cancelar
              </Button>
              <Tooltip title={podeSalvar ? undefined : 'Você não tem permissão para gerenciar depósitos'}>
                <Button type="primary" icon={<SaveOutlined />} loading={salvando} disabled={!podeSalvar} onClick={handleSalvar}>
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          <Row gutter={12}>
            <Col span={6}>
              <Form.Item name="codigo" label="Código" rules={[{ required: true, message: 'Informe o código' }, { max: 20 }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="tipo" label="Tipo" rules={[{ required: true }]}>
                <Select options={Object.entries(TIPO_DEPOSITO_LABEL).map(([value, label]) => ({ value, label }))} />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Card>
    </Flex>
  )
}
