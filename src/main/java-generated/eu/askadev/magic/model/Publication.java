package eu.askadev.magic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

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

    public Publication() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublicationTitle() {
        return publicationTitle;
    }

    public void setPublicationTitle(String publicationTitle) {
        this.publicationTitle = publicationTitle;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAbstractNote() {
        return abstractNote;
    }

    public void setAbstractNote(String abstractNote) {
        this.abstractNote = abstractNote;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(String accessDate) {
        this.accessDate = accessDate;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getNumPages() {
        return numPages;
    }

    public void setNumPages(String numPages) {
        this.numPages = numPages;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getNumberOfVolumes() {
        return numberOfVolumes;
    }

    public void setNumberOfVolumes(String numberOfVolumes) {
        this.numberOfVolumes = numberOfVolumes;
    }

    public String getJournalAbbreviation() {
        return journalAbbreviation;
    }

    public void setJournalAbbreviation(String journalAbbreviation) {
        this.journalAbbreviation = journalAbbreviation;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getSeriesNumber() {
        return seriesNumber;
    }

    public void setSeriesNumber(String seriesNumber) {
        this.seriesNumber = seriesNumber;
    }

    public String getSeriesText() {
        return seriesText;
    }

    public void setSeriesText(String seriesText) {
        this.seriesText = seriesText;
    }

    public String getSeriesTitle() {
        return seriesTitle;
    }

    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArchive() {
        return archive;
    }

    public void setArchive(String archive) {
        this.archive = archive;
    }

    public String getArchiveLocation() {
        return archiveLocation;
    }

    public void setArchiveLocation(String archiveLocation) {
        this.archiveLocation = archiveLocation;
    }

    public String getLibraryCatalog() {
        return libraryCatalog;
    }

    public void setLibraryCatalog(String libraryCatalog) {
        this.libraryCatalog = libraryCatalog;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Set<String> getNotes() {
        return notes;
    }

    public void setNotes(Set<String> notes) {
        this.notes = notes;
    }

    public Set<String> getFileAttachments() {
        return fileAttachments;
    }

    public void setFileAttachments(Set<String> fileAttachments) {
        this.fileAttachments = fileAttachments;
    }

    public Set<String> getLinkAttachments() {
        return linkAttachments;
    }

    public void setLinkAttachments(Set<String> linkAttachments) {
        this.linkAttachments = linkAttachments;
    }

    public Set<String> getManualTags() {
        return manualTags;
    }

    public void setManualTags(Set<String> manualTags) {
        this.manualTags = manualTags;
    }

    public Set<String> getAutomaticTags() {
        return automaticTags;
    }

    public void setAutomaticTags(Set<String> automaticTags) {
        this.automaticTags = automaticTags;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getSeriesEditor() {
        return seriesEditor;
    }

    public void setSeriesEditor(String seriesEditor) {
        this.seriesEditor = seriesEditor;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getAttorneyAgent() {
        return attorneyAgent;
    }

    public void setAttorneyAgent(String attorneyAgent) {
        this.attorneyAgent = attorneyAgent;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getCastMember() {
        return castMember;
    }

    public void setCastMember(String castMember) {
        this.castMember = castMember;
    }

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getCosponsor() {
        return cosponsor;
    }

    public void setCosponsor(String cosponsor) {
        this.cosponsor = cosponsor;
    }

    public String getCounsel() {
        return counsel;
    }

    public void setCounsel(String counsel) {
        this.counsel = counsel;
    }

    public String getInterviewer() {
        return interviewer;
    }

    public void setInterviewer(String interviewer) {
        this.interviewer = interviewer;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getReviewedAuthor() {
        return reviewedAuthor;
    }

    public void setReviewedAuthor(String reviewedAuthor) {
        this.reviewedAuthor = reviewedAuthor;
    }

    public String getScriptwriter() {
        return scriptwriter;
    }

    public void setScriptwriter(String scriptwriter) {
        this.scriptwriter = scriptwriter;
    }

    public String getWordsBy() {
        return wordsBy;
    }

    public void setWordsBy(String wordsBy) {
        this.wordsBy = wordsBy;
    }

    public String getGuest() {
        return guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(String runningTime) {
        this.runningTime = runningTime;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getArtworkSize() {
        return artworkSize;
    }

    public void setArtworkSize(String artworkSize) {
        this.artworkSize = artworkSize;
    }

    public String getFilingDate() {
        return filingDate;
    }

    public void setFilingDate(String filingDate) {
        this.filingDate = filingDate;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getConferenceName() {
        return conferenceName;
    }

    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }

    public String getCourt() {
        return court;
    }

    public void setCourt(String court) {
        this.court = court;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getLegalStatus() {
        return legalStatus;
    }

    public void setLegalStatus(String legalStatus) {
        this.legalStatus = legalStatus;
    }

    public String getPriorityNumbers() {
        return priorityNumbers;
    }

    public void setPriorityNumbers(String priorityNumbers) {
        this.priorityNumbers = priorityNumbers;
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeNumber() {
        return codeNumber;
    }

    public void setCodeNumber(String codeNumber) {
        this.codeNumber = codeNumber;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getCommittee() {
        return committee;
    }

    public void setCommittee(String committee) {
        this.committee = committee;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getLegislativeBody() {
        return legislativeBody;
    }

    public void setLegislativeBody(String legislativeBody) {
        this.legislativeBody = legislativeBody;
    }
}
