package br.com.cactuserp.pdv.service.fiscal;

import com.fincatto.documentofiscal.nfe.NFeConfig;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;

/**
 * Porta de saída de tudo que o PDV fala com a SEFAZ sobre NFC-e. Única fronteira com a rede: o resto do PDV (e os
 * testes) trabalha com os retornos já interpretados. Implementação real: {@link SefazTransmissorNfce}.
 *
 * <p>Todos os métodos lançam {@link FalhaComunicacaoNfceException} quando não há resposta utilizável.</p>
 */
public interface TransmissorNfce {

    /** Transmite o lote assinado (modo síncrono). {@code notaAssinada} serve para montar o {@code nfeProc}. */
    RetornoAutorizacao autorizar(String xmlLote, NFNota notaAssinada, NFeConfig config);

    /** Consulta pela chave. Autorizada e com {@code notaAssinada} informada: o retorno traz o {@code nfeProc}. */
    RetornoConsulta consultar(String chaveAcesso, NFNota notaAssinada, NFeConfig config);

    /** Status do serviço de autorização (cStat 107 = em operação). */
    boolean servicoEmOperacao(NFeConfig config);

    /** Evento 110111. */
    RetornoEvento cancelar(String chaveAcesso, String protocoloAutorizacao, String justificativa, NFeConfig config);

    /**
     * Evento 110112 (só NFC-e): cancela uma NFC-e autorizada que foi substituída por outra emitida em contingência
     * offline.
     */
    RetornoEvento cancelarPorSubstituicao(String chaveAcesso, String protocoloAutorizacao, String justificativa,
                                          String versaoAplicativoSubstituta, String chaveSubstituta, NFeConfig config);

    /** Inutilização de um número (faixa de um). {@code ano} com 2 dígitos. */
    RetornoEvento inutilizar(int ano, String cnpj, int serie, long numero, String justificativa, NFeConfig config);

    enum Resultado {
        AUTORIZADA,
        DENEGADA,
        REJEITADA,
        /** cStat 204/539: a SEFAZ já tem este número — consultar a chave para saber o que aconteceu. */
        DUPLICIDADE
    }

    /** @param xmlProcessado {@code nfeProc}; só em AUTORIZADA/DENEGADA. */
    record RetornoAutorizacao(Resultado resultado, String codigoStatus, String motivo, String protocolo,
                              String versaoAplicativo, String xmlProcessado) {
    }

    /**
     * @param situacao AUTORIZADA (100/150), DENEGADA, CANCELADA (101/151/155) ou NAO_ENCONTRADA (217).
     * @param xmlProcessado {@code nfeProc} quando autorizada e a nota assinada foi informada.
     */
    record RetornoConsulta(SituacaoConsulta situacao, String codigoStatus, String motivo, String protocolo,
                           String versaoAplicativo, String xmlProcessado) {
    }

    enum SituacaoConsulta {
        AUTORIZADA,
        DENEGADA,
        CANCELADA,
        NAO_ENCONTRADA,
        OUTRA
    }

    /** @param xml {@code procEventoNFe} (cancelamento) ou retorno assinado pela SEFAZ (substituição, inutilização). */
    record RetornoEvento(boolean homologado, String codigoStatus, String motivo, String protocolo, String xml) {
    }
}
