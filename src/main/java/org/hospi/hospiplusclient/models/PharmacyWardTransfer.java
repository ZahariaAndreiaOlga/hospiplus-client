package org.hospi.hospiplusclient.models;

import javafx.beans.property.*;

public class PharmacyWardTransfer {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty productId = new SimpleIntegerProperty();
    private final IntegerProperty wardId = new SimpleIntegerProperty();
    private final IntegerProperty quantity = new SimpleIntegerProperty();
    private final StringProperty typeOfTransfer = new SimpleStringProperty();

    public PharmacyWardTransfer() {}

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public int getProductId() { return productId.get(); }
    public void setProductId(int value) { productId.set(value); }
    public IntegerProperty productIdProperty() { return productId; }

    public int getWardId() { return wardId.get(); }
    public void setWardId(int value) { wardId.set(value); }
    public IntegerProperty wardIdProperty() { return wardId; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int value) { quantity.set(value); }
    public IntegerProperty quantityProperty() { return quantity; }

    public String getTypeOfTransfer() { return typeOfTransfer.get(); }
    public void setTypeOfTransfer(String value) { typeOfTransfer.set(value); }
    public StringProperty typeOfTransferProperty() { return typeOfTransfer; }
}
