import { useEffect, useState } from 'react'
import { App, Button, Card, Flex, Popconfirm, Select, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import NovaTransferenciaModal from './modals/NovaTransferenciaModal'
import {
  useCancelarTransferencia,
  useDepositosAtivos,
  useEnviarTransferencia,
  useReceberTransferencia,
  useTransferencias,
} from './queries'
import { SITUACAO_TRANSFERENCIA_LABEL } from './types'
import type { TransferenciaEstoqueResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_SITUACAO: Record<string, string> = {
  SOLICITADA: 'default',
  EM_TRANSITO: 'processing',
  CONCLUIDA: 'success',
  CANCELADA: 'error',
}

/**
 * Backend exige `depositoId` na listagem (`TransferenciaEstoqueController.listarPorDeposito`,
 * sem "listar tudo") — por isso o Select de depósito age como filtro OBRIGATÓRIO, não
 * opcional como em Saldos/Movimentos, e a tabela só carrega depois de um depósito
 * selecionado (default: primeiro depósito ativo da empresa).
 */
export default function TransferenciasListPage() {
  const { message } = App.useApp()
  const { empresaAtivaId, authorities } = useAuth()
  const podeTransferir = hasAuthority(authorities, 'ESTOQUE_TRANSFERIR')
  const [depositoId, setDepositoId] = useState<number | undefined>(undefined)
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)

  const { data: depositos } = useDepositosAtivos(empresaAtivaId ?? undefined)
  const { data, isLoading, isPlaceholderData } = useTransferencias(depositoId, pagina, TAMANHO_PAGINA)
  const enviar = useEnviarTransferencia()
  const receber = useReceberTransferencia()
  const cancelar = useCancelarTransferencia()

  /* oxlint-disable react-hooks/set-state-in-effect -- default de conveniência assim que a lista de depósitos ativos carrega */
  useEffect(() => {
    if (depositoId === undefined && depositos && depositos.length > 0) {
      setDepositoId(depositos[0]?.id)
    }
  }, [depositos, depositoId])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function executar(acao: 'enviar' | 'receber' | 'cancelar', id: number) {
    try {
      if (acao === 'enviar') await enviar.mutateAsync(id)
      if (acao === 'receber') await receber.mutateAsync(id)
      if (acao === 'cancelar') await cancelar.mutateAsync(id)
      message.success('Transferência atualizada com sucesso.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao atualizar transferência.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Transferências entre Depósitos
        </Title>
        <Tooltip title={podeTransferir ? undefined : 'Você não tem permissão para transferir estoque'}>
          <Button type="primary" icon={<PlusOutlined />} disabled={!podeTransferir} onClick={() => setModalAberto(true)}>
            Nova Transferência
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

        <Table<TransferenciaEstoqueResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} transferência(s)`,
          }}
          columns={[
            { title: 'Produto', key: 'produto', render: (_, t) => <ProdutoResumoCell produtoId={t.produtoId} /> },
            { title: 'Origem', dataIndex: 'depositoOrigemNome' },
            { title: 'Destino', dataIndex: 'depositoDestinoNome' },
            { title: 'Quantidade', dataIndex: 'quantidade', align: 'right' },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (situacao: keyof typeof SITUACAO_TRANSFERENCIA_LABEL) => (
                <Tag color={COR_SITUACAO[situacao]}>{SITUACAO_TRANSFERENCIA_LABEL[situacao]}</Tag>
              ),
            },
            {
              title: 'Envio',
              dataIndex: 'dataEnvio',
              render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY HH:mm') : '—'),
            },
            {
              title: 'Recebimento',
              dataIndex: 'dataRecebimento',
              render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY HH:mm') : '—'),
            },
            ...(podeTransferir
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    render: (_: unknown, t: TransferenciaEstoqueResponse) => (
                      <Space>
                        {t.situacao === 'SOLICITADA' && t.depositoOrigemId === depositoId && (
                          <Button size="small" onClick={() => executar('enviar', t.id)}>
                            Enviar
                          </Button>
                        )}
                        {t.situacao === 'EM_TRANSITO' && t.depositoDestinoId === depositoId && (
                          <Button size="small" type="primary" onClick={() => executar('receber', t.id)}>
                            Receber
                          </Button>
                        )}
                        {t.situacao === 'SOLICITADA' && (
                          <Popconfirm title="Cancelar esta transferência?" onConfirm={() => executar('cancelar', t.id)}>
                            <Button size="small" danger>
                              Cancelar
                            </Button>
                          </Popconfirm>
                        )}
                      </Space>
                    ),
                  },
                ]
              : []),
          ]}
        />
      </Card>

      <NovaTransferenciaModal open={modalAberto} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
