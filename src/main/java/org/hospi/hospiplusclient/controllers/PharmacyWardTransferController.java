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
import org.hospi.hospiplusclient.models.PharmacyWardTransfer;
import org.hospi.hospiplusclient.models.Ward;
import org.hospi.hospiplusclient.models.Product;
import org.hospi.hospiplusclient.services.ApiService;

public class PharmacyWardTransferController {

    @FXML private TextField searchField;
    @FXML private TableView<PharmacyWardTransfer> transferTable;
    @FXML private TableColumn<PharmacyWardTransfer, Integer> idColumn;
    @FXML private TableColumn<PharmacyWardTransfer, String> wardColumn;
    @FXML private TableColumn<PharmacyWardTransfer, String> productColumn;
    @FXML private TableColumn<PharmacyWardTransfer, Integer> quantityColumn;
    @FXML private TableColumn<PharmacyWardTransfer, String> typeColumn;

    @FXML private ComboBox<Ward> wardComboBox;
    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField quantityField;
    @FXML private ComboBox<String> typeComboBox;

    private final ObservableList<PharmacyWardTransfer> transfers = FXCollections.observableArrayList();
    private FilteredList<PharmacyWardTransfer> filteredTransfers = new FilteredList<>(transfers, t -> true);
    private final ObservableList<Ward> wards = FXCollections.observableArrayList();
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final ObjectMapper objectMapper = JacksonConfig.createObjectMapper();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        wardColumn.setCellValueFactory(cellData -> {
            Ward w = wards.stream().filter(ward -> ward.getId() == cellData.getValue().getWardId()).findFirst().orElse(null);
            return new ReadOnlyStringWrapper(w != null ? w.getWardName() : "");
        });
        productColumn.setCellValueFactory(cellData -> {
            Product p = products.stream().filter(pr -> pr.getId() == cellData.getValue().getProductId()).findFirst().orElse(null);
            return new ReadOnlyStringWrapper(p != null ? p.getCode() : "");
        });
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeOfTransferProperty());

        transferTable.setItems(filteredTransfers);

        fetchTransfers();
        fetchWards();
        fetchProducts();

        typeComboBox.setItems(FXCollections.observableArrayList("To Ward", "To Pharmacy"));

        transferTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                quantityField.setText(String.valueOf(newSel.getQuantity()));
                wards.stream().filter(w -> w.getId() == newSel.getWardId()).findFirst().ifPresent(wardComboBox::setValue);
                products.stream().filter(p -> p.getId() == newSel.getProductId()).findFirst().ifPresent(productComboBox::setValue);
                typeComboBox.setValue(newSel.getTypeOfTransfer());
            }
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredTransfers.setPredicate(t -> {
                if (newVal == null || newVal.isEmpty()) return true;
                Product p = products.stream().filter(pr -> pr.getId() == t.getProductId()).findFirst().orElse(null);
                return p != null && p.getCode().toLowerCase().contains(newVal.toLowerCase());
            });
        });
    }

    private void fetchTransfers() {
        Task<ObservableList<PharmacyWardTransfer>> task = new Task<>() {
            @Override
            protected ObservableList<PharmacyWardTransfer> call() throws Exception {
                String response = ApiService.sendGetRequest("pharmacy-ward-transfer").join().getBody();
                if (response == null || response.isEmpty()) return FXCollections.emptyObservableList();
                PharmacyWardTransfer[] arr = objectMapper.readValue(response, PharmacyWardTransfer[].class);
                return FXCollections.observableArrayList(arr);
            }
        };
        task.setOnSucceeded(e -> transfers.setAll(task.getValue()));
        new Thread(task).start();
    }

    private void fetchWards() {
        Task<ObservableList<Ward>> task = new Task<>() {
            @Override
            protected ObservableList<Ward> call() throws Exception {
                String response = ApiService.sendGetRequest("ward").join().getBody();
                if (response == null || response.isEmpty()) return FXCollections.observableArrayList();
                Ward[] arr = objectMapper.readValue(response, Ward[].class);
                return FXCollections.observableArrayList(arr);
            }
        };
        task.setOnSucceeded(e -> {
            wards.setAll(task.getValue());
            wardComboBox.setItems(wards);
        });
        new Thread(task).start();
    }

    private void fetchProducts() {
        Task<ObservableList<Product>> task = new Task<>() {
            @Override
            protected ObservableList<Product> call() throws Exception {
                String response = ApiService.sendGetRequest("product").join().getBody();
                if (response == null || response.isEmpty()) return FXCollections.observableArrayList();
                Product[] arr = objectMapper.readValue(response, Product[].class);
                return FXCollections.observableArrayList(arr);
            }
        };
        task.setOnSucceeded(e -> {
            products.setAll(task.getValue());       // update the observable list
            productComboBox.setItems(products);     // assign it to the ComboBox
        });
        new Thread(task).start();
    }

    @FXML
    private void handleAdd() {
        if (wardComboBox.getValue() == null || productComboBox.getValue() == null || quantityField.getText().isEmpty() || typeComboBox.getValue() == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        Ward selectedWard = wardComboBox.getSelectionModel().getSelectedItem();
        Product selectedProduct = productComboBox.getSelectionModel().getSelectedItem();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                PharmacyWardTransfer transfer = new PharmacyWardTransfer();
                transfer.setWardId(selectedWard.getId());
                transfer.setProductId(selectedProduct.getId());
                transfer.setQuantity(Integer.parseInt(quantityField.getText()));
                transfer.setTypeOfTransfer(typeComboBox.getValue());

                String json = objectMapper.writeValueAsString(transfer);
                ApiService.sendPostRequest("pharmacy-ward-transfer", json).join();
                return null;
            }
        };
        task.setOnSucceeded(e -> { fetchTransfers(); handleClear(); });
        new Thread(task).start();
    }

    @FXML
    private void handleUpdate() {
        PharmacyWardTransfer selected = transferTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No transfer selected for update");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                selected.setWardId(wardComboBox.getValue().getId());
                selected.setProductId(productComboBox.getValue().getId());
                selected.setQuantity(Integer.parseInt(quantityField.getText()));
                selected.setTypeOfTransfer(typeComboBox.getValue());

                String json = objectMapper.writeValueAsString(selected);
                ApiService.sendPutRequest("pharmacy-ward-transfer", selected.getId(), json).join();
                return null;
            }
        };
        task.setOnSucceeded(e -> { fetchTransfers(); handleClear(); });
        new Thread(task).start();
    }

    @FXML
    private void handleDelete() {
        PharmacyWardTransfer selected = transferTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No transfer selected for deletion");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ApiService.sendDeleteRequest("pharmacy-ward-transfer", selected.getId()).join();
                return null;
            }
        };
        task.setOnSucceeded(e -> { fetchTransfers(); handleClear(); });
        new Thread(task).start();
    }

    @FXML
    private void handleClear() {
        wardComboBox.setValue(null);
        productComboBox.setValue(null);
        quantityField.clear();
        typeComboBox.setValue(null);
    }
}
