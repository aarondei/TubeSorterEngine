module com.example.coloredtubesorter {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.coloredtubesorter to javafx.fxml;
    exports com.example.coloredtubesorter;
}