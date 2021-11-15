package com.messenger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// TODO: detect native dark mode (DwmSetWindowAttribute,
// https://stackoverflow.com/questions/51334674/how-to-detect-windows-10-light-dark-mode-in-win32-application,
// https://github.com/Dansoftowner/jSystemThemeDetector/tree/master/src/main/java/com/jthemedetecor)

public class Window extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layout.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        Controller controller = fxmlLoader.getController();
        stage.setOnCloseRequest(controller::terminate);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
