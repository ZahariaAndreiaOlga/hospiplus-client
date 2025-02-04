package org.hospi.hospiplusclient.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.hospi.hospiplusclient.HospiApplication;
import org.hospi.hospiplusclient.configuration.JacksonConfig;
import org.hospi.hospiplusclient.models.Basket;
import org.hospi.hospiplusclient.models.BasketOrdered;
import org.hospi.hospiplusclient.models.Product;
import org.hospi.hospiplusclient.services.ApiService;
import org.hospi.hospiplusclient.utils.ServiceResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrderController {

    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableView<Basket> basketTable;
    @FXML
    private TableColumn<Product, Integer> idColumn;
    @FXML
    private TableColumn<Product, String> codeColumn;
    @FXML
    private TableColumn<Product, Integer> quantityColumn;
    @FXML
    private TableColumn<Product, Float> priceColumn;

    @FXML
    private TableColumn<Basket, Integer> basketIdColumn;
    @FXML
    private TableColumn<Basket, String> basketCodeColumn;
    @FXML
    private TableColumn<Basket, Integer> basketQuantityColumn;
    @FXML
    private TableColumn<Basket, Float> basketPriceColumn;
    @FXML
    private TableColumn<Basket, Float> basketTotalColumn;

    @FXML
    private Button addToBasketButton;
    @FXML
    private Button removeFromBasketButton;
    @FXML
    private Button submitOrderButton;

    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final ObservableList<Basket> basketList = FXCollections.observableArrayList();

    private final ObjectMapper objectMapper = JacksonConfig.createObjectMapper();

    public void initialize() {

        // Binding
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().pricePerUnitProperty().asObject());

        basketIdColumn.setCellValueFactory(cellData -> cellData.getValue().productIdProperty().asObject());
        basketCodeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
        basketQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        basketPriceColumn.setCellValueFactory(cellData -> cellData.getValue().pricePerUnitProperty().asObject());

        productTable.setItems(products);
        basketTable.setItems(basketList);

        // Load Needed Data
        fetchProducts();
    }

    @FXML
    private void handleAddToBasket() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Prompt user for quantity
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.initOwner(HospiApplication.getPrimaryStage());
            dialog.setTitle("Add to Basket");
            dialog.setHeaderText("Add Product to Basket");
            dialog.setContentText("Enter quantity:");

            dialog.showAndWait().ifPresent(input -> {
                try {
                    int quantity = Integer.parseInt(input);
                    if (quantity <= 0) {
                        HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "Quantity must be greater than zero.");
                        return;
                    }

                    // Check if product is already in the basket
                    Basket existing = basketList.stream()
                            .filter(p -> p.productIdProperty().getValue().equals(selected.getId()))
                            .findFirst()
                            .orElse(null);

                    if (existing != null) {
                        existing.setQuantity(existing.getQuantity() + quantity);
                    } else {
                        basketList.add(new Basket(selected.getId(), selected.getCode(), selected.getMu(),
                                quantity, selected.getPricePerUnit()));
                    }
                    basketTable.refresh();
                } catch (NumberFormatException e) {
                    HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for quantity.");
                }
            });
        }else{
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "Please select a valid product");
        }
    }


    @FXML
    private void handleSubmitOrder() {

        if(basketList.isEmpty()){
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Order Failed", "Your Basket is Empty");
            return;
        }

        // Convert basket to BasketOrdered array
        List<BasketOrdered> basketOrdered = basketList.stream()
                .map(product -> new BasketOrdered(product.getProductId(), product.getQuantity()))
                .collect(Collectors.toList());


        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {

                String json = objectMapper.writeValueAsString(basketOrdered);
                ServiceResponse response = ApiService.sendPostRequest("order", json).join();
                if (response.getStatusCode() == 200) {
                    // HospiApplication.showAlert(Alert.AlertType.INFORMATION, "Order Submitted", "Your order has been submitted successfully.");
                    return null;
                } else {
                    HospiApplication.showAlert(Alert.AlertType.ERROR, "Submission Failed", "Failed to submit order: " + response.getBody());
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            fetchProducts();
            basketList.clear();
            HospiApplication.showAlert(Alert.AlertType.INFORMATION, "Order Submitted", "Your order has been submitted successfully.");
        });

        new Thread(task).start();

    }


    @FXML
    private void handleRemoveFromBasket() {
        Basket selected = basketTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            basketList.remove(selected);
            basketTable.refresh();
        }else{
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "Please select a valid product");
        }
    }

    private void fetchProducts() {
        System.out.println("Inside Fetch Function");
        Task<ObservableList<Product>> task = new Task<>() {
            @Override
            protected ObservableList<Product> call() throws Exception {
                System.out.println("Inside Call Function");

                // Wrap the API call and deserialization in try-catch for better error handling
                try {
                    // Making the GET request to fetch the categories
                    String response = ApiService.sendGetRequest("product").join().getBody();
                    System.out.println("API Response: " + response);  // Log the raw response

                    if (response == null || response.isEmpty()) {
                        System.out.println("Error: No data received from the API.");
                        return FXCollections.emptyObservableList();  // Return empty list if no data
                    }

                    // Deserialization of the response into an array of Category objects
                    Product[] productArray = objectMapper.readValue(response, Product[].class);
                    System.out.println("Deserialized : " + Arrays.toString(productArray));  // Log the deserialized roles

                    // Return the list of Category as an observable list
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

}
