import blockinglogicalcomponents.Locks;
import javafx.application.Application;
import javafx.stage.Stage;
import physicalcomponentssimulation.directory.Directory;
import physicalcomponentssimulation.memory.Memory;
import physicalcomponentssimulation.processor.Processor;
import physicalcomponentssimulation.systemthread.SystemThread;
import physicalcomponentssimulation.time.Clock;
import simulationviewer.gui.MainGUI;

public class Main extends Application {

    MainGUI viewer;

    @Override
    public void start(Stage primaryStage) throws Exception{
        viewer = new MainGUI("/simulationviewer/guifiles/initialgui.fxml",primaryStage);
        Directory directory = new Directory(16, 1);
        Memory memory = new Memory(24);
        int numCores=2;
        Processor processor = new Processor(0,numCores,1,5, "DatosHilillos/P0", memory);
        processor.setDirectory(directory);

        Clock clock = new Clock();
        processor.setClock(clock);
        Locks locks= new Locks(numCores,3,2,2);
        processor.setLocks(locks);
        for (int i = 0; i <numCores ; i++) {
            processor.getCores()[i].setMyProcessor(processor);
            processor.getCores()[i].setCoreID(i);
            new Thread(processor.getCores()[i]).start();
            //System.out.println("TERMINO EL CORE "+processor.getCores()[i].getCoreID());
        }
        //processor.getCores()[1].setMyProcessor(processor);
        //processor.getCores()[2].setMyProcessor(processor);

        //new Thread(processor.getCores()[1]).start();
        //new Thread(processor.getCores()[2]).start();


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
        //
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
