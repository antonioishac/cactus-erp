package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.Cliente;
import br.com.cactuserp.pdv.entity.FidelidadeAviso;
import br.com.cactuserp.pdv.entity.StatusVenda;
import br.com.cactuserp.pdv.repository.PdvVendaCabecalhoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Avalia se um cliente atingiu a meta de fidelidade configurada em
 * {@code CLIENTE.FIDELIDADE_AVISO}/{@code _QUANTIDADE}/{@code _VALOR}.
 *
 * <p>Não existe tabela de acúmulo/pontos no schema — a meta é calculada
 * <b>sob demanda</b>, contando/somando as vendas fechadas do cliente em
 * {@code PDV_VENDA_CABECALHO}. Isso só ficou viável depois da correção que
 * passou a persistir o cliente na venda de balcão (ver {@code VendaService
 * #atualizarCliente}) — antes disso, {@code PDV_VENDA_CABECALHO.ID_CLIENTE}
 * nunca era preenchido fora de mesa/comanda/delivery.
 *
 * <p>Sem "zerar" a contagem após um aviso — se a loja quiser resetar a
 * meta do cliente (ex: após resgatar um prêmio), isso é uma decisão de
 * produto que precisa de um campo próprio (ex: "última vez que atingiu a
 * meta") ainda não modelado. Documentado como limitação conhecida.
 */
@Service
@Transactional(readOnly = true)
public class FidelidadeService {

    private final PdvVendaCabecalhoRepository cabecalhoRepository;

    public FidelidadeService(PdvVendaCabecalhoRepository cabecalhoRepository) {
        this.cabecalhoRepository = cabecalhoRepository;
    }

    /**
     * Avalia a fidelidade de um cliente. Retorna {@link Resultado#semMeta()}
     * se o cliente não tem meta configurada (aviso null) — nesse caso a UI
     * não deve mostrar nada.
     */
    public Resultado avaliar(Cliente cliente) {
        if (cliente == null || cliente.getFidelidadeAviso() == null) {
            return Resultado.semMeta();
        }

        FidelidadeAviso tipo = cliente.getFidelidadeAviso();

        if (tipo == FidelidadeAviso.Q) {
            Integer meta = cliente.getFidelidadeQuantidade();
            if (meta == null || meta <= 0) return Resultado.semMeta();
            long atual = cabecalhoRepository.countByClienteAndStatusVenda(cliente, StatusVenda.F);
            return new Resultado(tipo, BigDecimal.valueOf(atual), BigDecimal.valueOf(meta), atual >= meta);
        }

        // tipo == V
        BigDecimal meta = cliente.getFidelidadeValor();
        if (meta == null || meta.signum() <= 0) return Resultado.semMeta();
        BigDecimal atual = cabecalhoRepository.somarValorComprasDoCliente(cliente).orElse(BigDecimal.ZERO);
        return new Resultado(tipo, atual, meta, atual.compareTo(meta) >= 0);
    }

    public static class Resultado {
        private final FidelidadeAviso tipo; // null = sem meta configurada
        private final BigDecimal atual;
        private final BigDecimal meta;
        private final boolean atingiuMeta;

        private Resultado(FidelidadeAviso tipo, BigDecimal atual, BigDecimal meta, boolean atingiuMeta) {
            this.tipo = tipo;
            this.atual = atual;
            this.meta = meta;
            this.atingiuMeta = atingiuMeta;
        }

        static Resultado semMeta() {
            return new Resultado(null, BigDecimal.ZERO, BigDecimal.ZERO, false);
        }

        public boolean temMetaConfigurada() { return tipo != null; }
        public boolean atingiuMeta() { return atingiuMeta; }
        public FidelidadeAviso getTipo() { return tipo; }
        public BigDecimal getAtual() { return atual; }
        public BigDecimal getMeta() { return meta; }

        /** Mensagem pronta pra exibir num Alert/banner, ou null se não há o que avisar. */
        public String mensagemOuNull(String nomeCliente) {
            if (!atingiuMeta) return null;
            String base = "Cliente " + nomeCliente + " atingiu a meta de fidelidade";
            return tipo == FidelidadeAviso.Q
                    ? base + " (" + atual.toBigInteger() + " compras)."
                    : base + " (R$ " + atual.setScale(2, java.math.RoundingMode.HALF_UP) + " em compras).";
        }
    }
}
