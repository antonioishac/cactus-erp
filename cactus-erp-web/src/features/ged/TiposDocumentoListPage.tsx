import { useState } from 'react'
import { Button, Card, Flex, Table, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import SituacaoTag from '@shared/ui/SituacaoTag'
import TipoDocumentoModal from './modals/TipoDocumentoModal'
import { useTiposDocumento } from './queries'
import type { TipoDocumentoResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function TiposDocumentoListPage() {
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'GED_GERENCIAR')
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)
  const [tipoEmEdicao, setTipoEmEdicao] = useState<TipoDocumentoResponse | undefined>(undefined)

  const { data, isLoading, isPlaceholderData } = useTiposDocumento(pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Tipos de Documento
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar tipos de documento'}>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            disabled={!podeGerenciar}
            onClick={() => {
              setTipoEmEdicao(undefined)
              setModalAberto(true)
            }}
          >
            Novo Tipo
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Table<TipoDocumentoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(tipo) => ({
            style: podeGerenciar ? { cursor: 'pointer' } : undefined,
            onClick: () => {
              if (!podeGerenciar) return
              setTipoEmEdicao(tipo)
              setModalAberto(true)
            },
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} tipo(s) de documento`,
          }}
          columns={[
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Tamanho máximo', dataIndex: 'tamanhoMaximoMb', render: (v: number) => `${v} MB` },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (situacao) => <SituacaoTag situacao={situacao} />,
            },
          ]}
        />
      </Card>

      <TipoDocumentoModal open={modalAberto} valorInicial={tipoEmEdicao} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
