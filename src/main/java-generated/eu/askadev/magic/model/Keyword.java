package eu.askadev.magic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "keyword")
public class Keyword {

    @Id
    private String id;

    private String value;

    @DBRef
    private Set<SubKeyword> childSubKeywords = new HashSet<>();

    @DBRef
    private Concept parentConcept;

    public Keyword() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Set<SubKeyword> getChildSubKeywords() {
        return childSubKeywords;
    }

    public void setChildSubKeywords(Set<SubKeyword> childSubKeywords) {
        this.childSubKeywords = childSubKeywords;
    }

    public Concept getParentConcept() {
        return parentConcept;
    }

    public void setParentConcept(Concept parentConcept) {
        this.parentConcept = parentConcept;
    }

    @Override
    public String toString() {
        return value;
    }
}
