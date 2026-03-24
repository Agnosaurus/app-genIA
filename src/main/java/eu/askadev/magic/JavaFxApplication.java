package eu.askadev.magic;

import eu.askadev.magic.service.PersistenceService;
import eu.askadev.magic.service.LocalizationService;
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
        try {
            springContext = new SpringApplicationBuilder(AppGenIAApplication.class)
                    .headless(false)
                    .run();

            // Add shutdown hook to ensure clean exit
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("INFO: Shutdown hook triggered");
                if (springContext != null && springContext.isActive()) {
                    try {
                        springContext.close();
                    } catch (Exception e) {
                        System.err.println("ERROR: Error closing context in shutdown hook: " + e.getMessage());
                    }
                }
            }));
        } catch (Exception e) {
            System.err.println("FATAL: Error initializing Spring context:");
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize application", e);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Check if another instance is already running
            if (!SingleInstanceManager.acquireLock()) {
                // Another instance is running
                System.out.println("INFO: Another instance is already running");
                showAlreadyRunningDialog();
                Platform.exit();
                return;
            }

            System.out.println("INFO: Loading data from file...");
            PersistenceService persistenceService = springContext.getBean(PersistenceService.class);
            persistenceService.loadFromFile();
            System.out.println("INFO: Data loaded successfully");

            primaryStage.setOnCloseRequest(e -> {
                System.out.println("INFO: Window close requested");

                // Save data in background thread
                new Thread(() -> {
                    try {
                        System.out.println("INFO: Saving data...");
                        persistenceService.saveToFile();
                        System.out.println("INFO: Data saved successfully");
                    } catch (Exception ex) {
                        System.err.println("ERROR: Error saving data on window close: " + ex.getMessage());
                        ex.printStackTrace();
                    }

                    try {
                        SingleInstanceManager.releaseLock();
                    } catch (Exception ex) {
                        System.err.println("ERROR: Error releasing lock on window close: " + ex.getMessage());
                        ex.printStackTrace();
                    }

                    try {
                        springContext.close();
                    } catch (Exception ex) {
                        System.err.println("ERROR: Error closing context on window close: " + ex.getMessage());
                        ex.printStackTrace();
                    }

                    Platform.exit();
                }).start();
            });

            System.out.println("INFO: Creating landing view...");
            LandingView landingView = springContext.getBean(LandingView.class);
            landingView.setStage(primaryStage);

            System.out.println("INFO: Creating scene...");
            Scene scene = new Scene(landingView.getView(), 1200, 800);

            // Load CSS stylesheet
            System.out.println("INFO: Loading CSS stylesheet...");
            String stylesheet = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(stylesheet);
            System.out.println("INFO: CSS stylesheet loaded");

            primaryStage.setTitle("M.A.G.I.C - Morgana's Archive for Gathering, Indexing, and Cataloging");
            primaryStage.setScene(scene);

            // Maximize the window
            primaryStage.setWidth(1400);
            primaryStage.setHeight(900);
            primaryStage.centerOnScreen();
            primaryStage.setMaximized(true);

            System.out.println("INFO: Displaying window...");
            primaryStage.show();
            System.out.println("INFO: Application started successfully");
        } catch (Exception e) {
            System.err.println("FATAL: Error starting application:");
            e.printStackTrace();
            showErrorDialog(e);
            Platform.exit();
        }
    }

    private void showAlreadyRunningDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Application Already Running");
        alert.setHeaderText("Application is Already Running");
        alert.setContentText("Another instance of App GenIA is already running.\n\n" +
                "Please close the existing instance before launching a new one.");
        alert.showAndWait();
    }

    private void showErrorDialog(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Application Startup Error");
        alert.setHeaderText("Failed to Start M.A.G.I.C Application");
        alert.setContentText("An error occurred while starting the application:\n\n" +
                e.getClass().getSimpleName() + ": " + e.getMessage() + "\n\n" +
                "Check the console for detailed error information.");
        alert.showAndWait();
    }

    @Override
    public void stop() {
        System.out.println("INFO: Shutting down application...");

        // Release lock FIRST before anything else
        try {
            System.out.println("INFO: Releasing lock...");
            SingleInstanceManager.releaseLock();
            System.out.println("INFO: Lock released successfully");
        } catch (Exception e) {
            System.err.println("ERROR: Error releasing lock: " + e.getMessage());
            e.printStackTrace();
        }

        // Save data
        if (springContext != null && springContext.isActive()) {
            try {
                System.out.println("INFO: Saving data before shutdown...");
                PersistenceService persistenceService = springContext.getBean(PersistenceService.class);
                persistenceService.saveToFile();
                System.out.println("INFO: Data saved successfully");
            } catch (Exception e) {
                System.err.println("ERROR: Error saving on shutdown: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Close Spring context in a separate thread with timeout
        new Thread(() -> {
            if (springContext != null && springContext.isActive()) {
                try {
                    System.out.println("INFO: Closing Spring context...");
                    springContext.close();
                    System.out.println("INFO: Spring context closed");
                } catch (Exception e) {
                    System.err.println("ERROR: Error closing Spring context: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("INFO: Application shutdown complete");
            System.exit(0);
        }).start();
    }
}
