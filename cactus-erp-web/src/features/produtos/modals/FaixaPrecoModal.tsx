import { useEffect } from 'react'
import { Col, DatePicker, Form, Input, InputNumber, Modal, Row, Select } from 'antd'
import dayjs, { type Dayjs } from 'dayjs'
import type { ProdutoFaixaPrecoDTO } from '../types'

interface FaixaPrecoModalProps {
  open: boolean
  valorInicial?: ProdutoFaixaPrecoDTO
  onCancel: () => void
  onSalvar: (faixa: ProdutoFaixaPrecoDTO) => void
}

const VALORES_PADRAO: Partial<ProdutoFaixaPrecoDTO> = { quantidadeMinima: 1, situacao: 'ATIVO' }

/**
 * Um único formulário cobre os dois usos de `ProdutoFaixaPreco` (ver Javadoc
 * de `ProdutoFaixaPrecoDTO.java` e a seção própria no CLAUDE.md raiz —
 * "é UM conceito só"): quantidade mínima = 1 + datas preenchidas vira
 * promoção temporária; quantidade mínima > 1 sem datas vira desconto
 * permanente por atacado. Não há dois formulários/dois modos — o texto de
 * apoio abaixo do campo é o que ensina a diferença.
 */
export default function FaixaPrecoModal({ open, valorInicial, onCancel, onSalvar }: FaixaPrecoModalProps) {
  const [form] = Form.useForm<Record<string, unknown>>()

  useEffect(() => {
    if (open) {
      form.setFieldsValue({
        ...(valorInicial ?? VALORES_PADRAO),
        dataInicio: valorInicial?.dataInicio ? dayjs(valorInicial.dataInicio) : undefined,
        dataFim: valorInicial?.dataFim ? dayjs(valorInicial.dataFim) : undefined,
      })
    }
  }, [open, valorInicial, form])

  const handleOk = () => {
    form.validateFields().then((valores) => {
      const dataInicio = valores.dataInicio as Dayjs | undefined
      const dataFim = valores.dataFim as Dayjs | undefined
      onSalvar({
        ...(valores as unknown as ProdutoFaixaPrecoDTO),
        dataInicio: dataInicio ? dataInicio.format('YYYY-MM-DD') : undefined,
        dataFim: dataFim ? dataFim.format('YYYY-MM-DD') : undefined,
      })
      form.resetFields()
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar faixa de preço' : 'Adicionar faixa de preço'}
      open={open}
      onCancel={onCancel}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      destroyOnHidden
    >
      <Form form={form} name="faixaPrecoForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item
              name="quantidadeMinima"
              label="Quantidade mínima"
              rules={[{ required: true, message: 'Informe a quantidade mínima' }]}
              tooltip="1 = vale pra qualquer quantidade (use com vigência de datas pra virar promoção). Maior que 1 = desconto por atacado a partir dessa quantidade."
            >
              <InputNumber style={{ width: '100%' }} min={0.001} precision={3} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="valorUnitario" label="Valor unitário nesta faixa (R$)" rules={[{ required: true, message: 'Informe o valor' }]}>
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="situacao" label="Situação">
              <Select options={[{ value: 'ATIVO', label: 'Ativo' }, { value: 'INATIVO', label: 'Inativo' }]} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={24}>
            <Form.Item name="rotulo" label="Rótulo" rules={[{ max: 50 }]} tooltip='Ex: "ATACADO", "CX FECHADA", "PROMOÇÃO"'>
              <Input maxLength={50} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item
              name="dataInicio"
              label="Início da vigência"
              tooltip="Deixe em branco pra valer sempre (desconto permanente). Preencha os dois pra virar promoção temporária."
            >
              <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="dataFim" label="Fim da vigência">
              <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
