module com.example.todolistgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.todolistgui to javafx.fxml;
    exports com.example.todolistgui;
}