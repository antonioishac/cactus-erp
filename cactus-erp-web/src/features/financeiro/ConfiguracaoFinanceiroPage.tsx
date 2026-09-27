import { useEffect } from 'react'
import { App, Alert, Button, Card, Col, Flex, Form, InputNumber, Row, Skeleton, Tooltip, Typography } from 'antd'
import { SaveOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useAtualizarConfiguracaoFinanceiro, useConfiguracaoFinanceiro } from './queries'
import type { ConfiguracaoFinanceiroEmpresaRequest } from './types'

const { Title } = Typography

/** Sem lista/rota `:id` — é 1 config por empresa (a empresa ativa da sessão), `GET` sempre
 * devolve 200 com default (percentuais nulos = sem cobrança), mesmo padrão de
 * `ConfiguracaoEstoquePage`. */
export default function ConfiguracaoFinanceiroPage() {
  const { message } = App.useApp()
  const [form] = Form.useForm<ConfiguracaoFinanceiroEmpresaRequest>()
  const { empresaAtivaId, authorities } = useAuth()
  const podeSalvar = hasAuthority(authorities, 'FINANCEIRO_GERENCIAR')

  const { data: configuracao, isLoading: carregando } = useConfiguracaoFinanceiro(empresaAtivaId ?? undefined)
  const atualizar = useAtualizarConfiguracaoFinanceiro(empresaAtivaId ?? -1)

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
      message.success('Configuração financeira atualizada com sucesso.')
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
        Configuração Financeira
      </Title>

      <Card>
        <Alert
          type="info"
          showIcon
          style={{ marginBottom: 16 }}
          title="Vale pra empresa ativa da sessão"
          description="Percentual de multa/juros por atraso, aplicado a todo título vencido sem override próprio — deixe em branco pra não cobrar. Troque de empresa no cabeçalho pra configurar outra filial."
        />

        <Form form={form} layout="vertical" disabled={!podeSalvar}>
          <Row gutter={12}>
            <Col span={12}>
              <Form.Item name="percentualMultaAtraso" label="% multa por atraso" tooltip="Cobrada uma vez, assim que o título vence">
                <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="percentualJurosDiaAtraso"
                label="% juros por dia de atraso"
                tooltip="Multiplicado pelos dias corridos de atraso"
              >
                <InputNumber<number> style={{ width: '100%' }} min={0} precision={4} decimalSeparator="," />
              </Form.Item>
            </Col>
          </Row>

          <Tooltip title={podeSalvar ? undefined : 'Você não tem permissão para gerenciar a configuração financeira'}>
            <Button type="primary" icon={<SaveOutlined />} loading={atualizar.isPending} disabled={!podeSalvar} onClick={handleSalvar}>
              Salvar
            </Button>
          </Tooltip>
        </Form>
      </Card>
    </Flex>
  )
}
