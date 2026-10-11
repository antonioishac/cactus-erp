import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Card, Flex, Input, Segmented, Table, Tag, Typography } from 'antd'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import AsyncSelect from '@shared/ui/AsyncSelect'
import ExportarRelatorioButtons from '@shared/ui/ExportarRelatorioButtons'
import { exportarTitulosEmAberto } from './api'
import { ClienteResumoCell, FornecedorResumoCell } from './ResumoCells'
import { useBuscaClientesParaSelecao, useBuscaFornecedoresParaSelecao, useTitulosEmAberto } from './queries'
import { SITUACAO_TITULO_LABEL } from './types'
import type { TipoTituloRelatorio, TituloEmAbertoResponse, TitulosEmAbertoFiltro } from './types'

const { Title, Text } = Typography

const moeda = (v: number) => `R$ ${v.toFixed(2)}`

/**
 * Relatório de títulos ABERTA/PARCIAL (Contas a Receber ou a Pagar) por vencimento —
 * `GET /relatorios/titulos-em-aberto` do financeiro-service, calculado sob demanda.
 * Dias de atraso/multa/juros/valor atualizado vêm do backend com o mesmo cálculo da
 * tela de título. Vencimento vazio = carteira inteira em aberto. PDF/Excel exportam
 * exatamente os filtros da tela.
 */
