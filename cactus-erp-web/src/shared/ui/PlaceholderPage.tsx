import type { ReactNode } from 'react'
import { Card, Flex, Typography } from 'antd'
import { ToolOutlined } from '@ant-design/icons'

const { Title, Text } = Typography

interface PlaceholderPageProps {
  titulo: string
  icone?: ReactNode
}

/** Tela genérica "em construção" pros 6 módulos de negócio nesta entrega
 *  (auth + shell) — cada módulo ganha telas reais nas próximas entregas. */
export default function PlaceholderPage({ titulo, icone }: PlaceholderPageProps) {
  return (
    <Card>
      <Flex vertical align="center" justify="center" gap={12} style={{ padding: '48px 0' }}>
        <div style={{ fontSize: 40, color: '#8cc63f' }}>{icone ?? <ToolOutlined />}</div>
        <Title level={4} style={{ margin: 0 }}>
          {titulo}
        </Title>
        <Text type="secondary">Módulo em construção — chega numa próxima entrega.</Text>
      </Flex>
    </Card>
  )
}
