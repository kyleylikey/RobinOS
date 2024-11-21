module com.mycompany.robinos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.mycompany.robinos to javafx.fxml;
    exports com.mycompany.robinos;
}
