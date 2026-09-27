import { useState } from 'react'
import { Button, Col, Descriptions, Empty, Flex, Form, Input, InputNumber, Row, Segmented, Select, Tooltip } from 'antd'
import { CalculatorOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import AsyncSelect from '@shared/ui/AsyncSelect'
import RecalcularTributacaoModal from '../modals/RecalcularTributacaoModal'
import { useBuscaCest, useBuscaNcm, useGruposTributarios } from '../queries'
import { CSOSN_LABEL, CST_ICMS_LABEL, TIPO_ITEM_SPED_LABEL } from '../types'
import type { Csosn, CstIcms, ProdutoResponse } from '../types'

interface TributacaoTabProps {
  /** `undefined` em modo criação — "recalcular tributação padrão" só existe depois que o produto tem id. */
  produto?: ProdutoResponse
}

export default function TributacaoTab({ produto }: TributacaoTabProps) {
  const [termoNcm, setTermoNcm] = useState<string | undefined>(undefined)
  const [termoCest, setTermoCest] = useState<string | undefined>(undefined)
  const [modalRecalcularAberto, setModalRecalcularAberto] = useState(false)

  const { data: ncmOpcoes, isLoading: carregandoNcm } = useBuscaNcm(termoNcm)
  const { data: cestOpcoes, isLoading: carregandoCest } = useBuscaCest(termoCest)
  const { data: gruposTributarios, isLoading: carregandoGrupos } = useGruposTributarios()

  return (
    <>
      <Row gutter={12}>
        <Col span={8}>
          <Form.Item name="ncmId" label="NCM" rules={[{ required: true, message: 'Selecione o NCM' }]}>
            <AsyncSelect
              opcoes={ncmOpcoes}
              carregando={carregandoNcm}
              termo={termoNcm}
              onTermoChange={setTermoNcm}
              toOption={(ncm) => ({ value: ncm.id, label: `${ncm.codigo} — ${ncm.descricao}` })}
              labelValorAtual={produto?.ncmCodigo}
            />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item
            name="cestId"
            label="CEST"
            tooltip="Só preencher quando o produto é sujeito a Substituição Tributária."
          >
            <AsyncSelect
              allowClear
              opcoes={cestOpcoes}
              carregando={carregandoCest}
              termo={termoCest}
              onTermoChange={setTermoCest}
              toOption={(cest) => ({ value: cest.id, label: `${cest.codigo} — ${cest.descricao}` })}
              labelValorAtual={produto?.cestCodigo}
            />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item
            name="grupoTributarioId"
            label="Grupo tributário"
            rules={[{ required: true, message: 'Selecione o grupo tributário' }]}
            tooltip="Perfil tributário do produto (origem da mercadoria + regra de ICMS/PIS/COFINS/IPI) — cadastrado no Motor Tributário."
          >
            <Select
              showSearch
              loading={carregandoGrupos}
              optionFilterProp="label"
              options={gruposTributarios?.map((g) => ({ value: g.id, label: g.nome }))}
            />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={12}>
        <Col span={8}>
          <Form.Item name="tipoItemSped" label="Tipo de item (SPED)" tooltip="Registro 0200 do SPED Fiscal.">
            <Select
              allowClear
              showSearch
              optionFilterProp="label"
              options={Object.entries(TIPO_ITEM_SPED_LABEL).map(([value, label]) => ({ value, label }))}
            />
          </Form.Item>
        </Col>
        <Col span={4}>
          <Form.Item name="iat" label="Arred./Trunc." tooltip="Como o troco/arredondamento é tratado no cupom.">
            <Segmented
              options={[
                { label: 'Arredonda', value: 'ARREDONDAMENTO' },
                { label: 'Trunca', value: 'TRUNCAMENTO' },
              ]}
            />
          </Form.Item>
        </Col>
        <Col span={4}>
          <Form.Item name="ippt" label="Próprio/Terceiro" tooltip="Mercadoria própria ou em consignação de terceiro.">
            <Segmented
              options={[
                { label: 'Próprio', value: 'PROPRIO' },
                { label: 'Terceiro', value: 'TERCEIRO' },
              ]}
            />
          </Form.Item>
        </Col>
        <Col span={4}>
          <Form.Item name="totalizadorParcial" label="Totalizador parcial" rules={[{ max: 10 }]}>
            <Input />
          </Form.Item>
        </Col>
        <Col span={4}>
          <Form.Item name="codigoBalanca" label="Código balança">
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={12}>
        <Col span={6}>
          <Form.Item name="pafPSt" label="PAF-ST" rules={[{ max: 1 }]} tooltip="Indicador PAF-NFC-e — domínio não documentado, valor cru.">
            <Input maxLength={1} />
          </Form.Item>
        </Col>
      </Row>

      <Flex justify="space-between" align="center" style={{ marginTop: 8, marginBottom: 12 }}>
        <b>Tributação resolvida (cache p/ sincronização com o PDV)</b>
        <Tooltip title={produto ? undefined : 'Salve o produto primeiro'}>
          <Button
            icon={<CalculatorOutlined />}
            disabled={!produto}
            onClick={() => setModalRecalcularAberto(true)}
          >
            Recalcular
          </Button>
        </Tooltip>
      </Flex>

      {produto?.dataResolucaoPadrao ? (
        <Descriptions bordered size="small" column={3}>
          <Descriptions.Item label="CST ICMS">
            {produto.cstIcmsPadrao ? CST_ICMS_LABEL[produto.cstIcmsPadrao as CstIcms] : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="CSOSN">
            {produto.csosnPadrao ? CSOSN_LABEL[produto.csosnPadrao as Csosn] : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Resolvido em">
            {dayjs(produto.dataResolucaoPadrao).format('DD/MM/YYYY HH:mm')}
          </Descriptions.Item>
          <Descriptions.Item label="ICMS">{produto.aliquotaIcmsPadrao ?? '—'}%</Descriptions.Item>
          <Descriptions.Item label="IPI">{produto.aliquotaIpiPadrao ?? '—'}%</Descriptions.Item>
          <Descriptions.Item label="PIS">{produto.aliquotaPisPadrao ?? '—'}%</Descriptions.Item>
          <Descriptions.Item label="COFINS">{produto.aliquotaCofinsPadrao ?? '—'}%</Descriptions.Item>
          <Descriptions.Item label="ISSQN">{produto.aliquotaIssqnPadrao ?? '—'}%</Descriptions.Item>
        </Descriptions>
      ) : (
        <Empty
          description="Ainda não calculado — use Recalcular informando uma operação fiscal e UF de destino"
          image={Empty.PRESENTED_IMAGE_SIMPLE}
        />
      )}

      {produto && (
        <RecalcularTributacaoModal
          open={modalRecalcularAberto}
          produtoId={produto.id}
          onClose={() => setModalRecalcularAberto(false)}
        />
      )}
    </>
  )
}
