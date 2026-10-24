import { useState } from 'react'
import { Card, Flex, Select, Table, Typography } from 'antd'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import AsyncSelect from '@shared/ui/AsyncSelect'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import ExportarRelatorioButtons from '@shared/ui/ExportarRelatorioButtons'
import { exportarSaldoEstoque } from './api'
import { useBuscaProdutosParaSelecao, useDepositosAtivos, useSaldos } from './queries'
import type { SaldoEstoqueResponse, SaldosFiltro } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

/** Só leitura — saldo muda exclusivamente via lançamento de movimento (aba Movimentos). */
export default function SaldosListPage() {
  const { empresaAtivaId } = useAuth()
  const [filtro, setFiltro] = useState<SaldosFiltro>({})
  const [pagina, setPagina] = useState(0)
  const [termoProduto, setTermoProduto] = useState<string | undefined>(undefined)

  const { data: depositos } = useDepositosAtivos(empresaAtivaId ?? undefined)
  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termoProduto)
  const { data, isLoading, isPlaceholderData } = useSaldos(filtro, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Saldos de Estoque
      </Title>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <AsyncSelect
            allowClear
            style={{ width: 320 }}
            opcoes={produtos}
            carregando={carregandoProdutos}
            termo={termoProduto}
            onTermoChange={setTermoProduto}
            toOption={(p) => ({ value: p.id, label: `${p.codigoInterno} — ${p.nome}` })}
            onChange={(produtoId: number | undefined) => {
              setFiltro((f) => ({ ...f, produtoId }))
              setPagina(0)
            }}
          />
          <Select
            allowClear
            style={{ width: 240 }}
            placeholder="Filtrar por depósito"
            options={depositos?.map((d) => ({ value: d.id, label: d.nome }))}
            onChange={(depositoId: number | undefined) => {
              setFiltro((f) => ({ ...f, depositoId }))
              setPagina(0)
            }}
          />
          <ExportarRelatorioButtons
            onExportar={(formato) => exportarSaldoEstoque(filtro.produtoId, filtro.depositoId, formato)}
          />
        </Flex>

        <Table<SaldoEstoqueResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} saldo(s)`,
          }}
          columns={[
            { title: 'Produto', key: 'produto', render: (_, saldo) => <ProdutoResumoCell produtoId={saldo.produtoId} /> },
            { title: 'Depósito', dataIndex: 'depositoNome' },
            { title: 'Quantidade', dataIndex: 'quantidade', align: 'right' },
            { title: 'Reservada', dataIndex: 'quantidadeReservada', align: 'right' },
            { title: 'Disponível', dataIndex: 'quantidadeDisponivel', align: 'right' },
            {
              title: 'Custo médio',
              dataIndex: 'custoMedioPonderado',
              align: 'right',
              render: (v: number) => `R$ ${v.toFixed(2)}`,
            },
            {
              title: 'Última movimentação',
              dataIndex: 'dataUltimaMovimentacao',
              render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY HH:mm') : '—'),
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
