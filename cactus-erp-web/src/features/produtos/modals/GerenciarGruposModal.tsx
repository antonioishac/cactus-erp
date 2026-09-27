import { useEffect } from 'react'
import { App, Button, Col, Divider, Form, Input, Modal, Row, Select, Table } from 'antd'
import { EditOutlined, PlusOutlined } from '@ant-design/icons'
import { useLookupModal } from '@shared/hooks/useLookupModal'
import { useAtualizarProdutoGrupo, useCriarProdutoGrupo, useProdutoGrupos } from '../queries'
import type { ProdutoGrupoRequest, ProdutoGrupoResponse } from '../types'

interface GerenciarGruposModalProps {
  open: boolean
  onClose: () => void
}

const VALORES_PADRAO: Partial<ProdutoGrupoRequest> = { situacao: 'ATIVO' }

/** Mesmo padrão de `GerenciarCargosModal.tsx` (Colaborador) — aberto a partir do "+" ao lado do Select de Grupo no `DadosGeraisTab` de Produto. */
export default function GerenciarGruposModal({ open, onClose }: GerenciarGruposModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<ProdutoGrupoRequest>()
  const { data: grupos, isLoading } = useProdutoGrupos()
  const criar = useCriarProdutoGrupo()
  const atualizar = useAtualizarProdutoGrupo()
  const lookup = useLookupModal<ProdutoGrupoResponse>()
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
        message.success('Grupo atualizado.')
      } else {
        await criar.mutateAsync(valores)
        message.success('Grupo criado.')
      }
      lookup.fechar()
      form.resetFields()
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar grupo.')
    }
  }

  return (
    <Modal title="Gerenciar grupos de produto" open={open} onCancel={onClose} footer={null} width={680} destroyOnHidden>
      <Table<ProdutoGrupoResponse>
        rowKey="id"
        size="small"
        loading={isLoading}
        dataSource={grupos}
        pagination={false}
        columns={[
          { title: 'Nome', dataIndex: 'nome' },
          { title: 'Descrição', dataIndex: 'descricao', render: (v) => v || '—' },
          {
            title: 'Ações',
            key: 'acoes',
            width: 60,
            render: (_, grupo) => <Button type="text" icon={<EditOutlined />} onClick={() => lookup.abrirEdicao(grupo)} />,
          },
        ]}
      />

      <Divider style={{ margin: '16px 0' }} />

      {/* name="produtoGrupoForm": evita colisão de id="nome" com o form de Produto por trás (achado já registrado nos demais modais de lookup). */}
      <Form form={form} name="produtoGrupoForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12} align="bottom">
          <Col span={9}>
            <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]} style={{ marginBottom: 12 }}>
              <Input />
            </Form.Item>
          </Col>
          <Col span={9}>
            <Form.Item name="descricao" label="Descrição" rules={[{ max: 250 }]} style={{ marginBottom: 12 }}>
              <Input />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="situacao" label="Situação" style={{ marginBottom: 12 }}>
              <Select options={[{ value: 'ATIVO', label: 'Ativo' }, { value: 'INATIVO', label: 'Inativo' }]} />
            </Form.Item>
          </Col>
        </Row>
        <Button type="primary" icon={lookup.itemEmEdicao ? <EditOutlined /> : <PlusOutlined />} loading={salvando} onClick={handleSalvar}>
          {lookup.itemEmEdicao ? 'Salvar alterações' : 'Adicionar grupo'}
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
