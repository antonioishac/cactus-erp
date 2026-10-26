package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.entity.ClassificacaoTributariaIbsCbs;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * O que o ERP já sabe emitir em IBS/CBS. Cada CST exige grupos próprios no XML (NT 2025.002-RTC); o emissor
 * hoje monta o grupo padrão {@code gIBSCBS} (com redução de alíquota quando o CST manda) e o caso sem valores
 * (isenção/imunidade). Classificação que exige outro grupo é RECUSADA na configuração, com o motivo — melhor
 * que gerar uma NF-e que a SEFAZ rejeitaria.
 *
 * <p>Fora por enquanto: 220/221/222 (alíquota fixa/base reduzida), 510/515 (diferimento, grupo gDif), 550
 * (suspensão), 620 (monofásica, gIBSCBSMono), 800 (transferência de crédito), 810/811 (ajustes), 820
 * (documento específico), 830, e qualquer classificação com tributação regular (grupo gTribRegular).
 */
@Component
public class RegrasClassificacaoIbsCbs {

    static final Set<String> CST_SUPORTADOS = Set.of("000", "010", "011", "200", "400", "410");

    public boolean cstSuportado(String cst) {
        return CST_SUPORTADOS.contains(cst);
    }

    /** Motivo pelo qual a classificação não pode ser usada, ou vazio se pode. */
    public Optional<String> motivoNaoSuportada(ClassificacaoTributariaIbsCbs c) {
        if (!cstSuportado(c.getCst().getCodigo())) {
            return Optional.of("CST " + c.getCst().getCodigo() + " (" + c.getCst().getDescricao() + ") exige grupos do XML ainda não emitidos pelo ERP");
        }
        if (c.isTributacaoRegular()) {
            return Optional.of("Classificação exige o grupo de tributação regular (gTribRegular), ainda não emitido pelo ERP");
        }
        if (!c.isAceitaNfe() && !c.isAceitaNfce()) {
            return Optional.of("Classificação não é aceita em NF-e nem NFC-e pela tabela oficial");
        }
        return Optional.empty();
    }
}
