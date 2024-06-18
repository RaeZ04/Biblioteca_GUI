module org.example.interfazfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires com.dlsc.formsfx;
    requires javafx.graphics;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens org.example.interfazfx to javafx.fxml;
    exports org.example.interfazfx;
}