import { useState } from 'react'
import { Select, App } from 'antd'
import { useAuth } from '@features/auth/useAuth'
import { useEmpresasDisponiveis } from '@features/auth/queries'

/**
 * Header: lista as empresas em que o usuário tem papel atribuído
 * (`GET /empresas-disponiveis`) e troca a empresa ativa (`POST /trocar-empresa`)
 * via `authStore.trocarEmpresaAtiva` — ver authStore.ts para o porquê disso não
 * ser um simples PUT de preferência local (o token efetivo de API muda inteiro).
 *
 * Usa `App.useApp()` (não o `message` estático importado direto de 'antd') pra
 * consumir o `ConfigProvider`/tema corretamente — ver AppProviders.tsx, que já
 * monta o `<App>` do antd em volta de toda a árvore.
 */
export default function CompanySwitcher() {
  const { message } = App.useApp()
  const { empresaAtivaId, trocarEmpresaAtiva } = useAuth()
  const { data: empresas, isLoading } = useEmpresasDisponiveis()
  const [trocando, setTrocando] = useState(false)

  async function handleChange(empresaId: number) {
    setTrocando(true)
    const hide = message.loading('Trocando empresa...', 0)
    try {
      await trocarEmpresaAtiva(empresaId)
      message.success('Empresa alterada com sucesso.')
    } catch (err) {
      // err já chega normalizado como AppError (ver shared/api/problemDetail.ts) —
      // o interceptor de resposta em httpClients.ts nunca deixa um AxiosError cru
      // escapar. Código real: SegurancaErrorCode.USUARIO_SEM_ACESSO_EMPRESA (backend).
      const codigo = (err as { codigo?: string })?.codigo
      message.error(
        codigo === 'SEGURANCA_USUARIO_SEM_ACESSO_EMPRESA'
          ? 'Você não tem papel atribuído nessa empresa.'
          : 'Não foi possível trocar de empresa.',
      )
    } finally {
      hide()
      setTrocando(false)
    }
  }

  return (
    <Select
      value={empresaAtivaId ?? undefined}
      loading={isLoading || trocando}
      disabled={trocando}
      style={{ minWidth: 200 }}
      placeholder="Selecione a empresa"
      options={(empresas ?? []).map((e) => ({
        value: e.empresaId,
        label: `Empresa #${e.empresaId}`,
      }))}
      onChange={handleChange}
    />
  )
}
