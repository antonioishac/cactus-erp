import { useEffect, useState } from 'react'
import { App, Col, Divider, Form, Input, InputNumber, Modal, Row, Select, Typography } from 'antd'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useAtualizarOperacaoFiscal, useBuscaCfop, useCriarOperacaoFiscal } from '../queries'
import { CODIGO_TRIBUTACAO_ISS_LABEL, MODALIDADE_BASE_CALCULO_ISS_LABEL } from '../types'
import type { OperacaoFiscalRequest, OperacaoFiscalResponse } from '../types'

const { Text } = Typography

interface OperacaoFiscalModalProps {
  open: boolean
  valorInicial?: OperacaoFiscalResponse
  onClose: () => void
}

const VALORES_PADRAO: Partial<OperacaoFiscalRequest> = { situacao: 'ATIVO' }

const OPCOES_MODALIDADE_ISS = Object.entries(MODALIDADE_BASE_CALCULO_ISS_LABEL).map(([value, label]) => ({ value, label }))
const OPCOES_CODIGO_TRIBUTACAO_ISS = Object.entries(CODIGO_TRIBUTACAO_ISS_LABEL).map(([value, label]) => ({ value, label }))

/**
 * Tipo de venda/operação (ex: "Venda a consumidor final", "Revenda", "Interestadual") — cada
 * uma com seu CFOP. `issConfiguracao` é 1:1 direto com a operação (não com a célula
 * Grupo×Operação como o ICMS/PIS/COFINS/IPI) — só faz sentido preencher quando a operação é
 * de prestação de serviço, não venda de mercadoria (ver ANÁLISE TRIBUTÁRIA no CLAUDE.md raiz).
 */
export default function OperacaoFiscalModal({ open, valorInicial, onClose }: OperacaoFiscalModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<OperacaoFiscalRequest>()
  const [termoCfop, setTermoCfop] = useState<string | undefined>(undefined)
  const { data: cfops, isLoading: carregandoCfops } = useBuscaCfop(termoCfop)
  const criar = useCriarOperacaoFiscal()
  const atualizar = useAtualizarOperacaoFiscal()
  const salvando = criar.isPending || atualizar.isPending

  useEffect(() => {
    if (open) {
      form.setFieldsValue(valorInicial ?? (VALORES_PADRAO as OperacaoFiscalRequest))
    }
  }, [open, valorInicial, form])

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        if (valorInicial) {
          await atualizar.mutateAsync({ id: valorInicial.id, request: valores })
          message.success('Operação fiscal atualizada.')
        } else {
          await criar.mutateAsync(valores)
          message.success('Operação fiscal criada.')
        }
        form.resetFields()
        setTermoCfop(undefined)
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao salvar operação fiscal.')
      }
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar operação fiscal' : 'Nova operação fiscal'}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={salvando}
      width={640}
      destroyOnHidden
    >
      <Form form={form} name="operacaoFiscalForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12}>
          <Col span={16}>
            <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]}>
              <Input placeholder="Ex: Venda a consumidor final (dentro do estado)" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="situacao" label="Situação">
              <Select
                options={[
                  { value: 'ATIVO', label: 'Ativo' },
                  { value: 'INATIVO', label: 'Inativo' },
                ]}
              />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="cfopId" label="CFOP" tooltip="Código Fiscal de Operações e Prestações — vai na nota fiscal">
              <AsyncSelect
                allowClear
                opcoes={cfops}
                carregando={carregandoCfops}
                termo={termoCfop}
                onTermoChange={setTermoCfop}
                toOption={(c) => ({ value: c.id, label: `${c.codigo} — ${c.descricao}` })}
                labelValorAtual={valorInicial?.cfopCodigo ? String(valorInicial.cfopCodigo) : undefined}
              />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="descricaoNaNf" label="Descrição na nota fiscal" rules={[{ max: 100 }]}>
              <Input />
            </Form.Item>
          </Col>
        </Row>
        <Form.Item name="observacao" label="Observação" rules={[{ max: 250 }]}>
          <Input.TextArea rows={2} maxLength={250} showCount />
        </Form.Item>

        <Divider titlePlacement="left">Configuração de ISS (só pra operação de serviço)</Divider>
        <Text type="secondary" style={{ display: 'block', marginBottom: 12 }}>
          Deixe em branco se esta operação é de venda de mercadoria — ISS só incide sobre prestação de serviço.
        </Text>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name={['issConfiguracao', 'modalidadeBaseCalculo']} label="Modalidade da base de cálculo">
              <Select allowClear options={OPCOES_MODALIDADE_ISS} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name={['issConfiguracao', 'codigoTributacao']} label="Código de tributação">
              <Select allowClear options={OPCOES_CODIGO_TRIBUTACAO_ISS} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name={['issConfiguracao', 'aliquotaPercentual']} label="Alíquota (%)">
              <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name={['issConfiguracao', 'itemListaServico']} label="Item da lista de serviço" tooltip="LC 116/2003">
              <InputNumber style={{ width: '100%' }} min={0} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name={['issConfiguracao', 'porcentoBaseCalculo']} label="% da base de cálculo">
              <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
