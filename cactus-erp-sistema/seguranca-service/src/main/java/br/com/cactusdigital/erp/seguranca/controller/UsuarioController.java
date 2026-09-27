package br.com.cactusdigital.erp.seguranca.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.seguranca.dto.AlterarSenhaRequest;
import br.com.cactusdigital.erp.seguranca.dto.UsuarioPapelEmpresaRequest;
import br.com.cactusdigital.erp.seguranca.dto.UsuarioPapelEmpresaResponse;
import br.com.cactusdigital.erp.seguranca.dto.UsuarioRequest;
import br.com.cactusdigital.erp.seguranca.dto.UsuarioResponse;
import br.com.cactusdigital.erp.seguranca.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Segurança — Usuário", description = "Login do sistema, vínculo opcional com Colaborador (id solto, cross-serviço), papéis por empresa (UsuarioPapelEmpresa)")
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Listar usuários", description = "Busca paginada por login ou nome, opcional")
    @ApiResponse(responseCode = "200", description = "Página de usuários")
    @GetMapping
    @PreAuthorize("hasAuthority('SEGURANCA_CONSULTAR')")
    public PageResponse<UsuarioResponse> listar(@RequestParam(required = false) String busca, Pageable pageable) {
        return PageResponse.of(usuarioService.listar(busca, pageable));
    }

    @Operation(summary = "Buscar usuário por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SEGURANCA_CONSULTAR')")
    public UsuarioResponse buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    @Operation(summary = "Criar usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Senha ausente ao criar",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('SEGURANCA_GERENCIAR')")
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(request));
    }

    @Operation(summary = "Atualizar usuário", description = "Não altera ativo/bloqueado — ver endpoints próprios (inativar/reativar, bloquear/desbloquear)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SEGURANCA_GERENCIAR')")
    public UsuarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
        return usuarioService.atualizar(id, request);
    }

    @Operation(summary = "Alterar senha do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha alterada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PatchMapping("/{id}/senha")
    @PreAuthorize("hasAuthority('SEGURANCA_GERENCIAR')")
    public ResponseEntity<Void> alterarSenha(@PathVariable Long id, @Valid @RequestBody AlterarSenhaRequest request) {
        usuarioService.alterarSenha(id, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Bloquear usuário", description = "Travamento temporário — reversível, diferente de inativar/excluir")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário bloqueado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PatchMapping("/{id}/bloquear")
    @PreAuthorize("hasAuthority('SEGURANCA_GERENCIAR')")
    public UsuarioResponse bloquear(@PathVariable Long id) {
        return usuarioService.bloquear(id);
    }

    @Operation(summary = "Desbloquear usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário desbloqueado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PatchMapping("/{id}/desbloquear")
    @PreAuthorize("hasAuthority('SEGURANCA_GERENCIAR')")
    public UsuarioResponse desbloquear(@PathVariable Long id) {
        return usuarioService.desbloquear(id);
    }

    @Operation(summary = "Inativar usuário", description = "Desativação deliberada (equivalente a \"excluído\" sem perder histórico/auditoria) — reversível via reativar, diferente de bloquear (travamento temporário) e de excluir (definitivo)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário inativado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasAuthority('SEGURANCA_GERENCIAR')")
    public UsuarioResponse inativar(@PathVariable Long id) {
        return usuarioService.inativar(id);
    }

    @Operation(summary = "Reativar usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário reativado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PatchMapping("/{id}/reativar")
    @PreAuthorize("hasAuthority('SEGURANCA_GERENCIAR')")
    public UsuarioResponse reativar(@PathVariable Long id) {
        return usuarioService.reativar(id);
    }

    @Operation(summary = "Excluir usuário", description = "Definitivo — pra desativação reversível, ver inativar")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário excluído"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SEGURANCA_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar papéis do usuário por empresa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Papéis por empresa do usuário"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}/papeis-empresa")
    @PreAuthorize("hasAuthority('SEGURANCA_CONSULTAR')")
    public List<UsuarioPapelEmpresaResponse> listarPapeisEmpresa(@PathVariable Long id) {
        return usuarioService.listarPapeisEmpresa(id);
    }

    @Operation(summary = "Atribuir papel do usuário numa empresa", description = "É essa associação que define as authorities do token quando o usuário loga nessa empresa. empresaId não é validado contra o cadastro-base (referência solta, cross-serviço)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Papel atribuído"),
            @ApiResponse(responseCode = "404", description = "Usuário ou papel não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Usuário já possui esse papel nessa empresa",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/papeis-empresa")
    @PreAuthorize("hasAuthority('SEGURANCA_GERENCIAR')")
    public ResponseEntity<UsuarioPapelEmpresaResponse> atribuirPapelEmpresa(
            @PathVariable Long id, @Valid @RequestBody UsuarioPapelEmpresaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.atribuirPapelEmpresa(id, request));
    }

    @Operation(summary = "Revogar papel do usuário numa empresa")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Papel revogado"),
            @ApiResponse(responseCode = "404", description = "Usuário ou associação papel-empresa não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}/papeis-empresa/{papelEmpresaId}")
    @PreAuthorize("hasAuthority('SEGURANCA_GERENCIAR')")
    public ResponseEntity<Void> revogarPapelEmpresa(@PathVariable Long id, @PathVariable Long papelEmpresaId) {
        usuarioService.revogarPapelEmpresa(id, papelEmpresaId);
        return ResponseEntity.noContent().build();
    }
}
