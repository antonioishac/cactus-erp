import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Flex, Form, Segmented, Skeleton, Space, Switch, Tabs, Tooltip } from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import DadosGeraisTab from './tabs/DadosGeraisTab'
import EnderecosTab from '@shared/ui/pessoa/EnderecosTab'
import ContatosTab from '@shared/ui/pessoa/ContatosTab'
import TelefonesTab from '@shared/ui/pessoa/TelefonesTab'
import ComercialTab from './tabs/ComercialTab'
import { useAtualizarCliente, useCliente, useCriarCliente } from './queries'
import type { ClienteRequest, ContatoDTO, EnderecoDTO, TelefoneDTO, TipoPessoa } from './types'

/** Campos com valor default sensato pra um cliente novo. */
const VALORES_PADRAO_NOVO: Partial<ClienteRequest> = {
  tipoPessoa: 'FISICA',
  situacao: 'ATIVO',
  fidelidadeAviso: 'QUANTIDADE',
}

/**
 * Formulário de Cliente — cria (`/clientes/novo`) e edita (`/clientes/:id`) com o
 * mesmo componente. Um único `Form` (Ant Design) cobre todas as abas (Tabs não
 * desmonta painel inativo por padrão, então os valores sobrevivem à troca de
 * aba) — só os campos escalares (nome, documento, comercial...) vivem no Form;
 * as 3 listas filhas (endereços/contatos/telefones) são estado próprio, editadas
 * via modal, e só se juntam ao resto no submit (ver `montarRequest`).
 * `tipoPessoa` também é estado próprio (não campo do Form) porque decide
 * renderização condicional e não pode mudar depois de criado (regra do
 * backend) — trocado via `Segmented` no cabeçalho, desabilitado em modo edição.
 */
export default function ClienteFormPage() {
  const { id } = useParams<{ id: string }>()
  const clienteId = id ? Number(id) : undefined
  const modoEdicao = clienteId !== undefined
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<ClienteRequest>()

  const { data: cliente, isLoading: carregando } = useCliente(clienteId)
  const criar = useCriarCliente()
  const atualizar = useAtualizarCliente(clienteId ?? -1)
  const salvando = criar.isPending || atualizar.isPending

  // RBAC granular (achado registrado no CLAUDE.md, item 2 das pendências da
  // Entrega 2): a rota inteira já é protegida por RequireAuthority (nível
  // grosso, "CONSULTAR" de algum módulo de Cadastro Base) — aqui refinamos
  // pra CRIAR/EDITAR especificamente, já que o backend distingue as duas
  // (ClienteController: @PreAuthorize("hasAuthority('CADASTRO_CLIENTE_CRIAR')")
  // no POST, EDITAR no PUT). Usuário só-consulta vê a tela em modo leitura,
  // não um botão habilitado que renderia 403 ao clicar.
  const { authorities } = useAuth()
  const podeSalvar = hasAuthority(authorities, modoEdicao ? 'CADASTRO_CLIENTE_EDITAR' : 'CADASTRO_CLIENTE_CRIAR')

  const [tipoPessoa, setTipoPessoa] = useState<TipoPessoa>('FISICA')
  const [enderecos, setEnderecos] = useState<EnderecoDTO[]>([])
  const [contatos, setContatos] = useState<ContatoDTO[]>([])
  const [telefones, setTelefones] = useState<TelefoneDTO[]>([])

  // Hidratação legítima do Form (imperativo, form.setFieldsValue) e das 3 listas
  // filhas — só podem ser populados depois que o TanStack Query resolve o fetch,
  // não dá pra derivar isso durante o render.
  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (!cliente) return
    setTipoPessoa(cliente.tipoPessoa)
    setEnderecos(cliente.enderecos)
    setContatos(cliente.contatos)
    setTelefones(cliente.telefones)
    form.setFieldsValue({
      ...cliente,
      cpf: cliente.tipoPessoa === 'FISICA' ? cliente.documento : undefined,
      cnpj: cliente.tipoPessoa === 'JURIDICA' ? cliente.documento : undefined,
      // @ts-expect-error DatePicker consome dayjs, não a string ISO do backend
      dataNascimento: cliente.dataNascimento ? dayjs(cliente.dataNascimento) : undefined,
    })
  }, [cliente, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    const request: ClienteRequest = {
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
        message.success('Cliente atualizado com sucesso.')
      } else {
        const criado = await criar.mutateAsync(request)
        message.success('Cliente criado com sucesso.')
        navigate(`/cadastro-base/clientes/${criado.id}`, { replace: true })
        return
      }
    } catch (erro) {
      const mensagem = erro instanceof Error ? erro.message : 'Falha ao salvar cliente.'
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
          { title: <a onClick={() => navigate('/cadastro-base/clientes')}>Clientes</a> },
          { title: modoEdicao ? cliente?.nome : 'Novo cliente' },
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
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/cadastro-base/clientes')}>
                Cancelar
              </Button>
              <Tooltip title={podeSalvar ? undefined : 'Você não tem permissão para salvar alterações em clientes'}>
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
                key: 'comercial',
                label: 'Comercial & Fidelidade',
                children: <ComercialTab form={form} />,
              },
            ]}
          />
        </Form>
      </Card>
    </Flex>
  )
}
