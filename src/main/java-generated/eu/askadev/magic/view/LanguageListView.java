package eu.askadev.magic.view;

import eu.askadev.magic.controller.LanguageController;
import eu.askadev.magic.model.Language;
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
public class LanguageListView {

    private final LanguageController controller;
    private ListView<Language> listView;
    private VBox detailView;
    private Language selectedLanguage;
    private boolean isEditMode = false;

    public LanguageListView(LanguageController controller) {
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

        Label title = new Label("🌐 Languages");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0078d4;");

        listView = new ListView<>();
        listView.setCellFactory(lv -> new ListCell<Language>() {
            @Override
            protected void updateItem(Language item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        refreshList();

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedLanguage = newVal;
            isEditMode = false;
            refreshDetailView();
        });

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && selectedLanguage != null) {
                isEditMode = true;
                refreshDetailView();
            }
        });

        Button addBtn = new Button("Add New");
        addBtn.setOnAction(e -> {
            selectedLanguage = null;
            isEditMode = true;
            refreshDetailView();
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            if (selectedLanguage != null) {
                controller.delete(selectedLanguage.getId());
                refreshList();
                selectedLanguage = null;
                refreshDetailView();
            }
        });

        HBox buttons = new HBox(10, addBtn, deleteBtn);
        listBox.getChildren().addAll(title, listView, buttons);
        VBox.setVgrow(listView, Priority.ALWAYS);
        return listBox;
    }

    private void refreshList() {
        listView.setItems(FXCollections.observableArrayList(controller.findAll()));
    }

    private void refreshDetailView() {
        detailView.getChildren().clear();

        if (selectedLanguage == null && !isEditMode) {
            detailView.getChildren().add(new Label("Select a language or click Add New"));
            return;
        }

        TextField nameField = new TextField(selectedLanguage == null ? "" : selectedLanguage.getName());
        nameField.setEditable(isEditMode);

        ScrollPane scrollPane = new ScrollPane();
        VBox content = new VBox(10,
                new Label("Name:"), nameField);
        content.setPadding(new Insets(10));
        scrollPane.setContent(content);
        detailView.getChildren().add(scrollPane);

        HBox buttonBox = new HBox(10);
        if (isEditMode) {
            Button saveBtn = new Button("Save");
            saveBtn.setOnAction(e -> saveLanguage(nameField));
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

    private void saveLanguage(TextField nameField) {
        Language result = selectedLanguage == null ? new Language() : selectedLanguage;
        result.setName(nameField.getText());

        if (selectedLanguage == null) {
            controller.create(result);
        } else {
            controller.update(result.getId(), result);
        }
        refreshList();
        isEditMode = false;
        refreshDetailView();
    }
}
