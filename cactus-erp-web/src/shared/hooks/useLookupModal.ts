import { useState } from 'react'

/**
 * Estado do modal de gerenciamento de um lookup simples (Cargo/Setor/Contador
 * — nome+alguns campos+situação, CRUD raso). Diferente de `useEditableList`
 * (listas filhas em memória, sem id real, editadas por índice): aqui cada
 * item já tem um `id` de verdade vindo do backend, então criar/editar são
 * duas mutações HTTP separadas (`useCriarX`/`useAtualizarX`), não uma
 * atualização de array local — este hook só controla "abrir novo" vs.
 * "editar item X", quem persiste é o componente que o usa.
 */
export function useLookupModal<T extends { id: number }>() {
  const [open, setOpen] = useState(false)
  const [itemEmEdicao, setItemEmEdicao] = useState<T | null>(null)

  const abrirNovo = () => {
    setItemEmEdicao(null)
    setOpen(true)
  }

  const abrirEdicao = (item: T) => {
    setItemEmEdicao(item)
    setOpen(true)
  }

  const fechar = () => {
    setOpen(false)
    setItemEmEdicao(null)
  }

  return { open, itemEmEdicao, abrirNovo, abrirEdicao, fechar }
}
