import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Alert, Button, Flex, Table, Tag, Tooltip, Typography } from 'antd'
import { DownloadOutlined, PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useBaixarUltimaVersao, useDocumentos } from '@features/ged/queries'
import type { DocumentoResponse } from '@features/ged/types'
import DocumentoEntidadeUploadModal from './DocumentoEntidadeUploadModal'

const { Title } = Typography
const TAMANHO_PAGINA = 5

interface DocumentosEntidadeSectionProps {
  entidadeTipo: string
  /** `undefined` enquanto o cadastro pai ainda não foi salvo — sem id real
   * pra vincular, a seção mostra um aviso em vez de listar/permitir anexar
   * (mesmo gate já usado nas sub-abas de Empresa/OsAbertura). */
  entidadeId: number | undefined
}

/**
 * Seção reaproveitável de "Documentos anexados" — usa o vínculo genérico
 * `entidadeTipo`/`entidadeId` já modelado no GED (`gestao-documentos-service`)
 * desde a Fase 4, nunca consumido por nenhum outro módulo até agora (ver
 * CLAUDE.md raiz e `cactus-erp-web/CLAUDE.md`, Entrega 11, pendência 16).
 * Fica em `shared/` de propósito — é o mesmo padrão de reaproveitamento já
 * usado pra `ProdutoResumoCell`/`useProdutoResumo` (Entrega 8): qualquer
 * módulo futuro com cadastro próprio (Empresa, OS, Fornecedor...) pode
 * embutir a mesma seção só passando `entidadeTipo`/`entidadeId`.
 *
 * O detalhe completo (histórico de versões, nova versão, excluir) continua
 * vivendo só em `DocumentoDetailPage` (features/ged) — o botão "Detalhes"
 * navega pra lá em vez de duplicar essa UI aqui.
 */
export default function DocumentosEntidadeSection({ entidadeTipo, entidadeId }: DocumentosEntidadeSectionProps) {
  const navigate = useNavigate()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'GED_GERENCIAR')
  const [modalAberto, setModalAberto] = useState(false)
  const [pagina, setPagina] = useState(0)

  const { data, isLoading, isPlaceholderData } = useDocumentos({ entidadeTipo, entidadeId }, pagina, TAMANHO_PAGINA)
  const baixarUltima = useBaixarUltimaVersao()

  if (entidadeId === undefined) {
    return <Alert type="info" showIcon message="Salve o cadastro primeiro para anexar documentos." />
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={5} style={{ margin: 0 }}>
          Documentos anexados
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar documentos'}>
          <Button type="primary" icon={<PlusOutlined />} disabled={!podeGerenciar} onClick={() => setModalAberto(true)}>
            Adicionar Documento
          </Button>
        </Tooltip>
      </Flex>

      <Table<DocumentoResponse>
        rowKey="id"
        size="small"
        loading={isLoading || isPlaceholderData}
        dataSource={data?.conteudo ?? []}
        pagination={{
          current: pagina + 1,
          pageSize: TAMANHO_PAGINA,
          total: data?.totalRegistros ?? 0,
          onChange: (novaPagina) => setPagina(novaPagina - 1),
          showTotal: (total) => `${total} documento(s)`,
        }}
        locale={{ emptyText: 'Nenhum documento anexado' }}
        columns={[
          { title: 'Nome', dataIndex: 'nome' },
          { title: 'Tipo', dataIndex: 'tipoDocumentoNome' },
          { title: 'Versões', key: 'versoes', render: (_, d) => d.versoes.length },
          {
            title: 'Assinado',
            dataIndex: 'assinado',
            render: (v: boolean) => (v ? <Tag color="success">Sim</Tag> : '—'),
          },
          {
            title: 'Ações',
            key: 'acoes',
            render: (_, documento) => (
              <Flex gap={8}>
                <Button
                  size="small"
                  icon={<DownloadOutlined />}
                  loading={baixarUltima.isPending}
                  onClick={() => baixarUltima.mutate({ id: documento.id, nomeSugerido: documento.nome })}
                >
                  Baixar
                </Button>
                <Button size="small" onClick={() => navigate(`/ged/documentos/${documento.id}`)}>
                  Detalhes
                </Button>
              </Flex>
            ),
          },
        ]}
      />

      <DocumentoEntidadeUploadModal
        open={modalAberto}
        entidadeTipo={entidadeTipo}
        entidadeId={entidadeId}
        onClose={() => setModalAberto(false)}
      />
    </Flex>
  )
}
