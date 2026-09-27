import { useEffect } from 'react'
import { App, Button, Col, Divider, Form, Input, Modal, Row, Table } from 'antd'
import { EditOutlined, PlusOutlined } from '@ant-design/icons'
import { useLookupModal } from '@shared/hooks/useLookupModal'
import { useAtualizarNotaFiscalModelo, useCriarNotaFiscalModelo, useNotasFiscaisModelos } from '../queries'
import type { NotaFiscalModeloRequest, NotaFiscalModeloResponse } from '../types'

interface GerenciarNotasFiscaisModelosModalProps {
  open: boolean
  onClose: () => void
}

/**
 * Catálogo de modelo fiscal (55=NF-e, 65=NFC-e, etc.) — muda extremamente
 * raro, mesmo critério de modal-aninhado já usado em Cargo/Setor/Contador/
 * Perfil de Comissão: aberto pelo "+" ao lado do Select de Modelo dentro do
 * modal de Tipo de Nota Fiscal.
 */
export default function GerenciarNotasFiscaisModelosModal({ open, onClose }: GerenciarNotasFiscaisModelosModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<NotaFiscalModeloRequest>()
  const { data, isLoading } = useNotasFiscaisModelos()
  const criar = useCriarNotaFiscalModelo()
  const atualizar = useAtualizarNotaFiscalModelo()
  const lookup = useLookupModal<NotaFiscalModeloResponse>()
  const salvando = criar.isPending || atualizar.isPending

  useEffect(() => {
    if (open) {
      form.setFieldsValue(lookup.itemEmEdicao ?? {})
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps -- só reage a abrir o modal/trocar o item em edição
  }, [open, lookup.itemEmEdicao])

  async function handleSalvar() {
    const valores = await form.validateFields()
    try {
      if (lookup.itemEmEdicao) {
        await atualizar.mutateAsync({ id: lookup.itemEmEdicao.id, request: valores })
        message.success('Modelo fiscal atualizado.')
      } else {
        await criar.mutateAsync(valores)
        message.success('Modelo fiscal criado.')
      }
      lookup.fechar()
      form.resetFields()
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar modelo fiscal.')
    }
  }

  return (
    <Modal title="Gerenciar modelos fiscais" open={open} onCancel={onClose} footer={null} width={640} destroyOnHidden>
      <Table<NotaFiscalModeloResponse>
        rowKey="id"
        size="small"
        loading={isLoading}
        dataSource={data?.conteudo}
        pagination={false}
        columns={[
          { title: 'Código', dataIndex: 'codigo', width: 100 },
          { title: 'Descrição', dataIndex: 'descricao', render: (v?: string) => v || '—' },
          { title: 'Modelo SEFAZ', dataIndex: 'modelo', render: (v?: string) => v || '—' },
          {
            title: 'Ações',
            key: 'acoes',
            width: 60,
            render: (_, modelo) => <Button type="text" icon={<EditOutlined />} onClick={() => lookup.abrirEdicao(modelo)} />,
          },
        ]}
      />

      <Divider style={{ margin: '16px 0' }} />

      <Form form={form} name="notaFiscalModeloForm" layout="vertical">
        <Row gutter={12} align="bottom">
          <Col span={6}>
            <Form.Item name="codigo" label="Código" rules={[{ required: true, message: 'Informe o código' }, { max: 2 }]} style={{ marginBottom: 12 }}>
              <Input maxLength={2} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="descricao" label="Descrição" rules={[{ max: 100 }]} style={{ marginBottom: 12 }}>
              <Input placeholder="Ex: Nota Fiscal Eletrônica" />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="modelo" label="Modelo SEFAZ" rules={[{ max: 10 }]} style={{ marginBottom: 12 }}>
              <Input placeholder="Ex: 55" maxLength={10} />
            </Form.Item>
          </Col>
        </Row>
        <Button
          type="primary"
          icon={lookup.itemEmEdicao ? <EditOutlined /> : <PlusOutlined />}
          loading={salvando}
          onClick={handleSalvar}
        >
          {lookup.itemEmEdicao ? 'Salvar alterações' : 'Adicionar modelo'}
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
