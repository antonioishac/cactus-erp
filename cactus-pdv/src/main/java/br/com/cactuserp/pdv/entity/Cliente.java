package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, columnDefinition = "INT UNSIGNED")
    private Long id;

    @Column(name = "NOME", length = 150)
    private String nome;

    @Column(name = "FANTASIA", length = 150)
    private String fantasia;

    @Column(name = "EMAIL", length = 250)
    private String email;

    @Column(name = "URL", length = 250)
    private String url;

    @Column(name = "CPF_CNPJ", length = 14)
    private String cpfCnpj;

    @Column(name = "RG", length = 20)
    private String rg;

    @Column(name = "ORGAO_RG", length = 20)
    private String orgaoRg;

    @Column(name = "DATA_EMISSAO_RG")
    private LocalDate dataEmissaoRg;

    @Enumerated(EnumType.STRING)
    @Column(name = "SEXO", length = 1, columnDefinition = "CHAR(1)")
    private Sexo sexo;

    @Column(name = "INSCRICAO_ESTADUAL", length = 30)
    private String inscricaoEstadual;

    @Column(name = "INSCRICAO_MUNICIPAL", length = 30)
    private String inscricaoMunicipal;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_PESSOA", length = 1, columnDefinition = "CHAR(1)")
    private TipoPessoa tipoPessoa;

    @Column(name = "DATA_CADASTRO")
    private LocalDate dataCadastro;

    @Column(name = "LOGRADOURO", length = 250)
    private String logradouro;

    @Column(name = "NUMERO", length = 10)
    private String numero;

    @Column(name = "COMPLEMENTO", length = 100)
    private String complemento;

    @Column(name = "CEP", length = 8)
    private String cep;

    @Column(name = "BAIRRO", length = 100)
    private String bairro;

    @Column(name = "CIDADE", length = 100)
    private String cidade;

    @Column(name = "UF", length = 2, columnDefinition = "CHAR(2)")
    private String uf;

    @Column(name = "TELEFONE", length = 15)
    private String telefone;

    @Column(name = "CELULAR", length = 15)
    private String celular;

    @Column(name = "CONTATO", length = 50)
    private String contato;

    @Column(name = "CODIGO_IBGE_CIDADE", columnDefinition = "INT UNSIGNED")
    private Long codigoIbgeCidade;

    @Column(name = "CODIGO_IBGE_UF", columnDefinition = "INT UNSIGNED")
    private Long codigoIbgeUf;

    @Enumerated(EnumType.STRING)
    @Column(name = "FIDELIDADE_AVISO", length = 1, columnDefinition = "CHAR(1)")
    private FidelidadeAviso fidelidadeAviso;

    @Column(name = "FIDELIDADE_QUANTIDADE")
    private Integer fidelidadeQuantidade;

    @Column(name = "FIDELIDADE_VALOR", precision = 18, scale = 6)
    private BigDecimal fidelidadeValor;

    @Column(name = "FIADO_VALOR_TETO", precision = 18, scale = 6)
    private BigDecimal fiadoValorTeto;

    // ===== Construtores =====

    public Cliente() {
    }

    // ===== Getters e Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFantasia() {
        return fantasia;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getOrgaoRg() {
        return orgaoRg;
    }

    public void setOrgaoRg(String orgaoRg) {
        this.orgaoRg = orgaoRg;
    }

    public LocalDate getDataEmissaoRg() {
        return dataEmissaoRg;
    }

    public void setDataEmissaoRg(LocalDate dataEmissaoRg) {
        this.dataEmissaoRg = dataEmissaoRg;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getInscricaoMunicipal() {
        return inscricaoMunicipal;
    }

    public void setInscricaoMunicipal(String inscricaoMunicipal) {
        this.inscricaoMunicipal = inscricaoMunicipal;
    }

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public Long getCodigoIbgeCidade() {
        return codigoIbgeCidade;
    }

    public void setCodigoIbgeCidade(Long codigoIbgeCidade) {
        this.codigoIbgeCidade = codigoIbgeCidade;
    }

    public Long getCodigoIbgeUf() {
        return codigoIbgeUf;
    }

    public void setCodigoIbgeUf(Long codigoIbgeUf) {
        this.codigoIbgeUf = codigoIbgeUf;
    }

    public FidelidadeAviso getFidelidadeAviso() {
        return fidelidadeAviso;
    }

    public void setFidelidadeAviso(FidelidadeAviso fidelidadeAviso) {
        this.fidelidadeAviso = fidelidadeAviso;
    }

    public Integer getFidelidadeQuantidade() {
        return fidelidadeQuantidade;
    }

    public void setFidelidadeQuantidade(Integer fidelidadeQuantidade) {
        this.fidelidadeQuantidade = fidelidadeQuantidade;
    }

    public BigDecimal getFidelidadeValor() {
        return fidelidadeValor;
    }

    public void setFidelidadeValor(BigDecimal fidelidadeValor) {
        this.fidelidadeValor = fidelidadeValor;
    }

    public BigDecimal getFiadoValorTeto() {
        return fiadoValorTeto;
    }

    public void setFiadoValorTeto(BigDecimal fiadoValorTeto) {
        this.fiadoValorTeto = fiadoValorTeto;
    }

    // ===== equals, hashCode e toString =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente cliente = (Cliente) o;
        return id != null && Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cpfCnpj='" + cpfCnpj + '\'' +
                ", tipoPessoa=" + tipoPessoa +
                '}';
    }
}