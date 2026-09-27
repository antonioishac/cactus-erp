import { useState } from 'react'
import { App, Button, Empty, Flex, Popconfirm, Skeleton, Table, Tooltip } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import AtribuirPapelEmpresaModal from '../modals/AtribuirPapelEmpresaModal'
import { useEmpresaResumo, usePapeisEmpresaDoUsuario, useRevogarPapelEmpresa } from '../queries'
import type { UsuarioPapelEmpresaResponse } from '../types'

interface PapeisEmpresaTabProps {
  usuarioId: number
  podeGerenciar: boolean
}

/** Resolve o nome da empresa a partir do id solto (cross-serviço, sem FK) — mesmo padrão de `ProdutoResumoCell` em Estoque. */
function EmpresaCell({ empresaId }: { empresaId: number }) {
  const { data, isLoading } = useEmpresaResumo(empresaId)
  if (isLoading) return <Skeleton.Input active size="small" style={{ width: 160 }} />
  if (!data) return <>{`#${empresaId}`}</>
  return <>{data.nomeFantasia || data.razaoSocial}</>
}

/**
 * É esta tabela que responde, na prática, "quais módulos este usuário
 * enxerga" — cada linha é um Papel numa Empresa; a união das Funcionalidades
 * de todos os papéis ativos do usuário na empresa corrente é que vira as
 * authorities do token (ver Javadoc de `UsuarioController.atribuirPapelEmpresa`).
 */
export default function PapeisEmpresaTab({ usuarioId, podeGerenciar }: PapeisEmpresaTabProps) {
  const { message } = App.useApp()
  const { data: papeisEmpresa, isLoading } = usePapeisEmpresaDoUsuario(usuarioId)
  const revogar = useRevogarPapelEmpresa(usuarioId)
  const [modalAberto, setModalAberto] = useState(false)

  async function handleRevogar(papelEmpresaId: number) {
    try {
      await revogar.mutateAsync(papelEmpresaId)
      message.success('Papel revogado.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao revogar papel.')
    }
  }

  return (
    <Flex vertical gap={16}>
      {podeGerenciar && (
        <Flex justify="flex-end">
          <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalAberto(true)}>
            Atribuir Papel
          </Button>
        </Flex>
      )}

      <Table<UsuarioPapelEmpresaResponse>
        rowKey="id"
        loading={isLoading}
        dataSource={papeisEmpresa ?? []}
        pagination={false}
        locale={{ emptyText: <Empty description="Este usuário ainda não tem papel atribuído em nenhuma empresa — sem authorities, não enxerga nenhum módulo" /> }}
        columns={[
          { title: 'Empresa', key: 'empresa', render: (_, pe) => <EmpresaCell empresaId={pe.empresaId} /> },
          { title: 'Papel', dataIndex: 'papelNome' },
          ...(podeGerenciar
            ? [
                {
                  title: 'Ações',
                  key: 'acoes',
                  width: 100,
                  render: (_: unknown, pe: UsuarioPapelEmpresaResponse) => (
                    <Popconfirm title="Revogar este papel nesta empresa?" onConfirm={() => handleRevogar(pe.id)}>
                      <Tooltip title="Revogar">
                        <Button size="small" danger>
                          Revogar
                        </Button>
                      </Tooltip>
                    </Popconfirm>
                  ),
                },
              ]
            : []),
        ]}
      />

      <AtribuirPapelEmpresaModal open={modalAberto} usuarioId={usuarioId} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
