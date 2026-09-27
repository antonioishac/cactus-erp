import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Table, Tag, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { ColaboradorResumoCell } from './ResumoCells'
import { useBuscaColaboradoresParaSelecao, useRequisicoes } from './queries'
import { SITUACAO_REQUISICAO_LABEL, TIPO_REQUISICAO_LABEL } from './types'
import type { RequisicaoCompraResponse, RequisicoesFiltro } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_SITUACAO: Record<string, string> = {
  ABERTA: 'processing',
  PEDIDO_GERADO: 'success',
  CANCELADA: 'error',
}

export default function RequisicoesListPage() {
  const navigate = useNavigate()
  const [filtro, setFiltro] = useState<RequisicoesFiltro>({})
  const [pagina, setPagina] = useState(0)
  const [termoColaborador, setTermoColaborador] = useState<string | undefined>(undefined)

  const { data: colaboradores, isLoading: carregandoColaboradores } = useBuscaColaboradoresParaSelecao(termoColaborador)
  const { data, isLoading, isPlaceholderData } = useRequisicoes(filtro, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Requisições de Compra
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/compras/requisicoes/novo')}>
          Nova Requisição
        </Button>
      </Flex>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <AsyncSelect
            allowClear
            style={{ width: 320 }}
            placeholder="Filtrar por solicitante"
            opcoes={colaboradores}
            carregando={carregandoColaboradores}
            termo={termoColaborador}
            onTermoChange={setTermoColaborador}
            toOption={(c) => ({ value: c.id, label: c.nome })}
            onChange={(colaboradorId: number | undefined) => {
              setFiltro((f) => ({ ...f, colaboradorId }))
              setPagina(0)
            }}
          />
        </Flex>

        <Table<RequisicaoCompraResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(requisicao) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/compras/requisicoes/${requisicao.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} requisição(ões)`,
          }}
          columns={[
            { title: 'Id', dataIndex: 'id', width: 80 },
            { title: 'Solicitante', key: 'solicitante', render: (_, r) => <ColaboradorResumoCell colaboradorId={r.colaboradorId} /> },
            {
              title: 'Tipo',
              dataIndex: 'tipo',
              render: (tipo: keyof typeof TIPO_REQUISICAO_LABEL) => TIPO_REQUISICAO_LABEL[tipo],
            },
            { title: 'Descrição', dataIndex: 'descricao', render: (v) => v || '—', ellipsis: true },
            { title: 'Itens', key: 'itens', render: (_, r) => r.itens.length },
            {
              title: 'Data',
              dataIndex: 'dataRequisicao',
              render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY') : '—'),
            },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (situacao: keyof typeof SITUACAO_REQUISICAO_LABEL) => (
                <Tag color={COR_SITUACAO[situacao]}>{SITUACAO_REQUISICAO_LABEL[situacao]}</Tag>
              ),
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
