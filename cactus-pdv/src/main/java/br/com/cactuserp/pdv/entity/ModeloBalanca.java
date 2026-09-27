package br.com.cactuserp.pdv.entity;

/**
 * Modelo de balança configurado, mapeado por posição (ORDINAL) para o
 * inteiro semeado em {@code PDV_CONFIGURACAO_BALANCA.MODELO}
 * (0=Nenhum, 1=Filizola, 2=Toledo, ..., 8=Magellan — ver PDV.md). A ordem
 * dos valores abaixo é parte do contrato com o banco — não reordenar.
 */
public enum ModeloBalanca {
    NENHUM,      // 0
    FILIZOLA,    // 1
    TOLEDO,      // 2
    TOLEDO2180,  // 3
    URANO,       // 4
    LUCASTEC,    // 5
    MAGNA,       // 6
    DIGITRON,    // 7
    MAGELLAN     // 8
}
