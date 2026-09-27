import { useEffect } from 'react'
import { Col, Divider, Form, InputNumber, Modal, Row, Select } from 'antd'
import { UFS } from '@shared/domain/pessoaUtils'
import {
  CSOSN_LABEL,
  CST_ICMS_LABEL,
  MODALIDADE_BASE_CALCULO_ICMS_LABEL,
  MODALIDADE_BASE_CALCULO_ICMS_ST_LABEL,
} from '../types'
import type { IcmsConfiguracaoDTO } from '../types'

interface IcmsPorUfModalProps {
  open: boolean
  valorInicial?: IcmsConfiguracaoDTO
  /** UFs já usadas por OUTRAS linhas desta configuração — a UF sendo editada (se houver) não entra aqui, senão ela mesma ficaria bloqueada. */
  ufsIndisponiveis: string[]
  onCancel: () => void
  onSalvar: (icms: IcmsConfiguracaoDTO) => void
}

const OPCOES_CST_ICMS = Object.entries(CST_ICMS_LABEL).map(([value, label]) => ({ value, label }))
const OPCOES_CSOSN = Object.entries(CSOSN_LABEL).map(([value, label]) => ({ value, label }))
const OPCOES_MODALIDADE_ICMS = Object.entries(MODALIDADE_BASE_CALCULO_ICMS_LABEL).map(([value, label]) => ({ value, label }))
const OPCOES_MODALIDADE_ICMS_ST = Object.entries(MODALIDADE_BASE_CALCULO_ICMS_ST_LABEL).map(([value, label]) => ({ value, label }))

/**
 * ICMS de UMA UF de destino — a complexidade real do ICMS mora aqui (alíquota interna,
 * interestadual, MVA, base de cálculo ST), variando por estado. `cstIcms` (Regime Normal)
 * e `csosn` (Simples Nacional) são mutuamente exclusivos — qual preencher depende do CRT
 * da empresa vendedora, regra de negócio não validada neste modal.
 */
export default function IcmsPorUfModal({ open, valorInicial, ufsIndisponiveis, onCancel, onSalvar }: IcmsPorUfModalProps) {
  const [form] = Form.useForm<IcmsConfiguracaoDTO>()

  useEffect(() => {
    if (open) {
      form.setFieldsValue(valorInicial ?? {})
    }
  }, [open, valorInicial, form])

  const handleOk = () => {
    form.validateFields().then((valores) => {
      onSalvar(valores)
      form.resetFields()
    })
  }

  return (
    <Modal title="Configurar ICMS por UF" open={open} onCancel={onCancel} onOk={handleOk} okText="Salvar" cancelText="Cancelar" width={680} destroyOnHidden>
      <Form form={form} name="icmsPorUfForm" layout="vertical">
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="ufDestino" label="UF de destino" rules={[{ required: true, message: 'Selecione a UF' }]}>
              <Select showSearch options={UFS.filter((uf) => !ufsIndisponiveis.includes(uf)).map((uf) => ({ value: uf, label: uf }))} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="cstIcms" label="CST (Regime Normal)">
              <Select allowClear showSearch optionFilterProp="label" options={OPCOES_CST_ICMS} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="csosn" label="CSOSN (Simples Nacional)">
              <Select allowClear showSearch optionFilterProp="label" options={OPCOES_CSOSN} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="modalidadeBaseCalculo" label="Modalidade da base de cálculo">
              <Select allowClear options={OPCOES_MODALIDADE_ICMS} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="aliquota" label="Alíquota (%)">
              <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="porcentoBaseCalculo" label="% da base de cálculo">
              <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="valorPauta" label="Valor de pauta">
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="valorPrecoMaximo" label="Preço máximo">
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>

        <Divider titlePlacement="left">Substituição Tributária</Divider>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="mva" label="MVA (%)" tooltip="Margem de Valor Agregado">
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="modalidadeBaseCalculoSt" label="Modalidade da base de cálculo ST">
              <Select allowClear options={OPCOES_MODALIDADE_ICMS_ST} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="aliquotaInternaSt" label="Alíquota interna ST (%)">
              <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="aliquotaInterestadualSt" label="Alíquota interestadual ST (%)">
              <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="aliquotaIcmsSt" label="Alíquota ICMS ST (%)">
              <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="porcentoBaseCalculoSt" label="% da base de cálculo ST">
              <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="valorPautaSt" label="Valor de pauta ST">
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="valorPrecoMaximoSt" label="Preço máximo ST">
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
