package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.EmpresaRegimeTributario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRegimeTributarioRepository extends JpaRepository<EmpresaRegimeTributario, Long> {

    List<EmpresaRegimeTributario> findByEmpresaIdOrderByAnoCalendarioDesc(Long empresaId);

    boolean existsByEmpresaIdAndAnoCalendario(Long empresaId, Integer anoCalendario);
}
