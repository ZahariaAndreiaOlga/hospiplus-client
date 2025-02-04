package org.hospi.hospiplusclient.models;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.util.Set;

public class Category {

    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty catgName = new SimpleStringProperty();
    private ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private Set<Product> products;

    public Category() {}

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getCatgName() {
        return catgName.get();
    }

    public void setCatgName(String catgName) {
        this.catgName.set(catgName);
    }

    public StringProperty catgNameProperty() {
        return catgName;
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt.get();
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt.set(updatedAt);
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return catgName.get();
    }
}
