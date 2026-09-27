package br.com.cactusdigital.erp.ged.repository;

import br.com.cactusdigital.erp.ged.entity.DocumentoVersao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoVersaoRepository extends JpaRepository<DocumentoVersao, Long> {

    List<DocumentoVersao> findByDocumentoIdOrderByNumeroVersaoDesc(Long documentoId);

    Optional<DocumentoVersao> findFirstByDocumentoIdOrderByNumeroVersaoDesc(Long documentoId);
}
