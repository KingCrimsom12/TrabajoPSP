module org.example.practicapsp {
    requires javafx.controls;
    requires javafx.fxml;
    requires google.api.services.gmail.v1.rev110;
    requires com.google.api.client;
    requires google.api.client;
    requires com.google.api.client.auth;
    requires com.google.api.client.json.gson;
    requires jakarta.mail;


    opens org.example.practicapsp to javafx.fxml;
    exports org.example.practicapsp;
    exports org.example.practicapsp.controllers;
    opens org.example.practicapsp.controllers to javafx.fxml;
    opens org.example.practicapsp.Modelo to javafx.base;
}