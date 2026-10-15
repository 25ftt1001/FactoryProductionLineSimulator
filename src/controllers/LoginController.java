package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class LoginController {

    @FXML
    private void handleLogin(ActionEvent event) {
        Navigation.switchScene(event, "/views/login_page.fxml");
    }

    @FXML
    private void handleCreateAccount(ActionEvent event) {
        Navigation.switchScene(event, "/views/create_account.fxml");
    }
}