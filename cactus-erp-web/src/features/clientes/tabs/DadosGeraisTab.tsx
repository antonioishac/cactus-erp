import { Col, DatePicker, Form, Input, Row } from 'antd'
import type { TipoPessoa } from '../types'

interface DadosGeraisTabProps {
  tipoPessoa: TipoPessoa
}

/**
 * Renderizado dentro do `<Form>` único da página (`ClienteFormPage`), não um
 * form próprio — Ant Design Tabs mantém painéis inativos montados por padrão,
 * então um Form só cobrindo todas as abas funciona sem perder valor ao trocar
 * de aba. `tipoPessoa` só decide QUAIS campos aparecem aqui — o valor em si é
 * controlado no cabeçalho da página (Segmented), não dentro desta aba.
 */
export default function DadosGeraisTab({ tipoPessoa }: DadosGeraisTabProps) {
  return (
    <>
      <Row gutter={12}>
        <Col span={24}>
          <Form.Item
            name="nome"
            label={tipoPessoa === 'FISICA' ? 'Nome completo' : 'Razão social'}
            rules={[{ required: true, message: 'Informe o nome' }, { max: 150 }]}
          >
            <Input />
          </Form.Item>
        </Col>
      </Row>

      {tipoPessoa === 'FISICA' ? (
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="cpf" label="CPF" rules={[{ max: 11 }]}>
              <Input placeholder="Somente números" maxLength={11} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="rg" label="RG">
              <Input maxLength={20} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="dataNascimento" label="Data de nascimento">
              <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
            </Form.Item>
          </Col>
        </Row>
      ) : (
        <>
          <Row gutter={12}>
            <Col span={12}>
              <Form.Item name="cnpj" label="CNPJ" rules={[{ max: 14 }]}>
                <Input placeholder="Somente números" maxLength={14} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="nomeFantasia" label="Nome fantasia" rules={[{ max: 150 }]}>
                <Input />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={12}>
              <Form.Item name="inscricaoEstadual" label="Inscrição estadual">
                <Input maxLength={20} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="inscricaoMunicipal" label="Inscrição municipal">
                <Input maxLength={20} />
              </Form.Item>
            </Col>
          </Row>
        </>
      )}

      <Row gutter={12}>
        <Col span={12}>
          <Form.Item name="email" label="E-mail" rules={[{ type: 'email', message: 'E-mail inválido' }, { max: 250 }]}>
            <Input />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item name="url" label="Site" rules={[{ max: 250 }]}>
            <Input placeholder="https://" />
          </Form.Item>
        </Col>
      </Row>
    </>
  )
}
