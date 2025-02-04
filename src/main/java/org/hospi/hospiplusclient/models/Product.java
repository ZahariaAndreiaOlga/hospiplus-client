package org.hospi.hospiplusclient.models;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.util.Set;

public class Product {

    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty code = new SimpleStringProperty();
    private StringProperty mu = new SimpleStringProperty();
    private IntegerProperty quantity = new SimpleIntegerProperty();
    private IntegerProperty criticalQuantity = new SimpleIntegerProperty();
    private FloatProperty pricePerUnit = new SimpleFloatProperty();
    private ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private IntegerProperty categoryId = new SimpleIntegerProperty();
    private Set<ProductHistory> productRecords;

    public Product() {}

    public IntegerProperty idProperty() { return id; }
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public StringProperty codeProperty() { return code; }
    public String getCode() { return code.get(); }
    public void setCode(String code) { this.code.set(code); }

    public StringProperty muProperty() { return mu; }
    public String getMu() { return mu.get(); }
    public void setMu(String mu) { this.mu.set(mu); }

    public IntegerProperty quantityProperty() { return quantity; }
    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }

    public IntegerProperty criticalQuantityProperty() { return criticalQuantity; }
    public int getCriticalQuantity() { return criticalQuantity.get(); }
    public void setCriticalQuantity(int criticalQuantity) { this.criticalQuantity.set(criticalQuantity); }

    public FloatProperty pricePerUnitProperty() { return pricePerUnit; }
    public float getPricePerUnit() { return pricePerUnit.get(); }
    public void setPricePerUnit(float pricePerUnit) { this.pricePerUnit.set(pricePerUnit); }

    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }
    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }

    public ObjectProperty<LocalDateTime> updatedAtProperty() { return updatedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt.get(); }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt.set(updatedAt); }

    public IntegerProperty categoryIdProperty() { return categoryId; }
    public int getCategoryId() { return categoryId.get(); }
    public void setCategoryId(int categoryId) { this.categoryId.set(categoryId); }

    public Set<ProductHistory> getProductRecords() { return productRecords; }
    public void setProductRecords(Set<ProductHistory> productRecords) { this.productRecords = productRecords; }
}
