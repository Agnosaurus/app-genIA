package eu.askadev.magic.controller;

import eu.askadev.magic.model.SubKeyword;
import eu.askadev.magic.repository.SubKeywordRepository;
import eu.askadev.magic.service.PersistenceService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SubKeywordController {

    private final SubKeywordRepository repository;
    private final PersistenceService persistenceService;

    public SubKeywordController(SubKeywordRepository repository, PersistenceService persistenceService) {
        this.repository = repository;
        this.persistenceService = persistenceService;
    }

    public SubKeyword create(SubKeyword subKeyword) {
        SubKeyword saved = repository.save(subKeyword);
        persistenceService.saveToFile();
        return saved;
    }

    public List<SubKeyword> findAll() {
        return repository.findAll();
    }

    public Optional<SubKeyword> findById(String id) {
        return repository.findById(id);
    }

    public SubKeyword update(String id, SubKeyword subKeyword) {
        subKeyword.setId(id);
        SubKeyword updated = repository.save(subKeyword);
        persistenceService.saveToFile();
        return updated;
    }

    public void delete(String id) {
        repository.deleteById(id);
        persistenceService.saveToFile();
    }
}
