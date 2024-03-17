package org.example.interfazfx;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class App extends Application {

    public static String currentUsername;

    @Override
    public void start(Stage stage) {
        AppInitializer appInitializer = new AppInitializer();

        try {
            appInitializer.start(stage);
            DataBase dataBase = new DataBase();
            dataBase.connectToDatabase();
        } catch (SQLException e) {
            try {
                appInitializer.changeScene(stage, "errorDBView.fxml");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            try {
                appInitializer.changeScene(stage, "errorExe.fxml");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}