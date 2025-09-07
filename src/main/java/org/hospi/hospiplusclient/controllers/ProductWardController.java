package org.hospi.hospiplusclient.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.hospi.hospiplusclient.HospiApplication;
import org.hospi.hospiplusclient.configuration.JacksonConfig;
import org.hospi.hospiplusclient.models.Product;
import org.hospi.hospiplusclient.models.ProductWard;
import org.hospi.hospiplusclient.models.ProductWardHistory;
import org.hospi.hospiplusclient.models.Ward;
import org.hospi.hospiplusclient.services.ApiService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

public class ProductWardController {

    @FXML private ComboBox<Ward> wardComboBox;

    @FXML private TableView<ProductWard> productTable;
    @FXML private TableColumn<ProductWard, String> codeColumn;
    @FXML private TableColumn<ProductWard, Integer> quantityColumn;
    @FXML private TableColumn<ProductWard, Integer> criticalQuantityColumn;

    @FXML private TableView<ProductWardHistory> productHistoryTable;
    @FXML private TableColumn<ProductWardHistory, Integer> historyQuantityColumn;
    @FXML private TableColumn<ProductWardHistory, LocalDateTime> historyDateColumn;

    private final ObservableList<Ward> wards = FXCollections.observableArrayList();
    private final ObservableList<ProductWard> wardProducts = FXCollections.observableArrayList();
    private final ObservableList<ProductWardHistory> productHistories = FXCollections.observableArrayList();
    private final ObjectMapper objectMapper = JacksonConfig.createObjectMapper();

    @FXML
    public void initialize() {
        // Bind product table columns
        codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        criticalQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().criticalQuantityProperty().asObject());
        productTable.setItems(wardProducts);

        // Bind product history table columns
        historyQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        historyDateColumn.setCellValueFactory(cellData -> cellData.getValue().createdAtProperty());
        productHistoryTable.setItems(productHistories);

        // Load wards
        fetchWards();

        // Update products when ward selected
        wardComboBox.setOnAction(event -> updateProductsForWard());

        // Update product history when a product is selected
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fetchProductHistory();
            } else {
                productHistories.clear();
            }
        });
    }

    private void fetchWards() {
        Task<ObservableList<Ward>> task = new Task<>() {
            @Override
            protected ObservableList<Ward> call() throws Exception {
                try {
                    String response = ApiService.sendGetRequest("ward").join().getBody();
                    if (response == null || response.isEmpty()) return FXCollections.emptyObservableList();

                    Ward[] wardArray = objectMapper.readValue(response, Ward[].class);
                    for (Ward w : wardArray) {
                        if (w.getProductWards() == null) w.setProductWards(Set.of());
                    }
                    return FXCollections.observableArrayList(wardArray);
                } catch (Exception e) {
                    e.printStackTrace();
                    return FXCollections.emptyObservableList();
                }
            }
        };

        task.setOnSucceeded(event -> {
            wards.setAll(task.getValue());
            wardComboBox.setItems(wards);
            if (!wards.isEmpty()) {
                wardComboBox.getSelectionModel().selectFirst();
                updateProductsForWard();
            }
        });

        new Thread(task).start();
    }

    private void updateProductsForWard() {
        Ward selectedWard = wardComboBox.getValue();
        if (selectedWard != null && selectedWard.getProductWards() != null) {
            wardProducts.setAll(selectedWard.getProductWards());
        } else {
            wardProducts.clear();
        }
        productHistories.clear();
    }

    private void fetchProductHistory() {
        ProductWard selectedProductWard = productTable.getSelectionModel().getSelectedItem();
        Task<ObservableList<ProductWardHistory>> task = new Task<>() {
            @Override
            protected ObservableList<ProductWardHistory> call() throws Exception {
                try {
                    String response = ApiService.sendGetRequest("product-ward/"+selectedProductWard.getId()+"/history").join().getBody();
                    if (response == null || response.isEmpty()) return FXCollections.emptyObservableList();

                    ProductWardHistory[] histories = objectMapper.readValue(response, ProductWardHistory[].class);
                    return FXCollections.observableArrayList(histories);
                } catch (Exception e) {
                    e.printStackTrace();
                    return FXCollections.emptyObservableList();
                }
            }
        };

        task.setOnSucceeded(event -> productHistories.setAll(task.getValue()));
        new Thread(task).start();
    }
}
