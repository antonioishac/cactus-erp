package br.com.cactusdigital.erp.crm.entity;

/**
 * Classifica um {@link EstagioFunil} quanto ao efeito de mover uma {@link Oportunidade}
 * pra ele — mesmo espírito de {@code OsStatus} (catálogo livre, não enum fixo de
 * estágios), mas aqui a MÁQUINA DE MOVIMENTAÇÃO precisa saber quando um estágio fecha o
 * negócio (ganho ou perdido) pra disparar o efeito colateral certo em
 * {@code OportunidadeService#moverEstagio} (setar {@code situacao}/{@code dataFechamento},
 * exigir {@code motivoPerda} quando for {@code PERDIDO}).
 */
public enum TipoEncerramentoFunil {
    ABERTO,
    GANHO,
    PERDIDO
}
