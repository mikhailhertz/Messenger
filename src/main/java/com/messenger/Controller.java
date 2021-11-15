package com.messenger;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Controller implements Initializable {
    @FXML
    TextArea textArea;

    double dragX;
    double dragY;
    boolean dragging;

    @FXML
    void titlebarMousePressed(MouseEvent event) {
        dragging = true;
        dragX = event.getScreenX();
        dragY = event.getScreenY();
    }

    @FXML
    void titlebarMouseDragged(MouseEvent event) {
        Window stage = ((Node) event.getSource()).getScene().getWindow();
        stage.setX(stage.getX() + (event.getScreenX() - dragX));
        stage.setY(stage.getY() + (event.getScreenY() - dragY));
        dragX = event.getScreenX();
        dragY = event.getScreenY();
    }

    @FXML
    void titlebarMouseReleased(MouseEvent event) {
        dragging = false;
    }

    Cursor getResizeDirection(double x, double y, double w, double h) {
        int n = 9;
        if (x < n && y > h - n) {
            return Cursor.SW_RESIZE;
        } else if (x < n && y > n && y < h - n) {
            return Cursor.W_RESIZE;
        } else if (x < n && y < n) {
            return Cursor.NW_RESIZE;
        } else if (x > n && x < w - n && y < h - n) {
            return Cursor.N_RESIZE;
        } else if (x > w - n && y < n) {
            return Cursor.NE_RESIZE;
        } else if (x > w - n && y > n && y < h - n) {
            return Cursor.E_RESIZE;
        } else if (x > w - n && y > h - n) {
            return Cursor.SE_RESIZE;
        } else if (x > n && x < w - n && y > h - n) {
            return Cursor.S_RESIZE;
        } else {
            return Cursor.DEFAULT;
        }
    }

    boolean insideBorder;
    Cursor cursor;
    double resizeX;
    double resizeY;

    @FXML
    void borderMouseEntered(MouseEvent event) {
        insideBorder = true;
    }

    @FXML
    void borderMouseExited(MouseEvent event) {
        Scene scene = ((Node) event.getSource()).getScene();
        insideBorder = false;
        scene.setCursor(Cursor.DEFAULT);
    }

    @FXML
    void vBoxMouseEntered(MouseEvent event) {
        Scene scene = ((Node) event.getSource()).getScene();
        insideBorder = false;
        scene.setCursor(Cursor.DEFAULT);
    }

    @FXML
    void vBoxMouseExited(MouseEvent event) {
        insideBorder = true;
    }

    @FXML
    void borderMouseMoved(MouseEvent event) {
        if (insideBorder && !dragging) {
            Scene scene = ((Node) event.getSource()).getScene();
            cursor = getResizeDirection(event.getSceneX(), event.getSceneY(), scene.getWidth(), scene.getHeight());
            scene.setCursor(cursor);
        }
    }

    @FXML
    void borderMousePressed(MouseEvent event) {
        if (insideBorder && !dragging) {
            Scene scene = ((Node) event.getSource()).getScene();
            cursor = getResizeDirection(event.getSceneX(), event.getSceneY(), scene.getWidth(), scene.getHeight());
            resizeX = event.getScreenX();
            resizeY = event.getScreenY();
        }
    }

    @FXML
    void borderMouseDragged(MouseEvent event) {
        Scene scene = ((Node) event.getSource()).getScene();
        Window stage = scene.getWindow();
        double dx = event.getScreenX() - resizeX;
        double dy = event.getScreenY() - resizeY;
        if (cursor == null || cursor == Cursor.DEFAULT || dragging) {
            return;
        } else if (cursor == Cursor.SW_RESIZE) {
            stage.setHeight(stage.getHeight() + dy);
            stage.setX(event.getScreenX() - dx);
            stage.setWidth(stage.getWidth() - dx);
        } else if (cursor == Cursor.W_RESIZE) {
            stage.setX(event.getScreenX() - dx);
            stage.setWidth(stage.getWidth() - dx);
        } else if (cursor == Cursor.NW_RESIZE) {
            stage.setY(event.getScreenY() - dy);
            stage.setHeight(stage.getHeight() - dy);
            stage.setX(event.getScreenX() - dx);
            stage.setWidth(stage.getWidth() - dx);
        } else if (cursor == Cursor.N_RESIZE) {
            stage.setY(event.getScreenY() - dy);
            stage.setHeight(stage.getHeight() - dy);
        } else if (cursor == Cursor.NE_RESIZE) {
            stage.setY(event.getScreenY() - dy);
            stage.setHeight(stage.getHeight() - dy);
            stage.setWidth(stage.getWidth() + dx);
        } else if (cursor == Cursor.E_RESIZE) {
            stage.setWidth(stage.getWidth() + dx);
        } else if (cursor == Cursor.SE_RESIZE) {
            stage.setWidth(stage.getWidth() + dx);
            stage.setHeight(stage.getHeight() + dy);
        } else if (cursor == Cursor.S_RESIZE) {
            stage.setHeight(stage.getHeight() + dy);
        }
        resizeX = event.getScreenX();
        resizeY = event.getScreenY();
    }

    @FXML
    void borderMouseReleased(MouseEvent event) {
        cursor = Cursor.DEFAULT;
        insideBorder = false;
    }

    @FXML
    void closeButtonAction(ActionEvent event) {
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    void sendButtonAction(ActionEvent event) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
