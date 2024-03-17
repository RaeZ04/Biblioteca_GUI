package org.example.interfazfx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {

    private String dbURL = "jdbc:oracle:thin:@(description= (retry_count=20)(retry_delay=3)(address=(protocol=tcps)(port=1522)(host=adb.eu-madrid-1.oraclecloud.com))(connect_data=(service_name=gfa4a84575c3a35_raezdb_high.adb.oraclecloud.com))(security=(ssl_server_dn_match=yes)))";
    private String username = "Admin";
    private String password = "Basededatos2004";

    public void connectToDatabase() throws SQLException {

        Connection connection = DriverManager.getConnection(dbURL, username, password);

        if (connection != null) {
            // Crea un objeto Statement
            Statement statement = connection.createStatement();

            // Ejecuta la consulta SQL
            ResultSet resultSet = statement.executeQuery("SELECT * FROM prueba order by 1");

            // Procesa el ResultSet
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
            }

            // Cierra el ResultSet y el Statement
            resultSet.close();
            statement.close();

            // Cierra la conexión
            connection.close();
        }
    }
}