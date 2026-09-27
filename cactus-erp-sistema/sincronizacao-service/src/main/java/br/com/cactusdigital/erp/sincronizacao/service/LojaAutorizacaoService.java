package br.com.cactusdigital.erp.sincronizacao.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Fecha o gap de isolamento por loja encontrado na validação ao vivo de 2026-08-31 (ver
 * FASE6_SINCRONIZACAO_PDV.md, seção 11.3): antes desta classe, qualquer client
 * autenticado podia empurrar/puxar dado de qualquer {@code lojaId}, porque os
 * controllers só checavam {@code isAuthenticated()}.
 * <p>
 * Regra: um token de client "um por loja" (provisionado via
 * {@code ClienteLojaService}, no seguranca-service) carrega a claim {@code lojaId} — se
 * presente, só pode acessar dado da própria loja. Um token SEM essa claim (token de
 * serviço do próprio sincronizacao-service, ou um token humano ADMIN/suporte) é tratado
 * como cliente de confiança, sem isolamento por loja aplicável — mesmo critério já usado
 * em toda a Fase 6 pra distinguir chamada interna de chamada externa (PDV).
 */
@Component
public class LojaAutorizacaoService {

    public void validarAcesso(Jwt jwt, Long lojaIdSolicitado) {
        Long lojaIdToken = jwt.getClaim("lojaId");
        if (lojaIdToken != null && !lojaIdToken.equals(lojaIdSolicitado)) {
            throw new AccessDeniedException(
                    "Client de sincronização da loja " + lojaIdToken + " não pode acessar dado da loja " + lojaIdSolicitado);
        }
    }
}
