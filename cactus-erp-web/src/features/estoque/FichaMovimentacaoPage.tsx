import { useState } from 'react'
import { Card, Flex, Input, Select, Table, Tag, Typography } from 'antd'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import AsyncSelect from '@shared/ui/AsyncSelect'
import ExportarRelatorioButtons from '@shared/ui/ExportarRelatorioButtons'
import { exportarFichaMovimentacao } from './api'
import { useBuscaProdutosParaSelecao, useDepositosAtivos, useFichaMovimentacao } from './queries'
import { ORIGEM_MOVIMENTO_LABEL, TIPO_MOVIMENTO_LABEL } from './types'
import type { EstoqueMovimentoResponse } from './types'

const { Title } = Typography

const COR_TIPO: Record<string, string> = {
  ENTRADA: 'success',
  DEVOLUCAO: 'success',
  TRANSFERENCIA_ENTRADA: 'success',
  SAIDA: 'error',
  PERDA: 'error',
  TRANSFERENCIA_SAIDA: 'error',
  AJUSTE: 'gold',
}

/** Ledger cronológico de um único produto no período — diferente de `MovimentosListPage`
 * (que lista qualquer movimento, sem exigir produto nem período). */
export default function FichaMovimentacaoPage() {
  const { empresaAtivaId } = useAuth()
  const [produtoId, setProdutoId] = useState<number | undefined>(undefined)
  const [termoProduto, setTermoProduto] = useState<string | undefined>(undefined)
  const [depositoId, setDepositoId] = useState<number | undefined>(undefined)
  const [dataInicio, setDataInicio] = useState('')
  const [dataFim, setDataFim] = useState('')

  const { data: depositos } = useDepositosAtivos(empresaAtivaId ?? undefined)
  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termoProduto)
  const { data, isLoading } = useFichaMovimentacao(produtoId, dataInicio || undefined, dataFim || undefined, depositoId)
  const movimentos = data ?? []
  const filtroCompleto = !!produtoId && !!dataInicio && !!dataFim

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Ficha de Movimentação de Produto
      </Title>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <AsyncSelect
            style={{ width: 320 }}
            opcoes={produtos}
            carregando={carregandoProdutos}
            termo={termoProduto}
            onTermoChange={setTermoProduto}
            toOption={(p) => ({ value: p.id, label: `${p.codigoInterno} — ${p.nome}` })}
            onChange={(v: number | undefined) => setProdutoId(v)}
            placeholder="Selecione o produto"
          />
          <Select
            allowClear
            style={{ width: 240 }}
            placeholder="Filtrar por depósito"
            options={depositos?.map((d) => ({ value: d.id, label: d.nome }))}
            onChange={(v: number | undefined) => setDepositoId(v)}
          />
          <span>Período</span>
          <Input type="date" value={dataInicio} onChange={(e) => setDataInicio(e.target.value)} style={{ width: 160 }} />
          <span>até</span>
          <Input type="date" value={dataFim} onChange={(e) => setDataFim(e.target.value)} style={{ width: 160 }} />
          <ExportarRelatorioButtons
            disabled={!filtroCompleto}
            motivoDesabilitado="Selecione o produto e o período"
            onExportar={(formato) => exportarFichaMovimentacao(produtoId as number, dataInicio, dataFim, depositoId, formato)}
          />
        </Flex>

        <Table<EstoqueMovimentoResponse>
          rowKey="id"
          loading={isLoading}
          dataSource={movimentos}
          pagination={false}
          locale={{ emptyText: 'Selecione o produto e o período pra consultar a ficha de movimentação' }}
          columns={[
            { title: 'Data', dataIndex: 'dataMovimento', render: (v: string) => dayjs(v).format('DD/MM/YYYY HH:mm') },
            { title: 'Depósito', dataIndex: 'depositoId', render: (v: number) => `#${v}` },
            {
              title: 'Tipo',
              dataIndex: 'tipoMovimento',
              render: (tipo: keyof typeof TIPO_MOVIMENTO_LABEL) => <Tag color={COR_TIPO[tipo]}>{TIPO_MOVIMENTO_LABEL[tipo]}</Tag>,
            },
            { title: 'Quantidade', dataIndex: 'quantidade', align: 'right' },
            { title: 'Anterior → Nova', key: 'saldo', render: (_, m) => `${m.quantidadeAnterior} → ${m.quantidadeNova}` },
            {
              title: 'Origem',
              dataIndex: 'origemTipo',
              render: (origem: keyof typeof ORIGEM_MOVIMENTO_LABEL) => ORIGEM_MOVIMENTO_LABEL[origem],
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
