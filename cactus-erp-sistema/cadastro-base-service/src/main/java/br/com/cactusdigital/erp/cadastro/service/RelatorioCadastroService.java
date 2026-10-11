package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.ClienteResponse;
import br.com.cactusdigital.erp.cadastro.dto.FornecedorResponse;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoResponse;
import br.com.cactusdigital.erp.cadastro.entity.Cliente;
import br.com.cactusdigital.erp.cadastro.entity.Fornecedor;
import br.com.cactusdigital.erp.cadastro.entity.Produto;
import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import br.com.cactusdigital.erp.cadastro.mapper.ClienteMapper;
import br.com.cactusdigital.erp.cadastro.mapper.FornecedorMapper;
import br.com.cactusdigital.erp.cadastro.mapper.ProdutoMapper;
import br.com.cactusdigital.erp.cadastro.repository.ClienteRepository;
import br.com.cactusdigital.erp.cadastro.repository.FornecedorRepository;
import br.com.cactusdigital.erp.cadastro.repository.ProdutoRepository;
import br.com.cactusdigital.erp.cadastro.specification.ClienteSpecifications;
import br.com.cactusdigital.erp.cadastro.specification.FornecedorSpecifications;
import br.com.cactusdigital.erp.cadastro.specification.ProdutoSpecifications;
import br.com.cactusdigital.erp.common.domain.Situacao;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listagem cadastral de clientes/fornecedores/produtos — o relatório mais simples do
 * lote da Fase 17 (ver ANÁLISE: MÓDULO DE RELATÓRIOS no CLAUDE.md raiz): reaproveita os
 * mesmos {@code *Specifications}/mapper já usados pelo {@code GET} paginado de cada
 * recurso, só sem {@code Pageable} ({@link org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(Specification)}
 * já devolve {@code List} de graça).
 * <p>
 * <b>Achado real, corrigido antes de validar com volume (não só com 1 linha)</b>: a
 * primeira versão não chamava as hidratações, no pressuposto de que pular a
 * hidratação evitaria o N+1 (já que o {@code .jrxml} não referencia
 * {@code enderecos}/{@code contatos}/{@code telefones}/{@code faixasPreco}/
 * {@code imagens}). Raciocínio errado: {@code clienteMapper.toResponse(entity)} é um
 * método MapStruct gerado que sempre monta TODOS os campos do record de destino,
 * inclusive essas coleções — não hidratar só adia o N+1 pra dentro do próprio mapper
 * (1 SELECT por linha, por coleção), não o evita. As mesmas hidratações já usadas pelo
 * {@code GET} paginado de cada recurso (`ClienteServiceImpl`/`FornecedorServiceImpl`/
 * `ProdutoServiceImpl`) são reaproveitadas aqui, na lista inteira em vez de uma página.
 */
@Service
@RequiredArgsConstructor
public class RelatorioCadastroService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final FornecedorRepository fornecedorRepository;
    private final FornecedorMapper fornecedorMapper;
    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;

    @Transactional(readOnly = true)
    public List<ClienteResponse> clientes(String nome, String documento, TipoPessoa tipoPessoa, Situacao situacao) {
        Specification<Cliente> especificacao = ClienteSpecifications.comFiltros(nome, documento, tipoPessoa, situacao);
        List<Cliente> itens = clienteRepository.findAll(especificacao);
        if (!itens.isEmpty()) {
            List<Long> ids = itens.stream().map(Cliente::getId).toList();
            clienteRepository.hidratarEnderecos(ids);
            clienteRepository.hidratarContatos(ids);
            clienteRepository.hidratarTelefones(ids);
        }
        return itens.stream().map(clienteMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponse> fornecedores(String nome, String documento, TipoPessoa tipoPessoa, Situacao situacao) {
        Specification<Fornecedor> especificacao = FornecedorSpecifications.comFiltros(nome, documento, tipoPessoa, situacao);
        List<Fornecedor> itens = fornecedorRepository.findAll(especificacao);
        if (!itens.isEmpty()) {
            List<Long> ids = itens.stream().map(Fornecedor::getId).toList();
            fornecedorRepository.hidratarEnderecos(ids);
            fornecedorRepository.hidratarContatos(ids);
            fornecedorRepository.hidratarTelefones(ids);
        }
        return itens.stream().map(fornecedorMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> produtos(String nome, String codigoInterno, String gtin, Long produtoSubgrupoId, Long produtoMarcaId, Situacao situacao) {
        Specification<Produto> especificacao =
                ProdutoSpecifications.comFiltros(nome, codigoInterno, gtin, produtoSubgrupoId, produtoMarcaId, situacao, null);
        List<Produto> itens = produtoRepository.findAll(especificacao);
        if (!itens.isEmpty()) {
            List<Long> ids = itens.stream().map(Produto::getId).toList();
            produtoRepository.hidratarFaixasPreco(ids);
            produtoRepository.hidratarImagens(ids);
        }
        return itens.stream().map(produtoMapper::toResponse).toList();
    }
}
