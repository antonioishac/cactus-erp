import { useEffect, useMemo, useState } from 'react'
import { Button, Col, Flex, Form, Input, Row, Select, Tooltip } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import GerenciarGruposModal from '../modals/GerenciarGruposModal'
import GerenciarSubgruposModal from '../modals/GerenciarSubgruposModal'
import GerenciarTiposModal from '../modals/GerenciarTiposModal'
import GerenciarUnidadesModal from '../modals/GerenciarUnidadesModal'
import GerenciarMarcasModal from '../modals/GerenciarMarcasModal'
import { useProdutoGrupos, useProdutoMarcas, useProdutoSubgrupos, useProdutoTipos, useProdutoUnidades } from '../queries'

interface DadosGeraisTabProps {
  /** Só disponível depois que o produto carrega (modo edição) — hidrata o Select de Grupo, que não é campo do request (só decide o filtro do Select de Subgrupo). */
  produtoGrupoIdInicial?: number
}

/**
 * Grupo → Subgrupo em cascata: `produtoGrupoId` NÃO é campo de `ProdutoRequest`
 * (só `produtoSubgrupoId` é enviado) — o Select de Grupo é estado local só pra
 * filtrar as opções do Select de Subgrupo. Em modo edição, `ProdutoResponse`
 * já devolve `produtoGrupoId`/`produtoGrupoNome` resolvidos (denormalizado a
 * partir do subgrupo), então dá pra hidratar o Grupo sem round-trip extra.
 */
