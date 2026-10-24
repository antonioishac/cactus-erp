import { useState } from 'react'
import { Card, Flex, Input, InputNumber, Table, Tag, Typography } from 'antd'
import dayjs from 'dayjs'
import ExportarRelatorioButtons from '@shared/ui/ExportarRelatorioButtons'
import { exportarNotasPorPeriodo, type NotaFiscalPeriodoItem } from './api'
import { useNotasPorPeriodo } from './queries'
import { SITUACAO_NOTA_FISCAL_LABEL } from './types'

const { Title, Text } = Typography

const LABEL_TIPO: Record<NotaFiscalPeriodoItem['tipoDocumento'], string> = {
  NFE: 'NF-e',
  NFCE: 'NFC-e',
  NFSE: 'NFS-e',
  CTE: 'CT-e',
}

const COR_SITUACAO: Record<string, string> = {
  RASCUNHO: 'default',
  ENVIADA: 'processing',
  AUTORIZADA: 'success',
  REJEITADA: 'error',
  ERRO: 'error',
  CANCELADA: 'default',
}

/** Unifica NF-e/NFC-e/NFS-e/CT-e numa grade só por data de emissão — diferente das 3 listagens
 * próprias (`NotasFiscaisListPage`/`CtesListPage`/`NotasServicoListPage`), que são por tipo. */
export default function NotasPeriodoPage() {
  const [dataInicio, setDataInicio] = useState('')
  const [dataFim, setDataFim] = useState('')
  const [empresaId, setEmpresaId] = useState<number | undefined>(undefined)

  const { data, isLoading } = useNotasPorPeriodo(dataInicio || undefined, dataFim || undefined, empresaId)
  const notas = data ?? []
  const filtroCompleto = !!dataInicio && !!dataFim
  const total = notas.reduce((soma, n) => soma + n.valorTotal, 0)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Notas Emitidas por Período
      </Title>

      <Card>
        <Flex gap={12} align="center" wrap="wrap" style={{ marginBottom: 16 }}>
          <span>Data de emissão</span>
          <Input type="date" value={dataInicio} onChange={(e) => setDataInicio(e.target.value)} style={{ width: 160 }} />
          <span>até</span>
          <Input type="date" value={dataFim} onChange={(e) => setDataFim(e.target.value)} style={{ width: 160 }} />
          <InputNumber placeholder="Empresa (id)" min={1} value={empresaId} onChange={(v) => setEmpresaId(v ?? undefined)} />
          <ExportarRelatorioButtons
            disabled={!filtroCompleto}
            motivoDesabilitado="Selecione o período"
            onExportar={(formato) => exportarNotasPorPeriodo(dataInicio, dataFim, empresaId, formato)}
          />
        </Flex>

        <Table<NotaFiscalPeriodoItem>
          rowKey={(n) => `${n.tipoDocumento}-${n.numero}-${n.serie ?? ''}`}
          loading={isLoading}
          dataSource={notas}
          pagination={false}
          locale={{ emptyText: 'Selecione o período pra listar as notas emitidas' }}
          columns={[
            { title: 'Tipo', dataIndex: 'tipoDocumento', render: (v: NotaFiscalPeriodoItem['tipoDocumento']) => LABEL_TIPO[v] },
            { title: 'Número', dataIndex: 'numero' },
            { title: 'Série', dataIndex: 'serie', render: (v?: string) => v || '—' },
            {
              title: 'Data de emissão',
              dataIndex: 'dataEmissao',
              render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY HH:mm') : '—'),
            },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (v: keyof typeof SITUACAO_NOTA_FISCAL_LABEL) => (
                <Tag color={COR_SITUACAO[v]}>{SITUACAO_NOTA_FISCAL_LABEL[v]}</Tag>
              ),
            },
            { title: 'Valor total', dataIndex: 'valorTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
          ]}
          summary={() =>
            notas.length > 0 && (
              <Table.Summary.Row>
                <Table.Summary.Cell index={0} colSpan={5}>
                  <Text strong>Total ({notas.length} documento(s))</Text>
                </Table.Summary.Cell>
                <Table.Summary.Cell index={5} align="right">
                  <Text strong>R$ {total.toFixed(2)}</Text>
                </Table.Summary.Cell>
              </Table.Summary.Row>
            )
          }
        />
      </Card>
    </Flex>
  )
}
