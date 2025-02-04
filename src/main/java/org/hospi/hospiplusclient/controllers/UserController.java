package org.hospi.hospiplusclient.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.hospi.hospiplusclient.HospiApplication;
import org.hospi.hospiplusclient.configuration.JacksonConfig;
import org.hospi.hospiplusclient.models.Role;
import org.hospi.hospiplusclient.models.User;
import org.hospi.hospiplusclient.services.ApiService;

import java.util.List;

public class UserController {

    @FXML
    private TextField usernameField, surnameField, emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<Role> roleComboBox;
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> usernameColumn, surnameColumn, emailColumn;
    @FXML
    private TableColumn<User, String> roleColumn;

    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObservableList<Role> roles = FXCollections.observableArrayList();

    private final ObjectMapper objectMapper = JacksonConfig.createObjectMapper();

    @FXML
    public void initialize() {
        // Bind TableColumns
        idColumn.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        usernameColumn.setCellValueFactory(data -> data.getValue().usernameProperty());
        surnameColumn.setCellValueFactory(data -> data.getValue().surnameProperty());
        emailColumn.setCellValueFactory(data -> data.getValue().emailProperty());
        roleColumn.setCellValueFactory(data -> data.getValue().roleNameProperty());

        userTable.setItems(users);

        // Load roles
        loadRoles();
        refreshUsers();
    }

    private void loadRoles() {
        Task<List<Role>> roleTask = new Task<>() {
            @Override
            protected List<Role> call() throws Exception {
                // Replace with API call to fetch roles
                String jsonResponse = ApiService.sendGetRequest("role").get().getBody();
                return objectMapper.readValue(jsonResponse, new TypeReference<List<Role>>() {});
            }
        };

        roleTask.setOnSucceeded(event -> roles.setAll(roleTask.getValue()));
        roleComboBox.setItems(roles);
        new Thread(roleTask).start();
    }

    @FXML
    private void handleCreate() {

        if (usernameField.getText().isEmpty() || surnameField.getText().isEmpty() || emailField.getText().isEmpty() || passwordField.getText().isEmpty() || roleComboBox.getValue() == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        User user = new User();
        user.setUserName(usernameField.getText());
        user.setSurname(surnameField.getText());
        user.setEmail(emailField.getText());
        user.setUserPassword(passwordField.getText());
        user.setRoleId(roleComboBox.getValue().getId());

        Task<Void> createTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String payload = objectMapper.writeValueAsString(user); // Serialize user to JSON
                ApiService.sendPostRequest("user", payload).get();
                return null;
            }
        };

        createTask.setOnSucceeded(event -> {
            refreshUsers();
            handleClear();
        });
        new Thread(createTask).start();
    }

    @FXML
    private void handleUpdate() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "Please select a user to update.");
            return;
        }

        selectedUser.setUserName(usernameField.getText());
        selectedUser.setSurname(surnameField.getText());
        selectedUser.setEmail(emailField.getText());
        selectedUser.setUserPassword(passwordField.getText());
        selectedUser.setRoleId(roleComboBox.getValue().getId());

        Task<Void> updateTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String payload = objectMapper.writeValueAsString(selectedUser);
                ApiService.sendPutRequest("user", selectedUser.getId(), payload).get();
                return null;
            }
        };

        updateTask.setOnSucceeded(event -> {
            refreshUsers();
            handleClear();
        });
        new Thread(updateTask).start();
    }

    @FXML
    private void handleDelete() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "Please select a user to delete.");
            return;
        }

        Task<Void> deleteTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ApiService.sendDeleteRequest("user", selectedUser.getId()).get();
                return null;
            }
        };

        deleteTask.setOnSucceeded(event -> refreshUsers());
        new Thread(deleteTask).start();
    }

    private void refreshUsers() {
        Task<List<User>> userTask = new Task<>() {
            @Override
            protected List<User> call() throws Exception {
                try{
                    String jsonResponse = ApiService.sendGetRequest("user").get().getBody();
                    return objectMapper.readValue(jsonResponse, new TypeReference<List<User>>() {});
                } catch (Exception e) {
                    System.out.println("Error during API call or deserialization: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
        };

        userTask.setOnSucceeded(event -> users.setAll(userTask.getValue()));
        new Thread(userTask).start();
    }

    @FXML
    private void handleClear() {
        usernameField.clear();
        surnameField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.setValue(null);
    }
}
