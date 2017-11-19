package simulationviewer.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import simulationcontroller.SimulationController;

import java.io.IOException;

public class InitialGUIController {
    private Stage infoSimulation;

    public Button initExecution;
    public Button seeResults;
    public TextField pathP0;
    public TextField pathP1;
    public TextField quantumSize;
    public CheckBox modeExecution;

    SimulationController simulationController;

    String valuePathP0;
    String valuePathP1;
    int valueQuantumSize;
    boolean valueModeExecuttion;

    public void initSimulation(){
         valuePathP0= pathP0.getText();
         valuePathP1 = pathP1.getText();
         valueQuantumSize= Integer.parseInt(quantumSize.getText());
         valueModeExecuttion= modeExecution.isSelected();
        this.simulationController= new SimulationController();
        simulationController.run(2,2,1,1,valuePathP0,valuePathP1,valueModeExecuttion,valueQuantumSize);
        initExecution.setVisible(false);
        seeResults.setVisible(true);


    }

    public void displayResults(javafx.event.ActionEvent event) throws IOException {
        Parent blah = FXMLLoader.load(getClass().getResource("displayinformation.fxml"));
        Scene scene = new Scene(blah);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();

    }

    public void loadInfoSimultation(){


    }

}
