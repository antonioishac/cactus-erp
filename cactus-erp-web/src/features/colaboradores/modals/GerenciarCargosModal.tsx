import { useEffect } from 'react'
import { App, Button, Col, Divider, Form, Input, InputNumber, Modal, Row, Select, Table } from 'antd'
import { EditOutlined, PlusOutlined } from '@ant-design/icons'
import { useLookupModal } from '@shared/hooks/useLookupModal'
import { useAtualizarCargo, useCargos, useCriarCargo } from '../queries'
import type { CargoRequest, CargoResponse } from '../types'

interface GerenciarCargosModalProps {
  open: boolean
  onClose: () => void
}

const VALORES_PADRAO: Partial<CargoRequest> = { situacao: 'ATIVO' }

/**
 * Modal de gerenciamento — aberto a partir do `Select` de Cargo no `RhTab`
 * (botão "+" ao lado), não uma página/rota própria: se o usuário está no
 * meio de preencher um Colaborador e percebe que falta um cargo, ele cadastra
 * sem perder o progresso do formulário que já estava preenchendo. Fecha as
 * pendências 0b/1a registradas no CLAUDE.md (Cargo/Setor sem CRUD pela UI).
 */
export default function GerenciarCargosModal({ open, onClose }: GerenciarCargosModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<CargoRequest>()
  const { data: cargos, isLoading } = useCargos()
  const criar = useCriarCargo()
  const atualizar = useAtualizarCargo()
  const lookup = useLookupModal<CargoResponse>()
  const salvando = criar.isPending || atualizar.isPending

  useEffect(() => {
    if (open) {
      form.setFieldsValue(lookup.itemEmEdicao ?? VALORES_PADRAO)
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps -- só reage a abrir o modal/trocar o item em edição
  }, [open, lookup.itemEmEdicao])

  async function handleSalvar() {
    const valores = await form.validateFields()
    try {
      if (lookup.itemEmEdicao) {
        await atualizar.mutateAsync({ id: lookup.itemEmEdicao.id, request: valores })
        message.success('Cargo atualizado.')
      } else {
        await criar.mutateAsync(valores)
        message.success('Cargo criado.')
      }
      lookup.fechar()
      form.resetFields()
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar cargo.')
    }
  }

  return (
    <Modal title="Gerenciar cargos" open={open} onCancel={onClose} footer={null} width={720} destroyOnHidden>
      <Table<CargoResponse>
        rowKey="id"
        size="small"
        loading={isLoading}
        dataSource={cargos}
        pagination={false}
        columns={[
          { title: 'Nome', dataIndex: 'nome' },
          { title: 'Salário', dataIndex: 'salario', render: (v?: number) => (v != null ? `R$ ${v.toFixed(2)}` : '—') },
          {
            title: 'Ações',
            key: 'acoes',
            width: 60,
            render: (_, cargo) => <Button type="text" icon={<EditOutlined />} onClick={() => lookup.abrirEdicao(cargo)} />,
          },
        ]}
      />

      <Divider style={{ margin: '16px 0' }} />

      {/* name="cargoForm": sem isso o Ant Design usaria `id="nome"` pro campo
          "Nome" deste form, colidindo com o campo "nome" do form de
          Colaborador por trás (Tabs mantém painéis inativos montados no DOM)
          — dois elementos com o mesmo id é HTML inválido e confundia
          getByLabel/querySelector (achado real ao vivo, 2026-09-01). */}
      <Form form={form} name="cargoForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12} align="bottom">
          <Col span={8}>
            <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]} style={{ marginBottom: 12 }}>
              <Input />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="descricao" label="Descrição" rules={[{ max: 250 }]} style={{ marginBottom: 12 }}>
              <Input />
            </Form.Item>
          </Col>
          <Col span={4}>
            <Form.Item name="salario" label="Salário-base" style={{ marginBottom: 12 }}>
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} />
            </Form.Item>
          </Col>
          <Col span={4}>
            <Form.Item name="situacao" label="Situação" style={{ marginBottom: 12 }}>
              <Select options={[{ value: 'ATIVO', label: 'Ativo' }, { value: 'INATIVO', label: 'Inativo' }]} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={6}>
            <Form.Item name="cbo1994" label="CBO 1994">
              <Input maxLength={10} />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="cbo2002" label="CBO 2002">
              <Input maxLength={10} />
            </Form.Item>
          </Col>
        </Row>
        <Button
          type="primary"
          icon={lookup.itemEmEdicao ? <EditOutlined /> : <PlusOutlined />}
          loading={salvando}
          onClick={handleSalvar}
        >
          {lookup.itemEmEdicao ? 'Salvar alterações' : 'Adicionar cargo'}
        </Button>
        {lookup.itemEmEdicao && (
          <Button style={{ marginLeft: 8 }} onClick={() => { lookup.fechar(); form.resetFields() }}>
            Cancelar edição
          </Button>
        )}
      </Form>
    </Modal>
  )
}
