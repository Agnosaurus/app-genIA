package eu.askadev.magic.view;

import eu.askadev.magic.controller.VariantController;
import eu.askadev.magic.model.Variant;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class VariantListView {

    private final VariantController controller;
    private ListView<Variant> listView;

    public VariantListView(VariantController controller) {
        this.controller = controller;
    }

    public Parent getView(LandingView landingView) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("Variants");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        listView = new ListView<>();
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Variant item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getVariant());
            }
        });
        refreshList();

        Button addBtn = new Button("Add New");
        addBtn.setOnAction(e -> showEditDialog(null));

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            Variant selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                controller.delete(selected.getId());
                refreshList();
            }
        });

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Variant selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) showEditDialog(selected);
            }
        });

        Button backBtn = new Button("Back to Home");
        backBtn.setOnAction(e -> Platform.runLater(() -> {
            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.getScene().setRoot(landingView.getView());
        }));

        HBox buttons = new HBox(10, addBtn, deleteBtn, backBtn);
        root.getChildren().addAll(title, listView, buttons);

        return root;
    }

    private void refreshList() {
        listView.setItems(FXCollections.observableArrayList(controller.findAll()));
    }

    private void showEditDialog(Variant variant) {
        Dialog<Variant> dialog = new Dialog<>();
        dialog.setTitle(variant == null ? "Add Variant" : "Edit Variant");

        TextField variantField = new TextField(variant == null ? "" : variant.getVariant());

        VBox content = new VBox(10, new Label("Variant:"), variantField);
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                Variant result = variant == null ? new Variant() : variant;
                result.setVariant(variantField.getText());
                return result;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (variant == null) {
                controller.create(result);
            } else {
                controller.update(result.getId(), result);
            }
            refreshList();
        });
    }
}
