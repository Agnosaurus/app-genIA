package eu.askadev.magic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.askadev.magic.model.*;
import eu.askadev.magic.repository.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PersistenceService {

    private static final String DB_FILE = "database-file.db";
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private final AuthorRepository authorRepository;
    private final ConceptRepository conceptRepository;
    private final KeywordRepository keywordRepository;
    private final LanguageRepository languageRepository;
    private final ManuscriptRepository manuscriptRepository;
    private final NameRepository nameRepository;
    private final PublicationRepository publicationRepository;
    private final VariantRepository variantRepository;

    public PersistenceService(AuthorRepository authorRepository, ConceptRepository conceptRepository,
                            KeywordRepository keywordRepository, LanguageRepository languageRepository,
                            ManuscriptRepository manuscriptRepository, NameRepository nameRepository,
                            PublicationRepository publicationRepository, VariantRepository variantRepository) {
        this.authorRepository = authorRepository;
        this.conceptRepository = conceptRepository;
        this.keywordRepository = keywordRepository;
        this.languageRepository = languageRepository;
        this.manuscriptRepository = manuscriptRepository;
        this.nameRepository = nameRepository;
        this.publicationRepository = publicationRepository;
        this.variantRepository = variantRepository;
    }

    public void saveToFile() {
        Map<String, Object> data = new HashMap<>();
        data.put("authors", authorRepository.findAll());
        data.put("concepts", conceptRepository.findAll());
        data.put("keywords", keywordRepository.findAll());
        data.put("languages", languageRepository.findAll());
        data.put("manuscripts", manuscriptRepository.findAll());
        data.put("names", nameRepository.findAll());
        data.put("publications", publicationRepository.findAll());
        data.put("variants", variantRepository.findAll());

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(DB_FILE), data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save database to file", e);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        File file = new File(DB_FILE);
        if (!file.exists()) {
            return;
        }

        try {
            Map<String, Object> data = objectMapper.readValue(file, Map.class);

            // Delete all in reverse dependency order
            variantRepository.deleteAll();
            manuscriptRepository.deleteAll();
            conceptRepository.deleteAll();
            authorRepository.deleteAll();
            keywordRepository.deleteAll();
            languageRepository.deleteAll();
            nameRepository.deleteAll();
            publicationRepository.deleteAll();

            // Load leaf entities first (no @DBRef dependencies)
            loadList(data, "authors", Author.class).forEach(authorRepository::save);
            loadList(data, "keywords", Keyword.class).forEach(keywordRepository::save);
            loadList(data, "languages", Language.class).forEach(languageRepository::save);
            loadList(data, "names", Name.class).forEach(nameRepository::save);
            loadList(data, "publications", Publication.class).forEach(publicationRepository::save);

            // Load entities with @DBRef: resolve references by ID from already-saved entities
            loadList(data, "concepts", Concept.class).forEach(concept -> {
                if (concept.getKeywords() != null) {
                    Set<Keyword> resolved = concept.getKeywords().stream()
                        .map(k -> keywordRepository.findById(k.getId()).orElse(null))
                        .filter(k -> k != null)
                        .collect(java.util.stream.Collectors.toSet());
                    concept.setKeywords(resolved);
                }
                conceptRepository.save(concept);
            });

            loadList(data, "manuscripts", Manuscript.class).forEach(manuscript -> {
                if (manuscript.getLanguage() != null) {
                    manuscript.setLanguage(languageRepository.findById(manuscript.getLanguage().getId()).orElse(null));
                }
                if (manuscript.getAuthors() != null) {
                    Set<Author> resolved = manuscript.getAuthors().stream()
                        .map(a -> authorRepository.findById(a.getId()).orElse(null))
                        .filter(a -> a != null)
                        .collect(java.util.stream.Collectors.toSet());
                    manuscript.setAuthors(resolved);
                }
                manuscriptRepository.save(manuscript);
            });

            loadList(data, "variants", Variant.class).forEach(variant -> {
                if (variant.getName() != null) {
                    variant.setName(nameRepository.findById(variant.getName().getId()).orElse(null));
                }
                variantRepository.save(variant);
            });

        } catch (IOException e) {
            throw new RuntimeException("Failed to load database from file", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> loadList(Map<String, Object> data, String key, Class<T> type) {
        if (!data.containsKey(key)) return java.util.Collections.emptyList();
        List<Map<String, Object>> items = (List<Map<String, Object>>) data.get(key);
        return items.stream()
            .map(item -> objectMapper.convertValue(item, type))
            .collect(java.util.stream.Collectors.toList());
    }
}
