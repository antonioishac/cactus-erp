package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.TipoEmpresa;
import br.com.cactusdigital.erp.cadastro.entity.TipoEstabelecimento;
import br.com.cactusdigital.erp.common.domain.Situacao;
import java.time.LocalDate;

public record EmpresaResponse(
        Long id,
        String razaoSocial,
        String nomeFantasia,
        String cnpj,
        String inscricaoEstadual,
        String crt,
        String email,
        String site,
        String contato,
        LocalDate dataConstituicao,
        TipoEmpresa tipo,
        TipoEstabelecimento tipoEstabelecimento,
        String inscricaoJuntaComercial,
        LocalDate dataInscricaoJuntaComercial,
        String cei,
        String codigoCnaePrincipal,
        Long contadorId,
        String contadorNome,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cep,
        String municipio,
        String uf,
        Integer codigoIbgeMunicipio,
        Integer codigoIbgeUf,
        String telefone,
        Situacao situacao) {
}
