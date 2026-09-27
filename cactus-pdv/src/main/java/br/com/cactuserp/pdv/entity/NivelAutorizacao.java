package br.com.cactuserp.pdv.entity;

public enum NivelAutorizacao {
    G, // Gerente
    S, // Supervisor
    O, // Operador
    E; // Entregador

    public boolean podeAutorizarMovimento() {
        return this == G || this == S;
    }
}