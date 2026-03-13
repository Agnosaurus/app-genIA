package eu.askadev.magic.view;

import eu.askadev.magic.controller.AuthorController;
import eu.askadev.magic.model.Author;
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
public class AuthorListView {

    private final AuthorController controller;
    private TableView<Author> tableView;

    public AuthorListView(AuthorController controller) {
        this.controller = controller;
    }

    public Parent getView(LandingView landingView) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("Authors");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        
        TableColumn<Author, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));
        
        TableColumn<Author, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));
        
        tableView.getColumns().addAll(firstNameCol, lastNameCol);
        refreshList();

        Button addBtn = new Button("Add New");
        addBtn.setOnAction(e -> showEditDialog(null));

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            Author selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                controller.delete(selected.getId());
                refreshList();
            }
        });

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Author selected = tableView.getSelectionModel().getSelectedItem();
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

    private void showEditDialog(Author author) {
        Dialog<Author> dialog = new Dialog<>();
        dialog.setTitle(author == null ? "Add Author" : "Edit Author");

        TextField firstNameField = new TextField(author == null ? "" : author.getFirstName());
        TextField lastNameField = new TextField(author == null ? "" : author.getLastName());
        TextField birthDateField = new TextField(author == null ? "" : author.getBirthDate());

        VBox content = new VBox(10,
                new Label("First Name:"), firstNameField,
                new Label("Last Name:"), lastNameField,
                new Label("Birth Date:"), birthDateField);
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                Author result = author == null ? new Author() : author;
                result.setFirstName(firstNameField.getText());
                result.setLastName(lastNameField.getText());
                result.setBirthDate(birthDateField.getText());
                return result;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (author == null) {
                controller.create(result);
            } else {
                controller.update(result.getId(), result);
            }
            refreshList();
        });
    }
}
