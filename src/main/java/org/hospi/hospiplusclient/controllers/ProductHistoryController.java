package org.hospi.hospiplusclient.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import org.hospi.hospiplusclient.HospiApplication;
import org.hospi.hospiplusclient.configuration.JacksonConfig;
import org.hospi.hospiplusclient.models.Category;
import org.hospi.hospiplusclient.models.Product;
import org.hospi.hospiplusclient.models.ProductHistory;
import org.hospi.hospiplusclient.services.ApiService;

import java.time.LocalDateTime;
import java.util.Arrays;

public class ProductHistoryController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Integer> productIdColumn;
    @FXML
    private TableColumn<Product, String> productCodeColumn;
    @FXML
    private TableColumn<Product, String> productMuColumn;
    @FXML
    private TableColumn<Product, Integer> productQuantityColumn;
    @FXML
    private TableColumn<Product, Integer> productCriticalColumn;
    @FXML
    private TextField currentQuantityField;
    @FXML
    private TextField changeQuantityField;
    @FXML
    private TableView<ProductHistory> historyTable;
    @FXML
    private TableColumn<ProductHistory, Integer> historyIdColumn;
    @FXML
    private TableColumn<ProductHistory, Integer> historyQuantityColumn;
    @FXML
    private TableColumn<ProductHistory, LocalDateTime> historyDateColumn;

    private ObservableList<Product> products = FXCollections.observableArrayList();
    private FilteredList<Product> filteredProducts = new FilteredList<>(products, p -> true);

    private ObservableList<ProductHistory> productHistories = FXCollections.observableArrayList();
    private ObjectMapper objectMapper = JacksonConfig.createObjectMapper();

    @FXML
    public void initialize() {
        // Bind Product Table columns
        productIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        productCodeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
        productMuColumn.setCellValueFactory(cellData -> cellData.getValue().muProperty());
        productQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        productCriticalColumn.setCellValueFactory(cellData -> cellData.getValue().criticalQuantityProperty().asObject());
        productTable.setItems(filteredProducts);

        // Bind History Table columns
        historyIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        historyQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        historyDateColumn.setCellValueFactory(cellData -> cellData.getValue().createdAtProperty());
        historyTable.setItems(productHistories);

        // Load Needed Data
        fetchProducts();

        // Add listeners or event handlers as needed
        productTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentQuantityField.setText(String.valueOf(newValue.getQuantity()));
                handleProductSelection();
            }
        });

        historyTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentQuantityField.setText(String.valueOf(newValue.getQuantity()));
            }
        });


        // Add a listener to the searchField for filtering
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredProducts.setPredicate(product -> {
                // If the search field is empty, show all products
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare product code with the search text (case insensitive)
                String lowerCaseFilter = newValue.toLowerCase();
                return product.getCode().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }


    @FXML
    private void handleProductSelection() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            // Make API call to fetch ProductHistory for the selected product
            Task<ObservableList<ProductHistory>> task = new Task<>() {
                @Override
                protected ObservableList<ProductHistory> call() throws Exception {
                    System.out.println("Inside Call Function");

                    // Wrap the API call and deserialization in try-catch for better error handling
                    try {
                        // Making the GET request to fetch the product history
                        String response = ApiService.sendGetRequest("product/"+ selectedProduct.getId() +"/history").join().getBody();
                        System.out.println("API Response: " + response);  // Log the raw response

                        if (response == null || response.isEmpty()) {
                            System.out.println("Error: No data received from the API.");
                            return FXCollections.emptyObservableList();  // Return empty list if no data
                        }

                        // Deserialization of the response into an array of product history objects
                        ProductHistory[] productArray = objectMapper.readValue(response, ProductHistory[].class);
                        System.out.println("Deserialized : " + Arrays.toString(productArray));  // Log the deserialized roles

                        // Return the list of product history as an observable list
                        return FXCollections.observableArrayList(productArray);

                    } catch (Exception e) {
                        // Log any exceptions that occur during the process
                        System.out.println("Error during API call or deserialization: " + e.getMessage());
                        e.printStackTrace();  // Print stack trace for better debugging
                        return FXCollections.emptyObservableList();  // Return an empty list in case of an error
                    }
                }
            };

            task.setOnSucceeded(event -> productHistories.setAll(task.getValue()));
            new Thread(task).start();
        }
    }

    @FXML
    private void handleValidateChange() {
        // Validate and update product quantity

        if (changeQuantityField.getText().isEmpty()) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Change quantity cannot be empty");
            return;
        }

        Integer change = Integer.parseInt(changeQuantityField.getText());
        Product product = productTable.getSelectionModel().getSelectedItem();

        if (product == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No product selected for stock update");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ProductHistory productHistory = new ProductHistory();
                productHistory.setQuantity(change);
                productHistory.setProductId(product.getId());
                String json = objectMapper.writeValueAsString(productHistory);
                ApiService.sendPostRequest("producthistory", json).join();
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            fetchProducts();
            handleProductSelection();
            handleClear();
        });
        new Thread(task).start();

    }

    @FXML
    private void handleUpdateHistory() {
        // Update selected ProductHistory

        if (changeQuantityField.getText().isEmpty()) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Change quantity cannot be empty");
            return;
        }

        Integer change = Integer.parseInt(changeQuantityField.getText());
        ProductHistory productHistory = historyTable.getSelectionModel().getSelectedItem();

        if (productHistory == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No product history selected for deletion");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                productHistory.setQuantity(change);
                String json = objectMapper.writeValueAsString(productHistory);
                ApiService.sendPutRequest("producthistory", productHistory.getId(), json).join();
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            fetchProducts();
            handleProductSelection();
            handleClear();
        });
        new Thread(task).start();
    }

    @FXML
    private void handleDeleteHistory() {
        // Delete selected ProductHistory
        ProductHistory selectedProductHistory = historyTable.getSelectionModel().getSelectedItem();
        if (selectedProductHistory == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No product history selected for deletion");
            return;
        }

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ApiService.sendDeleteRequest("producthistory", selectedProductHistory.getId()).join();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            fetchProducts();
            handleProductSelection();
            handleClear();
        });
        new Thread(task).start();

    }

    private void fetchProducts() {
        System.out.println("Inside Fetch Function");
        Task<ObservableList<Product>> task = new Task<>() {
            @Override
            protected ObservableList<Product> call() throws Exception {
                System.out.println("Inside Call Function");

                // Wrap the API call and deserialization in try-catch for better error handling
                try {
                    // Making the GET request to fetch the product
                    String response = ApiService.sendGetRequest("product").join().getBody();
                    System.out.println("API Response: " + response);  // Log the raw response

                    if (response == null || response.isEmpty()) {
                        System.out.println("Error: No data received from the API.");
                        return FXCollections.emptyObservableList();  // Return empty list if no data
                    }

                    // Deserialization of the response into an array of product objects
                    Product[] productArray = objectMapper.readValue(response, Product[].class);
                    System.out.println("Deserialized : " + Arrays.toString(productArray));  // Log the deserialized roles

                    // Return the list of product as an observable list
                    return FXCollections.observableArrayList(productArray);

                } catch (Exception e) {
                    // Log any exceptions that occur during the process
                    System.out.println("Error during API call or deserialization: " + e.getMessage());
                    e.printStackTrace();  // Print stack trace for better debugging
                    return FXCollections.emptyObservableList();  // Return an empty list in case of an error
                }
            }
        };

        task.setOnSucceeded(event -> products.setAll(task.getValue()));
        new Thread(task).start();
    }

    private void handleClear() {
        currentQuantityField.clear();
        changeQuantityField.clear();
    }
}
