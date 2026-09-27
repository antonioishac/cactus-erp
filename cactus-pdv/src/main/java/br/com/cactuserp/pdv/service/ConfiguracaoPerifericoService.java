package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.ModeloBalanca;
import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import br.com.cactuserp.pdv.entity.PdvConfiguracaoBalanca;
import br.com.cactuserp.pdv.repository.PdvConfiguracaoBalancaRepository;
import br.com.cactuserp.pdv.repository.PdvConfiguracaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Leitura/gravação da configuração de periféricos (balança, impressora,
 * gaveta, TEF) de um caixa — fase 7 do roadmap.
 *
 * <p>Cria a linha de {@link PdvConfiguracao}/{@link PdvConfiguracaoBalanca}
 * sob demanda se ainda não existir pra esse caixa (primeira configuração).
 */
@Service
@Transactional(readOnly = true)
public class ConfiguracaoPerifericoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfiguracaoPerifericoService.class);

    private final PdvConfiguracaoRepository configuracaoRepository;
    private final PdvConfiguracaoBalancaRepository configuracaoBalancaRepository;

    public ConfiguracaoPerifericoService(PdvConfiguracaoRepository configuracaoRepository,
                                         PdvConfiguracaoBalancaRepository configuracaoBalancaRepository) {
        this.configuracaoRepository = configuracaoRepository;
        this.configuracaoBalancaRepository = configuracaoBalancaRepository;
    }

    /** Busca (ou cria em memória, sem persistir) a configuração do caixa. */
    public PdvConfiguracao buscarOuNovaConfiguracao(PdvCaixa caixa) {
        return configuracaoRepository.findFirstByCaixaId(caixa.getId())
                .orElseGet(() -> {
                    PdvConfiguracao nova = new PdvConfiguracao();
                    nova.setCaixa(caixa);
                    return nova;
                });
    }

    /** Busca (ou cria em memória) a configuração de balança vinculada. */
    public PdvConfiguracaoBalanca buscarOuNovaConfiguracaoBalanca(PdvConfiguracao configuracao) {
        if (configuracao.getId() == null) {
            PdvConfiguracaoBalanca nova = new PdvConfiguracaoBalanca();
            nova.setModelo(ModeloBalanca.NENHUM);
            return nova;
        }
        return configuracaoBalancaRepository.findFirstByConfiguracaoId(configuracao.getId())
                .orElseGet(() -> {
                    PdvConfiguracaoBalanca nova = new PdvConfiguracaoBalanca();
                    nova.setConfiguracao(configuracao);
                    nova.setModelo(ModeloBalanca.NENHUM);
                    return nova;
                });
    }

    /**
     * Salva impressora/gaveta/TEF (em {@link PdvConfiguracao}) e a
     * configuração de balança ({@link PdvConfiguracaoBalanca}) numa
     * transação só.
     */
    @Transactional
    public void salvar(PdvConfiguracao configuracao, PdvConfiguracaoBalanca configuracaoBalanca) {
        PdvConfiguracao salva = configuracaoRepository.save(configuracao);

        configuracaoBalanca.setConfiguracao(salva);
        configuracaoBalancaRepository.save(configuracaoBalanca);

        LOGGER.info("Configuração de periféricos salva: caixa={}, impressora={}, balança={}, TEF configurado={}",
                salva.getCaixa() != null ? salva.getCaixa().getId() : null,
                salva.getNomeImpressoraCupom(),
                configuracaoBalanca.getModelo(),
                salva.getTefHiperlinkUrl() != null && !salva.getTefHiperlinkUrl().isBlank());
    }
}
