import { useState } from 'react'
import { App, Button, Card, Flex, Select, Table, Tag, Tabs, Tooltip, Typography } from 'antd'
import { DownloadOutlined, PlusOutlined, UploadOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import GerarRemessaModal from './modals/GerarRemessaModal'
import ImportarRetornoModal from './modals/ImportarRetornoModal'
import { useBaixarArquivoRemessa, useContasBancariasAtivas, useRemessasBancarias, useRetornosBancarios } from './queries'
import type { RemessaBancariaItemResponse, RemessaBancariaResponse, RetornoBancarioItemResponse, RetornoBancarioResponse } from './types'

const { Title } = Typography

export default function EdiBancarioPage() {
  const { message } = App.useApp()
  const { empresaAtivaId, authorities } = useAuth()
  const podeMovimentar = hasAuthority(authorities, 'CAIXA_MOVIMENTAR')
  const [contaBancariaId, setContaBancariaId] = useState<number | undefined>(undefined)
  const [modalGerarAberto, setModalGerarAberto] = useState(false)
  const [modalImportarAberto, setModalImportarAberto] = useState(false)

  const { data: contasBancarias } = useContasBancariasAtivas(empresaAtivaId ?? undefined)
  const { data: remessas, isLoading: carregandoRemessas } = useRemessasBancarias(contaBancariaId, 0, 20)
  const { data: retornos, isLoading: carregandoRetornos } = useRetornosBancarios(contaBancariaId, 0, 20)
  const baixarArquivo = useBaixarArquivoRemessa()

  async function handleBaixarArquivo(id: number) {
    try {
      await baixarArquivo.mutateAsync(id)
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao baixar arquivo da remessa.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
        <Title level={3} style={{ margin: 0 }}>
          EDI Bancário
        </Title>
        <Select
          allowClear
          style={{ width: 280 }}
          placeholder="Todas as contas"
          options={contasBancarias?.map((c) => ({ value: c.id, label: c.nome }))}
          value={contaBancariaId}
          onChange={setContaBancariaId}
        />
      </Flex>

      <Card>
        <Tabs
          items={[
            {
              key: 'remessas',
              label: 'Remessas',
              children: (
                <Flex vertical gap={12}>
                  <Flex justify="flex-end">
                    <Tooltip title={!contaBancariaId ? 'Selecione uma conta bancária' : !podeMovimentar ? 'Sem permissão' : undefined}>
                      <Button
                        type="primary"
                        icon={<PlusOutlined />}
                        disabled={!contaBancariaId || !podeMovimentar}
                        onClick={() => setModalGerarAberto(true)}
                      >
                        Gerar Remessa
                      </Button>
                    </Tooltip>
                  </Flex>
                  <Table<RemessaBancariaResponse>
                    rowKey="id"
                    loading={carregandoRemessas}
                    dataSource={remessas?.conteudo ?? []}
                    pagination={false}
                    expandable={{
                      expandedRowRender: (remessa) => (
                        <Table<RemessaBancariaItemResponse>
                          rowKey="id"
                          size="small"
                          dataSource={remessa.itens}
                          pagination={false}
                          columns={[
                            { title: 'Nosso número', dataIndex: 'nossoNumero' },
                            { title: 'Documento', dataIndex: 'numeroDocumento', render: (v?: string) => v || '—' },
                            { title: 'Sacado', dataIndex: 'sacadoNome' },
                            { title: 'Vencimento', dataIndex: 'dataVencimento', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
                            { title: 'Valor', dataIndex: 'valorTitulo', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
                          ]}
                        />
                      ),
                    }}
                    columns={[
                      { title: 'Nº sequencial', dataIndex: 'numeroSequencialArquivo' },
                      { title: 'Data geração', dataIndex: 'dataGeracao', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
                      { title: 'Qtd. títulos', dataIndex: 'quantidadeTitulos', align: 'right' },
                      { title: 'Valor total', dataIndex: 'valorTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
                      {
                        title: 'Ações',
                        key: 'acoes',
                        render: (_, remessa) => (
                          <Button
                            size="small"
                            icon={<DownloadOutlined />}
                            loading={baixarArquivo.isPending}
                            onClick={() => handleBaixarArquivo(remessa.id)}
                          >
                            Baixar arquivo
                          </Button>
                        ),
                      },
                    ]}
                  />
                </Flex>
              ),
            },
            {
              key: 'retornos',
              label: 'Retornos',
              children: (
                <Flex vertical gap={12}>
                  <Flex justify="flex-end">
                    <Tooltip title={!contaBancariaId ? 'Selecione uma conta bancária' : !podeMovimentar ? 'Sem permissão' : undefined}>
                      <Button
                        type="primary"
                        icon={<UploadOutlined />}
                        disabled={!contaBancariaId || !podeMovimentar}
                        onClick={() => setModalImportarAberto(true)}
                      >
                        Importar Retorno
                      </Button>
                    </Tooltip>
                  </Flex>
                  <Table<RetornoBancarioResponse>
                    rowKey="id"
                    loading={carregandoRetornos}
                    dataSource={retornos?.conteudo ?? []}
                    pagination={false}
                    expandable={{
                      expandedRowRender: (retorno) => (
                        <Table<RetornoBancarioItemResponse>
                          rowKey="id"
                          size="small"
                          dataSource={retorno.itens}
                          pagination={false}
                          columns={[
                            { title: 'Nosso número', dataIndex: 'nossoNumero', render: (v?: string) => v || '—' },
                            { title: 'Documento', dataIndex: 'numeroDocumento', render: (v?: string) => v || '—' },
                            { title: 'Cód. movimento', dataIndex: 'codigoMovimento', render: (v?: string) => v || '—' },
                            { title: 'Valor pago', dataIndex: 'valorPago', align: 'right', render: (v?: number) => (v != null ? `R$ ${v.toFixed(2)}` : '—') },
                            {
                              title: 'Processado',
                              key: 'processado',
                              render: (_, item) =>
                                item.processado ? (
                                  <Tag color="success">Baixa gerada</Tag>
                                ) : (
                                  <Tooltip title={item.observacao}>
                                    <Tag color="default">Não processado</Tag>
                                  </Tooltip>
                                ),
                            },
                          ]}
                        />
                      ),
                    }}
                    columns={[
                      { title: 'Arquivo', dataIndex: 'nomeArquivo', render: (v?: string) => v || '—' },
                      { title: 'Qtd. registros', dataIndex: 'quantidadeRegistros', align: 'right' },
                      {
                        title: 'Processados',
                        key: 'processados',
                        render: (_, retorno) => `${retorno.itens.filter((i) => i.processado).length} / ${retorno.itens.length}`,
                      },
                    ]}
                  />
                </Flex>
              ),
            },
          ]}
        />
      </Card>

      {contaBancariaId && (
        <>
          <GerarRemessaModal
            open={modalGerarAberto}
            contaBancariaId={contaBancariaId}
            onClose={() => setModalGerarAberto(false)}
            onGerada={() => setModalGerarAberto(false)}
          />
          <ImportarRetornoModal open={modalImportarAberto} contaBancariaId={contaBancariaId} onClose={() => setModalImportarAberto(false)} />
        </>
      )}
    </Flex>
  )
}
