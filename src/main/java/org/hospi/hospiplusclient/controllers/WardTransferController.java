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
import org.hospi.hospiplusclient.models.WardTransfer;
import org.hospi.hospiplusclient.models.Ward;
import org.hospi.hospiplusclient.models.Product;
import org.hospi.hospiplusclient.services.ApiService;

public class WardTransferController {

    @FXML private TextField searchField;
    @FXML private TableView<WardTransfer> transferTable;
    @FXML private TableColumn<WardTransfer, Integer> idColumn;
    @FXML private TableColumn<WardTransfer, String> giverColumn;
    @FXML private TableColumn<WardTransfer, String> receiverColumn;
    @FXML private TableColumn<WardTransfer, String> productColumn;
    @FXML private TableColumn<WardTransfer, Integer> quantityColumn;
    @FXML private TableColumn<WardTransfer, String> typeColumn;

    @FXML private ComboBox<Ward> giverComboBox;
    @FXML private ComboBox<Ward> receiverComboBox;
    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField quantityField;
    @FXML private ComboBox<String> typeComboBox;

    private final ObservableList<WardTransfer> transfers = FXCollections.observableArrayList();
    private FilteredList<WardTransfer> filteredTransfers = new FilteredList<>(transfers, t -> true);
    private final ObservableList<Ward> wards = FXCollections.observableArrayList();
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final ObjectMapper objectMapper = JacksonConfig.createObjectMapper();

    @FXML
    public void initialize() {
        // Table bindings
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        giverColumn.setCellValueFactory(cellData -> {
            Ward w = wards.stream().filter(ward -> ward.getId() == cellData.getValue().getGiver()).findFirst().orElse(null);
            return new ReadOnlyStringWrapper(w != null ? w.getWardName() : "");
        });
        receiverColumn.setCellValueFactory(cellData -> {
            Ward w = wards.stream().filter(ward -> ward.getId() == cellData.getValue().getReciever()).findFirst().orElse(null);
            return new ReadOnlyStringWrapper(w != null ? w.getWardName() : "");
        });
        productColumn.setCellValueFactory(cellData -> {
            Product p = products.stream().filter(pr -> pr.getId() == cellData.getValue().getProductId()).findFirst().orElse(null);
            return new ReadOnlyStringWrapper(p != null ? p.getCode() : "");
        });
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().transferTypeProperty());

        transferTable.setItems(filteredTransfers);

        fetchTransfers();
        fetchWards();
        fetchProducts();

        typeComboBox.setItems(FXCollections.observableArrayList("Internal Transfer", "Emergency Transfer"));

        // Table selection listener
        transferTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                quantityField.setText(String.valueOf(newSel.getQuantity()));
                wards.stream().filter(w -> w.getId() == newSel.getGiver()).findFirst().ifPresent(giverComboBox::setValue);
                wards.stream().filter(w -> w.getId() == newSel.getReciever()).findFirst().ifPresent(receiverComboBox::setValue);
                products.stream().filter(p -> p.getId() == newSel.getProductId()).findFirst().ifPresent(productComboBox::setValue);
                typeComboBox.setValue(newSel.getTransferType());
            }
        });

        // Search listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredTransfers.setPredicate(t -> {
                if (newVal == null || newVal.isEmpty()) return true;
                Product p = products.stream().filter(pr -> pr.getId() == t.getProductId()).findFirst().orElse(null);
                return p != null && p.getCode().toLowerCase().contains(newVal.toLowerCase());
            });
        });
    }

    private void fetchTransfers() {
        Task<ObservableList<WardTransfer>> task = new Task<>() {
            @Override
            protected ObservableList<WardTransfer> call() throws Exception {
                String response = ApiService.sendGetRequest("ward-transfer").join().getBody();
                if (response == null || response.isEmpty()) return FXCollections.emptyObservableList();
                WardTransfer[] arr = objectMapper.readValue(response, WardTransfer[].class);
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
            giverComboBox.setItems(wards);
            receiverComboBox.setItems(wards);
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
            products.setAll(task.getValue());
            productComboBox.setItems(products);
        });
        new Thread(task).start();
    }

    @FXML
    private void handleAdd() {
        if (giverComboBox.getValue() == null || receiverComboBox.getValue() == null || productComboBox.getValue() == null ||
                quantityField.getText().isEmpty() || typeComboBox.getValue() == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        Ward wardGiver = giverComboBox.getSelectionModel().getSelectedItem();
        Ward wardReceiver = receiverComboBox.getSelectionModel().getSelectedItem();
        Product product = productComboBox.getSelectionModel().getSelectedItem();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                WardTransfer transfer = new WardTransfer();
                transfer.setGiver(wardGiver.getId());
                transfer.setReciever(wardReceiver.getId());
                transfer.setProductId(product.getId());
                transfer.setQuantity(Integer.parseInt(quantityField.getText()));
                transfer.setTransferType(typeComboBox.getValue());

                String json = objectMapper.writeValueAsString(transfer);
                ApiService.sendPostRequest("ward-transfer", json).join();
                return null;
            }
        };
        task.setOnSucceeded(e -> { fetchTransfers(); handleClear(); });
        new Thread(task).start();
    }

    @FXML
    private void handleUpdate() {
        WardTransfer selected = transferTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No transfer selected for update");
            return;
        }

        Ward wardGiver = giverComboBox.getSelectionModel().getSelectedItem();
        Ward wardReceiver = receiverComboBox.getSelectionModel().getSelectedItem();
        Product product = productComboBox.getSelectionModel().getSelectedItem();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                selected.setGiver(wardGiver.getId());
                selected.setReciever(wardReceiver.getId());
                selected.setProductId(product.getId());
                selected.setQuantity(Integer.parseInt(quantityField.getText()));
                selected.setTransferType(typeComboBox.getValue());

                String json = objectMapper.writeValueAsString(selected);
                ApiService.sendPutRequest("ward-transfer", selected.getId(), json).join();
                return null;
            }
        };
        task.setOnSucceeded(e -> { fetchTransfers(); handleClear(); });
        new Thread(task).start();
    }

    @FXML
    private void handleDelete() {
        WardTransfer selected = transferTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            HospiApplication.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "No transfer selected for deletion");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ApiService.sendDeleteRequest("ward-transfer", selected.getId()).join();
                return null;
            }
        };
        task.setOnSucceeded(e -> { fetchTransfers(); handleClear(); });
        new Thread(task).start();
    }

    @FXML
    private void handleClear() {
        giverComboBox.setValue(null);
        receiverComboBox.setValue(null);
        productComboBox.setValue(null);
        quantityField.clear();
        typeComboBox.setValue(null);
    }
}
