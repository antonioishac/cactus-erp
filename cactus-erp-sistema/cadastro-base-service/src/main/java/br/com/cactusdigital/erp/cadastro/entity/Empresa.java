package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Filial/matriz da empresa (multi-filial) — o PDV, hoje, não tem esse conceito
 * ({@code LOJA_CONFIGURACAO} nele é sempre uma única loja). É este {@code id} que dá
 * escopo ao perfilamento de usuário no seguranca-service: um mesmo {@code Usuario} pode
 * ter papéis diferentes em cada empresa (ver {@code UsuarioPapelEmpresa} de lá — é uma
 * referência solta por {@code empresaId}, sem FK, já que os dois serviços têm schema
 * próprio).
 * <p>
 * Campos validados contra {@code fenix.sql.EMPRESA} (email/site/contato/dataConstituicao/
 * tipo/inscricaoJuntaComercial/cei/cnaePrincipal) e {@code BANCO_PDV.md.LOJA_CONFIGURACAO}
 * (tipoEstabelecimento/codigoIbgeUf) — nenhum desses existia na primeira passada.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "empresa")
public class Empresa extends BaseEntity {

    @Column(name = "razao_social", nullable = false, length = 150)
    private String razaoSocial;

    @Column(name = "nome_fantasia", length = 150)
    private String nomeFantasia;

    @Column(name = "cnpj", nullable = false, length = 14, unique = true)
    private String cnpj;

    @Column(name = "inscricao_estadual", length = 20)
    private String inscricaoEstadual;

    @Column(name = "crt", length = 1)
    private String crt;

    @Column(name = "email", length = 250)
    private String email;

    @Column(name = "site", length = 250)
    private String site;

    @Column(name = "contato", length = 100)
    private String contato;

    @Column(name = "data_constituicao")
    private LocalDate dataConstituicao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 10)
    private TipoEmpresa tipo;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "tipo_estabelecimento", length = 20, nullable = false)
    private TipoEstabelecimento tipoEstabelecimento = TipoEstabelecimento.VAREJO;

    @Column(name = "inscricao_junta_comercial", length = 30)
    private String inscricaoJuntaComercial;

    @Column(name = "data_inscricao_junta_comercial")
    private LocalDate dataInscricaoJuntaComercial;

    @Column(name = "cei", length = 12)
    private String cei;

    @Column(name = "codigo_cnae_principal", length = 7)
    private String codigoCnaePrincipal;

    /** N:1 — um escritório de contabilidade atende várias empresas/filiais. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contador_id")
    private Contador contador;

    @Column(name = "logradouro", length = 150)
    private String logradouro;

    @Column(name = "numero", length = 10)
    private String numero;

    @Column(name = "complemento", length = 100)
    private String complemento;

    @Column(name = "bairro", length = 100)
    private String bairro;

    @Column(name = "cep", length = 8)
    private String cep;

    @Column(name = "municipio", length = 100)
    private String municipio;

    @Column(name = "uf", length = 2)
    private String uf;

    @Column(name = "codigo_ibge_municipio")
    private Integer codigoIbgeMunicipio;

    @Column(name = "codigo_ibge_uf")
    private Integer codigoIbgeUf;

    @Column(name = "telefone", length = 15)
    private String telefone;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
