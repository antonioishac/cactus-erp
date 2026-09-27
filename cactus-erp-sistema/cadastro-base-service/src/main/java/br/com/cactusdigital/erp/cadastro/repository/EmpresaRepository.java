package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Page<Empresa> findByRazaoSocialContainingIgnoreCaseOrNomeFantasiaContainingIgnoreCase(
            String razaoSocial, String nomeFantasia, Pageable pageable);
}
