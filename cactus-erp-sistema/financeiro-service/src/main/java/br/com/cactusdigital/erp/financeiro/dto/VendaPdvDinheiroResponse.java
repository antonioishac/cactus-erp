package br.com.cactusdigital.erp.financeiro.dto;

/** {@code aplicado=false}: o lançamento já existia (reprocessamento do vendas-service), nada foi feito de novo. */
public record VendaPdvDinheiroResponse(boolean aplicado) {
}
