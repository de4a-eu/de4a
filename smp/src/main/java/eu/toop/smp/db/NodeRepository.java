package eu.toop.smp.db;

import org.springframework.stereotype.Component;
@Component
public interface NodeRepository extends org.springframework.data.repository.CrudRepository<Node,String> {
 
}