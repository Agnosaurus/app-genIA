package eu.askadev.magic;

import eu.askadev.magic.service.PersistenceService;
import eu.askadev.magic.view.LandingView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(AppGenIAApplication.class)
                .headless(false)
                .run();
    }

    @Override
    public void start(Stage primaryStage) {
        PersistenceService persistenceService = springContext.getBean(PersistenceService.class);
        persistenceService.loadFromFile();
        
        primaryStage.setOnCloseRequest(e -> {
            persistenceService.saveToFile();
            springContext.close();
            Platform.exit();
        });
        
        LandingView landingView = springContext.getBean(LandingView.class);
        landingView.setStage(primaryStage);
        Scene scene = new Scene(landingView.getView(), 800, 600);
        primaryStage.setTitle("App GenIA - CRUD Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (springContext != null && springContext.isActive()) {
            PersistenceService persistenceService = springContext.getBean(PersistenceService.class);
            persistenceService.saveToFile();
            springContext.close();
        }
        Platform.exit();
    }
}
