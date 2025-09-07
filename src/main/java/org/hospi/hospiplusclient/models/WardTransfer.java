package org.hospi.hospiplusclient.models;

import javafx.beans.property.*;

public class WardTransfer {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty giver = new SimpleIntegerProperty();
    private final IntegerProperty reciever = new SimpleIntegerProperty();
    private final IntegerProperty productId = new SimpleIntegerProperty();
    private final IntegerProperty quantity = new SimpleIntegerProperty();
    private final StringProperty transferType = new SimpleStringProperty();

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public int getGiver() { return giver.get(); }
    public void setGiver(int value) { giver.set(value); }
    public IntegerProperty giverProperty() { return giver; }

    public int getReciever() { return reciever.get(); }
    public void setReciever(int value) { reciever.set(value); }
    public IntegerProperty recieverProperty() { return reciever; }

    public int getProductId() { return productId.get(); }
    public void setProductId(int value) { productId.set(value); }
    public IntegerProperty productIdProperty() { return productId; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int value) { quantity.set(value); }
    public IntegerProperty quantityProperty() { return quantity; }

    public String getTransferType() { return transferType.get(); }
    public void setTransferType(String value) { transferType.set(value); }
    public StringProperty transferTypeProperty() { return transferType; }
}
