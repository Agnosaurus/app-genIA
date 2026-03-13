package eu.askadev.magic.repository;

import eu.askadev.magic.model.Concept;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConceptRepository extends MongoRepository<Concept, String> {
}
