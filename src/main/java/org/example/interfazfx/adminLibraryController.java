package org.example.interfazfx;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    private ListView<Libro> listaLibros;

    @FXML
    protected void initialize() {

        listaLibros.setCellFactory(param -> new ListCell<Libro>() {
            @Override
            protected void updateItem(Libro libro, boolean empty) {
                super.updateItem(libro, empty);
        
                if (empty || libro == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label tituloLabel = new Label("Título: " + libro.getTitulo());
                    tituloLabel.setStyle("-fx-font-size: 15px; -fx-text-fill:white;"); // Ajusta el tamaño de la fuente
                    // Haz lo mismo para los demás labels
                    Label autorLabel = new Label("Autor: " + libro.getAutor());
                    autorLabel.setStyle("-fx-font-size: 15px;-fx-text-fill:white;");
                    Label editorialLabel = new Label("Editorial: " + libro.getEditorial());
                    editorialLabel.setStyle("-fx-font-size: 15px;-fx-text-fill:white;");
                    Label isbnLabel = new Label("ISBN: " + libro.getIsbn());
                    isbnLabel.setStyle("-fx-font-size: 15;-fx-text-fill:white;");
                    Label cantidadLabel = new Label("Cantidad: " + libro.getCantidad());
                    cantidadLabel.setStyle("-fx-font-size: 15px;-fx-text-fill:white;");
        
                    VBox vboxDetalles = new VBox(tituloLabel, autorLabel, editorialLabel, isbnLabel, cantidadLabel);
                    vboxDetalles.setAlignment(Pos.CENTER);
        
                    Button reservarButton = new Button("Reservar");
                    reservarButton.setOnAction(event -> reservarLibro(libro));
                    reservarButton.setStyle("-fx-background-radius: 15;"); // Añade un radio de borde de 15
                    VBox vboxBoton = new VBox(reservarButton);
                    vboxBoton.setAlignment(Pos.CENTER); // Centra el botón en el VBox
        
                    VBox vbox = new VBox(vboxDetalles, vboxBoton);
                    vbox.setAlignment(Pos.CENTER);
                    vbox.setPadding(new Insets(10, 0, 0, 0)); // Añade un espacio de 10 píxeles encima de cada elemento
                    vbox.setSpacing(10); // Añade un espacio de 10 píxeles entre los elementos del VBox
        
                    VBox vboxWithSeparator;
                    if (getIndex() < getListView().getItems().size() - 1) {
                        // Añade un separador
                        Separator separator = new Separator();
                        separator.setStyle("-fx-background-color: white; -fx-opacity: 0.5;");
        
                        // Añade un espacio extra antes del separador
                        Region extraSpace = new Region();
                        extraSpace.setPrefHeight(15); // Añade un espacio de 10 píxeles
        
                        vboxWithSeparator = new VBox(vbox, extraSpace, separator);
                    } else {
                        // Agrega un espacio al final de la última celda
                        VBox endSpace = new VBox();
                        endSpace.setPrefHeight(10); // Añade un espacio de 10 píxeles
        
                        vboxWithSeparator = new VBox(vbox, endSpace);
                    }
        
                    setText(null);
                    setGraphic(vboxWithSeparator);
                }
            }
        });
        
        // Crea un rectángulo redondeado para usar como clip
        Rectangle clip = new Rectangle();
        clip.setArcWidth(32);
        clip.setArcHeight(32);
        
        // Asegúrate de que el clip se redimensione con la ListView
        listaLibros.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            clip.setWidth(newValue.getWidth());
            clip.setHeight(newValue.getHeight());
        });
        
        // Aplica el clip a la ListView
        listaLibros.setClip(clip);
        
        List<Libro> libros = obtenerLibros();
        libros.sort(Comparator.comparing(Libro::getTitulo));
        listaLibros.setFocusTraversable(false);
        
        listaLibros.getItems().setAll(libros);

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

            // En algún lugar de tu código donde quieras actualizar la lista de libros
            List<Libro> librosActualizados = obtenerLibros();
            librosActualizados.sort(Comparator.comparing(Libro::getTitulo));
            listaLibros.getItems().setAll(librosActualizados);
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

            List<Libro> librosActualizados = obtenerLibros();
            librosActualizados.sort(Comparator.comparing(Libro::getTitulo));
            listaLibros.getItems().setAll(librosActualizados);
            
        });

    }

    public void reservarLibro(Libro libro) {
        // Aquí iría tu código para reservar el libro seleccionado
    }

    public List<Libro> obtenerLibros() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros";

        try (Connection conn = DriverManager.getConnection(DataBase.dbURL, DataBase.username, DataBase.password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Libro libro = new Libro(rs.getInt("id"), rs.getString("titulo"), rs.getString("autor"),
                        rs.getString("editorial"), rs.getString("isbn"), rs.getInt("cantidad"));
                libros.add(libro);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return libros;
    }

}
