package org.hospi.hospiplusclient.models;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Set;

public class ProductWard {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty wardId = new SimpleIntegerProperty();
    private final StringProperty code = new SimpleStringProperty();
    private final IntegerProperty quantity = new SimpleIntegerProperty();
    private final IntegerProperty criticalQuantity = new SimpleIntegerProperty();
    private Set<ProductWardHistory> productWardHistories;

    public ProductWard() {}

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public int getWardId() { return wardId.get(); }
    public void setWardId(int value) { wardId.set(value); }
    public IntegerProperty wardIdProperty() { return wardId; }

    public String getCode() { return code.get(); }
    public void setCode(String value) { code.set(value); }
    public StringProperty codeProperty() { return code; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int value) { quantity.set(value); }
    public IntegerProperty quantityProperty() { return quantity; }

    public int getCriticalQuantity() { return criticalQuantity.get(); }
    public void setCriticalQuantity(int value) { criticalQuantity.set(value); }
    public IntegerProperty criticalQuantityProperty() { return criticalQuantity; }

    public Set<ProductWardHistory> getProductWardHistories() {
        return productWardHistories;
    }

    public void setProductWardHistories(Set<ProductWardHistory> productWardHistories) {
        this.productWardHistories = productWardHistories;
    }
    /*
    public void addProductWardHistories(ProductWardHistory productWardHistory){
        this.productWardHistories.add(productWardHistory);
    }

    public void removeProductWardHistories(ProductWardHistory productWardHistory){
        this.productWardHistories.remove(productWardHistory);
    }
     */

    @Override
    public String toString() {
        return code.get();
    }
}
