import javafx.application.Application;
import javafx.stage.Stage;
import physicalcomponentssimulation.directory.Directory;
import physicalcomponentssimulation.memory.Memory;
import physicalcomponentssimulation.processor.Processor;
import simulationviewer.gui.MainGUI;

public class Main extends Application {

    MainGUI viewer;

    @Override
    public void start(Stage primaryStage) throws Exception{
        viewer = new MainGUI("/simulationviewer/guifiles/initialgui.fxml",primaryStage);
        Directory directory = new Directory(Memory.shareMemorySize, 1);
        Memory memory = new Memory();
        Processor processor = new Processor(1,1,1,200, "DatosHilillos/P0", memory);
        processor.setDirectory(directory);
        processor.loadThreads();
    }


    public static void main(String[] args) {
        launch(args);
                /*
        DataCache dataCache= new DataCache();
        Memory physicalcomponentssimulation.memory = new Memory();
        dataCache.setShareMemoryAccess(physicalcomponentssimulation.memory);
        for (int j = 0; j <4 ; j++) {
            Block block = new Block();

            for (int i = 0; i <4 ; i++) {
                block.setWord(i,j+i+1);
            }
            dataCache.getShareMemoryAccess().setBlock(j,block);
            System.out.print( block.toString());
        }
        dataCache.loadBlock(0);
        System.out.print(dataCache.getWord(0,2));
        int x=2;
        System.out.print("hopa"+(x<<2));
*/
        //  SystemThread hilillo = new SystemThread("DatosHilillos/hililloPrueba.txt");
        /*
        File folder = new File("DatosHilillos/P0/");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println(file.getName());
            }
        }
*/

    }
}
