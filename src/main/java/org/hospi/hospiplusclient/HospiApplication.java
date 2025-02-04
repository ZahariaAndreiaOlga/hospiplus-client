package org.hospi.hospiplusclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.hospi.hospiplusclient.controllers.SidebarController;

import java.io.IOException;

public class HospiApplication extends Application {

    private static Stage primaryStage;
    private static Scene mainScene;


    @Override
    public  void start(Stage stage) throws Exception {
        primaryStage = stage;

        // load the login screen
        loadLoginScreen();
    }

    public static void loadLoginScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(HospiApplication.class.getResource("views/login-view.fxml"));
        Parent loginRoot = loader.load();

        // Set the scene with the login FXML
        Scene loginScene = new Scene(loginRoot, 600, 700);

        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(700);
        primaryStage.setMaxWidth(600);
        primaryStage.setMaxHeight(700);

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    public static void loadRegisterScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(HospiApplication.class.getResource("views/register-view.fxml"));
        Parent loginRoot = loader.load();

        // Set the scene with the register FXML
        Scene loginScene = new Scene(loginRoot, 600, 700);

        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(700);
        primaryStage.setMaxWidth(600);
        primaryStage.setMaxHeight(700);

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Register");
        primaryStage.show();
    }

    public static void loadMainInterface(String userRole) throws IOException {
        // Load the sidebar and main interface
        FXMLLoader sidebarLoader = new FXMLLoader(HospiApplication.class.getResource("views/sidebar-menu-view.fxml"));
        SidebarController sidebarController = new SidebarController();

        Parent root = sidebarLoader.load();
        Scene mainScene = new Scene(root, 1400, 700);

        primaryStage.setMinWidth(1400);
        primaryStage.setMinHeight(700);
        primaryStage.setMaxWidth(1400);
        primaryStage.setMaxHeight(700);

        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Main Interface");
        primaryStage.show();

    }

    public static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        if (primaryStage != null) {
            alert.initOwner(primaryStage);
        }
        alert.showAndWait();
    }

    public static Stage getPrimaryStage(){
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}