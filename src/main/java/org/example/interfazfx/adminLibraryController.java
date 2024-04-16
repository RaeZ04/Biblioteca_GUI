package org.example.interfazfx;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class adminLibraryController {

    private DataBase dataBase = new DataBase();

    @FXML
    private Button exitButton;

    @FXML
    private Button minimizeButton;

    @FXML
    private TextField nombrefield;

    @FXML 
    private TextField autorfield;

    @FXML
    private TextField editorialfield;

    @FXML
    private TextField isbnfield;

    @FXML
    private TextField cantfield;

    @FXML
    private Button añadirlibrobutton;

    @FXML
    private Button eliminarlibrobutton;

    @FXML
    private TextField isbnfieldeliminar;


    @FXML
    protected void initialize() {

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


        añadirlibrobutton.setOnAction(event -> {
            String titulo = nombrefield.getText();
            String autor = autorfield.getText();
            String editorial = editorialfield.getText();
            String isbn = isbnfield.getText();
            int cantidad = Integer.parseInt(cantfield.getText());

            try {
                dataBase.insertarLibro(titulo, autor, editorial, isbn, cantidad);
            } catch (SQLException e) {
                // Manejar la excepción
                e.printStackTrace();
            }

            // Limpiar los campos de texto después de insertar el libro
            nombrefield.clear();
            autorfield.clear();
            editorialfield.clear();
            isbnfield.clear();
            cantfield.clear();
        });

        eliminarlibrobutton.setOnAction(event -> {
            String isbn = isbnfieldeliminar.getText();
        
            try {
                dataBase.eliminarLibro(isbn);
            } catch (SQLException e) {
                // Handle the exception
                e.printStackTrace();
            }
        
            // Clear the isbnfield after deleting the book
            isbnfieldeliminar.clear();
        });

    }

}

