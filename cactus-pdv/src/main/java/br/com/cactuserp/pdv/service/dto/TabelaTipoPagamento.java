package br.com.cactuserp.pdv.service.dto;

public class TabelaTipoPagamento {

    private Long id;
    private String tipo;
    private String valor;

    public TabelaTipoPagamento(Long id, String tipo, String valor) {
        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
