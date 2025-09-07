package org.hospi.hospiplusclient.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import org.hospi.hospiplusclient.HospiApplication;
import org.hospi.hospiplusclient.configuration.JacksonConfig;
import org.hospi.hospiplusclient.models.ProductWard;
import org.hospi.hospiplusclient.models.ProductWardHistory;
import org.hospi.hospiplusclient.services.ApiService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class ProductWardHistoryController {

    @FXML private TextField searchField;
    @FXML private TableView<ProductWardHistory> historyTable;
    @FXML private TableColumn<ProductWardHistory, Integer> idColumn;
    @FXML private TableColumn<ProductWardHistory, String> productWardColumn;
    @FXML private TableColumn<ProductWardHistory, Integer> quantityColumn;
    @FXML private TableColumn<ProductWardHistory, String> createdAtColumn;
    @FXML private ComboBox<ProductWard> productWardComboBox;
    @FXML private TextField quantityField;

    private final ObservableList<ProductWardHistory> histories = FXCollections.observableArrayList();
    private FilteredList<ProductWardHistory> filteredHistories = new FilteredList<>(histories, h -> true);
    private final ObservableList<ProductWard> productWards = FXCollections.observableArrayList();
    private final ObjectMapper objectMapper = JacksonConfig.createObjectMapper();

    @FXML
    public void initialize() {
        // Table bindings
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        productWardColumn.setCellValueFactory(cellData -> {
            ProductWard pw = productWards.stream().filter(p -> p.getId() == cellData.getValue().getProductWardId()).findFirst().orElse(null);
            return new ReadOnlyStringWrapper(pw != null ? pw.getCode() : "");
        });
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        createdAtColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
                cellData.getValue().getCreatedAt() != null ? cellData.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : ""
        ));

        historyTable.setItems(filteredHistories);

        fetchHistories();
        fetchProductWards();

        // Table selection listener
        historyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                quantityField.setText(String.valueOf(newSel.getQuantity()));
                productWards.stream()
                        .filter(pw -> pw.getId() == newSel.getProductWardId())
                        .findFirst()
                        .ifPresent(productWardComboBox::setValue);
            }
        });

        // Search listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredHistories.setPredicate(h -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return String.valueOf(h.getQuantity()).contains(newVal);
            });
        });
    }

    private void fetchHistories() {
        Task<ObservableList<ProductWardHistory>> task = new Task<>() {
            @Override
            protected ObservableList<ProductWardHistory> call() throws Exception {
                String response = ApiService.sendGetRequest("product-ward-history").join().getBody();
                if (response == null || response.isEmpty()) return FXCollections.emptyObservableList();
                ProductWardHistory[] arr = objectMapper.readValue(response, ProductWardHistory[].class);
                return FXCollections.observableArrayList(arr);
            }
        };
        task.setOnSucceeded(e -> histories.setAll(task.getValue()));
        new Thread(task).start();
    }

    private void fetchProductWards() {
        Task<ObservableList<ProductWard>> task = new Task<>() {
            @Override
            protected ObservableList<ProductWard> call() throws Exception {
                String response = ApiService.sendGetRequest("product-ward").join().getBody();
                if (response == null || response.isEmpty()) return FXCollections.observableArrayList();
                ProductWard[] arr = objectMapper.readValue(response, ProductWard[].class);
                return FXCollections.observableArrayList(arr);
            }
        };
        task.setOnSucceeded(e -> productWards.setAll(task.getValue()));
        productWardComboBox.setItems(productWards);
        new Thread(task).start();
    }

    @FXML
    private void handleAdd() {
        if (productWardComboBox.getValue() == null || quantityField.getText().isEmpty()) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ProductWardHistory history = new ProductWardHistory();
                history.setProductWardId(productWardComboBox.getValue().getId());
                history.setQuantity(Integer.parseInt(quantityField.getText()));
                history.setCreatedAt(LocalDateTime.now());

                String json = objectMapper.writeValueAsString(history);
                ApiService.sendPostRequest("product-ward-history", json).join();
                return null;
            }
        };
        task.setOnSucceeded(e -> { fetchHistories(); handleClear(); });
        new Thread(task).start();
    }

    @FXML
    private void handleUpdate() {
        ProductWardHistory selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No history selected for update");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                selected.setQuantity(Integer.parseInt(quantityField.getText()));
                selected.setProductWardId(productWardComboBox.getValue().getId());

                String json = objectMapper.writeValueAsString(selected);
                ApiService.sendPutRequest("product-ward-history", selected.getId(), json).join();
                return null;
            }
        };
        task.setOnSucceeded(e -> { fetchHistories(); handleClear(); });
        new Thread(task).start();
    }

    @FXML
    private void handleDelete() {
        ProductWardHistory selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No history selected for deletion");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ApiService.sendDeleteRequest("product-ward-history", selected.getId()).join();
                return null;
            }
        };
        task.setOnSucceeded(e -> { fetchHistories(); handleClear(); });
        new Thread(task).start();
    }

    @FXML
    private void handleClear() {
        quantityField.clear();
        productWardComboBox.setValue(null);
    }
}
