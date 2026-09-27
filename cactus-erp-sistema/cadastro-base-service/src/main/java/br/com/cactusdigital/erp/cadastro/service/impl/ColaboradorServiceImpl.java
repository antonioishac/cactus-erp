package br.com.cactusdigital.erp.cadastro.service.impl;

import br.com.cactusdigital.erp.cadastro.dto.ColaboradorRequest;
import br.com.cactusdigital.erp.cadastro.dto.ColaboradorResponse;
import br.com.cactusdigital.erp.cadastro.entity.Cargo;
import br.com.cactusdigital.erp.cadastro.entity.Colaborador;
import br.com.cactusdigital.erp.cadastro.entity.Pessoa;
import br.com.cactusdigital.erp.cadastro.entity.Setor;
import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import br.com.cactusdigital.erp.cadastro.exception.CadastroErrorCode;
import br.com.cactusdigital.erp.cadastro.mapper.ColaboradorMapper;
import br.com.cactusdigital.erp.cadastro.repository.CargoRepository;
import br.com.cactusdigital.erp.cadastro.repository.ColaboradorRepository;
import br.com.cactusdigital.erp.cadastro.repository.SetorRepository;
import br.com.cactusdigital.erp.cadastro.service.ColaboradorService;
import br.com.cactusdigital.erp.cadastro.service.PessoaAssembler;
import br.com.cactusdigital.erp.cadastro.specification.ColaboradorSpecifications;
import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
class ColaboradorServiceImpl implements ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;
    private final CargoRepository cargoRepository;
    private final SetorRepository setorRepository;
    private final ColaboradorMapper colaboradorMapper;
    private final PessoaAssembler pessoaAssembler;

    @Override
    @Transactional(readOnly = true)
    public Page<ColaboradorResponse> listar(String nome, String documento, Long cargoId, Long setorId, Situacao situacao, Pageable pageable) {
        var specification = ColaboradorSpecifications.comFiltros(nome, documento, cargoId, setorId, situacao);
        Page<Colaborador> pagina = colaboradorRepository.findAll(specification, pageable);
        hidratarColecoesDePessoa(pagina);
        return pagina.map(colaboradorMapper::toResponse);
    }

    /** Evita N+1 nas 3 coleções de {@code Pessoa} (enderecos/contatos/telefones) que
     * {@link ColaboradorResponse} expõe — ver Javadoc de
     * {@code ClienteRepository#hidratarEnderecos}. */
    private void hidratarColecoesDePessoa(Page<Colaborador> pagina) {
        if (pagina.isEmpty()) {
            return;
        }
        List<Long> ids = pagina.getContent().stream().map(Colaborador::getId).toList();
        colaboradorRepository.hidratarEnderecos(ids);
        colaboradorRepository.hidratarContatos(ids);
        colaboradorRepository.hidratarTelefones(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public ColaboradorResponse buscarPorId(Long id) {
        return colaboradorMapper.toResponse(buscarEntidade(id));
    }

    @Override
    @Transactional
    public ColaboradorResponse criar(ColaboradorRequest request) {
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

        Colaborador colaborador = Colaborador.builder()
                .pessoa(pessoa)
                .cargo(request.cargoId() != null ? buscarCargo(request.cargoId()) : null)
                .setor(request.setorId() != null ? buscarSetor(request.setorId()) : null)
                .matricula(request.matricula())
                .dataAdmissao(request.dataAdmissao())
                .dataDemissao(request.dataDemissao())
                .ctpsNumero(request.ctpsNumero())
                .ctpsSerie(request.ctpsSerie())
                .ctpsDataExpedicao(request.ctpsDataExpedicao())
                .ctpsUf(request.ctpsUf())
                .comissaoVista(request.comissaoVista())
                .comissaoPrazo(request.comissaoPrazo())
                .entregadorVeiculo(request.entregadorVeiculo())
                .observacao(request.observacao())
                .situacao(request.situacao())
                .build();

        return colaboradorMapper.toResponse(colaboradorRepository.save(colaborador));
    }

    @Override
    @Transactional
    public ColaboradorResponse atualizar(Long id, ColaboradorRequest request) {
        validarDocumento(request);
        Colaborador colaborador = buscarEntidade(id);
        Pessoa pessoa = colaborador.getPessoa();

        if (pessoa.getTipoPessoa() != request.tipoPessoa()) {
            throw new BusinessException(CadastroErrorCode.PESSOA_TIPO_IMUTAVEL,
                    "Não é possível alterar o tipo de pessoa (física/jurídica) de um colaborador já cadastrado");
        }

        pessoa.setNome(request.nome());
        pessoa.setEmail(request.email());
        pessoaAssembler.aplicarEspecializacao(pessoa, request.tipoPessoa(), request.cpf(), request.rg(), request.dataNascimento(),
                request.cnpj(), request.nomeFantasia(), request.inscricaoEstadual(), request.inscricaoMunicipal());
        pessoaAssembler.substituirListas(pessoa, request.enderecos(), request.contatos(), request.telefones());

        colaborador.setCargo(request.cargoId() != null ? buscarCargo(request.cargoId()) : null);
        colaborador.setSetor(request.setorId() != null ? buscarSetor(request.setorId()) : null);
        colaborador.setMatricula(request.matricula());
        colaborador.setDataAdmissao(request.dataAdmissao());
        colaborador.setDataDemissao(request.dataDemissao());
        colaborador.setCtpsNumero(request.ctpsNumero());
        colaborador.setCtpsSerie(request.ctpsSerie());
        colaborador.setCtpsDataExpedicao(request.ctpsDataExpedicao());
        colaborador.setCtpsUf(request.ctpsUf());
        colaborador.setComissaoVista(request.comissaoVista());
        colaborador.setComissaoPrazo(request.comissaoPrazo());
        colaborador.setEntregadorVeiculo(request.entregadorVeiculo());
        colaborador.setObservacao(request.observacao());
        colaborador.setSituacao(request.situacao());

        return colaboradorMapper.toResponse(colaborador);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        colaboradorRepository.delete(buscarEntidade(id));
    }

    private Colaborador buscarEntidade(Long id) {
        return colaboradorRepository.findById(id).orElseThrow(() -> NotFoundException.of("Colaborador", id));
    }

    private Cargo buscarCargo(Long id) {
        return cargoRepository.findById(id).orElseThrow(() -> NotFoundException.of("Cargo", id));
    }

    private Setor buscarSetor(Long id) {
        return setorRepository.findById(id).orElseThrow(() -> NotFoundException.of("Setor", id));
    }

    private void validarDocumento(ColaboradorRequest request) {
        if (request.tipoPessoa() == TipoPessoa.FISICA && !StringUtils.hasText(request.cpf())) {
            throw new BusinessException(CadastroErrorCode.PESSOA_CPF_OBRIGATORIO, "CPF é obrigatório para colaborador pessoa física");
        }
        if (request.tipoPessoa() == TipoPessoa.JURIDICA && !StringUtils.hasText(request.cnpj())) {
            throw new BusinessException(CadastroErrorCode.PESSOA_CNPJ_OBRIGATORIO, "CNPJ é obrigatório para colaborador pessoa jurídica");
        }
    }
}
