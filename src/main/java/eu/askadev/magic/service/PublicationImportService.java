package eu.askadev.magic.service;

import eu.askadev.magic.model.Publication;
import eu.askadev.magic.repository.PublicationRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PublicationImportService {

    @Autowired
    private PublicationRepository publicationRepository;

    public void importCsv(MultipartFile file) throws Exception {
        List<Publication> publications = new ArrayList<>();

        try (BOMInputStream bomInputStream = new BOMInputStream(file.getInputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(bomInputStream, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {
                Publication publication = new Publication();
                publication.setKey(record.get("Key"));
                publication.setItemType(record.get("Item Type"));
                publication.setPublicationYear(record.get("Publication Year"));
                publication.setAuthor(record.get("Author"));
                publication.setTitle(record.get("Title"));
                publication.setPublicationTitle(record.get("Publication Title"));
                publication.setIsbn(record.get("ISBN"));
                publication.setIssn(record.get("ISSN"));
                publication.setDoi(record.get("DOI"));
                publication.setUrl(record.get("Url"));
                publication.setAbstractNote(record.get("Abstract Note"));
                publication.setDate(record.get("Date"));
                publication.setDateAdded(record.get("Date Added"));
                publication.setDateModified(record.get("Date Modified"));
                publication.setAccessDate(record.get("Access Date"));
                publication.setPages(record.get("Pages"));
                publication.setNumPages(record.get("Num Pages"));
                publication.setIssue(record.get("Issue"));
                publication.setVolume(record.get("Volume"));
                publication.setNumberOfVolumes(record.get("Number Of Volumes"));
                publication.setJournalAbbreviation(record.get("Journal Abbreviation"));
                publication.setShortTitle(record.get("Short Title"));
                publication.setSeries(record.get("Series"));
                publication.setSeriesNumber(record.get("Series Number"));
                publication.setSeriesText(record.get("Series Text"));
                publication.setSeriesTitle(record.get("Series Title"));
                publication.setPublisher(record.get("Publisher"));
                publication.setPlace(record.get("Place"));
                publication.setLanguage(record.get("Language"));
                publication.setRights(record.get("Rights"));
                publication.setType(record.get("Type"));
                publication.setArchive(record.get("Archive"));
                publication.setArchiveLocation(record.get("Archive Location"));
                publication.setLibraryCatalog(record.get("Library Catalog"));
                publication.setCallNumber(record.get("Call Number"));
                publication.setExtra(record.get("Extra"));

                publication.setNotes(splitSet(record.get("Notes")));
                publication.setFileAttachments(splitSet(record.get("File Attachments")));
                publication.setLinkAttachments(splitSet(record.get("Link Attachments")));
                publication.setManualTags(splitSet(record.get("Manual Tags")));
                publication.setAutomaticTags(splitSet(record.get("Automatic Tags")));

                publication.setEditor(record.get("Editor"));
                publication.setSeriesEditor(record.get("Series Editor"));
                publication.setTranslator(record.get("Translator"));
                publication.setContributor(record.get("Contributor"));
                publication.setAttorneyAgent(record.get("Attorney Agent"));
                publication.setBookAuthor(record.get("Book Author"));
                publication.setCastMember(record.get("Cast Member"));
                publication.setCommenter(record.get("Commenter"));
                publication.setComposer(record.get("Composer"));
                publication.setCosponsor(record.get("Cosponsor"));
                publication.setCounsel(record.get("Counsel"));
                publication.setInterviewer(record.get("Interviewer"));
                publication.setProducer(record.get("Producer"));
                publication.setRecipient(record.get("Recipient"));
                publication.setReviewedAuthor(record.get("Reviewed Author"));
                publication.setScriptwriter(record.get("Scriptwriter"));
                publication.setWordsBy(record.get("Words By"));
                publication.setGuest(record.get("Guest"));

                publication.setNumber(record.get("Number"));
                publication.setEdition(record.get("Edition"));
                publication.setRunningTime(record.get("Running Time"));
                publication.setScale(record.get("Scale"));
                publication.setMedium(record.get("Medium"));
                publication.setArtworkSize(record.get("Artwork Size"));

                publication.setFilingDate(record.get("Filing Date"));
                publication.setApplicationNumber(record.get("Application Number"));
                publication.setAssignee(record.get("Assignee"));
                publication.setIssuingAuthority(record.get("Issuing Authority"));
                publication.setCountry(record.get("Country"));

                publication.setMeetingName(record.get("Meeting Name"));
                publication.setConferenceName(record.get("Conference Name"));
                publication.setCourt(record.get("Court"));
                publication.setReferences(record.get("References"));
                publication.setReporter(record.get("Reporter"));
                publication.setLegalStatus(record.get("Legal Status"));
                publication.setPriorityNumbers(record.get("Priority Numbers"));

                publication.setProgrammingLanguage(record.get("Programming Language"));
                publication.setVersion(record.get("Version"));
                publication.setSystem(record.get("System"));
                publication.setCode(record.get("Code"));
                publication.setCodeNumber(record.get("Code Number"));
                publication.setSection(record.get("Section"));
                publication.setSession(record.get("Session"));
                publication.setCommittee(record.get("Committee"));
                publication.setHistory(record.get("History"));
                publication.setLegislativeBody(record.get("Legislative Body"));

                publications.add(publication);
            }

            publicationRepository.saveAll(publications);
        }
    }

    public void importCsv(File file) throws Exception {
        List<Publication> publications = new ArrayList<>();
        try (BOMInputStream bomInputStream = new BOMInputStream(new java.io.FileInputStream(file));
             BufferedReader reader = new BufferedReader(new InputStreamReader(bomInputStream, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                Publication publication = new Publication();
                publication.setKey(record.get("Key"));
                publication.setItemType(record.get("Item Type"));
                publication.setPublicationYear(record.get("Publication Year"));
                publication.setAuthor(record.get("Author"));
                publication.setTitle(record.get("Title"));
                publication.setPublicationTitle(record.get("Publication Title"));
                publication.setIsbn(record.get("ISBN"));
                publication.setIssn(record.get("ISSN"));
                publication.setDoi(record.get("DOI"));
                publication.setUrl(record.get("Url"));
                publication.setAbstractNote(record.get("Abstract Note"));
                publication.setDate(record.get("Date"));
                publication.setDateAdded(record.get("Date Added"));
                publication.setDateModified(record.get("Date Modified"));
                publication.setAccessDate(record.get("Access Date"));
                publication.setPages(record.get("Pages"));
                publication.setNumPages(record.get("Num Pages"));
                publication.setIssue(record.get("Issue"));
                publication.setVolume(record.get("Volume"));
                publication.setNumberOfVolumes(record.get("Number Of Volumes"));
                publication.setJournalAbbreviation(record.get("Journal Abbreviation"));
                publication.setShortTitle(record.get("Short Title"));
                publication.setSeries(record.get("Series"));
                publication.setSeriesNumber(record.get("Series Number"));
                publication.setSeriesText(record.get("Series Text"));
                publication.setSeriesTitle(record.get("Series Title"));
                publication.setPublisher(record.get("Publisher"));
                publication.setPlace(record.get("Place"));
                publication.setLanguage(record.get("Language"));
                publication.setRights(record.get("Rights"));
                publication.setType(record.get("Type"));
                publication.setArchive(record.get("Archive"));
                publication.setArchiveLocation(record.get("Archive Location"));
                publication.setLibraryCatalog(record.get("Library Catalog"));
                publication.setCallNumber(record.get("Call Number"));
                publication.setExtra(record.get("Extra"));

                publication.setNotes(splitSet(record.get("Notes")));
                publication.setFileAttachments(splitSet(record.get("File Attachments")));
                publication.setLinkAttachments(splitSet(record.get("Link Attachments")));
                publication.setManualTags(splitSet(record.get("Manual Tags")));
                publication.setAutomaticTags(splitSet(record.get("Automatic Tags")));

                publication.setEditor(record.get("Editor"));
                publication.setSeriesEditor(record.get("Series Editor"));
                publication.setTranslator(record.get("Translator"));
                publication.setContributor(record.get("Contributor"));
                publication.setAttorneyAgent(record.get("Attorney Agent"));
                publication.setBookAuthor(record.get("Book Author"));
                publication.setCastMember(record.get("Cast Member"));
                publication.setCommenter(record.get("Commenter"));
                publication.setComposer(record.get("Composer"));
                publication.setCosponsor(record.get("Cosponsor"));
                publication.setCounsel(record.get("Counsel"));
                publication.setInterviewer(record.get("Interviewer"));
                publication.setProducer(record.get("Producer"));
                publication.setRecipient(record.get("Recipient"));
                publication.setReviewedAuthor(record.get("Reviewed Author"));
                publication.setScriptwriter(record.get("Scriptwriter"));
                publication.setWordsBy(record.get("Words By"));
                publication.setGuest(record.get("Guest"));

                publication.setNumber(record.get("Number"));
                publication.setEdition(record.get("Edition"));
                publication.setRunningTime(record.get("Running Time"));
                publication.setScale(record.get("Scale"));
                publication.setMedium(record.get("Medium"));
                publication.setArtworkSize(record.get("Artwork Size"));

                publication.setFilingDate(record.get("Filing Date"));
                publication.setApplicationNumber(record.get("Application Number"));
                publication.setAssignee(record.get("Assignee"));
                publication.setIssuingAuthority(record.get("Issuing Authority"));
                publication.setCountry(record.get("Country"));

                publication.setMeetingName(record.get("Meeting Name"));
                publication.setConferenceName(record.get("Conference Name"));
                publication.setCourt(record.get("Court"));
                publication.setReferences(record.get("References"));
                publication.setReporter(record.get("Reporter"));
                publication.setLegalStatus(record.get("Legal Status"));
                publication.setPriorityNumbers(record.get("Priority Numbers"));

                publication.setProgrammingLanguage(record.get("Programming Language"));
                publication.setVersion(record.get("Version"));
                publication.setSystem(record.get("System"));
                publication.setCode(record.get("Code"));
                publication.setCodeNumber(record.get("Code Number"));
                publication.setSection(record.get("Section"));
                publication.setSession(record.get("Session"));
                publication.setCommittee(record.get("Committee"));
                publication.setHistory(record.get("History"));
                publication.setLegislativeBody(record.get("Legislative Body"));

                publications.add(publication);
            }
            publicationRepository.saveAll(publications);
        }
    }

    private Set<String> splitSet(String value) {
        if (value == null || value.isBlank()) return new HashSet<>();
        String[] arr = value.split(";");
        Set<String> set = new HashSet<>();
        for (String s : arr) {
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) set.add(trimmed);
        }
        return set;
    }
}
