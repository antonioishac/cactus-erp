import { useEffect } from 'react'
import { App, Button, Col, Divider, Form, Input, Modal, Row, Select, Table } from 'antd'
import { EditOutlined, PlusOutlined } from '@ant-design/icons'
import { useLookupModal } from '@shared/hooks/useLookupModal'
import { useAtualizarSetor, useCriarSetor, useSetores } from '../queries'
import type { SetorRequest, SetorResponse } from '../types'

interface GerenciarSetoresModalProps {
  open: boolean
  onClose: () => void
}

const VALORES_PADRAO: Partial<SetorRequest> = { situacao: 'ATIVO' }

/** Mesmo padrão de `GerenciarCargosModal.tsx`, Setor é mais simples (sem salário/CBO). */
export default function GerenciarSetoresModal({ open, onClose }: GerenciarSetoresModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<SetorRequest>()
  const { data: setores, isLoading } = useSetores()
  const criar = useCriarSetor()
  const atualizar = useAtualizarSetor()
  const lookup = useLookupModal<SetorResponse>()
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
        message.success('Setor atualizado.')
      } else {
        await criar.mutateAsync(valores)
        message.success('Setor criado.')
      }
      lookup.fechar()
      form.resetFields()
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar setor.')
    }
  }

  return (
    <Modal title="Gerenciar setores" open={open} onCancel={onClose} footer={null} width={640} destroyOnHidden>
      <Table<SetorResponse>
        rowKey="id"
        size="small"
        loading={isLoading}
        dataSource={setores}
        pagination={false}
        columns={[
          { title: 'Nome', dataIndex: 'nome' },
          { title: 'Descrição', dataIndex: 'descricao', render: (v) => v || '—' },
          {
            title: 'Ações',
            key: 'acoes',
            width: 60,
            render: (_, setor) => <Button type="text" icon={<EditOutlined />} onClick={() => lookup.abrirEdicao(setor)} />,
          },
        ]}
      />

      <Divider style={{ margin: '16px 0' }} />

      {/* name="setorForm": mesmo achado/motivo de GerenciarCargosModal.tsx —
          evita colisão de id="nome" com o form de Colaborador por trás. */}
      <Form form={form} name="setorForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12} align="bottom">
          <Col span={10}>
            <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]} style={{ marginBottom: 12 }}>
              <Input />
            </Form.Item>
          </Col>
          <Col span={10}>
            <Form.Item name="descricao" label="Descrição" rules={[{ max: 250 }]} style={{ marginBottom: 12 }}>
              <Input />
            </Form.Item>
          </Col>
          <Col span={4}>
            <Form.Item name="situacao" label="Situação" style={{ marginBottom: 12 }}>
              <Select options={[{ value: 'ATIVO', label: 'Ativo' }, { value: 'INATIVO', label: 'Inativo' }]} />
            </Form.Item>
          </Col>
        </Row>
        <Button
          type="primary"
          icon={lookup.itemEmEdicao ? <EditOutlined /> : <PlusOutlined />}
          loading={salvando}
          onClick={handleSalvar}
        >
          {lookup.itemEmEdicao ? 'Salvar alterações' : 'Adicionar setor'}
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
