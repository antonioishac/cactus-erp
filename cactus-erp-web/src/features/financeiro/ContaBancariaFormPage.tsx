import { useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Col, Flex, Form, Input, InputNumber, Row, Select, Skeleton, Space, Tooltip, Typography } from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useAtualizarContaBancaria, useBancos, useContaBancaria, useCriarContaBancaria } from './queries'
import { TIPO_CONTA_BANCARIA_LABEL } from './types'
import type { ContaBancariaRequest } from './types'

const { Title } = Typography

const VALORES_PADRAO: Partial<ContaBancariaRequest> = { tipo: 'CORRENTE', saldoInicial: 0 }

/** `bancoId` obrigatório quando `tipo !== 'CAIXA_INTERNO'` (regra condicional validada no
 * backend) — `Form.useWatch` esconde/exige o campo conforme o tipo escolhido. `saldoAtual`
 * nunca é editável aqui (só via `MovimentoCaixaBanco`) — `PUT` não mexe em saldo. */
export default function ContaBancariaFormPage() {
  const { id } = useParams<{ id: string }>()
  const contaId = id ? Number(id) : undefined
  const modoEdicao = contaId !== undefined
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<ContaBancariaRequest>()
  const { empresaAtivaId, authorities } = useAuth()
  const podeSalvar = hasAuthority(authorities, 'CAIXA_GERENCIAR')
  const tipoSelecionado = Form.useWatch('tipo', form)

  const { data: conta, isLoading: carregando } = useContaBancaria(contaId)
  const { data: bancos } = useBancos()
  const criar = useCriarContaBancaria()
  const atualizar = useAtualizarContaBancaria(contaId ?? -1)
  const salvando = criar.isPending || atualizar.isPending

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (conta) {
      form.setFieldsValue(conta)
    }
  }, [conta, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    const request = { ...valores, empresaId: empresaAtivaId ?? -1, dataSaldoInicial: dayjs(valores.dataSaldoInicial).format('YYYY-MM-DD') }
    try {
      if (modoEdicao) {
        await atualizar.mutateAsync(request)
        message.success('Conta bancária atualizada com sucesso.')
      } else {
        await criar.mutateAsync(request)
        message.success('Conta bancária criada com sucesso.')
      }
      navigate('/financeiro/contas-bancarias')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar conta bancária.')
    }
  }

  if (modoEdicao && carregando) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 6 }} />
      </Card>
    )
  }

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate('/financeiro/contas-bancarias')}>Contas Bancárias</a> },
          { title: modoEdicao ? conta?.nome : 'Nova conta' },
        ]}
      />

      <Card>
        <Form form={form} name="contaBancariaForm" layout="vertical" initialValues={VALORES_PADRAO} disabled={!podeSalvar}>
          <Flex justify="space-between" align="center" wrap="wrap" gap={16} style={{ marginBottom: 24 }}>
            <Title level={4} style={{ margin: 0 }}>
              {modoEdicao ? 'Editar Conta Bancária' : 'Nova Conta Bancária'}
            </Title>
            <Space size="large">
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/financeiro/contas-bancarias')}>
                Voltar
              </Button>
              <Tooltip title={podeSalvar ? undefined : 'Você não tem permissão para gerenciar contas bancárias'}>
                <Button type="primary" icon={<SaveOutlined />} loading={salvando} disabled={!podeSalvar} onClick={handleSalvar}>
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          <Row gutter={12}>
            <Col span={12}>
              <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]}>
                <Input placeholder="Ex: BB Conta Movimento, Caixa Loja 1" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="tipo" label="Tipo" rules={[{ required: true }]}>
                <Select options={Object.entries(TIPO_CONTA_BANCARIA_LABEL).map(([value, label]) => ({ value, label }))} />
              </Form.Item>
            </Col>
          </Row>
          {tipoSelecionado !== 'CAIXA_INTERNO' && (
            <Row gutter={12}>
              <Col span={24}>
                <Form.Item name="bancoId" label="Banco" rules={[{ required: true, message: 'Selecione o banco' }]}>
                  <Select
                    showSearch
                    optionFilterProp="label"
                    options={bancos?.map((b) => ({ value: b.id, label: `${b.codigo} — ${b.nome}` }))}
                  />
                </Form.Item>
              </Col>
            </Row>
          )}
          {tipoSelecionado !== 'CAIXA_INTERNO' && (
            <Row gutter={12}>
              <Col span={6}>
                <Form.Item name="agencia" label="Agência" rules={[{ max: 20 }]}>
                  <Input maxLength={20} />
                </Form.Item>
              </Col>
              <Col span={6}>
                <Form.Item name="agenciaDigito" label="Dígito ag." rules={[{ max: 2 }]}>
                  <Input maxLength={2} />
                </Form.Item>
              </Col>
              <Col span={6}>
                <Form.Item name="numero" label="Número da conta" rules={[{ max: 20 }]}>
                  <Input maxLength={20} />
                </Form.Item>
              </Col>
              <Col span={6}>
                <Form.Item name="numeroDigito" label="Dígito nº" rules={[{ max: 2 }]}>
                  <Input maxLength={2} />
                </Form.Item>
              </Col>
            </Row>
          )}
          <Row gutter={12}>
            <Col span={12}>
              <Form.Item name="cedenteNome" label="Nome do cedente" rules={[{ max: 150 }]}>
                <Input maxLength={150} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="cedenteDocumento" label="Documento do cedente" rules={[{ max: 20 }]}>
                <Input maxLength={20} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={24}>
              <Typography.Text type="secondary">
                Convênio de cobrança (opcional — só necessário pra gerar remessa CNAB 240 no EDI Bancário)
              </Typography.Text>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name="codigoConvenioCobranca" label="Código do convênio" rules={[{ max: 20 }]}>
                <Input maxLength={20} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="codigoCedenteCobranca" label="Código do cedente" rules={[{ max: 20 }]}>
                <Input maxLength={20} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="carteiraCobranca" label="Carteira" rules={[{ max: 5 }]}>
                <Input maxLength={5} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name="dataSaldoInicial" label="Data do saldo inicial" rules={[{ required: true, message: 'Informe a data' }]}>
                <Input type="date" disabled={modoEdicao} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                name="saldoInicial"
                label="Saldo inicial (R$)"
                rules={[{ required: true, message: 'Informe o saldo inicial' }]}
                tooltip={modoEdicao ? 'Não muda o saldo atual — mudanças de saldo só via movimento' : undefined}
              >
                <InputNumber<number> style={{ width: '100%' }} precision={2} decimalSeparator="," disabled={modoEdicao} />
              </Form.Item>
            </Col>
            {modoEdicao && conta && (
              <Col span={8}>
                <Form.Item label="Saldo atual">
                  <Input value={`R$ ${conta.saldoAtual.toFixed(2)}`} disabled />
                </Form.Item>
              </Col>
            )}
          </Row>
        </Form>
      </Card>
    </Flex>
  )
}
