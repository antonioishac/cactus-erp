package br.com.cactusdigital.erp.bi.repository;

import br.com.cactusdigital.erp.bi.entity.FornecedorDim;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FornecedorDimRepository extends JpaRepository<FornecedorDim, Long> {

    List<FornecedorDim> findByFornecedorIdIn(List<Long> fornecedorIds);
}
