import { useEffect } from 'react'
import { App, Alert, Button, Card, Col, Flex, Form, InputNumber, Row, Select, Skeleton, Tooltip, Typography } from 'antd'
import { SaveOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useAtualizarConfiguracaoEstoque, useConfiguracaoEstoque } from './queries'
import { METODO_CUSTEIO_LABEL } from './types'
import type { ConfiguracaoEstoqueEmpresaRequest } from './types'

const { Title } = Typography

/**
 * Sem lista/rota `:id` — é 1 config por empresa (a empresa ativa da sessão),
 * GET sempre devolve 200 com default (`buscarPorEmpresa`, ver
 * `ConfiguracaoEstoqueController`), diferente do padrão 404→null usado nas
 * sub-abas de Empresa (`ConfiguracaoOperacionalTab`).
 */
export default function ConfiguracaoEstoquePage() {
  const { message } = App.useApp()
  const [form] = Form.useForm<ConfiguracaoEstoqueEmpresaRequest>()
  const { empresaAtivaId, authorities } = useAuth()
  const podeSalvar = hasAuthority(authorities, 'ESTOQUE_GERENCIAR')

  const { data: configuracao, isLoading: carregando } = useConfiguracaoEstoque(empresaAtivaId ?? undefined)
  const atualizar = useAtualizarConfiguracaoEstoque(empresaAtivaId ?? -1)

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
      message.success('Configuração de estoque atualizada com sucesso.')
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
        Configuração de Estoque
      </Title>

      <Card>
        <Alert
          type="info"
          showIcon
          style={{ marginBottom: 16 }}
          title="Vale pra empresa ativa da sessão"
          description="Método de custeio e limite de aprovação de ajuste/perda — troque de empresa no cabeçalho pra configurar outra filial."
        />

        <Form form={form} layout="vertical" disabled={!podeSalvar}>
          <Row gutter={12}>
            <Col span={12}>
              <Form.Item
                name="metodoCusteio"
                label="Método de custeio"
                rules={[{ required: true }]}
                tooltip="Média ponderada é obrigatória na maioria dos regimes fiscais no Brasil; FIFO é alternativa configurável."
              >
                <Select options={Object.entries(METODO_CUSTEIO_LABEL).map(([value, label]) => ({ value, label }))} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="limiteAprovacaoAjuste"
                label="Limite de aprovação de ajuste/perda (R$)"
                rules={[{ required: true, message: 'Informe o limite' }]}
                tooltip="Ajuste/perda cujo valor exceda este limite exige um aprovador nominal. Zero exige aprovador sempre."
              >
                <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
          </Row>

          <Tooltip title={podeSalvar ? undefined : 'Você não tem permissão para gerenciar a configuração de estoque'}>
            <Button type="primary" icon={<SaveOutlined />} loading={atualizar.isPending} disabled={!podeSalvar} onClick={handleSalvar}>
              Salvar
            </Button>
          </Tooltip>
        </Form>
      </Card>
    </Flex>
  )
}
