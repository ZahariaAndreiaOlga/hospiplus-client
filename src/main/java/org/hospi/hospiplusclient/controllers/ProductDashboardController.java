package org.hospi.hospiplusclient.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.hospi.hospiplusclient.configuration.JacksonConfig;
import org.hospi.hospiplusclient.models.Product;
import org.hospi.hospiplusclient.models.ProductHistory;
import org.hospi.hospiplusclient.services.ApiService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.TextStyle;
import java.util.*;

public class ProductDashboardController {
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Integer> idColumn;
    @FXML
    private TableColumn<Product, String> codeColumn;
    @FXML
    private TableColumn<Product, Integer> quantityColumn;
    @FXML
    private TableColumn<Product, Integer> criticalQuantityColumn;
    @FXML
    private TableColumn<Product, Float> pricePerUnitColumn;

    @FXML
    private BarChart<String, Number> stockHistogram;

    private ObservableList<Product> products = FXCollections.observableArrayList();

    private ObjectMapper objectMapper = JacksonConfig.createObjectMapper();


    public void initialize() {

        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        criticalQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().criticalQuantityProperty().asObject());
        pricePerUnitColumn.setCellValueFactory(cellData -> cellData.getValue().pricePerUnitProperty().asObject());

        productTable.setItems(products);

        // Load Needed Data
        fetchProducts();

        productTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateHistogram(newValue);
            }
        });

    }

    private void updateHistogram(Product product) {
        // Clear previous data
        stockHistogram.getData().clear();

        // Insert product history inside an Observable List
        ObservableList<ProductHistory> historyData = FXCollections.observableArrayList(product.getProductRecords());

        // Sort the history data by date
        historyData.sort(Comparator.comparing(ProductHistory::getCreatedAt));

        // Calculate the initial stock
        int initialStock = product.getQuantity(); // Start with the current stock
        for (ProductHistory history : historyData) {
            initialStock -= history.getQuantity(); // Reverse the changes to determine initial stock
        }

        // Calculate total stock month by month
        Map<String, Integer> totalStockByMonth = new LinkedHashMap<>();
        int currentStock = initialStock; // Start from the initial stock

        for (ProductHistory history : historyData) {
            String month = history.getCreatedAt().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            // Update stock based on history record
            currentStock += history.getQuantity();

            // Store the stock for the month
            totalStockByMonth.put(month, currentStock);
        }

        // Create series for histogram
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Stock");

        // Populate series with total stock data
        for (Map.Entry<String, Integer> entry : totalStockByMonth.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Add series to the chart
        stockHistogram.getData().add(series);

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

    @FXML
    public void downloadCsv() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Use Platform.runLater to open FileChooser on the JavaFX thread
                Platform.runLater(() -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save CSV File");
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
                    File file = fileChooser.showSaveDialog(null);

                    if (file != null) {
                        try (FileWriter writer = new FileWriter(file)) {
                            // Write CSV header
                            writer.write("ID,Code,MU,Quantity,CriticalQuantity,PricePerUnit,CreatedAt,UpdatedAt,CategoryId\n");

                            // Write each product
                            for (Product product : products) {
                                writer.write(String.format(Locale.US, "%d,%s,%s,%d,%d,%.2f,%s,%s,%d\n",
                                        product.getId(),
                                        product.getCode(),
                                        product.getMu(),
                                        product.getQuantity(),
                                        product.getCriticalQuantity(),
                                        product.getPricePerUnit(),
                                        product.getCreatedAt(),
                                        product.getUpdatedAt(),
                                        product.getCategoryId()));
                            }

                            System.out.println("CSV file saved successfully!");
                        } catch (IOException e) {
                            System.err.println("Error writing CSV file: " + e.getMessage());
                        }
                    }
                });

                return null;
            }
        };

        task.setOnSucceeded(event -> System.out.println("Task completed successfully!"));
        task.setOnFailed(event -> System.err.println("Task failed: " + task.getException().getMessage()));

        new Thread(task).start();
    }

}
