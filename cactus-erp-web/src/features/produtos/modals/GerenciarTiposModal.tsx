import { useEffect } from 'react'
import { App, Button, Col, Divider, Form, Input, Modal, Row, Select, Table } from 'antd'
import { EditOutlined, PlusOutlined } from '@ant-design/icons'
import { useLookupModal } from '@shared/hooks/useLookupModal'
import { useAtualizarProdutoTipo, useCriarProdutoTipo, useProdutoTipos } from '../queries'
import type { ProdutoTipoRequest, ProdutoTipoResponse } from '../types'

interface GerenciarTiposModalProps {
  open: boolean
  onClose: () => void
}

const VALORES_PADRAO: Partial<ProdutoTipoRequest> = { situacao: 'ATIVO' }

/**
 * `ProdutoTipo` é distinta de `TipoItemSped` (ver CLAUDE.md raiz) — é o
 * "tipo de item" livre do PDV (`codigo` char(2) + descrição), não a tabela
 * oficial do Registro 0200 do SPED (que é enum fixo, editado direto na
 * aba Tributação, sem CRUD).
 */
export default function GerenciarTiposModal({ open, onClose }: GerenciarTiposModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<ProdutoTipoRequest>()
  const { data: tipos, isLoading } = useProdutoTipos()
  const criar = useCriarProdutoTipo()
  const atualizar = useAtualizarProdutoTipo()
  const lookup = useLookupModal<ProdutoTipoResponse>()
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
        message.success('Tipo atualizado.')
      } else {
        await criar.mutateAsync(valores)
        message.success('Tipo criado.')
      }
      lookup.fechar()
      form.resetFields()
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar tipo.')
    }
  }

  return (
    <Modal title="Gerenciar tipos de produto" open={open} onCancel={onClose} footer={null} width={680} destroyOnHidden>
      <Table<ProdutoTipoResponse>
        rowKey="id"
        size="small"
        loading={isLoading}
        dataSource={tipos}
        pagination={false}
        columns={[
          { title: 'Código', dataIndex: 'codigo', width: 90 },
          { title: 'Descrição', dataIndex: 'descricao' },
          {
            title: 'Ações',
            key: 'acoes',
            width: 60,
            render: (_, tipo) => <Button type="text" icon={<EditOutlined />} onClick={() => lookup.abrirEdicao(tipo)} />,
          },
        ]}
      />

      <Divider style={{ margin: '16px 0' }} />

      <Form form={form} name="produtoTipoForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12} align="bottom">
          <Col span={4}>
            <Form.Item name="codigo" label="Código" rules={[{ required: true, message: 'Informe o código' }, { max: 2 }]} style={{ marginBottom: 12 }}>
              <Input maxLength={2} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="descricao" label="Descrição" rules={[{ required: true, message: 'Informe a descrição' }, { max: 100 }]} style={{ marginBottom: 12 }}>
              <Input />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="situacao" label="Situação" style={{ marginBottom: 12 }}>
              <Select options={[{ value: 'ATIVO', label: 'Ativo' }, { value: 'INATIVO', label: 'Inativo' }]} />
            </Form.Item>
          </Col>
        </Row>
        <Button type="primary" icon={lookup.itemEmEdicao ? <EditOutlined /> : <PlusOutlined />} loading={salvando} onClick={handleSalvar}>
          {lookup.itemEmEdicao ? 'Salvar alterações' : 'Adicionar tipo'}
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
