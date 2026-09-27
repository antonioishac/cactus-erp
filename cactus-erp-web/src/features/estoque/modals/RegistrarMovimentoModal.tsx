import { useState } from 'react'
import { App, Col, Form, Input, InputNumber, Modal, Row, Segmented, Select } from 'antd'
import { useAuth } from '@features/auth/useAuth'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useBuscaProdutosParaSelecao, useBuscaUsuariosParaSelecao, useDepositosAtivos, useRegistrarMovimento } from '../queries'
import { ORIGEM_MOVIMENTO_LABEL, TIPO_MOVIMENTO_LANCAVEL } from '../types'
import type { EstoqueMovimentoRequest, TipoMovimentoEstoque } from '../types'

interface RegistrarMovimentoModalProps {
  open: boolean
  onClose: () => void
}

const VALORES_PADRAO: Partial<EstoqueMovimentoRequest> = { origemTipo: 'AJUSTE_MANUAL' }

/**
 * `loteId`/`numeroSerieId` ficam de fora nesta Fase A (telas de Lote/Número de
 * Série ainda não existem — Fase B) — são opcionais no `EstoqueMovimentoRequest`,
 * então omiti-los é um lançamento válido pra produto sem rastreabilidade por
 * lote/série. `usuarioId` nunca é campo aqui — o backend extrai do JWT.
 */
export default function RegistrarMovimentoModal({ open, onClose }: RegistrarMovimentoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<EstoqueMovimentoRequest>()
  const { empresaAtivaId } = useAuth()
  const { data: depositos, isLoading: carregandoDepositos } = useDepositosAtivos(empresaAtivaId ?? undefined)
  const [termoProduto, setTermoProduto] = useState<string | undefined>(undefined)
  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termoProduto)
  const [termoAprovador, setTermoAprovador] = useState<string | undefined>(undefined)
  const { data: usuarios, isLoading: carregandoUsuarios } = useBuscaUsuariosParaSelecao(termoAprovador)
  const registrar = useRegistrarMovimento()

  const tipoMovimento = Form.useWatch('tipoMovimento', form) as TipoMovimentoEstoque | undefined

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await registrar.mutateAsync(valores)
        message.success('Movimento registrado com sucesso.')
        form.resetFields()
        setTermoProduto(undefined)
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao registrar movimento.')
      }
    })
  }

  return (
    <Modal
      title="Registrar movimento de estoque"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Registrar"
      cancelText="Cancelar"
      confirmLoading={registrar.isPending}
      width={640}
      destroyOnHidden
    >
      <Form form={form} name="registrarMovimentoForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12}>
          <Col span={24}>
            <Form.Item name="produtoId" label="Produto" rules={[{ required: true, message: 'Selecione o produto' }]}>
              <AsyncSelect
                opcoes={produtos}
                carregando={carregandoProdutos}
                termo={termoProduto}
                onTermoChange={setTermoProduto}
                toOption={(p) => ({ value: p.id, label: `${p.codigoInterno} — ${p.nome}` })}
              />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="depositoId" label="Depósito" rules={[{ required: true, message: 'Selecione o depósito' }]}>
              <Select showSearch loading={carregandoDepositos} optionFilterProp="label" options={depositos?.map((d) => ({ value: d.id, label: d.nome }))} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="tipoMovimento" label="Tipo de movimento" rules={[{ required: true, message: 'Selecione o tipo' }]}>
              <Select options={TIPO_MOVIMENTO_LANCAVEL} />
            </Form.Item>
          </Col>
        </Row>
        {tipoMovimento === 'AJUSTE' && (
          <Row gutter={12}>
            <Col span={24}>
              <Form.Item name="direcaoAjuste" label="Direção do ajuste" rules={[{ required: true, message: 'Selecione a direção' }]}>
                <Segmented
                  options={[
                    { label: 'Acréscimo (soma)', value: 'ACRESCIMO' },
                    { label: 'Decréscimo (subtrai)', value: 'DECRESCIMO' },
                  ]}
                />
              </Form.Item>
            </Col>
          </Row>
        )}
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="quantidade" label="Quantidade" rules={[{ required: true, message: 'Informe a quantidade' }]}>
              <InputNumber<number> style={{ width: '100%' }} min={0.000001} precision={3} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="custoUnitario" label="Custo unitário (R$)" tooltip="Obrigatório em ENTRADA pra recalcular o custo médio ponderado.">
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item
              name="aprovadorId"
              label="Aprovador"
              tooltip="Usuário aprovador — obrigatório em AJUSTE/PERDA que excedam o limite configurado."
            >
              <AsyncSelect
                allowClear
                opcoes={usuarios}
                carregando={carregandoUsuarios}
                termo={termoAprovador}
                onTermoChange={setTermoAprovador}
                toOption={(u) => ({ value: u.id, label: `${u.nome} (${u.login})` })}
              />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="origemTipo" label="Origem" rules={[{ required: true }]}>
              <Select options={Object.entries(ORIGEM_MOVIMENTO_LABEL).map(([value, label]) => ({ value, label }))} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="origemId" label="Id de referência da origem" tooltip="Ex: id do pedido de compra, da transferência, do inventário — deixe em branco se não houver.">
              <InputNumber style={{ width: '100%' }} min={1} />
            </Form.Item>
          </Col>
        </Row>
        <Form.Item name="observacao" label="Observação" rules={[{ max: 250 }]}>
          <Input.TextArea rows={2} maxLength={250} showCount />
        </Form.Item>
      </Form>
    </Modal>
  )
}
