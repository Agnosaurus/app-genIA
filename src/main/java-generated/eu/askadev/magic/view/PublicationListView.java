package eu.askadev.magic.view;

import eu.askadev.magic.controller.PublicationController;
import eu.askadev.magic.model.Publication;
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
public class PublicationListView {

    private final PublicationController controller;
    private ListView<Publication> listView;

    public PublicationListView(PublicationController controller) {
        this.controller = controller;
    }

    public Parent getView(LandingView landingView) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("Publications");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        listView = new ListView<>();
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Publication item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle() + " - " + item.getAuthor());
            }
        });
        refreshList();

        Button addBtn = new Button("Add New");
        addBtn.setOnAction(e -> showEditDialog(null));

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            Publication selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                controller.delete(selected.getId());
                refreshList();
            }
        });

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Publication selected = listView.getSelectionModel().getSelectedItem();
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

    private void showEditDialog(Publication publication) {
        Dialog<Publication> dialog = new Dialog<>();
        dialog.setTitle(publication == null ? "Add Publication" : "Edit Publication");

        TextField titleField = new TextField(publication == null ? "" : publication.getTitle());
        TextField authorField = new TextField(publication == null ? "" : publication.getAuthor());
        TextField yearField = new TextField(publication == null ? "" : publication.getPublicationYear());
        TextField publisherField = new TextField(publication == null ? "" : publication.getPublisher());

        VBox content = new VBox(10,
                new Label("Title:"), titleField,
                new Label("Author:"), authorField,
                new Label("Year:"), yearField,
                new Label("Publisher:"), publisherField);
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                Publication result = publication == null ? new Publication() : publication;
                result.setTitle(titleField.getText());
                result.setAuthor(authorField.getText());
                result.setPublicationYear(yearField.getText());
                result.setPublisher(publisherField.getText());
                return result;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (publication == null) {
                controller.create(result);
            } else {
                controller.update(result.getId(), result);
            }
            refreshList();
        });
    }
}
