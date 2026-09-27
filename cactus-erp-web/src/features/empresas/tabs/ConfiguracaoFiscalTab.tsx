import { useEffect } from 'react'
import { App, Button, Col, DatePicker, Flex, Form, Input, InputNumber, Row, Select, Skeleton } from 'antd'
import { SaveOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useConfiguracaoFiscal, useSalvarConfiguracaoFiscal } from '../queries'
import type { EmpresaConfiguracaoFiscalRequest } from '../types'

interface ConfiguracaoFiscalTabProps {
  empresaId: number
}

const VALORES_PADRAO: EmpresaConfiguracaoFiscalRequest = {
  ambienteFiscal: 'HOMOLOGACAO',
}

const OPCOES_CERTIFICADO = [
  { value: 'A1', label: 'A1 — Arquivo (.pfx/.p12)' },
  { value: 'A3', label: 'A3 — Hardware (token/smartcard)' },
]

const OPCOES_AMBIENTE = [
  { value: 'HOMOLOGACAO', label: 'Homologação' },
  { value: 'PRODUCAO', label: 'Produção' },
]

/**
 * Sub-recurso 1:1 (upsert), mesmo padrão de `ConfiguracaoOperacionalTab`.
 * `certificadoAlias` só referencia uma entrada de keystore/secret manager —
 * a senha do certificado NUNCA tem campo aqui, de propósito (ver Javadoc do
 * backend). Ambiente sempre nasce em `HOMOLOGACAO` — trocar pra `PRODUCAO`
 * é uma ação deliberada, nunca o default.
 */
export default function ConfiguracaoFiscalTab({ empresaId }: ConfiguracaoFiscalTabProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<EmpresaConfiguracaoFiscalRequest>()
  const { data: config, isLoading } = useConfiguracaoFiscal(empresaId)
  const salvar = useSalvarConfiguracaoFiscal(empresaId)

  const { authorities } = useAuth()
  const podeSalvar = hasAuthority(authorities, 'CADASTRO_EMPRESA_EDITAR')

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    form.setFieldsValue({
      ...(config ?? VALORES_PADRAO),
      // @ts-expect-error DatePicker consome dayjs, não a string ISO do backend
      certificadoValidade: config?.certificadoValidade ? dayjs(config.certificadoValidade) : undefined,
    })
  }, [config, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    const request: EmpresaConfiguracaoFiscalRequest = {
      ...valores,
      certificadoValidade: valores.certificadoValidade
        ? // @ts-expect-error valores.certificadoValidade é um objeto dayjs em runtime
          valores.certificadoValidade.format('YYYY-MM-DD')
        : undefined,
    }
    try {
      await salvar.mutateAsync(request)
      message.success('Configuração fiscal salva.')
    } catch (erro) {
      const mensagem = erro instanceof Error ? erro.message : 'Falha ao salvar configuração fiscal.'
      message.error(mensagem)
    }
  }

  if (isLoading) {
    return <Skeleton active paragraph={{ rows: 6 }} />
  }

  return (
    <Form form={form} layout="vertical" initialValues={VALORES_PADRAO} disabled={!podeSalvar}>
      <Flex justify="flex-end" style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<SaveOutlined />} loading={salvar.isPending} disabled={!podeSalvar} onClick={handleSalvar}>
          Salvar configuração fiscal
        </Button>
      </Flex>

      <Row gutter={12}>
        <Col span={8}>
          <Form.Item name="tipoCertificado" label="Tipo de certificado digital">
            <Select allowClear options={OPCOES_CERTIFICADO} />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="certificadoAlias" label="Alias do certificado" tooltip="Referência ao keystore/secret manager — a senha do certificado nunca é cadastrada aqui">
            <Input maxLength={100} />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="certificadoValidade" label="Validade do certificado">
            <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={12}>
        <Col span={8}>
          <Form.Item name="ambienteFiscal" label="Ambiente" rules={[{ required: true }]}>
            <Select options={OPCOES_AMBIENTE} />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="csc" label="CSC (Código de Segurança do Contribuinte)">
            <Input maxLength={100} />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="cscIdToken" label="CSC — Id Token">
            <Input maxLength={10} />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={12}>
        <Col span={4}>
          <Form.Item name="serieNfe" label="Série NFe">
            <Input maxLength={3} />
          </Form.Item>
        </Col>
        <Col span={4}>
          <Form.Item name="proximoNumeroNfe" label="Próximo número NFe">
            <InputNumber<number> style={{ width: '100%' }} min={1} />
          </Form.Item>
        </Col>
        <Col span={4}>
          <Form.Item name="serieNfce" label="Série NFCe">
            <Input maxLength={3} />
          </Form.Item>
        </Col>
        <Col span={4}>
          <Form.Item name="proximoNumeroNfce" label="Próximo número NFCe">
            <InputNumber<number> style={{ width: '100%' }} min={1} />
          </Form.Item>
        </Col>
        <Col span={4}>
          <Form.Item name="serieNfse" label="Série NFSe">
            <Input maxLength={3} />
          </Form.Item>
        </Col>
        <Col span={4}>
          <Form.Item name="proximoNumeroNfse" label="Próximo número NFSe">
            <InputNumber<number> style={{ width: '100%' }} min={1} />
          </Form.Item>
        </Col>
      </Row>
    </Form>
  )
}
