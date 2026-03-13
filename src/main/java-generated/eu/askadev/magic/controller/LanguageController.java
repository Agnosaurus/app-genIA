package eu.askadev.magic.controller;

import eu.askadev.magic.model.Language;
import eu.askadev.magic.repository.LanguageRepository;
import eu.askadev.magic.service.PersistenceService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LanguageController {

    private final LanguageRepository repository;
    private final PersistenceService persistenceService;

    public LanguageController(LanguageRepository repository, PersistenceService persistenceService) {
        this.repository = repository;
        this.persistenceService = persistenceService;
    }

    public Language create(Language language) {
        Language saved = repository.save(language);
        persistenceService.saveToFile();
        return saved;
    }

    public List<Language> findAll() {
        return repository.findAll();
    }

    public Optional<Language> findById(String id) {
        return repository.findById(id);
    }

    public Language update(String id, Language language) {
        language.setId(id);
        Language updated = repository.save(language);
        persistenceService.saveToFile();
        return updated;
    }

    public void delete(String id) {
        repository.deleteById(id);
        persistenceService.saveToFile();
    }
}
