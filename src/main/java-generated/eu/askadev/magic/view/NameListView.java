package eu.askadev.magic.view;

import eu.askadev.magic.controller.NameController;
import eu.askadev.magic.controller.LanguageController;
import eu.askadev.magic.model.Name;
import eu.askadev.magic.model.Language;
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
public class NameListView {

    private final NameController controller;
    private final LanguageController languageController;
    private ListView<Name> listView;
    private VBox detailView;
    private Name selectedName;
    private boolean isEditMode = false;

    public NameListView(NameController controller, LanguageController languageController) {
        this.controller = controller;
        this.languageController = languageController;
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

        Label title = new Label("Names");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        listView = new ListView<>();
        listView.setCellFactory(lv -> new ListCell<Name>() {
            @Override
            protected void updateItem(Name item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        refreshList();

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedName = newVal;
            isEditMode = false;
            refreshDetailView();
        });

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && selectedName != null) {
                isEditMode = true;
                refreshDetailView();
            }
        });

        Button addBtn = new Button("Add New");
        addBtn.setOnAction(e -> {
            selectedName = null;
            isEditMode = true;
            refreshDetailView();
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            if (selectedName != null) {
                controller.delete(selectedName.getId());
                refreshList();
                selectedName = null;
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

        if (selectedName == null && !isEditMode) {
            detailView.getChildren().add(new Label("Select a name or click Add New"));
            return;
        }

        TextField nameField = new TextField(selectedName == null ? "" : selectedName.getName());
        nameField.setEditable(isEditMode);

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
        if (selectedName != null && selectedName.getLang() != null) {
            languageCombo.setValue(selectedName.getLang());
        }

        ScrollPane scrollPane = new ScrollPane();
        VBox content = new VBox(10,
                new Label("Name:"), nameField,
                new Label("Language:"), languageCombo);
        content.setPadding(new Insets(10));
        scrollPane.setContent(content);
        detailView.getChildren().add(scrollPane);

        HBox buttonBox = new HBox(10);
        if (isEditMode) {
            Button saveBtn = new Button("Save");
            saveBtn.setOnAction(e -> saveName(nameField, languageCombo));
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

    private void saveName(TextField nameField, ComboBox<Language> languageCombo) {
        Name result = selectedName == null ? new Name() : selectedName;
        result.setName(nameField.getText());
        result.setLang(languageCombo.getValue());

        if (selectedName == null) {
            controller.create(result);
        } else {
            controller.update(result.getId(), result);
        }
        refreshList();
        isEditMode = false;
        refreshDetailView();
    }
}
