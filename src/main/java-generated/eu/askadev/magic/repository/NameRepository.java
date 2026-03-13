package eu.askadev.magic.repository;

import eu.askadev.magic.model.Name;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NameRepository extends MongoRepository<Name, String> {
}
