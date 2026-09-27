import { useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Flex, Form, Skeleton, Space, Switch, Tabs, Tooltip } from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import DadosGeraisTab from './tabs/DadosGeraisTab'
import EnderecoTab from './tabs/EnderecoTab'
import DadosLegaisTab from './tabs/DadosLegaisTab'
import RegimeTributarioTab from './tabs/RegimeTributarioTab'
import ConfiguracaoOperacionalTab from './tabs/ConfiguracaoOperacionalTab'
import ConfiguracaoFiscalTab from './tabs/ConfiguracaoFiscalTab'
import AbaBloqueada from './AbaBloqueada'
import { useAtualizarEmpresa, useCriarEmpresa, useEmpresa } from './queries'
import type { EmpresaRequest } from './types'

const VALORES_PADRAO_NOVO: Partial<EmpresaRequest> = {
  tipoEstabelecimento: 'VAREJO',
  situacao: 'ATIVO',
}

/**
 * Formulário de Empresa — bem diferente do molde de Cliente/Fornecedor/
 * Colaborador (não é especialização de Pessoa): Regime Tributário/
 * Configuração Operacional/Configuração Fiscal são SUB-RECURSOS com endpoint
 * próprio (`/api/v1/empresas/{id}/...`), não campos do `EmpresaRequest` — só
 * existem depois que a empresa tem um `id` de verdade. Por isso:
 * - O "Salvar" do cabeçalho só grava Dados Gerais/Endereço/Dados Legais
 *   (o `EmpresaRequest` propriamente dito).
 * - As 3 abas de sub-recurso ficam **desabilitadas** enquanto a empresa ainda
 *   não foi criada (`disabled` no item do `Tabs`, com tooltip explicando) —
 *   cada uma tem seu próprio botão de salvar, chamando seu próprio endpoint.
 */
export default function EmpresaFormPage() {
  const { id } = useParams<{ id: string }>()
  const empresaId = id ? Number(id) : undefined
  const modoEdicao = empresaId !== undefined
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<EmpresaRequest>()

  const { data: empresa, isLoading: carregando } = useEmpresa(empresaId)
  const criar = useCriarEmpresa()
  const atualizar = useAtualizarEmpresa(empresaId ?? -1)
  const salvando = criar.isPending || atualizar.isPending

  const { authorities } = useAuth()
  const podeSalvar = hasAuthority(authorities, modoEdicao ? 'CADASTRO_EMPRESA_EDITAR' : 'CADASTRO_EMPRESA_CRIAR')

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (!empresa) return
    form.setFieldsValue({
      ...empresa,
      // @ts-expect-error DatePicker consome dayjs, não a string ISO do backend
      dataConstituicao: empresa.dataConstituicao ? dayjs(empresa.dataConstituicao) : undefined,
      // @ts-expect-error idem
      dataInscricaoJuntaComercial: empresa.dataInscricaoJuntaComercial
        ? dayjs(empresa.dataInscricaoJuntaComercial)
        : undefined,
    })
  }, [empresa, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    const request: EmpresaRequest = {
      ...valores,
      dataConstituicao: valores.dataConstituicao
        ? // @ts-expect-error valores.dataConstituicao é um objeto dayjs em runtime
          valores.dataConstituicao.format('YYYY-MM-DD')
        : undefined,
      dataInscricaoJuntaComercial: valores.dataInscricaoJuntaComercial
        ? // @ts-expect-error idem
          valores.dataInscricaoJuntaComercial.format('YYYY-MM-DD')
        : undefined,
    }

    try {
      if (modoEdicao) {
        await atualizar.mutateAsync(request)
        message.success('Empresa atualizada com sucesso.')
      } else {
        const criada = await criar.mutateAsync(request)
        message.success('Empresa criada com sucesso. Agora você pode configurar regime tributário, operação e fiscal.')
        navigate(`/cadastro-base/empresas/${criada.id}`, { replace: true })
        return
      }
    } catch (erro) {
      const mensagem = erro instanceof Error ? erro.message : 'Falha ao salvar empresa.'
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

  const dicaAbaBloqueada = 'Salve os dados gerais da empresa primeiro'

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate('/cadastro-base/empresas')}>Empresas</a> },
          { title: modoEdicao ? empresa?.razaoSocial : 'Nova empresa' },
        ]}
      />

      <Card>
        {/* component={false}: não renderiza a tag HTML <form> — só o
            Provider/contexto que os Form.Item das 3 primeiras abas usam.
            Necessário porque as abas de sub-recurso (Configuração Operacional/
            Fiscal) têm seu PRÓPRIO <Form> com <form> real (chamam endpoints
            diferentes) — um <form> HTML dentro de outro é inválido e quebrava
            a hidratação (achado real ao vivo, 2026-09-01: React acusava
            "<form> cannot contain a nested form"). */}
        <Form form={form} layout="vertical" component={false} initialValues={VALORES_PADRAO_NOVO} disabled={!podeSalvar}>
          <Flex justify="flex-end" wrap="wrap" gap={16} style={{ marginBottom: 24 }}>
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
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/cadastro-base/empresas')}>
                Cancelar
              </Button>
              <Tooltip title={podeSalvar ? undefined : 'Você não tem permissão para salvar alterações em empresas'}>
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
                children: <DadosGeraisTab />,
              },
              {
                key: 'endereco',
                label: 'Endereço',
                children: <EnderecoTab />,
              },
              {
                key: 'dados-legais',
                label: 'Dados Legais',
                children: <DadosLegaisTab />,
              },
              {
                key: 'regime-tributario',
                label: 'Regime Tributário',
                disabled: !modoEdicao,
                children: empresaId !== undefined ? <RegimeTributarioTab empresaId={empresaId} /> : <AbaBloqueada />,
              },
              {
                key: 'configuracao-operacional',
                label: 'Configuração Operacional',
                disabled: !modoEdicao,
                children:
                  empresaId !== undefined ? <ConfiguracaoOperacionalTab empresaId={empresaId} /> : <AbaBloqueada />,
              },
              {
                key: 'configuracao-fiscal',
                label: 'Configuração Fiscal',
                disabled: !modoEdicao,
                children: empresaId !== undefined ? <ConfiguracaoFiscalTab empresaId={empresaId} /> : <AbaBloqueada />,
              },
            ].map((item) => (item.disabled ? { ...item, label: <Tooltip title={dicaAbaBloqueada}>{item.label}</Tooltip> } : item))}
          />
        </Form>
      </Card>
    </Flex>
  )
}
