package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.PdvNfceXml;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdvNfceXmlRepository extends JpaRepository<PdvNfceXml, Long> {
}
