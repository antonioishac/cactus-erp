import { useEffect, useState } from 'react'
import { App, Button, Card, Col, Flex, Form, Input, Modal, Row, Select, Space, Table, Typography } from 'antd'
import { EditOutlined, PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useLookupModal } from '@shared/hooks/useLookupModal'
import SituacaoTag from '@shared/ui/SituacaoTag'
import GerenciarNotasFiscaisModelosModal from './modals/GerenciarNotasFiscaisModelosModal'
import { useAtualizarNotaFiscalTipo, useCriarNotaFiscalTipo, useNotasFiscaisModelos, useNotasFiscaisTipos } from './queries'
import type { NotaFiscalTipoRequest, NotaFiscalTipoResponse } from './types'

const { Title } = Typography

const VALORES_PADRAO: Partial<NotaFiscalTipoRequest> = { situacao: 'ATIVO' }

/**
 * Catálogo/numeração de nota fiscal — pré-requisito pra criar uma Venda
 * (`VendaRequest.notaFiscalTipoId` é obrigatório). Modelo (55/65/etc.) é
 * gerenciado num modal aninhado (`GerenciarNotasFiscaisModelosModal`), mesmo
 * padrão de Cargo dentro de Colaborador — muda raro, não justifica página própria.
 */
export default function NotasFiscaisTiposPage() {
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'VENDAS_GERENCIAR')

  const [form] = Form.useForm<NotaFiscalTipoRequest>()
  const { data, isLoading } = useNotasFiscaisTipos()
  const { data: modelos } = useNotasFiscaisModelos()
  const criar = useCriarNotaFiscalTipo()
  const atualizar = useAtualizarNotaFiscalTipo()
  const lookup = useLookupModal<NotaFiscalTipoResponse>()
  const [modalModelosAberto, setModalModelosAberto] = useState(false)
  const salvando = criar.isPending || atualizar.isPending

  useEffect(() => {
    if (lookup.open) {
      form.setFieldsValue(lookup.itemEmEdicao ?? VALORES_PADRAO)
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps -- só reage a abrir o modal/trocar o item em edição
  }, [lookup.open, lookup.itemEmEdicao])

  async function handleSalvar() {
    const valores = await form.validateFields()
    try {
      if (lookup.itemEmEdicao) {
        await atualizar.mutateAsync({ id: lookup.itemEmEdicao.id, request: valores })
        message.success('Tipo de nota fiscal atualizado.')
      } else {
        await criar.mutateAsync(valores)
        message.success('Tipo de nota fiscal criado.')
      }
      lookup.fechar()
      form.resetFields()
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar tipo de nota fiscal.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Tipos de Nota Fiscal
        </Title>
        {podeGerenciar && (
          <Button type="primary" icon={<PlusOutlined />} onClick={lookup.abrirNovo}>
            Novo Tipo
          </Button>
        )}
      </Flex>

      <Card>
        <Table<NotaFiscalTipoResponse>
          rowKey="id"
          loading={isLoading}
          dataSource={data?.conteudo}
          pagination={false}
          columns={[
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Modelo', dataIndex: 'notaFiscalModeloDescricao', render: (v?: string) => v || '—' },
            { title: 'Série', dataIndex: 'serie', render: (v?: string) => v || '—' },
            { title: 'Último número', dataIndex: 'ultimoNumero', align: 'right', render: (v?: number) => v ?? 0 },
            { title: 'Situação', dataIndex: 'situacao', render: (situacao) => <SituacaoTag situacao={situacao} /> },
            ...(podeGerenciar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    width: 60,
                    render: (_: unknown, tipo: NotaFiscalTipoResponse) => (
                      <Button type="text" icon={<EditOutlined />} onClick={() => lookup.abrirEdicao(tipo)} />
                    ),
                  },
                ]
              : []),
          ]}
        />
      </Card>

      <Modal
        title={lookup.itemEmEdicao ? 'Editar tipo de nota fiscal' : 'Novo tipo de nota fiscal'}
        open={lookup.open}
        onCancel={lookup.fechar}
        onOk={handleSalvar}
        confirmLoading={salvando}
        okText="Salvar"
        cancelText="Cancelar"
        destroyOnHidden
      >
        <Form form={form} name="notaFiscalTipoForm" layout="vertical" initialValues={VALORES_PADRAO}>
          <Row gutter={12}>
            <Col span={16}>
              <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 50 }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="situacao" label="Situação">
                <Select options={[{ value: 'ATIVO', label: 'Ativo' }, { value: 'INATIVO', label: 'Inativo' }]} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={24}>
              <Form.Item label="Modelo fiscal">
                <Space.Compact style={{ width: '100%' }}>
                  <Form.Item name="notaFiscalModeloId" rules={[{ required: true, message: 'Selecione o modelo' }]} noStyle>
                    <Select
                      style={{ width: '100%' }}
                      options={modelos?.conteudo.map((m) => ({ value: m.id, label: `${m.codigo} — ${m.descricao ?? ''}` }))}
                    />
                  </Form.Item>
                  <Button icon={<PlusOutlined />} onClick={() => setModalModelosAberto(true)} aria-label="Gerenciar modelos fiscais" />
                </Space.Compact>
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name="serie" label="Série" rules={[{ max: 3 }]}>
                <Input maxLength={3} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="serieScan" label="Série SCAN" rules={[{ max: 3 }]}>
                <Input maxLength={3} />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item name="descricao" label="Descrição" rules={[{ max: 250 }]}>
            <Input.TextArea rows={2} maxLength={250} showCount />
          </Form.Item>
        </Form>
      </Modal>

      <GerenciarNotasFiscaisModelosModal open={modalModelosAberto} onClose={() => setModalModelosAberto(false)} />
    </Flex>
  )
}
