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
import DocumentosEntidadeSection from '@shared/ui/documentos/DocumentosEntidadeSection'
import DadosGeraisTab from './tabs/DadosGeraisTab'
import ComprasTab from './tabs/ComprasTab'
import { useAtualizarFornecedor, useCriarFornecedor, useFornecedor } from './queries'
import type { ContatoDTO, EnderecoDTO, FornecedorRequest, TelefoneDTO, TipoPessoa } from './types'

/** Campos com valor default sensato pra um fornecedor novo. */
const VALORES_PADRAO_NOVO: Partial<FornecedorRequest> = {
  tipoPessoa: 'FISICA',
  situacao: 'ATIVO',
}

/**
 * Formulário de Fornecedor — mesmo molde de `ClienteFormPage` (a mesma
 * estrutura Pessoa física/jurídica + 3 listas filhas), reaproveitando as tabs
 * de Endereços/Contatos/Telefones de `@shared/ui/pessoa` (extraídas de
 * `features/clientes` quando esta tela precisou exatamente da mesma coisa,
 * 2026-09-01). Diferenças reais: sem `url` (Dados Gerais própria), sem
 * comercial/fidelidade — aba "Compras" no lugar (`fornecedorDesde`/
 * `prazoEntregaDias`/`observacao`, campos que só existem em `FornecedorRequest`).
 */
export default function FornecedorFormPage() {
  const { id } = useParams<{ id: string }>()
  const fornecedorId = id ? Number(id) : undefined
  const modoEdicao = fornecedorId !== undefined
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<FornecedorRequest>()

  const { data: fornecedor, isLoading: carregando } = useFornecedor(fornecedorId)
  const criar = useCriarFornecedor()
  const atualizar = useAtualizarFornecedor(fornecedorId ?? -1)
  const salvando = criar.isPending || atualizar.isPending

  // RBAC granular — mesmo padrão de ClienteFormPage.
  const { authorities } = useAuth()
  const podeSalvar = hasAuthority(authorities, modoEdicao ? 'CADASTRO_FORNECEDOR_EDITAR' : 'CADASTRO_FORNECEDOR_CRIAR')

  const [tipoPessoa, setTipoPessoa] = useState<TipoPessoa>('FISICA')
  const [enderecos, setEnderecos] = useState<EnderecoDTO[]>([])
  const [contatos, setContatos] = useState<ContatoDTO[]>([])
  const [telefones, setTelefones] = useState<TelefoneDTO[]>([])

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (!fornecedor) return
    setTipoPessoa(fornecedor.tipoPessoa)
    setEnderecos(fornecedor.enderecos)
    setContatos(fornecedor.contatos)
    setTelefones(fornecedor.telefones)
    form.setFieldsValue({
      ...fornecedor,
      cpf: fornecedor.tipoPessoa === 'FISICA' ? fornecedor.documento : undefined,
      cnpj: fornecedor.tipoPessoa === 'JURIDICA' ? fornecedor.documento : undefined,
      // @ts-expect-error DatePicker consome dayjs, não a string ISO do backend
      dataNascimento: fornecedor.dataNascimento ? dayjs(fornecedor.dataNascimento) : undefined,
      // @ts-expect-error idem
      fornecedorDesde: fornecedor.fornecedorDesde ? dayjs(fornecedor.fornecedorDesde) : undefined,
    })
  }, [fornecedor, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    const request: FornecedorRequest = {
      ...valores,
      tipoPessoa,
      dataNascimento: valores.dataNascimento
        ? // @ts-expect-error valores.dataNascimento é um objeto dayjs em runtime
          valores.dataNascimento.format('YYYY-MM-DD')
        : undefined,
      fornecedorDesde: valores.fornecedorDesde
        ? // @ts-expect-error idem
          valores.fornecedorDesde.format('YYYY-MM-DD')
        : undefined,
      enderecos,
      contatos,
      telefones,
    }

    try {
      if (modoEdicao) {
        await atualizar.mutateAsync(request)
        message.success('Fornecedor atualizado com sucesso.')
      } else {
        const criado = await criar.mutateAsync(request)
        message.success('Fornecedor criado com sucesso.')
        navigate(`/cadastro-base/fornecedores/${criado.id}`, { replace: true })
        return
      }
    } catch (erro) {
      const mensagem = erro instanceof Error ? erro.message : 'Falha ao salvar fornecedor.'
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
          { title: <a onClick={() => navigate('/cadastro-base/fornecedores')}>Fornecedores</a> },
          { title: modoEdicao ? fornecedor?.nome : 'Novo fornecedor' },
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
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/cadastro-base/fornecedores')}>
                Cancelar
              </Button>
              <Tooltip title={podeSalvar ? undefined : 'Você não tem permissão para salvar alterações em fornecedores'}>
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
                key: 'compras',
                label: 'Compras',
                children: <ComprasTab />,
              },
              {
                key: 'documentos',
                label: 'Documentos',
                children: <DocumentosEntidadeSection entidadeTipo="FORNECEDOR" entidadeId={fornecedorId} />,
              },
            ]}
          />
        </Form>
      </Card>
    </Flex>
  )
}
