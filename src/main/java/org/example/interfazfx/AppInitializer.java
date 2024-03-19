package org.example.interfazfx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class AppInitializer {
    // Variables para almacenar la posición del ratón en la ventana
    private double xOffset = 0;
    private double yOffset = 0;

    // Método para iniciar la aplicación
    public void start(Stage stage) throws IOException {
        // Cargamos el archivo FXML de la vista de inicio de sesión y creamos una nueva escena con el contenido del archivo FXML
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("loginView.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        // Configuramos el estilo de la ventana para que sea transparente y asignamos la escena a la ventana
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setResizable(false);

        // Hacemos que la ventana sea arrastrable y mostramos la ventana
        makeWindowDraggable(root, stage);
        stage.show();
    }

    // Método para cambiar de escena
    public void changeScene(Stage stage, String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        // Asignamos la escena a la ventana y hacemos que la ventana sea arrastrable
        stage.setScene(scene);
        makeWindowDraggable(root, stage);
    }

    // Método para hacer que la ventana sea arrastrable
    private void makeWindowDraggable(Parent root, Stage stage) {
        // Cuando se presiona el ratón, almacenamos la posición actual del ratón
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        // Cuando se arrastra el ratón, movemos la ventana a la nueva posición del ratón
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }
}