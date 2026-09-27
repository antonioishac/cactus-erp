package br.com.cactuserp.pdv.entity;

/**
 * Status do ciclo de vida de uma conta aberta.
 *
 * <pre>
 *  A (Aberta)        — recebendo itens; visível no salão / busca
 *      ↓
 *  E (Em atendimento)— operador do caixa importou pro PDV, cliente está pagando
 *      ↓
 *  F (Fechada)       — cupom de venda gerado e finalizado
 *
 *  X (Cancelada)     — qualquer momento antes de F
 * </pre>
 */
public enum StatusContaAberta {
    A,  // Aberta
    E,  // Em atendimento (importada pro PDV, pagamento em curso)
    F,  // Fechada (venda gerada)
    X;  // Cancelada

    public String getRotulo() {
        switch (this) {
            case A: return "Aberta";
            case E: return "Em atendimento";
            case F: return "Fechada";
            case X: return "Cancelada";
            default: return name();
        }
    }
}