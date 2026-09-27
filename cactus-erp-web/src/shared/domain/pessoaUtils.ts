import type { TipoPessoa } from './pessoa'

/** Máscara de exibição — não valida dígito verificador, só formata pra leitura. */
export function formatarDocumento(documento: string | undefined, tipoPessoa: TipoPessoa): string {
  if (!documento) return '—'
  const digitos = documento.replace(/\D/g, '')
  if (tipoPessoa === 'FISICA' && digitos.length === 11) {
    return digitos.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
  }
  if (tipoPessoa === 'JURIDICA' && digitos.length === 14) {
    return digitos.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5')
  }
  return documento
}

export const UFS = [
  'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG',
  'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO',
] as const

export const TIPO_CONTATO_LABEL: Record<string, string> = {
  EMAIL: 'E-mail',
  RESPONSAVEL: 'Responsável',
  OUTRO: 'Outro',
}

export const TIPO_TELEFONE_LABEL: Record<string, string> = {
  FIXO: 'Fixo',
  CELULAR: 'Celular',
  COMERCIAL: 'Comercial',
  WHATSAPP: 'WhatsApp',
}
