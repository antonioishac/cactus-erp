package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.ConfiguracaoOperacionalLojaRequest;
import br.com.cactusdigital.erp.cadastro.dto.ConfiguracaoOperacionalLojaResponse;
import br.com.cactusdigital.erp.cadastro.dto.EmpresaConfiguracaoFiscalRequest;
import br.com.cactusdigital.erp.cadastro.dto.EmpresaConfiguracaoFiscalResponse;
import br.com.cactusdigital.erp.cadastro.dto.EmpresaRegimeTributarioRequest;
import br.com.cactusdigital.erp.cadastro.dto.EmpresaRegimeTributarioResponse;
import br.com.cactusdigital.erp.cadastro.dto.EmpresaRequest;
import br.com.cactusdigital.erp.cadastro.dto.EmpresaResponse;
import br.com.cactusdigital.erp.cadastro.dto.NumeroFiscalPuladoRequest;
import br.com.cactusdigital.erp.cadastro.dto.NumeroFiscalPuladoResponse;
import br.com.cactusdigital.erp.cadastro.service.ConfiguracaoOperacionalLojaService;
import br.com.cactusdigital.erp.cadastro.service.EmpresaConfiguracaoFiscalService;
import br.com.cactusdigital.erp.cadastro.service.EmpresaRegimeTributarioService;
import br.com.cactusdigital.erp.cadastro.service.EmpresaService;
import org.springframework.http.ProblemDetail;
import br.com.cactusdigital.erp.common.rest.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Empresa", description = "Cadastro de empresa multi-filial — regime tributário, configuração operacional de loja e configuração fiscal")
@RestController
@RequestMapping("/api/v1/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;
    private final EmpresaRegimeTributarioService regimeTributarioService;
    private final ConfiguracaoOperacionalLojaService configuracaoOperacionalLojaService;
    private final EmpresaConfiguracaoFiscalService configuracaoFiscalService;

    @Operation(summary = "Listar empresas", description = "Busca paginada por razão social ou nome fantasia, opcional")
    @ApiResponse(responseCode = "200", description = "Página de empresas")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_CONSULTAR')")
    public PageResponse<EmpresaResponse> listar(@RequestParam(required = false) String nome, Pageable pageable) {
        return PageResponse.of(empresaService.listar(nome, pageable));
    }

    @Operation(summary = "Buscar empresa por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empresa encontrada"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_CONSULTAR')")
    public EmpresaResponse buscarPorId(@PathVariable Long id) {
        return empresaService.buscarPorId(id);
    }

    @Operation(summary = "Criar empresa (matriz ou filial)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Empresa criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Contador informado não existe",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_CRIAR')")
    public ResponseEntity<EmpresaResponse> criar(@Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empresaService.criar(request));
    }

    @Operation(summary = "Atualizar empresa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empresa atualizada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Empresa ou contador não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_EDITAR')")
    public EmpresaResponse atualizar(@PathVariable Long id, @Valid @RequestBody EmpresaRequest request) {
        return empresaService.atualizar(id, request);
    }

    @Operation(summary = "Excluir empresa")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Empresa excluída"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        empresaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar histórico de regime tributário", description = "Um registro por ano-calendário — CRT pode mudar de um ano pro outro")
    @ApiResponse(responseCode = "200", description = "Histórico de regime tributário")
    @GetMapping("/{id}/regime-tributario")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_CONSULTAR')")
    public List<EmpresaRegimeTributarioResponse> listarRegimeTributario(@PathVariable Long id) {
        return regimeTributarioService.listar(id);
    }

    @Operation(summary = "Registrar regime tributário do ano")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Regime tributário registrado"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Já existe regime tributário registrado para esse ano",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/regime-tributario")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_EDITAR')")
    public ResponseEntity<EmpresaRegimeTributarioResponse> registrarRegimeTributario(
            @PathVariable Long id, @Valid @RequestBody EmpresaRegimeTributarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(regimeTributarioService.registrar(id, request));
    }

    @Operation(summary = "Buscar configuração operacional da loja", description = "Mesas/comandas/delivery + limites de desconto/acréscimo — empurrada pro PDV no provisionamento do terminal")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuração operacional encontrada"),
            @ApiResponse(responseCode = "404", description = "Empresa ainda não tem configuração operacional salva",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}/configuracao-operacional")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_CONSULTAR')")
    public ConfiguracaoOperacionalLojaResponse buscarConfiguracaoOperacional(@PathVariable Long id) {
        return configuracaoOperacionalLojaService.buscarPorEmpresa(id);
    }

    @Operation(summary = "Criar/atualizar configuração operacional da loja", description = "Upsert — cria na primeira chamada, atualiza nas seguintes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuração operacional salva"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}/configuracao-operacional")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_EDITAR')")
    public ConfiguracaoOperacionalLojaResponse salvarConfiguracaoOperacional(
            @PathVariable Long id, @Valid @RequestBody ConfiguracaoOperacionalLojaRequest request) {
        return configuracaoOperacionalLojaService.salvar(id, request);
    }

    @Operation(summary = "Buscar configuração fiscal da filial", description = "Certificado digital, série/numeração NFe/NFCe/NFSe, ambiente SEFAZ e CSC")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuração fiscal encontrada"),
            @ApiResponse(responseCode = "404", description = "Empresa ainda não tem configuração fiscal salva",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}/configuracao-fiscal")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_CONSULTAR')")
    public EmpresaConfiguracaoFiscalResponse buscarConfiguracaoFiscal(@PathVariable Long id) {
        return configuracaoFiscalService.buscarPorEmpresa(id);
    }

    @Operation(summary = "Criar/atualizar configuração fiscal da filial",
            description = "Upsert. certificadoAlias referencia uma entrada de keystore/secret manager — a senha do certificado nunca trafega aqui")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuração fiscal salva"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}/configuracao-fiscal")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_EDITAR')")
    public EmpresaConfiguracaoFiscalResponse salvarConfiguracaoFiscal(
            @PathVariable Long id, @Valid @RequestBody EmpresaConfiguracaoFiscalRequest request) {
        return configuracaoFiscalService.salvar(id, request);
    }

    @Operation(summary = "Reservar o próximo número de NF-e da filial",
            description = "Devolve o número reservado (o corrente) e incrementa o contador pro próximo — consumido pelo fiscal-service ao compor uma NF-e a partir de uma Venda")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Número reservado"),
            @ApiResponse(responseCode = "404", description = "Empresa ainda não tem configuração fiscal salva",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Configuração fiscal incompleta (sem série de NF-e)",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/configuracao-fiscal/reservar-numero-nfe")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_EDITAR')")
    public EmpresaConfiguracaoFiscalResponse reservarProximoNumeroNfe(@PathVariable Long id) {
        return configuracaoFiscalService.reservarProximoNumeroNfe(id);
    }

    @Operation(summary = "Reservar o próximo número de CT-e da filial",
            description = "Devolve o número reservado (o corrente) e incrementa o contador pro próximo — consumido pelo fiscal-service ao compor um CT-e a partir do frete de uma Venda")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Número reservado"),
            @ApiResponse(responseCode = "404", description = "Empresa ainda não tem configuração fiscal salva",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Configuração fiscal incompleta (sem série de CT-e)",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/configuracao-fiscal/reservar-numero-cte")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_EDITAR')")
    public EmpresaConfiguracaoFiscalResponse reservarProximoNumeroCte(@PathVariable Long id) {
        return configuracaoFiscalService.reservarProximoNumeroCte(id);
    }

    @Operation(summary = "Reservar o próximo número de NFC-e da filial",
            description = "Devolve o número reservado (o corrente) e incrementa o contador pro próximo — consumido pelo fiscal-service ao compor uma NFC-e a partir de um Pedido do loja-virtual-service")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Número reservado"),
            @ApiResponse(responseCode = "404", description = "Empresa ainda não tem configuração fiscal salva",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Configuração fiscal incompleta (sem série de NFC-e)",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/configuracao-fiscal/reservar-numero-nfce")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_EDITAR')")
    public EmpresaConfiguracaoFiscalResponse reservarProximoNumeroNfce(@PathVariable Long id) {
        return configuracaoFiscalService.reservarProximoNumeroNfce(id);
    }

    @Operation(summary = "Reservar o próximo número de MDF-e da filial",
            description = "Devolve o número reservado (o corrente) e incrementa o contador pro próximo — consumido pelo fiscal-service ao compor um MDF-e a partir de N CT-e já autorizados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Número reservado"),
            @ApiResponse(responseCode = "404", description = "Empresa ainda não tem configuração fiscal salva",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Configuração fiscal incompleta (sem série de MDF-e)",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/configuracao-fiscal/reservar-numero-mdfe")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_EDITAR')")
    public EmpresaConfiguracaoFiscalResponse reservarProximoNumeroMdfe(@PathVariable Long id) {
        return configuracaoFiscalService.reservarProximoNumeroMdfe(id);
    }

    @Operation(summary = "Registrar número fiscal pulado (compensação de saga)",
            description = "Chamado pelo fiscal-service quando um número já reservado (reservar-numero-xxx) nunca chegou a virar documento fiscal — NÃO devolve/decrementa o contador (risco de colisão com reserva concorrente); só registra o gap pra regularização manual futura via evento de inutilização. Ver Javadoc de NumeroFiscalPulado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro criado"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/configuracao-fiscal/numero-fiscal-pulado")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_EDITAR')")
    public ResponseEntity<NumeroFiscalPuladoResponse> registrarNumeroFiscalPulado(
            @PathVariable Long id, @Valid @RequestBody NumeroFiscalPuladoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(configuracaoFiscalService.registrarNumeroPulado(id, request));
    }

    @Operation(summary = "Listar números fiscais pulados da filial", description = "Visibilidade operacional sobre gaps de numeração — ver Javadoc de NumeroFiscalPulado")
    @ApiResponse(responseCode = "200", description = "Página de números pulados")
    @GetMapping("/{id}/configuracao-fiscal/numeros-fiscais-pulados")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_CONSULTAR')")
    public PageResponse<NumeroFiscalPuladoResponse> listarNumerosFiscaisPulados(@PathVariable Long id, Pageable pageable) {
        return PageResponse.of(configuracaoFiscalService.listarNumerosPulados(id, pageable));
    }
}
