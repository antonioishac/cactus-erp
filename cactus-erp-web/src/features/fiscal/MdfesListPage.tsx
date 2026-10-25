import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import GerarMdfeModal from './modals/GerarMdfeModal'
import { useMdfes } from './queries'
import { COR_SITUACAO_MDFE, SITUACAO_NOTA_FISCAL_LABEL } from './types'
import type { MdfeCabecalhoResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

/** Diferente de CT-e/NF-e, MDF-e nasce aqui mesmo — agrupando CT-e já autorizados, não a partir de uma Venda. */
export default function MdfesListPage() {
  const navigate = useNavigate()
  const { authorities } = useAuth()
  const podeEmitir = hasAuthority(authorities, 'FISCAL_EMITIR')
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)
  const { data, isLoading, isPlaceholderData } = useMdfes(pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Manifestos de Documentos Fiscais (MDF-e)
        </Title>
        <Tooltip title={podeEmitir ? undefined : 'Sem permissão para gerar MDF-e'}>
          <Button type="primary" icon={<PlusOutlined />} disabled={!podeEmitir} onClick={() => setModalAberto(true)}>
            Novo MDF-e
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Table<MdfeCabecalhoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(mdfe) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/fiscal/mdfes/${mdfe.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} MDF-e(s)`,
          }}
          columns={[
            { title: 'Série/Número', key: 'numero', render: (_, m) => (m.numero ? `${m.serie}/${m.numero}` : '—') },
            { title: 'Início da viagem', dataIndex: 'dataInicioViagem', render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY') : '—') },
            { title: 'Veículo', dataIndex: 'veiculoPlaca', render: (v?: string) => v ?? '—' },
            { title: 'Condutor', dataIndex: 'condutorNome', render: (v?: string) => v ?? '—' },
            { title: 'CT-e', key: 'qtd', align: 'right', render: (_, m) => m.documentos.length },
            {
              title: 'Valor da carga',
              dataIndex: 'valorTotalCarga',
              align: 'right',
              render: (v?: number) => (v != null ? `R$ ${v.toFixed(2)}` : '—'),
            },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (situacao: keyof typeof SITUACAO_NOTA_FISCAL_LABEL) => (
                <Tag color={COR_SITUACAO_MDFE[situacao]}>{SITUACAO_NOTA_FISCAL_LABEL[situacao]}</Tag>
              ),
            },
          ]}
        />
      </Card>

      <GerarMdfeModal open={modalAberto} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
