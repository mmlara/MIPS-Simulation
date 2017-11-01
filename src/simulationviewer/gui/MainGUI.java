package simulationviewer.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainGUI {
    private Parent root;
    private Stage primaryStage;

    public MainGUI(String rootPath,Stage primaryStage) throws IOException {
        this.root = FXMLLoader.load(getClass().getResource(rootPath));
        this.primaryStage = primaryStage;
        this.loadMainFrame();
    }

    public void loadMainFrame(){
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("MIPS-Simulation");
        primaryStage.show();
    }

    public void getInitialParameters(){

    }


}
