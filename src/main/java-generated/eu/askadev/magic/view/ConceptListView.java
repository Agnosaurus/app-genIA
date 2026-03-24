package eu.askadev.magic.view;

import eu.askadev.magic.controller.ConceptController;
import eu.askadev.magic.controller.KeywordController;
import eu.askadev.magic.model.Concept;
import eu.askadev.magic.model.Keyword;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ConceptListView {

    private final ConceptController controller;
    private final KeywordController keywordController;
    private TableView<Concept> tableView;
    private VBox detailView;
    private Concept selectedConcept;
    private boolean isEditMode = false;

    public ConceptListView(ConceptController controller, KeywordController keywordController) {
        this.controller = controller;
        this.keywordController = keywordController;
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

        Label title = new Label("💡 Concepts");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0078d4;");

        tableView = new TableView<>();
        TableColumn<Concept, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getValue()));
        TableColumn<Concept, String> keywordsCol = new TableColumn<>("Keywords");
        keywordsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getKeywords().stream().map(Keyword::getValue).collect(Collectors.joining(", "))));

        tableView.getColumns().addAll(nameCol, keywordsCol);
        refreshList();

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedConcept = newVal;
            isEditMode = false;
            refreshDetailView();
        });

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && selectedConcept != null) {
                isEditMode = true;
                refreshDetailView();
            }
        });

        Button addBtn = new Button("Add New");
        addBtn.setOnAction(e -> {
            selectedConcept = null;
            isEditMode = true;
            refreshDetailView();
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            if (selectedConcept != null) {
                controller.delete(selectedConcept.getId());
                refreshList();
                selectedConcept = null;
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

    private VBox createFieldGroup(String title, Node... fieldPairs) {
        VBox group = new VBox(10);
        group.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 4; -fx-padding: 15; -fx-background-color: #fafafa;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 0 0 10 0;");
        group.getChildren().add(titleLabel);

        for (Node field : fieldPairs) {
            group.getChildren().add(field);
        }

        return group;
    }

    private void refreshDetailView() {
        detailView.getChildren().clear();

        if (selectedConcept == null && !isEditMode) {
            detailView.getChildren().add(new Label("Select a concept or click Add New"));
            return;
        }

        TextField valueField = new TextField(selectedConcept == null ? "" : selectedConcept.getValue());
        valueField.setEditable(isEditMode);

        // Debug: show what keywords are currently linked to this concept
        if (selectedConcept != null) {
            System.out.println("DEBUG: Loading concept: " + selectedConcept.getValue());
            System.out.println("DEBUG: Current keywords count: " + (selectedConcept.getKeywords() != null ? selectedConcept.getKeywords().size() : 0));
            if (selectedConcept.getKeywords() != null && !selectedConcept.getKeywords().isEmpty()) {
                selectedConcept.getKeywords().forEach(k -> System.out.println("DEBUG: Linked keyword: " + k.toString()));
            }
        }

        // Create multi-select combo for keywords
        VBox keywordsComboWrapper = createMultiSelectComboWithFilter("Keywords", keywordController.findAll(),
            selectedConcept == null ? null : selectedConcept.getKeywords(), isEditMode);
        // Extract the combo from wrapper (it's the second child: filter field is first, combo is second)
        ComboBox<Object> keywordsCombo = (ComboBox<Object>) keywordsComboWrapper.getChildren().get(1);

        // Get the selections set - it's stored in the wrapper's userData
        @SuppressWarnings("unchecked")
        java.util.Set<Object> initialSelections = (java.util.Set<Object>) keywordsComboWrapper.getUserData();
        System.out.println("DEBUG: refreshDetailView - wrapper has " + (initialSelections != null ? initialSelections.size() : 0) + " selections");

        ScrollPane scrollPane = new ScrollPane();
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #ffffff;");

        // Value field group
        VBox valueGroup = new VBox(5);
        valueGroup.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 4; -fx-padding: 15; -fx-background-color: #fafafa;");
        Label valueTitle = new Label("Concept Information");
        valueTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 0 0 10 0;");
        valueGroup.getChildren().addAll(valueTitle, new Label("Value:"), valueField);

        // Keywords group
        VBox keywordsGroup = new VBox(5);
        keywordsGroup.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 4; -fx-padding: 15; -fx-background-color: #fafafa;");
        Label keywordsTitle = new Label("Keywords");
        keywordsTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 0 0 10 0;");
        keywordsGroup.getChildren().addAll(keywordsTitle, keywordsComboWrapper);

        content.getChildren().addAll(valueGroup, keywordsGroup);
        scrollPane.setContent(content);
        detailView.getChildren().add(scrollPane);

        HBox buttonBox = new HBox(10);
        if (isEditMode) {
            Button saveBtn = new Button("Save");
            saveBtn.setOnAction(e -> saveConcept(valueField, keywordsCombo));
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

    private ComboBox<Object> createMultiSelectCombo(String label, java.util.List<?> allItems, java.util.Set<?> selectedItems, boolean editable) {
        ComboBox<Object> comboBox = new ComboBox<>();
        comboBox.setDisable(!editable);
        comboBox.setPrefHeight(25);
        comboBox.setPrefWidth(300);

        // Initialize selections with provided items - IMPORTANT: must be a mutable set
        java.util.Set<Object> selections = new java.util.HashSet<>();
        if (selectedItems != null && !selectedItems.isEmpty()) {
            selections.addAll(selectedItems);
        }

        FilteredList<Object> filteredList = new FilteredList<>(FXCollections.observableArrayList(allItems));
        comboBox.setItems(filteredList);

        comboBox.setCellFactory(lv -> new ListCell<Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Display with checkmark if selected, use toString() for display
                    boolean isSelected = selections.contains(item);
                    setText((isSelected ? "✓ " : "") + item.toString());
                }

                setOnMouseClicked(event -> {
                    if (!empty && item != null) {
                        if (selections.contains(item)) {
                            selections.remove(item);
                        } else {
                            selections.add(item);
                        }
                        comboBox.hide();
                        // Refresh the list display
                        comboBox.getItems().clear();
                        comboBox.getItems().addAll(filteredList.getSource());
                        event.consume();
                    }
                });
            }
        });

        comboBox.setButtonCell(new ListCell<Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                setText(label + " (" + selections.size() + ")");
            }
        });

        comboBox.setOnShowing(e -> {
            if (comboBox.isShowing()) {
                // Set max height for dropdown to limit space
                comboBox.getStyleClass().add("limited-height-combo");
            }
        });

        // Store selections in userData for later retrieval
        comboBox.setUserData(selections);
        return comboBox;
    }

    private VBox createMultiSelectComboWithFilter(String label, java.util.List<?> allItems, java.util.Set<?> selectedItems, boolean editable) {
        ComboBox<Object> comboBox = createMultiSelectCombo(label, allItems, selectedItems, editable);

        TextField filterField = new TextField();
        filterField.setPromptText("Filter " + label + "...");
        filterField.setEditable(editable);
        filterField.setPrefWidth(300);

        // Get the filtered list from the combo
        FilteredList<Object> filteredList = (FilteredList<Object>) comboBox.getItems();

        // Update filter predicate when text changes
        filterField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                filteredList.setPredicate(item -> true);
            } else {
                String filterText = newVal.toLowerCase();
                filteredList.setPredicate(item -> item != null && item.toString().toLowerCase().contains(filterText));
            }
        });

        VBox wrapper = new VBox(5, filterField, comboBox);
        wrapper.setPrefWidth(300);

        // IMPORTANT: Store the selections set in the wrapper so we can retrieve it later
        java.util.Set<Object> selections = (java.util.Set<Object>) comboBox.getUserData();
        wrapper.setUserData(selections);

        System.out.println("DEBUG: Created wrapper with " + selections.size() + " initially selected items");

        return wrapper;
    }

    private void saveConcept(TextField valueField, ComboBox<Object> keywordsCombo) {
        Concept result = selectedConcept == null ? new Concept() : selectedConcept;
        result.setValue(valueField.getText());

        @SuppressWarnings("unchecked")
        java.util.Set<Object> selectedKeywords = (java.util.Set<Object>) keywordsCombo.getUserData();

        System.out.println("DEBUG: Saving concept with " + (selectedKeywords != null ? selectedKeywords.size() : 0) + " keywords");

        if (selectedKeywords != null && !selectedKeywords.isEmpty()) {
            selectedKeywords.forEach(k -> System.out.println("DEBUG: Selected keyword: " + k.toString()));
        }

        // Initialize keywords set if null
        if (result.getKeywords() == null) {
            result.setKeywords(new java.util.HashSet<>());
        }

        result.getKeywords().clear();
        if (selectedKeywords != null && !selectedKeywords.isEmpty()) {
            result.getKeywords().addAll(selectedKeywords.stream()
                .map(item -> (Keyword) item)
                .collect(Collectors.toSet()));
        }

        System.out.println("DEBUG: Concept now has " + result.getKeywords().size() + " keywords");

        if (selectedConcept == null) {
            controller.create(result);
        } else {
            controller.update(result.getId(), result);
        }
        refreshList();
        isEditMode = false;
        refreshDetailView();
    }
}
