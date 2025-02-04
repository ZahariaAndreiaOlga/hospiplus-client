package org.hospi.hospiplusclient.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hospi.hospiplusclient.HospiApplication;
import org.hospi.hospiplusclient.configuration.JacksonConfig;
import org.hospi.hospiplusclient.models.Role;
import org.hospi.hospiplusclient.services.ApiService;

import java.util.Arrays;

public class RoleController {

    @FXML
    private TextField roleNameField;

    @FXML
    private TableView<Role> roleTable;

    @FXML
    private TableColumn<Role, Integer> idColumn;

    @FXML
    private TableColumn<Role, String> roleNameColumn;

    private final ObservableList<Role> roles = FXCollections.observableArrayList();

    private final ObjectMapper objectMapper = JacksonConfig.createObjectMapper();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        roleNameColumn.setCellValueFactory(data -> data.getValue().roleNameProperty());

        roleTable.setItems(roles);
        fetchRoles();
    }

    @FXML
    private void handleAdd() {
        String roleName = roleNameField.getText();
        if (roleName.isEmpty()) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Role name cannot be empty");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Role newRole = new Role();
                newRole.setRoleName(roleName);
                String json = objectMapper.writeValueAsString(newRole);
                ApiService.sendPostRequest("role", json).join();
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            fetchRoles();
            handleClear();
        });
        new Thread(task).start();
    }

    @FXML
    private void handleUpdate() {
        Role selectedRole = roleTable.getSelectionModel().getSelectedItem();
        if (selectedRole == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No role selected for update");
            return;
        }

        String roleName = roleNameField.getText();
        if (roleName.isEmpty()) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Role name cannot be empty");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                selectedRole.setRoleName(roleName);
                String json = objectMapper.writeValueAsString(selectedRole);
                ApiService.sendPutRequest("role", selectedRole.getId(), json).join();
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            fetchRoles();
            handleClear();
        });
        new Thread(task).start();
    }

    @FXML
    private void handleDelete() {
        Role selectedRole = roleTable.getSelectionModel().getSelectedItem();
        if (selectedRole == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No role selected for update");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ApiService.sendDeleteRequest("role", selectedRole.getId()).join();
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            fetchRoles();
            handleClear();
        });
        new Thread(task).start();
    }

    private void fetchRoles() {
        System.out.println("Inside Fetch Function");
        Task<ObservableList<Role>> task = new Task<>() {
            @Override
            protected ObservableList<Role> call() throws Exception {
                System.out.println("Inside Call Function");

                // Wrap the API call and deserialization in try-catch for better error handling
                try {
                    // Making the GET request to fetch the roles
                    String response = ApiService.sendGetRequest("role").join().getBody();
                    System.out.println("API Response: " + response);  // Log the raw response

                    if (response == null || response.isEmpty()) {
                        System.out.println("Error: No data received from the API.");
                        return FXCollections.emptyObservableList();  // Return empty list if no data
                    }

                    // Deserialization of the response into an array of Role objects
                    Role[] roleArray = objectMapper.readValue(response, Role[].class);
                    System.out.println("Deserialized : " + Arrays.toString(roleArray));  // Log the deserialized roles

                    // Return the list of roles as an observable list
                    return FXCollections.observableArrayList(roleArray);

                } catch (Exception e) {
                    // Log any exceptions that occur during the process
                    System.out.println("Error during API call or deserialization: " + e.getMessage());
                    e.printStackTrace();  // Print stack trace for better debugging
                    return FXCollections.emptyObservableList();  // Return an empty list in case of an error
                }
            }
        };

        task.setOnSucceeded(event -> roles.setAll(task.getValue()));
        new Thread(task).start();
    }

    private void handleClear() {
        roleNameField.clear();
    }
}
