package eu.askadev.magic.view;

import eu.askadev.magic.controller.VariantController;
import eu.askadev.magic.controller.NameController;
import eu.askadev.magic.model.Variant;
import eu.askadev.magic.model.Name;
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
public class VariantListView {

    private final VariantController controller;
    private final NameController nameController;
    private TableView<Variant> tableView;
    private VBox detailView;
    private Variant selectedVariant;
    private boolean isEditMode = false;

    public VariantListView(VariantController controller, NameController nameController) {
        this.controller = controller;
        this.nameController = nameController;
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

        Label title = new Label("🔀 Variants");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0078d4;");

        tableView = new TableView<>();
        TableColumn<Variant, String> variantCol = new TableColumn<>("Variant");
        variantCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getVariant()));
        TableColumn<Variant, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getName() != null ? data.getValue().getName().getName() : ""));
        tableView.getColumns().addAll(variantCol, nameCol);
        refreshList();

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedVariant = newVal;
            isEditMode = false;
            refreshDetailView();
        });

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && selectedVariant != null) {
                isEditMode = true;
                refreshDetailView();
            }
        });

        Button addBtn = new Button("Add New");
        addBtn.setOnAction(e -> {
            selectedVariant = null;
            isEditMode = true;
            refreshDetailView();
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            if (selectedVariant != null) {
                controller.delete(selectedVariant.getId());
                refreshList();
                selectedVariant = null;
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

        if (selectedVariant == null && !isEditMode) {
            detailView.getChildren().add(new Label("Select a variant or click Add New"));
            return;
        }

        TextField variantField = new TextField(selectedVariant == null ? "" : selectedVariant.getVariant());
        variantField.setEditable(isEditMode);

        ComboBox<Name> nameCombo = new ComboBox<>();
        nameCombo.setItems(FXCollections.observableArrayList(nameController.findAll()));
        nameCombo.setCellFactory(lv -> new ListCell<Name>() {
            @Override
            protected void updateItem(Name item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });
        nameCombo.setButtonCell(new ListCell<Name>() {
            @Override
            protected void updateItem(Name item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });
        nameCombo.setDisable(!isEditMode);
        if (selectedVariant != null && selectedVariant.getName() != null) {
            nameCombo.setValue(selectedVariant.getName());
        }

        ScrollPane scrollPane = new ScrollPane();
        VBox content = new VBox(10,
                new Label("Variant:"), variantField,
                new Label("Name:"), nameCombo);
        content.setPadding(new Insets(10));
        scrollPane.setContent(content);
        detailView.getChildren().add(scrollPane);

        HBox buttonBox = new HBox(10);
        if (isEditMode) {
            Button saveBtn = new Button("Save");
            saveBtn.setOnAction(e -> saveVariant(variantField, nameCombo));
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

    private void saveVariant(TextField variantField, ComboBox<Name> nameCombo) {
        Variant result = selectedVariant == null ? new Variant() : selectedVariant;
        result.setVariant(variantField.getText());
        result.setName(nameCombo.getValue());

        if (selectedVariant == null) {
            controller.create(result);
        } else {
            controller.update(result.getId(), result);
        }
        refreshList();
        isEditMode = false;
        refreshDetailView();
    }
}
