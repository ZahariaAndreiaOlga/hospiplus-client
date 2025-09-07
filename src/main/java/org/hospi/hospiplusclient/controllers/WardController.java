package org.hospi.hospiplusclient.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.hospi.hospiplusclient.HospiApplication;
import org.hospi.hospiplusclient.configuration.JacksonConfig;
import org.hospi.hospiplusclient.models.Ward;
import org.hospi.hospiplusclient.services.ApiService;

import java.util.Arrays;

public class WardController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Ward> wardTable;
    @FXML
    private TableColumn<Ward, Integer> idColumn;
    @FXML
    private TableColumn<Ward, String> wardNameColumn;
    @FXML
    private TableColumn<Ward, Integer> capacityColumn;
    @FXML
    private TextField wardNameField;
    @FXML
    private TextField capacityField;

    private final ObservableList<Ward> wards = FXCollections.observableArrayList();
    private FilteredList<Ward> filteredWards = new FilteredList<>(wards, w -> true);
    private final ObjectMapper objectMapper = JacksonConfig.createObjectMapper();

    @FXML
    public void initialize() {
        // Table column bindings
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        wardNameColumn.setCellValueFactory(cellData -> cellData.getValue().wardNameProperty());
        capacityColumn.setCellValueFactory(cellData -> cellData.getValue().capacityProperty().asObject());

        wardTable.setItems(filteredWards);

        // Load data
        fetchWards();

        // Table selection listener
        wardTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                wardNameField.setText(newSelection.getWardName());
                capacityField.setText(String.valueOf(newSelection.getCapacity()));
            }
        });

        // Search listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredWards.setPredicate(ward -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return ward.getWardName().toLowerCase().contains(newVal.toLowerCase());
            });
        });
    }

    private void fetchWards() {
        System.out.println("Inside Fetch Function");
        Task<ObservableList<Ward>> task = new Task<>() {
            @Override
            protected ObservableList<Ward> call() throws Exception {
                System.out.println("Inside Call Function");

                try {
                    // Making the GET request to fetch the wards
                    String response = ApiService.sendGetRequest("ward").join().getBody();
                    System.out.println("API Response: " + response);  // Log the raw response

                    if (response == null || response.isEmpty()) {
                        System.out.println("Error: No data received from the API.");
                        return FXCollections.emptyObservableList();  // Return empty list if no data
                    }

                    // Deserialization of the response into an array of ward objects
                    Ward[] wardArray = objectMapper.readValue(response, Ward[].class);
                    System.out.println("Deserialized : " + Arrays.toString(wardArray));  // Log the deserialized wards

                    // Return the list of wards as an observable list
                    return FXCollections.observableArrayList(wardArray);

                } catch (Exception e) {
                    // Log any exceptions that occur during the process
                    System.out.println("Error during API call or deserialization: " + e.getMessage());
                    e.printStackTrace();  // Print stack trace for better debugging
                    return FXCollections.emptyObservableList();  // Return an empty list in case of an error
                }
            }
        };

        task.setOnSucceeded(event -> wards.setAll(task.getValue()));
        new Thread(task).start();
    }

    @FXML
    private void handleAdd() {
        if (wardNameField.getText().isEmpty() || capacityField.getText().isEmpty()) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Ward ward = new Ward();
                ward.setWardName(wardNameField.getText());
                ward.setCapacity(Integer.parseInt(capacityField.getText()));

                String json = objectMapper.writeValueAsString(ward);
                ApiService.sendPostRequest("ward", json).get();
                return null;
            }
        };
        task.setOnSucceeded(e -> { fetchWards(); handleClear(); });
        new Thread(task).start();
    }

    @FXML
    private void handleUpdate() {
        Ward selectedWard = wardTable.getSelectionModel().getSelectedItem();
        if (selectedWard == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No ward selected for update");
            return;
        }
        if (wardNameField.getText().isEmpty() || capacityField.getText().isEmpty()) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                selectedWard.setWardName(wardNameField.getText());
                selectedWard.setCapacity(Integer.parseInt(capacityField.getText()));

                String json = objectMapper.writeValueAsString(selectedWard);
                ApiService.sendPutRequest("ward", selectedWard.getId(), json).get();
                return null;
            }
        };
        task.setOnSucceeded(e -> { fetchWards(); handleClear(); });
        new Thread(task).start();
    }

    @FXML
    private void handleDelete() {
        Ward selectedWard = wardTable.getSelectionModel().getSelectedItem();
        if (selectedWard == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No ward selected for deletion");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ApiService.sendDeleteRequest("ward", selectedWard.getId()).get();
                return null;
            }
        };
        task.setOnSucceeded(e -> { fetchWards(); handleClear(); });
        new Thread(task).start();
    }

    @FXML
    private void handleClear() {
        wardNameField.clear();
        capacityField.clear();
    }
}
