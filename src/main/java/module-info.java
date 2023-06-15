module gui.library {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens gui.library to javafx.fxml;
    exports gui.library;
}