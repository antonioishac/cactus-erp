package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.TipoEmpresa;
import br.com.cactusdigital.erp.cadastro.entity.TipoEstabelecimento;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record EmpresaRequest(
        @NotBlank @Size(max = 150) String razaoSocial,
        @Size(max = 150) String nomeFantasia,
        @Pattern(regexp = "\\d{14}", message = "CNPJ deve ter 14 dígitos") String cnpj,
        @Size(max = 20) String inscricaoEstadual,
        @Pattern(regexp = "[123]", message = "CRT deve ser 1, 2 ou 3") String crt,
        @Email @Size(max = 250) String email,
        @Size(max = 250) String site,
        @Size(max = 100) String contato,
        LocalDate dataConstituicao,
        TipoEmpresa tipo,
        @NotNull TipoEstabelecimento tipoEstabelecimento,
        @Size(max = 30) String inscricaoJuntaComercial,
        LocalDate dataInscricaoJuntaComercial,
        @Size(max = 12) String cei,
        @Size(max = 7) String codigoCnaePrincipal,
        Long contadorId,
        @Size(max = 150) String logradouro,
        @Size(max = 10) String numero,
        @Size(max = 100) String complemento,
        @Size(max = 100) String bairro,
        @Pattern(regexp = "\\d{8}", message = "CEP deve ter 8 dígitos") String cep,
        @Size(max = 100) String municipio,
        @Size(min = 2, max = 2) String uf,
        Integer codigoIbgeMunicipio,
        Integer codigoIbgeUf,
        @Size(max = 15) String telefone,
        @NotNull Situacao situacao) {
}
