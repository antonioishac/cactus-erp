import { useState } from 'react'
import { App, Button, Empty, Flex, Table } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import RegimeTributarioModal from '../modals/RegimeTributarioModal'
import { useRegimeTributario, useRegistrarRegimeTributario } from '../queries'
import type { EmpresaRegimeTributarioRequest, EmpresaRegimeTributarioResponse } from '../types'

interface RegimeTributarioTabProps {
  empresaId: number
}

const LABEL_CRT: Record<string, string> = {
  '1': '1 — Simples Nacional',
  '2': '2 — Simples Nacional (excesso de sublimite)',
  '3': '3 — Regime Normal',
}

/**
 * Histórico append-only — sem editar/remover (o backend só expõe `POST`, ver
 * `EmpresaController.registrarRegimeTributario`). CRT pode mudar de um ano
 * pro outro; manter o histórico é o que permite recalcular imposto
 * retroativo com o regime correto de cada ano.
 */
export default function RegimeTributarioTab({ empresaId }: RegimeTributarioTabProps) {
  const { message } = App.useApp()
  const [modalAberto, setModalAberto] = useState(false)
  const { data: historico, isLoading } = useRegimeTributario(empresaId)
  const registrar = useRegistrarRegimeTributario(empresaId)

  async function handleRegistrar(request: EmpresaRegimeTributarioRequest) {
    try {
      await registrar.mutateAsync(request)
      message.success('Regime tributário registrado.')
      setModalAberto(false)
    } catch (erro) {
      const mensagem = erro instanceof Error ? erro.message : 'Falha ao registrar regime tributário.'
      message.error(mensagem)
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="flex-end">
        <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalAberto(true)}>
          Registrar regime tributário
        </Button>
      </Flex>

      <Table<EmpresaRegimeTributarioResponse>
        rowKey="id"
        loading={isLoading}
        dataSource={historico ? [...historico].sort((a, b) => b.anoCalendario - a.anoCalendario) : []}
        pagination={false}
        locale={{ emptyText: <Empty description="Nenhum regime tributário registrado ainda" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
        columns={[
          { title: 'Ano-calendário', dataIndex: 'anoCalendario' },
          { title: 'CRT', dataIndex: 'crt', render: (v: string) => LABEL_CRT[v] ?? v },
        ]}
      />

      <RegimeTributarioModal
        open={modalAberto}
        salvando={registrar.isPending}
        onCancel={() => setModalAberto(false)}
        onSalvar={handleRegistrar}
      />
    </Flex>
  )
}
