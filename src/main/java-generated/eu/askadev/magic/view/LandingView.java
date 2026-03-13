package eu.askadev.magic.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("App GenIA - Entity Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button authorBtn = createButton("Manage Authors", authorListView);
        Button conceptBtn = createButton("Manage Concepts", conceptListView);
        Button keywordBtn = createButton("Manage Keywords", keywordListView);
        Button languageBtn = createButton("Manage Languages", languageListView);
        Button manuscriptBtn = createButton("Manage Manuscripts", manuscriptListView);
        Button nameBtn = createButton("Manage Names", nameListView);
        Button publicationBtn = createButton("Manage Publications", publicationListView);
        Button variantBtn = createButton("Manage Variants", variantListView);

        root.getChildren().addAll(title, authorBtn, conceptBtn, keywordBtn, languageBtn,
                manuscriptBtn, nameBtn, publicationBtn, variantBtn);

        return root;
    }

    private Button createButton(String text, Object listView) {
        Button button = new Button(text);
        button.setPrefWidth(250);
        button.setOnAction(e -> navigateToListView(listView));
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
