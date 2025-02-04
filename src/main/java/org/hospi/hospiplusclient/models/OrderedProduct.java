package org.hospi.hospiplusclient.models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class OrderedProduct {

    private IntegerProperty id = new SimpleIntegerProperty();
    private IntegerProperty productId = new SimpleIntegerProperty();
    private IntegerProperty orderId = new SimpleIntegerProperty();
    private IntegerProperty quantity = new SimpleIntegerProperty();
    private DoubleProperty pricePerUnit = new SimpleDoubleProperty();
    private DoubleProperty totalPrice = new SimpleDoubleProperty();

    public OrderedProduct() {}

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
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

    public int getOrderId() {
        return orderId.get();
    }

    public IntegerProperty orderIdProperty() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId.set(orderId);
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

    public DoubleProperty pricePerUnitProperty() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit.set(pricePerUnit);
    }

    public double getTotalPrice() {
        return totalPrice.get();
    }

    public DoubleProperty totalPriceProperty() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice.set(totalPrice);
    }
}
