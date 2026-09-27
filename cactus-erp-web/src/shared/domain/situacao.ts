/**
 * Situação de ativação — usada por praticamente toda entidade de cadastro do
 * backend (`common.domain.Situacao`, `SituacaoConverter`). Exclusão é sempre
 * lógica (`situacao=INATIVO`), nunca física — ver CLAUDE.md raiz. Cross-cutting
 * de propósito (vive em `shared/`, não em `features/clientes/`): Produtos,
 * Empresas, Colaboradores e Fornecedores vão reusar o mesmo tipo/tag quando
 * ganharem tela.
 */
export type Situacao = 'ATIVO' | 'INATIVO'

export const SITUACAO_LABEL: Record<Situacao, string> = {
  ATIVO: 'Ativo',
  INATIVO: 'Inativo',
}
