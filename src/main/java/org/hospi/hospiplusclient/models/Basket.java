package org.hospi.hospiplusclient.models;

import javafx.beans.property.*;

public class Basket {

    private IntegerProperty productId = new SimpleIntegerProperty();
    private StringProperty code = new SimpleStringProperty();
    private StringProperty mu = new SimpleStringProperty();
    private IntegerProperty quantity = new SimpleIntegerProperty();
    private FloatProperty pricePerUnit = new SimpleFloatProperty();

    public Basket() {}

    public Basket(int productId, String code, String mu, int quantity, float pricePerUnit) {
        this.setProductId(productId);
        this.setCode(code);
        this.setMu(mu);
        this.setQuantity(quantity);
        this.setPricePerUnit(pricePerUnit);
    }

    public int getProductId() {
        return productId.get();
    }

    public IntegerProperty productIdProperty() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId.set(productId);
    }

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

    public double getPricePerUnit() {
        return pricePerUnit.get();
    }

    public FloatProperty pricePerUnitProperty() {
        return pricePerUnit;
    }

    public void setPricePerUnit(float pricePerUnit) {
        this.pricePerUnit.set(pricePerUnit);
    }
}
