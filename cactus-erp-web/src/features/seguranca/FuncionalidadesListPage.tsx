import { useState } from 'react'
import { Card, Flex, Select, Table, Typography } from 'antd'
import SituacaoTag from '@shared/ui/SituacaoTag'
import { useFuncionalidades, useTodosModulos } from './queries'
import type { FuncionalidadeResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 20

/**
 * Só leitura — catálogo semeado via migration a cada endpoint protegido novo
 * (ver `FuncionalidadeController`). Existe como tela pra dar visibilidade
 * total do que existe no sistema pra auditar/entender o que um Papel pode
 * conceder — a edição de verdade acontece na aba Funcionalidades de Papel.
 */
export default function FuncionalidadesListPage() {
  const [moduloId, setModuloId] = useState<number | undefined>(undefined)
  const [pagina, setPagina] = useState(0)

  const { data: modulos } = useTodosModulos()
  const { data, isLoading, isPlaceholderData } = useFuncionalidades(moduloId, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Funcionalidades
      </Title>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <Select
            allowClear
            style={{ width: 260 }}
            placeholder="Filtrar por módulo"
            options={modulos?.map((m) => ({ value: m.id, label: m.nome }))}
            onChange={(valor) => {
              setModuloId(valor)
              setPagina(0)
            }}
          />
        </Flex>

        <Table<FuncionalidadeResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} funcionalidade(s)`,
          }}
          columns={[
            { title: 'Código', dataIndex: 'codigo' },
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Módulo', dataIndex: 'moduloCodigo' },
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
