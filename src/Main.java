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
        int numCoresP0=2;
        int numCachesP0=2;
        int numCoresP1=1;
        int numCachesP1=1;
        Directory directory0 = new Directory(16, numCachesP0);
        Memory memory0 = new Memory(24);

        Directory directory1 = new Directory(16, numCachesP1);
        Memory memory1 = new Memory(24);

        boolean slowMode=false;

        Processor processorP0 = new Processor(0,numCoresP0,numCachesP0,5, slowMode,"DatosHilillos/P0", memory0);
        Processor processorP1 = new Processor(1,numCoresP1,numCachesP1,5, slowMode,"DatosHilillos/P1", memory1);

        processorP0.setDirectory(directory0);
        processorP1.setDirectory(directory1);

        Clock clock = new Clock();
        processorP0.setClock(clock);
        processorP1.setClock(clock);

        Locks locks= new Locks(numCoresP0+numCoresP1,numCachesP0+numCachesP1,2,2);
        processorP0.setLocks(locks);
        processorP1.setLocks(locks);

        processorP0.setNeigborProcessor(processorP1);
        processorP1.setNeigborProcessor(processorP0);

        for (int i = 0; i <numCoresP0 ; i++) {
            processorP0.getCores()[i].setMyProcessor(processorP0);
            processorP0.getCores()[i].setCoreID(i);
            new Thread(processorP0.getCores()[i]).start();
        }

        for (int i = 0; i <numCoresP1 ; i++) {

            processorP1.getCores()[i].setMyProcessor(processorP1);
            processorP1.getCores()[i].setCoreID(i);
            new Thread(processorP1.getCores()[i]).start();
        }
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
