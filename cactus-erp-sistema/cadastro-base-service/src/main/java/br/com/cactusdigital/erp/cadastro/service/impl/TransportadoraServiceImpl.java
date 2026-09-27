package br.com.cactusdigital.erp.cadastro.service.impl;

import br.com.cactusdigital.erp.cadastro.dto.TransportadoraRequest;
import br.com.cactusdigital.erp.cadastro.dto.TransportadoraResponse;
import br.com.cactusdigital.erp.cadastro.entity.Pessoa;
import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import br.com.cactusdigital.erp.cadastro.entity.Transportadora;
import br.com.cactusdigital.erp.cadastro.exception.CadastroErrorCode;
import br.com.cactusdigital.erp.cadastro.mapper.TransportadoraMapper;
import br.com.cactusdigital.erp.cadastro.repository.TransportadoraRepository;
import br.com.cactusdigital.erp.cadastro.service.PessoaAssembler;
import br.com.cactusdigital.erp.cadastro.service.TransportadoraService;
import br.com.cactusdigital.erp.cadastro.specification.TransportadoraSpecifications;
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
class TransportadoraServiceImpl implements TransportadoraService {

    private final TransportadoraRepository transportadoraRepository;
    private final TransportadoraMapper transportadoraMapper;
    private final PessoaAssembler pessoaAssembler;

    @Override
    @Transactional(readOnly = true)
    public Page<TransportadoraResponse> listar(String nome, String documento, TipoPessoa tipoPessoa, Situacao situacao, Pageable pageable) {
        var specification = TransportadoraSpecifications.comFiltros(nome, documento, tipoPessoa, situacao);
        Page<Transportadora> pagina = transportadoraRepository.findAll(specification, pageable);
        hidratarColecoesDePessoa(pagina);
        return pagina.map(transportadoraMapper::toResponse);
    }

    /** Evita N+1 nas 3 coleções de {@code Pessoa} (enderecos/contatos/telefones) que
     * {@link TransportadoraResponse} expõe — ver Javadoc de
     * {@code ClienteRepository#hidratarEnderecos}. */
    private void hidratarColecoesDePessoa(Page<Transportadora> pagina) {
        if (pagina.isEmpty()) {
            return;
        }
        List<Long> ids = pagina.getContent().stream().map(Transportadora::getId).toList();
        transportadoraRepository.hidratarEnderecos(ids);
        transportadoraRepository.hidratarContatos(ids);
        transportadoraRepository.hidratarTelefones(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public TransportadoraResponse buscarPorId(Long id) {
        return transportadoraMapper.toResponse(buscarEntidade(id));
    }

    @Override
    @Transactional
    public TransportadoraResponse criar(TransportadoraRequest request) {
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

        Transportadora transportadora = Transportadora.builder()
                .pessoa(pessoa)
                .rntrc(request.rntrc())
                .observacao(request.observacao())
                .dataCadastro(LocalDate.now())
                .situacao(request.situacao())
                .build();

        return transportadoraMapper.toResponse(transportadoraRepository.save(transportadora));
    }

    @Override
    @Transactional
    public TransportadoraResponse atualizar(Long id, TransportadoraRequest request) {
        validarDocumento(request);
        Transportadora transportadora = buscarEntidade(id);
        Pessoa pessoa = transportadora.getPessoa();

        if (pessoa.getTipoPessoa() != request.tipoPessoa()) {
            throw new BusinessException(CadastroErrorCode.PESSOA_TIPO_IMUTAVEL,
                    "Não é possível alterar o tipo de pessoa (física/jurídica) de uma transportadora já cadastrada");
        }

        pessoa.setNome(request.nome());
        pessoa.setEmail(request.email());
        pessoaAssembler.aplicarEspecializacao(pessoa, request.tipoPessoa(), request.cpf(), request.rg(), request.dataNascimento(),
                request.cnpj(), request.nomeFantasia(), request.inscricaoEstadual(), request.inscricaoMunicipal());
        pessoaAssembler.substituirListas(pessoa, request.enderecos(), request.contatos(), request.telefones());

        transportadora.setRntrc(request.rntrc());
        transportadora.setObservacao(request.observacao());
        transportadora.setSituacao(request.situacao());

        return transportadoraMapper.toResponse(transportadora);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        transportadoraRepository.delete(buscarEntidade(id));
    }

    private Transportadora buscarEntidade(Long id) {
        return transportadoraRepository.findById(id).orElseThrow(() -> NotFoundException.of("Transportadora", id));
    }

    private void validarDocumento(TransportadoraRequest request) {
        if (request.tipoPessoa() == TipoPessoa.FISICA && !StringUtils.hasText(request.cpf())) {
            throw new BusinessException(CadastroErrorCode.PESSOA_CPF_OBRIGATORIO, "CPF é obrigatório para transportadora pessoa física");
        }
        if (request.tipoPessoa() == TipoPessoa.JURIDICA && !StringUtils.hasText(request.cnpj())) {
            throw new BusinessException(CadastroErrorCode.PESSOA_CNPJ_OBRIGATORIO, "CNPJ é obrigatório para transportadora pessoa jurídica");
        }
    }
}
