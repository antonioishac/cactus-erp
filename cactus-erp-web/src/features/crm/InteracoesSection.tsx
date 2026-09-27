import { useState } from 'react'
import { Button, Card, Table } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import AdicionarInteracaoModal from './modals/AdicionarInteracaoModal'
import { UsuarioResumoCell } from './ResumoCells'
import { useInteracoes } from './queries'
import { TIPO_INTERACAO_LABEL } from './types'
import type { InteracaoResponse } from './types'

interface InteracoesSectionProps {
  leadId?: number
  oportunidadeId?: number
  podeGerenciar: boolean
}

/** Histórico append-only — sem edição/remoção, mesmo critério de `EstoqueMovimento`/`DocumentoVersao`. Reaproveitada em `LeadFormPage`/`OportunidadeFormPage`. */
export default function InteracoesSection({ leadId, oportunidadeId, podeGerenciar }: InteracoesSectionProps) {
  const [modalAberto, setModalAberto] = useState(false)
  const { data } = useInteracoes(leadId, oportunidadeId)

  return (
    <Card
      title="Interações"
      extra={
        podeGerenciar && (
          <Button icon={<PlusOutlined />} onClick={() => setModalAberto(true)}>
            Registrar Interação
          </Button>
        )
      }
    >
      <Table<InteracaoResponse>
        rowKey="id"
        dataSource={data?.conteudo}
        pagination={false}
        locale={{ emptyText: 'Nenhuma interação registrada ainda' }}
        columns={[
          { title: 'Tipo', dataIndex: 'tipo', render: (v: keyof typeof TIPO_INTERACAO_LABEL) => TIPO_INTERACAO_LABEL[v] },
          { title: 'Data', dataIndex: 'dataInteracao', render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY HH:mm') : '—') },
          {
            title: 'Registrado por',
            dataIndex: 'usuarioId',
            render: (v?: number) => (v ? <UsuarioResumoCell usuarioId={v} /> : '—'),
          },
          { title: 'Descrição', dataIndex: 'descricao' },
        ]}
      />

      <AdicionarInteracaoModal open={modalAberto} leadId={leadId} oportunidadeId={oportunidadeId} onClose={() => setModalAberto(false)} />
    </Card>
  )
}
