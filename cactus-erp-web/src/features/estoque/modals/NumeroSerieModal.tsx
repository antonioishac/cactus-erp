import { useEffect, useState } from 'react'
import { App, Form, Input, Modal, Select } from 'antd'
import { useAuth } from '@features/auth/useAuth'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useBuscaProdutosParaSelecao, useCadastrarNumeroSerie, useDepositosAtivos } from '../queries'
import { SITUACAO_NUMERO_SERIE_LABEL } from '../types'
import type { NumeroSerieRequest } from '../types'

interface NumeroSerieModalProps {
  open: boolean
  /** Quando o usuário já filtrou por produto na listagem, o cadastro nasce travado nesse produto. */
  produtoIdFixo?: number
  onClose: () => void
}

const VALORES_PADRAO: Partial<NumeroSerieRequest> = { situacao: 'DISPONIVEL' }

export default function NumeroSerieModal({ open, produtoIdFixo, onClose }: NumeroSerieModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<NumeroSerieRequest>()
  const { empresaAtivaId } = useAuth()
  const { data: depositos, isLoading: carregandoDepositos } = useDepositosAtivos(empresaAtivaId ?? undefined)
  const [termoProduto, setTermoProduto] = useState<string | undefined>(undefined)
  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termoProduto)
  const cadastrar = useCadastrarNumeroSerie()

  useEffect(() => {
    if (open) {
      form.setFieldsValue({ ...VALORES_PADRAO, produtoId: produtoIdFixo })
    }
  }, [open, produtoIdFixo, form])

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await cadastrar.mutateAsync(valores)
        message.success('Número de série cadastrado.')
        form.resetFields()
        setTermoProduto(undefined)
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao cadastrar número de série.')
      }
    })
  }

  return (
    <Modal
      title="Cadastrar número de série"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Cadastrar"
      cancelText="Cancelar"
      confirmLoading={cadastrar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="numeroSerieForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Form.Item name="produtoId" label="Produto" rules={[{ required: true, message: 'Selecione o produto' }]}>
          <AsyncSelect
            disabled={!!produtoIdFixo}
            opcoes={produtos}
            carregando={carregandoProdutos}
            termo={termoProduto}
            onTermoChange={setTermoProduto}
            toOption={(p) => ({ value: p.id, label: `${p.codigoInterno} — ${p.nome}` })}
          />
        </Form.Item>
        <Form.Item name="numeroSerie" label="Número de série" rules={[{ required: true, message: 'Informe o número de série' }, { max: 100 }]}>
          <Input />
        </Form.Item>
        <Form.Item name="depositoId" label="Depósito">
          <Select allowClear showSearch loading={carregandoDepositos} optionFilterProp="label" options={depositos?.map((d) => ({ value: d.id, label: d.nome }))} />
        </Form.Item>
        <Form.Item name="situacao" label="Situação" rules={[{ required: true }]}>
          <Select options={Object.entries(SITUACAO_NUMERO_SERIE_LABEL).map(([value, label]) => ({ value, label }))} />
        </Form.Item>
      </Form>
    </Modal>
  )
}
