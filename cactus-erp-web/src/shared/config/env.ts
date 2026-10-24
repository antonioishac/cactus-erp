/**
 * Acesso tipado às variáveis de ambiente do Vite (`import.meta.env`). Centralizado aqui
 * para não espalhar `import.meta.env.VITE_X` (sem checagem de tipo/presença) pelo app.
 */

function required(value: string | undefined, name: string): string {
  if (!value) {
    throw new Error(
      `Variável de ambiente ${name} não definida — verifique .env.development (ver .env.example)`,
    )
  }
  return value
}

export const env = {
  segurancaApiUrl: required(import.meta.env.VITE_SEGURANCA_API_URL, 'VITE_SEGURANCA_API_URL'),
  cadastroBaseApiUrl: required(
    import.meta.env.VITE_CADASTRO_BASE_API_URL,
    'VITE_CADASTRO_BASE_API_URL',
  ),
  estoqueApiUrl: required(import.meta.env.VITE_ESTOQUE_API_URL, 'VITE_ESTOQUE_API_URL'),
  gedApiUrl: required(import.meta.env.VITE_GED_API_URL, 'VITE_GED_API_URL'),
  comprasApiUrl: required(import.meta.env.VITE_COMPRAS_API_URL, 'VITE_COMPRAS_API_URL'),
  biApiUrl: required(import.meta.env.VITE_BI_API_URL, 'VITE_BI_API_URL'),
  vendasApiUrl: required(import.meta.env.VITE_VENDAS_API_URL, 'VITE_VENDAS_API_URL'),
  fiscalApiUrl: required(import.meta.env.VITE_FISCAL_API_URL, 'VITE_FISCAL_API_URL'),
  osApiUrl: required(import.meta.env.VITE_OS_API_URL, 'VITE_OS_API_URL'),
  crmApiUrl: required(import.meta.env.VITE_CRM_API_URL, 'VITE_CRM_API_URL'),
  financeiroApiUrl: required(import.meta.env.VITE_FINANCEIRO_API_URL, 'VITE_FINANCEIRO_API_URL'),
  contabilidadeApiUrl: required(
    import.meta.env.VITE_CONTABILIDADE_API_URL,
    'VITE_CONTABILIDADE_API_URL',
  ),
  lojaApiUrl: required(import.meta.env.VITE_LOJA_API_URL, 'VITE_LOJA_API_URL'),
  sincronizacaoApiUrl: required(
    import.meta.env.VITE_SINCRONIZACAO_API_URL,
    'VITE_SINCRONIZACAO_API_URL',
  ),
}
