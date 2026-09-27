import { Button, Col, Flex, Form, Input, Modal, Row, Select, Typography, App } from 'antd'
import { DeleteOutlined, PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { useContasReceber, useGerarRemessaBancaria } from '../queries'
import type { GerarRemessaRequest } from '../types'

interface GerarRemessaModalProps {
  open: boolean
  contaBancariaId: number
  onClose: () => void
  onGerada: (remessaId: number) => void
}

/** Dados do sacado (pagador) são informados manualmente, item a item — o `financeiro-service`
 * não tem cliente OAuth2 de saída pro cadastro-base-service, então não resolve nome/documento/
 * endereço do cliente sozinho (ver Javadoc de `ItemRemessaRequest` no backend). Só títulos
 * `ABERTA`/`PARCIAL` podem entrar (validado no backend, 409 se não). */
export default function GerarRemessaModal({ open, contaBancariaId, onClose, onGerada }: GerarRemessaModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<Omit<GerarRemessaRequest, 'contaBancariaId' | 'empresaId'>>()
  const { empresaAtivaId } = useAuth()
  const { data: titulos } = useContasReceber({}, empresaAtivaId ?? undefined, 0, 100)
  const titulosEmAberto = titulos?.conteudo.filter((t) => t.situacao === 'ABERTA' || t.situacao === 'PARCIAL')
  const gerar = useGerarRemessaBancaria()

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        const remessa = await gerar.mutateAsync({ ...valores, contaBancariaId, empresaId: empresaAtivaId ?? -1 })
        message.success('Remessa gerada com sucesso.')
        form.resetFields()
        onGerada(remessa.id)
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao gerar remessa.')
      }
    })
  }

  return (
    <Modal
      title="Gerar remessa CNAB 240"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Gerar Remessa"
      cancelText="Cancelar"
      confirmLoading={gerar.isPending}
      width={760}
      destroyOnHidden
    >
      <Form form={form} name="gerarRemessaForm" layout="vertical">
        <Form.List name="itens" rules={[{ validator: async (_, itens) => (itens?.length ? Promise.resolve() : Promise.reject(new Error('Adicione ao menos 1 título'))) }]}>
          {(fields, { add, remove }, { errors }) => (
            <Flex vertical gap={12}>
              {fields.map((field) => (
                <Row gutter={8} key={field.key} align="top" style={{ border: '1px solid #f0f0f0', padding: 8, borderRadius: 4 }}>
                  <Col span={24}>
                    <Form.Item
                      name={[field.name, 'contaReceberId']}
                      label="Título (conta a receber)"
                      rules={[{ required: true, message: 'Selecione o título' }]}
                      style={{ marginBottom: 8 }}
                    >
                      <Select
                        showSearch
                        optionFilterProp="label"
                        options={titulosEmAberto?.map((t) => ({
                          value: t.id,
                          label: `#${t.id} — ${t.numeroDocumento ?? 'sem documento'} — saldo R$ ${t.valorSaldo.toFixed(2)}`,
                        }))}
                      />
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item
                      name={[field.name, 'sacadoNumeroInscricao']}
                      label="CPF/CNPJ do sacado"
                      rules={[{ required: true, message: 'Informe o documento' }]}
                      style={{ marginBottom: 8 }}
                    >
                      <Input maxLength={20} />
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item
                      name={[field.name, 'sacadoNome']}
                      label="Nome do sacado"
                      rules={[{ required: true, message: 'Informe o nome' }]}
                      style={{ marginBottom: 8 }}
                    >
                      <Input maxLength={100} />
                    </Form.Item>
                  </Col>
                  <Col span={10}>
                    <Form.Item name={[field.name, 'sacadoLogradouro']} label="Logradouro" style={{ marginBottom: 8 }}>
                      <Input maxLength={100} />
                    </Form.Item>
                  </Col>
                  <Col span={8}>
                    <Form.Item name={[field.name, 'sacadoBairro']} label="Bairro" style={{ marginBottom: 8 }}>
                      <Input maxLength={50} />
                    </Form.Item>
                  </Col>
                  <Col span={6}>
                    <Form.Item name={[field.name, 'sacadoCep']} label="CEP" style={{ marginBottom: 8 }}>
                      <Input maxLength={10} />
                    </Form.Item>
                  </Col>
                  <Col span={16}>
                    <Form.Item name={[field.name, 'sacadoCidade']} label="Cidade" style={{ marginBottom: 8 }}>
                      <Input maxLength={50} />
                    </Form.Item>
                  </Col>
                  <Col span={4}>
                    <Form.Item name={[field.name, 'sacadoUf']} label="UF" style={{ marginBottom: 8 }}>
                      <Input maxLength={2} />
                    </Form.Item>
                  </Col>
                  <Col span={4}>
                    <Button danger type="text" icon={<DeleteOutlined />} onClick={() => remove(field.name)}>
                      Remover
                    </Button>
                  </Col>
                </Row>
              ))}
              <Form.ErrorList errors={errors} />
              <Button icon={<PlusOutlined />} onClick={() => add({})}>
                Adicionar título
              </Button>
              {!titulosEmAberto?.length && (
                <Typography.Text type="secondary">Nenhum título ABERTA/PARCIAL encontrado pra esta empresa.</Typography.Text>
              )}
            </Flex>
          )}
        </Form.List>
      </Form>
    </Modal>
  )
}
