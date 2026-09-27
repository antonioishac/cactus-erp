import { Card, Col, Flex, Row, Skeleton, Statistic, Table, Typography } from 'antd'
import { useAuth } from '@features/auth/useAuth'
import { usePosicaoCaixa } from './queries'
import { TIPO_CONTA_BANCARIA_LABEL } from './types'
import type { PosicaoCaixaTipoResponse } from './types'

const { Title } = Typography

/** Sem snapshot persistido — calculado sob demanda a partir do `saldoAtual` real de cada
 * conta ativa (mesmo critério de `CurvaAbcService`/`FluxoCaixaService`). */
export default function PosicaoCaixaPage() {
  const { empresaAtivaId } = useAuth()
  const { data, isLoading } = usePosicaoCaixa(empresaAtivaId ?? undefined)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Posição de Caixa
      </Title>

      {isLoading || !data ? (
        <Card>
          <Skeleton active paragraph={{ rows: 4 }} />
        </Card>
      ) : (
        <>
          <Card>
            <Statistic title="Saldo geral consolidado" value={data.saldoGeral} precision={2} prefix="R$" />
          </Card>
          <Card title="Por tipo de conta">
            <Table<PosicaoCaixaTipoResponse>
              rowKey="tipo"
              dataSource={data.porTipo}
              pagination={false}
              columns={[
                { title: 'Tipo', dataIndex: 'tipo', render: (v: keyof typeof TIPO_CONTA_BANCARIA_LABEL) => TIPO_CONTA_BANCARIA_LABEL[v] },
                { title: 'Qtd. contas', dataIndex: 'quantidadeContas', align: 'right' },
                { title: 'Saldo total', dataIndex: 'saldoTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
              ]}
            />
          </Card>
          <Row gutter={12}>
            {data.porTipo.map((p) => (
              <Col span={6} key={p.tipo}>
                <Card>
                  <Statistic title={TIPO_CONTA_BANCARIA_LABEL[p.tipo]} value={p.saldoTotal} precision={2} prefix="R$" />
                </Card>
              </Col>
            ))}
          </Row>
        </>
      )}
    </Flex>
  )
}
