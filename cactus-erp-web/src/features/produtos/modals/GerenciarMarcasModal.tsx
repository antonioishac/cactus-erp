import { useEffect } from 'react'
import { App, Button, Col, Divider, Form, Input, Modal, Row, Select, Table } from 'antd'
import { EditOutlined, PlusOutlined } from '@ant-design/icons'
import { useLookupModal } from '@shared/hooks/useLookupModal'
import { useAtualizarProdutoMarca, useCriarProdutoMarca, useProdutoMarcas } from '../queries'
import type { ProdutoMarcaRequest, ProdutoMarcaResponse } from '../types'

interface GerenciarMarcasModalProps {
  open: boolean
  onClose: () => void
}

const VALORES_PADRAO: Partial<ProdutoMarcaRequest> = { situacao: 'ATIVO' }

export default function GerenciarMarcasModal({ open, onClose }: GerenciarMarcasModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<ProdutoMarcaRequest>()
  const { data: marcas, isLoading } = useProdutoMarcas()
  const criar = useCriarProdutoMarca()
  const atualizar = useAtualizarProdutoMarca()
  const lookup = useLookupModal<ProdutoMarcaResponse>()
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
        message.success('Marca atualizada.')
      } else {
        await criar.mutateAsync(valores)
        message.success('Marca criada.')
      }
      lookup.fechar()
      form.resetFields()
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar marca.')
    }
  }

  return (
    <Modal title="Gerenciar marcas" open={open} onCancel={onClose} footer={null} width={560} destroyOnHidden>
      <Table<ProdutoMarcaResponse>
        rowKey="id"
        size="small"
        loading={isLoading}
        dataSource={marcas}
        pagination={false}
        columns={[
          { title: 'Nome', dataIndex: 'nome' },
          {
            title: 'Ações',
            key: 'acoes',
            width: 60,
            render: (_, marca) => <Button type="text" icon={<EditOutlined />} onClick={() => lookup.abrirEdicao(marca)} />,
          },
        ]}
      />

      <Divider style={{ margin: '16px 0' }} />

      <Form form={form} name="produtoMarcaForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12} align="bottom">
          <Col span={14}>
            <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]} style={{ marginBottom: 12 }}>
              <Input />
            </Form.Item>
          </Col>
          <Col span={10}>
            <Form.Item name="situacao" label="Situação" style={{ marginBottom: 12 }}>
              <Select options={[{ value: 'ATIVO', label: 'Ativo' }, { value: 'INATIVO', label: 'Inativo' }]} />
            </Form.Item>
          </Col>
        </Row>
        <Button type="primary" icon={lookup.itemEmEdicao ? <EditOutlined /> : <PlusOutlined />} loading={salvando} onClick={handleSalvar}>
          {lookup.itemEmEdicao ? 'Salvar alterações' : 'Adicionar marca'}
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
