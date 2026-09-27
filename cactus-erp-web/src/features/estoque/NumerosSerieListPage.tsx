import { useState } from 'react'
import { App, Button, Card, Empty, Flex, Select, Table, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import AsyncSelect from '@shared/ui/AsyncSelect'
import NumeroSerieModal from './modals/NumeroSerieModal'
import { useAtualizarSituacaoNumeroSerie, useBuscaProdutosParaSelecao, useNumerosSerie } from './queries'
import { SITUACAO_NUMERO_SERIE_LABEL } from './types'
import type { NumeroSerieResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

/** Rastreio unitário — `listarPorProduto` exige produto selecionado (ver `NumeroSerieController`), diferente de Lotes onde o filtro é opcional. */
export default function NumerosSerieListPage() {
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'ESTOQUE_GERENCIAR')
  const podeMovimentar = hasAuthority(authorities, 'ESTOQUE_MOVIMENTAR')
  const [termoProduto, setTermoProduto] = useState<string | undefined>(undefined)
  const [produtoId, setProdutoId] = useState<number | undefined>(undefined)
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)

  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termoProduto)
  const { data, isLoading, isPlaceholderData } = useNumerosSerie(produtoId, pagina, TAMANHO_PAGINA)
  const atualizarSituacao = useAtualizarSituacaoNumeroSerie()

  async function handleMudarSituacao(numeroSerie: NumeroSerieResponse, situacao: NumeroSerieResponse['situacao']) {
    try {
      await atualizarSituacao.mutateAsync({ id: numeroSerie.id, situacao, depositoId: numeroSerie.depositoId })
      message.success('Situação atualizada.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao atualizar situação.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Números de Série
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar números de série'}>
          <Button type="primary" icon={<PlusOutlined />} disabled={!podeGerenciar} onClick={() => setModalAberto(true)}>
            Cadastrar Número de Série
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <AsyncSelect
            id="filtroProdutoId"
            style={{ width: 320 }}
            opcoes={produtos}
            carregando={carregandoProdutos}
            termo={termoProduto}
            onTermoChange={setTermoProduto}
            toOption={(p) => ({ value: p.id, label: `${p.codigoInterno} — ${p.nome}` })}
            onChange={(valor: number | undefined) => {
              setProdutoId(valor)
              setPagina(0)
            }}
          />
        </Flex>

        {produtoId === undefined ? (
          <Empty description="Selecione um produto pra ver os números de série cadastrados" />
        ) : (
          <Table<NumeroSerieResponse>
            rowKey="id"
            loading={isLoading || isPlaceholderData}
            dataSource={data?.conteudo ?? []}
            pagination={{
              current: pagina + 1,
              pageSize: TAMANHO_PAGINA,
              total: data?.totalRegistros ?? 0,
              onChange: (novaPagina) => setPagina(novaPagina - 1),
              showTotal: (total) => `${total} número(s) de série`,
            }}
            columns={[
              { title: 'Número de série', dataIndex: 'numeroSerie' },
              { title: 'Depósito', dataIndex: 'depositoNome', render: (v) => v || '—' },
              {
                title: 'Situação',
                key: 'situacao',
                render: (_, ns) => (
                  <Select<NumeroSerieResponse['situacao']>
                    size="small"
                    style={{ width: 160 }}
                    disabled={!podeMovimentar}
                    value={ns.situacao}
                    options={Object.entries(SITUACAO_NUMERO_SERIE_LABEL).map(([value, label]) => ({ value, label }))}
                    onChange={(situacao) => handleMudarSituacao(ns, situacao)}
                  />
                ),
              },
            ]}
          />
        )}
      </Card>

      <NumeroSerieModal open={modalAberto} produtoIdFixo={produtoId} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
