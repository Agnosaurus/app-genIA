package eu.askadev.magic.view;

import eu.askadev.magic.service.LocalizationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class LandingView {

    private final AuthorListView authorListView;
    private final ConceptListView conceptListView;
    private final KeywordListView keywordListView;
    private final LanguageListView languageListView;
    private final ManuscriptListView manuscriptListView;
    private final NameListView nameListView;
    private final PublicationListView publicationListView;
    private final VariantListView variantListView;
    private Stage stage;

    public LandingView(AuthorListView authorListView, ConceptListView conceptListView,
                       KeywordListView keywordListView, LanguageListView languageListView,
                       ManuscriptListView manuscriptListView, NameListView nameListView,
                       PublicationListView publicationListView, VariantListView variantListView) {
        this.authorListView = authorListView;
        this.conceptListView = conceptListView;
        this.keywordListView = keywordListView;
        this.languageListView = languageListView;
        this.manuscriptListView = manuscriptListView;
        this.nameListView = nameListView;
        this.publicationListView = publicationListView;
        this.variantListView = variantListView;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Parent getView() {
        LocalizationService i18n = LocalizationService.getInstance();

        VBox root = new VBox(20);
        root.setPadding(new Insets(0));
        root.setStyle("-fx-background-color: #f5f5f5;");

        // M.A.G.I.C Header with Language Switcher
        VBox magicHeader = new VBox(3);
        magicHeader.setStyle("-fx-background-color: linear-gradient(to right, #0052a3, #0078d4); -fx-padding: 15px 30px;");

        // Top row: Language buttons on right
        HBox headerTop = new HBox();
        headerTop.setAlignment(Pos.TOP_RIGHT);
        headerTop.setSpacing(10);
        headerTop.setPadding(new Insets(0, 0, 5, 0));

        Button enBtn = new Button("🇬🇧 English");
        Button frBtn = new Button("🇫🇷 Français");

        enBtn.setStyle(
            "-fx-font-size: 11px; " +
            "-fx-padding: 6px 12px; " +
            "-fx-background-color: " + (i18n.isEnglish() ? "#ffffff" : "#0078d4") + "; " +
            "-fx-text-fill: " + (i18n.isEnglish() ? "#0078d4" : "#ffffff") + "; " +
            "-fx-border-radius: 3; " +
            "-fx-cursor: hand;"
        );

        frBtn.setStyle(
            "-fx-font-size: 11px; " +
            "-fx-padding: 6px 12px; " +
            "-fx-background-color: " + (i18n.isFrench() ? "#ffffff" : "#0078d4") + "; " +
            "-fx-text-fill: " + (i18n.isFrench() ? "#0078d4" : "#ffffff") + "; " +
            "-fx-border-radius: 3; " +
            "-fx-cursor: hand;"
        );

        enBtn.setOnAction(e -> {
            i18n.switchToEnglish();
            stage.getScene().setRoot(getView());
        });

        frBtn.setOnAction(e -> {
            i18n.switchToFrench();
            stage.getScene().setRoot(getView());
        });

        headerTop.getChildren().addAll(enBtn, frBtn);

        // Main header content
        VBox headerContent = new VBox(5);
        headerContent.setAlignment(Pos.CENTER_LEFT);

        Label magicTitle = new Label("M.A.G.I.C");
        magicTitle.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-letter-spacing: 2;");

        Label magicAcronym = new Label(i18n.get("app.subtitle"));
        magicAcronym.setStyle("-fx-font-size: 10px; -fx-text-fill: #b3d9ff; -fx-font-style: italic;");

        Label magicSubtitle = new Label(i18n.get("app.description"));
        magicSubtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #87ceeb; -fx-padding: 3 0 0 0;");

        headerContent.getChildren().addAll(magicTitle, magicAcronym, magicSubtitle);

        magicHeader.getChildren().addAll(headerTop, headerContent);

        // ...existing code...
        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(30));
        contentArea.setStyle("-fx-background-color: #f5f5f5;");

        Label welcome = new Label(i18n.get("landing.dashboard"));
        welcome.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

        Label subtitle = new Label(i18n.get("landing.select"));
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666; -fx-padding: 0 0 10 0;");

        // Button grid (3x3 matrix)
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(15);
        buttonGrid.setVgap(15);
        buttonGrid.setPadding(new Insets(20));
        buttonGrid.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 4;");

        Button authorBtn = createStyledButton("👤\n" + i18n.get("entity.authors"));
        Button conceptBtn = createStyledButton("💡\n" + i18n.get("entity.concepts"));
        Button keywordBtn = createStyledButton("🏷️\n" + i18n.get("entity.keywords"));
        Button languageBtn = createStyledButton("🌐\n" + i18n.get("entity.languages"));
        Button manuscriptBtn = createStyledButton("📄\n" + i18n.get("entity.manuscripts"));
        Button nameBtn = createStyledButton("📝\n" + i18n.get("entity.names"));
        Button publicationBtn = createStyledButton("📚\n" + i18n.get("entity.publications"));
        Button variantBtn = createStyledButton("🔀\n" + i18n.get("entity.variants"));

        authorBtn.setOnAction(e -> navigateToListView(authorListView));
        conceptBtn.setOnAction(e -> navigateToListView(conceptListView));
        keywordBtn.setOnAction(e -> navigateToListView(keywordListView));
        languageBtn.setOnAction(e -> navigateToListView(languageListView));
        manuscriptBtn.setOnAction(e -> navigateToListView(manuscriptListView));
        nameBtn.setOnAction(e -> navigateToListView(nameListView));
        publicationBtn.setOnAction(e -> navigateToListView(publicationListView));
        variantBtn.setOnAction(e -> navigateToListView(variantListView));

        // Add buttons to grid in 3x3 layout
        buttonGrid.add(authorBtn, 0, 0);
        buttonGrid.add(conceptBtn, 1, 0);
        buttonGrid.add(keywordBtn, 2, 0);
        buttonGrid.add(languageBtn, 0, 1);
        buttonGrid.add(manuscriptBtn, 1, 1);
        buttonGrid.add(nameBtn, 2, 1);
        buttonGrid.add(publicationBtn, 0, 2);
        buttonGrid.add(variantBtn, 1, 2);

        contentArea.getChildren().addAll(welcome, subtitle, buttonGrid);

        root.getChildren().addAll(magicHeader, contentArea);
        return root;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(150);
        button.setPrefHeight(100);
        button.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12px 20px; " +
            "-fx-background-color: #0078d4; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-border-radius: 4; " +
            "-fx-cursor: hand; " +
            "-fx-text-alignment: center; " +
            "-fx-wrap-text: true;"
        );
        button.setWrapText(true);
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12px 20px; " +
            "-fx-background-color: #106ebe; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-border-radius: 4; " +
            "-fx-cursor: hand; " +
            "-fx-text-alignment: center; " +
            "-fx-wrap-text: true;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12px 20px; " +
            "-fx-background-color: #0078d4; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-border-radius: 4; " +
            "-fx-cursor: hand; " +
            "-fx-text-alignment: center; " +
            "-fx-wrap-text: true;"
        ));
        return button;
    }

    private void navigateToListView(Object listView) {
        Parent view = null;
        if (listView instanceof AuthorListView) view = ((AuthorListView) listView).getView(this);
        else if (listView instanceof ConceptListView) view = ((ConceptListView) listView).getView(this);
        else if (listView instanceof KeywordListView) view = ((KeywordListView) listView).getView(this);
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
