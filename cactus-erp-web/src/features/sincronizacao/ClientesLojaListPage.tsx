import { useState } from 'react'
import { App, Alert, Button, Card, Flex, Form, Input, InputNumber, Modal, Table, Typography } from 'antd'
import { CopyOutlined, PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useClientesLoja, useCriarClienteLoja } from './queries'
import type { ClienteLojaRequest, ClienteLojaResponse, ClienteLojaResumoResponse } from './types'

const { Title, Text, Paragraph } = Typography

/** Provisiona o client OAuth2 Client Credentials "um por loja" que o bridge/PDV usa pra
 * chamar `/sync/produtos`/`/sync/lotes` etc. — vive no seguranca-service, não no
 * sincronizacao-service (ver Javadoc de `ClienteLojaController`). `clientSecret` só aparece
 * na resposta de criação — nunca mais recuperável depois, por isso o aviso explícito. */
export default function ClientesLojaListPage() {
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'SEGURANCA_GERENCIAR')
  const [form] = Form.useForm<ClienteLojaRequest>()
  const [modalAberto, setModalAberto] = useState(false)
  const [clienteCriado, setClienteCriado] = useState<ClienteLojaResponse | undefined>(undefined)

  const { data, isLoading } = useClientesLoja()
  const criar = useCriarClienteLoja()

  async function handleCriar() {
    const valores = await form.validateFields()
    try {
      const criado = await criar.mutateAsync(valores)
      setClienteCriado(criado)
      form.resetFields()
      setModalAberto(false)
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao provisionar client de loja.')
    }
  }

  async function copiarSecret() {
    if (!clienteCriado) return
    await navigator.clipboard.writeText(clienteCriado.clientSecret)
    message.success('Secret copiado.')
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Clientes de Loja (OAuth2)
        </Title>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          disabled={!podeGerenciar}
          onClick={() => setModalAberto(true)}
        >
          Provisionar Client
        </Button>
      </Flex>

      {clienteCriado && (
        <Alert
          type="warning"
          showIcon
          closable
          onClose={() => setClienteCriado(undefined)}
          title="Client provisionado — copie o secret agora"
          description={
            <Flex vertical gap={4}>
              <Paragraph style={{ margin: 0 }}>
                Este secret <Text strong>não será mostrado de novo</Text>. Se perder, só dá pra provisionar um client
                novo (não existe "ver secret depois").
              </Paragraph>
              <Flex gap={8} align="center">
                <Text code>{clienteCriado.clientId}</Text>
                <Text code>{clienteCriado.clientSecret}</Text>
                <Button size="small" icon={<CopyOutlined />} onClick={copiarSecret}>
                  Copiar secret
                </Button>
              </Flex>
            </Flex>
          }
        />
      )}

      <Card>
        <Table<ClienteLojaResumoResponse>
          rowKey="clientId"
          loading={isLoading}
          dataSource={data ?? []}
          pagination={false}
          columns={[
            { title: 'Client ID', dataIndex: 'clientId' },
            { title: 'Empresa', dataIndex: 'empresaId', render: (v: number) => `#${v}` },
            { title: 'Descrição', dataIndex: 'descricao' },
          ]}
        />
      </Card>

      <Modal
        title="Provisionar client de loja"
        open={modalAberto}
        onCancel={() => setModalAberto(false)}
        onOk={handleCriar}
        okText="Provisionar"
        cancelText="Cancelar"
        confirmLoading={criar.isPending}
        destroyOnHidden
      >
        <Form form={form} name="clienteLojaForm" layout="vertical">
          <Form.Item name="empresaId" label="Empresa (id)" rules={[{ required: true, message: 'Informe a empresa' }]}>
            <InputNumber style={{ width: '100%' }} min={1} />
          </Form.Item>
          <Form.Item name="descricao" label="Descrição" rules={[{ required: true, message: 'Informe a descrição' }]}>
            <Input placeholder="ex: PDV loja Centro" />
          </Form.Item>
        </Form>
      </Modal>
    </Flex>
  )
}
