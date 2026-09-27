package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.RemessaBancariaItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemessaBancariaItemRepository extends JpaRepository<RemessaBancariaItem, Long> {

    /** Usado por {@code EdiBancarioService#processarRetorno} pra resolver o {@code contaReceberId}
     * de um item de retorno pelo {@code nossoNumero}. */
    List<RemessaBancariaItem> findByNossoNumeroIn(List<String> nossosNumeros);
}
