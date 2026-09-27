import { useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Col, Flex, Form, Input, Row, Skeleton, Space, Switch, Tabs, Tooltip } from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import AbaBloqueada from './AbaBloqueada'
import FuncionalidadesTab from './tabs/FuncionalidadesTab'
import { useAtualizarPapel, useCriarPapel, usePapel } from './queries'
import type { PapelRequest } from './types'

const VALORES_PADRAO_NOVO: Partial<PapelRequest> = { situacao: 'ATIVO' }

/**
 * Funcionalidades é sub-recurso (`PUT /papeis/{id}/funcionalidades`) — só
 * existe com o papel já salvo, mesmo gate das sub-abas de Empresa/Usuário.
 */
export default function PapelFormPage() {
  const { id } = useParams<{ id: string }>()
  const papelId = id ? Number(id) : undefined
  const modoEdicao = papelId !== undefined
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<PapelRequest>()

  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'SEGURANCA_GERENCIAR')

  const { data: papel, isLoading: carregando } = usePapel(papelId)
  const criar = useCriarPapel()
  const atualizar = useAtualizarPapel(papelId ?? -1)
  const salvando = criar.isPending || atualizar.isPending

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (papel) {
      form.setFieldsValue(papel)
    }
  }, [papel, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    try {
      if (modoEdicao) {
        await atualizar.mutateAsync(valores)
        message.success('Papel atualizado com sucesso.')
      } else {
        const criado = await criar.mutateAsync(valores)
        message.success('Papel criado com sucesso. Agora atribua as funcionalidades.')
        navigate(`/seguranca/papeis/${criado.id}`, { replace: true })
        return
      }
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar papel.')
    }
  }

  if (modoEdicao && carregando) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 8 }} />
      </Card>
    )
  }

  const dicaAbaBloqueada = 'Salve os dados gerais do papel primeiro'

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate('/seguranca/papeis')}>Papéis</a> },
          { title: modoEdicao ? papel?.nome : 'Novo papel' },
        ]}
      />

      <Card>
        <Form form={form} layout="vertical" initialValues={VALORES_PADRAO_NOVO} disabled={!podeGerenciar}>
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
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/seguranca/papeis')}>
                Cancelar
              </Button>
              <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar papéis'}>
                <Button type="primary" icon={<SaveOutlined />} loading={salvando} disabled={!podeGerenciar} onClick={handleSalvar}>
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          <Tabs
            items={[
              {
                key: 'dados-gerais',
                label: 'Dados Gerais',
                children: (
                  <Row gutter={12}>
                    <Col span={8}>
                      <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 60 }]}>
                        <Input placeholder="Ex: GERENTE, COMPRADOR..." />
                      </Form.Item>
                    </Col>
                    <Col span={16}>
                      <Form.Item name="descricao" label="Descrição" rules={[{ max: 250 }]}>
                        <Input />
                      </Form.Item>
                    </Col>
                  </Row>
                ),
              },
              {
                key: 'funcionalidades',
                label: `Funcionalidades ${papel ? `(${papel.funcionalidades.length})` : ''}`,
                disabled: !modoEdicao,
                children:
                  papel !== undefined ? (
                    <FuncionalidadesTab papel={papel} podeGerenciar={podeGerenciar} />
                  ) : (
                    <AbaBloqueada mensagem={dicaAbaBloqueada} />
                  ),
              },
            ].map((item) => (item.disabled ? { ...item, label: <Tooltip title={dicaAbaBloqueada}>{item.label}</Tooltip> } : item))}
          />
        </Form>
      </Card>
    </Flex>
  )
}
