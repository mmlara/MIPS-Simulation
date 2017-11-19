import blockinglogicalcomponents.Locks;
import javafx.application.Application;
import javafx.stage.Stage;
import physicalcomponentssimulation.directory.Directory;
import physicalcomponentssimulation.memory.Memory;
import physicalcomponentssimulation.processor.Processor;
import physicalcomponentssimulation.systemthread.SystemThread;
import physicalcomponentssimulation.time.Clock;
import simulationcontroller.SimulationController;
import simulationviewer.gui.MainGUI;

public class Main extends Application {

    MainGUI viewer;

    @Override
    public void start(Stage primaryStage) throws Exception{
        viewer = new MainGUI("/simulationviewer/guifiles/initialgui.fxml",primaryStage);
        SimulationController simullation = new SimulationController();
        //simullation.run(2,2,1,1);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
