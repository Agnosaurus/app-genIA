package eu.askadev.magic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "manuscript")
public class Manuscript {

    @Id
    private String id;

    private String uniqueId;
    private Set<String> extracts = new HashSet<>();
    private String referenceManuscript;
    private String titre;

    @DBRef
    private Set<Author> authors;

    private String lieu;
    private String date;

    @DBRef
    private Language language;

    private String scriptorium;

    public Manuscript() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Set<String> getExtracts() {
        return extracts;
    }

    public void setExtracts(Set<String> extracts) {
        this.extracts = extracts;
    }

    public String getReferenceManuscript() {
        return referenceManuscript;
    }

    public void setReferenceManuscript(String referenceManuscript) {
        this.referenceManuscript = referenceManuscript;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getScriptorium() {
        return scriptorium;
    }

    public void setScriptorium(String scriptorium) {
        this.scriptorium = scriptorium;
    }

    @Override
    public String toString() {
        return uniqueId;
    }
}
