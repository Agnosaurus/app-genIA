package eu.askadev.magic.controller;

import eu.askadev.magic.model.Publication;
import eu.askadev.magic.repository.PublicationRepository;
import eu.askadev.magic.service.PersistenceService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PublicationController {

    private final PublicationRepository repository;
    private final PersistenceService persistenceService;

    public PublicationController(PublicationRepository repository, PersistenceService persistenceService) {
        this.repository = repository;
        this.persistenceService = persistenceService;
    }

    public Publication create(Publication publication) {
        Publication saved = repository.save(publication);
        persistenceService.saveToFile();
        return saved;
    }

    public List<Publication> findAll() {
        return repository.findAll();
    }

    public Optional<Publication> findById(String id) {
        return repository.findById(id);
    }

    public Publication update(String id, Publication publication) {
        publication.setId(id);
        Publication updated = repository.save(publication);
        persistenceService.saveToFile();
        return updated;
    }

    public void delete(String id) {
        repository.deleteById(id);
        persistenceService.saveToFile();
    }
}
