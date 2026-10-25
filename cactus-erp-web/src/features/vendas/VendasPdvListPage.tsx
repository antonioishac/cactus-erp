import { useMemo, useState } from 'react'
import { Alert, Card, DatePicker, Descriptions, Flex, InputNumber, Space, Table, Tag, Typography } from 'antd'
import dayjs, { type Dayjs } from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import { ORIGEM_VENDA_PDV_LABEL, TPAG_LABEL, useVendasPdv } from './vendasPdv'
import type { VendaPdv, VendaPdvDevolucao, VendaPdvItem, VendaPdvPagamento, VendasPdvFiltro } from './vendasPdv'

const { Title, Text } = Typography
const TAMANHO_PAGINA = 10
const reais = (v?: number | null) => (v != null ? `R$ ${v.toFixed(2)}` : '—')

/**
 * Consulta das vendas de balcão vindas do cactus-pdv. Só leitura — a venda nasce no PDV. Itens, pagamentos
 * (fracionados) e devoluções ficam na linha expandida, sem rota de detalhe própria.
 */
export default function VendasPdvListPage() {
  const { empresaAtivaId } = useAuth()
  const [periodo, setPeriodo] = useState<[Dayjs, Dayjs]>([dayjs().startOf('month'), dayjs()])
  const [pdvCaixaId, setPdvCaixaId] = useState<number | null>(null)
  const [pagina, setPagina] = useState(0)

  const filtro = useMemo<VendasPdvFiltro | null>(
    () =>
      empresaAtivaId == null
        ? null
        : {
            empresaId: empresaAtivaId,
            dataInicio: periodo[0].format('YYYY-MM-DD'),
            dataFim: periodo[1].format('YYYY-MM-DD'),
            pdvCaixaId: pdvCaixaId ?? undefined,
          },
    [empresaAtivaId, periodo, pdvCaixaId],
  )
  const { data, isLoading, isPlaceholderData } = useVendasPdv(filtro, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Vendas do PDV
      </Title>
      <Alert
        type="info"
        showIcon
        title="Vendas de balcão sincronizadas do cactus-pdv"
        description="Só leitura. O estoque já é baixado pelo próprio PDV (movimentos de estoque), e só o dinheiro líquido (dinheiro − troco) entra no caixa do terminal."
      />
      <Card>
        <Space wrap style={{ marginBottom: 16 }}>
          <DatePicker.RangePicker
            value={periodo}
            format="DD/MM/YYYY"
            allowClear={false}
            onChange={(v) => {
              if (v?.[0] && v[1]) {
                setPeriodo([v[0], v[1]])
                setPagina(0)
              }
            }}
          />
          <InputNumber<number>
            placeholder="Terminal (PDV_CAIXA)"
            style={{ width: 200 }}
            min={1}
            value={pdvCaixaId}
            onChange={(v) => {
              setPdvCaixaId(v ?? null)
              setPagina(0)
            }}
          />
        </Space>
        <Table<VendaPdv>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          expandable={{ expandedRowRender: (venda) => <DetalheVenda venda={venda} /> }}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (p) => setPagina(p - 1),
            showTotal: (total) => `${total} venda(s)`,
          }}
          columns={[
            { title: 'Data/hora', dataIndex: 'dataHoraVenda', width: 150, render: (v: string) => dayjs(v).format('DD/MM/YYYY HH:mm') },
            { title: 'Terminal', key: 'terminal', render: (_, v) => v.pdvCaixaNome ?? `#${v.pdvCaixaId}` },
            { title: 'Origem', dataIndex: 'origemVenda', render: (v: VendaPdv['origemVenda']) => ORIGEM_VENDA_PDV_LABEL[v] },
            { title: 'Cliente', key: 'cliente', render: (_, v) => v.nomeCliente ?? (v.clienteId ? `#${v.clienteId}` : 'Consumidor') },
            {
              title: 'Pagamento',
              key: 'pagamento',
              render: (_, v) =>
                v.pagamentos.length === 0
                  ? '—'
                  : v.pagamentos.map((p) => <Tag key={p.id}>{TPAG_LABEL[p.codigoPagamentoNfce] ?? p.codigoPagamentoNfce}</Tag>),
            },
            { title: 'Valor final', dataIndex: 'valorFinal', align: 'right', width: 120, render: reais },
            { title: 'Dinheiro no caixa', dataIndex: 'valorDinheiroLiquido', align: 'right', width: 150, render: reais },
            {
              title: 'Situação',
              key: 'situacao',
              render: (_, v) => (
                <Space size={4}>
                  <Tag color={v.situacao === 'FECHADA' ? 'success' : 'default'}>{v.situacao === 'FECHADA' ? 'Fechada' : 'Cancelada'}</Tag>
                  {v.devolucoes.length > 0 && <Tag color="orange">Com devolução</Tag>}
                </Space>
              ),
            },
          ]}
        />
      </Card>
    </Flex>
  )
}

