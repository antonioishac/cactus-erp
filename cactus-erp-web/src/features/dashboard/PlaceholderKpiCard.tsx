import type { ReactNode } from 'react'
import { Card, Flex, Statistic, Tag } from 'antd'

interface PlaceholderKpiCardProps {
  titulo: string
  valor: string | number
  icone: ReactNode
  cor?: string
}

/**
 * Card de KPI com dado de EXEMPLO — o módulo de BI real (business-intelligence-service)
 * ainda não está integrado a nenhuma tela. Marcado explicitamente como placeholder
 * (tag "Exemplo") pra não passar a impressão de dado real.
 */
export default function PlaceholderKpiCard({
  titulo,
  valor,
  icone,
  cor = '#8cc63f',
}: PlaceholderKpiCardProps) {
  return (
    <Card>
      <Flex justify="space-between" align="flex-start">
        <Statistic title={titulo} value={valor} styles={{ content: { fontSize: 26 } }} />
        <div style={{ fontSize: 28, color: cor }}>{icone}</div>
      </Flex>
      <Tag color="default" style={{ marginTop: 8, whiteSpace: 'normal', maxWidth: '100%' }}>
        Exemplo — módulo BI ainda não integrado
      </Tag>
    </Card>
  )
}
