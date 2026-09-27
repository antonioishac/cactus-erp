import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Divider, Flex, Form, Input, Select, Skeleton, Space, Switch, Tooltip, Typography } from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import ItensTabelaPrecoTab from './ItensTabelaPrecoTab'
import { useAtualizarTabelaPreco, useCriarTabelaPreco, useTabelaPreco } from './queries'
import { TIPO_AJUSTE_PRECO_LABEL } from './types'
import type { TabelaPrecoProdutoDTO, TabelaPrecoRequest, TipoAjustePreco } from './types'

const { Title } = Typography

const VALORES_PADRAO_NOVO: Partial<TabelaPrecoRequest> = { tipoAjuste: 'VALOR_FIXO', situacao: 'ATIVO' }

/**
 * Diferente de `ProdutoFaixaPreco` (desconto/promoção por quantidade dentro
 * do próprio produto): aqui a TABELA é dona da lista de produtos — pensada
 * pra segmentar preço por canal/perfil (Varejo, Atacado, Cliente VIP), não
 * por quantidade levada. Ver seção "PRODUTO_FAIXA_PRECO é UM conceito só" no
 * CLAUDE.md raiz pra não confundir os dois mecanismos.
 */
export default function TabelaPrecoFormPage() {
  const { id } = useParams<{ id: string }>()
  const tabelaId = id ? Number(id) : undefined
  const modoEdicao = tabelaId !== undefined
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<TabelaPrecoRequest>()

  const { data: tabela, isLoading: carregando } = useTabelaPreco(tabelaId)
  const criar = useCriarTabelaPreco()
  const atualizar = useAtualizarTabelaPreco(tabelaId ?? -1)
  const salvando = criar.isPending || atualizar.isPending

  const { authorities } = useAuth()
  const podeSalvar = hasAuthority(authorities, modoEdicao ? 'CADASTRO_PRODUTO_EDITAR' : 'CADASTRO_PRODUTO_CRIAR')

  const [tipoAjuste, setTipoAjuste] = useState<TipoAjustePreco>('VALOR_FIXO')
  const [itens, setItens] = useState<TabelaPrecoProdutoDTO[]>([])

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (!tabela) return
    setTipoAjuste(tabela.tipoAjuste)
    setItens(tabela.produtos)
    form.setFieldsValue(tabela)
  }, [tabela, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    const request: TabelaPrecoRequest = { ...valores, produtos: itens }

    try {
      if (modoEdicao) {
        await atualizar.mutateAsync(request)
        message.success('Tabela de preço atualizada com sucesso.')
      } else {
        const criada = await criar.mutateAsync(request)
        message.success('Tabela de preço criada com sucesso.')
        navigate(`/cadastro-base/tabelas-preco/${criada.id}`, { replace: true })
        return
      }
    } catch (erro) {
      const mensagem = erro instanceof Error ? erro.message : 'Falha ao salvar tabela de preço.'
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
          { title: <a onClick={() => navigate('/cadastro-base/tabelas-preco')}>Tabelas de Preço</a> },
          { title: modoEdicao ? tabela?.nome : 'Nova tabela de preço' },
        ]}
      />

      <Card>
        <Form form={form} layout="vertical" initialValues={VALORES_PADRAO_NOVO} disabled={!podeSalvar}>
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
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/cadastro-base/tabelas-preco')}>
                Cancelar
              </Button>
              <Tooltip title={podeSalvar ? undefined : 'Você não tem permissão para salvar alterações em tabelas de preço'}>
                <Button type="primary" icon={<SaveOutlined />} loading={salvando} disabled={!podeSalvar} onClick={handleSalvar}>
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          <Flex gap={12}>
            <Form.Item name="nome" label="Nome" style={{ flex: 1 }} rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]}>
              <Input placeholder="Ex: Atacado, Cliente VIP..." />
            </Form.Item>
            <Form.Item
              name="tipoAjuste"
              label="Tipo de ajuste"
              style={{ flex: 1 }}
              rules={[{ required: true }]}
              tooltip="Vale pra todos os produtos desta tabela — trocar depois de já ter produtos cadastrados não converte os valores existentes."
            >
              <Select
                options={Object.entries(TIPO_AJUSTE_PRECO_LABEL).map(([value, label]) => ({ value, label }))}
                onChange={setTipoAjuste}
              />
            </Form.Item>
          </Flex>
          <Form.Item name="descricao" label="Descrição" rules={[{ max: 250 }]}>
            <Input />
          </Form.Item>
        </Form>
      </Card>

      <Card>
        <Title level={5} style={{ marginTop: 0 }}>
          Produtos desta tabela
        </Title>
        <Divider style={{ marginTop: 8 }} />
        <ItensTabelaPrecoTab itens={itens} tipoAjuste={tipoAjuste} onChange={setItens} somenteLeitura={!podeSalvar} />
      </Card>
    </Flex>
  )
}
