import { useState } from 'react'
import { App, Button, Flex, Input, InputNumber, List, Modal, Popconfirm } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAdicionarOpcaoAtributo, useExcluirOpcaoAtributo } from '../queries'
import type { AtributoCategoriaResponse } from '../types'

interface Props {
  open: boolean
  atributo?: AtributoCategoriaResponse
  onClose: () => void
}

export default function GerenciarOpcoesModal({ open, atributo, onClose }: Props) {
  const { message } = App.useApp()
  const [valor, setValor] = useState('')
  const [ordem, setOrdem] = useState<number | undefined>(undefined)
  const adicionar = useAdicionarOpcaoAtributo()
  const excluir = useExcluirOpcaoAtributo()

  async function handleAdicionar() {
    if (!atributo || !valor.trim()) return
    try {
      await adicionar.mutateAsync({ atributoId: atributo.id, request: { valor, ordem } })
      setValor('')
      setOrdem(undefined)
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao adicionar opção.')
    }
  }

  async function handleExcluir(opcaoId: number) {
    if (!atributo) return
    try {
      await excluir.mutateAsync({ atributoId: atributo.id, opcaoId })
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir opção.')
    }
  }

  return (
    <Modal title={`Opções de "${atributo?.nome ?? ''}"`} open={open} onCancel={onClose} footer={null} destroyOnHidden>
      <List
        dataSource={atributo?.opcoes ?? []}
        locale={{ emptyText: 'Nenhuma opção cadastrada' }}
        renderItem={(opcao) => (
          <List.Item
            actions={[
              <Popconfirm key="excluir" title="Excluir esta opção?" onConfirm={() => handleExcluir(opcao.id)}>
                <Button size="small" danger>
                  Excluir
                </Button>
              </Popconfirm>,
            ]}
          >
            {opcao.valor} (ordem {opcao.ordem})
          </List.Item>
        )}
      />
      <Flex gap={8} style={{ marginTop: 16 }}>
        <Input placeholder="Nova opção" value={valor} onChange={(e) => setValor(e.target.value)} style={{ flex: 1 }} />
        <InputNumber placeholder="Ordem" value={ordem} onChange={(v) => setOrdem(v ?? undefined)} style={{ width: 90 }} />
        <Button icon={<PlusOutlined />} loading={adicionar.isPending} onClick={handleAdicionar}>
          Adicionar
        </Button>
      </Flex>
    </Modal>
  )
}
