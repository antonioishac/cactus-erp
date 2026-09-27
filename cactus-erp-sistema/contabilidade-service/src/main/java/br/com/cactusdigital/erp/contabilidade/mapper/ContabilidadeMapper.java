package br.com.cactusdigital.erp.contabilidade.mapper;

import br.com.cactusdigital.erp.contabilidade.dto.CentroCustoRequest;
import br.com.cactusdigital.erp.contabilidade.dto.CentroCustoResponse;
import br.com.cactusdigital.erp.contabilidade.dto.LancamentoContabilResponse;
import br.com.cactusdigital.erp.contabilidade.dto.PartidaContabilResponse;
import br.com.cactusdigital.erp.contabilidade.dto.PlanoContaRequest;
import br.com.cactusdigital.erp.contabilidade.dto.PlanoContaResponse;
import br.com.cactusdigital.erp.contabilidade.entity.CentroCusto;
import br.com.cactusdigital.erp.contabilidade.entity.LancamentoContabil;
import br.com.cactusdigital.erp.contabilidade.entity.PartidaContabil;
import br.com.cactusdigital.erp.contabilidade.entity.PlanoConta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ContabilidadeMapper {

    @Mapping(target = "contaPaiId", source = "contaPai.id")
    @Mapping(target = "contaPaiNome", source = "contaPai.nome")
    PlanoContaResponse toResponse(PlanoConta conta);

    /**
     * {@code contaPai} é resolvido/validado no service (precisa existir e ser sintética) —
     * nunca atribuído direto pelo mapper. {@code defaultValue="ATIVO"} em {@code situacao}:
     * BUG REAL encontrado na validação ao vivo — sem isso, o MapStruct copiava o {@code null}
     * do request (campo opcional) por cima do {@code @Builder.Default} da entidade (que só
     * se aplica via {@code .builder()}, não via o construtor sem argumentos que o MapStruct
     * usa aqui), estourando {@code DataIntegrityViolationException} (coluna
     * {@code NOT NULL}) ao criar uma conta sem informar {@code situacao} explicitamente —
     * mesma classe de bug já documentada em {@code ProdutoImagem.ordem}/
     * {@code FormaPagamento.tefTipoGp} no cadastro-base-service.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contaPai", ignore = true)
    @Mapping(target = "situacao", defaultValue = "ATIVO")
    PlanoConta toEntity(PlanoContaRequest request);

    /** {@code situacao} usa {@code IGNORE} (não {@code defaultValue}) num update: omitir o campo num PUT preserva a situação atual, nunca reativa silenciosamente uma conta inativa. */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contaPai", ignore = true)
    @Mapping(target = "empresaId", ignore = true)
    @Mapping(target = "situacao", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void atualizar(PlanoContaRequest request, @MappingTarget PlanoConta entity);

    CentroCustoResponse toResponse(CentroCusto centroCusto);

    /** {@code defaultValue="ATIVO"}: mesmo bug/fix de {@code toEntity(PlanoContaRequest)} acima. */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "situacao", defaultValue = "ATIVO")
    CentroCusto toEntity(CentroCustoRequest request);

    /** Mesmo critério de {@code atualizar(PlanoContaRequest, ...)} acima — {@code IGNORE}, não {@code defaultValue}. */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresaId", ignore = true)
    @Mapping(target = "situacao", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void atualizar(CentroCustoRequest request, @MappingTarget CentroCusto entity);

    @Mapping(target = "contaId", source = "conta.id")
    @Mapping(target = "contaCodigo", source = "conta.codigo")
    @Mapping(target = "contaNome", source = "conta.nome")
    @Mapping(target = "centroCustoId", source = "centroCusto.id")
    @Mapping(target = "centroCustoNome", source = "centroCusto.nome")
    PartidaContabilResponse toResponse(PartidaContabil partida);

    LancamentoContabilResponse toResponse(LancamentoContabil lancamento);
}
