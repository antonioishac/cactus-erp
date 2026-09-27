/**
 * Predicados puros sobre a lista de authorities do usuário (claim `authorities` do
 * JWT — strings cruas, sem prefixo `SCOPE_`/`ROLE_`, ex: `CADASTRO_CLIENTE_CONSULTAR`).
 * Reusados tanto pela guarda de rota (`RequireAuthority`) quanto pelo filtro de menu
 * do sidebar (`AppSider`), pra nunca ter duas implementações do mesmo critério.
 */

export function hasAuthority(authorities: string[], required: string): boolean {
  return authorities.includes(required)
}

export function hasAnyAuthority(authorities: string[], anyOf: string[]): boolean {
  return anyOf.some((required) => authorities.includes(required))
}
