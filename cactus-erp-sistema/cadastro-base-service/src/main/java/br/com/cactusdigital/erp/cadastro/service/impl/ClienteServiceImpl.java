package br.com.cactusdigital.erp.cadastro.service.impl;

import br.com.cactusdigital.erp.cadastro.dto.ClienteRequest;
import br.com.cactusdigital.erp.cadastro.dto.ClienteResponse;
import br.com.cactusdigital.erp.cadastro.entity.Cliente;
import br.com.cactusdigital.erp.cadastro.entity.Pessoa;
import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import br.com.cactusdigital.erp.cadastro.exception.CadastroErrorCode;
import br.com.cactusdigital.erp.cadastro.mapper.ClienteMapper;
import br.com.cactusdigital.erp.cadastro.repository.ClienteRepository;
import br.com.cactusdigital.erp.cadastro.service.ClienteService;
import br.com.cactusdigital.erp.cadastro.service.PessoaAssembler;
import br.com.cactusdigital.erp.cadastro.specification.ClienteSpecifications;
import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final PessoaAssembler pessoaAssembler;

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponse> listar(String nome, String documento, TipoPessoa tipoPessoa, Situacao situacao, Pageable pageable) {
        return listar(nome, documento, tipoPessoa, situacao, null, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponse> listar(
            String nome, String documento, TipoPessoa tipoPessoa, Situacao situacao, Instant desde, Pageable pageable) {
        var specification = ClienteSpecifications.comFiltros(nome, documento, tipoPessoa, situacao, desde);
        Page<Cliente> pagina = clienteRepository.findAll(specification, pageable);
        hidratarColecoesDePessoa(pagina);
        return pagina.map(clienteMapper::toResponse);
    }

    /**
     * Evita N+1 nas 3 coleções de {@code Pessoa} (enderecos/contatos/telefones) que
     * {@link ClienteResponse} expõe — ver Javadoc de
     * {@code ClienteRepository#hidratarEnderecos}.
     */
    private void hidratarColecoesDePessoa(Page<Cliente> pagina) {
        if (pagina.isEmpty()) {
            return;
        }
        List<Long> ids = pagina.getContent().stream().map(Cliente::getId).toList();
        clienteRepository.hidratarEnderecos(ids);
        clienteRepository.hidratarContatos(ids);
        clienteRepository.hidratarTelefones(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        return clienteMapper.toResponse(buscarEntidade(id));
    }

    @Override
    @Transactional
    public ClienteResponse criar(ClienteRequest request) {
        validarDocumento(request);

        Pessoa pessoa = Pessoa.builder()
                .nome(request.nome())
                .tipoPessoa(request.tipoPessoa())
                .email(request.email())
                .situacao(Situacao.ATIVO)
                .build();
        pessoaAssembler.aplicarEspecializacao(pessoa, request.tipoPessoa(), request.cpf(), request.rg(), request.dataNascimento(),
                request.cnpj(), request.nomeFantasia(), request.inscricaoEstadual(), request.inscricaoMunicipal());
        pessoaAssembler.substituirListas(pessoa, request.enderecos(), request.contatos(), request.telefones());

        Cliente cliente = Cliente.builder()
                .pessoa(pessoa)
                .url(request.url())
                .limiteCredito(request.limiteCredito())
                .taxaDesconto(request.taxaDesconto())
                .fidelidadeAviso(request.fidelidadeAviso())
                .fidelidadeQuantidade(request.fidelidadeQuantidade())
                .fidelidadeValor(request.fidelidadeValor())
                .dataCadastro(LocalDate.now())
                .situacao(request.situacao())
                .build();

        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest request) {
        validarDocumento(request);
        Cliente cliente = buscarEntidade(id);
        Pessoa pessoa = cliente.getPessoa();

        if (pessoa.getTipoPessoa() != request.tipoPessoa()) {
            throw new BusinessException(CadastroErrorCode.PESSOA_TIPO_IMUTAVEL,
                    "Não é possível alterar o tipo de pessoa (física/jurídica) de um cliente já cadastrado");
        }

        pessoa.setNome(request.nome());
        pessoa.setEmail(request.email());
        pessoaAssembler.aplicarEspecializacao(pessoa, request.tipoPessoa(), request.cpf(), request.rg(), request.dataNascimento(),
                request.cnpj(), request.nomeFantasia(), request.inscricaoEstadual(), request.inscricaoMunicipal());
        pessoaAssembler.substituirListas(pessoa, request.enderecos(), request.contatos(), request.telefones());

        cliente.setUrl(request.url());
        cliente.setLimiteCredito(request.limiteCredito());
        cliente.setTaxaDesconto(request.taxaDesconto());
        cliente.setFidelidadeAviso(request.fidelidadeAviso());
        cliente.setFidelidadeQuantidade(request.fidelidadeQuantidade());
        cliente.setFidelidadeValor(request.fidelidadeValor());
        cliente.setSituacao(request.situacao());

        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        clienteRepository.delete(buscarEntidade(id));
    }

    private Cliente buscarEntidade(Long id) {
        return clienteRepository.findById(id).orElseThrow(() -> NotFoundException.of("Cliente", id));
    }

    private void validarDocumento(ClienteRequest request) {
        if (request.tipoPessoa() == TipoPessoa.FISICA && !StringUtils.hasText(request.cpf())) {
            throw new BusinessException(CadastroErrorCode.PESSOA_CPF_OBRIGATORIO, "CPF é obrigatório para cliente pessoa física");
        }
        if (request.tipoPessoa() == TipoPessoa.JURIDICA && !StringUtils.hasText(request.cnpj())) {
            throw new BusinessException(CadastroErrorCode.PESSOA_CNPJ_OBRIGATORIO, "CNPJ é obrigatório para cliente pessoa jurídica");
        }
    }
}
