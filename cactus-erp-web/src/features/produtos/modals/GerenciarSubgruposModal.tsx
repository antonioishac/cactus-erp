import { useEffect } from 'react'
import { App, Button, Col, Divider, Form, Input, Modal, Row, Select, Table } from 'antd'
import { EditOutlined, PlusOutlined } from '@ant-design/icons'
import { useLookupModal } from '@shared/hooks/useLookupModal'
import { useAtualizarProdutoSubgrupo, useCriarProdutoSubgrupo, useProdutoGrupos, useProdutoSubgrupos } from '../queries'
import type { ProdutoSubgrupoRequest, ProdutoSubgrupoResponse } from '../types'

interface GerenciarSubgruposModalProps {
  open: boolean
  onClose: () => void
}

const VALORES_PADRAO: Partial<ProdutoSubgrupoRequest> = { situacao: 'ATIVO' }

/**
 * Igual aos demais modais de lookup, com um campo a mais: `produtoGrupoId`
 * (obrigatório) — todo Subgrupo pertence a um Grupo, é a base da cascata
 * Grupo→Subgrupo usada no `DadosGeraisTab` de Produto.
 */
export default function GerenciarSubgruposModal({ open, onClose }: GerenciarSubgruposModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<ProdutoSubgrupoRequest>()
  const { data: grupos } = useProdutoGrupos()
  const { data: subgrupos, isLoading } = useProdutoSubgrupos()
  const criar = useCriarProdutoSubgrupo()
  const atualizar = useAtualizarProdutoSubgrupo()
  const lookup = useLookupModal<ProdutoSubgrupoResponse>()
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
        message.success('Subgrupo atualizado.')
      } else {
        await criar.mutateAsync(valores)
        message.success('Subgrupo criado.')
      }
      lookup.fechar()
      form.resetFields()
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar subgrupo.')
    }
  }

  return (
    <Modal title="Gerenciar subgrupos de produto" open={open} onCancel={onClose} footer={null} width={760} destroyOnHidden>
      <Table<ProdutoSubgrupoResponse>
        rowKey="id"
        size="small"
        loading={isLoading}
        dataSource={subgrupos}
        pagination={false}
        columns={[
          { title: 'Grupo', dataIndex: 'produtoGrupoNome' },
          { title: 'Nome', dataIndex: 'nome' },
          { title: 'Descrição', dataIndex: 'descricao', render: (v) => v || '—' },
          {
            title: 'Ações',
            key: 'acoes',
            width: 60,
            render: (_, subgrupo) => <Button type="text" icon={<EditOutlined />} onClick={() => lookup.abrirEdicao(subgrupo)} />,
          },
        ]}
      />

      <Divider style={{ margin: '16px 0' }} />

      <Form form={form} name="produtoSubgrupoForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12} align="bottom">
          <Col span={7}>
            <Form.Item name="produtoGrupoId" label="Grupo" rules={[{ required: true, message: 'Informe o grupo' }]} style={{ marginBottom: 12 }}>
              <Select showSearch optionFilterProp="label" options={grupos?.map((g) => ({ value: g.id, label: g.nome }))} />
            </Form.Item>
          </Col>
          <Col span={7}>
            <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]} style={{ marginBottom: 12 }}>
              <Input />
            </Form.Item>
          </Col>
          <Col span={6}>
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
        <Button type="primary" icon={lookup.itemEmEdicao ? <EditOutlined /> : <PlusOutlined />} loading={salvando} onClick={handleSalvar}>
          {lookup.itemEmEdicao ? 'Salvar alterações' : 'Adicionar subgrupo'}
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
