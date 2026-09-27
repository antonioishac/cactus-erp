import { jwtDecode } from 'jwt-decode'

/**
 * Claims de negócio que o `jwtTokenCustomizer` do `seguranca-service` injeta no
 * access token (ver `AuthorizationServerConfig.jwtTokenCustomizer`, backend). O
 * decode aqui é só para LER os dados no frontend — a validação de assinatura/
 * expiração é responsabilidade de cada Resource Server, nunca do SPA.
 */
export interface CactusTokenClaims {
  sub: string
  usuarioId: number
  login: string
  empresaId?: number
  authorities: string[]
  exp: number
  iat: number
}

export function decodeClaims(accessToken: string): CactusTokenClaims {
  return jwtDecode<CactusTokenClaims>(accessToken)
}
