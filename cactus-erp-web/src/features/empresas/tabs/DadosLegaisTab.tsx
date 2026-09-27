import { useState } from 'react'
import { Button, Col, DatePicker, Flex, Form, Input, Row, Select, Tooltip } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import GerenciarContadoresModal from '../modals/GerenciarContadoresModal'
import { useContadores } from '../queries'

/**
 * Dados de registro/contabilidade da empresa — junta comercial, CEI, CNAE,
 * contador responsável. Botão "+" ao lado do `Select` de Contador abre
 * `GerenciarContadoresModal` (mesmo padrão de Cargo/Setor em Colaborador) —
 * fecha a pendência 0b registrada no CLAUDE.md.
 */
export default function DadosLegaisTab() {
  const { data: contadores, isLoading: carregandoContadores } = useContadores()
  const [modalContadoresAberto, setModalContadoresAberto] = useState(false)

  return (
    <>
      <Row gutter={12}>
        <Col span={8}>
          <Form.Item name="email" label="E-mail" rules={[{ type: 'email', message: 'E-mail inválido' }, { max: 250 }]}>
            <Input />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="site" label="Site" rules={[{ max: 250 }]}>
            <Input placeholder="https://" />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="contato" label="Contato" rules={[{ max: 100 }]}>
            <Input placeholder="Nome do responsável" />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={12}>
        <Col span={8}>
          <Form.Item name="dataConstituicao" label="Data de constituição">
            <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="codigoCnaePrincipal" label="CNAE principal" rules={[{ max: 7 }]}>
            <Input placeholder="0000000" maxLength={7} />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="cei" label="CEI">
            <Input maxLength={12} />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={12}>
        <Col span={12}>
          <Form.Item name="inscricaoJuntaComercial" label="Inscrição na junta comercial">
            <Input maxLength={30} />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item name="dataInscricaoJuntaComercial" label="Data de inscrição na junta comercial">
            <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={12}>
        <Col span={12}>
          <Form.Item label="Contador responsável">
            <Flex gap={8}>
              <Form.Item name="contadorId" noStyle>
                <Select
                  allowClear
                  showSearch
                  loading={carregandoContadores}
                  optionFilterProp="label"
                  options={contadores?.map((c) => ({ value: c.id, label: `${c.nome} (CRC ${c.crc})` }))}
                  style={{ flex: 1 }}
                />
              </Form.Item>
              <Tooltip title="Gerenciar contadores">
                <Button aria-label="Gerenciar contadores" icon={<PlusOutlined />} onClick={() => setModalContadoresAberto(true)} />
              </Tooltip>
            </Flex>
          </Form.Item>
        </Col>
      </Row>

      <GerenciarContadoresModal open={modalContadoresAberto} onClose={() => setModalContadoresAberto(false)} />
    </>
  )
}
