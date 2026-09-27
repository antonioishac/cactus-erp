import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Card, Flex, Table, Tag, Typography } from 'antd'
import dayjs from 'dayjs'
import { ClienteResumoCell } from './ResumoCells'
import { useNotasFiscais } from './queries'
import { SITUACAO_NOTA_FISCAL_LABEL } from './types'
import type { NotaFiscalEletronicaResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_SITUACAO: Record<string, string> = {
  RASCUNHO: 'default',
  ENVIADA: 'processing',
  AUTORIZADA: 'success',
  REJEITADA: 'error',
  ERRO: 'error',
  CANCELADA: 'default',
}

/** Sem `POST`/formulário de criação nesta tela — NF-e nasce a partir de uma Venda `CONFIRMADA` (botão "Gerar NF-e" na `VendaDetailPage`, `features/vendas`). */
export default function NotasFiscaisListPage() {
  const navigate = useNavigate()
  const [pagina, setPagina] = useState(0)
  const { data, isLoading, isPlaceholderData } = useNotasFiscais(pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Notas Fiscais Eletrônicas (NF-e)
      </Title>

      <Card>
        <Table<NotaFiscalEletronicaResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(nota) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/fiscal/notas-fiscais/${nota.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} nota(s)`,
          }}
          columns={[
            { title: 'Venda', dataIndex: 'vendaId', render: (v: number) => `#${v}` },
            { title: 'Cliente', key: 'cliente', render: (_, nota) => <ClienteResumoCell clienteId={nota.clienteId} /> },
            { title: 'Série/Número', key: 'numero', render: (_, nota) => (nota.numero ? `${nota.serie}/${nota.numero}` : '—') },
            { title: 'Emissão', dataIndex: 'dataEmissao', render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY HH:mm') : '—') },
            { title: 'Total', dataIndex: 'valorTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (situacao: keyof typeof SITUACAO_NOTA_FISCAL_LABEL) => (
                <Tag color={COR_SITUACAO[situacao]}>{SITUACAO_NOTA_FISCAL_LABEL[situacao]}</Tag>
              ),
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
