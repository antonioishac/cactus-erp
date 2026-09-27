import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Card, Flex, Table, Tag, Typography } from 'antd'
import { ClienteResumoCell } from './ResumoCells'
import { useNotasServico } from './queries'
import { SITUACAO_NOTA_FISCAL_LABEL } from './types'
import type { NotaFiscalServicoResponse } from './types'

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

/** Sem `POST`/formulário de criação nesta tela — NFS-e nasce a partir de uma OS com orçamento aprovado (botão "Gerar NFS-e" na `OsAberturaDetailPage`, `features/os`). */
export default function NotasServicoListPage() {
  const navigate = useNavigate()
  const [pagina, setPagina] = useState(0)
  const { data, isLoading, isPlaceholderData } = useNotasServico(pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Notas Fiscais de Serviço (NFS-e)
      </Title>

      <Card>
        <Table<NotaFiscalServicoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(nota) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/fiscal/notas-servico/${nota.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} nota(s)`,
          }}
          columns={[
            { title: 'OS', dataIndex: 'osAberturaId', render: (v: number) => `#${v}` },
            { title: 'Cliente', key: 'cliente', render: (_, nota) => <ClienteResumoCell clienteId={nota.clienteId} /> },
            { title: 'RPS', key: 'rps', render: (_, nota) => (nota.numeroRps ? `${nota.serieRps ?? ''}/${nota.numeroRps}` : '—') },
            { title: 'Competência', dataIndex: 'competencia', render: (v?: string) => v || '—' },
            { title: 'Valor dos serviços', dataIndex: 'valorServicos', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
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
