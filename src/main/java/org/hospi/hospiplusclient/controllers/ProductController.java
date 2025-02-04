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
import org.hospi.hospiplusclient.models.Category;
import org.hospi.hospiplusclient.models.Product;
import org.hospi.hospiplusclient.models.Role;
import org.hospi.hospiplusclient.services.ApiService;

import java.util.Arrays;

public class ProductController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Integer> idColumn;
    @FXML
    private TableColumn<Product, String> codeColumn;
    @FXML
    private TableColumn<Product, String> muColumn;
    @FXML
    private TableColumn<Product, Integer> quantityColumn;
    @FXML
    private TableColumn<Product, Float> priceColumn;
    @FXML
    private TextField codeField;
    @FXML
    private TextField muField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField criticalQuantityField;
    @FXML
    private TextField priceField;
    @FXML
    private ComboBox<Category> categoryComboBox;

    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private FilteredList<Product> filteredProducts = new FilteredList<>(products, p -> true);

    private final ObservableList<Category> categories = FXCollections.observableArrayList();
    private final ObjectMapper objectMapper = JacksonConfig.createObjectMapper();

    @FXML
    public void initialize(){

        // Binding
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
        muColumn.setCellValueFactory(cellData -> cellData.getValue().muProperty());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().pricePerUnitProperty().asObject());

        productTable.setItems(filteredProducts);

        // Load Needed Data
        fetchProducts();
        fetchCategories();

        // Add listeners
        productTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                codeField.setText(newValue.getCode());
                muField.setText(newValue.getMu());
                quantityField.setText(String.valueOf(newValue.getQuantity()));
                criticalQuantityField.setText(String.valueOf(newValue.getCriticalQuantity()));
                priceField.setText(String.valueOf(newValue.getPricePerUnit()));

                int categoryId = newValue.idProperty().getValue();
                for (Category category : categoryComboBox.getItems()) {
                    if (category.idProperty().getValue() == categoryId) {
                        categoryComboBox.setValue(category);
                        break;
                    }
                }
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
    private void handleAdd() {

        if (codeField.getText().isEmpty() || muField.getText().isEmpty() || quantityField.getText().isEmpty() || criticalQuantityField.getText().isEmpty() ||
                priceField.getText().isEmpty() || categoryComboBox.getValue() == null ) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        String codeFieldText = codeField.getText();
        String muFieldText = muField.getText();
        Integer quantityFieldText = Integer.parseInt(quantityField.getText());
        Integer criticalQuantityFieldText = Integer.parseInt(criticalQuantityField.getText());
        Float priceFieldText = Float.parseFloat(priceField.getText());
        Category selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No category selected");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {

                Product product = new Product();
                product.setCode(codeFieldText);
                product.setMu(muFieldText);
                product.setQuantity(quantityFieldText);
                product.setCriticalQuantity(criticalQuantityFieldText);
                product.setPricePerUnit(priceFieldText);
                product.setCategoryId(selectedCategory.getId());

                String json = objectMapper.writeValueAsString(product);
                ApiService.sendPostRequest("product", json).join();
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            fetchProducts();
            handleClear();
        });
        new Thread(task).start();
    }

    @FXML
    private void handleUpdate() {

        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No product selected for update");
            return;
        }

        if (codeField.getText().isEmpty() || muField.getText().isEmpty() || quantityField.getText().isEmpty() || criticalQuantityField.getText().isEmpty() ||
                priceField.getText().isEmpty() || categoryComboBox.getValue() == null ) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        String codeFieldText = codeField.getText();
        String muFieldText = muField.getText();
        Integer quantityFieldText = Integer.parseInt(quantityField.getText());
        Integer criticalQuantityFieldText = Integer.parseInt(criticalQuantityField.getText());
        Float priceFieldText = Float.parseFloat(priceField.getText());
        Category selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {

                selectedProduct.setCode(codeFieldText);
                selectedProduct.setMu(muFieldText);
                selectedProduct.setQuantity(quantityFieldText);
                selectedProduct.setCriticalQuantity(criticalQuantityFieldText);
                selectedProduct.setPricePerUnit(priceFieldText);
                selectedProduct.setCategoryId(selectedCategory.getId());

                String json = objectMapper.writeValueAsString(selectedProduct);
                ApiService.sendPutRequest("product", selectedProduct.getId(), json).join();
                return null;
            }
        };
        task.setOnSucceeded(event -> {fetchProducts(); handleClear();});
        new Thread(task).start();
    }

    @FXML
    private void handleDelete() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No product selected for deletion");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ApiService.sendDeleteRequest("product", selectedProduct.getId()).join();
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            fetchProducts();
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
                    // Making the GET request to fetch the products
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

    private void fetchCategories(){
        System.out.println("Inside Fetch Function");
        Task<ObservableList<Category>> task = new Task<>() {
            @Override
            protected ObservableList<Category> call() throws Exception {
                System.out.println("Inside Call Function");

                // Wrap the API call and deserialization in try-catch for better error handling
                try {
                    // Making the GET request to fetch the categories
                    String response = ApiService.sendGetRequest("category").join().getBody();
                    System.out.println("API Response: " + response);  // Log the raw response

                    if (response == null || response.isEmpty()) {
                        System.out.println("Error: No data received from the API.");
                        return FXCollections.emptyObservableList();  // Return empty list if no data
                    }

                    // Deserialization of the response into an array of  objects
                    Category[] categoryArray = objectMapper.readValue(response, Category[].class);
                    System.out.println("Deserialized : " + Arrays.toString(categoryArray));  // Log the deserialized roles

                    // Return the list of categories as an observable list
                    return FXCollections.observableArrayList(categoryArray);

                } catch (Exception e) {
                    // Log any exceptions that occur during the process
                    System.out.println("Error during API call or deserialization: " + e.getMessage());
                    e.printStackTrace();  // Print stack trace for better debugging
                    return FXCollections.emptyObservableList();  // Return an empty list in case of an error
                }
            }
        };

        task.setOnSucceeded(event -> categories.setAll(task.getValue()));
        categoryComboBox.setItems(categories);
        new Thread(task).start();
    }

    @FXML
    private void handleClear() {
        codeField.clear();
        muField.clear();
        quantityField.clear();
        criticalQuantityField.clear();
        priceField.clear();
        categoryComboBox.setValue(null);
    }
}
