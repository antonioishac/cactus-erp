package br.com.cactusdigital.erp.fiscal.sefaz;

import br.com.cactusdigital.erp.fiscal.client.dto.ClienteFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaConfiguracaoFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronica;

/**
 * Porta de saída da emissão de NF-e (modelo 55). O domínio fala só com esta interface; a implementação
 * ({@link SefazEmissorNfe}) é a única que conhece a biblioteca de documentos fiscais — trocar de biblioteca, ou voltar a
 * um middleware, não toca o {@code NotaFiscalEletronicaService}.
 */
public interface EmissorNfe {

    /**
     * Monta, assina, valida e transmite a nota (modo síncrono).
     *
     * @throws br.com.cactusdigital.erp.common.exception.BusinessException documento inválido, certificado ou ambiente
     *         não configurados — nada foi enviado.
     * @throws FalhaComunicacaoSefazException nota assinada, sem resposta utilizável da SEFAZ.
     */
    RetornoSefazNfe autorizar(NotaFiscalEletronica nota, EmpresaFiscalDTO emitente, ClienteFiscalDTO destinatario,
                              ClienteFiscalDTO transportadora, EmpresaConfiguracaoFiscalDTO configuracao);
}
