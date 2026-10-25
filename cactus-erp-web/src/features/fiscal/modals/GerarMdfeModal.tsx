import { useMemo } from 'react'
import { useNavigate } from 'react-router-dom'
import { Alert, App, Col, DatePicker, Form, Input, InputNumber, Modal, Row, Select } from 'antd'
import type { Dayjs } from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { useCtesDisponiveisParaMdfe, useGerarMdfe } from '../queries'
import type { GerarMdfeRequest } from '../types'

interface GerarMdfeModalProps {
  open: boolean
  onClose: () => void
}

interface FormValues extends Omit<GerarMdfeRequest, 'empresaId' | 'dataInicioViagem'> {
  dataInicioViagem?: Dayjs
}

/**
 * MDF-e agrupa CT-e já AUTORIZADOS da empresa ativa numa viagem. Carga, peso e
 * municípios saem dos próprios CT-e (backend) — aqui só o que é da viagem:
 * veículo, condutor e data. O seletor vem de GET /mdfes/ctes-disponiveis — a
 * mesma regra que POST /mdfes valida, aplicada no banco (sem limite de página).
 */
export default function GerarMdfeModal({ open, onClose }: GerarMdfeModalProps) {
  const { message } = App.useApp()
  const navigate = useNavigate()
  const { empresaAtivaId } = useAuth()
  const [form] = Form.useForm<FormValues>()
  const gerar = useGerarMdfe()
  const { data: ctes, isLoading: carregando } = useCtesDisponiveisParaMdfe(empresaAtivaId, open)

  const opcoes = useMemo(
    () =>
      (ctes ?? []).map((cte) => ({
        value: cte.id,
        label: `CT-e ${cte.serie}/${cte.numero} — Venda #${cte.vendaId} — carga R$ ${(cte.valorTotalCarga ?? 0).toFixed(2)}`,
      })),
    [ctes],
  )

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      if (empresaAtivaId == null) {
        message.error('Selecione uma empresa ativa antes de gerar o MDF-e.')
        return
      }
      const request: GerarMdfeRequest = {
        ...valores,
        empresaId: empresaAtivaId,
        veiculoPlaca: valores.veiculoPlaca.toUpperCase(),
        veiculoUf: valores.veiculoUf?.toUpperCase() || undefined,
        dataInicioViagem: valores.dataInicioViagem ? valores.dataInicioViagem.format('YYYY-MM-DD') : undefined,
      }
      try {
        const mdfe = await gerar.mutateAsync(request)
        message.success('MDF-e gerado em rascunho.')
        form.resetFields()
        onClose()
        navigate(`/fiscal/mdfes/${mdfe.id}`)
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao gerar MDF-e.')
      }
    })
  }

  return (
    <Modal
      title="Novo MDF-e"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Gerar MDF-e"
      cancelText="Cancelar"
      confirmLoading={gerar.isPending}
      width={680}
      destroyOnHidden
    >
      {!carregando && opcoes.length === 0 && (
        <Alert
          style={{ marginBottom: 16 }}
          type="info"
          showIcon
          title="Nenhum CT-e disponível"
          description="Só entram CT-e autorizados da empresa ativa que ainda não estão em outro MDF-e."
        />
      )}
      <Form form={form} name="gerarMdfeForm" layout="vertical">
        <Form.Item name="cteIds" label="CT-e da viagem" rules={[{ required: true, message: 'Selecione ao menos um CT-e' }]}>
          <Select mode="multiple" loading={carregando} options={opcoes} placeholder="CT-e autorizados e livres" />
        </Form.Item>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item
              name="veiculoPlaca"
              label="Placa"
              rules={[
                { required: true, message: 'Informe a placa' },
                { pattern: /^[A-Za-z0-9]{7}$/, message: '7 caracteres, sem hífen' },
              ]}
            >
              <Input maxLength={7} placeholder="ABC1D23" />
            </Form.Item>
          </Col>
          <Col span={4}>
            <Form.Item name="veiculoUf" label="UF" rules={[{ len: 2, message: '2 letras' }]} tooltip="Vazio = UF da empresa">
              <Input maxLength={2} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="veiculoRenavam" label="RENAVAM" rules={[{ max: 11 }]}>
              <Input maxLength={11} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="veiculoTaraKg" label="Tara (kg)" rules={[{ required: true, message: 'Informe a tara' }]}>
              <InputNumber<number> style={{ width: '100%' }} min={0} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="veiculoCapacidadeKg" label="Capacidade (kg)">
              <InputNumber<number> style={{ width: '100%' }} min={0} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="rntrc" label="RNTRC" rules={[{ max: 8 }]} tooltip="Vazio = RNTRC do primeiro CT-e">
              <Input maxLength={8} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="condutorNome" label="Condutor" rules={[{ required: true, message: 'Informe o condutor' }, { max: 60 }]}>
              <Input maxLength={60} />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item
              name="condutorCpf"
              label="CPF do condutor"
              rules={[
                { required: true, message: 'Informe o CPF' },
                { pattern: /^\d{11}$/, message: '11 dígitos, só números' },
              ]}
            >
              <Input maxLength={11} />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="dataInicioViagem" label="Início da viagem" tooltip="Vazio = hoje">
              <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
