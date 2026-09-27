import { useEffect, useState } from 'react'
import { App, Col, Form, Input, InputNumber, Modal, Row, Select } from 'antd'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useBuscaTransportadorasParaSelecao, useDefinirFreteVenda, useFreteVenda } from '../queries'
import { TIPO_FRETE_LABEL } from '../types'
import type { VendaFreteRequest } from '../types'

interface DefinirFreteModalProps {
  open: boolean
  vendaId: number
  onClose: () => void
}

/** `VendaFrete` é upsert 1:1 com a venda (`PUT`, sem `POST` — mesmo padrão de `ConfiguracaoOperacionalLoja` da Empresa) — `GET` devolve 404 quando ainda não definido, tratado como formulário vazio, não erro. */
export default function DefinirFreteModal({ open, vendaId, onClose }: DefinirFreteModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<VendaFreteRequest>()
  const { data: frete } = useFreteVenda(vendaId)
  const [termo, setTermo] = useState<string | undefined>(undefined)
  const { data: transportadoras, isLoading: carregandoTransportadoras } = useBuscaTransportadorasParaSelecao(termo)
  const definir = useDefinirFreteVenda(vendaId)

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (open) {
      form.setFieldsValue(frete ?? {})
    }
  }, [open, frete, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await definir.mutateAsync(valores)
        message.success('Frete definido.')
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao definir frete.')
      }
    })
  }

  return (
    <Modal
      title="Definir frete da venda"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={definir.isPending}
      width={640}
      destroyOnHidden
    >
      <Form form={form} name="definirFreteForm" layout="vertical">
        <Row gutter={12}>
          <Col span={16}>
            <Form.Item name="transportadoraId" label="Transportadora">
              <AsyncSelect
                allowClear
                opcoes={transportadoras}
                carregando={carregandoTransportadoras}
                termo={termo}
                onTermoChange={setTermo}
                toOption={(t) => ({ value: t.id, label: t.nome })}
              />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="responsavel" label="Responsável">
              <Select allowClear options={Object.entries(TIPO_FRETE_LABEL).map(([value, label]) => ({ value, label }))} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="conhecimento" label="Conhecimento de frete">
              <Input maxLength={20} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="seloFiscal" label="Selo fiscal">
              <Input maxLength={20} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="placa" label="Placa do veículo">
              <Input maxLength={10} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="ufPlaca" label="UF da placa">
              <Input maxLength={2} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="quantidadeVolume" label="Qtd. volumes">
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="marcaVolume" label="Marca do volume">
              <Input maxLength={30} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="especieVolume" label="Espécie do volume">
              <Input maxLength={30} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="pesoBruto" label="Peso bruto (kg)">
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={3} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="pesoLiquido" label="Peso líquido (kg)">
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={3} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
