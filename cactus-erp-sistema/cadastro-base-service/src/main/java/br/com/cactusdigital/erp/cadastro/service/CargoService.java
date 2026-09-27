package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.CargoRequest;
import br.com.cactusdigital.erp.cadastro.dto.CargoResponse;
import br.com.cactusdigital.erp.cadastro.entity.Cargo;
import br.com.cactusdigital.erp.cadastro.mapper.CargoSetorMapper;
import br.com.cactusdigital.erp.cadastro.repository.CargoRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CargoService {

    private final CargoRepository repository;
    private final CargoSetorMapper mapper;

    @Transactional(readOnly = true)
    public Page<CargoResponse> listar(String nome, Pageable pageable) {
        Page<Cargo> pagina = StringUtils.hasText(nome)
                ? repository.findByNomeContainingIgnoreCase(nome, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CargoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public CargoResponse criar(CargoRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public CargoResponse atualizar(Long id, CargoRequest request) {
        Cargo entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Cargo buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Cargo", id));
    }
}
