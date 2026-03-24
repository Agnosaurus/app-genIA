package eu.askadev.magic.view;

import eu.askadev.magic.controller.PublicationController;
import eu.askadev.magic.model.Publication;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class PublicationListView {

    private final PublicationController controller;
    private TableView<Publication> tableView;
    private VBox detailView;
    private Publication selectedPublication;
    private boolean isEditMode = false;

    public PublicationListView(PublicationController controller) {
        this.controller = controller;
    }

    public Parent getView(LandingView landingView) {
        SplitPane splitPane = new SplitPane();
        splitPane.setPrefWidth(800);
        splitPane.setPrefHeight(600);

        VBox listBox = createListView();
        detailView = new VBox(10);
        detailView.setPadding(new Insets(20));
        refreshDetailView();

        splitPane.getItems().addAll(listBox, detailView);
        splitPane.setDividerPosition(0, 0.35);

        Button backBtn = new Button("Back to Home");
        backBtn.setOnAction(e -> {
            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.getScene().setRoot(landingView.getView());
        });

        VBox root = new VBox(10, splitPane, backBtn);
        root.setPadding(new Insets(10));
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        return root;
    }

    private VBox createListView() {
        VBox listBox = new VBox(10);
        listBox.setPadding(new Insets(10));

        Label title = new Label("Publications");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        TableColumn<Publication, String> keyCol = new TableColumn<>("Key");
        keyCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getKey()));
        TableColumn<Publication, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthor()));
        TableColumn<Publication, String> pubTitleCol = new TableColumn<>("Publication Title");
        pubTitleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPublicationTitle()));
        TableColumn<Publication, String> shortTitleCol = new TableColumn<>("Short Title");
        shortTitleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getShortTitle()));

        tableView.getColumns().addAll(keyCol, authorCol, pubTitleCol, shortTitleCol);
        refreshList();

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedPublication = newVal;
            isEditMode = false;
            refreshDetailView();
        });

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && selectedPublication != null) {
                isEditMode = true;
                refreshDetailView();
            }
        });

        Button addBtn = new Button("Add New");
        addBtn.setOnAction(e -> {
            selectedPublication = null;
            isEditMode = true;
            refreshDetailView();
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            if (selectedPublication != null) {
                controller.delete(selectedPublication.getId());
                refreshList();
                selectedPublication = null;
                refreshDetailView();
            }
        });

        HBox buttons = new HBox(10, addBtn, deleteBtn);
        listBox.getChildren().addAll(title, tableView, buttons);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        return listBox;
    }

    private void refreshList() {
        tableView.setItems(FXCollections.observableArrayList(controller.findAll()));
    }

    private void refreshDetailView() {
        detailView.getChildren().clear();

        if (selectedPublication == null && !isEditMode) {
            detailView.getChildren().add(new Label("Select a publication or click Add New"));
            return;
        }

        // Create all text fields for Publication entity
        TextField keyField = new TextField(selectedPublication == null ? "" : (selectedPublication.getKey() != null ? selectedPublication.getKey() : ""));
        TextField itemTypeField = new TextField(selectedPublication == null ? "" : (selectedPublication.getItemType() != null ? selectedPublication.getItemType() : ""));
        TextField titleField = new TextField(selectedPublication == null ? "" : (selectedPublication.getTitle() != null ? selectedPublication.getTitle() : ""));
        TextField authorField = new TextField(selectedPublication == null ? "" : (selectedPublication.getAuthor() != null ? selectedPublication.getAuthor() : ""));
        TextField yearField = new TextField(selectedPublication == null ? "" : (selectedPublication.getPublicationYear() != null ? selectedPublication.getPublicationYear() : ""));
        TextField publisherField = new TextField(selectedPublication == null ? "" : (selectedPublication.getPublisher() != null ? selectedPublication.getPublisher() : ""));
        TextField publicationTitleField = new TextField(selectedPublication == null ? "" : (selectedPublication.getPublicationTitle() != null ? selectedPublication.getPublicationTitle() : ""));
        TextField shortTitleField = new TextField(selectedPublication == null ? "" : (selectedPublication.getShortTitle() != null ? selectedPublication.getShortTitle() : ""));
        TextField isbnField = new TextField(selectedPublication == null ? "" : (selectedPublication.getIsbn() != null ? selectedPublication.getIsbn() : ""));
        TextField issnField = new TextField(selectedPublication == null ? "" : (selectedPublication.getIssn() != null ? selectedPublication.getIssn() : ""));
        TextField doiField = new TextField(selectedPublication == null ? "" : (selectedPublication.getDoi() != null ? selectedPublication.getDoi() : ""));
        TextField urlField = new TextField(selectedPublication == null ? "" : (selectedPublication.getUrl() != null ? selectedPublication.getUrl() : ""));
        TextField abstractNoteField = new TextField(selectedPublication == null ? "" : (selectedPublication.getAbstractNote() != null ? selectedPublication.getAbstractNote() : ""));
        TextField dateField = new TextField(selectedPublication == null ? "" : (selectedPublication.getDate() != null ? selectedPublication.getDate() : ""));
        TextField dateAddedField = new TextField(selectedPublication == null ? "" : (selectedPublication.getDateAdded() != null ? selectedPublication.getDateAdded() : ""));
        TextField dateModifiedField = new TextField(selectedPublication == null ? "" : (selectedPublication.getDateModified() != null ? selectedPublication.getDateModified() : ""));
        TextField accessDateField = new TextField(selectedPublication == null ? "" : (selectedPublication.getAccessDate() != null ? selectedPublication.getAccessDate() : ""));
        TextField pagesField = new TextField(selectedPublication == null ? "" : (selectedPublication.getPages() != null ? selectedPublication.getPages() : ""));
        TextField numPagesField = new TextField(selectedPublication == null ? "" : (selectedPublication.getNumPages() != null ? selectedPublication.getNumPages() : ""));
        TextField issueField = new TextField(selectedPublication == null ? "" : (selectedPublication.getIssue() != null ? selectedPublication.getIssue() : ""));
        TextField volumeField = new TextField(selectedPublication == null ? "" : (selectedPublication.getVolume() != null ? selectedPublication.getVolume() : ""));
        TextField numberOfVolumesField = new TextField(selectedPublication == null ? "" : (selectedPublication.getNumberOfVolumes() != null ? selectedPublication.getNumberOfVolumes() : ""));
        TextField journalAbbreviationField = new TextField(selectedPublication == null ? "" : (selectedPublication.getJournalAbbreviation() != null ? selectedPublication.getJournalAbbreviation() : ""));
        TextField seriesField = new TextField(selectedPublication == null ? "" : (selectedPublication.getSeries() != null ? selectedPublication.getSeries() : ""));
        TextField seriesNumberField = new TextField(selectedPublication == null ? "" : (selectedPublication.getSeriesNumber() != null ? selectedPublication.getSeriesNumber() : ""));
        TextField seriesTitleField = new TextField(selectedPublication == null ? "" : (selectedPublication.getSeriesTitle() != null ? selectedPublication.getSeriesTitle() : ""));
        TextField placeField = new TextField(selectedPublication == null ? "" : (selectedPublication.getPlace() != null ? selectedPublication.getPlace() : ""));
        TextField languageField = new TextField(selectedPublication == null ? "" : (selectedPublication.getLanguage() != null ? selectedPublication.getLanguage() : ""));
        TextField rightsField = new TextField(selectedPublication == null ? "" : (selectedPublication.getRights() != null ? selectedPublication.getRights() : ""));
        TextField typeField = new TextField(selectedPublication == null ? "" : (selectedPublication.getType() != null ? selectedPublication.getType() : ""));
        TextField archiveField = new TextField(selectedPublication == null ? "" : (selectedPublication.getArchive() != null ? selectedPublication.getArchive() : ""));
        TextField archiveLocationField = new TextField(selectedPublication == null ? "" : (selectedPublication.getArchiveLocation() != null ? selectedPublication.getArchiveLocation() : ""));
        TextField libraryCatalogField = new TextField(selectedPublication == null ? "" : (selectedPublication.getLibraryCatalog() != null ? selectedPublication.getLibraryCatalog() : ""));
        TextField callNumberField = new TextField(selectedPublication == null ? "" : (selectedPublication.getCallNumber() != null ? selectedPublication.getCallNumber() : ""));
        TextField extraField = new TextField(selectedPublication == null ? "" : (selectedPublication.getExtra() != null ? selectedPublication.getExtra() : ""));
        TextField editorField = new TextField(selectedPublication == null ? "" : (selectedPublication.getEditor() != null ? selectedPublication.getEditor() : ""));
        TextField seriesEditorField = new TextField(selectedPublication == null ? "" : (selectedPublication.getSeriesEditor() != null ? selectedPublication.getSeriesEditor() : ""));
        TextField translatorField = new TextField(selectedPublication == null ? "" : (selectedPublication.getTranslator() != null ? selectedPublication.getTranslator() : ""));
        TextField contributorField = new TextField(selectedPublication == null ? "" : (selectedPublication.getContributor() != null ? selectedPublication.getContributor() : ""));
        TextField numberField = new TextField(selectedPublication == null ? "" : (selectedPublication.getNumber() != null ? selectedPublication.getNumber() : ""));
        TextField editionField = new TextField(selectedPublication == null ? "" : (selectedPublication.getEdition() != null ? selectedPublication.getEdition() : ""));

        // Set editable state
        java.util.List<TextField> fields = java.util.Arrays.asList(
            keyField, itemTypeField, titleField, authorField, yearField, publisherField, publicationTitleField, shortTitleField,
            isbnField, issnField, doiField, urlField, abstractNoteField, dateField, dateAddedField, dateModifiedField,
            accessDateField, pagesField, numPagesField, issueField, volumeField, numberOfVolumesField, journalAbbreviationField,
            seriesField, seriesNumberField, seriesTitleField, placeField, languageField, rightsField, typeField,
            archiveField, archiveLocationField, libraryCatalogField, callNumberField, extraField, editorField,
            seriesEditorField, translatorField, contributorField, numberField, editionField
        );
        fields.forEach(f -> f.setEditable(isEditMode));

        ScrollPane scrollPane = new ScrollPane();
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // Add all fields in organized groups
        content.getChildren().addAll(
            new Label("Key:"), keyField,
            new Label("Item Type:"), itemTypeField,
            new Label("Title:"), titleField,
            new Label("Author:"), authorField,
            new Label("Publication Year:"), yearField,
            new Label("Publisher:"), publisherField,
            new Label("Publication Title:"), publicationTitleField,
            new Label("Short Title:"), shortTitleField,
            new Label("ISBN:"), isbnField,
            new Label("ISSN:"), issnField,
            new Label("DOI:"), doiField,
            new Label("URL:"), urlField,
            new Label("Abstract Note:"), abstractNoteField,
            new Label("Date:"), dateField,
            new Label("Date Added:"), dateAddedField,
            new Label("Date Modified:"), dateModifiedField,
            new Label("Access Date:"), accessDateField,
            new Label("Pages:"), pagesField,
            new Label("Num Pages:"), numPagesField,
            new Label("Issue:"), issueField,
            new Label("Volume:"), volumeField,
            new Label("Number of Volumes:"), numberOfVolumesField,
            new Label("Journal Abbreviation:"), journalAbbreviationField,
            new Label("Series:"), seriesField,
            new Label("Series Number:"), seriesNumberField,
            new Label("Series Title:"), seriesTitleField,
            new Label("Place:"), placeField,
            new Label("Language:"), languageField,
            new Label("Rights:"), rightsField,
            new Label("Type:"), typeField,
            new Label("Archive:"), archiveField,
            new Label("Archive Location:"), archiveLocationField,
            new Label("Library Catalog:"), libraryCatalogField,
            new Label("Call Number:"), callNumberField,
            new Label("Extra:"), extraField,
            new Label("Editor:"), editorField,
            new Label("Series Editor:"), seriesEditorField,
            new Label("Translator:"), translatorField,
            new Label("Contributor:"), contributorField,
            new Label("Number:"), numberField,
            new Label("Edition:"), editionField
        );

        scrollPane.setContent(content);
        detailView.getChildren().add(scrollPane);

        HBox buttonBox = new HBox(10);
        if (isEditMode) {
            Button saveBtn = new Button("Save");
            saveBtn.setOnAction(e -> savePublication(keyField, itemTypeField, titleField, authorField, yearField, publisherField,
                publicationTitleField, shortTitleField, isbnField, issnField, doiField, urlField, abstractNoteField, dateField,
                dateAddedField, dateModifiedField, accessDateField, pagesField, numPagesField, issueField, volumeField,
                numberOfVolumesField, journalAbbreviationField, seriesField, seriesNumberField, seriesTitleField, placeField,
                languageField, rightsField, typeField, archiveField, archiveLocationField, libraryCatalogField, callNumberField,
                extraField, editorField, seriesEditorField, translatorField, contributorField, numberField, editionField));
            Button cancelBtn = new Button("Cancel");
            cancelBtn.setOnAction(e -> {
                isEditMode = false;
                refreshDetailView();
            });
            buttonBox.getChildren().addAll(saveBtn, cancelBtn);
        } else {
            Button editBtn = new Button("Edit");
            editBtn.setOnAction(e -> {
                isEditMode = true;
                refreshDetailView();
            });
            buttonBox.getChildren().add(editBtn);
        }

        detailView.getChildren().addAll(new Separator(), buttonBox);
    }

    private void savePublication(TextField keyField, TextField itemTypeField, TextField titleField, TextField authorField,
            TextField yearField, TextField publisherField, TextField publicationTitleField, TextField shortTitleField,
            TextField isbnField, TextField issnField, TextField doiField, TextField urlField, TextField abstractNoteField,
            TextField dateField, TextField dateAddedField, TextField dateModifiedField, TextField accessDateField,
            TextField pagesField, TextField numPagesField, TextField issueField, TextField volumeField,
            TextField numberOfVolumesField, TextField journalAbbreviationField, TextField seriesField,
            TextField seriesNumberField, TextField seriesTitleField, TextField placeField, TextField languageField,
            TextField rightsField, TextField typeField, TextField archiveField, TextField archiveLocationField,
            TextField libraryCatalogField, TextField callNumberField, TextField extraField, TextField editorField,
            TextField seriesEditorField, TextField translatorField, TextField contributorField, TextField numberField,
            TextField editionField) {

        Publication result = selectedPublication == null ? new Publication() : selectedPublication;
        result.setKey(keyField.getText());
        result.setItemType(itemTypeField.getText());
        result.setTitle(titleField.getText());
        result.setAuthor(authorField.getText());
        result.setPublicationYear(yearField.getText());
        result.setPublisher(publisherField.getText());
        result.setPublicationTitle(publicationTitleField.getText());
        result.setShortTitle(shortTitleField.getText());
        result.setIsbn(isbnField.getText());
        result.setIssn(issnField.getText());
        result.setDoi(doiField.getText());
        result.setUrl(urlField.getText());
        result.setAbstractNote(abstractNoteField.getText());
        result.setDate(dateField.getText());
        result.setDateAdded(dateAddedField.getText());
        result.setDateModified(dateModifiedField.getText());
        result.setAccessDate(accessDateField.getText());
        result.setPages(pagesField.getText());
        result.setNumPages(numPagesField.getText());
        result.setIssue(issueField.getText());
        result.setVolume(volumeField.getText());
        result.setNumberOfVolumes(numberOfVolumesField.getText());
        result.setJournalAbbreviation(journalAbbreviationField.getText());
        result.setSeries(seriesField.getText());
        result.setSeriesNumber(seriesNumberField.getText());
        result.setSeriesTitle(seriesTitleField.getText());
        result.setPlace(placeField.getText());
        result.setLanguage(languageField.getText());
        result.setRights(rightsField.getText());
        result.setType(typeField.getText());
        result.setArchive(archiveField.getText());
        result.setArchiveLocation(archiveLocationField.getText());
        result.setLibraryCatalog(libraryCatalogField.getText());
        result.setCallNumber(callNumberField.getText());
        result.setExtra(extraField.getText());
        result.setEditor(editorField.getText());
        result.setSeriesEditor(seriesEditorField.getText());
        result.setTranslator(translatorField.getText());
        result.setContributor(contributorField.getText());
        result.setNumber(numberField.getText());
        result.setEdition(editionField.getText());

        if (selectedPublication == null) {
            controller.create(result);
        } else {
            controller.update(result.getId(), result);
        }
        refreshList();
        isEditMode = false;
        refreshDetailView();
    }
}
