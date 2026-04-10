package eu.askadev.magic.view;

import eu.askadev.magic.service.LocalizationService;
import eu.askadev.magic.service.PublicationImportService;
import eu.askadev.magic.service.ThemeService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LandingView {

    private final AuthorListView authorListView;
    private final ConceptKeywordView conceptKeywordView;
    private final LanguageListView languageListView;
    private final ManuscriptListView manuscriptListView;
    private final NameListView nameListView;
    private final PublicationListView publicationListView;
    private final VariantListView variantListView;
    private final PublicationImportService publicationImportService;
    private final ThemeService themeService;
    private Stage stage;

    public LandingView(AuthorListView authorListView, ConceptKeywordView conceptKeywordView,
                       LanguageListView languageListView,
                       ManuscriptListView manuscriptListView, NameListView nameListView,
                       PublicationListView publicationListView, VariantListView variantListView,
                       PublicationImportService publicationImportService, ThemeService themeService) {
        this.authorListView = authorListView;
        this.conceptKeywordView = conceptKeywordView;
        this.languageListView = languageListView;
        this.manuscriptListView = manuscriptListView;
        this.nameListView = nameListView;
        this.publicationListView = publicationListView;
        this.variantListView = variantListView;
        this.publicationImportService = publicationImportService;
        this.themeService = themeService;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Parent getView() {
        LocalizationService i18n = LocalizationService.getInstance();

        VBox root = new VBox(20);
        root.setPadding(new Insets(0));

        // M.A.G.I.C Header
        VBox magicHeader = new VBox(3);
        magicHeader.getStyleClass().add("magic-header");

        HBox headerTop = new HBox();
        headerTop.setAlignment(Pos.TOP_RIGHT);
        headerTop.setSpacing(10);
        headerTop.setPadding(new Insets(0, 0, 5, 0));

        Button enBtn = new Button("🇬🇧 English");
        Button frBtn = new Button("🇫🇷 Français");
        enBtn.getStyleClass().add(i18n.isEnglish() ? "lang-btn-active" : "lang-btn");
        frBtn.getStyleClass().add(i18n.isFrench() ? "lang-btn-active" : "lang-btn");

        enBtn.setOnAction(e -> { i18n.switchToEnglish(); stage.getScene().setRoot(getView()); });
        frBtn.setOnAction(e -> { i18n.switchToFrench(); stage.getScene().setRoot(getView()); });

        Button darkModeBtn = new Button(themeService.isDarkMode() ? "☀️ Light" : "🌙 Dark");
        darkModeBtn.getStyleClass().add("lang-btn");
        darkModeBtn.setOnAction(e -> {
            themeService.toggle();
            stage.getScene().setRoot(getView());
        });

        headerTop.getChildren().addAll(darkModeBtn, enBtn, frBtn);

        VBox headerContent = new VBox(5);
        headerContent.setAlignment(Pos.CENTER_LEFT);
        Label magicTitle = new Label("M.A.G.I.C");
        magicTitle.getStyleClass().add("magic-title");
        Label magicAcronym = new Label("Morgana's Archive for Gathering, Indexing, and Cataloging");
        magicAcronym.getStyleClass().add("magic-acronym");
        Label magicSubtitle = new Label("Data Management System");
        magicSubtitle.getStyleClass().add("magic-subtitle");
        headerContent.getChildren().addAll(magicTitle, magicAcronym, magicSubtitle);
        magicHeader.getChildren().addAll(headerTop, headerContent);

        // Main content
        HBox splitContent = new HBox(30);
        splitContent.setPadding(new Insets(30));
        splitContent.getStyleClass().add("landing-content");
        splitContent.setPrefHeight(400);

        // Left: entity grid
        VBox leftZone = new VBox(10);
        leftZone.getStyleClass().add("landing-zone");
        Label welcome = new Label(i18n.get("landing.dashboard"));
        welcome.getStyleClass().add("section-title-large");
        Label subtitle = new Label(i18n.get("landing.select"));
        subtitle.getStyleClass().add("section-subtitle");

        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(15);
        buttonGrid.setVgap(15);
        buttonGrid.setPadding(new Insets(20));
        buttonGrid.getStyleClass().add("button-grid");

        Button authorBtn = createStyledButton("👤\n" + i18n.get("entity.authors"));
        Button conceptKeywordBtn = createStyledButton("💡\n" + i18n.get("entity.concepts_keywords"));
        Button languageBtn = createStyledButton("🌐\n" + i18n.get("entity.languages"));
        Button manuscriptBtn = createStyledButton("📄\n" + i18n.get("entity.manuscripts"));
        Button nameBtn = createStyledButton("📝\n" + i18n.get("entity.names"));
        Button publicationBtn = createStyledButton("📚\n" + i18n.get("entity.publications"));
        Button variantBtn = createStyledButton("🔀\n" + i18n.get("entity.variants"));

        authorBtn.setOnAction(e -> navigateToListView(authorListView));
        conceptKeywordBtn.setOnAction(e -> stage.getScene().setRoot(conceptKeywordView.getView(this)));
        languageBtn.setOnAction(e -> navigateToListView(languageListView));
        manuscriptBtn.setOnAction(e -> navigateToListView(manuscriptListView));
        nameBtn.setOnAction(e -> navigateToListView(nameListView));
        publicationBtn.setOnAction(e -> navigateToListView(publicationListView));
        variantBtn.setOnAction(e -> navigateToListView(variantListView));

        buttonGrid.add(authorBtn, 0, 0); buttonGrid.add(conceptKeywordBtn, 1, 0);
        buttonGrid.add(languageBtn, 0, 1); buttonGrid.add(manuscriptBtn, 1, 1); buttonGrid.add(nameBtn, 2, 1);
        buttonGrid.add(publicationBtn, 0, 2); buttonGrid.add(variantBtn, 1, 2);

        leftZone.getChildren().addAll(welcome, subtitle, buttonGrid);
        VBox.setVgrow(buttonGrid, Priority.ALWAYS);
        VBox.setVgrow(leftZone, Priority.ALWAYS);

        // Right: import
        VBox rightZone = new VBox(10);
        rightZone.setAlignment(Pos.TOP_CENTER);
        rightZone.getStyleClass().add("landing-zone");
        Label importTitle = new Label(i18n.get("import.references.title"));
        importTitle.getStyleClass().add("import-title");
        Button importBtn = new Button(i18n.get("button.import.references"));
        importBtn.getStyleClass().add("import-btn");
        importBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(i18n.get("import.references.dialog.title"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    publicationImportService.importCsv(file);
                    new Alert(Alert.AlertType.INFORMATION, i18n.get("import.references.success"), ButtonType.OK).showAndWait();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, i18n.get("import.references.error") + "\n" + ex.getMessage(), ButtonType.OK).showAndWait();
                }
            }
        });
        Button configBtn = new Button(i18n.get("button.config"));
        configBtn.getStyleClass().add("import-btn");
        configBtn.setOnAction(e -> stage.getScene().setRoot(buildEmptyPanel(i18n.get("button.config"))));

        Button aboutBtn = new Button(i18n.get("button.about"));
        aboutBtn.getStyleClass().add("import-btn");
        aboutBtn.setOnAction(e -> stage.getScene().setRoot(buildEmptyPanel(i18n.get("button.about"))));

        rightZone.getChildren().addAll(importTitle, importBtn, configBtn, aboutBtn);
        rightZone.setMinWidth(350);
        rightZone.setMaxWidth(350);
        VBox.setVgrow(rightZone, Priority.ALWAYS);

        splitContent.getChildren().addAll(leftZone, rightZone);
        HBox.setHgrow(leftZone, Priority.ALWAYS);
        splitContent.setFillHeight(true);

        root.getChildren().addAll(magicHeader, splitContent);
        return root;
    }

    private javafx.scene.Parent buildEmptyPanel(String title) {
        LocalizationService i18n = LocalizationService.getInstance();
        javafx.scene.control.Label label = new javafx.scene.control.Label(title);
        label.getStyleClass().add("section-title-large");
        Button backBtn = new Button(i18n.get("button.back"));
        backBtn.setOnAction(e -> stage.getScene().setRoot(getView()));
        VBox panel = new VBox(20, label, backBtn);
        panel.setPadding(new Insets(30));
        return panel;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(150);
        button.setPrefHeight(100);
        button.getStyleClass().add("entity-btn");
        button.setWrapText(true);
        return button;
    }

    private void navigateToListView(Object listView) {
        Parent view = null;
        if (listView instanceof AuthorListView) view = ((AuthorListView) listView).getView(this);
        else if (listView instanceof LanguageListView) view = ((LanguageListView) listView).getView(this);
        else if (listView instanceof ManuscriptListView) view = ((ManuscriptListView) listView).getView(this);
        else if (listView instanceof NameListView) view = ((NameListView) listView).getView(this);
        else if (listView instanceof PublicationListView) view = ((PublicationListView) listView).getView(this);
        else if (listView instanceof VariantListView) view = ((VariantListView) listView).getView(this);

        if (view != null && stage != null) {
            stage.getScene().setRoot(view);
        }
    }
}
