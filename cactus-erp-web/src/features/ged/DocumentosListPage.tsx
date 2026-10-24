import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Input, InputNumber, Select, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import SituacaoTag from '@shared/ui/SituacaoTag'
import ExportarRelatorioButtons from '@shared/ui/ExportarRelatorioButtons'
import { exportarDocumentosCadastro, type DocumentosCadastroFiltro } from './api'
import { useDocumentos, useTodasPastas, useTodosTiposDocumento } from './queries'
import type { DocumentoResponse, DocumentosFiltro } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function DocumentosListPage() {
  const navigate = useNavigate()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'GED_GERENCIAR')
  const [filtro, setFiltro] = useState<DocumentosFiltro>({})
  const [pagina, setPagina] = useState(0)
  const [filtroRelatorio, setFiltroRelatorio] = useState<DocumentosCadastroFiltro>({})

  const { data, isLoading, isPlaceholderData } = useDocumentos(filtro, pagina, TAMANHO_PAGINA)
  const { data: pastas } = useTodasPastas()
  const { data: tipos } = useTodosTiposDocumento()

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Documentos
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar documentos'}>
          <Button type="primary" icon={<PlusOutlined />} disabled={!podeGerenciar} onClick={() => navigate('/ged/documentos/novo')}>
            Novo Documento
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <Input
            placeholder="Filtrar por tipo de entidade (ex: PRODUTO)"
            allowClear
            style={{ maxWidth: 280 }}
            onChange={(e) => {
              setFiltro((f) => ({ ...f, entidadeTipo: e.target.value || undefined }))
              setPagina(0)
            }}
          />
          <InputNumber<number>
            placeholder="Id da entidade"
            style={{ width: 160 }}
            onChange={(v) => {
              setFiltro((f) => ({ ...f, entidadeId: v ?? undefined }))
              setPagina(0)
            }}
          />
        </Flex>

        <Flex gap={12} wrap="wrap" align="center" style={{ marginBottom: 16 }}>
          <span>Relatório:</span>
          <Select
            allowClear
            style={{ width: 200 }}
            placeholder="Todas as pastas"
            options={pastas?.map((p) => ({ value: p.id, label: p.nome }))}
            onChange={(v: number | undefined) => setFiltroRelatorio((f) => ({ ...f, pastaId: v }))}
          />
          <Select
            allowClear
            style={{ width: 200 }}
            placeholder="Todos os tipos"
            options={tipos?.map((t) => ({ value: t.id, label: t.nome }))}
            onChange={(v: number | undefined) => setFiltroRelatorio((f) => ({ ...f, tipoDocumentoId: v }))}
          />
          <Select
            allowClear
            style={{ width: 160 }}
            placeholder="Todas as situações"
            options={[
              { value: 'ATIVO', label: 'Ativo' },
              { value: 'INATIVO', label: 'Inativo' },
            ]}
            onChange={(v: 'ATIVO' | 'INATIVO' | undefined) => setFiltroRelatorio((f) => ({ ...f, situacao: v }))}
          />
          <ExportarRelatorioButtons onExportar={(formato) => exportarDocumentosCadastro(filtroRelatorio, formato)} />
        </Flex>

        <Table<DocumentoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(documento) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/ged/documentos/${documento.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} documento(s)`,
          }}
          columns={[
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Tipo', dataIndex: 'tipoDocumentoNome' },
            { title: 'Pasta', dataIndex: 'pastaNome', render: (v) => v || '— (raiz)' },
            { title: 'Versões', key: 'versoes', render: (_, d) => d.versoes.length },
            {
              title: 'Assinado',
              dataIndex: 'assinado',
              render: (v: boolean) => (v ? <Tag color="success">Sim</Tag> : '—'),
            },
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
