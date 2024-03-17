package org.example.interfazfx;

import javafx.scene.control.TextField;
import org.w3c.dom.Text;

import java.sql.*;

public class DataBase {

    private String dbURL = "jdbc:oracle:thin:@(description= (retry_count=20)(retry_delay=3)(address=(protocol=tcps)(port=1522)(host=adb.eu-madrid-1.oraclecloud.com))(connect_data=(service_name=gfa4a84575c3a35_raezdb_high.adb.oraclecloud.com))(security=(ssl_server_dn_match=yes)))";
    private String username = "Admin";
    private String password = "Basededatos2004";

    public void connectToDatabase() throws SQLException {

        Connection connection = DriverManager.getConnection(dbURL, username, password);

        if (connection != null) {
            // Crea un objeto Statement
            // Cierra la conexión
            connection.close();
        }
    }

    public void insertUser(TextField usernameField, TextField emailField, TextField passField) throws SQLException {

        Connection connection = DriverManager.getConnection(dbURL, this.username, this.password);

        if (connection != null) {
            String query = "INSERT INTO usuarios (nombre, email, pass) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, usernameField.getText());
            preparedStatement.setString(2, emailField.getText());
            preparedStatement.setString(3, passField.getText());
            preparedStatement.executeUpdate();
            connection.close();
        }
    }

    public boolean userExists(String email, String password) throws SQLException {
        Connection connection = DriverManager.getConnection(dbURL, this.username, this.password);
        if (connection != null) {
            String query = "SELECT * FROM usuarios WHERE email = ? AND pass = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean exists = resultSet.next();
            connection.close();
            return exists;
        }
        return false;
    }

}