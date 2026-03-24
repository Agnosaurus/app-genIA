package eu.askadev.magic;

import eu.askadev.magic.service.PersistenceService;
import eu.askadev.magic.util.SingleInstanceManager;
import eu.askadev.magic.view.LandingView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
        // Check if another instance is already running
        if (!SingleInstanceManager.acquireLock()) {
            // Another instance is running
            showAlreadyRunningDialog();
            Platform.exit();
            return;
        }

        PersistenceService persistenceService = springContext.getBean(PersistenceService.class);
        persistenceService.loadFromFile();
        
        primaryStage.setOnCloseRequest(e -> {
            persistenceService.saveToFile();
            SingleInstanceManager.releaseLock();
            springContext.close();
            Platform.exit();
        });
        
        LandingView landingView = springContext.getBean(LandingView.class);
        landingView.setStage(primaryStage);
        Scene scene = new Scene(landingView.getView(), 800, 600);

        // Load CSS stylesheet
        String stylesheet = getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);

        primaryStage.setTitle("App GenIA - CRUD Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlreadyRunningDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Application Already Running");
        alert.setHeaderText("Application is Already Running");
        alert.setContentText("Another instance of App GenIA is already running.\n\n" +
                "Please close the existing instance before launching a new one.");
        alert.showAndWait();
    }

    @Override
    public void stop() {
        if (springContext != null && springContext.isActive()) {
            PersistenceService persistenceService = springContext.getBean(PersistenceService.class);
            persistenceService.saveToFile();
            springContext.close();
        }
        SingleInstanceManager.releaseLock();
        Platform.exit();
    }
}
