package org.hospi.hospiplusclient.models;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class Order {

    private IntegerProperty id = new SimpleIntegerProperty();
    private IntegerProperty orderNum = new SimpleIntegerProperty();
    private ObjectProperty<LocalDateTime> orderedAt = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> arrival = new SimpleObjectProperty<>();
    private IntegerProperty quantity = new SimpleIntegerProperty();
    private FloatProperty pricePerProduct = new SimpleFloatProperty();
    private FloatProperty totalPrice = new SimpleFloatProperty();
    private IntegerProperty userId = new SimpleIntegerProperty();
    private Set<OrderedProduct> products;

    public Order(){}

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public int getOrderNum() {
        return orderNum.get();
    }

    public IntegerProperty orderNumProperty() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum.set(orderNum);
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt.get();
    }

    public ObjectProperty<LocalDateTime> orderedAtProperty() {
        return orderedAt;
    }

    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt.set(orderedAt);
    }

    public LocalDate getArrival() {
        return arrival.get();
    }

    public ObjectProperty<LocalDate> arrivalProperty() {
        return arrival;
    }

    public void setArrival(LocalDate arrival) {
        this.arrival.set(arrival);
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

    public float getPricePerProduct() {
        return pricePerProduct.get();
    }

    public FloatProperty pricePerProductProperty() {
        return pricePerProduct;
    }

    public void setPricePerProduct(float pricePerProduct) {
        this.pricePerProduct.set(pricePerProduct);
    }

    public float getTotalPrice() {
        return totalPrice.get();
    }

    public FloatProperty totalPriceProperty() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice.set(totalPrice);
    }

    public int getUserId() {
        return userId.get();
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public Set<OrderedProduct> getProducts() {
        return products;
    }

    public void setProducts(Set<OrderedProduct> products) {
        this.products = products;
    }
}
