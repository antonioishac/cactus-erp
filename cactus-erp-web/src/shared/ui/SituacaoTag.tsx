import { Tag } from 'antd'
import type { Situacao } from '@shared/domain/situacao'
import { SITUACAO_LABEL } from '@shared/domain/situacao'

/** Tag colorida consistente pra `Situacao` em toda listagem/detalhe do app. */
export default function SituacaoTag({ situacao }: { situacao: Situacao }) {
  return (
    <Tag color={situacao === 'ATIVO' ? 'success' : 'default'}>{SITUACAO_LABEL[situacao]}</Tag>
  )
}
