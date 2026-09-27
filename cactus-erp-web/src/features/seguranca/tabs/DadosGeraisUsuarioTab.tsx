import { useState } from 'react'
import { Col, Form, Input, Row, Select } from 'antd'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useBuscaColaboradoresParaSelecao, useBuscaEmpresasParaSelecao, useColaboradorResumo, useTodosPapeis } from '../queries'

interface DadosGeraisUsuarioTabProps {
  modoEdicao: boolean
  colaboradorIdInicial?: number | null
}

/**
 * `senha` só aparece na criação (obrigatória lá) — trocar senha de um
 * usuário existente é ação dedicada (`AlterarSenhaModal`, aberta pelo botão
 * no cabeçalho do form), nunca reescrita por este PUT genérico. Mesmo
 * critério pros campos "Papel inicial"/"Empresa inicial" logo abaixo —
 * só existem na criação, opcionais: preenchidos os dois, `UsuarioFormPage`
 * atribui o papel na empresa logo após criar o usuário (1 passo em vez de
 * 2 — criar e depois abrir a aba Papéis por Empresa separadamente).
 */
export default function DadosGeraisUsuarioTab({ modoEdicao, colaboradorIdInicial }: DadosGeraisUsuarioTabProps) {
  const [termoColaborador, setTermoColaborador] = useState<string | undefined>(undefined)
  const { data: colaboradores, isLoading: carregandoColaboradores } = useBuscaColaboradoresParaSelecao(termoColaborador)
  const { data: colaboradorAtual } = useColaboradorResumo(colaboradorIdInicial)
  const [termoEmpresaInicial, setTermoEmpresaInicial] = useState<string | undefined>(undefined)
  const { data: empresasIniciais, isLoading: carregandoEmpresasIniciais } = useBuscaEmpresasParaSelecao(termoEmpresaInicial)
  const { data: papeis, isLoading: carregandoPapeis } = useTodosPapeis()

  return (
    <>
      <Row gutter={12}>
        <Col span={8}>
          <Form.Item name="login" label="Login" rules={[{ required: true, message: 'Informe o login' }, { max: 60 }]}>
            <Input autoComplete="off" />
          </Form.Item>
        </Col>
        <Col span={16}>
          <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 150 }]}>
            <Input />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={12}>
        <Col span={12}>
          <Form.Item name="email" label="E-mail" rules={[{ type: 'email', message: 'E-mail inválido' }, { max: 250 }]}>
            <Input />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="colaboradorId"
            label="Colaborador vinculado"
            tooltip="Opcional — contas de sistema/suporte/administração pura não precisam de um funcionário por trás."
          >
            <AsyncSelect
              allowClear
              opcoes={colaboradores}
              carregando={carregandoColaboradores}
              termo={termoColaborador}
              onTermoChange={setTermoColaborador}
              toOption={(c) => ({ value: c.id, label: c.nome })}
              labelValorAtual={colaboradorAtual?.nome}
            />
          </Form.Item>
        </Col>
      </Row>

      {!modoEdicao && (
        <>
          <Row gutter={12}>
            <Col span={12}>
              <Form.Item
                name="senha"
                label="Senha"
                rules={[
                  { required: true, message: 'Informe a senha' },
                  { min: 8, message: 'A senha precisa ter pelo menos 8 caracteres' },
                ]}
              >
                <Input.Password autoComplete="new-password" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={12}>
              <Form.Item
                name="empresaInicialId"
                label="Empresa inicial"
                tooltip="Opcional — preenchida junto com o Papel inicial, já atribui o papel nesta empresa assim que o usuário for criado, sem precisar de um segundo passo na aba Papéis por Empresa."
              >
                <AsyncSelect
                  allowClear
                  opcoes={empresasIniciais}
                  carregando={carregandoEmpresasIniciais}
                  termo={termoEmpresaInicial}
                  onTermoChange={setTermoEmpresaInicial}
                  toOption={(e) => ({ value: e.id, label: e.nomeFantasia || e.razaoSocial })}
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="papelInicialId" label="Papel inicial" tooltip="Opcional — exige a Empresa inicial também preenchida.">
                <Select
                  allowClear
                  showSearch
                  loading={carregandoPapeis}
                  optionFilterProp="label"
                  options={papeis?.filter((p) => p.situacao === 'ATIVO').map((p) => ({ value: p.id, label: p.nome }))}
                />
              </Form.Item>
            </Col>
          </Row>
        </>
      )}
    </>
  )
}
