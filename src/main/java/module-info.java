module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.demo to javafx.fxml;
    opens com.example.demo.controller to javafx.fxml;

    // <<< Here, open the Login package to javafx.fxml too:
    opens Login to javafx.fxml;

    exports com.example.demo;
    exports com.example.demo.controller;
    exports Login;  // only if you want to export this package (not required for FXML injection)
}
