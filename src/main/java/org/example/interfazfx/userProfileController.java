package org.example.interfazfx;

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
import java.util.Optional;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class userProfileController {

    @FXML
    private ListView<Libro> listaLibros;

    @FXML
    private Label inicio;

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
    private TextField buscador;

    @FXML
    protected void initialize() {

        

        usernameLabel.setText(App.currentUsername);

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

        lupa.setOnMouseEntered(event -> lupa.setCursor(Cursor.HAND));
        logout.setOnMouseEntered(event -> logout.setCursor(Cursor.HAND));
        inicio.setOnMouseEntered(event -> inicio.setCursor(Cursor.HAND));

        logout.setOnMouseClicked(event -> {

            AppInitializer appInitializer = new AppInitializer();
            try {
                appInitializer.changeScene((Stage) logout.getScene().getWindow(), "loginView.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        inicio.setOnMouseClicked(event -> {

            AppInitializer appInitializer = new AppInitializer();
            try {
                appInitializer.changeScene((Stage) logout.getScene().getWindow(), "libraryUser.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        buscador.textProperty().addListener((observable, oldValue, newValue) -> {
            List<Libro> librosBuscados = obtenerLibros(newValue);
            librosBuscados.sort(Comparator.comparing(Libro::getTitulo));
            listaLibros.getItems().setAll(librosBuscados);
        });

        usernameLabel.setText(App.currentUsername);

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

                    Button devolverButton = new Button("Devolver");
                    devolverButton.setOnAction(null); // Elimina el manejador de eventos anterior
                    devolverButton.setOnAction(event -> devolverLibro(libro)); // Agrega el nuevo manejador de eventos
                    devolverButton.setStyle("-fx-background-radius: 15;");
                    VBox vboxBoton = new VBox(devolverButton);
                    vboxBoton.setAlignment(Pos.CENTER); 

                    VBox vbox = new VBox(vboxDetalles, vboxBoton);
                    vbox.setAlignment(Pos.CENTER);
                    vbox.setPadding(new javafx.geometry.Insets(10, 0, 0, 0)); 

                    vbox.setSpacing(10); 

                    VBox vboxWithSeparator;
                    if (getIndex() < getListView().getItems().size() - 1) {
                        // Añade un separador
                        Separator separator = new Separator();
                        separator.setStyle("-fx-background-color: white; -fx-opacity: 0.5;");

                        // Añade un espacio extra antes del separador
                        Region extraSpace = new Region();
                        extraSpace.setPrefHeight(15); 

                        vboxWithSeparator = new VBox(vbox, extraSpace, separator);
                    } else {
                        // Agrega un espacio al final de la última celda
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
    }

    public void devolverLibro(Libro libro) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Devolución de libro");
        dialog.setHeaderText("Ingrese la cantidad de libros que desea devolver:");
        Optional<String> result = dialog.showAndWait();
    
        if (result.isPresent()){
            String input = result.get();
            if (input.isEmpty() || input.equals("0")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de devolución");
                alert.setHeaderText(null);
                alert.setContentText("La cantidad ingresada es incorrecta.");
                alert.showAndWait();
                return;
            }
    
            int cantidadDevolver = Integer.parseInt(input);
            if (cantidadDevolver > libro.getCantidad()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de devolución");
                alert.setHeaderText(null);
                alert.setContentText("No puedes devolver más libros de los que tienes reservados.");
                alert.showAndWait();
                return;
            }
    
            String sqlReserva = "UPDATE reservas SET cantidad = cantidad - ? WHERE usuario = ? AND isbn = ?";
            String sqlCheckLibro = "SELECT COUNT(*) FROM libros WHERE isbn = ?";
            String sqlInsertLibro = "INSERT INTO libros(id,titulo, autor, editorial, isbn, cantidad) VALUES(pk_id_libros_sec.nextval,?, ?, ?, ?, ?)";
            String sqlUpdateLibro = "UPDATE libros SET cantidad = cantidad + ? WHERE isbn = ?";
            String sqlEliminacion = "DELETE FROM reservas WHERE usuario = ? AND isbn = ? AND cantidad = 0";
    
            try (Connection conn = DriverManager.getConnection(DataBase.dbURL, DataBase.username, DataBase.password);
                    PreparedStatement pstmtReserva = conn.prepareStatement(sqlReserva);
                    PreparedStatement pstmtCheckLibro = conn.prepareStatement(sqlCheckLibro);
                    PreparedStatement pstmtInsertLibro = conn.prepareStatement(sqlInsertLibro);
                    PreparedStatement pstmtUpdateLibro = conn.prepareStatement(sqlUpdateLibro);
                    PreparedStatement pstmtEliminacion = conn.prepareStatement(sqlEliminacion)) {
    
                pstmtReserva.setInt(1, cantidadDevolver);
                pstmtReserva.setString(2, App.currentUsername);
                pstmtReserva.setString(3, libro.getIsbn());
    
                pstmtReserva.executeUpdate();
    
                pstmtCheckLibro.setString(1, libro.getIsbn());
                ResultSet rs = pstmtCheckLibro.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    pstmtUpdateLibro.setInt(1, cantidadDevolver);
                    pstmtUpdateLibro.setString(2, libro.getIsbn());
                    pstmtUpdateLibro.execute();

                } else {
                    pstmtInsertLibro.setString(1, libro.getTitulo());
                    pstmtInsertLibro.setString(2, libro.getAutor());
                    pstmtInsertLibro.setString(3, libro.getEditorial());
                    pstmtInsertLibro.setString(4, libro.getIsbn());
                    pstmtInsertLibro.setInt(5, cantidadDevolver);
                    pstmtInsertLibro.execute();
                }

                pstmtEliminacion.setString(1, App.currentUsername);
                pstmtEliminacion.setString(2, libro.getIsbn());
    
                pstmtEliminacion.execute();
    
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Devolución exitosa");
                alert.setHeaderText(null);
                alert.setContentText("Se ha devuelto el libro correctamente.");
                alert.showAndWait();
    
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    
        List<Libro> libros = obtenerLibros();
        libros.sort(Comparator.comparing(Libro::getTitulo));
        listaLibros.setFocusTraversable(false);
    
        listaLibros.getItems().setAll(libros);
    }

    public List<Libro> obtenerLibros() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT titulo, autor, editorial, isbn, SUM(cantidad) as cantidad FROM reservas WHERE usuario = ? GROUP BY titulo, autor, editorial, isbn";
    
        try (Connection conn = DriverManager.getConnection(DataBase.dbURL, DataBase.username, DataBase.password);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, App.currentUsername);
    
            ResultSet rs = pstmt.executeQuery();
    
            while (rs.next()) {
                Libro libro = new Libro(rs.getString("titulo"), rs.getString("autor"),
                        rs.getString("editorial"), rs.getString("isbn"), rs.getInt("cantidad"));
                libros.add(libro);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    
        return libros;
    }

    public List<Libro> obtenerLibros(String busqueda) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT titulo, autor, editorial, isbn, SUM(cantidad) as cantidad FROM reservas WHERE (titulo LIKE ? OR autor LIKE ? OR editorial LIKE ? OR isbn LIKE ?) GROUP BY titulo, autor, editorial, isbn";
    
        try (Connection conn = DriverManager.getConnection(DataBase.dbURL, DataBase.username, DataBase.password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            String busquedaConComodines = "%" + busqueda + "%";
            pstmt.setString(1, busquedaConComodines);
            pstmt.setString(2, busquedaConComodines);
            pstmt.setString(3, busquedaConComodines);
            pstmt.setString(4, busquedaConComodines);
    
            ResultSet rs = pstmt.executeQuery();
    
            while (rs.next()) {
                Libro libro = new Libro(rs.getString("titulo"), rs.getString("autor"),
                        rs.getString("editorial"), rs.getString("isbn"), rs.getInt("cantidad"));
                libros.add(libro);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    
        return libros;
    }


}
