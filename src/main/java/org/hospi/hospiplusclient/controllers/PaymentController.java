package org.hospi.hospiplusclient.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.hospi.hospiplusclient.HospiApplication;
import org.hospi.hospiplusclient.configuration.JacksonConfig;
import org.hospi.hospiplusclient.models.Payment;
import org.hospi.hospiplusclient.services.ApiService;

import java.util.Arrays;

public class PaymentController {

    @FXML
    private TextField payTypeField;

    @FXML
    private TableView<Payment> paymentTable;

    @FXML
    private TableColumn<Payment, Integer> idColumn;

    @FXML
    private TableColumn<Payment, String> payTypeColumn;

    private ObservableList<Payment> payments = FXCollections.observableArrayList();
    private ObjectMapper objectMapper = JacksonConfig.createObjectMapper();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        payTypeColumn.setCellValueFactory(cellData -> cellData.getValue().payTypeProperty());

        paymentTable.setItems(payments);

        fetchPayments();
    }

    // Create a new Payment
    @FXML
    private void handleAdd() {
        String paymentName = payTypeField.getText();
        if (paymentName.isEmpty()) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Payment name cannot be empty");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Payment newPayment = new Payment();
                newPayment.setPayType(paymentName);
                String json = objectMapper.writeValueAsString(newPayment);
                ApiService.sendPostRequest("payment", json).join();
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            fetchPayments();
            handleClear();
        });
        new Thread(task).start();
    }

    // Update an existing Payment
    @FXML
    private void handleUpdate() {
        Payment selectedPayment = paymentTable.getSelectionModel().getSelectedItem();
        if (selectedPayment == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No payment selected for update");
            return;
        }

        String paymentName = payTypeField.getText();
        if (paymentName.isEmpty()) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Payment name cannot be empty");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                selectedPayment.setPayType(paymentName);
                String json = objectMapper.writeValueAsString(selectedPayment);
                ApiService.sendPutRequest("payment", selectedPayment.getId(), json).join();
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            fetchPayments();
            handleClear();
        });
        new Thread(task).start();
    }

    // Delete an existing Payment
    @FXML
    private void handleDelete() {
        Payment selectedPayment = paymentTable.getSelectionModel().getSelectedItem();
        if (selectedPayment == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No role selected for deletion");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ApiService.sendDeleteRequest("payment", selectedPayment.getId()).join();
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            fetchPayments();
            handleClear();
        });
        new Thread(task).start();
    }

    // Load the list of Payments from the API
    private void fetchPayments(){
        System.out.println("Inside Fetch Function");
        Task<ObservableList<Payment>> task = new Task<>() {
            @Override
            protected ObservableList<Payment> call() throws Exception {
                System.out.println("Inside Call Function");

                // Wrap the API call and deserialization in try-catch for better error handling
                try {
                    // Making the GET request to fetch the payment
                    String response = ApiService.sendGetRequest("payment").join().getBody();
                    System.out.println("API Response: " + response);  // Log the raw response

                    if (response == null || response.isEmpty()) {
                        System.out.println("Error: No data received from the API.");
                        return FXCollections.emptyObservableList();  // Return empty list if no data
                    }

                    // Deserialization of the response into an array of payment objects
                    Payment[] paymentArray = objectMapper.readValue(response, Payment[].class);
                    System.out.println("Deserialized : " + Arrays.toString(paymentArray));  // Log the deserialized roles

                    // Return the list of payment as an observable list
                    return FXCollections.observableArrayList(paymentArray);

                } catch (Exception e) {
                    // Log any exceptions that occur during the process
                    System.out.println("Error during API call or deserialization: " + e.getMessage());
                    e.printStackTrace();  // Print stack trace for better debugging
                    return FXCollections.emptyObservableList();  // Return an empty list in case of an error
                }
            }
        };

        task.setOnSucceeded(event -> payments.setAll(task.getValue()));
        new Thread(task).start();
    }

    private void handleClear() {
        payTypeField.clear();
    }
}
