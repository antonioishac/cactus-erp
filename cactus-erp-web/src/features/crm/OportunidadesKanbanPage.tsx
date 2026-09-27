import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { App, Button, Card, Empty, Flex, Skeleton, Tag, Typography } from 'antd'
import { PlusOutlined, SwapOutlined } from '@ant-design/icons'
import { DndContext, PointerSensor, useDraggable, useDroppable, useSensor, useSensors, type DragEndEvent } from '@dnd-kit/core'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { ClienteResumoCell } from './ResumoCells'
import MoverEstagioModal from './modals/MoverEstagioModal'
import { useEstagiosFunilOrdenado, useMoverEstagioOportunidadeGenerico, useVisaoFunil } from './queries'
import type { FunilEstagioResponse, OportunidadeResponse } from './types'

const { Title, Text } = Typography

interface CartaoOportunidadeProps {
  oportunidade: OportunidadeResponse
  estagioAtualId: number
  podeArrastar: boolean
  podeProspectar: boolean
  onNavegar: () => void
  onMover: () => void
}

/** Card arrastável — `useDraggable` só ganha os listeners de pointer quando o usuário tem `CRM_PROSPECTAR` (mesmo gate do botão "Mover" que continua existindo, como fallback explícito e como único caminho pra estágio PERDIDO, que exige motivo). */
function CartaoOportunidade({ oportunidade, estagioAtualId, podeArrastar, podeProspectar, onNavegar, onMover }: CartaoOportunidadeProps) {
  const { attributes, listeners, setNodeRef, transform, isDragging } = useDraggable({
    id: `oportunidade-${oportunidade.id}`,
    data: { oportunidade, estagioAtualId },
    disabled: !podeArrastar,
  })

  return (
    <Card
      ref={setNodeRef}
      size="small"
      hoverable
      onClick={onNavegar}
      styles={{ body: { padding: 12 } }}
      style={{
        transform: transform ? `translate3d(${transform.x}px, ${transform.y}px, 0)` : undefined,
        opacity: isDragging ? 0.4 : 1,
        cursor: podeArrastar ? 'grab' : 'pointer',
        touchAction: 'none',
        zIndex: isDragging ? 10 : undefined,
      }}
      {...(podeArrastar ? { ...attributes, ...listeners } : {})}
    >
      <Flex vertical gap={4}>
        <Text strong>{oportunidade.nome}</Text>
        {oportunidade.clienteId && (
          <Text type="secondary" style={{ fontSize: 12 }}>
            <ClienteResumoCell clienteId={oportunidade.clienteId} />
          </Text>
        )}
        <Flex justify="space-between" align="center">
          <Text>{oportunidade.valorEstimado != null ? `R$ ${oportunidade.valorEstimado.toFixed(2)}` : '—'}</Text>
          {oportunidade.probabilidadePercentual != null && <Tag>{oportunidade.probabilidadePercentual}%</Tag>}
        </Flex>
        {podeProspectar && (
          <Button
            size="small"
            icon={<SwapOutlined />}
            onClick={(e) => {
              e.stopPropagation()
              onMover()
            }}
          >
            Mover
          </Button>
        )}
      </Flex>
    </Card>
  )
}

interface ColunaFunilProps {
  coluna: FunilEstagioResponse
  podeProspectar: boolean
  onNavegar: (id: number) => void
  onMover: (oportunidade: OportunidadeResponse) => void
}

/** Coluna droppable — `isOver` só dá o destaque visual (fundo mais claro) enquanto um card é arrastado por cima, sem nenhuma lógica de negócio aqui (isso fica no `onDragEnd` da página). */
function ColunaFunil({ coluna, podeProspectar, onNavegar, onMover }: ColunaFunilProps) {
  const { setNodeRef, isOver } = useDroppable({
    id: `estagio-${coluna.estagioFunilId}`,
    data: { estagioFunilId: coluna.estagioFunilId },
  })

  return (
    <div ref={setNodeRef} style={{ minWidth: 300, flex: '0 0 300px' }}>
      <Card
        size="small"
        title={
          <Flex justify="space-between" align="center">
            <span>{coluna.estagioFunilNome}</span>
            <Tag>{coluna.oportunidades.length}</Tag>
          </Flex>
        }
        extra={coluna.valorEstimadoTotal != null ? `R$ ${coluna.valorEstimadoTotal.toFixed(2)}` : undefined}
        styles={{ body: { minHeight: 200, background: isOver ? '#eaf6da' : '#fafafa', transition: 'background 0.15s' } }}
      >
        <Flex vertical gap={8}>
          {coluna.oportunidades.length === 0 && <Text type="secondary">Nenhuma oportunidade neste estágio</Text>}
          {coluna.oportunidades.map((oportunidade) => (
            <CartaoOportunidade
              key={oportunidade.id}
              oportunidade={oportunidade}
              estagioAtualId={coluna.estagioFunilId}
              podeArrastar={podeProspectar}
              podeProspectar={podeProspectar}
              onNavegar={() => onNavegar(oportunidade.id)}
              onMover={() => onMover(oportunidade)}
            />
          ))}
        </Flex>
      </Card>
    </div>
  )
}

