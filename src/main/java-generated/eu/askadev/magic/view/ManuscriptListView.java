package eu.askadev.magic.view;

import eu.askadev.magic.controller.ManuscriptController;
import eu.askadev.magic.model.Manuscript;
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
public class ManuscriptListView {

    private final ManuscriptController controller;
    private ListView<Manuscript> listView;

    public ManuscriptListView(ManuscriptController controller) {
        this.controller = controller;
    }

    public Parent getView(LandingView landingView) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("Manuscripts");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        listView = new ListView<>();
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Manuscript item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitre() + " (" + item.getUniqueId() + ")");
            }
        });
        refreshList();

        Button addBtn = new Button("Add New");
        addBtn.setOnAction(e -> showEditDialog(null));

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            Manuscript selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                controller.delete(selected.getId());
                refreshList();
            }
        });

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Manuscript selected = listView.getSelectionModel().getSelectedItem();
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

    private void showEditDialog(Manuscript manuscript) {
        Dialog<Manuscript> dialog = new Dialog<>();
        dialog.setTitle(manuscript == null ? "Add Manuscript" : "Edit Manuscript");

        TextField uniqueIdField = new TextField(manuscript == null ? "" : manuscript.getUniqueId());
        TextField titreField = new TextField(manuscript == null ? "" : manuscript.getTitre());
        TextField lieuField = new TextField(manuscript == null ? "" : manuscript.getLieu());
        TextField dateField = new TextField(manuscript == null ? "" : manuscript.getDate());
        TextField scriptoriumField = new TextField(manuscript == null ? "" : manuscript.getScriptorium());

        VBox content = new VBox(10,
                new Label("Unique ID:"), uniqueIdField,
                new Label("Titre:"), titreField,
                new Label("Lieu:"), lieuField,
                new Label("Date:"), dateField,
                new Label("Scriptorium:"), scriptoriumField);
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                Manuscript result = manuscript == null ? new Manuscript() : manuscript;
                result.setUniqueId(uniqueIdField.getText());
                result.setTitre(titreField.getText());
                result.setLieu(lieuField.getText());
                result.setDate(dateField.getText());
                result.setScriptorium(scriptoriumField.getText());
                return result;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (manuscript == null) {
                controller.create(result);
            } else {
                controller.update(result.getId(), result);
            }
            refreshList();
        });
    }
}
