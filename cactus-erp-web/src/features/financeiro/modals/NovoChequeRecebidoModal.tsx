import { useState } from 'react'
import { App, Col, Form, Input, InputNumber, Modal, Row } from 'antd'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useBuscaClientesParaSelecao, useCriarChequeRecebido } from '../queries'
import type { ChequeRecebidoRequest } from '../types'

interface NovoChequeRecebidoModalProps {
  open: boolean
  onClose: () => void
}

/** `clienteId` é opcional (cheque de alguém sem cadastro de cliente) — `nomeEmitente` é o
 * único campo realmente obrigatório do lado do emitente do cheque. */
export default function NovoChequeRecebidoModal({ open, onClose }: NovoChequeRecebidoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<ChequeRecebidoRequest>()
  const [termoCliente, setTermoCliente] = useState<string | undefined>(undefined)
  const { data: clientes, isLoading: carregandoClientes } = useBuscaClientesParaSelecao(termoCliente)
  const criar = useCriarChequeRecebido()

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await criar.mutateAsync(valores)
        message.success('Cheque recebido registrado com sucesso.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao registrar cheque recebido.')
      }
    })
  }

  return (
    <Modal
      title="Registrar cheque recebido"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Registrar"
      cancelText="Cancelar"
      confirmLoading={criar.isPending}
      width={640}
      destroyOnHidden
    >
      <Form form={form} name="novoChequeRecebidoForm" layout="vertical">
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="clienteId" label="Cliente (opcional)">
              <AsyncSelect
                allowClear
                opcoes={clientes}
                carregando={carregandoClientes}
                termo={termoCliente}
                onTermoChange={setTermoCliente}
                toOption={(c) => ({ value: c.id, label: c.nome })}
              />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="nomeEmitente" label="Nome do emitente" rules={[{ required: true, message: 'Informe o nome' }, { max: 150 }]}>
              <Input maxLength={150} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="cpfCnpj" label="CPF/CNPJ" rules={[{ max: 20 }]}>
              <Input maxLength={20} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="bancoCodigo" label="Banco (código)" rules={[{ max: 3 }]}>
              <Input maxLength={3} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="numero" label="Número do cheque" rules={[{ max: 20 }]}>
              <Input maxLength={20} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="agencia" label="Agência" rules={[{ max: 20 }]}>
              <Input maxLength={20} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="conta" label="Conta" rules={[{ max: 20 }]}>
              <Input maxLength={20} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="dataEmissao" label="Data de emissão" rules={[{ required: true, message: 'Informe a emissão' }]}>
              <Input type="date" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="bomPara" label="Bom para (pré-datado)">
              <Input type="date" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="valor" label="Valor (R$)" rules={[{ required: true, message: 'Informe o valor' }]}>
              <InputNumber<number> style={{ width: '100%' }} min={0.01} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
