/**
 * Processor
 *
 * @author Gómez Brayan, Lara Milton, Quirós Esteban
 * @version 1.0
 * @since 25/11/2017
 */
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
     * Static size of the memory instruction in the processor 0
     */
    private final int INSTRUCTION_MEMORY_SIZE_P0 = 24;

    /**
     * Static size of the memory instruction in the processor 1
     */
    private final int INSTRUCTION_MEMORY_SIZE_P1 = 16;


    /**
     * Reference to the other processor in the simulation
     */
    private Processor neigborProcessor;

    /**
     * Mode to execute the simulation
     */
    private boolean inSlowExecution;

    /**
     * Identification of the processor
     */
    private int processorId;

    /**
     * Size of quantum from a system thread in the core
     */
    private int quantumSize;//cambiar este valor y chequear si se escribe así

    /**
     * Number of cores that  this processor has
     */
    private int numCores;

    /**
     * Number of caches that  this processor has
     */
    private int numOfCaches;

    /**
     * References to the cores that  this processor has
     */
    private Core[] cores;

    /**
     * Reference to the data memory
     */
    private Memory memory;

    /**
     * Reference to the Instruction Memory
     */
    private InstructionMemory instructionMemory;

    /**
     * Path of the directory where the files are located with the instructions that will be executed
     */
    private String threadDirectoryPath;

    /**
     * Queue with the system threads that will be executed in this processor
     */
    private Queue<SystemThread> assignedSystemThreads;//para que sea más eficiente seleccionar al siguiente hilillo en el procesador.

    /**
     * Reference of the clock to create a logical synchronization
     */
    private Clock clock;

    /**
     *
     */
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


    public Processor getNeigborProcessor() {
        return neigborProcessor;
    }

    public void setNeigborProcessor(Processor neigborProcessor) {
        this.neigborProcessor = neigborProcessor;
    }

    public int getQuantumSize() {
        return this.quantumSize;
    }

    public Core[] getCores() {
        return this.cores;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public Directory getDirectory() {return this.directory;}

    public Queue<SystemThread> getAssignedSystemThreads() {
        return assignedSystemThreads;
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


    public boolean isInSlowExecution() {
        return inSlowExecution;
    }

}
