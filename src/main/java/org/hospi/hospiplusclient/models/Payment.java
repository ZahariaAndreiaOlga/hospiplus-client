package org.hospi.hospiplusclient.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Payment {

    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty payType = new SimpleStringProperty();

    public Payment() {}

    public Integer getId() {
        return id.get();
    }

    public void setId(Integer id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getPayType() {
        return payType.get();
    }

    public void setPayType(String payType) {
        this.payType.set(payType);
    }

    public StringProperty payTypeProperty() {
        return payType;
    }
}


