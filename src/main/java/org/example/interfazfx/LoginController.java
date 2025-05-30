package org.example.interfazfx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    private DataBase dataBase = new DataBase();

    @FXML
    private Button exitButton;

    @FXML
    private Button minimizeButton;

    @FXML
    private Label registerLabel1;

    @FXML
    private Button loginButton;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passField;

    @FXML
    private Label errorLogin;

    @FXML
    protected void initialize() {
        // Configuración del botón de salida
        exitButton.setOnAction(event -> {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(0), new KeyValue(stage.opacityProperty(), 1.0)),
                    new KeyFrame(Duration.seconds(0.1), new KeyValue(stage.opacityProperty(), 0.0)));
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
            if (stage != null) {
                stage.iconifiedProperty().addListener((obs, wasMinimized, isNowMinimized) -> {
                    if (!isNowMinimized) {
                        Timeline timeline = new Timeline(
                                new KeyFrame(Duration.seconds(0), new KeyValue(stage.opacityProperty(), 0.0)),
                                new KeyFrame(Duration.seconds(0.1), new KeyValue(stage.opacityProperty(), 1.0)));
                        timeline.play();
                    }
                });

                minimizeButton.setOnAction(event -> {
                    Timeline timeline = new Timeline(
                            new KeyFrame(Duration.seconds(0), new KeyValue(stage.opacityProperty(), 1.0)),
                            new KeyFrame(Duration.seconds(0.1), new KeyValue(stage.opacityProperty(), 0.0)));
                    timeline.setOnFinished(e -> stage.setIconified(true));
                    timeline.play();
                });
            }
        });

        // Configuración del botón de inicio de sesión
        loginButton.setOnMouseEntered(event -> loginButton.setCursor(Cursor.HAND));
        registerLabel1.setOnMouseEntered(event -> registerLabel1.setCursor(Cursor.HAND));

        Platform.runLater(() -> emailField.requestFocus());

        // Configuración del enlace para ir a la vista de registro
        registerLabel1.setOnMouseClicked(event -> {
            try {
                AppInitializer appInitializer = new AppInitializer();
                Stage stage = (Stage) registerLabel1.getScene().getWindow();
                appInitializer.changeScene(stage, "registerView.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        loginButton.setOnAction(event -> login());
        emailField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                login();
            }
        });
        passField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                login();
            }
        });

    }

    private void login() {
        try {
            if (emailField.getText().equals("admin@edu.uah.es") && passField.getText().equals("admin")) {
                AppInitializer appInitializer = new AppInitializer();
                Stage stage = (Stage) loginButton.getScene().getWindow();
                appInitializer.changeScene(stage, "libraryAdmin.fxml");
            } else if (dataBase.userExists(emailField.getText(), passField.getText())) {
                String username = dataBase.getUsername(emailField.getText());
                App.currentUsername = username;
                AppInitializer appInitializer = new AppInitializer();
                Stage stage = (Stage) loginButton.getScene().getWindow();
                appInitializer.changeScene(stage, "libraryUser.fxml");
            } else {
                errorLogin.setVisible(true);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}