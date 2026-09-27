package br.com.cactuserp.pdv.entity;

/**
 * Status do ciclo de vida de uma venda (cupom) no PDV.
 *
 * <p>Espelha os valores da coluna {@code PDV_VENDA_CABECALHO.STATUS_VENDA}:
 * <ul>
 *   <li>{@link #A} — Aberta: cupom em curso, recebendo itens.</li>
 *   <li>{@link #F} — Fechada: cupom finalizado, pagamento confirmado e
 *       NFC-e/SAT autorizado pelo fisco.</li>
 *   <li>{@link #C} — Cancelada: cupom abortado pelo operador (F2) ou pelo
 *       sistema (descarte de cupom em aberto na recuperação).</li>
 *   <li>{@link #P} — Problemas: cupom finalizado no caixa mas com problema
 *       na transmissão fiscal (rejeição NFC-e, SEFAZ offline, etc).
 *       Exige tratamento manual ou retransmissão.</li>
 * </ul>
 *
 * <p>Transições válidas:
 * <pre>
 *   (novo) ──→ A ──→ F            (caminho feliz)
 *              │
 *              ├──→ C             (cancelamento)
 *              │
 *              └──→ P ──→ F       (problema resolvido por retransmissão)
 *                    │
 *                    └──→ C       (problema resolvido por cancelamento)
 * </pre>
 */
public enum StatusVenda {

    A, // Aberta
    F, // Fechada
    C, // Cancelada
    P; // Problemas

    /** Indica se o cupom ainda aceita modificações (itens, cancelamento, etc). */
    public boolean estaEmCurso() {
        return this == A;
    }

    /** Indica se o cupom já foi concluído com sucesso fiscal. */
    public boolean estaFinalizado() {
        return this == F;
    }

    /** Indica se o cupom precisa de intervenção (cancelamento ou retransmissão). */
    public boolean exigeIntervencao() {
        return this == P;
    }
}