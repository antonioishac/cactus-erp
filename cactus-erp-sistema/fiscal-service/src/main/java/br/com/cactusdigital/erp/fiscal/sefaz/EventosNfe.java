package br.com.cactusdigital.erp.fiscal.sefaz;

import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaConfiguracaoFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronica;

/**
 * Porta de saída dos pedidos à SEFAZ que acontecem depois (ou no lugar) da autorização de uma NF-e. Separada de
 * {@link EmissorNfe} de propósito: autorizar e registrar eventos são responsabilidades diferentes, com regras e
 * retornos diferentes.
 *
 * <p>Ambos os métodos: rejeição volta como {@link RetornoEventoSefaz} com {@code homologado=false}; falha de
 * comunicação lança {@link FalhaComunicacaoSefazException}; certificado/ambiente lançam
 * {@link br.com.cactusdigital.erp.common.exception.BusinessException}.</p>
 */
public interface EventosNfe {

    /** Evento 110111. A nota precisa ter chave e protocolo de autorização. */
    RetornoEventoSefaz cancelar(NotaFiscalEletronica nota, EmpresaFiscalDTO emitente, EmpresaConfiguracaoFiscalDTO configuracao,
                                String justificativa);

    /** Inutilização da faixa {@code [numeroInicial, numeroFinal]} da série, no ano corrente (horário de Brasília). */
    RetornoEventoSefaz inutilizar(AmbienteFiscal ambiente, EmpresaFiscalDTO emitente, EmpresaConfiguracaoFiscalDTO configuracao,
                                  String serie, long numeroInicial, long numeroFinal, int ano, String justificativa);
}
