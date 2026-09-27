package br.com.cactusdigital.erp.cadastro.service.impl;

import br.com.cactusdigital.erp.cadastro.dto.FornecedorRequest;
import br.com.cactusdigital.erp.cadastro.dto.FornecedorResponse;
import br.com.cactusdigital.erp.cadastro.entity.Fornecedor;
import br.com.cactusdigital.erp.cadastro.entity.Pessoa;
import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import br.com.cactusdigital.erp.cadastro.exception.CadastroErrorCode;
import br.com.cactusdigital.erp.cadastro.mapper.FornecedorMapper;
import br.com.cactusdigital.erp.cadastro.repository.FornecedorRepository;
import br.com.cactusdigital.erp.cadastro.service.FornecedorService;
import br.com.cactusdigital.erp.cadastro.service.PessoaAssembler;
import br.com.cactusdigital.erp.cadastro.specification.FornecedorSpecifications;
import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
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
class FornecedorServiceImpl implements FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final FornecedorMapper fornecedorMapper;
    private final PessoaAssembler pessoaAssembler;

    @Override
    @Transactional(readOnly = true)
    public Page<FornecedorResponse> listar(String nome, String documento, TipoPessoa tipoPessoa, Situacao situacao, Pageable pageable) {
        var specification = FornecedorSpecifications.comFiltros(nome, documento, tipoPessoa, situacao);
        Page<Fornecedor> pagina = fornecedorRepository.findAll(specification, pageable);
        hidratarColecoesDePessoa(pagina);
        return pagina.map(fornecedorMapper::toResponse);
    }

    /** Evita N+1 nas 3 coleções de {@code Pessoa} (enderecos/contatos/telefones) que
     * {@link FornecedorResponse} expõe — ver Javadoc de
     * {@code ClienteRepository#hidratarEnderecos}. */
    private void hidratarColecoesDePessoa(Page<Fornecedor> pagina) {
        if (pagina.isEmpty()) {
            return;
        }
        List<Long> ids = pagina.getContent().stream().map(Fornecedor::getId).toList();
        fornecedorRepository.hidratarEnderecos(ids);
        fornecedorRepository.hidratarContatos(ids);
        fornecedorRepository.hidratarTelefones(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public FornecedorResponse buscarPorId(Long id) {
        return fornecedorMapper.toResponse(buscarEntidade(id));
    }

    @Override
    @Transactional
    public FornecedorResponse criar(FornecedorRequest request) {
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

        Fornecedor fornecedor = Fornecedor.builder()
                .pessoa(pessoa)
                .fornecedorDesde(request.fornecedorDesde())
                .prazoEntregaDias(request.prazoEntregaDias())
                .observacao(request.observacao())
                .dataCadastro(LocalDate.now())
                .situacao(request.situacao())
                .build();

        return fornecedorMapper.toResponse(fornecedorRepository.save(fornecedor));
    }

    @Override
    @Transactional
    public FornecedorResponse atualizar(Long id, FornecedorRequest request) {
        validarDocumento(request);
        Fornecedor fornecedor = buscarEntidade(id);
        Pessoa pessoa = fornecedor.getPessoa();

        if (pessoa.getTipoPessoa() != request.tipoPessoa()) {
            throw new BusinessException(CadastroErrorCode.PESSOA_TIPO_IMUTAVEL,
                    "Não é possível alterar o tipo de pessoa (física/jurídica) de um fornecedor já cadastrado");
        }

        pessoa.setNome(request.nome());
        pessoa.setEmail(request.email());
        pessoaAssembler.aplicarEspecializacao(pessoa, request.tipoPessoa(), request.cpf(), request.rg(), request.dataNascimento(),
                request.cnpj(), request.nomeFantasia(), request.inscricaoEstadual(), request.inscricaoMunicipal());
        pessoaAssembler.substituirListas(pessoa, request.enderecos(), request.contatos(), request.telefones());

        fornecedor.setFornecedorDesde(request.fornecedorDesde());
        fornecedor.setPrazoEntregaDias(request.prazoEntregaDias());
        fornecedor.setObservacao(request.observacao());
        fornecedor.setSituacao(request.situacao());

        return fornecedorMapper.toResponse(fornecedor);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        fornecedorRepository.delete(buscarEntidade(id));
    }

    private Fornecedor buscarEntidade(Long id) {
        return fornecedorRepository.findById(id).orElseThrow(() -> NotFoundException.of("Fornecedor", id));
    }

    private void validarDocumento(FornecedorRequest request) {
        if (request.tipoPessoa() == TipoPessoa.FISICA && !StringUtils.hasText(request.cpf())) {
            throw new BusinessException(CadastroErrorCode.PESSOA_CPF_OBRIGATORIO, "CPF é obrigatório para fornecedor pessoa física");
        }
        if (request.tipoPessoa() == TipoPessoa.JURIDICA && !StringUtils.hasText(request.cnpj())) {
            throw new BusinessException(CadastroErrorCode.PESSOA_CNPJ_OBRIGATORIO, "CNPJ é obrigatório para fornecedor pessoa jurídica");
        }
    }
}
