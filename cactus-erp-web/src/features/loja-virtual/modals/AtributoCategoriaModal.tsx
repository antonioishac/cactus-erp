import { useEffect } from 'react'
import { App, Button, Checkbox, Flex, Form, Input, InputNumber, Modal, Select, Typography } from 'antd'
import { DeleteOutlined, PlusOutlined } from '@ant-design/icons'
import { useAtualizarAtributo, useCriarAtributo } from '../queries'
import { TIPO_ATRIBUTO_LABEL } from '../types'
import type { AtributoCategoriaRequest, AtributoCategoriaResponse } from '../types'

const { Text } = Typography

interface Props {
  open: boolean
  categoriaLojaId: number
  valorInicial?: AtributoCategoriaResponse
  onClose: () => void
}

/**
 * `PUT` do backend não altera as opções (ver Javadoc de `AtributoCategoriaController`) —
 * o `Form.List` de opções só aparece na CRIAÇÃO (tipo LISTA); em edição, gerenciar opção
 * é ação dedicada na própria listagem (`GerenciarOpcoesModal`), que usa os endpoints
 * `POST/DELETE .../opcoes`.
 */
export default function AtributoCategoriaModal({ open, categoriaLojaId, valorInicial, onClose }: Props) {
  const { message } = App.useApp()
  const [form] = Form.useForm<AtributoCategoriaRequest>()
  const tipo = Form.useWatch('tipo', form)
  const criar = useCriarAtributo()
  const atualizar = useAtualizarAtributo()
  const salvando = criar.isPending || atualizar.isPending

  useEffect(() => {
    if (open) {
      form.setFieldsValue(
        valorInicial ?? { categoriaLojaId, tipo: 'TEXTO', obrigatorio: false, geraVariacao: false, opcoes: [] },
      )
    }
  }, [open, valorInicial, categoriaLojaId, form])

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        const request: AtributoCategoriaRequest = { ...valores, categoriaLojaId }
        if (valorInicial) {
          delete request.opcoes
          await atualizar.mutateAsync({ id: valorInicial.id, request })
          message.success('Atributo atualizado.')
        } else {
          await criar.mutateAsync(request)
          message.success('Atributo criado.')
        }
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao salvar atributo.')
      }
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar atributo' : 'Novo atributo'}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={salvando}
      destroyOnHidden
      width={560}
    >
      <Form form={form} name="atributoCategoriaForm" layout="vertical">
        <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 60 }]}>
          <Input />
        </Form.Item>
        <Form.Item name="tipo" label="Tipo" rules={[{ required: true }]}>
          <Select options={Object.entries(TIPO_ATRIBUTO_LABEL).map(([value, label]) => ({ value, label }))} />
        </Form.Item>
        <Form.Item name="obrigatorio" valuePropName="checked">
          <Checkbox>Obrigatório pra compor a variante</Checkbox>
        </Form.Item>
        <Form.Item
          name="geraVariacao"
          valuePropName="checked"
          tooltip="Atributo meramente informativo (não compõe SKU) deve ficar desmarcado — ex: composição do tecido."
        >
          <Checkbox>Gera variação (compõe o SKU)</Checkbox>
        </Form.Item>
        <Form.Item name="ordem" label="Ordem de exibição">
          <InputNumber style={{ width: '100%' }} min={0} />
        </Form.Item>

        {!valorInicial && tipo === 'LISTA' && (
          <Form.Item label="Opções">
            <Form.List name="opcoes">
              {(fields, { add, remove }) => (
                <Flex vertical gap={8}>
                  {fields.map((field) => (
                    <Flex key={field.key} gap={8}>
                      <Form.Item name={[field.name, 'valor']} rules={[{ required: true, message: 'Valor' }]} style={{ marginBottom: 0, flex: 1 }}>
                        <Input placeholder="Valor da opção" />
                      </Form.Item>
                      <Form.Item name={[field.name, 'ordem']} style={{ marginBottom: 0, width: 80 }}>
                        <InputNumber placeholder="Ordem" style={{ width: '100%' }} />
                      </Form.Item>
                      <Button type="text" danger icon={<DeleteOutlined />} onClick={() => remove(field.name)} />
                    </Flex>
                  ))}
                  <Button icon={<PlusOutlined />} onClick={() => add()}>
                    Adicionar opção
                  </Button>
                </Flex>
              )}
            </Form.List>
          </Form.Item>
        )}
        {valorInicial && valorInicial.tipo === 'LISTA' && (
          <Text type="secondary">As opções não são editadas aqui — use "Gerenciar opções" na listagem.</Text>
        )}
      </Form>
    </Modal>
  )
}
