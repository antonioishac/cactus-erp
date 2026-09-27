import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Descriptions, Flex, Popconfirm, Skeleton, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { DownloadOutlined, PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import NovaVersaoModal from './modals/NovaVersaoModal'
import { useBaixarUltimaVersao, useBaixarVersao, useDocumento, useExcluirDocumento } from './queries'
import { TIPO_ACAO_VERSAO_LABEL } from './types'
import type { DocumentoVersaoResponse } from './types'

const { Title } = Typography

function formatarTamanho(bytes?: number): string {
  if (bytes === undefined) return '—'
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / (1024 * 1024)).toFixed(2)} MB`
}

/**
 * Não é um "form de edição" — não existe `PUT` de metadado pra Documento na
 * API (ver Javadoc de `DocumentoController`). O que existe aqui é consulta +
 * ações: baixar, enviar nova versão, excluir (lógico).
 */
export default function DocumentoDetailPage() {
  const { id } = useParams<{ id: string }>()
  const documentoId = Number(id)
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'GED_GERENCIAR')

  const { data: documento, isLoading } = useDocumento(documentoId)
  const excluir = useExcluirDocumento()
  const baixarUltima = useBaixarUltimaVersao()
  const baixarVersaoEspecifica = useBaixarVersao()
  const [modalVersaoAberto, setModalVersaoAberto] = useState(false)

  async function handleBaixarUltima() {
    if (!documento) return
    try {
      await baixarUltima.mutateAsync({ id: documento.id, nomeSugerido: documento.nome })
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao baixar documento.')
    }
  }

  async function handleBaixarVersao(versao: DocumentoVersaoResponse) {
    if (!documento) return
    try {
      await baixarVersaoEspecifica.mutateAsync({
        id: documento.id,
        versaoId: versao.id,
        nomeSugerido: versao.nomeArquivoOriginal ?? documento.nome,
      })
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao baixar versão.')
    }
  }

  async function handleExcluir() {
    if (!documento) return
    try {
      await excluir.mutateAsync(documento.id)
      message.success('Documento excluído.')
      navigate('/ged/documentos')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir documento.')
    }
  }

  if (isLoading || !documento) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 8 }} />
      </Card>
    )
  }

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate('/ged/documentos')}>Documentos</a> },
          { title: documento.nome },
        ]}
      />

      <Card>
        <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
          <Space size="middle">
            <Title level={4} style={{ margin: 0 }}>
              {documento.nome}
            </Title>
            <Tag color={documento.situacao === 'ATIVO' ? 'success' : 'default'}>
              {documento.situacao === 'ATIVO' ? 'Ativo' : 'Inativo'}
            </Tag>
            {documento.assinado && <Tag color="blue">Assinado</Tag>}
          </Space>
          <Space>
            <Button icon={<DownloadOutlined />} loading={baixarUltima.isPending} onClick={handleBaixarUltima}>
              Baixar última versão
            </Button>
            {podeGerenciar && documento.situacao === 'ATIVO' && (
              <>
                <Tooltip title={documento.permiteAlteracao ? undefined : 'Este documento não permite nova versão'}>
                  <Button icon={<PlusOutlined />} disabled={!documento.permiteAlteracao} onClick={() => setModalVersaoAberto(true)}>
                    Nova Versão
                  </Button>
                </Tooltip>
                <Tooltip title={documento.permiteExclusao ? undefined : 'Este documento não permite exclusão'}>
                  <Popconfirm title="Excluir este documento?" description="Fica marcado como inativo, o histórico de versões é preservado." onConfirm={handleExcluir} disabled={!documento.permiteExclusao}>
                    <Button danger disabled={!documento.permiteExclusao} loading={excluir.isPending}>
                      Excluir
                    </Button>
                  </Popconfirm>
                </Tooltip>
              </>
            )}
          </Space>
        </Flex>

        <Descriptions bordered size="small" column={2} style={{ marginTop: 16 }}>
          <Descriptions.Item label="Tipo de documento">{documento.tipoDocumentoNome}</Descriptions.Item>
          <Descriptions.Item label="Pasta">{documento.pastaNome || '— (raiz)'}</Descriptions.Item>
          <Descriptions.Item label="Descrição" span={2}>{documento.descricao || '—'}</Descriptions.Item>
          <Descriptions.Item label="Palavras-chave" span={2}>{documento.palavrasChave || '—'}</Descriptions.Item>
          <Descriptions.Item label="Vínculo">
            {documento.entidadeTipo ? `${documento.entidadeTipo} #${documento.entidadeId}` : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Fim de vigência">
            {documento.dataFimVigencia ? dayjs(documento.dataFimVigencia).format('DD/MM/YYYY') : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Permite exclusão">{documento.permiteExclusao ? 'Sim' : 'Não'}</Descriptions.Item>
          <Descriptions.Item label="Permite nova versão">{documento.permiteAlteracao ? 'Sim' : 'Não'}</Descriptions.Item>
        </Descriptions>
      </Card>

      <Card>
        <Title level={5} style={{ marginTop: 0 }}>
          Histórico de versões
        </Title>
        <Table<DocumentoVersaoResponse>
          rowKey="id"
          dataSource={documento.versoes}
          pagination={false}
          columns={[
            { title: '#', dataIndex: 'numeroVersao', width: 60 },
            {
              title: 'Ação',
              dataIndex: 'tipoAcao',
              render: (v: keyof typeof TIPO_ACAO_VERSAO_LABEL) => TIPO_ACAO_VERSAO_LABEL[v],
            },
            { title: 'Arquivo', dataIndex: 'nomeArquivoOriginal', render: (v) => v || '—' },
            { title: 'Tamanho', dataIndex: 'tamanhoBytes', render: (v?: number) => formatarTamanho(v) },
            { title: 'Observação', dataIndex: 'observacao', render: (v) => v || '—' },
            { title: 'Data', dataIndex: 'dataCriacao', render: (v: string) => dayjs(v).format('DD/MM/YYYY HH:mm') },
            {
              title: 'Ações',
              key: 'acoes',
              render: (_, versao) =>
                versao.tipoAcao !== 'EXCLUSAO' ? (
                  <Button
                    size="small"
                    icon={<DownloadOutlined />}
                    loading={baixarVersaoEspecifica.isPending}
                    onClick={() => handleBaixarVersao(versao)}
                  >
                    Baixar
                  </Button>
                ) : (
                  '—'
                ),
            },
          ]}
        />
      </Card>

      <NovaVersaoModal open={modalVersaoAberto} documentoId={documento.id} onClose={() => setModalVersaoAberto(false)} />
    </Flex>
  )
}
