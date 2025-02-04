package org.hospi.hospiplusclient.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.hospi.hospiplusclient.configuration.JacksonConfig;
import org.hospi.hospiplusclient.models.Order;
import org.hospi.hospiplusclient.models.OrderDetails;
import org.hospi.hospiplusclient.services.ApiService;

import java.time.LocalDateTime;
import java.util.Arrays;

public class MyOrderController {

    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private TableColumn<Order, Integer> orderIdColumn;
    @FXML
    private TableColumn<Order, LocalDateTime> orderDateColumn;
    @FXML
    private TableColumn<Order, Float> orderTotalPriceColumn;


    @FXML
    private TableView<OrderDetails> orderContentTable;
    @FXML
    private TableColumn<OrderDetails, String> productNameColumn;
    @FXML
    private TableColumn<OrderDetails, Integer> productQuantityColumn;
    @FXML
    private TableColumn<OrderDetails, Float> productPriceColumn;
    @FXML
    private TableColumn<OrderDetails, Float> productTotalPriceColumn;


    private ObservableList<Order> orders = FXCollections.observableArrayList();

    private ObservableList<OrderDetails> ordersDetails = FXCollections.observableArrayList();
    private ObjectMapper objectMapper = JacksonConfig.createObjectMapper();

    @FXML
    public void initialize() {

        // Binding
        orderIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        orderDateColumn.setCellValueFactory(cellData -> cellData.getValue().orderedAtProperty());
        orderTotalPriceColumn.setCellValueFactory(cellData -> cellData.getValue().totalPriceProperty().asObject());

        productNameColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
        productQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        productPriceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        productTotalPriceColumn.setCellValueFactory(cellData ->cellData.getValue().totalProperty().asObject());

        ordersTable.setItems(orders);
        orderContentTable.setItems(ordersDetails);

        // Load Orders
        fetchOrders();

        // Add listener for order selection
        ordersTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fetchOrderContent(newValue.idProperty().getValue());
            }
        });
    }

    private void fetchOrders() {
        System.out.println("Inside Fetch Function");
        Task<ObservableList<Order>> task = new Task<>() {
            @Override
            protected ObservableList<Order> call() throws Exception {
                System.out.println("Inside Call Function");

                // Wrap the API call and deserialization in try-catch for better error handling
                try {
                    // Making the GET request to fetch the categories
                    String response = ApiService.sendGetRequest("order").join().getBody();
                    System.out.println("API Response: " + response);  // Log the raw response

                    if (response == null || response.isEmpty()) {
                        System.out.println("Error: No data received from the API.");
                        return FXCollections.emptyObservableList();  // Return empty list if no data
                    }

                    // Deserialization of the response into an array
                    Order[] ordersArray = objectMapper.readValue(response, Order[].class);
                    System.out.println("Deserialized : " + Arrays.toString(ordersArray));  // Log

                    // Return the observable list
                    return FXCollections.observableArrayList(ordersArray);

                } catch (Exception e) {
                    // Log any exceptions that occur during the process
                    System.out.println("Error during API call or deserialization: " + e.getMessage());
                    e.printStackTrace();  // Print stack trace for better debugging
                    return FXCollections.emptyObservableList();  // Return an empty list in case of an error
                }
            }
        };

        task.setOnSucceeded(event -> orders.setAll(task.getValue()));
        new Thread(task).start();
    }

    private void fetchOrderContent(int id) {
        System.out.println("Inside Fetch Function");
        Task<ObservableList<OrderDetails>> task = new Task<>() {
            @Override
            protected ObservableList<OrderDetails> call() throws Exception {
                System.out.println("Inside Call Function");

                // Wrap the API call and deserialization in try-catch for better error handling
                try {
                    // Making the GET request to fetch the order details
                    String response = ApiService.sendGetRequest("order/"+id+"/details").join().getBody();
                    System.out.println("API Response: " + response);  // Log the raw response

                    if (response == null || response.isEmpty()) {
                        System.out.println("Error: No data received from the API.");
                        return FXCollections.emptyObservableList();  // Return empty list if no data
                    }

                    // Deserialization of the response into an array
                    OrderDetails[] ordersDetailsArray = objectMapper.readValue(response, OrderDetails[].class);
                    System.out.println("Deserialized : " + Arrays.toString(ordersDetailsArray));  // Log

                    // Return the observable list
                    return FXCollections.observableArrayList(ordersDetailsArray);

                } catch (Exception e) {
                    // Log any exceptions that occur during the process
                    System.out.println("Error during API call or deserialization: " + e.getMessage());
                    e.printStackTrace();  // Print stack trace for better debugging
                    return FXCollections.emptyObservableList();  // Return an empty list in case of an error
                }
            }
        };

        task.setOnSucceeded(event -> ordersDetails.setAll(task.getValue()));
        new Thread(task).start();
    }

}
