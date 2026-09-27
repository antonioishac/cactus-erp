import { useEffect } from 'react'
import { App, Col, Form, Input, Modal, Row, Select } from 'antd'
import { useAtualizarGrupoTributario, useCriarGrupoTributario } from '../queries'
import { ORIGEM_MERCADORIA_OPCOES } from '../types'
import type { GrupoTributarioRequest, GrupoTributarioResponse } from '../types'

interface GrupoTributarioModalProps {
  open: boolean
  valorInicial?: GrupoTributarioResponse
  onClose: () => void
}

const VALORES_PADRAO: Partial<GrupoTributarioRequest> = { situacao: 'ATIVO', origemMercadoria: '0' }

/** Perfil tributário do produto (ex: "Revenda sujeita a ICMS-ST") + origem da mercadoria (campo `orig` da NFe, 0-9) — ver ANÁLISE TRIBUTÁRIA no CLAUDE.md raiz. */
export default function GrupoTributarioModal({ open, valorInicial, onClose }: GrupoTributarioModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<GrupoTributarioRequest>()
  const criar = useCriarGrupoTributario()
  const atualizar = useAtualizarGrupoTributario()
  const salvando = criar.isPending || atualizar.isPending

  useEffect(() => {
    if (open) {
      form.setFieldsValue(valorInicial ?? (VALORES_PADRAO as GrupoTributarioRequest))
    }
  }, [open, valorInicial, form])

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        if (valorInicial) {
          await atualizar.mutateAsync({ id: valorInicial.id, request: valores })
          message.success('Grupo tributário atualizado.')
        } else {
          await criar.mutateAsync(valores)
          message.success('Grupo tributário criado.')
        }
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao salvar grupo tributário.')
      }
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar grupo tributário' : 'Novo grupo tributário'}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={salvando}
      destroyOnHidden
    >
      <Form form={form} name="grupoTributarioForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12}>
          <Col span={16}>
            <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]}>
              <Input placeholder="Ex: Revenda sujeita a ICMS-ST" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item
              name="origemMercadoria"
              label="Origem da mercadoria"
              rules={[{ required: true, message: 'Selecione a origem' }]}
              tooltip="Campo 'orig' da NFe"
            >
              <Select options={ORIGEM_MERCADORIA_OPCOES} />
            </Form.Item>
          </Col>
        </Row>
        <Form.Item name="observacao" label="Observação" rules={[{ max: 250 }]}>
          <Input.TextArea rows={2} maxLength={250} showCount />
        </Form.Item>
        <Form.Item name="situacao" label="Situação">
          <Select
            options={[
              { value: 'ATIVO', label: 'Ativo' },
              { value: 'INATIVO', label: 'Inativo' },
            ]}
          />
        </Form.Item>
      </Form>
    </Modal>
  )
}
