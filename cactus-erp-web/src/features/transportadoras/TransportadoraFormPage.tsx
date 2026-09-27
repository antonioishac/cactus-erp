import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Flex, Form, Segmented, Skeleton, Space, Switch, Tabs, Tooltip } from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import EnderecosTab from '@shared/ui/pessoa/EnderecosTab'
import ContatosTab from '@shared/ui/pessoa/ContatosTab'
import TelefonesTab from '@shared/ui/pessoa/TelefonesTab'
import DadosGeraisTab from './tabs/DadosGeraisTab'
import DadosOperacionaisTab from './tabs/DadosOperacionaisTab'
import { useAtualizarTransportadora, useCriarTransportadora, useTransportadora } from './queries'
import type { ContatoDTO, EnderecoDTO, TelefoneDTO, TipoPessoa, TransportadoraRequest } from './types'

const VALORES_PADRAO_NOVO: Partial<TransportadoraRequest> = {
  tipoPessoa: 'FISICA',
  situacao: 'ATIVO',
}

/**
 * Formulário de Transportadora — mesmo molde de `FornecedorFormPage` (Pessoa
 * física/jurídica + 3 listas filhas via `shared/ui/pessoa`), fechando o gap
 * registrado no BACKLOG (`Transportadora` construída no backend na Fase 7 do
 * `vendas-service`, mas sem tela até esta entrega). Diferença real: aba
 * "Dados Operacionais" (`rntrc`/`observacao`) no lugar de "Compras".
 */
export default function TransportadoraFormPage() {
  const { id } = useParams<{ id: string }>()
  const transportadoraId = id ? Number(id) : undefined
  const modoEdicao = transportadoraId !== undefined
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<TransportadoraRequest>()

  const { data: transportadora, isLoading: carregando } = useTransportadora(transportadoraId)
  const criar = useCriarTransportadora()
  const atualizar = useAtualizarTransportadora(transportadoraId ?? -1)
  const salvando = criar.isPending || atualizar.isPending

  const { authorities } = useAuth()
  const podeSalvar = hasAuthority(
    authorities,
    modoEdicao ? 'CADASTRO_TRANSPORTADORA_EDITAR' : 'CADASTRO_TRANSPORTADORA_CRIAR',
  )

  const [tipoPessoa, setTipoPessoa] = useState<TipoPessoa>('FISICA')
  const [enderecos, setEnderecos] = useState<EnderecoDTO[]>([])
  const [contatos, setContatos] = useState<ContatoDTO[]>([])
  const [telefones, setTelefones] = useState<TelefoneDTO[]>([])

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (!transportadora) return
    setTipoPessoa(transportadora.tipoPessoa)
    setEnderecos(transportadora.enderecos)
    setContatos(transportadora.contatos)
    setTelefones(transportadora.telefones)
    form.setFieldsValue({
      ...transportadora,
      cpf: transportadora.tipoPessoa === 'FISICA' ? transportadora.documento : undefined,
      cnpj: transportadora.tipoPessoa === 'JURIDICA' ? transportadora.documento : undefined,
      // @ts-expect-error DatePicker consome dayjs, não a string ISO do backend
      dataNascimento: transportadora.dataNascimento ? dayjs(transportadora.dataNascimento) : undefined,
    })
  }, [transportadora, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    const request: TransportadoraRequest = {
      ...valores,
      tipoPessoa,
      dataNascimento: valores.dataNascimento
        ? // @ts-expect-error valores.dataNascimento é um objeto dayjs em runtime
          valores.dataNascimento.format('YYYY-MM-DD')
        : undefined,
      enderecos,
      contatos,
      telefones,
    }

    try {
      if (modoEdicao) {
        await atualizar.mutateAsync(request)
        message.success('Transportadora atualizada com sucesso.')
      } else {
        const criada = await criar.mutateAsync(request)
        message.success('Transportadora criada com sucesso.')
        navigate(`/cadastro-base/transportadoras/${criada.id}`, { replace: true })
        return
      }
    } catch (erro) {
      const mensagem = erro instanceof Error ? erro.message : 'Falha ao salvar transportadora.'
      message.error(mensagem)
    }
  }

  if (modoEdicao && carregando) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 8 }} />
      </Card>
    )
  }

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate('/cadastro-base/transportadoras')}>Transportadoras</a> },
          { title: modoEdicao ? transportadora?.nome : 'Nova transportadora' },
        ]}
      />

      <Card>
        <Form form={form} layout="vertical" initialValues={VALORES_PADRAO_NOVO} disabled={!podeSalvar}>
          <Flex justify="space-between" align="center" wrap="wrap" gap={16} style={{ marginBottom: 24 }}>
            <Segmented<TipoPessoa>
              value={tipoPessoa}
              disabled={modoEdicao || !podeSalvar}
              onChange={setTipoPessoa}
              options={[
                { label: 'Pessoa Física', value: 'FISICA' },
                { label: 'Pessoa Jurídica', value: 'JURIDICA' },
              ]}
            />

            <Space size="large">
              <Form.Item
                name="situacao"
                valuePropName="checked"
                noStyle
                normalize={(v: boolean) => (v ? 'ATIVO' : 'INATIVO')}
                getValueProps={(v: string) => ({ checked: v === 'ATIVO' })}
              >
                <Switch checkedChildren="Ativo" unCheckedChildren="Inativo" />
              </Form.Item>
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/cadastro-base/transportadoras')}>
                Cancelar
              </Button>
              <Tooltip title={podeSalvar ? undefined : 'Você não tem permissão para salvar alterações em transportadoras'}>
                <Button
                  type="primary"
                  icon={<SaveOutlined />}
                  loading={salvando}
                  disabled={!podeSalvar}
                  onClick={handleSalvar}
                >
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          <Tabs
            items={[
              {
                key: 'dados-gerais',
                label: 'Dados Gerais',
                children: <DadosGeraisTab tipoPessoa={tipoPessoa} />,
              },
              {
                key: 'enderecos',
                label: `Endereços (${enderecos.length})`,
                children: (
                  <EnderecosTab enderecos={enderecos} onChange={setEnderecos} somenteLeitura={!podeSalvar} />
                ),
              },
              {
                key: 'contatos',
                label: `Contatos (${contatos.length})`,
                children: <ContatosTab contatos={contatos} onChange={setContatos} somenteLeitura={!podeSalvar} />,
              },
              {
                key: 'telefones',
                label: `Telefones (${telefones.length})`,
                children: (
                  <TelefonesTab telefones={telefones} onChange={setTelefones} somenteLeitura={!podeSalvar} />
                ),
              },
              {
                key: 'operacionais',
                label: 'Dados Operacionais',
                children: <DadosOperacionaisTab />,
              },
            ]}
          />
        </Form>
      </Card>
    </Flex>
  )
}
