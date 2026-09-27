import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Select, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import AbrirInventarioModal from './modals/AbrirInventarioModal'
import { useDepositosAtivos, useInventarios } from './queries'
import { SITUACAO_INVENTARIO_LABEL, TIPO_INVENTARIO_LABEL } from './types'
import type { InventarioFisicoResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_SITUACAO: Record<string, string> = {
  ABERTO: 'default',
  EM_CONTAGEM: 'processing',
  FINALIZADO: 'success',
  CANCELADO: 'error',
}

/** Backend exige `depositoId` na listagem (`InventarioFisicoController.listarPorDeposito`), mesmo padrão de Transferências. */
export default function InventariosListPage() {
  const navigate = useNavigate()
  const { empresaAtivaId, authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'ESTOQUE_INVENTARIO_GERENCIAR')
  const [depositoId, setDepositoId] = useState<number | undefined>(undefined)
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)

  const { data: depositos } = useDepositosAtivos(empresaAtivaId ?? undefined)
  const { data, isLoading, isPlaceholderData } = useInventarios(depositoId, pagina, TAMANHO_PAGINA)

  /* oxlint-disable react-hooks/set-state-in-effect -- default de conveniência assim que a lista de depósitos ativos carrega */
  useEffect(() => {
    if (depositoId === undefined && depositos && depositos.length > 0) {
      setDepositoId(depositos[0]?.id)
    }
  }, [depositos, depositoId])
  /* oxlint-enable react-hooks/set-state-in-effect */

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Inventários Físicos
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar inventários'}>
          <Button type="primary" icon={<PlusOutlined />} disabled={!podeGerenciar || !depositoId} onClick={() => setModalAberto(true)}>
            Abrir Inventário
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <Select
            style={{ width: 280 }}
            placeholder="Selecione um depósito"
            value={depositoId}
            options={depositos?.map((d) => ({ value: d.id, label: d.nome }))}
            onChange={(valor) => {
              setDepositoId(valor)
              setPagina(0)
            }}
          />
        </Flex>

        <Table<InventarioFisicoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(inventario) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/estoque/inventarios/${inventario.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} inventário(s)`,
          }}
          columns={[
            { title: 'Id', dataIndex: 'id', width: 80 },
            {
              title: 'Tipo',
              dataIndex: 'tipo',
              render: (tipo: keyof typeof TIPO_INVENTARIO_LABEL) => TIPO_INVENTARIO_LABEL[tipo],
            },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (situacao: keyof typeof SITUACAO_INVENTARIO_LABEL) => (
                <Tag color={COR_SITUACAO[situacao]}>{SITUACAO_INVENTARIO_LABEL[situacao]}</Tag>
              ),
            },
            { title: 'Itens', key: 'itens', render: (_, i) => i.itens.length },
            {
              title: 'Abertura',
              dataIndex: 'dataAbertura',
              render: (v: string) => dayjs(v).format('DD/MM/YYYY HH:mm'),
            },
            {
              title: 'Fechamento',
              dataIndex: 'dataFechamento',
              render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY HH:mm') : '—'),
            },
          ]}
        />
      </Card>

      {depositoId !== undefined && (
        <AbrirInventarioModal open={modalAberto} depositoId={depositoId} onClose={() => setModalAberto(false)} />
      )}
    </Flex>
  )
}
