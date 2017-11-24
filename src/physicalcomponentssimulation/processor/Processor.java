package physicalcomponentssimulation.processor;

import blockinglogicalcomponents.Locks;
import org.omg.CORBA.PUBLIC_MEMBER;
import physicalcomponentssimulation.cache.DataCache;
import physicalcomponentssimulation.cache.InstructionCache;
import physicalcomponentssimulation.core.Core;
import physicalcomponentssimulation.directory.Directory;
import physicalcomponentssimulation.memory.InstructionMemory;
import physicalcomponentssimulation.memory.Memory;
import physicalcomponentssimulation.systemthread.SystemThread;
import physicalcomponentssimulation.time.Clock;

import java.awt.*;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Processor {
    
    private final int INSTRUCTION_MEMORY_SIZE_P0 = 24;
    private final int INSTRUCTION_MEMORY_SIZE_P1 = 16;


    // Local variables which represents physical parts inside a our physicalcomponentssimulation.processor
    private Processor neigborProcessor;
    private boolean inSlowExecution;
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
    private Clock clock;
    private Locks locks;// Contains all lock objects


    Directory directory;

    /**
     * @param processorId         Process identifier
     * @param numCores            Number of cores in the physicalcomponentssimulation.processor
     * @param numOfCaches         Number of caches in the physicalcomponentssimulation.processor. It is proportional to the number of cores in the physicalcomponentssimulation.processor
     * @param quantumSize         Quantum size which defines the cpu time before the system change the thread assigned to the physicalcomponentssimulation.core
     * @param threadDirectoryPath Path to execution information about the thread to load
     */
    public Processor(int processorId, int numCores, int numOfCaches, int quantumSize,boolean inSlowExecution, String threadDirectoryPath, Memory memory, InstructionMemory instructionMemory) {
        this.processorId = processorId;
        this.numCores = numCores;
        this.cores = new Core[numCores];
        this.numOfCaches = numOfCaches;
        this.quantumSize = quantumSize;
        this.inSlowExecution=inSlowExecution;
        this.threadDirectoryPath = threadDirectoryPath;
        this.assignedSystemThreads = new ArrayDeque<>();

        this.memory = memory;
        this.instructionMemory = instructionMemory;
        this.initializeCores();
        this.loadThreads();                                                             // Load the information of the files located in the physicalcomponentssimulation.directory path sent
        this.instructionMemory.loadInstructionsInMemory(this.assignedSystemThreads);


        for (int i = 0; i < numCores; i++) {//setea los "punteros de las caches de los cores a las respectivas memorias"
            this.cores[i].getInstructionCache().setInstructionMemory(this.instructionMemory);
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
        int i=0;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String threadFileFile = this.threadDirectoryPath + "/" + file.getName();
                SystemThread systemThread = new SystemThread(threadFileFile);
                systemThread.setMyName(file.getName());
                systemThread.setIdHilillo(i);
                i++;
                this.assignedSystemThreads.add(systemThread);

                System.out.println("Thread successfully loaded. Thread name: "+file.getName());
            }
        }
    }

    public InstructionMemory getInstructionMemory() {
        return instructionMemory;
    }

    public Processor getNeigborProcessor() {
        return neigborProcessor;
    }

    public void setNeigborProcessor(Processor neigborProcessor) {
        this.neigborProcessor = neigborProcessor;
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

    public Directory getDirectory() {return this.directory;}

    public Queue<SystemThread> getAssignedSystemThreads() {
        return assignedSystemThreads;
    }



    public void setAssignedSystemThreads(Queue<SystemThread> assignedSystemThreads) {
        this.assignedSystemThreads = assignedSystemThreads;
    }

    public Clock getClock() {
        return clock;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    public void setLocks(Locks locks) {
        this.locks = locks;
    }

    public Locks getLocks() {
        return locks;
    }

    public Memory getMemory(){return memory;}

    public int getProcessorId() {
        return processorId;
    }

    public void setProcessorId(int processorId) {
        this.processorId = processorId;
    }

    public boolean isInSlowExecution() {
        return inSlowExecution;
    }

    public void setInSlowExecution(boolean inSlowExecution) {
        this.inSlowExecution = inSlowExecution;
    }
}
