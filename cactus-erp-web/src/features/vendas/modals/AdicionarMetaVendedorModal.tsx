import { useState } from 'react'
import { App, Col, DatePicker, Form, InputNumber, Modal, Row, Select } from 'antd'
import type { Dayjs } from 'dayjs'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useAdicionarMetaVendedor, useBuscaClientesParaSelecao } from '../queries'
import { PERIODO_META_LABEL } from '../types'
import type { VendedorMetaRequest } from '../types'

interface AdicionarMetaVendedorModalProps {
  open: boolean
  vendedorId: number
  onClose: () => void
}

interface FormValues extends Omit<VendedorMetaRequest, 'dataInicio' | 'dataFim'> {
  dataInicio?: Dayjs
  dataFim?: Dayjs
}

export default function AdicionarMetaVendedorModal({ open, vendedorId, onClose }: AdicionarMetaVendedorModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<FormValues>()
  const [termoCliente, setTermoCliente] = useState<string | undefined>(undefined)
  const { data: clientes, isLoading: carregandoClientes } = useBuscaClientesParaSelecao(termoCliente)
  const adicionar = useAdicionarMetaVendedor(vendedorId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      const request: VendedorMetaRequest = {
        ...valores,
        dataInicio: valores.dataInicio ? valores.dataInicio.format('YYYY-MM-DD') : undefined,
        dataFim: valores.dataFim ? valores.dataFim.format('YYYY-MM-DD') : undefined,
      }
      try {
        await adicionar.mutateAsync(request)
        message.success('Meta adicionada.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao adicionar meta.')
      }
    })
  }

  return (
    <Modal
      title="Adicionar meta por cliente/período"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Adicionar"
      cancelText="Cancelar"
      confirmLoading={adicionar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="adicionarMetaVendedorForm" layout="vertical">
        <Form.Item name="clienteId" label="Cliente" rules={[{ required: true, message: 'Selecione o cliente' }]}>
          <AsyncSelect
            opcoes={clientes}
            carregando={carregandoClientes}
            termo={termoCliente}
            onTermoChange={setTermoCliente}
            toOption={(c) => ({ value: c.id, label: c.nome })}
          />
        </Form.Item>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="periodoMeta" label="Período">
              <Select allowClear options={Object.entries(PERIODO_META_LABEL).map(([value, label]) => ({ value, label }))} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="metaOrcada" label="Meta orçada (R$)">
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="dataInicio" label="Data de início">
              <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="dataFim" label="Data de fim">
              <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
