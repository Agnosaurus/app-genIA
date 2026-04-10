package eu.askadev.magic.repository;

import eu.askadev.magic.model.SubKeyword;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubKeywordRepository extends MongoRepository<SubKeyword, String> {
}
