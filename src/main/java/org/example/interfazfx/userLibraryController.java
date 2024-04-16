package org.example.interfazfx;

import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class userLibraryController {

    @FXML
    private Button exitButton;

    @FXML
    private Button minimizeButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private ImageView logout;

    @FXML
    private ImageView lupa;

    @FXML
    private Button devolverlibrobutton;

    @FXML
    private Button reservarlibrobutton;

    @FXML
    public void initialize() {

        usernameLabel.setText(App.currentUsername);

        // Configuración del botón de salida
        exitButton.setOnAction(event -> {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(0), new KeyValue(stage.opacityProperty(), 1.0)),
                    new KeyFrame(Duration.seconds(0.1), new KeyValue(stage.opacityProperty(), 0.0))
            );
            timeline.setOnFinished(e -> {
                new Thread(() -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    Platform.runLater(() -> System.exit(0));
                }).start();
            });
            timeline.play();
        });

        // Configuración del botón de minimizar
        Platform.runLater(() -> {
            Stage stage = (Stage) minimizeButton.getScene().getWindow();

            stage.iconifiedProperty().addListener((obs, wasMinimized, isNowMinimized) -> {
                if (!isNowMinimized) {
                    Timeline timeline = new Timeline(
                            new KeyFrame(Duration.seconds(0), new KeyValue(stage.opacityProperty(), 0.0)),
                            new KeyFrame(Duration.seconds(0.1), new KeyValue(stage.opacityProperty(), 1.0))
                    );
                    timeline.play();
                }
            });

            minimizeButton.setOnAction(event -> {
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.seconds(0), new KeyValue(stage.opacityProperty(), 1.0)),
                        new KeyFrame(Duration.seconds(0.1), new KeyValue(stage.opacityProperty(), 0.0))
                );
                timeline.setOnFinished(e -> stage.setIconified(true));
                timeline.play();
            });
        });

        // Configuración del botón de cierre de sesión

        logout.setOnMouseClicked(event -> {

            AppInitializer appInitializer = new AppInitializer();
            try {
                appInitializer.changeScene((Stage) logout.getScene().getWindow(), "loginView.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        // Mostrar mano en todos los botones

        lupa.setOnMouseEntered(event -> lupa.setCursor(Cursor.HAND));
        logout.setOnMouseEntered(event -> logout.setCursor(Cursor.HAND));
        devolverlibrobutton.setOnMouseEntered(event -> devolverlibrobutton.setCursor(Cursor.HAND));
        reservarlibrobutton.setOnMouseEntered(event -> reservarlibrobutton.setCursor(Cursor.HAND));

    }

}
