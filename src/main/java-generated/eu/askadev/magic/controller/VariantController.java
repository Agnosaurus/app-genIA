package eu.askadev.magic.controller;

import eu.askadev.magic.model.Variant;
import eu.askadev.magic.repository.VariantRepository;
import eu.askadev.magic.service.PersistenceService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class VariantController {

    private final VariantRepository repository;
    private final PersistenceService persistenceService;

    public VariantController(VariantRepository repository, PersistenceService persistenceService) {
        this.repository = repository;
        this.persistenceService = persistenceService;
    }

    public Variant create(Variant variant) {
        Variant saved = repository.save(variant);
        persistenceService.saveToFile();
        return saved;
    }

    public List<Variant> findAll() {
        return repository.findAll();
    }

    public Optional<Variant> findById(String id) {
        return repository.findById(id);
    }

    public Variant update(String id, Variant variant) {
        variant.setId(id);
        Variant updated = repository.save(variant);
        persistenceService.saveToFile();
        return updated;
    }

    public void delete(String id) {
        repository.deleteById(id);
        persistenceService.saveToFile();
    }
}
