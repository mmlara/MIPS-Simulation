package simulationviewer.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.beans.value.ObservableValue;

import java.io.IOException;

public class MainGUI {
    static Parent root;
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
        StringProperty numOfCores = new SimpleStringProperty(this,"numberOfCores","5");
        numOfCores.setValue("56");
    }

    public void getInitialParameters(){

    }


}
