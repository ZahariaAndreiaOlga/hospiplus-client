package org.hospi.hospiplusclient.models;

import javafx.beans.property.*;

public class Role {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty roleName = new SimpleStringProperty();

    public Role() {}

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getRoleName() {
        return roleName.get();
    }

    public void setRoleName(String roleName) {
        this.roleName.set(roleName);
    }

    public StringProperty roleNameProperty() {
        return roleName;
    }

    @Override
    public String toString() {
        return roleName.get();
    }
}
