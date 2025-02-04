package org.hospi.hospiplusclient.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.hospi.hospiplusclient.HospiApplication;
import org.hospi.hospiplusclient.services.ApiService;
import org.hospi.hospiplusclient.utils.ServiceResponse;
import org.hospi.hospiplusclient.utils.TokenStorage;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField userNameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public Label errorLabel;

    @FXML
    private void handleRegistration() {

        errorLabel.setText("");

        String userName = userNameField.getText();
        String surname = surnameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (userName.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        String jsonRequest = String.format("{\"userName\":\"%s\", \"surname\":\"%s\", \"email\":\"%s\", \"userPassword\":\"%s\"}",
                userName, surname, email, password);

        Task<String> registerTask = new Task<>(){

            @Override
            protected String call() throws Exception {
                try{
                    ServiceResponse response = ApiService.sendPostRequest("auth/register", jsonRequest).get();
                    if (response.getStatusCode() == 200 && response.getBody() != null && !response.getBody().isEmpty()) {
                        return response.getBody();
                    } else {
                        return null;
                    }
                }catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void succeeded() {
                String response = getValue();
                if (response != null && !response.isEmpty()) {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String token = jsonObject.getString("token");
                        String role = jsonObject.getString("role");

                        TokenStorage.setJwtToken(token);
                        TokenStorage.setRole(role);

                        HospiApplication.loadMainInterface(TokenStorage.getRole());

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    errorLabel.setText("Registration failed. Please try again.");
                }
            }

            @Override
            protected void failed() {
                errorLabel.setText("Registration failed. Please try again.");
            }

        };

        new Thread(registerTask).start();

    }

    @FXML
    private void switchToLogin() {
        try{
            HospiApplication.loadLoginScreen();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
