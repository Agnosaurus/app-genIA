package eu.askadev.magic.controller;

import eu.askadev.magic.model.Concept;
import eu.askadev.magic.repository.ConceptRepository;
import eu.askadev.magic.service.PersistenceService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ConceptController {

    private final ConceptRepository repository;
    private final PersistenceService persistenceService;

    public ConceptController(ConceptRepository repository, PersistenceService persistenceService) {
        this.repository = repository;
        this.persistenceService = persistenceService;
    }

    public Concept create(Concept concept) {
        Concept saved = repository.save(concept);
        persistenceService.saveToFile();
        return saved;
    }

    public List<Concept> findAll() {
        return repository.findAll();
    }

    public Optional<Concept> findById(String id) {
        return repository.findById(id);
    }

    public Concept update(String id, Concept concept) {
        concept.setId(id);
        Concept updated = repository.save(concept);
        persistenceService.saveToFile();
        return updated;
    }

    public void delete(String id) {
        repository.deleteById(id);
        persistenceService.saveToFile();
    }
}
