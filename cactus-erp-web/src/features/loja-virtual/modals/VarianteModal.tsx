import { useEffect } from 'react'
import { App, Form, Input, InputNumber, Modal, Select, Typography } from 'antd'
import { useAtualizarVariante, useCriarVariante } from '../queries'
import type { AtributoCategoriaResponse, VarianteProdutoLojaRequest, VarianteProdutoLojaResponse } from '../types'

const { Text } = Typography

interface Props {
  open: boolean
  produtoId: number
  /** Atributos da categoria do produto com `geraVariacao=true` — um campo por atributo, na criação. */
  atributosVariacao: AtributoCategoriaResponse[]
  valorInicial?: VarianteProdutoLojaResponse
  onClose: () => void
}

interface FormValues {
  sku: string
  gtin?: string
  precoVenda: number
  precoPromocional?: number
  estoqueQuantidade?: number
  pesoKg?: number
  ncm?: string
  csosn?: string
  valoresAtributos?: Record<number, string>
}

/**
 * `valoresAtributos` é imutável depois de criada (o `PUT` do backend tecnicamente aceita
 * o campo, mas o Javadoc avisa que não deveria mudar) — só aparece na criação, um campo
 * por atributo `geraVariacao=true` da categoria do produto.
 */
export default function VarianteModal({ open, produtoId, atributosVariacao, valorInicial, onClose }: Props) {
  const { message } = App.useApp()
  const [form] = Form.useForm<FormValues>()
  const criar = useCriarVariante(produtoId)
  const atualizar = useAtualizarVariante(produtoId)
  const salvando = criar.isPending || atualizar.isPending

  useEffect(() => {
    if (open) {
      if (valorInicial) {
        form.setFieldsValue({
          sku: valorInicial.sku,
          gtin: valorInicial.gtin,
          precoVenda: valorInicial.precoVenda,
          precoPromocional: valorInicial.precoPromocional,
          estoqueQuantidade: valorInicial.estoqueQuantidade,
          pesoKg: valorInicial.pesoKg,
          ncm: valorInicial.ncm,
          csosn: valorInicial.csosn,
        })
      } else {
        form.resetFields()
      }
    }
  }, [open, valorInicial, form])

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        const request: VarianteProdutoLojaRequest = {
          sku: valores.sku,
          gtin: valores.gtin,
          precoVenda: valores.precoVenda,
          precoPromocional: valores.precoPromocional,
          estoqueQuantidade: valores.estoqueQuantidade,
          pesoKg: valores.pesoKg,
          ncm: valores.ncm,
          csosn: valores.csosn,
          ...(valorInicial
            ? {}
            : {
                valoresAtributos: atributosVariacao.map((a) => ({
                  atributoCategoriaId: a.id,
                  valor: valores.valoresAtributos?.[a.id] ?? '',
                })),
              }),
        }
        if (valorInicial) {
          await atualizar.mutateAsync({ id: valorInicial.id, request })
          message.success('Variante atualizada.')
        } else {
          await criar.mutateAsync(request)
          message.success('Variante criada.')
        }
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao salvar variante.')
      }
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar variante' : 'Nova variante'}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={salvando}
      destroyOnHidden
      width={560}
    >
      <Form form={form} name="varianteForm" layout="vertical">
        <Form.Item name="sku" label="SKU" rules={[{ required: true, message: 'Informe o SKU' }, { max: 50 }]}>
          <Input />
        </Form.Item>
        <Form.Item name="gtin" label="GTIN" rules={[{ max: 20 }]}>
          <Input />
        </Form.Item>
        <Form.Item name="precoVenda" label="Preço de venda" rules={[{ required: true, message: 'Informe o preço' }]}>
          <InputNumber style={{ width: '100%' }} min={0.01} precision={2} />
        </Form.Item>
        <Form.Item name="precoPromocional" label="Preço promocional">
          <InputNumber style={{ width: '100%' }} min={0} precision={2} />
        </Form.Item>
        <Form.Item name="estoqueQuantidade" label="Estoque inicial" tooltip={valorInicial ? 'Mude o estoque pelos pedidos/ajustes, não aqui depois de criada' : undefined}>
          <InputNumber style={{ width: '100%' }} min={0} disabled={!!valorInicial} />
        </Form.Item>
        <Form.Item name="pesoKg" label="Peso (kg)">
          <InputNumber style={{ width: '100%' }} min={0} precision={3} />
        </Form.Item>
        <Form.Item name="ncm" label="NCM" rules={[{ max: 8 }]}>
          <Input />
        </Form.Item>
        <Form.Item name="csosn" label="CSOSN" rules={[{ max: 3 }]}>
          <Input />
        </Form.Item>

        {!valorInicial && atributosVariacao.length > 0 && (
          <>
            <Text strong>Composição da variante</Text>
            {atributosVariacao.map((atributo) => (
              <Form.Item
                key={atributo.id}
                name={['valoresAtributos', atributo.id]}
                label={atributo.nome}
                rules={[{ required: atributo.obrigatorio, message: `Informe ${atributo.nome}` }]}
                style={{ marginTop: 8 }}
              >
                {atributo.tipo === 'LISTA' ? (
                  <Select options={atributo.opcoes.map((o) => ({ value: o.valor, label: o.valor }))} />
                ) : (
                  <Input />
                )}
              </Form.Item>
            ))}
          </>
        )}
        {valorInicial && valorInicial.valoresAtributos.length > 0 && (
          <Text type="secondary">
            Composição: {valorInicial.valoresAtributos.map((v) => `${v.atributoCategoriaNome}=${v.valor}`).join(', ')}
          </Text>
        )}
      </Form>
    </Modal>
  )
}
