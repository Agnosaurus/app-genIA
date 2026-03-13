package eu.askadev.magic.view;

import eu.askadev.magic.controller.NameController;
import eu.askadev.magic.model.Name;
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
public class NameListView {

    private final NameController controller;
    private ListView<Name> listView;

    public NameListView(NameController controller) {
        this.controller = controller;
    }

    public Parent getView(LandingView landingView) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("Names");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        listView = new ListView<>();
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Name item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        refreshList();

        Button addBtn = new Button("Add New");
        addBtn.setOnAction(e -> showEditDialog(null));

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            Name selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                controller.delete(selected.getId());
                refreshList();
            }
        });

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Name selected = listView.getSelectionModel().getSelectedItem();
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

    private void showEditDialog(Name name) {
        Dialog<Name> dialog = new Dialog<>();
        dialog.setTitle(name == null ? "Add Name" : "Edit Name");

        TextField nameField = new TextField(name == null ? "" : name.getName());

        VBox content = new VBox(10, new Label("Name:"), nameField);
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                Name result = name == null ? new Name() : name;
                result.setName(nameField.getText());
                return result;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (name == null) {
                controller.create(result);
            } else {
                controller.update(result.getId(), result);
            }
            refreshList();
        });
    }
}
