import { useState } from 'react'
import { App, Col, Form, Input, InputNumber, Modal, Row, Select } from 'antd'
import { useAuth } from '@features/auth/useAuth'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useBuscaProdutosParaSelecao, useCriarTransferencia, useDepositosAtivos } from '../queries'
import type { TransferenciaEstoqueRequest } from '../types'

interface NovaTransferenciaModalProps {
  open: boolean
  onClose: () => void
}

/**
 * Só cria a transferência (situação inicial `SOLICITADA`) — enviar/receber são
 * ações separadas na tabela (ver `TransferenciasListPage`), cada uma exige uma
 * confirmação explícita do usuário no depósito certo, nunca automáticas.
 */
export default function NovaTransferenciaModal({ open, onClose }: NovaTransferenciaModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<TransferenciaEstoqueRequest>()
  const { empresaAtivaId } = useAuth()
  const { data: depositos, isLoading: carregandoDepositos } = useDepositosAtivos(empresaAtivaId ?? undefined)
  const [termoProduto, setTermoProduto] = useState<string | undefined>(undefined)
  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termoProduto)
  const criar = useCriarTransferencia()

  const depositoOrigemId = Form.useWatch('depositoOrigemId', form)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await criar.mutateAsync(valores)
        message.success('Transferência criada — use "Enviar" na listagem quando estiver pronta pra sair do depósito de origem.')
        form.resetFields()
        setTermoProduto(undefined)
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao criar transferência.')
      }
    })
  }

  return (
    <Modal
      title="Nova transferência entre depósitos"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Criar"
      cancelText="Cancelar"
      confirmLoading={criar.isPending}
      width={600}
      destroyOnHidden
    >
      <Form form={form} name="novaTransferenciaForm" layout="vertical">
        <Form.Item name="produtoId" label="Produto" rules={[{ required: true, message: 'Selecione o produto' }]}>
          <AsyncSelect
            opcoes={produtos}
            carregando={carregandoProdutos}
            termo={termoProduto}
            onTermoChange={setTermoProduto}
            toOption={(p) => ({ value: p.id, label: `${p.codigoInterno} — ${p.nome}` })}
          />
        </Form.Item>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="depositoOrigemId" label="Depósito de origem" rules={[{ required: true, message: 'Selecione a origem' }]}>
              <Select showSearch loading={carregandoDepositos} optionFilterProp="label" options={depositos?.map((d) => ({ value: d.id, label: d.nome }))} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="depositoDestinoId"
              label="Depósito de destino"
              rules={[
                { required: true, message: 'Selecione o destino' },
                {
                  validator: (_, value) =>
                    value != null && value === depositoOrigemId
                      ? Promise.reject(new Error('Origem e destino não podem ser o mesmo depósito'))
                      : Promise.resolve(),
                },
              ]}
            >
              <Select showSearch loading={carregandoDepositos} optionFilterProp="label" options={depositos?.map((d) => ({ value: d.id, label: d.nome }))} />
            </Form.Item>
          </Col>
        </Row>
        <Form.Item name="quantidade" label="Quantidade" rules={[{ required: true, message: 'Informe a quantidade' }]}>
          <InputNumber<number> style={{ width: '100%' }} min={0.000001} precision={3} />
        </Form.Item>
        <Form.Item name="observacao" label="Observação" rules={[{ max: 250 }]}>
          <Input.TextArea rows={2} maxLength={250} showCount />
        </Form.Item>
      </Form>
    </Modal>
  )
}
