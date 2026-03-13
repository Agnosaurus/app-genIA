package eu.askadev.magic.repository;

import eu.askadev.magic.model.Keyword;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordRepository extends MongoRepository<Keyword, String> {
}
