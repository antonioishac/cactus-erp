package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.EmpresaRegimeTributarioRequest;
import br.com.cactusdigital.erp.cadastro.dto.EmpresaRegimeTributarioResponse;
import br.com.cactusdigital.erp.cadastro.entity.Empresa;
import br.com.cactusdigital.erp.cadastro.entity.EmpresaRegimeTributario;
import br.com.cactusdigital.erp.cadastro.mapper.EmpresaMapper;
import br.com.cactusdigital.erp.cadastro.repository.EmpresaRegimeTributarioRepository;
import br.com.cactusdigital.erp.cadastro.exception.CadastroErrorCode;
import br.com.cactusdigital.erp.cadastro.repository.EmpresaRepository;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmpresaRegimeTributarioService {

    private final EmpresaRegimeTributarioRepository repository;
    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper mapper;

    @Transactional(readOnly = true)
    public List<EmpresaRegimeTributarioResponse> listar(Long empresaId) {
        return repository.findByEmpresaIdOrderByAnoCalendarioDesc(empresaId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public EmpresaRegimeTributarioResponse registrar(Long empresaId, EmpresaRegimeTributarioRequest request) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> NotFoundException.of("Empresa", empresaId));

        if (repository.existsByEmpresaIdAndAnoCalendario(empresaId, request.anoCalendario())) {
            throw new ConflictException(CadastroErrorCode.REGIME_TRIBUTARIO_ANO_DUPLICADO,
                    "Já existe regime tributário registrado para o ano " + request.anoCalendario());
        }

        EmpresaRegimeTributario entidade = EmpresaRegimeTributario.builder()
                .empresa(empresa)
                .anoCalendario(request.anoCalendario())
                .crt(request.crt())
                .build();
        return mapper.toResponse(repository.save(entidade));
    }
}
