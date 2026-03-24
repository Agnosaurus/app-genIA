package eu.askadev.magic.view;

import eu.askadev.magic.controller.KeywordController;
import eu.askadev.magic.model.Keyword;
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
public class KeywordListView {

    private final KeywordController controller;
    private TableView<Keyword> tableView;
    private VBox detailView;
    private Keyword selectedKeyword;
    private boolean isEditMode = false;

    public KeywordListView(KeywordController controller) {
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
        splitPane.setDividerPosition(0, 0.4);

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

        Label title = new Label("🏷️ Keywords");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0078d4;");

        tableView = new TableView<>();
        TableColumn<Keyword, String> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getValue()));
        tableView.getColumns().add(valueCol);
        refreshList();

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedKeyword = newVal;
            isEditMode = false;
            refreshDetailView();
        });

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && selectedKeyword != null) {
                isEditMode = true;
                refreshDetailView();
            }
        });

        Button addBtn = new Button("Add New");
        addBtn.setOnAction(e -> {
            selectedKeyword = null;
            isEditMode = true;
            refreshDetailView();
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            if (selectedKeyword != null) {
                controller.delete(selectedKeyword.getId());
                refreshList();
                selectedKeyword = null;
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

        if (selectedKeyword == null && !isEditMode) {
            detailView.getChildren().add(new Label("Select a keyword or click Add New"));
            return;
        }

        TextField valueField = new TextField(selectedKeyword == null ? "" : selectedKeyword.getValue());
        valueField.setEditable(isEditMode);

        ScrollPane scrollPane = new ScrollPane();
        VBox content = new VBox(10,
                new Label("Value:"), valueField);
        content.setPadding(new Insets(10));
        scrollPane.setContent(content);
        detailView.getChildren().add(scrollPane);

        HBox buttonBox = new HBox(10);
        if (isEditMode) {
            Button saveBtn = new Button("Save");
            saveBtn.setOnAction(e -> saveKeyword(valueField));
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

    private void saveKeyword(TextField valueField) {
        Keyword result = selectedKeyword == null ? new Keyword() : selectedKeyword;
        result.setValue(valueField.getText());

        if (selectedKeyword == null) {
            controller.create(result);
        } else {
            controller.update(result.getId(), result);
        }
        refreshList();
        isEditMode = false;
        refreshDetailView();
    }
}
