package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.repository.TipoPagamentoRepository;
import br.com.cactuserp.pdv.service.dto.TipoPagamentoDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoPagamentoService {

    private final TipoPagamentoRepository repository;

    public TipoPagamentoService(TipoPagamentoRepository repository) {
        this.repository = repository;
    }

    public List<TipoPagamentoDTO> listAllPayments() {
        return repository.findAll()
                .stream()
                .map(pgto -> {
                    TipoPagamentoDTO dto = new TipoPagamentoDTO();
                    dto.setId(pgto.getId());
                    dto.setCodigo(pgto.getCodigo());
                    dto.setCodigoPagamentoNfce(pgto.getCodigoPagamentoNfce());
                    dto.setDescricao(pgto.getDescricao());
                    dto.setTefTipoGp(pgto.getTefTipoGp());
                    dto.setTef(pgto.getTef());
                    dto.setGeraParcelas(pgto.getGeraParcelas());
                    dto.setImprimeVinculado(pgto.getImprimeVinculado());
                    dto.setPermiteTroco(pgto.getPermiteTroco());
                    dto.setIcone(pgto.getIcone());
                    return dto;
                })
                .toList();
    }

}
