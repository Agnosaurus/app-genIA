package eu.askadev.magic.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
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
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Header
        Label title = new Label("App GenIA - Entity Management");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

        Label subtitle = new Label("Select an entity to manage");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666; -fx-padding: 0 0 20 0;");

        // Button grid
        FlowPane buttonGrid = new FlowPane();
        buttonGrid.setHgap(15);
        buttonGrid.setVgap(15);
        buttonGrid.setPrefWrapLength(500);
        buttonGrid.setAlignment(Pos.CENTER_LEFT);
        buttonGrid.setStyle("-fx-padding: 20; -fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 4;");

        Button authorBtn = createStyledButton("👤 Authors");
        Button conceptBtn = createStyledButton("💡 Concepts");
        Button keywordBtn = createStyledButton("🏷️ Keywords");
        Button languageBtn = createStyledButton("🌐 Languages");
        Button manuscriptBtn = createStyledButton("📄 Manuscripts");
        Button nameBtn = createStyledButton("📝 Names");
        Button publicationBtn = createStyledButton("📚 Publications");
        Button variantBtn = createStyledButton("🔀 Variants");

        authorBtn.setOnAction(e -> navigateToListView(authorListView));
        conceptBtn.setOnAction(e -> navigateToListView(conceptListView));
        keywordBtn.setOnAction(e -> navigateToListView(keywordListView));
        languageBtn.setOnAction(e -> navigateToListView(languageListView));
        manuscriptBtn.setOnAction(e -> navigateToListView(manuscriptListView));
        nameBtn.setOnAction(e -> navigateToListView(nameListView));
        publicationBtn.setOnAction(e -> navigateToListView(publicationListView));
        variantBtn.setOnAction(e -> navigateToListView(variantListView));

        buttonGrid.getChildren().addAll(authorBtn, conceptBtn, keywordBtn, languageBtn,
                manuscriptBtn, nameBtn, publicationBtn, variantBtn);

        root.getChildren().addAll(title, subtitle, buttonGrid);
        return root;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(120);
        button.setPrefHeight(60);
        button.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12px 20px; " +
            "-fx-background-color: #0078d4; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-border-radius: 4; " +
            "-fx-cursor: hand;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12px 20px; " +
            "-fx-background-color: #106ebe; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-border-radius: 4; " +
            "-fx-cursor: hand;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12px 20px; " +
            "-fx-background-color: #0078d4; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-border-radius: 4; " +
            "-fx-cursor: hand;"
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
