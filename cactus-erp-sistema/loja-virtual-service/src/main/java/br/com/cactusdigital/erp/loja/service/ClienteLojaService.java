package br.com.cactusdigital.erp.loja.service;

import br.com.cactusdigital.erp.loja.dto.FinalizarCompraRequest;
import br.com.cactusdigital.erp.loja.entity.ClienteLoja;
import br.com.cactusdigital.erp.loja.repository.ClienteLojaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cliente final da loja — sem tela/CRUD próprio nesta fatia, só resolvido internamente
 * no checkout (guest checkout, ver Javadoc de {@link ClienteLoja}). Um mesmo e-mail
 * dentro da mesma loja reaproveita o registro existente (atualiza nome/telefone com o
 * que veio no request mais recente) em vez de duplicar cliente a cada compra.
 */
@Service
@RequiredArgsConstructor
class ClienteLojaService {

    private final ClienteLojaRepository repository;

    @Transactional
    ClienteLoja buscarOuCriar(Long lojaId, FinalizarCompraRequest request) {
        ClienteLoja cliente = repository.findByLojaIdAndEmail(lojaId, request.email())
                .orElseGet(() -> ClienteLoja.builder().lojaId(lojaId).email(request.email()).build());
        cliente.setNome(request.nome());
        cliente.setTelefone(request.telefone());
        cliente.setCpf(request.cpf());
        return repository.save(cliente);
    }
}
