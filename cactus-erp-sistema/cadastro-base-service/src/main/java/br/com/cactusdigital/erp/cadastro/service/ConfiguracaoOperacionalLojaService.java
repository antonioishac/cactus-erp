package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.ConfiguracaoOperacionalLojaRequest;
import br.com.cactusdigital.erp.cadastro.dto.ConfiguracaoOperacionalLojaResponse;
import br.com.cactusdigital.erp.cadastro.entity.ConfiguracaoOperacionalLoja;
import br.com.cactusdigital.erp.cadastro.entity.Empresa;
import br.com.cactusdigital.erp.cadastro.mapper.EmpresaMapper;
import br.com.cactusdigital.erp.cadastro.repository.ConfiguracaoOperacionalLojaRepository;
import br.com.cactusdigital.erp.cadastro.repository.EmpresaRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Config 1:1 por empresa — cria na primeira chamada de {@link #salvar}, atualiza depois (upsert). */
@Service
@RequiredArgsConstructor
public class ConfiguracaoOperacionalLojaService {

    private final ConfiguracaoOperacionalLojaRepository repository;
    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper mapper;

    @Transactional(readOnly = true)
    public ConfiguracaoOperacionalLojaResponse buscarPorEmpresa(Long empresaId) {
        return repository.findByEmpresaId(empresaId)
                .map(mapper::toResponse)
                .orElseThrow(() -> NotFoundException.of("Configuração operacional da empresa", empresaId));
    }

    @Transactional
    public ConfiguracaoOperacionalLojaResponse salvar(Long empresaId, ConfiguracaoOperacionalLojaRequest request) {
        ConfiguracaoOperacionalLoja entidade = repository.findByEmpresaId(empresaId).orElseGet(() -> {
            Empresa empresa = empresaRepository.findById(empresaId)
                    .orElseThrow(() -> NotFoundException.of("Empresa", empresaId));
            return ConfiguracaoOperacionalLoja.builder().empresa(empresa).build();
        });

        mapper.atualizar(request, entidade);
        return mapper.toResponse(repository.save(entidade));
    }
}
