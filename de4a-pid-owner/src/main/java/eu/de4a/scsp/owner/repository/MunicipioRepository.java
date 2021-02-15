package eu.de4a.scsp.owner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import eu.de4a.scsp.owner.model.Municipio;
import eu.de4a.scsp.owner.model.MunicipioPK;
@Component
public interface MunicipioRepository extends JpaRepository<Municipio,MunicipioPK> {
 
}