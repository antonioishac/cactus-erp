package br.com.cactusdigital.erp.fiscal.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.fiscal.dto.CancelarMdfeRequest;
import br.com.cactusdigital.erp.fiscal.dto.CteDisponivelMdfeResponse;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import br.com.cactusdigital.erp.fiscal.dto.EncerrarMdfeRequest;
import br.com.cactusdigital.erp.fiscal.dto.GerarMdfeRequest;
import br.com.cactusdigital.erp.fiscal.dto.MdfeCabecalhoResponse;
import br.com.cactusdigital.erp.fiscal.service.MdfeCabecalhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MDF-e (Manifesto Eletrônico de Documentos Fiscais)", description = "RASCUNHO (agrupando N CT-e AUTORIZADA da mesma empresa) → (emitir) → ENVIADA → AUTORIZADA/REJEITADA/ERRO, via ACBr Monitor — só modal Rodoviário intra-UF, ver Javadoc de MdfeCabecalho")
@RestController
@RequestMapping("/api/v1/mdfes")
@RequiredArgsConstructor
public class MdfeCabecalhoController {

    private final MdfeCabecalhoService service;

    @Operation(summary = "Listar MDF-e")
    @ApiResponse(responseCode = "200", description = "Página de MDF-e")
    @GetMapping
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public PageResponse<MdfeCabecalhoResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Listar CT-e disponíveis para um novo MDF-e",
            description = "CT-e AUTORIZADA da empresa que não estão em nenhum MDF-e não cancelado — exatamente os que POST /mdfes aceita. Sem paginação: é a fila de CT-e ainda não manifestados")
    @ApiResponse(responseCode = "200", description = "CT-e disponíveis, ordenados por série/número")
    @GetMapping("/ctes-disponiveis")
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public List<CteDisponivelMdfeResponse> listarCtesDisponiveis(@RequestParam Long empresaId) {
        return service.listarCtesDisponiveis(empresaId);
    }

    @Operation(summary = "Buscar MDF-e por id", description = "Inclui os CT-e vinculados e os municípios de descarga")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "MDF-e encontrado"),
            @ApiResponse(responseCode = "404", description = "MDF-e não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public MdfeCabecalhoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Gerar MDF-e agrupando CT-e autorizados",
            description = "Todo CT-e precisa estar AUTORIZADA, ser da empresa informada, não estar em outro MDF-e e ter destino na mesma UF do emitente. Carga/peso/municípios são derivados dos CT-e")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "MDF-e composto em RASCUNHO"),
            @ApiResponse(responseCode = "404", description = "CT-e, empresa ou cliente não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Algum CT-e já está vinculado a outro MDF-e", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "CT-e não autorizado, de outra empresa, duplicado, interestadual ou destinatário sem município", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "502", description = "Falha ao consultar o cadastro-base-service", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('FISCAL_EMITIR')")
    public ResponseEntity<MdfeCabecalhoResponse> gerar(@Valid @RequestBody GerarMdfeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.gerar(request));
    }

    @Operation(summary = "Emitir o MDF-e via ACBr Monitor",
            description = "Monta o INI (modelo 58) e envia MDFE.CriarEnviarMDFe — sucesso vira AUTORIZADA, rejeição fiscal vira REJEITADA, falha de comunicação vira ERRO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retorno do ACBr Monitor processado (AUTORIZADA/REJEITADA/ERRO)"),
            @ApiResponse(responseCode = "404", description = "MDF-e não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "MDF-e não está RASCUNHO/ERRO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/emitir")
    @PreAuthorize("hasAuthority('FISCAL_EMITIR')")
    public MdfeCabecalhoResponse emitir(@PathVariable Long id) {
        return service.emitir(id);
    }

    @Operation(summary = "Encerrar o MDF-e (fim da viagem)",
            description = "Evento 110112 via MDFE.EncerrarMDFe. Corpo opcional: sem data usa hoje, sem município usa o de descarga do último CT-e. Falha não muda a situação (continua AUTORIZADA)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento registrado — MDF-e ENCERRADA"),
            @ApiResponse(responseCode = "404", description = "MDF-e não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "MDF-e não está AUTORIZADA", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Data de encerramento inválida ou SEFAZ rejeitou o evento", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "502", description = "Falha de comunicação com o ACBr Monitor ou o cadastro-base-service", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/encerrar")
    @PreAuthorize("hasAuthority('FISCAL_EMITIR')")
    public MdfeCabecalhoResponse encerrar(@PathVariable Long id, @RequestBody(required = false) EncerrarMdfeRequest request) {
        return service.encerrar(id, request);
    }

    @Operation(summary = "Cancelar o MDF-e",
            description = "Evento 110111 via MDFE.CancelarMDFe. Só em até 24h da autorização e antes do encerramento; libera os CT-e para um novo MDF-e. Falha não muda a situação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento registrado — MDF-e CANCELADA"),
            @ApiResponse(responseCode = "400", description = "Justificativa ausente ou fora de 15 a 255 caracteres", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "MDF-e não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "MDF-e não está AUTORIZADA", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Prazo de 24h expirado ou SEFAZ rejeitou o evento", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "502", description = "Falha de comunicação com o ACBr Monitor ou o cadastro-base-service", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('FISCAL_GERENCIAR')")
    public MdfeCabecalhoResponse cancelar(@PathVariable Long id, @Valid @RequestBody CancelarMdfeRequest request) {
        return service.cancelar(id, request);
    }
}
