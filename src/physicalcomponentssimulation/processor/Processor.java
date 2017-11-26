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

    /**
     * Static sizes of memory
     */
    private final int INSTRUCTION_MEMORY_SIZE_P0 = 24;
    private final int INSTRUCTION_MEMORY_SIZE_P1 = 16;

    /**
     * Represents the other processor in the simulation
     */
    private Processor neigborProcessor;

    /**
     * Flag representing if simulation is in slow mode or not
     */
    private boolean inSlowExecution;//Flag to check if execution is in slow mode

    /**
     * Processor ID
     */
    private int processorId;

    /**
     * Size of the quantum for the processor
     */
    private int quantumSize;

    /**
     * Number of cores in this processor
     */
    private int numCores;

    /**
     * Number of caches in this processor
     */
    private int numOfCaches;

    /**
     * Array that contains cores of this processor
     */
    private Core[] cores;

    /**
     * This processors memory
     */
    private Memory memory;

    /**
     * This processors instruction memory
     */
    private InstructionMemory instructionMemory;

    /**
     * String with the Path for the directory
     */
    private String threadDirectoryPath;

    /**
     * Queue that controls "hilillos" to execute
     */
    private Queue<SystemThread> assignedSystemThreads;

    /**
     * Processors clock
     */
    private Clock clock;

    /**
     * Reference to object that contains all the locks necessary for execution control
     */
    private Locks locks;

    /**
     * Directory of this processor
     */
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
     *  This method initializes the cores of the physicalcomponentssimulation.processor
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

    /**
     * Get the neighbor processor(not the one where you are located)
     */
    public Processor getNeigborProcessor() {
        return neigborProcessor;
    }

    /**
     * Set the neighbor Processor
     * @param neigborProcessor Processor to set as neighbor processor
     */
    public void setNeigborProcessor(Processor neigborProcessor) {
        this.neigborProcessor = neigborProcessor;
    }

    /**
     * Get the quantum size
     * @return quantum size
     */
    public int getQuantumSize() {
        return this.quantumSize;
    }

    /**
     * Get the array that contains the processors cores
     * @return the array of cores in the processor
     */
    public Core[] getCores() {
        return this.cores;
    }

    /**
     * Set this processors directory
     * @param directory directory to use as processors directory
     */
    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    /**
     * Get the directory
     * @return this processors directory
     */
    public Directory getDirectory() {return this.directory;}

    /**
     * Get the queue of "hilillos"
     * @return queue of "hilillos"
     */
    public Queue<SystemThread> getAssignedSystemThreads() {
        return assignedSystemThreads;
    }


    public Clock getClock() {
        return clock;
    }

    /**
     * Set the clock of this processor
     * @param clock clock
     */
    public void setClock(Clock clock) {
        this.clock = clock;
    }

    /**
     * Set the processors lock object
     * @param locks new set of locks
     */
    public void setLocks(Locks locks) {
        this.locks = locks;
    }

    /**
     * Get the clock
     * @return processors clock
     */
    public Locks getLocks() {
        return locks;
    }

    /**
     * Get the data memory
     * @return data memory
     */
    public Memory getMemory(){return memory;}

    /**
     * Get the processors ID
     * @return processors ID
     */
    public int getProcessorId() {
        return processorId;
    }

    /**
     * Get the slow execution flag
     * @return slow execution flag
     */
    public boolean isInSlowExecution() {
        return inSlowExecution;
    }
}
