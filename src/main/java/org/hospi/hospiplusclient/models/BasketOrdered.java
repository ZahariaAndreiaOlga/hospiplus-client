package org.hospi.hospiplusclient.models;

public class BasketOrdered {

    private Integer productId;
    private Integer quantity;

    public BasketOrdered() {}

    public BasketOrdered(Integer productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
