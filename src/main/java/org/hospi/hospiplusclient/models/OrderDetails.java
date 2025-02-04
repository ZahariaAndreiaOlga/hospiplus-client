package org.hospi.hospiplusclient.models;

import javafx.beans.property.*;

public class OrderDetails {

    private StringProperty code = new SimpleStringProperty();
    private StringProperty mu = new SimpleStringProperty();
    private IntegerProperty quantity = new SimpleIntegerProperty();
    private FloatProperty price = new SimpleFloatProperty();
    private FloatProperty total = new SimpleFloatProperty();

    public OrderDetails() {}

    public String getCode() {
        return code.get();
    }

    public StringProperty codeProperty() {
        return code;
    }

    public void setCode(String code) {
        this.code.set(code);
    }

    public String getMu() {
        return mu.get();
    }

    public StringProperty muProperty() {
        return mu;
    }

    public void setMu(String mu) {
        this.mu.set(mu);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public float getPrice() {
        return price.get();
    }

    public FloatProperty priceProperty() {
        return price;
    }

    public void setPrice(float price) {
        this.price.set(price);
    }

    public float getTotal() {
        return total.get();
    }

    public FloatProperty totalProperty() {
        return total;
    }

    public void setTotal(float total) {
        this.total.set(total);
    }
}
