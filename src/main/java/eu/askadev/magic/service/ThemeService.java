package eu.askadev.magic.service;

import javafx.scene.Scene;
import org.springframework.stereotype.Service;

@Service
public class ThemeService {

    private boolean darkMode = false;
    private Scene scene;

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void toggle() {
        darkMode = !darkMode;
        apply();
    }

    public void apply() {
        if (scene == null) return;
        String darkCss = getClass().getResource("/dark.css").toExternalForm();
        if (darkMode) {
            if (!scene.getStylesheets().contains(darkCss)) {
                scene.getStylesheets().add(darkCss);
            }
        } else {
            scene.getStylesheets().remove(darkCss);
        }
    }
}
