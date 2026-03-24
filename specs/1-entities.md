# Author entity
@Document(collection = "author")
public class Author {

    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String birthDate;
}


# Concept entity
@Document(collection = "concept")
public class Concept {

    @Id
    private String id;

    private String value;

    @DBRef
    private Set<Keyword> keywords = new HashSet<>();

}


# Keyword entity
@Document(collection = "keyword")
public class Keyword {

    @Id
    private String id;

    private String value;
}

# Language entity
@Document(collection = "language")
public class Language {

    @MongoId
    private String id; // MongoDB uses String for the default _id field
    private String name;

# Manuscript entity
@Document(collection = "manuscript")
public class Manuscript {

    @Id
    private String id;
    private String uniqueId; // entered manually

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

# Name entity
@Document(collection = "name")
public class Name {

    @Id
    private String id;

    private String name;

    @DBRef // Relation avec Language
    private Language lang;


# Publication entity
@Document(collection = "publication")
public class Publication {

    @Id
    private String id;

    private String key;
    private String itemType;
    private String publicationYear;
    private String author;
    private String title;
    private String publicationTitle;
    private String isbn;
    private String issn;
    private String doi;
    private String url;
    private String abstractNote;
    private String date;
    private String dateAdded;
    private String dateModified;
    private String accessDate;
    private String pages;
    private String numPages;
    private String issue;
    private String volume;
    private String numberOfVolumes;
    private String journalAbbreviation;
    private String shortTitle;
    private String series;
    private String seriesNumber;
    private String seriesText;
    private String seriesTitle;
    private String publisher;
    private String place;
    private String language;
    private String rights;
    private String type;
    private String archive;
    private String archiveLocation;
    private String libraryCatalog;
    private String callNumber;
    private String extra;

    private Set<String> notes;
    private Set<String> fileAttachments;
    private Set<String> linkAttachments;
    private Set<String> manualTags;
    private Set<String> automaticTags;

    private String editor;
    private String seriesEditor;
    private String translator;
    private String contributor;
    private String attorneyAgent;
    private String bookAuthor;
    private String castMember;
    private String commenter;
    private String composer;
    private String cosponsor;
    private String counsel;
    private String interviewer;
    private String producer;
    private String recipient;
    private String reviewedAuthor;
    private String scriptwriter;
    private String wordsBy;
    private String guest;

    private String number;
    private String edition;
    private String runningTime;
    private String scale;
    private String medium;
    private String artworkSize;

    private String filingDate;
    private String applicationNumber;
    private String assignee;
    private String issuingAuthority;
    private String country;

    private String meetingName;
    private String conferenceName;
    private String court;
    private String references;
    private String reporter;
    private String legalStatus;
    private String priorityNumbers;

    private String programmingLanguage;
    private String version;
    private String system;
    private String code;
    private String codeNumber;
    private String section;
    private String session;
    private String committee;
    private String history;
    private String legislativeBody;
}



# Variant entity
@Document(collection = "variant")
public class Variant {

    @Id
    private String id;

    private String variant;

    @DBRef
    private Name name;



# Additional methods
variant.getName // return the name

-**All entities should have a public String toString() method**
-**Those methods will return the most significant String data of the entity. EG Name, uniqueId **



