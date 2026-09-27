import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Table, Tag, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { ClienteResumoCell } from './ResumoCells'
import { useBuscaClientesParaSelecao, useOsAberturas } from './queries'
import type { OsAberturaResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function OsAberturasListPage() {
  const navigate = useNavigate()
  const [pagina, setPagina] = useState(0)
  const [clienteId, setClienteId] = useState<number | undefined>(undefined)
  const [termoCliente, setTermoCliente] = useState<string | undefined>(undefined)
  const { data: clientes, isLoading: carregandoClientes } = useBuscaClientesParaSelecao(termoCliente)

  const { data, isLoading, isPlaceholderData } = useOsAberturas(clienteId, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Ordens de Serviço
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/os/aberturas/novo')}>
          Abrir OS
        </Button>
      </Flex>

      <Card>
        <Flex style={{ marginBottom: 16 }} gap={12} align="center">
          <span>Filtrar por cliente:</span>
          <AsyncSelect
            style={{ width: 280 }}
            allowClear
            opcoes={clientes}
            carregando={carregandoClientes}
            termo={termoCliente}
            onTermoChange={setTermoCliente}
            toOption={(c) => ({ value: c.id, label: c.nome })}
            value={clienteId}
            onChange={(value) => {
              setClienteId(value as number | undefined)
              setPagina(0)
            }}
          />
        </Flex>

        <Table<OsAberturaResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(os) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/os/aberturas/${os.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} ordem(ns) de serviço`,
          }}
          columns={[
            { title: 'Número', key: 'numero', render: (_, os) => os.numero || `#${os.id}` },
            { title: 'Cliente', key: 'cliente', render: (_, os) => <ClienteResumoCell clienteId={os.clienteId} /> },
            { title: 'Status', dataIndex: 'osStatusNome', render: (v) => <Tag>{v}</Tag> },
            { title: 'Data de início', dataIndex: 'dataInicio', render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY') : '—') },
            { title: 'Previsão', dataIndex: 'dataPrevisao', render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY') : '—') },
            { title: 'Total', dataIndex: 'valorTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            {
              title: 'Orçamento',
              dataIndex: 'orcamentoAprovado',
              render: (v: boolean) => (v ? <Tag color="success">Aprovado</Tag> : <Tag>Pendente</Tag>),
            },
            {
              title: 'Encerrada',
              dataIndex: 'dataFim',
              render: (v?: string) => (v ? <Tag color="default">{dayjs(v).format('DD/MM/YYYY')}</Tag> : <Tag color="processing">Em aberto</Tag>),
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
