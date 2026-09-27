import { useEffect } from 'react'
import { App, Button, Checkbox, Col, Divider, Form, Input, Modal, Row, Select, Table } from 'antd'
import { EditOutlined, PlusOutlined } from '@ant-design/icons'
import { useLookupModal } from '@shared/hooks/useLookupModal'
import { useAtualizarProdutoUnidade, useCriarProdutoUnidade, useProdutoUnidades } from '../queries'
import type { ProdutoUnidadeRequest, ProdutoUnidadeResponse } from '../types'

interface GerenciarUnidadesModalProps {
  open: boolean
  onClose: () => void
}

const VALORES_PADRAO: Partial<ProdutoUnidadeRequest> = { situacao: 'ATIVO', podeFracionar: false }

export default function GerenciarUnidadesModal({ open, onClose }: GerenciarUnidadesModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<ProdutoUnidadeRequest>()
  const { data: unidades, isLoading } = useProdutoUnidades()
  const criar = useCriarProdutoUnidade()
  const atualizar = useAtualizarProdutoUnidade()
  const lookup = useLookupModal<ProdutoUnidadeResponse>()
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
        message.success('Unidade atualizada.')
      } else {
        await criar.mutateAsync(valores)
        message.success('Unidade criada.')
      }
      lookup.fechar()
      form.resetFields()
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar unidade.')
    }
  }

  return (
    <Modal title="Gerenciar unidades de medida" open={open} onCancel={onClose} footer={null} width={680} destroyOnHidden>
      <Table<ProdutoUnidadeResponse>
        rowKey="id"
        size="small"
        loading={isLoading}
        dataSource={unidades}
        pagination={false}
        columns={[
          { title: 'Sigla', dataIndex: 'sigla', width: 90 },
          { title: 'Descrição', dataIndex: 'descricao' },
          { title: 'Fracionável', dataIndex: 'podeFracionar', width: 100, render: (v) => (v ? 'Sim' : 'Não') },
          {
            title: 'Ações',
            key: 'acoes',
            width: 60,
            render: (_, unidade) => <Button type="text" icon={<EditOutlined />} onClick={() => lookup.abrirEdicao(unidade)} />,
          },
        ]}
      />

      <Divider style={{ margin: '16px 0' }} />

      <Form form={form} name="produtoUnidadeForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12} align="bottom">
          <Col span={4}>
            <Form.Item name="sigla" label="Sigla" rules={[{ required: true, message: 'Informe a sigla' }, { max: 10 }]} style={{ marginBottom: 12 }}>
              <Input maxLength={10} placeholder="UN, KG, CX..." />
            </Form.Item>
          </Col>
          <Col span={10}>
            <Form.Item name="descricao" label="Descrição" rules={[{ required: true, message: 'Informe a descrição' }, { max: 100 }]} style={{ marginBottom: 12 }}>
              <Input />
            </Form.Item>
          </Col>
          <Col span={5}>
            <Form.Item name="podeFracionar" valuePropName="checked" style={{ marginBottom: 12 }}>
              <Checkbox>Pode fracionar</Checkbox>
            </Form.Item>
          </Col>
          <Col span={5}>
            <Form.Item name="situacao" label="Situação" style={{ marginBottom: 12 }}>
              <Select options={[{ value: 'ATIVO', label: 'Ativo' }, { value: 'INATIVO', label: 'Inativo' }]} />
            </Form.Item>
          </Col>
        </Row>
        <Button type="primary" icon={lookup.itemEmEdicao ? <EditOutlined /> : <PlusOutlined />} loading={salvando} onClick={handleSalvar}>
          {lookup.itemEmEdicao ? 'Salvar alterações' : 'Adicionar unidade'}
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
