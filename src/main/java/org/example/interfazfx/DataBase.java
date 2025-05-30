package org.example.interfazfx;

import javafx.scene.control.TextField;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {

    public static String dbURL = "jdbc:oracle:thin:@(description= (retry_count=20)(retry_delay=3)(address=(protocol=tcps)(port=1522)(host=adb.eu-madrid-1.oraclecloud.com))(connect_data=(service_name=gfa4a84575c3a35_raezdb_high.adb.oraclecloud.com))(security=(ssl_server_dn_match=yes)))";
    public static String username = "Admin";
    public static String password = "Basededatos2004";

    public void connectToDatabase() throws SQLException {

        Connection connection = DriverManager.getConnection(dbURL, username, password);

        if (connection != null) {
            connection.close();
        }
    }

    @SuppressWarnings("exports")
    public void insertUser(TextField usernameField, TextField emailField, TextField passField) throws SQLException {

        Connection connection = DriverManager.getConnection(dbURL, this.username, this.password);

        if (connection != null) {
            String query = "INSERT INTO usuarios (id, nombre, contraseña, correo) VALUES (pk_id_usuarios_sec.nextval, ?, ?, ? || '@edu.uah.es')";
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


    public void insertarLibro(String titulo, String autor, String editorial, String isbn, int cantidad) throws SQLException {
        Connection connection = DriverManager.getConnection(dbURL, this.username, this.password);
        if (connection != null) {
            String query = "INSERT INTO libros (id,titulo, autor, editorial, isbn, cantidad) VALUES (pk_id_libros_sec.nextval,?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, titulo);
            preparedStatement.setString(2, autor);
            preparedStatement.setString(3, editorial);
            preparedStatement.setString(4, isbn);
            preparedStatement.setInt(5, cantidad);
            preparedStatement.executeUpdate();
            connection.close();
        }
    }

    public void eliminarLibro(String isbn) throws SQLException {
        Connection connection = DriverManager.getConnection(dbURL, this.username, this.password);
        if (connection != null) {
            String query = "DELETE FROM libros WHERE isbn = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, isbn);
            preparedStatement.executeUpdate();
            connection.close();
        }
    }


    public static class Usuario {
        private String nombre;
        private String correo;

        public Usuario(String nombre, String correo) {
            this.nombre = nombre;
            this.correo = correo;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getCorreo() {
            return correo;
        }

        public void setCorreo(String correo) {
            this.correo = correo;
        }
    }

    
    public List<Usuario> obtenerUsuarios() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT nombre, correo FROM usuarios";

        try (Connection conn = DriverManager.getConnection(dbURL, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario(rs.getString("nombre"), rs.getString("correo"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }

        return usuarios;
    }



}