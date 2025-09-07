package org.hospi.hospiplusclient.models;
import javafx.beans.property.*;

import java.util.Set;

public class Ward {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty wardName = new SimpleStringProperty();
    private final IntegerProperty capacity = new SimpleIntegerProperty();
    private Set<ProductWard> productWards;

    public Ward() {}

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public String getWardName() { return wardName.get(); }
    public void setWardName(String value) { wardName.set(value); }
    public StringProperty wardNameProperty() { return wardName; }

    public int getCapacity() { return capacity.get(); }
    public void setCapacity(int value) { capacity.set(value); }
    public IntegerProperty capacityProperty() { return capacity; }

    public Set<ProductWard> getProductWards() {
        return productWards;
    }

    public void setProductWards(Set<ProductWard> productWards) {
        this.productWards = productWards;
    }

    @Override
    public String toString() {
        return wardName.get();
    }
}
