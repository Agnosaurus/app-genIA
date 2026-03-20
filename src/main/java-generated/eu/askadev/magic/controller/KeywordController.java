package eu.askadev.magic.controller;

import eu.askadev.magic.model.Keyword;
import eu.askadev.magic.repository.KeywordRepository;
import eu.askadev.magic.service.PersistenceService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class KeywordController {

    private final KeywordRepository repository;
    private final PersistenceService persistenceService;

    public KeywordController(KeywordRepository repository, PersistenceService persistenceService) {
        this.repository = repository;
        this.persistenceService = persistenceService;
    }

    public Keyword create(Keyword keyword) {
        Keyword saved = repository.save(keyword);
        persistenceService.saveToFile();
        return saved;
    }

    public List<Keyword> findAll() {
        return repository.findAll();
    }

    public Optional<Keyword> findById(String id) {
        return repository.findById(id);
    }

    public Keyword update(String id, Keyword keyword) {
        keyword.setId(id);
        Keyword updated = repository.save(keyword);
        persistenceService.saveToFile();
        return updated;
    }

    public void delete(String id) {
        repository.deleteById(id);
        persistenceService.saveToFile();
    }
}
