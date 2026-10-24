import { useEffect } from 'react'
import { App, Checkbox, Form, Input, Modal, Select } from 'antd'
import { useAtualizarPlanoConta, useCriarPlanoConta, useTodasContas } from '../queries'
import { NATUREZA_CONTA_LABEL, TIPO_CONTA_CONTABIL_LABEL } from '../types'
import type { PlanoContaRequest, PlanoContaResponse } from '../types'

interface Props {
  open: boolean
  empresaId: number
  valorInicial?: PlanoContaResponse
  onClose: () => void
}

/**
 * Só conta sintética pode ser pai de outra (validado pelo servidor, 422) — o Select de
 * conta pai já filtra pra só mostrar sintéticas, evitando a chamada fadada ao erro.
 * Ciclo de hierarquia (pai = descendente da própria conta) também é validado pelo
 * backend, não replicado aqui — mesmo critério de `PastaModal`.
 */
export default function PlanoContaModal({ open, empresaId, valorInicial, onClose }: Props) {
  const { message } = App.useApp()
  const [form] = Form.useForm<PlanoContaRequest>()
  const { data: contas, isLoading: carregandoContas } = useTodasContas(empresaId)
  const criar = useCriarPlanoConta()
  const atualizar = useAtualizarPlanoConta()
  const salvando = criar.isPending || atualizar.isPending

  useEffect(() => {
    if (open) {
      form.setFieldsValue(
        valorInicial ?? {
          empresaId,
          contaAnalitica: true,
          situacao: 'ATIVO',
        },
      )
    }
  }, [open, valorInicial, empresaId, form])

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        const request: PlanoContaRequest = { ...valores, empresaId }
        if (valorInicial) {
          await atualizar.mutateAsync({ id: valorInicial.id, request })
          message.success('Conta atualizada.')
        } else {
          await criar.mutateAsync(request)
          message.success('Conta criada.')
        }
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao salvar conta.')
      }
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar conta' : 'Nova conta'}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={salvando}
      destroyOnHidden
    >
      <Form form={form} name="planoContaForm" layout="vertical">
        <Form.Item name="codigo" label="Código" rules={[{ required: true, message: 'Informe o código' }, { max: 20 }]}>
          <Input />
        </Form.Item>
        <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 150 }]}>
          <Input />
        </Form.Item>
        <Form.Item
          name="contaPaiId"
          label="Conta pai"
          tooltip="Deixe em branco pra criar uma conta raiz. Só conta sintética pode ser pai."
          rules={[
            {
              validator: (_, value) =>
                value != null && valorInicial != null && value === valorInicial.id
                  ? Promise.reject(new Error('Uma conta não pode ser pai de si mesma'))
                  : Promise.resolve(),
            },
          ]}
        >
          <Select
            allowClear
            showSearch
            loading={carregandoContas}
            optionFilterProp="label"
            options={contas
              ?.filter((c) => !c.contaAnalitica && c.id !== valorInicial?.id)
              .map((c) => ({ value: c.id, label: `${c.codigo} — ${c.nome}` }))}
          />
        </Form.Item>
        <Form.Item name="tipo" label="Tipo" rules={[{ required: true, message: 'Selecione o tipo' }]}>
          <Select
            options={Object.entries(TIPO_CONTA_CONTABIL_LABEL).map(([value, label]) => ({ value, label }))}
          />
        </Form.Item>
        <Form.Item name="natureza" label="Natureza" rules={[{ required: true, message: 'Selecione a natureza' }]}>
          <Select options={Object.entries(NATUREZA_CONTA_LABEL).map(([value, label]) => ({ value, label }))} />
        </Form.Item>
        <Form.Item
          name="contaAnalitica"
          label="Conta analítica"
          tooltip="Só conta analítica pode receber lançamento. Conta sintética só agrupa subcontas."
          valuePropName="checked"
        >
          <Checkbox>Recebe lançamento diretamente</Checkbox>
        </Form.Item>
        <Form.Item name="codigoReferencialRfb" label="Código referencial RFB" rules={[{ max: 20 }]}>
          <Input />
        </Form.Item>
      </Form>
    </Modal>
  )
}
