import directory.Directory;
import hilillo.Hilillo;
import memory.Memory;
import processor.Processor;

import java.io.File;
import java.io.IOException;


public class Main {

    public static void main(String[]args) throws IOException {
        /*
        DataCache dataCache= new DataCache();
        Memory memory = new Memory();
        dataCache.setShareMemoryAccess(memory);
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
      //  Hilillo hilillo = new Hilillo("DatosHilillos/hililloPrueba.txt");
        /*
        File folder = new File("DatosHilillos/P0/");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println(file.getName());
            }
        }
*/
        Directory directory = new Directory(Memory.shareMemorySize, 1);
        Memory memory = new Memory();
        Processor processor = new Processor(1,1,1,200, "DatosHilillos/P0", memory);
        processor.setDirectory(directory);
        processor.loadHilillos();
    }
}
