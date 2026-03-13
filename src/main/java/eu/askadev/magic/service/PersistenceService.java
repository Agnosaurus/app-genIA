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
            
            authorRepository.deleteAll();
            conceptRepository.deleteAll();
            keywordRepository.deleteAll();
            languageRepository.deleteAll();
            manuscriptRepository.deleteAll();
            nameRepository.deleteAll();
            publicationRepository.deleteAll();
            variantRepository.deleteAll();

            if (data.containsKey("authors")) {
                List<Map<String, Object>> authors = (List<Map<String, Object>>) data.get("authors");
                authors.forEach(a -> authorRepository.save(objectMapper.convertValue(a, Author.class)));
            }
            if (data.containsKey("concepts")) {
                List<Map<String, Object>> concepts = (List<Map<String, Object>>) data.get("concepts");
                concepts.forEach(c -> conceptRepository.save(objectMapper.convertValue(c, Concept.class)));
            }
            if (data.containsKey("keywords")) {
                List<Map<String, Object>> keywords = (List<Map<String, Object>>) data.get("keywords");
                keywords.forEach(k -> keywordRepository.save(objectMapper.convertValue(k, Keyword.class)));
            }
            if (data.containsKey("languages")) {
                List<Map<String, Object>> languages = (List<Map<String, Object>>) data.get("languages");
                languages.forEach(l -> languageRepository.save(objectMapper.convertValue(l, Language.class)));
            }
            if (data.containsKey("manuscripts")) {
                List<Map<String, Object>> manuscripts = (List<Map<String, Object>>) data.get("manuscripts");
                manuscripts.forEach(m -> manuscriptRepository.save(objectMapper.convertValue(m, Manuscript.class)));
            }
            if (data.containsKey("names")) {
                List<Map<String, Object>> names = (List<Map<String, Object>>) data.get("names");
                names.forEach(n -> nameRepository.save(objectMapper.convertValue(n, Name.class)));
            }
            if (data.containsKey("publications")) {
                List<Map<String, Object>> publications = (List<Map<String, Object>>) data.get("publications");
                publications.forEach(p -> publicationRepository.save(objectMapper.convertValue(p, Publication.class)));
            }
            if (data.containsKey("variants")) {
                List<Map<String, Object>> variants = (List<Map<String, Object>>) data.get("variants");
                variants.forEach(v -> variantRepository.save(objectMapper.convertValue(v, Variant.class)));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database from file", e);
        }
    }
}
