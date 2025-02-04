package org.hospi.hospiplusclient.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hospi.hospiplusclient.HospiApplication;

import java.io.IOException;

public class SceneController {

    private Stage stage;
    private Scene scene;
    private Parent parent;

    public SceneController(Stage stage){
        this.stage = stage;
    }

    public <T> T switchToScene(String fxmlFilePath){
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(HospiApplication.class.getResource(fxmlFilePath));
            parent = loader.load();

            // Create a new scene from the loaded FXML
            scene = new Scene(parent);

            // Set the new scene on the stage
            stage.setScene(scene);

            // Show the stage
            stage.show();

            return loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Unable to load the FXML file.");
            return null;
        }
    }

}
