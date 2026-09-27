import { useState } from 'react'
import { App, Button, Card, Popconfirm, Space, Table, Tag } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import AdicionarAtividadeModal from './modals/AdicionarAtividadeModal'
import { UsuarioResumoCell } from './ResumoCells'
import { useAtividades, useConcluirAtividade, useExcluirAtividade } from './queries'
import { TIPO_ATIVIDADE_LABEL } from './types'
import type { AtividadeResponse } from './types'

interface AtividadesSectionProps {
  leadId?: number
  oportunidadeId?: number
  podeGerenciar: boolean
}

/** Reaproveitada em `LeadFormPage` e `OportunidadeFormPage` — pelo menos um de `leadId`/`oportunidadeId` sempre presente. */
export default function AtividadesSection({ leadId, oportunidadeId, podeGerenciar }: AtividadesSectionProps) {
  const { message } = App.useApp()
  const [modalAberto, setModalAberto] = useState(false)
  const { data } = useAtividades(leadId, oportunidadeId)
  const concluir = useConcluirAtividade(leadId, oportunidadeId)
  const excluir = useExcluirAtividade(leadId, oportunidadeId)

  async function handleConcluir(id: number) {
    try {
      await concluir.mutateAsync(id)
      message.success('Atividade concluída.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao concluir atividade.')
    }
  }

  async function handleExcluir(id: number) {
    try {
      await excluir.mutateAsync(id)
      message.success('Atividade excluída.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir atividade.')
    }
  }

  return (
    <Card
      title="Atividades"
      extra={
        podeGerenciar && (
          <Button icon={<PlusOutlined />} onClick={() => setModalAberto(true)}>
            Nova Atividade
          </Button>
        )
      }
    >
      <Table<AtividadeResponse>
        rowKey="id"
        dataSource={data?.conteudo}
        pagination={false}
        locale={{ emptyText: 'Nenhuma atividade agendada ainda' }}
        columns={[
          { title: 'Tipo', dataIndex: 'tipo', render: (v: keyof typeof TIPO_ATIVIDADE_LABEL) => TIPO_ATIVIDADE_LABEL[v] },
          { title: 'Data/hora', dataIndex: 'dataHora', render: (v: string) => dayjs(v).format('DD/MM/YYYY HH:mm') },
          {
            title: 'Responsável',
            dataIndex: 'responsavelId',
            render: (v?: number) => (v ? <UsuarioResumoCell usuarioId={v} /> : '—'),
          },
          { title: 'Descrição', dataIndex: 'descricao', render: (v) => v || '—' },
          {
            title: 'Situação',
            dataIndex: 'concluida',
            render: (v: boolean) => (v ? <Tag color="success">Concluída</Tag> : <Tag color="processing">Pendente</Tag>),
          },
          ...(podeGerenciar
            ? [
                {
                  title: 'Ações',
                  key: 'acoes',
                  render: (_: unknown, item: AtividadeResponse) => (
                    <Space>
                      {!item.concluida && (
                        <Button size="small" onClick={() => handleConcluir(item.id)}>
                          Concluir
                        </Button>
                      )}
                      <Popconfirm title="Excluir esta atividade?" onConfirm={() => handleExcluir(item.id)}>
                        <Button size="small" danger>
                          Excluir
                        </Button>
                      </Popconfirm>
                    </Space>
                  ),
                },
              ]
            : []),
        ]}
      />

      <AdicionarAtividadeModal open={modalAberto} leadId={leadId} oportunidadeId={oportunidadeId} onClose={() => setModalAberto(false)} />
    </Card>
  )
}
