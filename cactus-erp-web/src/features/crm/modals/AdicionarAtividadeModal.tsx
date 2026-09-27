import { useState } from 'react'
import { App, DatePicker, Form, Input, Modal, Select } from 'antd'
import type { Dayjs } from 'dayjs'
import dayjs from 'dayjs'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useBuscaUsuariosParaSelecao, useCriarAtividade } from '../queries'
import { TIPO_ATIVIDADE_LABEL } from '../types'
import type { AtividadeRequest } from '../types'

interface AdicionarAtividadeModalProps {
  open: boolean
  leadId?: number
  oportunidadeId?: number
  onClose: () => void
}

interface FormValues extends Omit<AtividadeRequest, 'leadId' | 'oportunidadeId' | 'dataHora'> {
  dataHora: Dayjs
}

const VALORES_PADRAO: Partial<FormValues> = { tipo: 'LIGACAO', dataHora: dayjs() }

export default function AdicionarAtividadeModal({ open, leadId, oportunidadeId, onClose }: AdicionarAtividadeModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<FormValues>()
  const [termoResponsavel, setTermoResponsavel] = useState<string | undefined>(undefined)
  const { data: usuarios, isLoading: carregandoUsuarios } = useBuscaUsuariosParaSelecao(termoResponsavel)
  const criar = useCriarAtividade(leadId, oportunidadeId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      const request: AtividadeRequest = {
        ...valores,
        leadId,
        oportunidadeId,
        dataHora: valores.dataHora.toISOString(),
      }
      try {
        await criar.mutateAsync(request)
        message.success('Atividade criada.')
        form.resetFields()
        setTermoResponsavel(undefined)
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao criar atividade.')
      }
    })
  }

  return (
    <Modal
      title="Nova atividade"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Criar"
      cancelText="Cancelar"
      confirmLoading={criar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="adicionarAtividadeForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Form.Item name="tipo" label="Tipo" rules={[{ required: true }]}>
          <Select options={Object.entries(TIPO_ATIVIDADE_LABEL).map(([value, label]) => ({ value, label }))} />
        </Form.Item>
        <Form.Item name="dataHora" label="Data/hora agendada" rules={[{ required: true, message: 'Informe a data/hora' }]}>
          <DatePicker showTime style={{ width: '100%' }} format="DD/MM/YYYY HH:mm" />
        </Form.Item>
        <Form.Item name="responsavelId" label="Responsável">
          <AsyncSelect
            allowClear
            opcoes={usuarios}
            carregando={carregandoUsuarios}
            termo={termoResponsavel}
            onTermoChange={setTermoResponsavel}
            toOption={(u) => ({ value: u.id, label: `${u.nome} (${u.login})` })}
          />
        </Form.Item>
        <Form.Item name="descricao" label="Descrição" rules={[{ max: 500 }]}>
          <Input.TextArea rows={2} maxLength={500} showCount />
        </Form.Item>
      </Form>
    </Modal>
  )
}
