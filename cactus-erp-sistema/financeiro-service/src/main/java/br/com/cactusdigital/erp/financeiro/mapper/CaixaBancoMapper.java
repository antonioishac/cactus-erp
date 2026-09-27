package br.com.cactusdigital.erp.financeiro.mapper;

import br.com.cactusdigital.erp.financeiro.dto.AplicacaoFinanceiraResponse;
import br.com.cactusdigital.erp.financeiro.dto.BancoResponse;
import br.com.cactusdigital.erp.financeiro.dto.ChequeEmitidoResponse;
import br.com.cactusdigital.erp.financeiro.dto.ChequeRecebidoResponse;
import br.com.cactusdigital.erp.financeiro.dto.ContaBancariaResponse;
import br.com.cactusdigital.erp.financeiro.dto.ExtratoBancarioImportacaoResponse;
import br.com.cactusdigital.erp.financeiro.dto.ExtratoBancarioItemResponse;
import br.com.cactusdigital.erp.financeiro.dto.MovimentoCaixaBancoResponse;
import br.com.cactusdigital.erp.financeiro.dto.NaturezaFinanceiraResponse;
import br.com.cactusdigital.erp.financeiro.dto.TipoDocumentoFinanceiroResponse;
import br.com.cactusdigital.erp.financeiro.dto.TransferenciaBancariaResponse;
import br.com.cactusdigital.erp.financeiro.entity.AplicacaoFinanceira;
import br.com.cactusdigital.erp.financeiro.entity.Banco;
import br.com.cactusdigital.erp.financeiro.entity.ChequeEmitido;
import br.com.cactusdigital.erp.financeiro.entity.ChequeRecebido;
import br.com.cactusdigital.erp.financeiro.entity.ContaBancaria;
import br.com.cactusdigital.erp.financeiro.entity.ExtratoBancarioImportacao;
import br.com.cactusdigital.erp.financeiro.entity.ExtratoBancarioItem;
import br.com.cactusdigital.erp.financeiro.entity.MovimentoCaixaBanco;
import br.com.cactusdigital.erp.financeiro.entity.NaturezaFinanceira;
import br.com.cactusdigital.erp.financeiro.entity.TipoDocumentoFinanceiro;
import br.com.cactusdigital.erp.financeiro.entity.TransferenciaBancaria;
import org.mapstruct.Mapper;

/**
 * Mapper das entidades de Caixa/Bancos/Cheque — separado de {@link FinanceiroMapper}
 * (que já cuidava só de Contas a Pagar/Receber) por SRP, mesmo critério de módulo
 * organizado por camada técnica global (não por domínio) usado no resto do monorepo:
 * um mapper por agrupamento coeso de entidades, não necessariamente um por módulo
 * inteiro. Todos os campos batem por nome entre entidade e DTO — nenhuma expressão
 * customizada necessária aqui (diferente de {@code FinanceiroMapper}, que calcula
 * juros/multa/vencido).
 */
@Mapper(componentModel = "spring")
public interface CaixaBancoMapper {

    BancoResponse toResponse(Banco banco);

    NaturezaFinanceiraResponse toResponse(NaturezaFinanceira natureza);

    TipoDocumentoFinanceiroResponse toResponse(TipoDocumentoFinanceiro tipo);

    ContaBancariaResponse toResponse(ContaBancaria conta);

    MovimentoCaixaBancoResponse toResponse(MovimentoCaixaBanco movimento);

    ChequeEmitidoResponse toResponse(ChequeEmitido cheque);

    ChequeRecebidoResponse toResponse(ChequeRecebido cheque);

    TransferenciaBancariaResponse toResponse(TransferenciaBancaria transferencia);

    AplicacaoFinanceiraResponse toResponse(AplicacaoFinanceira aplicacao);

    ExtratoBancarioImportacaoResponse toResponse(ExtratoBancarioImportacao importacao);

    ExtratoBancarioItemResponse toResponse(ExtratoBancarioItem item);
}