export default function TitulosEmAbertoPage() {
  const navigate = useNavigate()
  const { empresaAtivaId } = useAuth()
  const [tipo, setTipo] = useState<TipoTituloRelatorio>('RECEBER')
  const [dataInicio, setDataInicio] = useState<string>('')
  const [dataFim, setDataFim] = useState<string>('')
  const [pessoaId, setPessoaId] = useState<number | undefined>(undefined)
  const [termoPessoa, setTermoPessoa] = useState<string | undefined>(undefined)

  const receber = tipo === 'RECEBER'
  const { data: clientes, isLoading: carregandoClientes } = useBuscaClientesParaSelecao(receber ? termoPessoa : undefined)
  const { data: fornecedores, isLoading: carregandoFornecedores } = useBuscaFornecedoresParaSelecao(receber ? undefined : termoPessoa)

  const filtro: TitulosEmAbertoFiltro | undefined = empresaAtivaId
    ? {
        tipo,
        empresaId: empresaAtivaId,
        dataInicio: dataInicio || undefined,
        dataFim: dataFim || undefined,
        pessoaId,
      }
    : undefined
  const { data, isLoading } = useTitulosEmAberto(filtro)
  const titulos = data ?? []

  function trocarTipo(novo: TipoTituloRelatorio) {
    setTipo(novo)
    // Cliente e fornecedor são cadastros diferentes — o id de um não vale no outro.
    setPessoaId(undefined)
    setTermoPessoa(undefined)
  }

  const total = (campo: keyof Pick<TituloEmAbertoResponse, 'valorOriginal' | 'valorPago' | 'valorSaldo' | 'valorMulta' | 'valorJuros' | 'valorAtualizado'>) =>
    titulos.reduce((soma, t) => soma + t[campo], 0)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Títulos em Aberto
      </Title>

      <Card>
        <Flex gap={12} align="center" wrap="wrap" style={{ marginBottom: 16 }}>
          <Segmented
            value={tipo}
            onChange={(v) => trocarTipo(v as TipoTituloRelatorio)}
            options={[
              { label: 'A receber', value: 'RECEBER' },
              { label: 'A pagar', value: 'PAGAR' },
            ]}
          />
          <span>Vencimento</span>
          <Input type="date" value={dataInicio} onChange={(e) => setDataInicio(e.target.value)} style={{ width: 160 }} />
          <span>até</span>
          <Input type="date" value={dataFim} onChange={(e) => setDataFim(e.target.value)} style={{ width: 160 }} />
          <AsyncSelect
            key={tipo}
            allowClear
            placeholder={receber ? 'Todos os clientes' : 'Todos os fornecedores'}
            style={{ width: 260 }}
            value={pessoaId}
            onChange={(v) => setPessoaId(v as number | undefined)}
            opcoes={receber ? clientes : fornecedores}
            carregando={receber ? carregandoClientes : carregandoFornecedores}
            termo={termoPessoa}
            onTermoChange={setTermoPessoa}
            toOption={(p) => ({ value: p.id, label: p.nome })}
          />
          <ExportarRelatorioButtons
            disabled={!filtro}
            motivoDesabilitado="Selecione uma empresa ativa"
            onExportar={(formato) => exportarTitulosEmAberto(filtro as TitulosEmAbertoFiltro, formato)}
          />
        </Flex>

        <Table<TituloEmAbertoResponse>
          rowKey="tituloId"
          size="small"
          loading={isLoading}
          dataSource={titulos}
          pagination={false}
          scroll={{ x: 1200 }}
          locale={{ emptyText: 'Nenhum título em aberto com os filtros selecionados' }}
          onRow={(t) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/financeiro/${receber ? 'contas-receber' : 'contas-pagar'}/${t.tituloId}`),
          })}
          columns={[
            { title: 'Título', dataIndex: 'tituloId', width: 70, render: (v: number) => `#${v}` },
            {
              title: receber ? 'Cliente' : 'Fornecedor',
              key: 'pessoa',
              render: (_, t) => (receber ? <ClienteResumoCell clienteId={t.pessoaId} /> : <FornecedorResumoCell fornecedorId={t.pessoaId} />),
            },
            { title: 'Documento', dataIndex: 'numeroDocumento', render: (v?: string) => v || '—' },
            { title: 'Vencimento', dataIndex: 'dataVencimento', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
            {
              title: 'Situação',
              key: 'situacao',
              render: (_, t) => (
                <Flex gap={4}>
                  <Tag color={t.situacao === 'PARCIAL' ? 'gold' : 'default'}>{SITUACAO_TITULO_LABEL[t.situacao]}</Tag>
                  {t.diasAtraso != null && <Tag color="volcano">{t.diasAtraso} dia(s) de atraso</Tag>}
                </Flex>
              ),
            },
            { title: 'Original', dataIndex: 'valorOriginal', align: 'right', render: moeda },
            { title: 'Pago', dataIndex: 'valorPago', align: 'right', render: moeda },
            { title: 'Saldo', dataIndex: 'valorSaldo', align: 'right', render: moeda },
            { title: 'Multa', dataIndex: 'valorMulta', align: 'right', render: moeda },
            { title: 'Juros', dataIndex: 'valorJuros', align: 'right', render: moeda },
            { title: 'Atualizado', dataIndex: 'valorAtualizado', align: 'right', render: (v: number) => <strong>{moeda(v)}</strong> },
          ]}
          summary={() =>
            titulos.length > 0 && (
              <Table.Summary.Row>
                <Table.Summary.Cell index={0} colSpan={5}>
                  <Text strong>Total ({titulos.length} título(s))</Text>
                </Table.Summary.Cell>
                <Table.Summary.Cell index={5} align="right"><Text strong>{moeda(total('valorOriginal'))}</Text></Table.Summary.Cell>
                <Table.Summary.Cell index={6} align="right"><Text strong>{moeda(total('valorPago'))}</Text></Table.Summary.Cell>
                <Table.Summary.Cell index={7} align="right"><Text strong>{moeda(total('valorSaldo'))}</Text></Table.Summary.Cell>
                <Table.Summary.Cell index={8} align="right"><Text strong>{moeda(total('valorMulta'))}</Text></Table.Summary.Cell>
                <Table.Summary.Cell index={9} align="right"><Text strong>{moeda(total('valorJuros'))}</Text></Table.Summary.Cell>
                <Table.Summary.Cell index={10} align="right"><Text strong>{moeda(total('valorAtualizado'))}</Text></Table.Summary.Cell>
              </Table.Summary.Row>
            )
          }
        />
      </Card>
    </Flex>
  )
}
