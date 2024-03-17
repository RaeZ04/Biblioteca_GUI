package org.example.interfazfx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterController {

    private DataBase dataBase = new DataBase();

    @FXML
    private Button exitButton;

    @FXML
    private Button minimizeButton;

    @FXML
    private Label loginLabel1;

    @FXML
    private Button registerButton;

    @FXML
    private TextField passField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    protected void initialize() {
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

        // Configuración del botón de inicio de sesión
        registerButton.setOnMouseEntered(event -> registerButton.setCursor(Cursor.HAND));
        loginLabel1.setOnMouseEntered(event -> loginLabel1.setCursor(Cursor.HAND));

        Platform.runLater(() -> usernameField.requestFocus());

        // Configuración del enlace para volver a la vista de inicio de sesión
        loginLabel1.setOnMouseClicked(event -> {
            try {
                AppInitializer appInitializer = new AppInitializer();
                Stage stage = (Stage) loginLabel1.getScene().getWindow();
                appInitializer.changeScene(stage, "loginView.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        registerButton.setOnMouseClicked(event ->{

            try {
                dataBase.insertUser(usernameField, emailField, passField);
            }catch (SQLException e){
                e.printStackTrace();
            };

        });

    }
}