function DetalheVenda({ venda }: { venda: VendaPdv }) {
  return (
    <Flex vertical gap={12}>
      {venda.dataHoraCancelamento && (
        <Alert
          type="warning"
          showIcon
          title={`Cancelada depois de fechada em ${dayjs(venda.dataHoraCancelamento).format('DD/MM/YYYY HH:mm')}`}
          description={
            <>
              {venda.justificativaCancelamento} · autorizado pelo gerente #{venda.pdvGerenteCancelamentoId}
              {venda.protocoloCancelamento && ` · protocolo SEFAZ ${venda.protocoloCancelamento}`}. O dinheiro desta
              venda foi estornado do caixa do terminal.
            </>
          }
        />
      )}
      <Descriptions size="small" column={4}>
        <Descriptions.Item label="Id no PDV">{venda.idOrigem}</Descriptions.Item>
        <Descriptions.Item label="Total produtos">{reais(venda.valorTotalProdutos)}</Descriptions.Item>
        <Descriptions.Item label="Desconto / acréscimo">
          {reais(venda.valorDesconto)} / {reais(venda.valorAcrescimo)}
        </Descriptions.Item>
        <Descriptions.Item label="Recebido / troco">
          {reais(venda.valorRecebido)} / {reais(venda.valorTroco)}
        </Descriptions.Item>
        <Descriptions.Item label="Documento fiscal">{venda.tipoOperacaoFiscal ?? '—'}</Descriptions.Item>
        <Descriptions.Item label="Chave de acesso" span={3}>
          {venda.chaveAcessoFiscal ?? '—'}
        </Descriptions.Item>
      </Descriptions>
      <Table<VendaPdvItem>
        size="small"
        rowKey="id"
        pagination={false}
        dataSource={venda.itens}
        columns={[
          { title: '#', dataIndex: 'numeroItem', width: 50 },
          { title: 'Produto', key: 'produto', render: (_, i) => <ProdutoResumoCell produtoId={i.produtoId} /> },
          { title: 'Qtd.', dataIndex: 'quantidade', align: 'right' },
          { title: 'Unitário', dataIndex: 'valorUnitario', align: 'right', render: reais },
          { title: 'Total', dataIndex: 'valorTotal', align: 'right', render: reais },
          {
            title: 'Devolvido',
            dataIndex: 'quantidadeDevolvida',
            align: 'right',
            render: (v: number, i) => (i.cancelado ? <Tag>Cancelado</Tag> : v > 0 ? <Text type="warning">{v}</Text> : '—'),
          },
        ]}
      />
      <Table<VendaPdvPagamento>
        size="small"
        rowKey="id"
        pagination={false}
        dataSource={venda.pagamentos}
        locale={{ emptyText: 'Sem pagamentos (venda cancelada)' }}
        columns={[
          { title: 'Forma', key: 'forma', render: (_, p) => TPAG_LABEL[p.codigoPagamentoNfce] ?? p.descricao ?? p.codigoPagamentoNfce },
          { title: 'Valor', dataIndex: 'valor', align: 'right', render: reais },
          { title: 'NSU / rede', key: 'tef', render: (_, p) => (p.nsu ? `${p.nsu} / ${p.rede ?? '—'}` : '—') },
          { title: '', key: 'estorno', render: (_, p) => (p.estornado ? <Tag color="red">Estornado</Tag> : null) },
        ]}
      />
      {venda.devolucoes.length > 0 && (
        <Table<VendaPdvDevolucao>
          size="small"
          rowKey="id"
          pagination={false}
          dataSource={venda.devolucoes}
          title={() => 'Devoluções'}
          columns={[
            { title: 'Data/hora', dataIndex: 'dataHoraDevolucao', render: (v: string) => dayjs(v).format('DD/MM/YYYY HH:mm') },
            { title: 'Item', dataIndex: 'numeroItem' },
            { title: 'Qtd.', dataIndex: 'quantidadeDevolvida', align: 'right' },
            { title: 'Valor', dataIndex: 'valorDevolvido', align: 'right', render: reais },
            { title: 'Motivo', dataIndex: 'motivo', render: (v?: string | null) => v ?? '—' },
            { title: 'Autorizado por', dataIndex: 'pdvGerenteAutorizadorId', render: (v: number) => `Operador #${v}` },
          ]}
        />
      )}
    </Flex>
  )
}
