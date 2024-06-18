package org.example.interfazfx;

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
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
    private TextField isbnfieldeliminar;
    
    @FXML
    private ListView<Libro> listaLibros;

    @FXML
    private TextField buscador;

    @FXML
    private ImageView logout;

    @FXML
    private ImageView refresh;

    @FXML
    private Label listausers;

    @FXML
    private ImageView exportar;

    @FXML
    protected void initialize() {

        logout.setOnMouseEntered(event -> logout.setCursor(Cursor.HAND));
        refresh.setOnMouseEntered(event -> refresh.setCursor(Cursor.HAND));
        listausers.setOnMouseEntered(event -> listausers.setCursor(Cursor.HAND));
        exportar.setOnMouseEntered(event -> exportar.setCursor(Cursor.HAND));

        buscador.textProperty().addListener((observable, oldValue, newValue) -> {
            List<Libro> librosBuscados = obtenerLibros(newValue);
            librosBuscados.sort(Comparator.comparing(Libro::getTitulo));
            listaLibros.getItems().setAll(librosBuscados);
        });

        listaLibros.setCellFactory(param -> new ListCell<Libro>() {
            @Override
            protected void updateItem(Libro libro, boolean empty) {
                super.updateItem(libro, empty);

                if (empty || libro == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label tituloLabel = new Label("Título: " + libro.getTitulo());
                    tituloLabel.setStyle("-fx-font-size: 15px; -fx-text-fill:white;");
                    Label autorLabel = new Label("Autor: " + libro.getAutor());
                    autorLabel.setStyle("-fx-font-size: 15px;-fx-text-fill:white;");
                    Label editorialLabel = new Label("Editorial: " + libro.getEditorial());
                    editorialLabel.setStyle("-fx-font-size: 15px;-fx-text-fill:white;");
                    Label isbnLabel = new Label("ISBN: " + libro.getIsbn());
                    isbnLabel.setStyle("-fx-font-size: 15;-fx-text-fill:white;");
                    Label cantidadLabel = new Label("Cantidad: " + libro.getCantidad());
                    cantidadLabel.setStyle("-fx-font-size: 15px;-fx-text-fill:white;");
                    // Nuevo label para la valoración media
                    Label valoracionMediaLabel = new Label(String.format("Valoración Media: %.2f", libro.getValoracionMedia()));
                    valoracionMediaLabel.setStyle("-fx-font-size: 15px;-fx-text-fill:white;");

                    VBox vboxDetalles = new VBox(tituloLabel, autorLabel, editorialLabel, isbnLabel, cantidadLabel, valoracionMediaLabel);
                    vboxDetalles.setAlignment(Pos.CENTER);

                    Button reservarButton = new Button("Eliminar");
                    reservarButton.setOnAction(event -> Eliminar(libro));
                    reservarButton.setStyle("-fx-background-radius: 15;");
                    VBox vboxBoton = new VBox(reservarButton);
                    vboxBoton.setAlignment(Pos.CENTER);

                    VBox vbox = new VBox(vboxDetalles, vboxBoton);
                    vbox.setAlignment(Pos.CENTER);
                    vbox.setPadding(new Insets(10, 0, 0, 0));
                    vbox.setSpacing(10);

                    VBox vboxWithSeparator;
                    if (getIndex() < getListView().getItems().size() - 1) {
                        Separator separator = new Separator();
                        separator.setStyle("-fx-background-color: white; -fx-opacity: 0.5;");

                        Region extraSpace = new Region();
                        extraSpace.setPrefHeight(15);

                        vboxWithSeparator = new VBox(vbox, extraSpace, separator);
                    } else {
                        VBox endSpace = new VBox();
                        endSpace.setPrefHeight(10);

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
            int cantidad;
            try {
                cantidad = Integer.parseInt(cantfield.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error en la cantidad");
                alert.setHeaderText(null);
                alert.setContentText("La cantidad debe ser un número entero.");
                alert.showAndWait();
                return;
            }

            try {
                if (libroYaExiste(isbn)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Libro existente");
                    alert.setHeaderText(null);
                    alert.setContentText("Ya existe un libro con el ISBN " + isbn + ". No se puede agregar.");
                    alert.showAndWait();
                } else {
                    dataBase.insertarLibro(titulo, autor, editorial, isbn, cantidad);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Libro añadido");
                    alert.setHeaderText(null);
                    alert.setContentText("El libro ha sido añadido con éxito.");
                    alert.showAndWait();

                    // Limpiar los campos de texto después de insertar el libro
                    nombrefield.clear();
                    autorfield.clear();
                    editorialfield.clear();
                    isbnfield.clear();
                    cantfield.clear();

                    // Actualizar la lista de libros
                    List<Libro> librosActualizados = obtenerLibros();
                    librosActualizados.sort(Comparator.comparing(Libro::getTitulo));
                    listaLibros.getItems().setAll(librosActualizados);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error al añadir libro");
                alert.setHeaderText(null);
                alert.setContentText("Ocurrió un error al añadir el libro: " + e.getMessage());
                alert.showAndWait();
            }
        });



        logout.setOnMouseClicked(event -> {

            AppInitializer appInitializer = new AppInitializer();
            try {
                appInitializer.changeScene((Stage) logout.getScene().getWindow(), "loginView.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        refresh.setOnMouseClicked(event -> {

            nombrefield.clear();
            autorfield.clear();
            editorialfield.clear();
            isbnfield.clear();
            cantfield.clear();

        }) ;

        listausers.setOnMouseClicked(event -> {

            AppInitializer appInitializer = new AppInitializer();
            try {
                appInitializer.changeScene((Stage) listausers.getScene().getWindow(), "vistaAdmin2.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        exportar.setOnMouseClicked(event -> exportarLibros());

    }

    public void Eliminar(Libro libro) {
        String sql = "DELETE FROM libros WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DataBase.dbURL, DataBase.username, DataBase.password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Establecer el valor del parámetro ID
            pstmt.setInt(1, libro.getId());
            // Ejecutar la consulta de eliminación
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Actualizar la lista de libros después de la eliminación
        List<Libro> librosActualizados = obtenerLibros();
        librosActualizados.sort(Comparator.comparing(Libro::getTitulo));
        listaLibros.getItems().setAll(librosActualizados);

        // Mostrar una alerta
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Libro eliminado");
        alert.setHeaderText(null);
        alert.setContentText("El libro '" + libro.getTitulo() + "' ha sido eliminado.");
        alert.showAndWait();
    }

    public List<Libro> obtenerLibros() {
        List<Libro> libros = new ArrayList<>();
        // Modificada para incluir la valoración media
        String sql = "SELECT l.*, AVG(v.valoracion) as valoracion_media FROM libros l LEFT JOIN valoraciones v ON l.isbn = v.isbn GROUP BY l.id, l.titulo, l.autor, l.editorial, l.isbn, l.cantidad";
    
        try (Connection conn = DriverManager.getConnection(DataBase.dbURL, DataBase.username, DataBase.password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
    
            while (rs.next()) {
                Libro libro = new Libro(rs.getInt("id"), rs.getString("titulo"), rs.getString("autor"),
                        rs.getString("editorial"), rs.getString("isbn"), rs.getInt("cantidad"));
                libro.setValoracionMedia(rs.getDouble("valoracion_media")); // Asumiendo que se añade este campo a la clase Libro
                libros.add(libro);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    
        return libros;
    }
    
    public List<Libro> obtenerLibros(String busqueda) {
        List<Libro> libros = new ArrayList<>();
        // Modificada para incluir la valoración media
        String sql = "SELECT l.*, AVG(v.valoracion) as valoracion_media FROM libros l LEFT JOIN valoraciones v ON l.isbn = v.isbn WHERE l.titulo LIKE ? OR l.autor LIKE ? OR l.editorial LIKE ? OR l.isbn LIKE ? GROUP BY l.id, l.titulo, l.autor, l.editorial, l.isbn, l.cantidad";
    
        try (Connection conn = DriverManager.getConnection(DataBase.dbURL, DataBase.username, DataBase.password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            String busquedaConComodines = "%" + busqueda + "%";
            pstmt.setString(1, busquedaConComodines);
            pstmt.setString(2, busquedaConComodines);
            pstmt.setString(3, busquedaConComodines);
            pstmt.setString(4, busquedaConComodines);
    
            ResultSet rs = pstmt.executeQuery();
    
            while (rs.next()) {
                Libro libro = new Libro(rs.getInt("id"), rs.getString("titulo"), rs.getString("autor"),
                        rs.getString("editorial"), rs.getString("isbn"), rs.getInt("cantidad"));
                libro.setValoracionMedia(rs.getDouble("valoracion_media")); // Asumiendo que se añade este campo a la clase Libro
                libros.add(libro);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    
        return libros;
    }

    public boolean libroYaExiste(String isbn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM libros WHERE isbn = ?";
        try (Connection conn = DriverManager.getConnection(dataBase.dbURL, dataBase.username, dataBase.password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retorna true si hay al menos un libro con ese ISBN
            }
        }
        return false; // Retorna false si no encuentra ningún libro con ese ISBN
    }

        public void exportarLibros() {
    List<Libro> libros = obtenerLibros(); // Asume que este método devuelve todos los libros
    String path = "libros_exportados.xlsx"; // Define el nombre y ruta del archivo de exportación

    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Libros");

        // Escribe la cabecera del CSV
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Titulo");
        headerRow.createCell(2).setCellValue("Autor");
        headerRow.createCell(3).setCellValue("Editorial");
        headerRow.createCell(4).setCellValue("ISBN");
        headerRow.createCell(5).setCellValue("Cantidad");

        // Escribe los datos de cada libro
        int rowNum = 1;
        for (Libro libro : libros) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(libro.getId());
            row.createCell(1).setCellValue(libro.getTitulo());
            row.createCell(2).setCellValue(libro.getAutor());
            row.createCell(3).setCellValue(libro.getEditorial());
            row.createCell(4).setCellValue(libro.getIsbn());
            row.createCell(5).setCellValue(libro.getCantidad());
        }

        // Ajustar el tamaño de las columnas
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }

        // Escribir el libro de trabajo a un archivo
        try (FileOutputStream fileOut = new FileOutputStream(path)) {
            workbook.write(fileOut);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exportación exitosa");
        alert.setHeaderText(null);
        alert.setContentText("Libros exportados exitosamente.");
        alert.showAndWait();

    } catch (IOException e) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error al exportar libros");
        alert.setHeaderText(null);
        alert.setContentText("Ocurrió un error al exportar los libros.");
        alert.showAndWait();
    }
}


}