export default function DadosGeraisTab({ produtoGrupoIdInicial }: DadosGeraisTabProps) {
  const form = Form.useFormInstance()
  const { data: grupos, isLoading: carregandoGrupos } = useProdutoGrupos()
  const { data: subgrupos, isLoading: carregandoSubgrupos } = useProdutoSubgrupos()
  const { data: tipos, isLoading: carregandoTipos } = useProdutoTipos()
  const { data: unidades, isLoading: carregandoUnidades } = useProdutoUnidades()
  const { data: marcas, isLoading: carregandoMarcas } = useProdutoMarcas()

  const [grupoId, setGrupoId] = useState<number | undefined>(undefined)
  const [modalAberto, setModalAberto] = useState<'grupo' | 'subgrupo' | 'tipo' | 'unidade' | 'marca' | null>(null)

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    setGrupoId(produtoGrupoIdInicial)
  }, [produtoGrupoIdInicial])
  /* oxlint-enable react-hooks/set-state-in-effect */

  const subgruposDoGrupo = useMemo(
    () => subgrupos?.filter((s) => s.produtoGrupoId === grupoId),
    [subgrupos, grupoId],
  )

  return (
    <>
      <Row gutter={12}>
        <Col span={6}>
          <Form.Item name="codigoInterno" label="Código interno" rules={[{ required: true, message: 'Informe o código interno' }, { max: 50 }]}>
            <Input />
          </Form.Item>
        </Col>
        <Col span={6}>
          <Form.Item name="gtin" label="GTIN / código de barras" rules={[{ max: 14 }]}>
            <Input maxLength={14} />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 150 }]}>
            <Input />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={12}>
        <Col span={16}>
          <Form.Item name="descricao" label="Descrição" rules={[{ max: 250 }]}>
            <Input />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item
            name="descricaoPdv"
            label="Descrição no cupom (PDV)"
            rules={[{ max: 30 }]}
            tooltip="Nome reduzido impresso no cupom fiscal — o PDV tem espaço limitado, diferente da descrição completa."
          >
            <Input maxLength={30} />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={12}>
        <Col span={6}>
          <Form.Item label="Unidade de medida">
            <Flex gap={8}>
              <Form.Item name="produtoUnidadeId" rules={[{ required: true, message: 'Informe a unidade' }]} noStyle>
                <Select
                  showSearch
                  loading={carregandoUnidades}
                  optionFilterProp="label"
                  options={unidades?.map((u) => ({ value: u.id, label: `${u.sigla} — ${u.descricao}` }))}
                  style={{ flex: 1 }}
                />
              </Form.Item>
              <Tooltip title="Gerenciar unidades">
                <Button aria-label="Gerenciar unidades" icon={<PlusOutlined />} onClick={() => setModalAberto('unidade')} />
              </Tooltip>
            </Flex>
          </Form.Item>
        </Col>
        <Col span={6}>
          <Form.Item label="Grupo">
            <Flex gap={8}>
              <Select
                showSearch
                allowClear
                loading={carregandoGrupos}
                optionFilterProp="label"
                value={grupoId}
                options={grupos?.map((g) => ({ value: g.id, label: g.nome }))}
                onChange={(valor) => {
                  setGrupoId(valor)
                  form.setFieldValue('produtoSubgrupoId', undefined)
                }}
                style={{ flex: 1 }}
              />
              <Tooltip title="Gerenciar grupos">
                <Button aria-label="Gerenciar grupos" icon={<PlusOutlined />} onClick={() => setModalAberto('grupo')} />
              </Tooltip>
            </Flex>
          </Form.Item>
        </Col>
        <Col span={6}>
          <Form.Item label="Subgrupo">
            <Flex gap={8}>
              <Form.Item name="produtoSubgrupoId" rules={[{ required: true, message: 'Informe o subgrupo' }]} noStyle>
                <Select
                  showSearch
                  disabled={!grupoId}
                  loading={carregandoSubgrupos}
                  optionFilterProp="label"
                  placeholder={grupoId ? undefined : 'Selecione um grupo primeiro'}
                  options={subgruposDoGrupo?.map((s) => ({ value: s.id, label: s.nome }))}
                  style={{ flex: 1 }}
                />
              </Form.Item>
              <Tooltip title="Gerenciar subgrupos">
                <Button aria-label="Gerenciar subgrupos" icon={<PlusOutlined />} onClick={() => setModalAberto('subgrupo')} />
              </Tooltip>
            </Flex>
          </Form.Item>
        </Col>
        <Col span={6}>
          <Form.Item label="Marca">
            <Flex gap={8}>
              <Form.Item name="produtoMarcaId" noStyle>
                <Select
                  allowClear
                  showSearch
                  loading={carregandoMarcas}
                  optionFilterProp="label"
                  options={marcas?.map((m) => ({ value: m.id, label: m.nome }))}
                  style={{ flex: 1 }}
                />
              </Form.Item>
              <Tooltip title="Gerenciar marcas">
                <Button aria-label="Gerenciar marcas" icon={<PlusOutlined />} onClick={() => setModalAberto('marca')} />
              </Tooltip>
            </Flex>
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={12}>
        <Col span={8}>
          <Form.Item label="Tipo de item">
            <Flex gap={8}>
              <Form.Item name="produtoTipoId" noStyle>
                <Select
                  allowClear
                  showSearch
                  loading={carregandoTipos}
                  optionFilterProp="label"
                  options={tipos?.map((t) => ({ value: t.id, label: `${t.codigo} — ${t.descricao}` }))}
                  style={{ flex: 1 }}
                />
              </Form.Item>
              <Tooltip title="Gerenciar tipos">
                <Button aria-label="Gerenciar tipos" icon={<PlusOutlined />} onClick={() => setModalAberto('tipo')} />
              </Tooltip>
            </Flex>
          </Form.Item>
        </Col>
      </Row>

      <GerenciarGruposModal open={modalAberto === 'grupo'} onClose={() => setModalAberto(null)} />
      <GerenciarSubgruposModal open={modalAberto === 'subgrupo'} onClose={() => setModalAberto(null)} />
      <GerenciarTiposModal open={modalAberto === 'tipo'} onClose={() => setModalAberto(null)} />
      <GerenciarUnidadesModal open={modalAberto === 'unidade'} onClose={() => setModalAberto(null)} />
      <GerenciarMarcasModal open={modalAberto === 'marca'} onClose={() => setModalAberto(null)} />
    </>
  )
}
