package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.financeiro.dto.TipoDocumentoFinanceiroRequest;
import br.com.cactusdigital.erp.financeiro.dto.TipoDocumentoFinanceiroResponse;
import br.com.cactusdigital.erp.financeiro.service.TipoDocumentoFinanceiroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Tipo de Documento Financeiro", description = "Catálogo GLOBAL (NF/Boleto/Recibo/Duplicata/Cheque etc.) — fecha o gap contra FIN_DOCUMENTO_ORIGEM do fenix.sql")
@RestController
@RequestMapping("/api/v1/tipos-documento-financeiro")
@RequiredArgsConstructor
public class TipoDocumentoFinanceiroController {

    private final TipoDocumentoFinanceiroService service;

    @Operation(summary = "Listar tipos de documento financeiro ativos")
    @ApiResponse(responseCode = "200", description = "Lista de tipos de documento")
    @GetMapping
    @PreAuthorize("hasAuthority('FINANCEIRO_CONSULTAR')")
    public List<TipoDocumentoFinanceiroResponse> listar() {
        return service.listarAtivos();
    }

    @Operation(summary = "Criar tipo de documento financeiro")
    @ApiResponse(responseCode = "201", description = "Tipo criado")
    @PostMapping
    @PreAuthorize("hasAuthority('FINANCEIRO_GERENCIAR')")
    public ResponseEntity<TipoDocumentoFinanceiroResponse> criar(@Valid @RequestBody TipoDocumentoFinanceiroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Inativar tipo de documento financeiro")
    @ApiResponse(responseCode = "204", description = "Tipo inativado")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCEIRO_GERENCIAR')")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
