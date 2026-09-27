package br.com.cactusdigital.erp.loja.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.loja.entity.PlanoAssinatura;
import br.com.cactusdigital.erp.loja.entity.SituacaoAssinatura;
import java.time.LocalDate;

public record LojaResponse(
        Long id,
        String nome,
        String razaoSocial,
        String cnpj,
        Long empresaId,
        String subdominio,
        String emailContato,
        String telefone,
        PlanoAssinatura planoAssinatura,
        SituacaoAssinatura situacaoAssinatura,
        LocalDate dataInicioAssinatura,
        Situacao situacao) {
}
