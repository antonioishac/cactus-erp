import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Flex, Form, Popconfirm, Skeleton, Space, Tabs, Tag, Tooltip } from 'antd'
import { ArrowLeftOutlined, KeyOutlined, LockOutlined, SaveOutlined, UnlockOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { atribuirPapelEmpresa } from './api'
import AbaBloqueada from './AbaBloqueada'
import AlterarSenhaModal from './modals/AlterarSenhaModal'
import DadosGeraisUsuarioTab from './tabs/DadosGeraisUsuarioTab'
import PapeisEmpresaTab from './tabs/PapeisEmpresaTab'
import {
  useAtualizarUsuario,
  useBloquearUsuario,
  useCriarUsuario,
  useDesbloquearUsuario,
  useInativarUsuario,
  useReativarUsuario,
  useUsuario,
} from './queries'
import type { UsuarioRequest } from './types'

/** `papelInicialId`/`empresaInicialId` não são campos de `UsuarioRequest` — só existem
 * neste form pra viabilizar o atalho "criar usuário + já atribuir papel" (ver
 * `DadosGeraisUsuarioTab`), removidos do request antes do `POST /usuarios` e usados,
 * os dois juntos, numa chamada separada logo depois (`atribuirPapelEmpresa`). */
interface FormValues extends UsuarioRequest {
  papelInicialId?: number
  empresaInicialId?: number
}

const VALORES_PADRAO_NOVO: Partial<FormValues> = { ativo: true }

/**
 * `ativo`/`bloqueado` nunca são editados pelo Form principal — só por ação
 * dedicada (ver Javadoc de `UsuarioRequest` em `types.ts`). Por isso este
 * form não tem um Switch de situação no cabeçalho como Cliente/Empresa/
 * Produto têm: os dois estados aqui são só leitura (`Tag`) + botão de ação.
 */
export default function UsuarioFormPage() {
  const { id } = useParams<{ id: string }>()
  const usuarioId = id ? Number(id) : undefined
  const modoEdicao = usuarioId !== undefined
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<FormValues>()

  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'SEGURANCA_GERENCIAR')

  const { data: usuario, isLoading: carregando } = useUsuario(usuarioId)
  const criar = useCriarUsuario()
  const atualizar = useAtualizarUsuario(usuarioId ?? -1)
  const bloquear = useBloquearUsuario()
  const desbloquear = useDesbloquearUsuario()
  const inativar = useInativarUsuario()
  const reativar = useReativarUsuario()
  const salvando = criar.isPending || atualizar.isPending

  const [modalSenhaAberto, setModalSenhaAberto] = useState(false)

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (usuario) {
      // colaboradorId vem `null` (não `undefined`) quando não há vínculo —
      // o Select do form trata `undefined` como "sem valor", não `null`.
      form.setFieldsValue({ ...usuario, colaboradorId: usuario.colaboradorId ?? undefined })
    }
  }, [usuario, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    const { papelInicialId, empresaInicialId, ...valoresUsuario } = valores
    // `ativo` nunca tem um Form.Item próprio (não é editável pelo usuário,
    // ver Javadoc de UsuarioRequest) — sem incluir explicitamente aqui,
    // `valores.ativo` vem `undefined` (nenhum campo registrado pra ele), e
    // como o backend usa `boolean` primitivo (não `Boolean`), `null`/ausente
    // no JSON quebra o parse com 400 (achado real ao vivo, 2026-09-02). Na
    // criação sempre `true`; na edição, mantém o valor atual (o backend
    // também ignora esse campo no PUT, mas evita mandar algo sem sentido).
    const request: UsuarioRequest = {
      ...valoresUsuario,
      ativo: usuario?.ativo ?? true,
      senha: modoEdicao ? undefined : valoresUsuario.senha,
    }

    try {
      if (modoEdicao) {
        await atualizar.mutateAsync(request)
        message.success('Usuário atualizado com sucesso.')
      } else {
        const criado = await criar.mutateAsync(request)
        message.success('Usuário criado com sucesso.')
        // Atalho "criar + já atribuir papel" — chamada avulsa (não um hook de
        // mutation, que exigiria o id do usuário já no momento do render) com
        // seu próprio try/catch: uma falha aqui não deve impedir a navegação
        // pro usuário recém-criado, que já existe de verdade e pode ganhar o
        // papel depois pela própria aba Papéis por Empresa.
        if (papelInicialId != null && empresaInicialId != null) {
          try {
            await atribuirPapelEmpresa(criado.id, { papelId: papelInicialId, empresaId: empresaInicialId })
            message.success('Papel inicial atribuído.')
          } catch (erro) {
            message.error(erro instanceof Error ? erro.message : 'Usuário criado, mas falhou ao atribuir o papel inicial.')
          }
        }
        navigate(`/seguranca/usuarios/${criado.id}`, { replace: true })
        return
      }
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar usuário.')
    }
  }

  async function executarAcao(acao: 'bloquear' | 'desbloquear' | 'inativar' | 'reativar') {
    if (usuarioId === undefined) return
    const mapa = { bloquear, desbloquear, inativar, reativar }
    const mensagens = {
      bloquear: 'Usuário bloqueado.',
      desbloquear: 'Usuário desbloqueado.',
      inativar: 'Usuário inativado.',
      reativar: 'Usuário reativado.',
    }
    try {
      await mapa[acao].mutateAsync(usuarioId)
      message.success(mensagens[acao])
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao executar ação.')
    }
  }

  if (modoEdicao && carregando) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 8 }} />
      </Card>
    )
  }

  const dicaAbaBloqueada = 'Salve os dados gerais do usuário primeiro'

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate('/seguranca/usuarios')}>Usuários</a> },
          { title: modoEdicao ? usuario?.login : 'Novo usuário' },
        ]}
      />

      <Card>
        <Form form={form} layout="vertical" initialValues={VALORES_PADRAO_NOVO} disabled={!podeGerenciar}>
          <Flex justify="space-between" align="center" wrap="wrap" gap={16} style={{ marginBottom: 24 }}>
            <Space size="middle">
              {modoEdicao && usuario && (
                <>
                  <Tag color={usuario.ativo ? 'success' : 'default'}>{usuario.ativo ? 'Ativo' : 'Inativo'}</Tag>
                  {usuario.bloqueado && <Tag color="error">Bloqueado</Tag>}
                </>
              )}
            </Space>
            <Space size="small" wrap>
              {modoEdicao && podeGerenciar && (
                <>
                  <Button icon={<KeyOutlined />} onClick={() => setModalSenhaAberto(true)}>
                    Alterar Senha
                  </Button>
                  {usuario?.bloqueado ? (
                    <Button icon={<UnlockOutlined />} loading={desbloquear.isPending} onClick={() => executarAcao('desbloquear')}>
                      Desbloquear
                    </Button>
                  ) : (
                    <Button icon={<LockOutlined />} loading={bloquear.isPending} onClick={() => executarAcao('bloquear')}>
                      Bloquear
                    </Button>
                  )}
                  {usuario?.ativo ? (
                    <Popconfirm title="Inativar este usuário?" description="Ele deixa de conseguir logar até ser reativado." onConfirm={() => executarAcao('inativar')}>
                      <Button danger loading={inativar.isPending}>
                        Inativar
                      </Button>
                    </Popconfirm>
                  ) : (
                    <Button type="dashed" loading={reativar.isPending} onClick={() => executarAcao('reativar')}>
                      Reativar
                    </Button>
                  )}
                </>
              )}
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/seguranca/usuarios')}>
                Cancelar
              </Button>
              <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar usuários'}>
                <Button type="primary" icon={<SaveOutlined />} loading={salvando} disabled={!podeGerenciar} onClick={handleSalvar}>
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          <Tabs
            items={[
              {
                key: 'dados-gerais',
                label: 'Dados Gerais',
                children: <DadosGeraisUsuarioTab modoEdicao={modoEdicao} colaboradorIdInicial={usuario?.colaboradorId} />,
              },
              {
                key: 'papeis-empresa',
                label: 'Papéis por Empresa',
                disabled: !modoEdicao,
                children:
                  usuarioId !== undefined ? (
                    <PapeisEmpresaTab usuarioId={usuarioId} podeGerenciar={podeGerenciar} />
                  ) : (
                    <AbaBloqueada mensagem={dicaAbaBloqueada} />
                  ),
              },
            ].map((item) => (item.disabled ? { ...item, label: <Tooltip title={dicaAbaBloqueada}>{item.label}</Tooltip> } : item))}
          />
        </Form>
      </Card>

      {usuarioId !== undefined && (
        <AlterarSenhaModal open={modalSenhaAberto} usuarioId={usuarioId} onClose={() => setModalSenhaAberto(false)} />
      )}
    </Flex>
  )
}
