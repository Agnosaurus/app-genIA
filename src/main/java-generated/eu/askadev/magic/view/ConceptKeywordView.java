package eu.askadev.magic.view;

import eu.askadev.magic.controller.ConceptController;
import eu.askadev.magic.controller.KeywordController;
import eu.askadev.magic.controller.SubKeywordController;
import eu.askadev.magic.model.Concept;
import eu.askadev.magic.model.Keyword;
import eu.askadev.magic.model.SubKeyword;
import eu.askadev.magic.service.LocalizationService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ConceptKeywordView {

    private final ConceptController conceptController;
    private final KeywordController keywordController;
    private final SubKeywordController subKeywordController;

    private Concept selectedConcept;
    private Keyword selectedKeyword;
    private SubKeyword selectedSubKeyword;
    private final Set<String> collapsedKeywordIds = new HashSet<>();

    private ListView<Concept> conceptListView;
    private VBox rightPanel;
    private VBox editFormBar;

    private static final Color[] KW_COLORS = {
        Color.web("#4a90d9"), Color.web("#27ae60"), Color.web("#e6b800"),
        Color.web("#e67e22"), Color.web("#e74c3c"), Color.web("#9b59b6"),
        Color.web("#1abc9c"), Color.web("#e91e8c")
    };

    public ConceptKeywordView(ConceptController conceptController,
                              KeywordController keywordController,
                              SubKeywordController subKeywordController) {
        this.conceptController = conceptController;
        this.keywordController = keywordController;
        this.subKeywordController = subKeywordController;
    }

    public Parent getView(LandingView landingView) {
        LocalizationService i18n = LocalizationService.getInstance();

        editFormBar = new VBox();
        rightPanel = new VBox();
        rightPanel.setFillWidth(true);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(buildLeftPanel(), rightPanel);
        splitPane.setDividerPosition(0, 0.20);

        refreshRight();

        Button backBtn = new Button(i18n.get("button.back"));
        backBtn.setOnAction(e -> {
            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.getScene().setRoot(landingView.getView());
        });

        VBox root = new VBox(10, splitPane, backBtn);
        root.setPadding(new Insets(10));
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        return root;
    }

    // ── Left panel ───────────────────────────────────────────────────

    private VBox buildLeftPanel() {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(10));

        Label title = new Label("💡 Concepts");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        conceptListView = new ListView<>(FXCollections.observableArrayList(conceptController.findAll()));
        conceptListView.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Concept item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getValue());
            }
        });
        conceptListView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selectedConcept = n;
            selectedKeyword = null;
            selectedSubKeyword = null;
            collapsedKeywordIds.clear();
            refreshRight();
        });

        Button addBtn = new Button("＋ Add Concept");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> openForm("concept", null, null));

        panel.getChildren().addAll(title, conceptListView, addBtn);
        VBox.setVgrow(conceptListView, Priority.ALWAYS);
        return panel;
    }

    // ── Right panel ──────────────────────────────────────────────────

    private void refreshRight() {
        rightPanel.getChildren().clear();
        editFormBar.getChildren().clear();
        editFormBar.getStyleClass().clear();

        if (selectedConcept == null) {
            Label hint = new Label("← Select a concept to explore its hierarchy");
            hint.setStyle("-fx-text-fill: #888888; -fx-font-style: italic;");
            VBox centered = new VBox(hint);
            centered.setAlignment(Pos.CENTER);
            VBox.setVgrow(centered, Priority.ALWAYS);
            rightPanel.getChildren().addAll(centered, editFormBar);
            return;
        }

        // Top toolbar: add buttons + export
        Button addKwBtn = new Button("＋ Keyword");
        addKwBtn.setOnAction(e -> openForm("keyword", selectedConcept, null));

        Button addSkBtn = new Button("＋ SubKeyword");
        addSkBtn.setOnAction(e -> openForm("subkeyword", null, null));

        Button exportBtn = new Button("📷 Copy to Clipboard");
        exportBtn.setOnAction(e -> exportToClipboard());

        HBox toolbar = new HBox(10, addKwBtn, addSkBtn, exportBtn);
        toolbar.setPadding(new Insets(6, 10, 6, 10));
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // Canvas tree — fills all available space
        StackPane canvasPane = new StackPane();
        canvasPane.setStyle("-fx-background-color: white;");
        VBox.setVgrow(canvasPane, Priority.ALWAYS);

        Canvas canvas = buildCanvas(canvasPane);
        canvasPane.getChildren().add(canvas);

        // Resize canvas when pane size changes
        canvasPane.widthProperty().addListener((obs, o, n) -> {
            Canvas c = buildCanvas(canvasPane);
            canvasPane.getChildren().setAll(c);
        });
        canvasPane.heightProperty().addListener((obs, o, n) -> {
            Canvas c = buildCanvas(canvasPane);
            canvasPane.getChildren().setAll(c);
        });

        rightPanel.getChildren().addAll(toolbar, canvasPane, buildOrphanSection(), editFormBar);
    }

    // ── Canvas tree ──────────────────────────────────────────────────

    private Canvas buildCanvas(StackPane pane) {
        double w = Math.max(pane.getWidth(), 800);
        double h = Math.max(pane.getHeight(), 400);
        return drawTree(w, h, false);
    }

    private Canvas drawTree(double w, double h, boolean exportMode) {
        List<Keyword> keywords = keywordController.findAll().stream()
            .filter(k -> k.getParentConcept() != null
                && k.getParentConcept().getId().equals(selectedConcept.getId()))
            .collect(Collectors.toList());

        List<List<SubKeyword>> kwSubs = new ArrayList<>();
        for (Keyword kw : keywords) {
            List<SubKeyword> subs = collapsedKeywordIds.contains(kw.getId())
                ? Collections.emptyList()
                : subKeywordController.findAll().stream()
                    .filter(sk -> sk.getParentKeyword() != null && sk.getParentKeyword().getId().equals(kw.getId()))
                    .collect(Collectors.toList());
            kwSubs.add(subs);
        }

        List<Integer> kwRowCounts = new ArrayList<>();
        for (int i = 0; i < keywords.size(); i++) kwRowCounts.add(Math.max(1, kwSubs.get(i).size()));
        int totalRows = Math.max(kwRowCounts.stream().mapToInt(Integer::intValue).sum(), 1);

        final double PAD_V = 30;
        final double ROW_H = Math.max(28, (h - PAD_V * 2) / totalRows);

        // Column X positions proportional to canvas width
        final double COL_SK_X   = w * 0.02;
        final double COL_KW_X   = w * 0.42;
        final double COL_CON_X  = w * 0.82;

        Canvas canvas = new Canvas(w, h);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, w, h);

        double conceptY = h / 2.0;

        // Concept label
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        gc.setFill(Color.web("#1a237e"));
        gc.fillText(selectedConcept.getValue(), COL_CON_X, conceptY + 7);

        // Double-click concept to edit
        double conceptTextW = selectedConcept.getValue().length() * 11.5;

        List<double[]> kwHitAreas = new ArrayList<>();
        List<double[]> skHitAreas = new ArrayList<>();
        List<double[]> toggleHitAreas = new ArrayList<>();

        int rowOffset = 0;
        for (int ki = 0; ki < keywords.size(); ki++) {
            Keyword kw = keywords.get(ki);
            Color kwColor = KW_COLORS[ki % KW_COLORS.length];
            List<SubKeyword> subs = kwSubs.get(ki);
            int rows = kwRowCounts.get(ki);
            double kwY = PAD_V + (rowOffset + rows / 2.0) * ROW_H;

            // Bezier: concept → keyword
            boolean kwSel = selectedKeyword != null && selectedKeyword.getId().equals(kw.getId());
            gc.setStroke(kwColor);
            gc.setLineWidth(kwSel ? 3.0 : 1.8);
            gc.beginPath();
            double cpX = (COL_KW_X + kw.getValue().length() * 8.5 + COL_CON_X) / 2.0;
            gc.moveTo(COL_CON_X, conceptY);
            gc.bezierCurveTo(cpX, conceptY, cpX, kwY, COL_KW_X + kw.getValue().length() * 8.5, kwY);
            gc.stroke();

            // Keyword label
            gc.setFont(Font.font("Segoe UI", kwSel ? FontWeight.BOLD : FontWeight.NORMAL, 15));
            gc.setFill(kwSel ? kwColor.darker() : kwColor.darker().darker());
            gc.fillText(kw.getValue(), COL_KW_X, kwY + 5);
            double kwTextW = kw.getValue().length() * 8.5;
            kwHitAreas.add(new double[]{COL_KW_X, kwY - 14, COL_KW_X + kwTextW, kwY + 8, ki});

            // Collapse toggle (▶/▼) — only in interactive mode
            long skCountAll = subKeywordController.findAll().stream()
                .filter(sk -> sk.getParentKeyword() != null && sk.getParentKeyword().getId().equals(kw.getId()))
                .count();
            if (skCountAll > 0 && !exportMode) {
                boolean collapsed = collapsedKeywordIds.contains(kw.getId());
                String toggleTxt = collapsed ? " ▶" : " ▼";
                gc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 10));
                gc.setFill(Color.web("#aaaaaa"));
                gc.fillText(toggleTxt, COL_KW_X + kwTextW + 2, kwY + 5);
                toggleHitAreas.add(new double[]{COL_KW_X + kwTextW, kwY - 10, COL_KW_X + kwTextW + 20, kwY + 8, ki});
            }

            // SubKeywords
            for (int si = 0; si < subs.size(); si++) {
                SubKeyword sk = subs.get(si);
                double skY = PAD_V + (rowOffset + si) * ROW_H + ROW_H / 2.0;
                boolean skSel = selectedSubKeyword != null && selectedSubKeyword.getId().equals(sk.getId());

                // Bezier: keyword → subkeyword
                gc.setStroke(kwColor.deriveColor(0, 1, skSel ? 0.7 : 0.85, 1));
                gc.setLineWidth(skSel ? 2.5 : 1.4);
                gc.beginPath();
                double cpX2 = (COL_SK_X + sk.getValue().length() * 7.5 + COL_KW_X) / 2.0;
                gc.moveTo(COL_KW_X, kwY);
                gc.bezierCurveTo(cpX2, kwY, cpX2, skY, COL_SK_X + sk.getValue().length() * 7.5, skY);
                gc.stroke();

                // SubKeyword label
                gc.setFont(Font.font("Segoe UI", skSel ? FontWeight.BOLD : FontWeight.NORMAL, 12));
                gc.setFill(skSel ? kwColor.darker() : kwColor.deriveColor(0, 0.8, 0.7, 1));
                gc.fillText(sk.getValue(), COL_SK_X, skY + 4);
                double skTextW = sk.getValue().length() * 7.5;
                skHitAreas.add(new double[]{COL_SK_X, skY - 12, COL_SK_X + skTextW, skY + 6, ki, si});
            }

            rowOffset += rows;
        }

        if (!exportMode) {
            canvas.setOnMouseMoved(e -> {
                double mx = e.getX(), my = e.getY();
                boolean overTarget =
                    (mx >= COL_CON_X && mx <= COL_CON_X + conceptTextW && my >= conceptY - 16 && my <= conceptY + 10)
                    || toggleHitAreas.stream().anyMatch(hit -> mx >= hit[0] && mx <= hit[2] && my >= hit[1] && my <= hit[3])
                    || kwHitAreas.stream().anyMatch(hit -> mx >= hit[0] && mx <= hit[2] && my >= hit[1] && my <= hit[3])
                    || skHitAreas.stream().anyMatch(hit -> mx >= hit[0] && mx <= hit[2] && my >= hit[1] && my <= hit[3]);
                canvas.setCursor(overTarget ? Cursor.HAND : Cursor.DEFAULT);
            });
            canvas.setOnMouseClicked(e -> {
                double mx = e.getX(), my = e.getY();

                // Concept double-click
                if (e.getClickCount() == 2 && mx >= COL_CON_X && mx <= COL_CON_X + conceptTextW
                        && my >= conceptY - 16 && my <= conceptY + 10) {
                    openForm("concept-edit", selectedConcept, null); return;
                }

                // Toggle collapse
                for (double[] hit : toggleHitAreas) {
                    if (mx >= hit[0] && mx <= hit[2] && my >= hit[1] && my <= hit[3]) {
                        Keyword kw = keywords.get((int) hit[4]);
                        if (collapsedKeywordIds.contains(kw.getId())) collapsedKeywordIds.remove(kw.getId());
                        else collapsedKeywordIds.add(kw.getId());
                        refreshRight(); return;
                    }
                }

                // Keyword click
                for (double[] hit : kwHitAreas) {
                    if (mx >= hit[0] && mx <= hit[2] && my >= hit[1] && my <= hit[3]) {
                        Keyword kw = keywords.get((int) hit[4]);
                        if (e.getClickCount() == 2) openForm("keyword-edit", null, kw);
                        else { selectedKeyword = kw; selectedSubKeyword = null; refreshRight(); }
                        return;
                    }
                }

                // SubKeyword click
                for (double[] hit : skHitAreas) {
                    if (mx >= hit[0] && mx <= hit[2] && my >= hit[1] && my <= hit[3]) {
                        SubKeyword sk = kwSubs.get((int) hit[4]).get((int) hit[5]);
                        if (e.getClickCount() == 2) openForm("subkeyword-edit", null, sk);
                        else { selectedSubKeyword = sk; selectedKeyword = null; refreshRight(); }
                        return;
                    }
                }
            });
        }

        return canvas;
    }

    // ── Export to clipboard ──────────────────────────────────────────

    private void exportToClipboard() {
        // Find the canvas pane size
        StackPane pane = rightPanel.getChildren().stream()
            .filter(n -> n instanceof StackPane).map(n -> (StackPane) n).findFirst().orElse(null);
        if (pane == null) return;

        double w = Math.max(pane.getWidth(), 800);
        double h = Math.max(pane.getHeight(), 400);

        Canvas exportCanvas = drawTree(w, h, true);
        WritableImage img = exportCanvas.snapshot(null, null);
        int iw = (int) img.getWidth(), ih = (int) img.getHeight();
        BufferedImage buffered = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB);
        javafx.scene.image.PixelReader pr = img.getPixelReader();
        for (int y = 0; y < ih; y++)
            for (int x = 0; x < iw; x++)
                buffered.setRGB(x, y, pr.getArgb(x, y));

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new Transferable() {
            @Override public DataFlavor[] getTransferDataFlavors() { return new DataFlavor[]{DataFlavor.imageFlavor}; }
            @Override public boolean isDataFlavorSupported(DataFlavor f) { return f.equals(DataFlavor.imageFlavor); }
            @Override public Object getTransferData(DataFlavor f) throws UnsupportedFlavorException, java.io.IOException {
                if (!f.equals(DataFlavor.imageFlavor)) throw new UnsupportedFlavorException(f);
                return buffered;
            }
        }, null);

        new Alert(Alert.AlertType.INFORMATION, "Tree image copied to clipboard.", ButtonType.OK).showAndWait();
    }

    // ── Orphan section ───────────────────────────────────────────────

    private VBox buildOrphanSection() {
        List<SubKeyword> orphans = subKeywordController.findAll().stream()
            .filter(sk -> sk.getParentKeyword() == null)
            .collect(Collectors.toList());

        VBox section = new VBox(6);
        section.setPadding(new Insets(4, 10, 4, 10));
        if (orphans.isEmpty()) return section;

        section.getStyleClass().add("orphan-section");
        Label title = new Label("⚠️ Orphaned SubKeywords (" + orphans.size() + ")");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        section.getChildren().add(title);

        for (SubKeyword sk : orphans) {
            ComboBox<Keyword> combo = new ComboBox<>(FXCollections.observableArrayList(keywordController.findAll()));
            combo.setPromptText("Reassign to keyword...");
            combo.setCellFactory(lv -> new ListCell<>() {
                @Override protected void updateItem(Keyword item, boolean empty) {
                    super.updateItem(item, empty); setText(empty || item == null ? null : item.getValue());
                }
            });
            combo.setButtonCell(new ListCell<>() {
                @Override protected void updateItem(Keyword item, boolean empty) {
                    super.updateItem(item, empty); setText(empty || item == null ? null : item.getValue());
                }
            });
            Button reassign = new Button("✔ Reassign");
            reassign.setOnAction(e -> {
                if (combo.getValue() != null) {
                    sk.setParentKeyword(combo.getValue());
                    subKeywordController.update(sk.getId(), sk);
                    refreshRight();
                }
            });
            Button delete = new Button("🗑 Delete");
            delete.getStyleClass().add("button-danger");
            delete.setOnAction(e -> { subKeywordController.delete(sk.getId()); refreshRight(); });

            HBox row = new HBox(8, new Label(sk.getValue()), combo, reassign, delete);
            row.setAlignment(Pos.CENTER_LEFT);
            section.getChildren().add(row);
        }
        return section;
    }

    // ── Edit form ────────────────────────────────────────────────────

    private void openForm(String type, Object parentForNew, Object existing) {
        editFormBar.getChildren().clear();
        editFormBar.getStyleClass().setAll("edit-form-bar");

        String currentValue = switch (type) {
            case "concept-edit"    -> ((Concept) existing).getValue();
            case "keyword-edit"    -> ((Keyword) existing).getValue();
            case "subkeyword-edit" -> ((SubKeyword) existing).getValue();
            default -> "";
        };

        String labelText = switch (type) {
            case "concept-edit"    -> "Edit Concept:";
            case "keyword"         -> "New Keyword:";
            case "keyword-edit"    -> "Edit Keyword:";
            case "subkeyword"      -> "New SubKeyword:";
            case "subkeyword-edit" -> "Edit SubKeyword:";
            default                -> "New Concept:";
        };

        Label formLabel = new Label(labelText);
        formLabel.setStyle("-fx-font-weight: bold;");
        TextField field = new TextField(currentValue);
        field.setPromptText("Value...");
        field.setPrefWidth(220);

        // Keyword selector for subkeyword
        ComboBox<Keyword> kwCombo = new ComboBox<>();
        if (type.equals("subkeyword") || type.equals("subkeyword-edit")) {
            List<Keyword> kwList = keywordController.findAll().stream()
                .filter(k -> selectedConcept == null || (k.getParentConcept() != null
                    && k.getParentConcept().getId().equals(selectedConcept.getId())))
                .collect(Collectors.toList());
            kwCombo.setItems(FXCollections.observableArrayList(kwList));
            kwCombo.setPromptText("Parent Keyword...");
            kwCombo.setPrefWidth(180);
            kwCombo.setCellFactory(lv -> new ListCell<>() {
                @Override protected void updateItem(Keyword item, boolean empty) {
                    super.updateItem(item, empty); setText(empty || item == null ? null : item.getValue());
                }
            });
            kwCombo.setButtonCell(new ListCell<>() {
                @Override protected void updateItem(Keyword item, boolean empty) {
                    super.updateItem(item, empty); setText(empty || item == null ? null : item.getValue());
                }
            });
            if (parentForNew instanceof Keyword kw) kwCombo.setValue(kw);
            else if (existing instanceof SubKeyword sk && sk.getParentKeyword() != null)
                kwList.stream().filter(k -> k.getId().equals(sk.getParentKeyword().getId())).findFirst().ifPresent(kwCombo::setValue);
            if (kwCombo.getValue() == null && selectedKeyword != null) kwCombo.setValue(selectedKeyword);

            kwCombo.setOnKeyPressed(ke -> {
                switch (ke.getCode()) {
                    case ENTER -> saveBtn.fire();
                    case DELETE -> { if (deleteBtn2 != null) deleteBtn2.fire(); }
                    default -> {}
                }
            });
        }

        Button saveBtn = new Button("💾 Save");
        Button deleteBtn2 = existing != null ? new Button("🗑 Delete") : null;

        field.setOnKeyPressed(ke -> {
            switch (ke.getCode()) {
                case ENTER -> saveBtn.fire();
                case DELETE -> { if (deleteBtn2 != null) deleteBtn2.fire(); }
                default -> {}
            }
        });
        saveBtn.setOnAction(e -> {
            String val = field.getText().trim();
            if (val.isEmpty()) return;
            switch (type) {
                case "concept" -> {
                    Concept c = new Concept(); c.setValue(val);
                    Concept saved = conceptController.create(c);
                    selectedConcept = saved;
                    conceptListView.setItems(FXCollections.observableArrayList(conceptController.findAll()));
                    conceptListView.getSelectionModel().select(saved);
                }
                case "concept-edit" -> {
                    Concept c = (Concept) existing; c.setValue(val);
                    conceptController.update(c.getId(), c);
                    selectedConcept = c;
                    conceptListView.setItems(FXCollections.observableArrayList(conceptController.findAll()));
                    conceptListView.getSelectionModel().select(c);
                }
                case "keyword" -> {
                    Keyword k = new Keyword(); k.setValue(val);
                    k.setParentConcept((Concept) parentForNew);
                    keywordController.create(k);
                }
                case "keyword-edit" -> {
                    Keyword k = (Keyword) existing; k.setValue(val);
                    keywordController.update(k.getId(), k);
                }
                case "subkeyword" -> {
                    Keyword parent = kwCombo.getValue();
                    if (parent == null) return;
                    SubKeyword sk = new SubKeyword(); sk.setValue(val);
                    sk.setParentKeyword(parent);
                    subKeywordController.create(sk);
                }
                case "subkeyword-edit" -> {
                    SubKeyword sk = (SubKeyword) existing; sk.setValue(val);
                    if (kwCombo.getValue() != null) sk.setParentKeyword(kwCombo.getValue());
                    subKeywordController.update(sk.getId(), sk);
                }
            }
            editFormBar.getChildren().clear();
            editFormBar.getStyleClass().clear();
            refreshRight();
        });

        Button cancelBtn = new Button("✖ Cancel");
        cancelBtn.setOnAction(e -> { editFormBar.getChildren().clear(); editFormBar.getStyleClass().clear(); });

        HBox row = new HBox(10, formLabel, field);
        if (type.equals("subkeyword") || type.equals("subkeyword-edit")) row.getChildren().add(kwCombo);
        row.getChildren().addAll(saveBtn, cancelBtn);
        row.setAlignment(Pos.CENTER_LEFT);

        if (existing != null) {
            deleteBtn2.getStyleClass().add("button-danger");
            deleteBtn2.setOnAction(e -> {
                if (existing instanceof Concept c) {
                    keywordController.findAll().stream()
                        .filter(k -> k.getParentConcept() != null && k.getParentConcept().getId().equals(c.getId()))
                        .forEach(k -> { k.setParentConcept(null); keywordController.update(k.getId(), k); });
                    conceptController.delete(c.getId());
                    selectedConcept = null;
                    conceptListView.setItems(FXCollections.observableArrayList(conceptController.findAll()));
                } else if (existing instanceof Keyword k) {
                    subKeywordController.findAll().stream()
                        .filter(sk -> sk.getParentKeyword() != null && sk.getParentKeyword().getId().equals(k.getId()))
                        .forEach(sk -> { sk.setParentKeyword(null); subKeywordController.update(sk.getId(), sk); });
                    keywordController.delete(k.getId());
                } else if (existing instanceof SubKeyword sk) {
                    subKeywordController.delete(sk.getId());
                }
                editFormBar.getChildren().clear();
                editFormBar.getStyleClass().clear();
                refreshRight();
            });
            row.getChildren().add(deleteBtn2);
        }

        editFormBar.getChildren().add(row);
    }
}