/**
 * Kanban com drag-and-drop real (`@dnd-kit/core`) — reverte a decisão original
 * registrada no BACKLOG ("sem drag-and-drop nesta fase, só o botão Mover +
 * modal") a pedido do usuário. Arrastar um card pra uma coluna cujo
 * `tipoEncerramento` é `ABERTO`/`GANHO` move direto (`useMoverEstagioOportunidadeGenerico`,
 * sem confirmação — mesmo espírito de arrastar um card num board real);
 * arrastar pra uma coluna `PERDIDO` abre `MoverEstagioModal` já com o
 * estágio destino pré-selecionado, porque `motivoPerda` é obrigatório e só
 * o modal coleta esse dado (o drop nunca pula essa validação, só evita
 * reabrir o Select quando o motivo não é necessário). O botão "Mover"
 * continua existindo — fallback explícito e único caminho de teclado/toque
 * sem gesto de arrastar.
 */
export default function OportunidadesKanbanPage() {
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeProspectar = hasAuthority(authorities, 'CRM_PROSPECTAR')
  const { data: funil, isLoading } = useVisaoFunil()
  const { data: estagios } = useEstagiosFunilOrdenado()
  const moverGenerico = useMoverEstagioOportunidadeGenerico()
  const [oportunidadeMovendo, setOportunidadeMovendo] = useState<OportunidadeResponse | null>(null)
  const [estagioDestinoPendente, setEstagioDestinoPendente] = useState<number | undefined>(undefined)

  const sensors = useSensors(useSensor(PointerSensor, { activationConstraint: { distance: 8 } }))

  function fecharModalMover() {
    setOportunidadeMovendo(null)
    setEstagioDestinoPendente(undefined)
  }

  function handleDragEnd(event: DragEndEvent) {
    const { active, over } = event
    if (!over) return

    const oportunidade = active.data.current?.oportunidade as OportunidadeResponse | undefined
    const estagioAtualId = active.data.current?.estagioAtualId as number | undefined
    const estagioDestinoId = over.data.current?.estagioFunilId as number | undefined
    if (!oportunidade || estagioDestinoId === undefined || estagioDestinoId === estagioAtualId) return

    const destino = estagios?.find((e) => e.id === estagioDestinoId)
    if (destino?.tipoEncerramento === 'PERDIDO') {
      setOportunidadeMovendo(oportunidade)
      setEstagioDestinoPendente(estagioDestinoId)
      return
    }

    moverGenerico.mutate(
      { id: oportunidade.id, request: { estagioFunilId: estagioDestinoId } },
      { onError: (erro) => message.error(erro instanceof Error ? erro.message : 'Falha ao mover oportunidade.') },
    )
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Funil de Vendas
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/crm/oportunidades/novo')}>
          Nova Oportunidade
        </Button>
      </Flex>

      {isLoading ? (
        <Skeleton active paragraph={{ rows: 6 }} />
      ) : !funil || funil.length === 0 ? (
        <Empty description="Nenhum estágio do funil cadastrado ainda" />
      ) : (
        <DndContext sensors={sensors} onDragEnd={handleDragEnd}>
          <div style={{ display: 'flex', gap: 16, overflowX: 'auto', paddingBottom: 8 }}>
            {funil.map((coluna) => (
              <ColunaFunil
                key={coluna.estagioFunilId}
                coluna={coluna}
                podeProspectar={podeProspectar}
                onNavegar={(id) => navigate(`/crm/oportunidades/${id}`)}
                onMover={(oportunidade) => {
                  setEstagioDestinoPendente(undefined)
                  setOportunidadeMovendo(oportunidade)
                }}
              />
            ))}
          </div>
        </DndContext>
      )}

      {oportunidadeMovendo && (
        <MoverEstagioModal
          open={!!oportunidadeMovendo}
          oportunidadeId={oportunidadeMovendo.id}
          estagioAtualId={oportunidadeMovendo.estagioFunilId}
          estagioDestinoInicial={estagioDestinoPendente}
          onClose={fecharModalMover}
        />
      )}
    </Flex>
  )
}
