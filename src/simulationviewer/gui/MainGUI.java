package simulationviewer.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainGUI {
    private Stage primaryStage;
    static Parent root;

    public MainGUI(String rootPath, Stage primaryStage) throws IOException {
        root = FXMLLoader.load(getClass().getResource(rootPath));
        this.primaryStage = primaryStage;
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
