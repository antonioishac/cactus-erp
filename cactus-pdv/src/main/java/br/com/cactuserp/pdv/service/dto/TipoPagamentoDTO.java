package br.com.cactuserp.pdv.service.dto;

public class TipoPagamentoDTO {

    private Long id;

    private String codigo;

    private String descricao;

    // S=SIM | N=NAO
    private String tef;

    // S=SIM | N=NAO
    private String imprimeVinculado;

    // S=SIM | N=NAO
    private String permiteTroco;

    // 1=TEFDial | 2=TECBAN | 3=TEFHiperlink | N=Não
    private String tefTipoGp;

    // S=SIM | N=NAO
    private String geraParcelas;

    private String codigoPagamentoNfce;

    private String icone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTef() {
        return tef;
    }

    public void setTef(String tef) {
        this.tef = tef;
    }

    public String getImprimeVinculado() {
        return imprimeVinculado;
    }

    public void setImprimeVinculado(String imprimeVinculado) {
        this.imprimeVinculado = imprimeVinculado;
    }

    public String getPermiteTroco() {
        return permiteTroco;
    }

    public void setPermiteTroco(String permiteTroco) {
        this.permiteTroco = permiteTroco;
    }

    public String getTefTipoGp() {
        return tefTipoGp;
    }

    public void setTefTipoGp(String tefTipoGp) {
        this.tefTipoGp = tefTipoGp;
    }

    public String getGeraParcelas() {
        return geraParcelas;
    }

    public void setGeraParcelas(String geraParcelas) {
        this.geraParcelas = geraParcelas;
    }

    public String getCodigoPagamentoNfce() {
        return codigoPagamentoNfce;
    }

    public void setCodigoPagamentoNfce(String codigoPagamentoNfce) {
        this.codigoPagamentoNfce = codigoPagamentoNfce;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }
}
