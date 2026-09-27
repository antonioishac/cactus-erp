package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

/**
 * Configuração geral da loja (1 linha apenas no banco).
 *
 * <p>Define o tipo de estabelecimento e quais funcionalidades estão
 * habilitadas. No PDV, é apenas consultada (não editada) — o cadastro
 * é responsabilidade do sistema back-office (replicado via mensageria).
 *
 * <h3>Modos de comanda</h3>
 * <ul>
 *   <li><b>F</b> — cliente recebe código FÍSICO (cartão numerado).
 *       Ex: bar onde cliente pega cartão na entrada.</li>
 *   <li><b>V</b> — sistema gera código VIRTUAL automaticamente.
 *       Ex: padaria onde cliente fala o nome e pega depois.</li>
 *   <li><b>A</b> — AMBOS (operador escolhe na hora).</li>
 * </ul>
 *
 * <h3>Dados do emitente (fase 6 — fiscal)</h3>
 * <p>CNPJ/IE/razão social/endereço são os dados obrigatórios da seção
 * {@code [Emitente]} do layout NFCe.INI do ACBr — sem eles não é possível
 * montar um documento fiscal válido. Ver {@code AcbrComandoFiscalBuilder}.
 */
@Entity
@Table(name = "LOJA_CONFIGURACAO")
public class LojaConfiguracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_ESTABELECIMENTO", length = 20, nullable = false)
    private TipoEstabelecimento tipoEstabelecimento;

    @Column(name = "CNPJ", length = 14)
    private String cnpj;

    @Column(name = "INSCRICAO_ESTADUAL", length = 20)
    private String inscricaoEstadual;

    @Column(name = "RAZAO_SOCIAL", length = 150)
    private String razaoSocial;

    @Column(name = "NOME_FANTASIA", length = 150)
    private String nomeFantasia;

    /** 1=Simples Nacional | 2=Simples Nacional excesso sublimite | 3=Regime Normal. */
    @Column(name = "CRT", length = 1, columnDefinition = "CHAR(1)")
    private String crt;

    @Column(name = "LOGRADOURO", length = 150)
    private String logradouro;

    @Column(name = "NUMERO", length = 10)
    private String numero;

    @Column(name = "COMPLEMENTO", length = 100)
    private String complemento;

    @Column(name = "BAIRRO", length = 100)
    private String bairro;

    @Column(name = "CEP", length = 8)
    private String cep;

    @Column(name = "CODIGO_IBGE_CIDADE")
    private Long codigoIbgeCidade;

    @Column(name = "CIDADE", length = 100)
    private String cidade;

    @Column(name = "CODIGO_IBGE_UF")
    private Long codigoIbgeUf;

    @Column(name = "UF", length = 2, columnDefinition = "CHAR(2)")
    private String uf;

    @Column(name = "TELEFONE", length = 15)
    private String telefone;

    @Column(name = "PERMITE_MESAS", length = 1, nullable = false)
    private String permiteMesas;

    @Column(name = "PERMITE_COMANDAS", length = 1, nullable = false)
    private String permiteComandas;

    @Column(name = "PERMITE_DELIVERY", length = 1, nullable = false)
    private String permiteDelivery;

    /** F=Físico, V=Virtual, A=Ambos */
    @Column(name = "MODO_COMANDA", length = 1, nullable = false)
    private String modoComanda;

    @Column(name = "PERMITE_MESA_AVULSA", length = 1, nullable = false)
    private String permiteMesaAvulsa;

    public LojaConfiguracao() {}

    // ====== Helpers booleanos ======
    public boolean isPermiteMesas() { return "S".equals(permiteMesas); }
    public boolean isPermiteComandas() { return "S".equals(permiteComandas); }
    public boolean isPermiteDelivery() { return "S".equals(permiteDelivery); }
    public boolean isPermiteMesaAvulsa() { return "S".equals(permiteMesaAvulsa); }
    public boolean isComandaFisica() { return "F".equals(modoComanda) || "A".equals(modoComanda); }
    public boolean isComandaVirtual() { return "V".equals(modoComanda) || "A".equals(modoComanda); }

    // ====== Getters / Setters ======
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoEstabelecimento getTipoEstabelecimento() { return tipoEstabelecimento; }
    public void setTipoEstabelecimento(TipoEstabelecimento v) { this.tipoEstabelecimento = v; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String v) { this.cnpj = v; }

    public String getInscricaoEstadual() { return inscricaoEstadual; }
    public void setInscricaoEstadual(String v) { this.inscricaoEstadual = v; }

    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String v) { this.razaoSocial = v; }

    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String v) { this.nomeFantasia = v; }

    public String getCrt() { return crt; }
    public void setCrt(String v) { this.crt = v; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String v) { this.logradouro = v; }

    public String getNumero() { return numero; }
    public void setNumero(String v) { this.numero = v; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String v) { this.complemento = v; }

    public String getBairro() { return bairro; }
    public void setBairro(String v) { this.bairro = v; }

    public String getCep() { return cep; }
    public void setCep(String v) { this.cep = v; }

    public Long getCodigoIbgeCidade() { return codigoIbgeCidade; }
    public void setCodigoIbgeCidade(Long v) { this.codigoIbgeCidade = v; }

    public String getCidade() { return cidade; }
    public void setCidade(String v) { this.cidade = v; }

    public Long getCodigoIbgeUf() { return codigoIbgeUf; }
    public void setCodigoIbgeUf(Long v) { this.codigoIbgeUf = v; }

    public String getUf() { return uf; }
    public void setUf(String v) { this.uf = v; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String v) { this.telefone = v; }

    public String getPermiteMesas() { return permiteMesas; }
    public void setPermiteMesas(String v) { this.permiteMesas = v; }

    public String getPermiteComandas() { return permiteComandas; }
    public void setPermiteComandas(String v) { this.permiteComandas = v; }

    public String getPermiteDelivery() { return permiteDelivery; }
    public void setPermiteDelivery(String v) { this.permiteDelivery = v; }

    public String getModoComanda() { return modoComanda; }
    public void setModoComanda(String v) { this.modoComanda = v; }

    public String getPermiteMesaAvulsa() { return permiteMesaAvulsa; }
    public void setPermiteMesaAvulsa(String v) { this.permiteMesaAvulsa = v; }
}