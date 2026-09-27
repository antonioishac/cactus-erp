import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Col, Divider, Empty, Flex, Form, InputNumber, Row, Select, Skeleton, Space, Popconfirm, Table, Typography } from 'antd'
import { ArrowLeftOutlined, DeleteOutlined, EditOutlined, PlusOutlined, SaveOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import IcmsPorUfModal from './modals/IcmsPorUfModal'
import {
  useAtualizarConfiguracaoTributaria,
  useConfiguracaoTributaria,
  useCriarConfiguracaoTributaria,
  useTodasOperacoesFiscais,
  useTodosGruposTributarios,
} from './queries'
import {
  CST_ICMS_LABEL,
  CST_IPI_LABEL,
  CST_PIS_COFINS_LABEL,
  CSOSN_LABEL,
  MODALIDADE_BASE_CALCULO_LABEL,
} from './types'
import type { ConfiguracaoTributariaRequest, IcmsConfiguracaoDTO } from './types'

const { Text } = Typography

const OPCOES_MODALIDADE = Object.entries(MODALIDADE_BASE_CALCULO_LABEL).map(([value, label]) => ({ value, label }))
const OPCOES_CST_PIS_COFINS = Object.entries(CST_PIS_COFINS_LABEL).map(([value, label]) => ({ value, label }))
const OPCOES_CST_IPI = Object.entries(CST_IPI_LABEL).map(([value, label]) => ({ value, label }))

/**
 * A célula da matriz Grupo Tributário × Operação Fiscal — o coração do motor tributário
 * (ver ANÁLISE TRIBUTÁRIA no CLAUDE.md raiz). ICMS varia por UF de destino (`icmsPorUf`,
 * lista gerenciada como estado local + modal por linha, mesmo padrão de Endereços/
 * Contatos/Telefones de Pessoa); PIS/COFINS/IPI são 1:1 com a célula (não variam por UF).
 */
export default function ConfiguracaoTributariaFormPage() {
  const { id } = useParams<{ id: string }>()
  const configuracaoId = id ? Number(id) : undefined
  const modoEdicao = configuracaoId !== undefined
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<Omit<ConfiguracaoTributariaRequest, 'icmsPorUf'>>()
  const { authorities } = useAuth()
  const podeSalvar = hasAuthority(authorities, modoEdicao ? 'CADASTRO_TRIBUTACAO_EDITAR' : 'CADASTRO_TRIBUTACAO_CRIAR')

  const { data: configuracao, isLoading: carregando } = useConfiguracaoTributaria(configuracaoId)
  const { data: grupos } = useTodosGruposTributarios()
  const { data: operacoes } = useTodasOperacoesFiscais()
  const criar = useCriarConfiguracaoTributaria()
  const atualizar = useAtualizarConfiguracaoTributaria()
  const salvando = criar.isPending || atualizar.isPending

  const [icmsPorUf, setIcmsPorUf] = useState<IcmsConfiguracaoDTO[]>([])
  const [modalIcmsAberto, setModalIcmsAberto] = useState(false)
  const [indiceIcmsEdicao, setIndiceIcmsEdicao] = useState<number | null>(null)

  /* oxlint-disable react-hooks/set-state-in-effect */
  if (configuracao && form.getFieldValue('grupoTributarioId') === undefined) {
    form.setFieldsValue(configuracao)
    setIcmsPorUf(configuracao.icmsPorUf)
  }
  /* oxlint-enable react-hooks/set-state-in-effect */

  function abrirNovoIcms() {
    setIndiceIcmsEdicao(null)
    setModalIcmsAberto(true)
  }

  function abrirEdicaoIcms(indice: number) {
    setIndiceIcmsEdicao(indice)
    setModalIcmsAberto(true)
  }

  function salvarIcms(valor: IcmsConfiguracaoDTO) {
    setIcmsPorUf((atual) => (indiceIcmsEdicao === null ? [...atual, valor] : atual.map((it, i) => (i === indiceIcmsEdicao ? valor : it))))
    setModalIcmsAberto(false)
  }

  function removerIcms(indice: number) {
    setIcmsPorUf((atual) => atual.filter((_, i) => i !== indice))
  }

  async function handleSalvar() {
    const valores = await form.validateFields()
    const request: ConfiguracaoTributariaRequest = { ...valores, icmsPorUf }

    try {
      if (modoEdicao) {
        await atualizar.mutateAsync({ id: configuracaoId as number, request })
        message.success('Configuração tributária atualizada com sucesso.')
      } else {
        await criar.mutateAsync(request)
        message.success('Configuração tributária criada com sucesso.')
      }
      navigate('/cadastro-base/configuracoes-tributarias')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar configuração tributária.')
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
          { title: <a onClick={() => navigate('/cadastro-base/configuracoes-tributarias')}>Configurações Tributárias</a> },
          { title: modoEdicao ? `${configuracao?.grupoTributarioNome} × ${configuracao?.operacaoFiscalNome}` : 'Nova configuração' },
        ]}
      />

      <Card>
        <Form form={form} layout="vertical" disabled={!podeSalvar}>
          <Flex justify="flex-end" style={{ marginBottom: 16 }}>
            <Space>
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/cadastro-base/configuracoes-tributarias')}>
                Cancelar
              </Button>
              <Button type="primary" icon={<SaveOutlined />} loading={salvando} onClick={handleSalvar} disabled={!podeSalvar}>
                Salvar
              </Button>
            </Space>
          </Flex>

          <Row gutter={12}>
            <Col span={12}>
              <Form.Item name="grupoTributarioId" label="Grupo Tributário" rules={[{ required: true, message: 'Selecione o grupo tributário' }]}>
                <Select
                  showSearch
                  optionFilterProp="label"
                  options={grupos?.map((g) => ({ value: g.id, label: g.nome }))}
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="operacaoFiscalId" label="Operação Fiscal" rules={[{ required: true, message: 'Selecione a operação fiscal' }]}>
                <Select
                  showSearch
                  optionFilterProp="label"
                  options={operacoes?.map((o) => ({ value: o.id, label: o.nome }))}
                />
              </Form.Item>
            </Col>
          </Row>

          <Divider titlePlacement="left">ICMS por UF de destino</Divider>
          <Text type="secondary" style={{ display: 'block', marginBottom: 12 }}>
            A alíquota/base de cálculo do ICMS varia por estado — cadastre 1 linha por UF de destino que esta operação atende.
          </Text>
          <Flex justify="flex-end" style={{ marginBottom: 12 }}>
            <Button icon={<PlusOutlined />} onClick={abrirNovoIcms} disabled={!podeSalvar}>
              Adicionar UF
            </Button>
          </Flex>
          <Table<IcmsConfiguracaoDTO>
            rowKey={(icms) => icmsPorUf.indexOf(icms)}
            dataSource={icmsPorUf}
            pagination={false}
            locale={{ emptyText: <Empty description="Nenhuma UF configurada" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
            columns={[
              { title: 'UF', dataIndex: 'ufDestino' },
              { title: 'CST', dataIndex: 'cstIcms', render: (v) => (v ? CST_ICMS_LABEL[v as keyof typeof CST_ICMS_LABEL] : '—') },
              { title: 'CSOSN', dataIndex: 'csosn', render: (v) => (v ? CSOSN_LABEL[v as keyof typeof CSOSN_LABEL] : '—') },
              { title: 'Alíquota', dataIndex: 'aliquota', render: (v) => (v != null ? `${v}%` : '—') },
              { title: 'MVA', dataIndex: 'mva', render: (v) => (v != null ? `${v}%` : '—') },
              ...(podeSalvar
                ? [
                    {
                      title: 'Ações',
                      key: 'acoes',
                      width: 100,
                      render: (_: unknown, __: IcmsConfiguracaoDTO, index: number) => (
                        <Space>
                          <Button type="text" icon={<EditOutlined />} onClick={() => abrirEdicaoIcms(index)} />
                          <Popconfirm title="Remover esta UF?" onConfirm={() => removerIcms(index)}>
                            <Button type="text" danger icon={<DeleteOutlined />} />
                          </Popconfirm>
                        </Space>
                      ),
                    },
                  ]
                : []),
            ]}
          />

          <Divider titlePlacement="left">PIS</Divider>
          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name={['pis', 'cst']} label="CST">
                <Select allowClear showSearch optionFilterProp="label" options={OPCOES_CST_PIS_COFINS} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name={['pis', 'modalidadeBaseCalculo']} label="Modalidade da base de cálculo">
                <Select allowClear options={OPCOES_MODALIDADE} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name={['pis', 'aliquotaPercentual']} label="Alíquota (%)">
                <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
          </Row>

          <Divider titlePlacement="left">COFINS</Divider>
          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name={['cofins', 'cst']} label="CST">
                <Select allowClear showSearch optionFilterProp="label" options={OPCOES_CST_PIS_COFINS} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name={['cofins', 'modalidadeBaseCalculo']} label="Modalidade da base de cálculo">
                <Select allowClear options={OPCOES_MODALIDADE} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name={['cofins', 'aliquotaPercentual']} label="Alíquota (%)">
                <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
          </Row>

          <Divider titlePlacement="left">IPI</Divider>
          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name={['ipi', 'cst']} label="CST">
                <Select allowClear showSearch optionFilterProp="label" options={OPCOES_CST_IPI} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name={['ipi', 'modalidadeBaseCalculo']} label="Modalidade da base de cálculo">
                <Select allowClear options={OPCOES_MODALIDADE} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name={['ipi', 'aliquotaPercentual']} label="Alíquota (%)">
                <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Card>

      <IcmsPorUfModal
        open={modalIcmsAberto}
        valorInicial={indiceIcmsEdicao !== null ? icmsPorUf[indiceIcmsEdicao] : undefined}
        ufsIndisponiveis={icmsPorUf.filter((_, i) => i !== indiceIcmsEdicao).map((icms) => icms.ufDestino)}
        onCancel={() => setModalIcmsAberto(false)}
        onSalvar={salvarIcms}
      />
    </Flex>
  )
}
