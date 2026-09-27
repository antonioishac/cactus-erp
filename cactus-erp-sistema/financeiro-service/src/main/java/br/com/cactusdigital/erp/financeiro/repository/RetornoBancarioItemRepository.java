package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.RetornoBancarioItem;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositório próprio (não só a coleção {@code RetornoBancario.itens}) — o pai já está
 * persistido quando os itens são criados ({@code EdiBancarioService#importarRetorno}),
 * então cada item precisa ser salvo explicitamente aqui: {@code GenerationType.IDENTITY}
 * só atribui o id no INSERT de verdade, não no {@code add()} a uma coleção de um pai já
 * gerenciado (mesmo bug já documentado e corrigido em várias fases anteriores do sistema). */
public interface RetornoBancarioItemRepository extends JpaRepository<RetornoBancarioItem, Long> {
}
