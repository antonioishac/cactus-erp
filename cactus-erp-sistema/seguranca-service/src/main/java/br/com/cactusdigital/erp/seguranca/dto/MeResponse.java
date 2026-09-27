package br.com.cactusdigital.erp.seguranca.dto;

import java.util.List;

/** {@code empresaNome} propositalmente ausente — ver {@link UsuarioPapelEmpresaResponse}. */
public record MeResponse(Long usuarioId, String login, Long empresaId, List<String> authorities) {
}
