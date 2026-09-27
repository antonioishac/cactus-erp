package br.com.cactusdigital.erp.estoque.entity;

/** Média ponderada é o default e o método obrigatório na maioria dos regimes fiscais no
 * Brasil; FIFO é alternativa configurável por empresa (usa {@link CamadaCustoFifo}). */
public enum MetodoCusteio {
    MEDIA_PONDERADA,
    FIFO
}
