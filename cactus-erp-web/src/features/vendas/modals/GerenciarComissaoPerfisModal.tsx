import { useEffect } from 'react'
import { App, Button, Col, Divider, Form, Input, Modal, Row, Select, Table } from 'antd'
import { EditOutlined, PlusOutlined } from '@ant-design/icons'
import { useLookupModal } from '@shared/hooks/useLookupModal'
import { useAtualizarComissaoPerfil, useComissaoPerfis, useCriarComissaoPerfil } from '../queries'
import type { ComissaoPerfilRequest, ComissaoPerfilResponse } from '../types'

interface GerenciarComissaoPerfisModalProps {
  open: boolean
  onClose: () => void
}

const VALORES_PADRAO: Partial<ComissaoPerfilRequest> = { situacao: 'ATIVO' }

/**
 * Modal de gerenciamento, aberto pelo botão "+" ao lado do `Select` de Perfil
 * de Comissão no `VendedorFormPage` — mesmo padrão de `GerenciarCargosModal`
 * (Colaborador): se o usuário está no meio de cadastrar um Vendedor e falta
 * um perfil, cadastra sem perder o progresso do formulário por trás.
 */
export default function GerenciarComissaoPerfisModal({ open, onClose }: GerenciarComissaoPerfisModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<ComissaoPerfilRequest>()
  const { data, isLoading } = useComissaoPerfis()
  const criar = useCriarComissaoPerfil()
  const atualizar = useAtualizarComissaoPerfil()
  const lookup = useLookupModal<ComissaoPerfilResponse>()
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
        message.success('Perfil de comissão atualizado.')
      } else {
        await criar.mutateAsync(valores)
        message.success('Perfil de comissão criado.')
      }
      lookup.fechar()
      form.resetFields()
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar perfil de comissão.')
    }
  }

  return (
    <Modal title="Gerenciar perfis de comissão" open={open} onCancel={onClose} footer={null} width={640} destroyOnHidden>
      <Table<ComissaoPerfilResponse>
        rowKey="id"
        size="small"
        loading={isLoading}
        dataSource={data?.conteudo}
        pagination={false}
        columns={[
          { title: 'Código', dataIndex: 'codigo', width: 100, render: (v?: string) => v || '—' },
          { title: 'Nome', dataIndex: 'nome' },
          { title: 'Situação', dataIndex: 'situacao' },
          {
            title: 'Ações',
            key: 'acoes',
            width: 60,
            render: (_, perfil) => <Button type="text" icon={<EditOutlined />} onClick={() => lookup.abrirEdicao(perfil)} />,
          },
        ]}
      />

      <Divider style={{ margin: '16px 0' }} />

      {/* name="comissaoPerfilForm": evita colisão de id com o form de Vendedor
          por trás — mesma lição registrada em GerenciarCargosModal. */}
      <Form form={form} name="comissaoPerfilForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12} align="bottom">
          <Col span={6}>
            <Form.Item name="codigo" label="Código" rules={[{ max: 3 }]} style={{ marginBottom: 12 }}>
              <Input maxLength={3} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]} style={{ marginBottom: 12 }}>
              <Input />
            </Form.Item>
          </Col>
          <Col span={6}>
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
          {lookup.itemEmEdicao ? 'Salvar alterações' : 'Adicionar perfil'}
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
