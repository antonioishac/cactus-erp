import { useState } from 'react'
import { App, Alert, Button, Card, DatePicker, Flex, Tooltip, Typography } from 'antd'
import { PlayCircleOutlined } from '@ant-design/icons'
import dayjs, { type Dayjs } from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useExecutarEtl } from './queries'

const { Title, Paragraph } = Typography

/**
 * Disparo manual do ETL — além da execução agendada diária às 2h (ver
 * `EtlService.java`). Idempotente: pode ser rodado mais de uma vez pro mesmo
 * dia sem duplicar nada (delete-e-reinsere por chave, ver CLAUDE.md raiz).
 */
export default function EtlExecucaoPage() {
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeExecutar = hasAuthority(authorities, 'BI_GERENCIAR')
  const [dataReferencia, setDataReferencia] = useState<Dayjs>(dayjs())
  const executar = useExecutarEtl()

  async function handleExecutar() {
    try {
      await executar.mutateAsync({ dataReferencia: dataReferencia.format('YYYY-MM-DD') })
      message.success('ETL executado com sucesso — indicadores atualizados.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao executar o ETL.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Execução do ETL
      </Title>

      <Card>
        <Alert
          type="info"
          showIcon
          style={{ marginBottom: 16 }}
          title="O que este ETL faz"
          description="Atualiza as dimensões (produto/fornecedor/depósito) e regenera os snapshots de Valor de Estoque, Curva ABC e Compras por Fornecedor pra data de referência escolhida. Roda automaticamente todo dia às 2h — este disparo manual é útil pra ver os indicadores atualizados na hora, sem esperar o agendamento."
        />

        <Paragraph>Data de referência</Paragraph>
        <Flex gap={12} align="center">
          <DatePicker
            value={dataReferencia}
            format="DD/MM/YYYY"
            allowClear={false}
            onChange={(data) => setDataReferencia(data ?? dayjs())}
          />
          <Tooltip title={podeExecutar ? undefined : 'Você não tem permissão para executar o ETL'}>
            <Button
              type="primary"
              icon={<PlayCircleOutlined />}
              disabled={!podeExecutar}
              loading={executar.isPending}
              onClick={handleExecutar}
            >
              Executar ETL
            </Button>
          </Tooltip>
        </Flex>
      </Card>
    </Flex>
  )
}
