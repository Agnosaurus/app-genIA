package eu.askadev.magic.controller;

import eu.askadev.magic.model.Manuscript;
import eu.askadev.magic.repository.ManuscriptRepository;
import eu.askadev.magic.service.PersistenceService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ManuscriptController {

    private final ManuscriptRepository repository;
    private final PersistenceService persistenceService;

    public ManuscriptController(ManuscriptRepository repository, PersistenceService persistenceService) {
        this.repository = repository;
        this.persistenceService = persistenceService;
    }

    public Manuscript create(Manuscript manuscript) {
        Manuscript saved = repository.save(manuscript);
        persistenceService.saveToFile();
        return saved;
    }

    public List<Manuscript> findAll() {
        return repository.findAll();
    }

    public Optional<Manuscript> findById(String id) {
        return repository.findById(id);
    }

    public Manuscript update(String id, Manuscript manuscript) {
        manuscript.setId(id);
        Manuscript updated = repository.save(manuscript);
        persistenceService.saveToFile();
        return updated;
    }

    public void delete(String id) {
        repository.deleteById(id);
        persistenceService.saveToFile();
    }
}
