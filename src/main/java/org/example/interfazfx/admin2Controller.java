package org.example.interfazfx;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.example.interfazfx.DataBase.Usuario;

public class admin2Controller {

    private DataBase dataBase = new DataBase();

    @FXML
    private Button exitButton;

    @FXML
    private Button minimizeButton;

    @FXML
    private ImageView logout;

    @FXML
    private ListView listaUsuarios;

    @FXML
    private ImageView exportar;

    @FXML
    protected void initialize() {

        logout.setOnMouseEntered(event -> logout.setCursor(Cursor.HAND));
        exportar.setOnMouseEntered(event -> exportar.setCursor(Cursor.HAND));

        exportar.setOnMouseClicked(event -> exportarUsuariosAExcel());

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

        logout.setOnMouseClicked(event -> {

            AppInitializer appInitializer = new AppInitializer();
            try {
                appInitializer.changeScene((Stage) logout.getScene().getWindow(), "loginView.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        cargarUsuarios();

    }

    @SuppressWarnings("unchecked")
    private void cargarUsuarios() {
        try {
            DataBase db = new DataBase();
            List<Usuario> usuarios = db.obtenerUsuarios(); // Crea una instancia de la clase Database
            listaUsuarios.getItems().clear(); // Limpia la lista antes de añadir nuevos elementos
            listaUsuarios.getItems().addAll(usuarios);

            // Personalizar cómo se muestran los usuarios con estilo CSS
            listaUsuarios.setCellFactory(param -> new ListCell<Usuario>() {
                @Override
                protected void updateItem(Usuario usuario, boolean empty) {
                    super.updateItem(usuario, empty);
                    if (empty || usuario == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText("Usuario: " + usuario.getNombre() + "   |    Correo:  " + usuario.getCorreo()); 
                                                                                                        
                        setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-alignment: center;"); 
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportarUsuariosAExcel() {
        try {
            Path path = Paths.get("usuarios.xlsx");
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Usuarios");

            // Crear fila de encabezado
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Nombre");
            headerRow.createCell(1).setCellValue("Correo");

            // Obtener la lista de usuarios
            List<Usuario> usuarios = dataBase.obtenerUsuarios(); // Asume que DataBase tiene un método obtenerUsuarios

            // Escribir datos de los usuarios en el archivo Excel
            int rowNum = 1;
            for (Usuario usuario : usuarios) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(usuario.getNombre());
                row.createCell(1).setCellValue(usuario.getCorreo());
            }

            // Ajustar el tamaño de las columnas
            for (int i = 0; i < 2; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir el archivo Excel en el sistema de archivos
            FileOutputStream fileOut = new FileOutputStream("usuarios_exportados.xlsx");
            workbook.write(fileOut);
            fileOut.close();

            // Cerrar el libro de trabajo para liberar recursos
            workbook.close();

            // Mostrar un mensaje de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exportación exitosa");
            alert.setHeaderText(null);
            alert.setContentText("Los usuarios han sido exportados con éxito.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            // Mostrar un mensaje de error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al exportar usuarios");
            alert.setContentText("No se pudo exportar los usuarios a Excel.");
            alert.showAndWait();
        }
    }
}
