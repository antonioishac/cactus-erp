import { useState } from 'react'
import { Card, Flex, Table, Typography } from 'antd'
import SituacaoTag from '@shared/ui/SituacaoTag'
import { useModulos } from './queries'
import type { ModuloResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 20

/** Só leitura — 1 módulo por serviço real implantado, semeado via migration (ver `ModuloController`). */
export default function ModulosListPage() {
  const [pagina, setPagina] = useState(0)
  const { data, isLoading, isPlaceholderData } = useModulos(pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Módulos
      </Title>

      <Card>
        <Table<ModuloResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} módulo(s)`,
          }}
          columns={[
            { title: 'Código', dataIndex: 'codigo' },
            { title: 'Nome', dataIndex: 'nome' },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (situacao) => <SituacaoTag situacao={situacao} />,
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
