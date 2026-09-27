import { useState } from 'react'

interface ComPrincipal {
  principal: boolean
}

/**
 * Estado compartilhado das 3 listas filhas de toda especialização de Pessoa
 * (endereços/contatos/telefones — Cliente, Fornecedor, Colaborador) — cada uma
 * é editada via modal (não inline na tabela, ver CLAUDE.md do frontend), então
 * a lógica de "qual índice está sendo editado" / "adicionar novo" / "remover"
 * se repete idêntica nas 3 listas de cada entidade. Também garante no máximo
 * um item `principal=true` por lista (desmarca os demais ao marcar um novo) —
 * UX de conveniência, não uma regra que o backend valida.
 */
export function useEditableList<T extends ComPrincipal>(
  itens: T[],
  onChange: (itens: T[]) => void,
) {
  const [modalAberto, setModalAberto] = useState(false)
  const [indiceEdicao, setIndiceEdicao] = useState<number | null>(null)

  const abrirNovo = () => {
    setIndiceEdicao(null)
    setModalAberto(true)
  }

  const abrirEdicao = (indice: number) => {
    setIndiceEdicao(indice)
    setModalAberto(true)
  }

  const fechar = () => setModalAberto(false)

  const salvar = (item: T) => {
    let novaLista = indiceEdicao === null ? [...itens, item] : itens.map((it, i) => (i === indiceEdicao ? item : it))
    if (item.principal) {
      novaLista = novaLista.map((it, i) => {
        const éEsteItem = indiceEdicao === null ? i === novaLista.length - 1 : i === indiceEdicao
        return éEsteItem ? it : { ...it, principal: false }
      })
    }
    onChange(novaLista)
    setModalAberto(false)
  }

  const remover = (indice: number) => {
    onChange(itens.filter((_, i) => i !== indice))
  }

  return {
    modalAberto,
    valorEdicao: indiceEdicao !== null ? itens[indiceEdicao] : undefined,
    abrirNovo,
    abrirEdicao,
    fechar,
    salvar,
    remover,
  }
}
