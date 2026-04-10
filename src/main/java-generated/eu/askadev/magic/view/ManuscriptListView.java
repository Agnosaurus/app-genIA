package eu.askadev.magic.view;

import eu.askadev.magic.controller.ManuscriptController;
import eu.askadev.magic.controller.LanguageController;
import eu.askadev.magic.controller.AuthorController;
import eu.askadev.magic.model.Manuscript;
import eu.askadev.magic.model.Language;
import eu.askadev.magic.model.Author;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ManuscriptListView {

    private final ManuscriptController controller;
    private final LanguageController languageController;
    private final AuthorController authorController;
    private TableView<Manuscript> tableView;
    private VBox detailView;
    private Manuscript selectedManuscript;
    private boolean isEditMode = false;

    public ManuscriptListView(ManuscriptController controller, LanguageController languageController, AuthorController authorController) {
        this.controller = controller;
        this.languageController = languageController;
        this.authorController = authorController;
    }

    public Parent getView(LandingView landingView) {
        SplitPane splitPane = new SplitPane();
        splitPane.setPrefWidth(800);
        splitPane.setPrefHeight(600);

        VBox listBox = createListBox();
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

    private VBox createListBox() {
        VBox listBox = new VBox(10);
        listBox.setPadding(new Insets(10));

        Label title = new Label("Manuscripts");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        TableColumn<Manuscript, String> uniqueIdCol = new TableColumn<>("Unique ID");
        uniqueIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUniqueId()));
        TableColumn<Manuscript, String> titreCol = new TableColumn<>("Titre");
        titreCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitre()));
        tableView.getColumns().addAll(uniqueIdCol, titreCol);
        refreshList();

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedManuscript = newVal;
            isEditMode = false;
            refreshDetailView();
        });

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && selectedManuscript != null) {
                isEditMode = true;
                refreshDetailView();
            }
        });

        Button addBtn = new Button("Add New");
        addBtn.setOnAction(e -> {
            selectedManuscript = null;
            isEditMode = true;
            refreshDetailView();
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            if (selectedManuscript != null) {
                controller.delete(selectedManuscript.getId());
                selectedManuscript = null;
                refreshList();
                refreshDetailView();
            }
        });

        listBox.getChildren().addAll(title, tableView, new HBox(10, addBtn, deleteBtn));
        VBox.setVgrow(tableView, Priority.ALWAYS);
        return listBox;
    }

    private void refreshList() {
        tableView.setItems(FXCollections.observableArrayList(controller.findAll()));
    }

    private void refreshDetailView() {
        detailView.getChildren().clear();

        if (selectedManuscript == null && !isEditMode) {
            detailView.getChildren().add(new Label("Select a manuscript or click Add New"));
            return;
        }

        TextField uniqueIdField = new TextField(selectedManuscript == null ? "" : selectedManuscript.getUniqueId());
        TextField titreField = new TextField(selectedManuscript == null ? "" : selectedManuscript.getTitre());
        TextField refField = new TextField(selectedManuscript == null ? "" : nvl(selectedManuscript.getReferenceManuscript()));
        TextField lieuField = new TextField(selectedManuscript == null ? "" : nvl(selectedManuscript.getLieu()));
        TextField dateField = new TextField(selectedManuscript == null ? "" : nvl(selectedManuscript.getDate()));
        TextField scriptoriumField = new TextField(selectedManuscript == null ? "" : nvl(selectedManuscript.getScriptorium()));

        uniqueIdField.setEditable(isEditMode);
        titreField.setEditable(isEditMode);
        refField.setEditable(isEditMode);
        lieuField.setEditable(isEditMode);
        dateField.setEditable(isEditMode);
        scriptoriumField.setEditable(isEditMode);

        List<Language> allLanguages = languageController.findAll();
        ComboBox<Language> languageCombo = new ComboBox<>(FXCollections.observableArrayList(allLanguages));
        languageCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Language item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });
        languageCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Language item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });
        languageCombo.setDisable(!isEditMode);
        if (selectedManuscript != null && selectedManuscript.getLanguage() != null) {
            String langId = selectedManuscript.getLanguage().getId();
            allLanguages.stream().filter(l -> l.getId().equals(langId)).findFirst()
                .ifPresent(languageCombo::setValue);
        }

        List<Author> allAuthors = authorController.findAll();
        Set<String> initialAuthorIds = selectedManuscript == null || selectedManuscript.getAuthors() == null ? Set.of() :
            selectedManuscript.getAuthors().stream().map(Author::getId).collect(Collectors.toSet());
        FilterableMultiSelectBox<Author> authorsBox = new FilterableMultiSelectBox<>(
            allAuthors, initialAuthorIds,
            a -> a.getFirstName() + " " + a.getLastName(),
            Author::getId, isEditMode);

        VBox content = new VBox(10,
            new Label("Unique ID:"), uniqueIdField,
            new Label("Titre:"), titreField,
            new Label("Reference:"), refField,
            new Label("Lieu:"), lieuField,
            new Label("Date:"), dateField,
            new Label("Scriptorium:"), scriptoriumField,
            new Label("Language:"), languageCombo,
            new Label("Authors:"), authorsBox);
        content.setPadding(new Insets(15));
        detailView.getChildren().add(new ScrollPane(content));

        HBox buttonBox = new HBox(10);
        if (isEditMode) {
            Button saveBtn = new Button("Save");
            saveBtn.setOnAction(e -> saveManuscript(uniqueIdField, titreField, refField, lieuField, dateField, scriptoriumField, languageCombo, allAuthors, authorsBox.getSelectedIds()));
            Button cancelBtn = new Button("Cancel");
            cancelBtn.setOnAction(e -> { isEditMode = false; refreshDetailView(); });
            buttonBox.getChildren().addAll(saveBtn, cancelBtn);
        } else {
            Button editBtn = new Button("Edit");
            editBtn.setOnAction(e -> { isEditMode = true; refreshDetailView(); });
            buttonBox.getChildren().add(editBtn);
        }
        detailView.getChildren().addAll(new Separator(), buttonBox);
    }

    private void saveManuscript(TextField uniqueIdField, TextField titreField, TextField refField,
                                TextField lieuField, TextField dateField, TextField scriptoriumField,
                                ComboBox<Language> languageCombo, List<Author> allAuthors, Set<String> selectedAuthorIds) {
        Manuscript result = selectedManuscript == null ? new Manuscript() : selectedManuscript;
        result.setUniqueId(uniqueIdField.getText());
        result.setTitre(titreField.getText());
        result.setReferenceManuscript(refField.getText());
        result.setLieu(lieuField.getText());
        result.setDate(dateField.getText());
        result.setScriptorium(scriptoriumField.getText());
        result.setLanguage(languageCombo.getValue());
        result.setAuthors(allAuthors.stream()
            .filter(a -> selectedAuthorIds.contains(a.getId()))
            .collect(Collectors.toSet()));

        if (selectedManuscript == null) {
            controller.create(result);
        } else {
            controller.update(result.getId(), result);
        }
        refreshList();
        isEditMode = false;
        refreshDetailView();
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }
}
