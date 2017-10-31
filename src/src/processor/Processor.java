package processor;

import cache.DataCache;
import cache.InstructionCache;
import core.Core;
import directory.Directory;
import hilillo.Hilillo;
import memory.InstructionMemory;
import memory.Memory;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Queue;

public class Processor {

    private final int INSTRUCTION_MEMORY_SIZE_P0=16;
    private final int INSTRUCTION_MEMORY_SIZE_P1=8;

    private int processorId;
    private int numCores;
    private Core[] cores;
    private  int quantumSize;//cambiar este valor y chequear si se escribe así
    Directory directory;
    private int numTotalCaches;
    private Memory memory;
    private InstructionMemory instructionMemory;
    private String pathDirectoryHilillos;
    private int numHilillosAsigned;
    private Queue<Hilillo> asignedHilillos;//para que sea más eficiente seleccionar al siguiente hilillo en el procesador.

    /**
     *
     * @param processorId identificador del procesador
     * @param numCores números de cores que tendrá el procesador
     * @param numTotalCaches números de cahés proporcional al número de  cores
     * @param quantumSize tamano de quantum que representa el tiempo de cpu antes de entregar el cpu
     * @param pathDirectoryHilillos
     */
    public Processor(int processorId, int numCores, int numTotalCaches, int quantumSize, String pathDirectoryHilillos ,Memory memory) {

        this.processorId=processorId;
        this.numCores = numCores;
        this.cores= new Core[numCores];
        for (int i = 0; i <numCores ; i++) {//instacia los cores
            Core core = new Core();
            DataCache dataCache = new DataCache();
            core.setDataCache(dataCache);
            InstructionCache instructionCache= new InstructionCache();
            core.setInstructionCache(instructionCache);
            this.cores[i]= core;
        }

        this.numTotalCaches=numTotalCaches;
        this.quantumSize=quantumSize;
        this.pathDirectoryHilillos=pathDirectoryHilillos;
        this.asignedHilillos= new ArrayDeque<>();
        if(this.processorId==0){//crea la memoria de instrucciones según el prccesador
            this.instructionMemory= new InstructionMemory(INSTRUCTION_MEMORY_SIZE_P0);
        }else if(this.processorId==1){
            this.instructionMemory= new InstructionMemory(INSTRUCTION_MEMORY_SIZE_P1);
        }else {
            System.out.println("id no válido");
        }

        this.loadHilillos();//carga la información de los archivos del path del directorio enviado por parámetro
        this.instructionMemory.loadInstructionsInMemory(this.asignedHilillos);

        this.memory=memory;

        for (int i = 0; i <numCores ; i++) {//setea los "punteros de las caches de los cores a las respectivas memorias"
            this.cores[i].getDataCache().setShareMemoryAccess(memory);
            this.cores[i].getInstructionCache().setInstructionMemory(this.instructionMemory);
        }
    }

    public int getQuantumSize() {
        return this.quantumSize;
    }

    public void setQuantumSize(int quantumSize) {
        this.quantumSize = quantumSize;
    }

    public int getNumCores() {
        return this.numCores;
    }

    public void setNumCores(int numCores) {
        this.numCores = numCores;
    }

    public Core[] getCores() {
        return this.cores;
    }

    public void setCores(Core[] cores) {
        this.cores = cores;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public void loadHilillos(){
        //Abrir el path enviado por parámetro y meter los hilos en el array
        File folder = new File(this.pathDirectoryHilillos);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String pathFileHillillo=this.pathDirectoryHilillos+"/"+file.getName();
                Hilillo hilillo= new Hilillo(pathFileHillillo);
                this.asignedHilillos.add(hilillo);
                System.out.println(file.getName());
            }
        }
    }
}
