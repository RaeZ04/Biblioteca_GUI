module org.example.interfazfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires com.dlsc.formsfx;

    opens org.example.interfazfx to javafx.fxml;
    exports org.example.interfazfx;
}