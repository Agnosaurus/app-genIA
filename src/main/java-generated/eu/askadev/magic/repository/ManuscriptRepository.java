package eu.askadev.magic.repository;

import eu.askadev.magic.model.Manuscript;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManuscriptRepository extends MongoRepository<Manuscript, String> {
}
