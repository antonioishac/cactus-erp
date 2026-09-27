import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Col, Flex, Form, Input, InputNumber, Row, Segmented, Select, Space, Tooltip, Typography } from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useBuscaFornecedoresParaSelecao, useContasBancariasAtivas, useCriarContaPagar, useGerarContaPagarParcelada, useNaturezasFinanceiras, useTiposDocumentoFinanceiro } from './queries'
import { TIPO_CONTA_BANCARIA_LABEL } from './types'
import type { ContaPagarRequest, GerarContaPagarParceladaRequest } from './types'

const { Title } = Typography

type FormularioContaPagar = ContaPagarRequest & Partial<Omit<GerarContaPagarParceladaRequest, keyof ContaPagarRequest>>

/** Espelho exato de `ContaReceberFormPage` — só criação (sem `PUT`), Segmented Avulso/Parcelado
 * troca entre `POST /contas-pagar` e `POST /contas-pagar/gerar-parcelado`. */
export default function ContaPagarFormPage() {
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { empresaAtivaId, authorities } = useAuth()
  const podeSalvar = hasAuthority(authorities, 'FINANCEIRO_LANCAR')
  const [form] = Form.useForm<FormularioContaPagar>()
  const [modo, setModo] = useState<'AVULSO' | 'PARCELADO'>('AVULSO')
  const [termoFornecedor, setTermoFornecedor] = useState<string | undefined>(undefined)

  const { data: fornecedores, isLoading: carregandoFornecedores } = useBuscaFornecedoresParaSelecao(termoFornecedor)
  const { data: naturezas } = useNaturezasFinanceiras(empresaAtivaId ?? undefined)
  const naturezasDespesa = naturezas?.filter((n) => n.tipo === 'DESPESA')
  const { data: tiposDocumento } = useTiposDocumentoFinanceiro()
  const { data: contasBancarias, isLoading: carregandoContas } = useContasBancariasAtivas(empresaAtivaId ?? undefined)

  const criar = useCriarContaPagar()
  const gerarParcelado = useGerarContaPagarParcelada()
  const salvando = criar.isPending || gerarParcelado.isPending

  async function handleSalvar() {
    const valores = await form.validateFields()
    const base = { ...valores, empresaId: empresaAtivaId ?? -1 }
    try {
      if (modo === 'AVULSO') {
        const criada = await criar.mutateAsync({
          ...base,
          dataEmissao: dayjs(valores.dataEmissao).format('YYYY-MM-DD'),
          dataVencimento: dayjs(valores.dataVencimento).format('YYYY-MM-DD'),
          descontoAte: valores.descontoAte ? dayjs(valores.descontoAte).format('YYYY-MM-DD') : undefined,
        })
        message.success('Título criado com sucesso.')
        navigate(`/financeiro/contas-pagar/${criada.id}`)
      } else {
        const geradas = await gerarParcelado.mutateAsync({
          ...base,
          dataEmissao: dayjs(valores.dataEmissao).format('YYYY-MM-DD'),
          primeiroVencimento: dayjs(valores.primeiroVencimento).format('YYYY-MM-DD'),
          descontoAte: valores.descontoAte ? dayjs(valores.descontoAte).format('YYYY-MM-DD') : undefined,
        } as GerarContaPagarParceladaRequest)
        message.success(`${geradas.length} parcela(s) gerada(s) com sucesso.`)
        navigate('/financeiro/contas-pagar')
      }
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar título.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Breadcrumb items={[{ title: <a onClick={() => navigate('/financeiro/contas-pagar')}>Contas a Pagar</a> }, { title: 'Novo título' }]} />

      <Card>
        <Form form={form} name="contaPagarForm" layout="vertical" disabled={!podeSalvar}>
          <Flex justify="space-between" align="center" wrap="wrap" gap={16} style={{ marginBottom: 24 }}>
            <Title level={4} style={{ margin: 0 }}>
              Novo Título a Pagar
            </Title>
            <Space size="large">
              <Segmented
                value={modo}
                onChange={(v) => setModo(v as 'AVULSO' | 'PARCELADO')}
                options={[
                  { label: 'Avulso (1 parcela)', value: 'AVULSO' },
                  { label: 'Parcelado (N parcelas)', value: 'PARCELADO' },
                ]}
              />
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/financeiro/contas-pagar')}>
                Voltar
              </Button>
              <Tooltip title={podeSalvar ? undefined : 'Você não tem permissão para lançar títulos'}>
                <Button type="primary" icon={<SaveOutlined />} loading={salvando} disabled={!podeSalvar} onClick={handleSalvar}>
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          <Row gutter={12}>
            <Col span={12}>
              <Form.Item name="fornecedorId" label="Fornecedor" rules={[{ required: true, message: 'Selecione o fornecedor' }]}>
                <AsyncSelect
                  opcoes={fornecedores}
                  carregando={carregandoFornecedores}
                  termo={termoFornecedor}
                  onTermoChange={setTermoFornecedor}
                  toOption={(f) => ({ value: f.id, label: f.nome })}
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="numeroDocumento" label="Número do documento" rules={[{ max: 30 }]}>
                <Input maxLength={30} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={12}>
              <Form.Item name="naturezaFinanceiraId" label="Natureza financeira" rules={[{ required: true, message: 'Selecione a natureza' }]}>
                <Select options={naturezasDespesa?.map((n) => ({ value: n.id, label: `${n.codigo} — ${n.descricao}` }))} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="tipoDocumentoId" label="Tipo de documento" rules={[{ required: true, message: 'Selecione o tipo' }]}>
                <Select options={tiposDocumento?.map((t) => ({ value: t.id, label: `${t.codigo} — ${t.descricao}` }))} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={24}>
              <Form.Item name="descricao" label="Descrição" rules={[{ max: 250 }]}>
                <Input.TextArea rows={2} maxLength={250} showCount />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name="dataEmissao" label="Data de emissão" rules={[{ required: true, message: 'Informe a emissão' }]}>
                <Input type="date" />
              </Form.Item>
            </Col>
            {modo === 'AVULSO' ? (
              <>
                <Col span={8}>
                  <Form.Item name="dataVencimento" label="Vencimento" rules={[{ required: true, message: 'Informe o vencimento' }]}>
                    <Input type="date" />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item name="valorOriginal" label="Valor (R$)" rules={[{ required: true, message: 'Informe o valor' }]}>
                    <InputNumber<number> style={{ width: '100%' }} min={0.01} precision={2} decimalSeparator="," />
                  </Form.Item>
                </Col>
              </>
            ) : (
              <>
                <Col span={8}>
                  <Form.Item name="quantidadeParcelas" label="Qtd. parcelas" rules={[{ required: true, message: 'Informe a quantidade' }]}>
                    <InputNumber style={{ width: '100%' }} min={1} />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item name="valorTotal" label="Valor total (R$)" rules={[{ required: true, message: 'Informe o valor total' }]}>
                    <InputNumber<number> style={{ width: '100%' }} min={0.01} precision={2} decimalSeparator="," />
                  </Form.Item>
                </Col>
              </>
            )}
          </Row>
          {modo === 'PARCELADO' && (
            <Row gutter={12}>
              <Col span={8}>
                <Form.Item
                  name="primeiroVencimento"
                  label="1º vencimento"
                  rules={[{ required: true, message: 'Informe o 1º vencimento' }]}
                >
                  <Input type="date" />
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item
                  name="intervaloEntreParcelasDias"
                  label="Intervalo entre parcelas (dias)"
                  rules={[{ required: true, message: 'Informe o intervalo' }]}
                >
                  <InputNumber style={{ width: '100%' }} min={0} />
                </Form.Item>
              </Col>
            </Row>
          )}

          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name="contaBancariaId" label="Conta bancária prevista">
                <Select
                  allowClear
                  loading={carregandoContas}
                  options={contasBancarias?.map((c) => ({ value: c.id, label: `${c.nome} — ${TIPO_CONTA_BANCARIA_LABEL[c.tipo]}` }))}
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="descontoAte" label="Desconto até">
                <Input type="date" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="percentualDesconto" label="% desconto">
                <InputNumber<number> style={{ width: '100%' }} min={0} precision={4} decimalSeparator="," />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name="percentualMultaAtraso" label="% multa por atraso (override)">
                <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="percentualJurosDiaAtraso" label="% juros/dia atraso (override)">
                <InputNumber<number> style={{ width: '100%' }} min={0} precision={4} decimalSeparator="," />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Card>
    </Flex>
  )
}
