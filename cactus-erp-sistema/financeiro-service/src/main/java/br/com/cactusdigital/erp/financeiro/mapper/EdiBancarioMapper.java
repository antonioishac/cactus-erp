package br.com.cactusdigital.erp.financeiro.mapper;

import br.com.cactusdigital.erp.financeiro.dto.RemessaBancariaItemResponse;
import br.com.cactusdigital.erp.financeiro.dto.RemessaBancariaResponse;
import br.com.cactusdigital.erp.financeiro.dto.RetornoBancarioItemResponse;
import br.com.cactusdigital.erp.financeiro.dto.RetornoBancarioResponse;
import br.com.cactusdigital.erp.financeiro.entity.RemessaBancaria;
import br.com.cactusdigital.erp.financeiro.entity.RemessaBancariaItem;
import br.com.cactusdigital.erp.financeiro.entity.RetornoBancario;
import br.com.cactusdigital.erp.financeiro.entity.RetornoBancarioItem;
import org.mapstruct.Mapper;

/** Mapper de EDI Bancário (Remessa/Retorno CNAB240) — separado de {@link CaixaBancoMapper}
 * por SRP, mesmo critério já usado no módulo (um mapper por agrupamento coeso). */
@Mapper(componentModel = "spring")
public interface EdiBancarioMapper {

    RemessaBancariaItemResponse toResponse(RemessaBancariaItem item);

    RemessaBancariaResponse toResponse(RemessaBancaria remessa);

    RetornoBancarioItemResponse toResponse(RetornoBancarioItem item);

    RetornoBancarioResponse toResponse(RetornoBancario retorno);
}
