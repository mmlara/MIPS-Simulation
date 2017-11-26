package simulationviewer.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import javax.swing.text.html.ListView;
import java.io.IOException;
import java.util.Observable;

public class DisplayResultController {

    ComboBox<String> select_hililloP0;
    ComboBox<String> select_hililloP1;



    public void displayResults(javafx.event.ActionEvent event) throws IOException {
        Parent blah = FXMLLoader.load(getClass().getResource("/simulationviewer/guifiles/displayinformation.fxml"));
        Scene scene = new Scene(blah);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.hide();
        appStage.setScene(scene);
        appStage.show();

    }


}
