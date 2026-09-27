package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.Cliente;
import br.com.cactuserp.pdv.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Value("${pdv.cliente.busca.limite:50}")
    private int limitePadrao;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Busca clientes por nome ou nome fantasia.
     * Retorna lista vazia se o termo for vazio (evita carregar a base inteira).
     */
    public List<Cliente> buscarPorNome(String termo) {
        if (termo == null || termo.isBlank()) {
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(0, limitePadrao);
        Page<Cliente> page = clienteRepository.buscarPorNomeOuFantasia(termo.trim(), pageable);
        return page.getContent();
    }

    /**
     * Busca clientes por CPF/CNPJ. Normaliza removendo qualquer máscara.
     */
    public List<Cliente> buscarPorCpfCnpj(String cpfCnpj) {
        if (cpfCnpj == null || cpfCnpj.isBlank()) {
            return Collections.emptyList();
        }
        String somenteNumeros = cpfCnpj.replaceAll("\\D", "");
        if (somenteNumeros.isEmpty()) {
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(0, limitePadrao);
        Page<Cliente> page = clienteRepository.buscarPorCpfCnpj(somenteNumeros, pageable);
        return page.getContent();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> buscarExatoPorCpfCnpj(String cpfCnpj) {
        if (cpfCnpj == null) return Optional.empty();
        return clienteRepository.findByCpfCnpj(cpfCnpj.replaceAll("\\D", ""));
    }
}