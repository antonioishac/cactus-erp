import { useEffect } from 'react'
import { App, Button, Col, Divider, Form, Input, Modal, Row, Select, Table } from 'antd'
import { EditOutlined, PlusOutlined } from '@ant-design/icons'
import { useLookupModal } from '@shared/hooks/useLookupModal'
import { useAtualizarContador, useContadores, useCriarContador } from '../queries'
import type { ContadorRequest, ContadorResponse } from '../types'

interface GerenciarContadoresModalProps {
  open: boolean
  onClose: () => void
}

const VALORES_PADRAO: Partial<ContadorRequest> = { situacao: 'ATIVO' }

/**
 * Mesmo padrão de `GerenciarCargosModal.tsx`/`GerenciarSetoresModal.tsx`
 * (Colaborador) — aberto a partir do `Select` de Contador no `DadosLegaisTab`
 * de Empresa, sem sair do formulário que já estava sendo preenchido.
 */
export default function GerenciarContadoresModal({ open, onClose }: GerenciarContadoresModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<ContadorRequest>()
  const { data: contadores, isLoading } = useContadores()
  const criar = useCriarContador()
  const atualizar = useAtualizarContador()
  const lookup = useLookupModal<ContadorResponse>()
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
        message.success('Contador atualizado.')
      } else {
        await criar.mutateAsync(valores)
        message.success('Contador criado.')
      }
      lookup.fechar()
      form.resetFields()
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar contador.')
    }
  }

  return (
    <Modal title="Gerenciar contadores" open={open} onCancel={onClose} footer={null} width={720} destroyOnHidden>
      <Table<ContadorResponse>
        rowKey="id"
        size="small"
        loading={isLoading}
        dataSource={contadores}
        pagination={false}
        columns={[
          { title: 'Nome', dataIndex: 'nome' },
          { title: 'CRC', dataIndex: 'crc' },
          { title: 'E-mail', dataIndex: 'email', render: (v) => v || '—' },
          {
            title: 'Ações',
            key: 'acoes',
            width: 60,
            render: (_, contador) => <Button type="text" icon={<EditOutlined />} onClick={() => lookup.abrirEdicao(contador)} />,
          },
        ]}
      />

      <Divider style={{ margin: '16px 0' }} />

      {/* name="contadorForm": mesmo achado/motivo do GerenciarCargosModal.tsx
          (colaboradores) — evita colisão de id="nome" com o form de Empresa
          por trás. */}
      <Form form={form} name="contadorForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12} align="bottom">
          <Col span={7}>
            <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 150 }]} style={{ marginBottom: 12 }}>
              <Input />
            </Form.Item>
          </Col>
          <Col span={4}>
            <Form.Item name="crc" label="CRC" rules={[{ required: true, message: 'Informe o CRC' }, { max: 20 }]} style={{ marginBottom: 12 }}>
              <Input />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="email" label="E-mail" rules={[{ type: 'email', message: 'E-mail inválido' }]} style={{ marginBottom: 12 }}>
              <Input />
            </Form.Item>
          </Col>
          <Col span={4}>
            <Form.Item name="telefone" label="Telefone" style={{ marginBottom: 12 }}>
              <Input maxLength={15} />
            </Form.Item>
          </Col>
          <Col span={3}>
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
          {lookup.itemEmEdicao ? 'Salvar alterações' : 'Adicionar contador'}
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
