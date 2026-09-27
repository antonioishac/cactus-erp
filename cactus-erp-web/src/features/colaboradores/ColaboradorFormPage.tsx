import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Flex, Form, Segmented, Skeleton, Space, Switch, Tabs, Tooltip } from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import dayjs, { type Dayjs } from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import EnderecosTab from '@shared/ui/pessoa/EnderecosTab'
import ContatosTab from '@shared/ui/pessoa/ContatosTab'
import TelefonesTab from '@shared/ui/pessoa/TelefonesTab'
import DadosGeraisTab from './tabs/DadosGeraisTab'
import RhTab from './tabs/RhTab'
import ComissaoTab from './tabs/ComissaoTab'
import { useAtualizarColaborador, useColaborador, useCriarColaborador } from './queries'
import type { ColaboradorRequest, ContatoDTO, EnderecoDTO, TelefoneDTO, TipoPessoa } from './types'

const VALORES_PADRAO_NOVO: Partial<ColaboradorRequest> = {
  tipoPessoa: 'FISICA',
  situacao: 'ATIVO',
}

/** Mesmo molde de `ClienteFormPage`/`FornecedorFormPage` — datas extras (admissão/
 *  demissão/CTPS expedição) convertidas dayjs↔ISO do mesmo jeito que dataNascimento. */
export default function ColaboradorFormPage() {
  const { id } = useParams<{ id: string }>()
  const colaboradorId = id ? Number(id) : undefined
  const modoEdicao = colaboradorId !== undefined
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<ColaboradorRequest>()

  const { data: colaborador, isLoading: carregando } = useColaborador(colaboradorId)
  const criar = useCriarColaborador()
  const atualizar = useAtualizarColaborador(colaboradorId ?? -1)
  const salvando = criar.isPending || atualizar.isPending

  const { authorities } = useAuth()
  const podeSalvar = hasAuthority(
    authorities,
    modoEdicao ? 'CADASTRO_COLABORADOR_EDITAR' : 'CADASTRO_COLABORADOR_CRIAR',
  )

  const [tipoPessoa, setTipoPessoa] = useState<TipoPessoa>('FISICA')
  const [enderecos, setEnderecos] = useState<EnderecoDTO[]>([])
  const [contatos, setContatos] = useState<ContatoDTO[]>([])
  const [telefones, setTelefones] = useState<TelefoneDTO[]>([])

  const CAMPOS_DATA = ['dataNascimento', 'dataAdmissao', 'dataDemissao', 'ctpsDataExpedicao'] as const

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (!colaborador) return
    setTipoPessoa(colaborador.tipoPessoa)
    setEnderecos(colaborador.enderecos)
    setContatos(colaborador.contatos)
    setTelefones(colaborador.telefones)
    // DatePicker consome dayjs, não a string ISO do backend — Record<string, unknown>
    // porque o valor real (objeto dayjs) diverge do tipo declarado em ColaboradorRequest
    // (string) só nesta ponte de hidratação do form.
    const datas: Record<string, unknown> = Object.fromEntries(
      CAMPOS_DATA.map((campo) => [campo, colaborador[campo] ? dayjs(colaborador[campo]) : undefined]),
    )
    form.setFieldsValue({
      ...colaborador,
      cpf: colaborador.tipoPessoa === 'FISICA' ? colaborador.documento : undefined,
      cnpj: colaborador.tipoPessoa === 'JURIDICA' ? colaborador.documento : undefined,
      ...datas,
    })
    // eslint-disable-next-line react-hooks/exhaustive-deps -- CAMPOS_DATA é constante, não precisa entrar nas deps
  }, [colaborador, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    // valores[campo] é um objeto dayjs em runtime (o Form ainda não passou pelo
    // formato ISO — só formatamos aqui, na saída), não a string do tipo declarado.
    const valoresComoRegistro = valores as unknown as Record<string, Dayjs | undefined>
    const datasFormatadas = Object.fromEntries(
      CAMPOS_DATA.map((campo) => {
        const valor = valoresComoRegistro[campo]
        return [campo, valor ? valor.format('YYYY-MM-DD') : undefined]
      }),
    )
    const request: ColaboradorRequest = {
      ...valores,
      ...datasFormatadas,
      tipoPessoa,
      enderecos,
      contatos,
      telefones,
    }

    try {
      if (modoEdicao) {
        await atualizar.mutateAsync(request)
        message.success('Colaborador atualizado com sucesso.')
      } else {
        const criado = await criar.mutateAsync(request)
        message.success('Colaborador criado com sucesso.')
        navigate(`/cadastro-base/colaboradores/${criado.id}`, { replace: true })
        return
      }
    } catch (erro) {
      const mensagem = erro instanceof Error ? erro.message : 'Falha ao salvar colaborador.'
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
          { title: <a onClick={() => navigate('/cadastro-base/colaboradores')}>Colaboradores</a> },
          { title: modoEdicao ? colaborador?.nome : 'Novo colaborador' },
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
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/cadastro-base/colaboradores')}>
                Cancelar
              </Button>
              <Tooltip title={podeSalvar ? undefined : 'Você não tem permissão para salvar alterações em colaboradores'}>
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
                key: 'rh',
                label: 'RH',
                children: <RhTab />,
              },
              {
                key: 'comissao',
                label: 'Comissão & Entrega',
                children: <ComissaoTab />,
              },
            ]}
          />
        </Form>
      </Card>
    </Flex>
  )
}
