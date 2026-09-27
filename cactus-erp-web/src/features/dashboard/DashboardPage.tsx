import { Col, Row, Skeleton, Typography } from 'antd'
import {
  BarChartOutlined,
  DatabaseOutlined,
  ShoppingCartOutlined,
  TeamOutlined,
} from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { useMe } from '@features/auth/queries'
import PlaceholderKpiCard from './PlaceholderKpiCard'

const { Title, Text } = Typography

export default function DashboardPage() {
  const { login } = useAuth()
  const { data: me, isLoading } = useMe()

  return (
    <div>
      <Title level={3} style={{ marginTop: 0 }}>
        Bem-vindo{login ? `, ${login}` : ''}
      </Title>
      {isLoading ? (
        <Skeleton.Input active size="small" />
      ) : (
        <Text type="secondary">
          Empresa ativa: <strong>{me?.empresaId ?? '—'}</strong>
        </Text>
      )}

      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <PlaceholderKpiCard titulo="Clientes ativos" valor={128} icone={<TeamOutlined />} />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <PlaceholderKpiCard
            titulo="Itens em estoque"
            valor="3.482"
            icone={<DatabaseOutlined />}
            cor="#1677ff"
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <PlaceholderKpiCard
            titulo="Pedidos de compra abertos"
            valor={17}
            icone={<ShoppingCartOutlined />}
            cor="#fa8c16"
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <PlaceholderKpiCard
            titulo="Faturamento do mês"
            valor="R$ 0,00"
            icone={<BarChartOutlined />}
            cor="#722ed1"
          />
        </Col>
      </Row>
    </div>
  )
}
