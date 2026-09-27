import { useState } from 'react'
import { Button, Col, DatePicker, Flex, Form, Input, Row, Select, Tooltip } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { UFS } from '@shared/domain/pessoaUtils'
import GerenciarCargosModal from '../modals/GerenciarCargosModal'
import GerenciarSetoresModal from '../modals/GerenciarSetoresModal'
import { useCargos, useSetores } from '../queries'

/**
 * Cargo/Setor + matrícula/admissão/demissão + CTPS — campos de RH que só
 * existem em Colaborador. Botão "+" ao lado dos `Select` de Cargo/Setor abre
 * um modal de gerenciamento (`GerenciarCargosModal`/`GerenciarSetoresModal`)
 * — fecha a pendência de "sem CRUD pela UI" sem tirar o usuário do formulário
 * de Colaborador que já está preenchendo (ver CLAUDE.md do frontend).
 */
export default function RhTab() {
  const { data: cargos, isLoading: carregandoCargos } = useCargos()
  const { data: setores, isLoading: carregandoSetores } = useSetores()
  const [modalCargosAberto, setModalCargosAberto] = useState(false)
  const [modalSetoresAberto, setModalSetoresAberto] = useState(false)

  return (
    <>
      <Row gutter={12}>
        <Col span={12}>
          <Form.Item label="Cargo">
            <Flex gap={8}>
              <Form.Item name="cargoId" noStyle>
                <Select
                  allowClear
                  showSearch
                  loading={carregandoCargos}
                  optionFilterProp="label"
                  options={cargos?.map((c) => ({ value: c.id, label: c.nome }))}
                  style={{ flex: 1 }}
                />
              </Form.Item>
              <Tooltip title="Gerenciar cargos">
                <Button aria-label="Gerenciar cargos" icon={<PlusOutlined />} onClick={() => setModalCargosAberto(true)} />
              </Tooltip>
            </Flex>
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item label="Setor">
            <Flex gap={8}>
              <Form.Item name="setorId" noStyle>
                <Select
                  allowClear
                  showSearch
                  loading={carregandoSetores}
                  optionFilterProp="label"
                  options={setores?.map((s) => ({ value: s.id, label: s.nome }))}
                  style={{ flex: 1 }}
                />
              </Form.Item>
              <Tooltip title="Gerenciar setores">
                <Button aria-label="Gerenciar setores" icon={<PlusOutlined />} onClick={() => setModalSetoresAberto(true)} />
              </Tooltip>
            </Flex>
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={12}>
        <Col span={8}>
          <Form.Item name="matricula" label="Matrícula">
            <Input maxLength={10} />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="dataAdmissao" label="Data de admissão">
            <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="dataDemissao" label="Data de demissão">
            <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={12}>
        <Col span={8}>
          <Form.Item name="ctpsNumero" label="CTPS — Número">
            <Input maxLength={10} />
          </Form.Item>
        </Col>
        <Col span={6}>
          <Form.Item name="ctpsSerie" label="CTPS — Série">
            <Input maxLength={10} />
          </Form.Item>
        </Col>
        <Col span={6}>
          <Form.Item name="ctpsDataExpedicao" label="CTPS — Data de expedição">
            <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
          </Form.Item>
        </Col>
        <Col span={4}>
          <Form.Item name="ctpsUf" label="CTPS — UF">
            <Select showSearch allowClear options={UFS.map((uf) => ({ value: uf, label: uf }))} />
          </Form.Item>
        </Col>
      </Row>

      <GerenciarCargosModal open={modalCargosAberto} onClose={() => setModalCargosAberto(false)} />
      <GerenciarSetoresModal open={modalSetoresAberto} onClose={() => setModalSetoresAberto(false)} />
    </>
  )
}
