package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.financeiro.dto.ContaBancariaRequest;
import br.com.cactusdigital.erp.financeiro.dto.ContaBancariaResponse;
import br.com.cactusdigital.erp.financeiro.entity.ContaBancaria;
import br.com.cactusdigital.erp.financeiro.entity.TipoContaBancaria;
import br.com.cactusdigital.erp.financeiro.exception.FinanceiroErrorCode;
import br.com.cactusdigital.erp.financeiro.mapper.CaixaBancoMapper;
import br.com.cactusdigital.erp.financeiro.repository.ContaBancariaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContaBancariaService {

    private final ContaBancariaRepository repository;
    private final CaixaBancoMapper mapper;

    @Transactional(readOnly = true)
    public Page<ContaBancariaResponse> listar(Long empresaId, Pageable pageable) {
        Page<ContaBancaria> pagina = empresaId != null ? repository.findByEmpresaId(empresaId, pageable) : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ContaBancariaResponse> listarAtivasDaEmpresa(Long empresaId) {
        return repository.findByEmpresaIdAndAtivoTrue(empresaId).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ContaBancariaResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ContaBancariaResponse criar(ContaBancariaRequest request) {
        if (request.tipo() != TipoContaBancaria.CAIXA_INTERNO && request.bancoId() == null) {
            throw new BusinessException(FinanceiroErrorCode.BANCO_OBRIGATORIO_PARA_TIPO_DE_CONTA);
        }
        ContaBancaria conta = ContaBancaria.builder()
                .empresaId(request.empresaId())
                .bancoId(request.bancoId())
                .tipo(request.tipo())
                .nome(request.nome())
                .agencia(request.agencia())
                .agenciaDigito(request.agenciaDigito())
                .numero(request.numero())
                .numeroDigito(request.numeroDigito())
                .cedenteNome(request.cedenteNome())
                .cedenteDocumento(request.cedenteDocumento())
                .codigoConvenioCobranca(request.codigoConvenioCobranca())
                .codigoCedenteCobranca(request.codigoCedenteCobranca())
                .carteiraCobranca(request.carteiraCobranca())
                .dataSaldoInicial(request.dataSaldoInicial())
                .saldoInicial(request.saldoInicial())
                .saldoAtual(request.saldoInicial())
                .build();
        return mapper.toResponse(repository.save(conta));
    }

    @Transactional
    public ContaBancariaResponse atualizar(Long id, ContaBancariaRequest request) {
        if (request.tipo() != TipoContaBancaria.CAIXA_INTERNO && request.bancoId() == null) {
            throw new BusinessException(FinanceiroErrorCode.BANCO_OBRIGATORIO_PARA_TIPO_DE_CONTA);
        }
        ContaBancaria conta = buscarEntidade(id);
        conta.setBancoId(request.bancoId());
        conta.setTipo(request.tipo());
        conta.setNome(request.nome());
        conta.setAgencia(request.agencia());
        conta.setAgenciaDigito(request.agenciaDigito());
        conta.setNumero(request.numero());
        conta.setNumeroDigito(request.numeroDigito());
        conta.setCedenteNome(request.cedenteNome());
        conta.setCedenteDocumento(request.cedenteDocumento());
        conta.setCodigoConvenioCobranca(request.codigoConvenioCobranca());
        conta.setCodigoCedenteCobranca(request.codigoCedenteCobranca());
        conta.setCarteiraCobranca(request.carteiraCobranca());
        // saldoInicial/saldoAtual não são reescritos aqui de propósito — mexer em saldo
        // só passa por MovimentoCaixaBancoService, nunca pelo PUT de cadastro da conta.
        return mapper.toResponse(conta);
    }

    /** Incrementa e devolve o próximo "nosso número" de cobrança da conta — mesmo padrão
     * de {@code EmpresaConfiguracaoFiscal.reservarProximoNumeroNfe} (cadastro-base-service):
     * reserva atômica, começa em 1 quando a conta nunca gerou remessa antes. */
    @Transactional
    public long reservarProximoNossoNumero(Long contaBancariaId) {
        ContaBancaria conta = buscarEntidade(contaBancariaId);
        long numero = conta.getProximoNossoNumero() != null ? conta.getProximoNossoNumero() : 1L;
        conta.setProximoNossoNumero(numero + 1);
        return numero;
    }

    @Transactional
    public ContaBancariaResponse inativar(Long id) {
        ContaBancaria conta = buscarEntidade(id);
        conta.setAtivo(false);
        return mapper.toResponse(conta);
    }

    @Transactional
    public ContaBancariaResponse reativar(Long id) {
        ContaBancaria conta = buscarEntidade(id);
        conta.setAtivo(true);
        return mapper.toResponse(conta);
    }

    ContaBancaria buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Conta bancária", id));
    }
}
