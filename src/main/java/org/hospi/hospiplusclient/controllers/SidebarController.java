package org.hospi.hospiplusclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.hospi.hospiplusclient.HospiApplication;
import org.hospi.hospiplusclient.utils.TokenStorage;

import java.io.IOException;

public class SidebarController {

    @FXML
    private Button homeButton;
    @FXML
    private Button productDashboardButton;
    @FXML
    private Button categoryButton;
    @FXML
    private Button productButton;
    @FXML
    private Button productHistoryButton;
    @FXML
    private Button roleButton;
    @FXML
    private Button paymentButton;
    @FXML
    private Button userButton;
    @FXML
    private Button allOrdersButton;
    @FXML
    private Button orderButton;
    @FXML
    private Button logoutButton;

    @FXML
    private AnchorPane contentArea;

    public SidebarController() {}

    public void initialize() {
        setUserRole(TokenStorage.getRole());
        handleHome();
    }

    @FXML
    public void handleHome() {
        loadContent("views/welcome-view.fxml");
    }

    @FXML
    private void handleProductDashboard(){
        loadContent("views/product-dashboard-view.fxml");
    }

    @FXML
    private void handleCategory(){
        loadContent("views/category-view.fxml");
    }

    @FXML
    private void handleProduct() {
        loadContent("views/product-view.fxml");
    }

    @FXML
    private void handleProductHistory() {
        loadContent("views/producthistory-view.fxml");
    }

    @FXML
    private void handleRole() {
        loadContent("views/role-view.fxml");
    }

    @FXML
    private void handlePayment() {
        loadContent("views/payment-view.fxml");
    }

    @FXML
    private void handleUser() {
        loadContent("views/user-view.fxml");
    }

    @FXML
    private void handleAllOrders() {
        loadContent("views/allorders-view.fxml");
    }

    @FXML
    private void handleOrder() {
        loadContent("views/order-view.fxml");
    }

    @FXML
    private void handleLogout() {
        try{
            TokenStorage.clearJwtToken();
            TokenStorage.setRole("user");
            HospiApplication.loadLoginScreen();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // Load a new FXML into the content area
    private void loadContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(HospiApplication.class.getResource(fxmlFile));
            AnchorPane newContent = loader.load();
            contentArea.getChildren().setAll(newContent);  // Replace the current content
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserRole(String role) {
        // Set visibility of buttons or load different content based on the role
        if (role.equals("admin")) {
            // Show admin-specific buttons or content
        } else if(role.equals("eco-dep")){
            // Show economic department-specific buttons or content
            if (roleButton != null) {
                roleButton.setVisible(false);
                roleButton.setManaged(false);
            }
            if (paymentButton != null) {
                paymentButton.setVisible(false);
                paymentButton.setManaged(false);
            }
            if (userButton != null) {
                userButton.setVisible(false);
                userButton.setManaged(false);
            }

        }else if (role.equals("user")) {
            // Show user-specific buttons or content
            if (categoryButton != null) {
                categoryButton.setVisible(false);
                categoryButton.setManaged(false);
            }
            if (productButton != null) {
                productButton.setVisible(false);
                productButton.setManaged(false);
            }
            if (productHistoryButton != null) {
                productHistoryButton.setVisible(false);
                productHistoryButton.setManaged(false);
            }
            if (roleButton != null) {
                roleButton.setVisible(false);
                roleButton.setManaged(false);
            }
            if (paymentButton != null) {
                paymentButton.setVisible(false);
                paymentButton.setManaged(false);
            }
            if (userButton != null) {
                userButton.setVisible(false);
                userButton.setManaged(false);
            }
            if (allOrdersButton != null) {
                allOrdersButton.setVisible(false);
                allOrdersButton.setManaged(false);
            }
            if (orderButton != null) {
                orderButton.setVisible(false);
                orderButton.setManaged(false);
            }
        }
    }
}
