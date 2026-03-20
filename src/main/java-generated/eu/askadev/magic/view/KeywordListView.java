package eu.askadev.magic.view;

import eu.askadev.magic.controller.KeywordController;
import eu.askadev.magic.model.Keyword;
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
public class KeywordListView {

    private final KeywordController controller;
    private TableView<Keyword> tableView;

    public KeywordListView(KeywordController controller) {
        this.controller = controller;
    }

    public Parent getView(LandingView landingView) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("Keywords");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        
        TableColumn<Keyword, String> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getValue()));
        
        tableView.getColumns().add(valueCol);
        refreshList();

        Button addBtn = new Button("Add New");
        addBtn.setOnAction(e -> showEditDialog(null));

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            Keyword selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                controller.delete(selected.getId());
                refreshList();
            }
        });

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Keyword selected = tableView.getSelectionModel().getSelectedItem();
                if (selected != null) showEditDialog(selected);
            }
        });

        Button backBtn = new Button("Back to Home");
        backBtn.setOnAction(e -> Platform.runLater(() -> {
            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.getScene().setRoot(landingView.getView());
        }));

        HBox buttons = new HBox(10, addBtn, deleteBtn, backBtn);
        root.getChildren().addAll(title, tableView, buttons);

        return root;
    }

    private void refreshList() {
        tableView.setItems(FXCollections.observableArrayList(controller.findAll()));
    }

    private void showEditDialog(Keyword keyword) {
        Dialog<Keyword> dialog = new Dialog<>();
        dialog.setTitle(keyword == null ? "Add Keyword" : "Edit Keyword");

        TextField valueField = new TextField(keyword == null ? "" : keyword.getValue());

        VBox content = new VBox(10, new Label("Value:"), valueField);
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                Keyword result = keyword == null ? new Keyword() : keyword;
                result.setValue(valueField.getText());
                return result;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (keyword == null) {
                controller.create(result);
            } else {
                controller.update(result.getId(), result);
            }
            refreshList();
        });
    }
}
