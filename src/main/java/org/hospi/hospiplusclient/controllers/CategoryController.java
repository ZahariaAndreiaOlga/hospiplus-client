package org.hospi.hospiplusclient.controllers;

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
import org.hospi.hospiplusclient.models.Category;
import org.hospi.hospiplusclient.services.ApiService;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CategoryController {

    @FXML
    private TextField catgNameField;

    @FXML
    private TableView<Category> categoryTable;

    @FXML
    private TableColumn<Category, Integer> idColumn;

    @FXML
    private TableColumn<Category, String> catgNameColumn;

    private ObservableList<Category> categories = FXCollections.observableArrayList();

    private final ObjectMapper objectMapper = JacksonConfig.createObjectMapper();

    public void initialize() {
        // Binding
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        catgNameColumn.setCellValueFactory(cellData -> cellData.getValue().catgNameProperty());

        categoryTable.setItems(categories);
        fetchCategory();
    }

    @FXML
    private void handleAdd() {
        String catgName = catgNameField.getText();
        if (catgName.isEmpty()) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Category name cannot be empty");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Category category = new Category();
                category.setCatgName(catgName);
                String json = objectMapper.writeValueAsString(category);
                ApiService.sendPostRequest("category", json).join();
                return null;
            }
        };
        task.setOnSucceeded(event -> fetchCategory());
        new Thread(task).start();
    }

    @FXML
    private void handleUpdate() {
        Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No Category selected for update");
            return;
        }

        String catgName = catgNameField.getText();
        if (catgName.isEmpty()) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Category name cannot be empty");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                selectedCategory.setCatgName(catgName);
                String json = objectMapper.writeValueAsString(selectedCategory);
                ApiService.sendPutRequest("category", selectedCategory.getId(), json).join();
                return null;
            }
        };
        task.setOnSucceeded(event -> fetchCategory());
        new Thread(task).start();
    }

    @FXML
    private void handleDelete() {
        Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No Category selected for update");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ApiService.sendDeleteRequest("category", selectedCategory.getId()).join();
                return null;
            }
        };
        task.setOnSucceeded(event -> fetchCategory());
        new Thread(task).start();
    }

    private void fetchCategory() {
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

                    // Deserialization of the response into an array of Category objects
                    Category[] categoryArray = objectMapper.readValue(response, Category[].class);
                    System.out.println("Deserialized : " + Arrays.toString(categoryArray));  // Log the deserialized roles

                    // Return the list of Category as an observable list
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
        new Thread(task).start();
    }

    private void handleClear() {
        catgNameField.clear();
    }
}

