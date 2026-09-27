import { App, Form, Modal, Select } from 'antd'
import { UFS } from '@shared/domain/pessoaUtils'
import { useOperacoesFiscais, useRecalcularTributacaoPadrao } from '../queries'
import type { RecalcularTributacaoPadraoRequest } from '../types'

interface RecalcularTributacaoModalProps {
  open: boolean
  produtoId: number
  onClose: () => void
}

/**
 * Só existe em modo edição (precisa de um produto já salvo) — pede Operação
 * Fiscal + UF de destino e chama `POST /produtos/{id}/recalcular-tributacao-padrao`,
 * que resolve `GrupoTributario × OperacaoFiscal × UF` no motor tributário e
 * cacheia CST/CSOSN/alíquotas nos campos `*Padrao` do produto (prontos pra
 * sync com o cactus-pdv). Não é o cadastro em si de Operação Fiscal/Grupo
 * Tributário — isso é escopo do futuro módulo de Motor Tributário.
 */
export default function RecalcularTributacaoModal({ open, produtoId, onClose }: RecalcularTributacaoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<RecalcularTributacaoPadraoRequest>()
  const { data: operacoesFiscais, isLoading: carregandoOperacoes } = useOperacoesFiscais()
  const recalcular = useRecalcularTributacaoPadrao(produtoId)

  async function handleOk() {
    const valores = await form.validateFields()
    try {
      await recalcular.mutateAsync(valores)
      message.success('Tributação padrão recalculada.')
      form.resetFields()
      onClose()
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao recalcular tributação.')
    }
  }

  return (
    <Modal
      title="Recalcular tributação padrão"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Recalcular"
      cancelText="Cancelar"
      confirmLoading={recalcular.isPending}
      destroyOnHidden
    >
      <Form form={form} name="recalcularTributacaoForm" layout="vertical">
        <Form.Item
          name="operacaoFiscalId"
          label="Operação fiscal"
          rules={[{ required: true, message: 'Selecione a operação fiscal' }]}
          tooltip="Tipo de venda usado como referência (ex: venda a consumidor final, revenda, interestadual)."
        >
          <Select
            showSearch
            loading={carregandoOperacoes}
            optionFilterProp="label"
            options={operacoesFiscais?.map((o) => ({ value: o.id, label: o.nome }))}
          />
        </Form.Item>
        <Form.Item
          name="ufDestino"
          label="UF de destino"
          rules={[{ required: true, message: 'Selecione a UF' }]}
          tooltip="ICMS varia por UF de destino — a resolução é sempre grupo tributário × operação fiscal × UF."
        >
          <Select showSearch options={UFS.map((uf) => ({ value: uf, label: uf }))} />
        </Form.Item>
      </Form>
    </Modal>
  )
}
