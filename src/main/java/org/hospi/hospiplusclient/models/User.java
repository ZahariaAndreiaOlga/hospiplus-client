package org.hospi.hospiplusclient.models;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class User {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty userName = new SimpleStringProperty();
    private final StringProperty surname = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty userPassword = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private final IntegerProperty roleId = new SimpleIntegerProperty();
    private final StringProperty roleName = new SimpleStringProperty(); // For display purposes

    // Constructors
    public User() {}

    // Getters and Setters with JavaFX Properties
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getUserName() {
        return userName.get();
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public StringProperty usernameProperty() {
        return userName;
    }

    public String getSurname() {
        return surname.get();
    }

    public void setSurname(String surname) {
        this.surname.set(surname);
    }

    public StringProperty surnameProperty() {
        return surname;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getUserPassword() {
        return userPassword.get();
    }

    public void setUserPassword(String userPassword) {
        this.userPassword.set(userPassword);
    }

    public StringProperty userPasswordProperty() {
        return userPassword;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt.get();
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt.set(updatedAt);
    }

    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public int getRoleId() {
        return roleId.get();
    }

    public void setRoleId(int roleId) {
        this.roleId.set(roleId);
    }

    public IntegerProperty roleIdProperty() {
        return roleId;
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

    // ToString for debugging or ComboBox display
    @Override
    public String toString() {
        return userName.get() + " (" + roleName.get() + ")";
    }
}

