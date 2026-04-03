package eu.askadev.magic.view;

import eu.askadev.magic.controller.ConceptController;
import eu.askadev.magic.controller.KeywordController;
import eu.askadev.magic.model.Concept;
import eu.askadev.magic.model.Keyword;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ConceptListView {

    private final ConceptController controller;
    private final KeywordController keywordController;
    private TableView<Concept> tableView;
    private VBox detailView;
    private Concept selectedConcept;
    private boolean isEditMode = false;

    public ConceptListView(ConceptController controller, KeywordController keywordController) {
        this.controller = controller;
        this.keywordController = keywordController;
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

        Label title = new Label("Concepts");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        TableColumn<Concept, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getValue()));
        TableColumn<Concept, String> keywordsCol = new TableColumn<>("Keywords");
        keywordsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getKeywords().stream().map(Keyword::getValue).collect(Collectors.joining(", "))));
        tableView.getColumns().addAll(nameCol, keywordsCol);
        refreshList();

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedConcept = newVal;
            isEditMode = false;
            refreshDetailView();
        });

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && selectedConcept != null) {
                isEditMode = true;
                refreshDetailView();
            }
        });

        Button addBtn = new Button("Add New");
        addBtn.setOnAction(e -> {
            selectedConcept = null;
            isEditMode = true;
            refreshDetailView();
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            if (selectedConcept != null) {
                controller.delete(selectedConcept.getId());
                selectedConcept = null;
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

        if (selectedConcept == null && !isEditMode) {
            detailView.getChildren().add(new Label("Select a concept or click Add New"));
            return;
        }

        TextField valueField = new TextField(selectedConcept == null ? "" : selectedConcept.getValue());
        valueField.setEditable(isEditMode);

        List<Keyword> allKeywords = keywordController.findAll();
        Set<String> selectedIds = new HashSet<>(selectedConcept == null ? Set.of() :
            selectedConcept.getKeywords().stream().map(Keyword::getId).collect(Collectors.toSet()));

        ListView<Keyword> keywordsListView = new ListView<>(FXCollections.observableArrayList(allKeywords));
        keywordsListView.setCellFactory(lv -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();
            {
                setGraphic(checkBox);
                checkBox.setDisable(!isEditMode);
                checkBox.setOnAction(e -> {
                    if (getItem() != null) {
                        if (checkBox.isSelected()) selectedIds.add(getItem().getId());
                        else selectedIds.remove(getItem().getId());
                    }
                });
            }
            @Override
            protected void updateItem(Keyword item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); checkBox.setVisible(false); }
                else { setText(item.getValue()); checkBox.setVisible(true); checkBox.setSelected(selectedIds.contains(item.getId())); }
            }
        });
        keywordsListView.setPrefHeight(120);

        VBox content = new VBox(10,
            new Label("Value:"), valueField,
            new Label("Keywords:"), keywordsListView);
        content.setPadding(new Insets(15));
        detailView.getChildren().add(new ScrollPane(content));

        HBox buttonBox = new HBox(10);
        if (isEditMode) {
            Button saveBtn = new Button("Save");
            saveBtn.setOnAction(e -> saveConcept(valueField, keywordsListView, selectedIds));
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

    private void saveConcept(TextField valueField, ListView<Keyword> keywordsListView, Set<String> selectedIds) {
        Concept result = selectedConcept == null ? new Concept() : selectedConcept;
        result.setValue(valueField.getText());
        List<Keyword> allKeywords = keywordController.findAll();
        result.setKeywords(allKeywords.stream()
            .filter(k -> selectedIds.contains(k.getId()))
            .collect(Collectors.toSet()));

        if (selectedConcept == null) {
            controller.create(result);
        } else {
            controller.update(result.getId(), result);
        }
        refreshList();
        isEditMode = false;
        refreshDetailView();
    }
}
