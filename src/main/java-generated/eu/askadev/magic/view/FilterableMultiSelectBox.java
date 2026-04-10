package eu.askadev.magic.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FilterableMultiSelectBox<T> extends VBox {

    private final List<T> allItems;
    private final Function<T, String> labelFn;
    private final Function<T, String> idFn;
    private final Set<String> selectedIds;
    private final ListView<T> listView;
    private final boolean editable;

    public FilterableMultiSelectBox(List<T> allItems, Set<String> initialSelectedIds,
                                    Function<T, String> labelFn, Function<T, String> idFn,
                                    boolean editable) {
        super(4);
        this.allItems = allItems;
        this.labelFn = labelFn;
        this.idFn = idFn;
        this.selectedIds = new HashSet<>(initialSelectedIds);
        this.editable = editable;

        TextField filter = new TextField();
        filter.setPromptText("Filter...");
        filter.setDisable(!editable);

        listView = new ListView<>();
        listView.setPrefHeight(130);
        listView.setMaxHeight(130);
        listView.setDisable(!editable);
        listView.setCellFactory(lv -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();
            {
                setGraphic(checkBox);
                checkBox.setDisable(!editable);
                checkBox.setOnAction(e -> {
                    T item = getItem();
                    if (item != null) {
                        if (checkBox.isSelected()) selectedIds.add(idFn.apply(item));
                        else selectedIds.remove(idFn.apply(item));
                    }
                });
            }
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); checkBox.setVisible(false); }
                else { setText(labelFn.apply(item)); checkBox.setVisible(true); checkBox.setSelected(selectedIds.contains(idFn.apply(item))); }
            }
        });

        applyFilter("");
        filter.textProperty().addListener((obs, o, n) -> applyFilter(n));

        getChildren().addAll(filter, listView);
    }

    private void applyFilter(String text) {
        String lower = text == null ? "" : text.toLowerCase();
        ObservableList<T> filtered = FXCollections.observableArrayList(
            allItems.stream()
                .filter(i -> labelFn.apply(i).toLowerCase().contains(lower))
                .collect(Collectors.toList())
        );
        listView.setItems(filtered);
    }

    public Set<String> getSelectedIds() {
        return selectedIds;
    }
}
