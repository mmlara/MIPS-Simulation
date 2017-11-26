package simulationviewer.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;

public class DisplayResultGUI {
    private Stage primaryStage;
    static Parent root;

    ComboBox<String> select_hililloP0;
    ComboBox<String> select_hililloP1;

    public DisplayResultGUI(String rootPath, Stage primaryStage) throws IOException {
        root = FXMLLoader.load(getClass().getResource(rootPath));
        this.primaryStage = primaryStage;
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
