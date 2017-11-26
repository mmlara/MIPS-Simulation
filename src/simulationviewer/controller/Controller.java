package simulationviewer.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import simulationcontroller.SimulationController;
import simulationviewer.gui.DisplayResultGUI;
import simulationviewer.gui.MainGUI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Controller extends Application {

    private static SimulationController simulationController;
    private Stage primaryStage;

    @FXML
    protected Button initExecution;
    public Button seeResults;
    public Button openDir1;
    public Button openDir2;
    public TextField pathP0;
    public TextField pathP1;
    public TextField quantumSize;
    public CheckBox modeExecution;
    public ComboBox<String> comboBoxHilillo;


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.getIcons().add(new Image("/simulationviewer/guifiles/icon.png"));
        primaryStage.setTitle("MIPS-Simulation");
        this.primaryStage = primaryStage;

        new MainGUI("/simulationviewer/guifiles/initialgui.fxml", primaryStage);
    }


    @FXML
    private void openPathP0() {
        File path = selectPath();
        pathP0.setText(path.getAbsolutePath());
    }

    @FXML
    private void openPathP1() {
        File path = selectPath();
        pathP1.setText(path.getAbsolutePath());
    }

    @FXML
    private void initSimulation() {
        int valueQuantumSize;
        try {
            valueQuantumSize = Integer.parseInt(quantumSize.getText());
        } catch (Exception e) {
            e.printStackTrace();
            quantumSize.setText("Por favor, escriba un valor numérico");
            return;
        }

        this.simulationController = new SimulationController();
        simulationController.run(2, 2, 1, 1,
                pathP0.getText(),
                pathP1.getText(),
                modeExecution.isSelected(),
                valueQuantumSize);


        initExecution.setVisible(false);
        seeResults.setVisible(true);
    }

    @FXML
    private void displayResults(ActionEvent event) throws IOException {
        new DisplayResultGUI("/simulationviewer/guifiles/displayinformation.fxml", new Stage());
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.hide();
        updateComboBox(FXCollections.observableArrayList(getThreadNames()));
    }


    public File selectPath() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("JavaFX Projects");
        File defaultDirectory = new File("c:");
        chooser.setInitialDirectory(defaultDirectory);
        return chooser.showDialog(primaryStage);
    }

    List<String> getThreadNames() {
        List<String> list = new ArrayList<>();
        list.addAll(simulationController.getProcessorP0().getNameHilillos());
        list.addAll(simulationController.getProcessorP1().getNameHilillos());
        return list;
    }

    public void updateComboBox(ObservableList<String> threads) {
        comboBoxHilillo = new ComboBox<>();
        comboBoxHilillo.setItems(threads);
    }

}
