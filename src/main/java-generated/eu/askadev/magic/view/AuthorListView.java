package eu.askadev.magic.view;

import eu.askadev.magic.controller.AuthorController;
import eu.askadev.magic.model.Author;
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
public class AuthorListView {

    private final AuthorController controller;
    private TableView<Author> tableView;
    private VBox detailView;
    private Author selectedAuthor;
    private boolean isEditMode = false;

    public AuthorListView(AuthorController controller) {
        this.controller = controller;
    }

    public Parent getView(LandingView landingView) {
        SplitPane splitPane = new SplitPane();
        splitPane.setPrefWidth(800);
        splitPane.setPrefHeight(600);

        // List view
        VBox listBox = createListView();

        // Detail view
        detailView = new VBox(10);
        detailView.setPadding(new Insets(20));
        refreshDetailView();

        splitPane.getItems().addAll(listBox, detailView);
        splitPane.setDividerPosition(0, 0.4);

        // Bottom buttons
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

        Label title = new Label("Authors");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        TableColumn<Author, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));
        TableColumn<Author, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));
        tableView.getColumns().addAll(firstNameCol, lastNameCol);
        refreshList();

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedAuthor = newVal;
            isEditMode = false;
            refreshDetailView();
        });

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && selectedAuthor != null) {
                isEditMode = true;
                refreshDetailView();
            }
        });

        Button addBtn = new Button("Add New");
        addBtn.setOnAction(e -> {
            selectedAuthor = null;
            isEditMode = true;
            refreshDetailView();
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            if (selectedAuthor != null) {
                controller.delete(selectedAuthor.getId());
                refreshList();
                selectedAuthor = null;
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

        if (selectedAuthor == null && !isEditMode) {
            detailView.getChildren().add(new Label("Select an author or click Add New"));
            return;
        }

        TextField firstNameField = new TextField(selectedAuthor == null ? "" : selectedAuthor.getFirstName());
        TextField lastNameField = new TextField(selectedAuthor == null ? "" : selectedAuthor.getLastName());
        TextField birthDateField = new TextField(selectedAuthor == null ? "" : selectedAuthor.getBirthDate());

        firstNameField.setEditable(isEditMode);
        lastNameField.setEditable(isEditMode);
        birthDateField.setEditable(isEditMode);

        VBox content = new VBox(10,
                new Label("First Name:"), firstNameField,
                new Label("Last Name:"), lastNameField,
                new Label("Birth Date:"), birthDateField);

        HBox buttonBox = new HBox(10);
        if (isEditMode) {
            Button saveBtn = new Button("Save");
            saveBtn.setOnAction(e -> {
                Author result = selectedAuthor == null ? new Author() : selectedAuthor;
                result.setFirstName(firstNameField.getText());
                result.setLastName(lastNameField.getText());
                result.setBirthDate(birthDateField.getText());

                if (selectedAuthor == null) {
                    controller.create(result);
                } else {
                    controller.update(result.getId(), result);
                }
                refreshList();
                isEditMode = false;
                refreshDetailView();
            });

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

        detailView.getChildren().addAll(content, new Separator(), buttonBox);
    }
}
