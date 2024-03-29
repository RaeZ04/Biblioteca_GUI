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
            String query = "INSERT INTO usuarios (nombre, contraseña, correo) VALUES (?, ?, ?|| '@edu.uah.es')";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, usernameField.getText());
            preparedStatement.setString(2, passField.getText());
            preparedStatement.setString(3, usernameField.getText());
            preparedStatement.executeUpdate();
            connection.close();
        }
    }

    public boolean userExists(String email, String password) throws SQLException {
        Connection connection = DriverManager.getConnection(dbURL, this.username, this.password);
        if (connection != null) {
            String query = "SELECT * FROM usuarios WHERE correo = ? AND contraseña = ?";
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

    public String getUsername(String email) throws SQLException {
        Connection connection = DriverManager.getConnection(dbURL, this.username, this.password);
        if (connection != null) {
            String query = "SELECT nombre FROM usuarios WHERE correo = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String username = resultSet.getString("nombre");
                connection.close();
                return username;
            }
            connection.close();
        }
        return null;
    }

}