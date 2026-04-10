package eu.askadev.magic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subkeyword")
public class SubKeyword {

    @Id
    private String id;

    private String value;

    @DBRef
    private Keyword parentKeyword;

    public SubKeyword() {
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

    public Keyword getParentKeyword() {
        return parentKeyword;
    }

    public void setParentKeyword(Keyword parentKeyword) {
        this.parentKeyword = parentKeyword;
    }

    @Override
    public String toString() {
        return value;
    }
}
