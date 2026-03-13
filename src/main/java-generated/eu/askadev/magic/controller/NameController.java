package eu.askadev.magic.controller;

import eu.askadev.magic.model.Name;
import eu.askadev.magic.repository.NameRepository;
import eu.askadev.magic.service.PersistenceService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class NameController {

    private final NameRepository repository;
    private final PersistenceService persistenceService;

    public NameController(NameRepository repository, PersistenceService persistenceService) {
        this.repository = repository;
        this.persistenceService = persistenceService;
    }

    public Name create(Name name) {
        Name saved = repository.save(name);
        persistenceService.saveToFile();
        return saved;
    }

    public List<Name> findAll() {
        return repository.findAll();
    }

    public Optional<Name> findById(String id) {
        return repository.findById(id);
    }

    public Name update(String id, Name name) {
        name.setId(id);
        Name updated = repository.save(name);
        persistenceService.saveToFile();
        return updated;
    }

    public void delete(String id) {
        repository.deleteById(id);
        persistenceService.saveToFile();
    }
}
