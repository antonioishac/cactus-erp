package br.com.cactusdigital.erp.seguranca.dto;

import java.util.List;

/** {@code empresaNome} propositalmente ausente — ver {@link UsuarioPapelEmpresaResponse}. */
public record EmpresaDisponivelResponse(Long empresaId, List<String> papeis) {
}
