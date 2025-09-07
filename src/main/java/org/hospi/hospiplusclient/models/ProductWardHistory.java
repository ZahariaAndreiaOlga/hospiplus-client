package org.hospi.hospiplusclient.models;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class ProductWardHistory {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty productWardId = new SimpleIntegerProperty();
    private final IntegerProperty quantity = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();

    public ProductWardHistory() {}

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public int getProductWardId() { return productWardId.get(); }
    public void setProductWardId(int value) { productWardId.set(value); }
    public IntegerProperty productWardIdProperty() { return productWardId; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int value) { quantity.set(value); }
    public IntegerProperty quantityProperty() { return quantity; }

    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime value) { createdAt.set(value); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }
}