package eu.askadev.magic.view;

import eu.askadev.magic.controller.ConceptController;
import eu.askadev.magic.model.Concept;
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

import java.util.stream.Collectors;

@Component
public class ConceptListView {

    private final ConceptController controller;
    private TableView<Concept> tableView;

    public ConceptListView(ConceptController controller) {
        this.controller = controller;
    }

    public Parent getView(LandingView landingView) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("Concepts");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        
        TableColumn<Concept, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getValue()));
        
        TableColumn<Concept, String> keywordsCol = new TableColumn<>("Keywords");
        keywordsCol.setCellValueFactory(data -> {
            String keywords = data.getValue().getKeywords().stream()
                .map(Keyword::getValue)
                .collect(Collectors.joining(", "));
            return new javafx.beans.property.SimpleStringProperty(keywords);
        });
        
        tableView.getColumns().addAll(nameCol, keywordsCol);
        refreshList();

        Button addBtn = new Button("Add New");
        addBtn.setOnAction(e -> showEditDialog(null));

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            Concept selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                controller.delete(selected.getId());
                refreshList();
            }
        });

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Concept selected = tableView.getSelectionModel().getSelectedItem();
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

    private void showEditDialog(Concept concept) {
        Dialog<Concept> dialog = new Dialog<>();
        dialog.setTitle(concept == null ? "Add Concept" : "Edit Concept");

        TextField valueField = new TextField(concept == null ? "" : concept.getValue());

        VBox content = new VBox(10, new Label("Value:"), valueField);
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                Concept result = concept == null ? new Concept() : concept;
                result.setValue(valueField.getText());
                return result;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (concept == null) {
                controller.create(result);
            } else {
                controller.update(result.getId(), result);
            }
            refreshList();
        });
    }
}
