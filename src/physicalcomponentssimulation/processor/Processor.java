package physicalcomponentssimulation.processor;

import physicalcomponentssimulation.cache.DataCache;
import physicalcomponentssimulation.cache.InstructionCache;
import physicalcomponentssimulation.core.Core;
import physicalcomponentssimulation.directory.Directory;
import physicalcomponentssimulation.memory.InstructionMemory;
import physicalcomponentssimulation.memory.Memory;
import physicalcomponentssimulation.systemthread.SystemThread;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Queue;

public class Processor {
    
    private final int INSTRUCTION_MEMORY_SIZE_P0 = 16;
    private final int INSTRUCTION_MEMORY_SIZE_P1 = 8;


    // Local variables which represents physical parts inside a our physicalcomponentssimulation.processor

    private int processorId;
    private int quantumSize;//cambiar este valor y chequear si se escribe así
    private int numCores;
    private int numOfCaches;
    private int numOfThreads;
    private Core[] cores;
    private Memory memory;
    private InstructionMemory instructionMemory;
    private String threadDirectoryPath;
    private Queue<SystemThread> assignedSystemThreads;//para que sea más eficiente seleccionar al siguiente hilillo en el procesador.

    Directory directory;

    /**
     * @param processorId         Process identifier
     * @param numCores            Number of cores in the physicalcomponentssimulation.processor
     * @param numOfCaches         Number of caches in the physicalcomponentssimulation.processor. It is proportional to the number of cores in the physicalcomponentssimulation.processor
     * @param quantumSize         Quantum size which defines the cpu time before the system change the thread assigned to the physicalcomponentssimulation.core
     * @param threadDirectoryPath Path to execution information about the thread to load
     */
    public Processor(int processorId, int numCores, int numOfCaches, int quantumSize, String threadDirectoryPath, Memory memory) {
        this.processorId = processorId;
        this.numCores = numCores;
        this.cores = new Core[numCores];
        this.numOfCaches = numOfCaches;
        this.quantumSize = quantumSize;
        this.threadDirectoryPath = threadDirectoryPath;
        this.assignedSystemThreads = new ArrayDeque<>();
        this.memory = memory;

        this.initializeCores();
        this.initializeInstructionMemory();
        this.loadThreads();                                                             // Load the information of the files located in the physicalcomponentssimulation.directory path sent
        this.instructionMemory.loadInstructionsInMemory(this.assignedSystemThreads);


        for (int i = 0; i < numCores; i++) {//setea los "punteros de las caches de los cores a las respectivas memorias"
            this.cores[i].getDataCache().setShareMemoryAccess(memory);
            this.cores[i].getInstructionCache().setInstructionMemory(this.instructionMemory);
        }
    }

    /**
     * This method initialize the instruction physicalcomponentssimulation.memory, setting a custom size that depends on the
     * local physicalcomponentssimulation.processor id
     */
    private void initializeInstructionMemory() {
        if (this.processorId == 0) {
            this.instructionMemory = new InstructionMemory(INSTRUCTION_MEMORY_SIZE_P0);
        } else if (this.processorId == 1) {
            this.instructionMemory = new InstructionMemory(INSTRUCTION_MEMORY_SIZE_P1);
        } else {
            System.out.println("Invalid physicalcomponentssimulation.processor id");
        }
    }

    /**
     *  This method initialize the cores of the physicalcomponentssimulation.processor
     */
    private void initializeCores() {

        for (int i = 0; i < numCores; i++) {
            Core core = new Core(assignedSystemThreads);
            DataCache dataCache = new DataCache();
            core.setDataCache(dataCache);
            InstructionCache instructionCache = new InstructionCache();
            core.setInstructionCache(instructionCache);
            this.cores[i] = core;
        }

    }

    /**
     * Open the physicalcomponentssimulation.directory sent as parameters and load the files located there inside an array of threads.
     */
    public void loadThreads() {
        File folder = new File(this.threadDirectoryPath);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String threadFileFile = this.threadDirectoryPath + "/" + file.getName();
                SystemThread systemThread = new SystemThread(threadFileFile);
                this.assignedSystemThreads.add(systemThread);
                System.out.println("Thread successfully loaded. Thread name: "+file.getName());
            }
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

    public Queue<SystemThread> getAssignedSystemThreads() {
        return assignedSystemThreads;
    }

    public void setAssignedSystemThreads(Queue<SystemThread> assignedSystemThreads) {
        this.assignedSystemThreads = assignedSystemThreads;
    }
}
