package eu.askadev.magic.controller;

import eu.askadev.magic.model.Author;
import eu.askadev.magic.repository.AuthorRepository;
import eu.askadev.magic.service.PersistenceService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AuthorController {

    private final AuthorRepository repository;
    private final PersistenceService persistenceService;

    public AuthorController(AuthorRepository repository, PersistenceService persistenceService) {
        this.repository = repository;
        this.persistenceService = persistenceService;
    }

    public Author create(Author author) {
        Author saved = repository.save(author);
        persistenceService.saveToFile();
        return saved;
    }

    public List<Author> findAll() {
        return repository.findAll();
    }

    public Optional<Author> findById(String id) {
        return repository.findById(id);
    }

    public Author update(String id, Author author) {
        author.setId(id);
        Author updated = repository.save(author);
        persistenceService.saveToFile();
        return updated;
    }

    public void delete(String id) {
        repository.deleteById(id);
        persistenceService.saveToFile();
    }
}
