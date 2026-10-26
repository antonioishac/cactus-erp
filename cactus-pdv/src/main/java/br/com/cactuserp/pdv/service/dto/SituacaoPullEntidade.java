package br.com.cactuserp.pdv.service.dto;

/** Situação do pull de uma entidade ({@code SINCRONIZACAO_CURSOR}) para a tela de sincronização. */
public record SituacaoPullEntidade(String entidade, String cursor, String ultimaExecucao, String ultimoErro) {

    /** Nome do cadastro para o operador. */
    public String entidadeDescricao() {
        return switch (entidade) {
            case "PRODUTO" -> "Produtos";
            case "CLIENTE" -> "Clientes";
            case "FORMA_PAGAMENTO" -> "Formas de pagamento";
            case "OPERADOR" -> "Operadores";
            case "CONFIGURACAO_LOJA" -> "Configuração da loja";
            default -> entidade;
        };
    }

    public boolean comErro() {
        return ultimoErro != null && !ultimoErro.isBlank();
    }
}
