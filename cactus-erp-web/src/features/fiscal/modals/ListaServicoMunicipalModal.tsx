import { useEffect } from 'react'
import { App, Col, Form, Input, InputNumber, Modal, Row } from 'antd'
import { useAtualizarListaServico, useCriarListaServico } from '../queries'
import type { ListaServicoMunicipalRequest, ListaServicoMunicipalResponse } from '../types'

interface ListaServicoMunicipalModalProps {
  open: boolean
  valorInicial?: ListaServicoMunicipalResponse
  onClose: () => void
}

/** ISS varia por ~5.500 municípios — sem catálogo nacional único pra semear, a empresa cadastra os itens que usa (ver Javadoc de `ListaServicoMunicipal` no backend). */
export default function ListaServicoMunicipalModal({ open, valorInicial, onClose }: ListaServicoMunicipalModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<ListaServicoMunicipalRequest>()
  const criar = useCriarListaServico()
  const atualizar = useAtualizarListaServico()
  const salvando = criar.isPending || atualizar.isPending

  useEffect(() => {
    if (open) {
      form.setFieldsValue(valorInicial ?? {})
    }
  }, [open, valorInicial, form])

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        if (valorInicial) {
          await atualizar.mutateAsync({ id: valorInicial.id, request: valores })
          message.success('Item da lista de serviço atualizado.')
        } else {
          await criar.mutateAsync(valores)
          message.success('Item da lista de serviço criado.')
        }
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao salvar item da lista de serviço.')
      }
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar item da lista de serviço' : 'Novo item da lista de serviço'}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={salvando}
      destroyOnHidden
    >
      <Form form={form} name="listaServicoForm" layout="vertical">
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="codigo" label="Código (LC 116/2003)" rules={[{ required: true, message: 'Informe o código' }, { max: 20 }]}>
              <Input placeholder="Ex: 14.01" maxLength={20} />
            </Form.Item>
          </Col>
          <Col span={16}>
            <Form.Item name="descricao" label="Descrição" rules={[{ required: true, message: 'Informe a descrição' }, { max: 250 }]}>
              <Input maxLength={250} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="codigoTributacaoMunicipio" label="Código de tributação do município" rules={[{ max: 20 }]}>
              <Input maxLength={20} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="aliquotaIssPadrao" label="Alíquota de ISS padrão (%)">
              <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
