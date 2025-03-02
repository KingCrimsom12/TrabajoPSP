package org.example.practicapsp.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import jakarta.mail.*;
import org.example.practicapsp.Modelo.Correo;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class GmailController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<Correo> emailTable;

    @FXML
    private TableColumn<Correo, String> subjectColumn;

    @FXML
    private TableColumn<Correo, String> labelsColumn;

    private Store store;

    @FXML
    public void initialize() {
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        labelsColumn.setCellValueFactory(new PropertyValueFactory<>("labels"));
    }

    @FXML
    public void administrarLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Ingrese su correo y contraseña.");
            return;
        }

        statusLabel.setText("Conectando...");

        new Thread(() -> {
            if (autenticarse(email, password)) {
                Platform.runLater(() -> statusLabel.setText("Inicio de sesión exitoso."));
                conseguirCorreos();
            } else {
                Platform.runLater(() -> statusLabel.setText("Error al iniciar sesión."));
            }
        }).start();
    }

    private boolean autenticarse(String email, String password) {
        try {
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.ssl.trust", "*");

            Session session = Session.getInstance(props);
            store = session.getStore("imaps");
            store.connect("imap.gmail.com", email, password);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void conseguirCorreos() {
        try {
            if (store == null || !store.isConnected()) {
                System.out.println("No hay conexión al servidor IMAP.");
                return;
            }

            Folder[] folders = store.getDefaultFolder().list("*");
            ObservableList<Correo> emailList = FXCollections.observableArrayList();

            List<String> excludeFolders = Arrays.asList(
                    "INBOX", "[Gmail]", "[Gmail]/All Mail", "[Gmail]/Drafts",
                    "[Gmail]/Important", "[Gmail]/Sent Mail",
                    "[Gmail]/Spam", "[Gmail]/Starred", "[Gmail]/Trash"
            );

            for (Folder folder : folders) {
                String folderName = folder.getFullName();

                if (excludeFolders.contains(folderName)) {
                    continue;
                }

                if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
                    folder.open(Folder.READ_ONLY);
                    Message[] messages = folder.getMessages();

                    for (Message message : messages) {
                        String subject = message.getSubject();
                        String labels = folderName;

                        String[] headers = message.getHeader("X-GM-LABELS");
                        if (headers != null && headers.length > 0) {
                            labels += ", " + String.join(", ", headers);
                        }

                        emailList.add(new Correo(subject, labels));
                    }

                    folder.close(false);
                }
            }

            emailTable.setItems(emailList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
