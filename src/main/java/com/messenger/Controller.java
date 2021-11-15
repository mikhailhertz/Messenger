package com.messenger;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    TextField textField;
    @FXML
    TextArea textArea;
    Server server;
    Thread thread;
    Runnable routine = () -> {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                textArea.appendText(server.messages.take() + "\n");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    };

    @FXML
    void sendButtonAction() {
        if (!textField.getText().isEmpty()) {
            server.input(textField.getText());
            textField.setText("");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        server = new Server();
        thread = new Thread(routine);
        thread.start();
    }

    public void terminate(WindowEvent windowEvent) {
        thread.interrupt();
        server.terminate();
    }
}
