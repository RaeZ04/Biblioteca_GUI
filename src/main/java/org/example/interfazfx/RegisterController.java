package org.example.interfazfx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.sql.SQLIntegrityConstraintViolationException;

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
    private Label errorReg;

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

        registerButton.setOnMouseClicked(event -> {
            try {
                if (usernameField.getText().isEmpty() ||  passField.getText().isEmpty()) {

                    errorReg.setText("Tienes que rellenar todos los campos");
                    errorReg.setVisible(true);
                } else {
                    dataBase.insertUser(usernameField, emailField, passField);
        
                    Stage primaryStage = (Stage) registerButton.getScene().getWindow();
                
                    Stage dialog = new Stage();
                    dialog.initModality(Modality.APPLICATION_MODAL);
                    dialog.setTitle("Registro exitoso");
                    dialog.setResizable(false); 
                    
                    // Cambiar el icono de la ventana
                    Image icon = new Image(getClass().getResourceAsStream("media/uah.png"));
                    dialog.getIcons().add(icon);
                    
                    Label label1 = new Label("Te has registrado correctamente.");
                    label1.setFont(new Font("Arial", 14)); // Aumentar el tamaño de la fuente
                    Label label2 = new Label("Email asociado: " + usernameField.getText() + "@edu.uah.es");
                    label2.setFont(new Font("Arial", 14)); // Aumentar el tamaño de la fuente
                    Button closeButton = new Button("Aceptar");
                    closeButton.setOnAction(e -> dialog.close());
                
                    Region spacer = new Region(); 
                    spacer.setMinHeight(10); 
                    
                    VBox layout = new VBox(10);
                    layout.getChildren().addAll(label1, label2, spacer, closeButton);
                    layout.setAlignment(Pos.CENTER);
                    
                    Scene dialogScene = new Scene(layout, 300, 150);
                    dialog.setScene(dialogScene);
                
                    // Posicionar la ventana emergente en el centro de la ventana actual
                    dialog.setX(primaryStage.getX() + (primaryStage.getWidth() - dialogScene.getWidth()) / 2);
                    dialog.setY(primaryStage.getY() + (primaryStage.getHeight() - dialogScene.getHeight()) / 2);
                
                    dialog.show();
        
                    AppInitializer appInitializer = new AppInitializer();
                    Stage stage = (Stage) loginLabel1.getScene().getWindow();
                    appInitializer.changeScene(stage, "loginView.fxml");
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                errorReg.setText("El usuario que has introducido ya existe");
                errorReg.setVisible(true);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }
}