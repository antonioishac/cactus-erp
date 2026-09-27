import { useEffect, useMemo, useState } from 'react'
import { App, Alert, Button, Card, Checkbox, Col, Divider, Flex, Input, Row, Skeleton, Tag, Tooltip, Typography } from 'antd'
import { SaveOutlined } from '@ant-design/icons'
import { useAtribuirFuncionalidades, useTodasFuncionalidades, useTodosModulos } from '../queries'
import type { FuncionalidadeResponse, PapelResponse } from '../types'

const { Text } = Typography

interface FuncionalidadesTabProps {
  papel: PapelResponse
  podeGerenciar: boolean
}

/**
 * A tela mais crítica do módulo — é este PUT que decide, na prática, quais
 * módulos um usuário com este Papel enxerga no sistema (a soma das
 * Funcionalidades de todos os Papéis ativos do usuário vira o claim
 * `authorities` do JWT, e é isso que o `NavConfig`/`RequireAuthority` do
 * frontend usam pra mostrar/esconder cada módulo). Agrupado por Módulo com um
 * checkbox "selecionar tudo" por grupo — dá pra ligar/desligar um módulo
 * inteiro pra este Papel num clique só.
 */
export default function FuncionalidadesTab({ papel, podeGerenciar }: FuncionalidadesTabProps) {
  const { message } = App.useApp()
  const { data: modulos, isLoading: carregandoModulos } = useTodosModulos()
  const { data: funcionalidades, isLoading: carregandoFuncionalidades } = useTodasFuncionalidades()
  const atribuir = useAtribuirFuncionalidades(papel.id)

  const [selecionadas, setSelecionadas] = useState<Set<number>>(new Set())
  const [busca, setBusca] = useState('')

  /* oxlint-disable react-hooks/set-state-in-effect -- ressincroniza o estado local sempre que o papel (ou o resultado de um save anterior) muda */
  useEffect(() => {
    setSelecionadas(new Set(papel.funcionalidades.map((f) => f.id)))
  }, [papel])
  /* oxlint-enable react-hooks/set-state-in-effect */

  const porModulo = useMemo(() => {
    const mapa = new Map<number, FuncionalidadeResponse[]>()
    for (const f of funcionalidades ?? []) {
      if (busca && !f.nome.toLowerCase().includes(busca.toLowerCase()) && !f.codigo.toLowerCase().includes(busca.toLowerCase())) continue
      const lista = mapa.get(f.moduloId) ?? []
      lista.push(f)
      mapa.set(f.moduloId, lista)
    }
    return mapa
  }, [funcionalidades, busca])

  const totalFuncionalidades = funcionalidades?.length ?? 0
  const houveAlteracao =
    selecionadas.size !== papel.funcionalidades.length || papel.funcionalidades.some((f) => !selecionadas.has(f.id))

  function alternarFuncionalidade(id: number, marcado: boolean) {
    setSelecionadas((atual) => {
      const novo = new Set(atual)
      if (marcado) novo.add(id)
      else novo.delete(id)
      return novo
    })
  }

  function alternarModulo(idsDoModulo: number[], marcarTudo: boolean) {
    setSelecionadas((atual) => {
      const novo = new Set(atual)
      for (const id of idsDoModulo) {
        if (marcarTudo) novo.add(id)
        else novo.delete(id)
      }
      return novo
    })
  }

  async function handleSalvar() {
    try {
      await atribuir.mutateAsync({ funcionalidadeIds: Array.from(selecionadas) })
      message.success('Funcionalidades atualizadas — usuários com este papel já refletem a mudança no próximo login/renovação de token.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar funcionalidades.')
    }
  }

  if (carregandoModulos || carregandoFuncionalidades) {
    return <Skeleton active paragraph={{ rows: 6 }} />
  }

  return (
    <Flex vertical gap={16}>
      <Alert
        type="info"
        showIcon
        title={`${selecionadas.size} de ${totalFuncionalidades} funcionalidades marcadas para o papel "${papel.nome}"`}
        description="A visibilidade de cada módulo no menu de um usuário depende só disto: se nenhuma funcionalidade de um módulo estiver marcada em nenhum papel do usuário, o módulo simplesmente não aparece pra ele."
      />

      <Flex justify="space-between" align="center" wrap="wrap" gap={12}>
        <Input.Search
          placeholder="Buscar por nome ou código da funcionalidade"
          allowClear
          style={{ maxWidth: 360 }}
          onChange={(e) => setBusca(e.target.value)}
        />
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar papéis'}>
          <Button
            type="primary"
            icon={<SaveOutlined />}
            disabled={!podeGerenciar || !houveAlteracao}
            loading={atribuir.isPending}
            onClick={handleSalvar}
          >
            Salvar Funcionalidades
          </Button>
        </Tooltip>
      </Flex>

      {(modulos ?? []).map((modulo) => {
        const funcionalidadesDoModulo = porModulo.get(modulo.id) ?? []
        if (funcionalidadesDoModulo.length === 0) return null
        const idsDoModulo = funcionalidadesDoModulo.map((f) => f.id)
        const todasMarcadas = idsDoModulo.every((id) => selecionadas.has(id))
        const algumaMarcada = idsDoModulo.some((id) => selecionadas.has(id))

        return (
          <Card key={modulo.id} size="small" title={
            <Flex align="center" gap={12}>
              <Checkbox
                disabled={!podeGerenciar}
                checked={todasMarcadas}
                indeterminate={algumaMarcada && !todasMarcadas}
                onChange={(e) => alternarModulo(idsDoModulo, e.target.checked)}
              >
                <b>{modulo.nome}</b>
              </Checkbox>
              <Tag>{idsDoModulo.filter((id) => selecionadas.has(id)).length}/{idsDoModulo.length}</Tag>
            </Flex>
          }>
            <Row gutter={[16, 8]}>
              {funcionalidadesDoModulo.map((f) => (
                <Col span={8} key={f.id}>
                  <Checkbox
                    disabled={!podeGerenciar}
                    checked={selecionadas.has(f.id)}
                    onChange={(e) => alternarFuncionalidade(f.id, e.target.checked)}
                  >
                    {f.nome} <Text type="secondary">({f.codigo})</Text>
                  </Checkbox>
                </Col>
              ))}
            </Row>
          </Card>
        )
      })}

      <Divider style={{ margin: '4px 0' }} />
      <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar papéis'}>
        <Button
          type="primary"
          icon={<SaveOutlined />}
          disabled={!podeGerenciar || !houveAlteracao}
          loading={atribuir.isPending}
          onClick={handleSalvar}
        >
          Salvar Funcionalidades
        </Button>
      </Tooltip>
    </Flex>
  )
}
