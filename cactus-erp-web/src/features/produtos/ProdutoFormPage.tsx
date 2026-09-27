import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Flex, Form, Skeleton, Space, Switch, Tabs, Tooltip } from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import DocumentosEntidadeSection from '@shared/ui/documentos/DocumentosEntidadeSection'
import DadosGeraisTab from './tabs/DadosGeraisTab'
import PrecosEstoqueTab from './tabs/PrecosEstoqueTab'
import TributacaoTab from './tabs/TributacaoTab'
import FaixasPrecoTab from './tabs/FaixasPrecoTab'
import ImagensTab from './tabs/ImagensTab'
import { useAtualizarProduto, useCriarProduto, useProduto } from './queries'
import type { ProdutoFaixaPrecoDTO, ProdutoImagemDTO, ProdutoRequest } from './types'

const VALORES_PADRAO_NOVO: Partial<ProdutoRequest> = { situacao: 'ATIVO' }

/**
 * Formulário de Produto — mesmo molde de Cliente/Fornecedor (um único `Form`
 * cobrindo Dados Gerais/Preços/Tributação, mais duas listas filhas em estado
 * local — faixas de preço e imagens — mescladas só no submit, mesmo padrão de
 * endereços/contatos/telefones). Diferente deles: a aba Tributação expõe uma
 * ação (`Recalcular tributação padrão`) que só existe depois que o produto já
 * tem id — mesmo gate já usado nas sub-abas de Empresa, mas sem exigir um
 * `<Form>` próprio (o recálculo abre um modal separado, não edita o Form
 * principal).
 */
export default function ProdutoFormPage() {
  const { id } = useParams<{ id: string }>()
  const produtoId = id ? Number(id) : undefined
  const modoEdicao = produtoId !== undefined
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<ProdutoRequest>()

  const { data: produto, isLoading: carregando } = useProduto(produtoId)
  const criar = useCriarProduto()
  const atualizar = useAtualizarProduto(produtoId ?? -1)
  const salvando = criar.isPending || atualizar.isPending

  const { authorities } = useAuth()
  const podeSalvar = hasAuthority(authorities, modoEdicao ? 'CADASTRO_PRODUTO_EDITAR' : 'CADASTRO_PRODUTO_CRIAR')

  const [faixasPreco, setFaixasPreco] = useState<ProdutoFaixaPrecoDTO[]>([])
  const [imagens, setImagens] = useState<ProdutoImagemDTO[]>([])

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (!produto) return
    setFaixasPreco(produto.faixasPreco)
    setImagens(produto.imagens)
    form.setFieldsValue(produto)
  }, [produto, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    const request: ProdutoRequest = { ...valores, faixasPreco, imagens }

    try {
      if (modoEdicao) {
        await atualizar.mutateAsync(request)
        message.success('Produto atualizado com sucesso.')
      } else {
        const criado = await criar.mutateAsync(request)
        message.success('Produto criado com sucesso.')
        navigate(`/cadastro-base/produtos/${criado.id}`, { replace: true })
        return
      }
    } catch (erro) {
      const mensagem = erro instanceof Error ? erro.message : 'Falha ao salvar produto.'
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
          { title: <a onClick={() => navigate('/cadastro-base/produtos')}>Produtos</a> },
          { title: modoEdicao ? produto?.nome : 'Novo produto' },
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
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/cadastro-base/produtos')}>
                Cancelar
              </Button>
              <Tooltip title={podeSalvar ? undefined : 'Você não tem permissão para salvar alterações em produtos'}>
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
                children: <DadosGeraisTab produtoGrupoIdInicial={produto?.produtoGrupoId} />,
              },
              {
                key: 'precos-estoque',
                label: 'Preços & Estoque',
                children: <PrecosEstoqueTab />,
              },
              {
                key: 'tributacao',
                label: 'Tributação',
                children: <TributacaoTab produto={produto} />,
              },
              {
                key: 'faixas-preco',
                label: `Faixas de Preço / Promoções (${faixasPreco.length})`,
                children: <FaixasPrecoTab faixas={faixasPreco} onChange={setFaixasPreco} somenteLeitura={!podeSalvar} />,
              },
              {
                key: 'imagens',
                label: `Imagens (${imagens.length})`,
                children: <ImagensTab imagens={imagens} onChange={setImagens} somenteLeitura={!podeSalvar} />,
              },
              {
                key: 'documentos',
                label: 'Documentos',
                children: <DocumentosEntidadeSection entidadeTipo="PRODUTO" entidadeId={produtoId} />,
              },
            ]}
          />
        </Form>
      </Card>
    </Flex>
  )
}
