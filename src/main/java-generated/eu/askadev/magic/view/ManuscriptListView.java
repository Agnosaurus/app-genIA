package eu.askadev.magic.view;

import eu.askadev.magic.controller.ManuscriptController;
import eu.askadev.magic.controller.LanguageController;
import eu.askadev.magic.controller.AuthorController;
import eu.askadev.magic.model.Keyword;
import eu.askadev.magic.model.Manuscript;
import eu.askadev.magic.model.Language;
import eu.askadev.magic.model.Author;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

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

        Label title = new Label("Manuscripts");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        TableColumn<Manuscript, String> uniqueIdCol = new TableColumn<>("Unique ID");
        uniqueIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUniqueId()));
        TableColumn<Manuscript, String> titreCol = new TableColumn<>("Titre");
        titreCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitre()));
        TableColumn<Manuscript, String> langCol = new TableColumn<>("Language");
        langCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getLanguage() != null ? data.getValue().getLanguage().getName() : ""));
        TableColumn<Manuscript, String> refCol = new TableColumn<>("Reference");
        refCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getReferenceManuscript() != null ? data.getValue().getReferenceManuscript() : ""));

        tableView.getColumns().addAll(uniqueIdCol, titreCol, langCol, refCol);
        refreshList();

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedManuscript = newVal;
            isEditMode = false;
            refreshDetailView();
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
                refreshList();
                selectedManuscript = null;
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

        if (selectedManuscript == null && !isEditMode) {
            detailView.getChildren().add(new Label("Select a manuscript or click Add New"));
            return;
        }

        TextField uniqueIdField = new TextField(selectedManuscript == null ? "" : selectedManuscript.getUniqueId());
        TextField titreField = new TextField(selectedManuscript == null ? "" : selectedManuscript.getTitre());
        TextField refField = new TextField(selectedManuscript == null ? "" : (selectedManuscript.getReferenceManuscript() != null ? selectedManuscript.getReferenceManuscript() : ""));
        TextField lieuField = new TextField(selectedManuscript == null ? "" : selectedManuscript.getLieu());
        TextField dateField = new TextField(selectedManuscript == null ? "" : selectedManuscript.getDate());
        TextField scriptoriumField = new TextField(selectedManuscript == null ? "" : selectedManuscript.getScriptorium());

        uniqueIdField.setEditable(isEditMode);
        titreField.setEditable(isEditMode);
        refField.setEditable(isEditMode);
        lieuField.setEditable(isEditMode);
        dateField.setEditable(isEditMode);
        scriptoriumField.setEditable(isEditMode);

        ComboBox<Language> languageCombo = new ComboBox<>();
        languageCombo.setItems(FXCollections.observableArrayList(languageController.findAll()));
        languageCombo.setCellFactory(lv -> new ListCell<Language>() {
            @Override
            protected void updateItem(Language item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });
        languageCombo.setButtonCell(new ListCell<Language>() {
            @Override
            protected void updateItem(Language item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });
        languageCombo.setDisable(!isEditMode);
        if (selectedManuscript != null && selectedManuscript.getLanguage() != null) {
            languageCombo.setValue(selectedManuscript.getLanguage());
        }

        // Authors multi-select ComboBox with filter
        VBox authorsComboWrapper = createMultiSelectComboWithFilter("Authors", authorController.findAll(),
            selectedManuscript == null ? null : selectedManuscript.getAuthors(), isEditMode);
        // Extract the combo from wrapper (it's the second child: filter field is first, combo is second)
        ComboBox<Object> authorsCombo = (ComboBox<Object>) authorsComboWrapper.getChildren().get(1);

        ScrollPane scrollPane = new ScrollPane();
        VBox content = new VBox(10,
                new Label("Unique ID:"), uniqueIdField,
                new Label("Titre:"), titreField,
                new Label("Language:"), languageCombo,
                new Label("Reference:"), refField,
                new Label("Lieu:"), lieuField,
                new Label("Date:"), dateField,
                new Label("Scriptorium:"), scriptoriumField,
                new Label("Authors:"), authorsComboWrapper);
        content.setPadding(new Insets(10));
        scrollPane.setContent(content);
        detailView.getChildren().add(scrollPane);

        HBox buttonBox = new HBox(10);
        if (isEditMode) {
            Button saveBtn = new Button("Save");
            saveBtn.setOnAction(e -> saveManuscript(uniqueIdField, titreField, refField, lieuField, dateField, scriptoriumField, languageCombo, authorsCombo));
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

    private ComboBox<Object> createMultiSelectCombo(String label, java.util.List<?> allItems, java.util.Set<?> selectedItems, boolean editable) {
        ComboBox<Object> comboBox = new ComboBox<>();
        comboBox.setDisable(!editable);
        comboBox.setPrefHeight(25);
        comboBox.setPrefWidth(300);

        java.util.Set<Object> selections = new java.util.HashSet<>(selectedItems != null ? selectedItems : java.util.Collections.emptySet());
        FilteredList<Object> filteredList = new FilteredList<>(FXCollections.observableArrayList(allItems));
        comboBox.setItems(filteredList);

        comboBox.setCellFactory(lv -> {
            VBox cellBox = new VBox();
            ListCell<Object> cell = new ListCell<Object>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText((selections.contains(item) ? "✓ " : "") + item.toString());
                    }

                    setOnMouseClicked(event -> {
                        if (!empty && item != null) {
                            if (selections.contains(item)) {
                                selections.remove(item);
                            } else {
                                selections.add(item);
                            }
                            comboBox.hide();
                            updateItem(item, false);
                            event.consume();
                        }
                    });
                }
            };
            cell.setPrefHeight(25);
            return cell;
        });

        comboBox.setButtonCell(new ListCell<Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                setText(label + " (" + selections.size() + ")");
            }
        });

        comboBox.setOnShowing(e -> {
            if (comboBox.isShowing()) {
                // Set max height for dropdown to limit space
                comboBox.getStyleClass().add("limited-height-combo");
            }
        });

        comboBox.setUserData(selections);
        return comboBox;
    }

    private VBox createMultiSelectComboWithFilter(String label, java.util.List<?> allItems, java.util.Set<?> selectedItems, boolean editable) {
        ComboBox<Object> comboBox = createMultiSelectCombo(label, allItems, selectedItems, editable);

        TextField filterField = new TextField();
        filterField.setPromptText("Filter " + label + "...");
        filterField.setEditable(editable);
        filterField.setPrefWidth(300);

        // Get the filtered list from the combo
        FilteredList<Object> filteredList = (FilteredList<Object>) comboBox.getItems();

        // Update filter predicate when text changes
        filterField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                filteredList.setPredicate(item -> true);
            } else {
                String filterText = newVal.toLowerCase();
                filteredList.setPredicate(item -> item != null && item.toString().toLowerCase().contains(filterText));
            }
        });

        VBox wrapper = new VBox(5, filterField, comboBox);
        wrapper.setPrefWidth(300);
        return wrapper;
    }

    private void saveManuscript(TextField uniqueIdField, TextField titreField, TextField refField, TextField lieuField, TextField dateField, TextField scriptoriumField, ComboBox<Language> languageCombo, ComboBox<Object> authorsCombo) {
        Manuscript result = selectedManuscript == null ? new Manuscript() : selectedManuscript;
        result.setUniqueId(uniqueIdField.getText());
        result.setTitre(titreField.getText());
        result.setReferenceManuscript(refField.getText());
        result.setLieu(lieuField.getText());
        result.setDate(dateField.getText());
        result.setScriptorium(scriptoriumField.getText());
        result.setLanguage(languageCombo.getValue());

        @SuppressWarnings("unchecked")
        java.util.Set<Object> selectedAuthors = (java.util.Set<Object>) authorsCombo.getUserData();

        // Initialize authors set if null
        if (result.getAuthors() == null) {
            result.setAuthors(new java.util.HashSet<>());
        }

        result.getAuthors().clear();
        result.getAuthors().addAll(selectedAuthors.stream()
            .map(item -> (Author) item)
            .collect(java.util.stream.Collectors.toSet()));

        if (selectedManuscript == null) {
            controller.create(result);
        } else {
            controller.update(result.getId(), result);
        }
        refreshList();
        isEditMode = false;
        refreshDetailView();
    }
}
