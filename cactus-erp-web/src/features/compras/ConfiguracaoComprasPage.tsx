import { useEffect } from 'react'
import { App, Alert, Button, Card, Flex, Form, InputNumber, Skeleton, Tooltip, Typography } from 'antd'
import { SaveOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useAtualizarConfiguracaoCompras, useConfiguracaoCompras } from './queries'
import type { ConfiguracaoComprasEmpresaRequest } from './types'

const { Title } = Typography

/**
 * Sem lista/rota `:id` — 1 config por empresa (a empresa ativa da sessão), GET
 * sempre devolve 200 com default (ver `ConfiguracaoComprasController`), mesmo
 * padrão de `ConfiguracaoEstoquePage`. `limiteAprovacaoPedido` vazio/nulo =
 * pedido nunca exige aprovação (vai direto pra ENVIADO ao ser enviado).
 */
export default function ConfiguracaoComprasPage() {
  const { message } = App.useApp()
  const [form] = Form.useForm<ConfiguracaoComprasEmpresaRequest>()
  const { empresaAtivaId, authorities } = useAuth()
  const podeSalvar = hasAuthority(authorities, 'COMPRAS_GERENCIAR')

  const { data: configuracao, isLoading: carregando } = useConfiguracaoCompras(empresaAtivaId ?? undefined)
  const atualizar = useAtualizarConfiguracaoCompras(empresaAtivaId ?? -1)

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (configuracao) {
      form.setFieldsValue(configuracao)
    }
  }, [configuracao, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    try {
      await atualizar.mutateAsync(valores)
      message.success('Configuração de compras atualizada com sucesso.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar configuração.')
    }
  }

  if (carregando) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 4 }} />
      </Card>
    )
  }

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Configuração de Compras
      </Title>

      <Card>
        <Alert
          type="info"
          showIcon
          style={{ marginBottom: 16 }}
          title="Vale pra empresa ativa da sessão"
          description="Limite de aprovação de pedido de compra — troque de empresa no cabeçalho pra configurar outra filial."
        />

        <Form form={form} layout="vertical" disabled={!podeSalvar}>
          <Form.Item
            name="limiteAprovacaoPedido"
            label="Limite de aprovação de pedido (R$)"
            tooltip="Pedido cujo valor total exceda este limite exige aprovação nominal (authority COMPRAS_APROVAR) antes de ser enviado ao fornecedor. Deixe em branco pra nunca exigir aprovação."
          >
            <InputNumber<number> style={{ width: 320 }} min={0} precision={2} decimalSeparator="," />
          </Form.Item>

          <Tooltip title={podeSalvar ? undefined : 'Você não tem permissão para gerenciar a configuração de compras'}>
            <Button type="primary" icon={<SaveOutlined />} loading={atualizar.isPending} disabled={!podeSalvar} onClick={handleSalvar}>
              Salvar
            </Button>
          </Tooltip>
        </Form>
      </Card>
    </Flex>
  )
}
