package eu.askadev.magic.repository;

import eu.askadev.magic.model.Variant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantRepository extends MongoRepository<Variant, String> {
}
