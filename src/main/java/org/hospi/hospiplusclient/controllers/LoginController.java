package org.hospi.hospiplusclient.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.hospi.hospiplusclient.HospiApplication;
import org.hospi.hospiplusclient.configuration.JacksonConfig;
import org.hospi.hospiplusclient.services.ApiService;
import org.hospi.hospiplusclient.utils.ServiceResponse;
import org.hospi.hospiplusclient.utils.TokenStorage;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {

        errorLabel.setText("");

        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        String jsonRequest = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);

        Task<String> loginTask = new Task<>(){

            @Override
            protected String call() throws Exception {

                try{
                    ServiceResponse response = ApiService.sendPostRequest("auth/login", jsonRequest).get();
                    if (response.getStatusCode() == 200 && response.getBody() != null && !response.getBody().isEmpty()) {
                        return response.getBody();
                    } else {
                        return "Invalid credentials or error occurred.";
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            protected void succeeded() {
                String response = getValue();
                if (response != null && response.startsWith("Invalid credentials")) {
                    errorLabel.setText(response);
                } else {

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

                }
            }

            @Override
            protected void failed() {
                errorLabel.setText("Login failed. Please try again.");
            }

        };

        new Thread(loginTask).start();

    }

    @FXML
    private void switchToRegister() {
        try{
            HospiApplication.loadRegisterScreen();
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }
}

