package simulationcontroller;

import blockinglogicalcomponents.Locks;
import physicalcomponentssimulation.directory.Directory;
import physicalcomponentssimulation.memory.InstructionMemory;
import physicalcomponentssimulation.memory.Memory;
import physicalcomponentssimulation.processor.Processor;
import physicalcomponentssimulation.processorsparts.Instruction;
import physicalcomponentssimulation.time.Clock;

import java.util.LinkedList;
import java.util.List;

public class SimulationController {

    /**
     * Static sizes of memory
     */
    private final int INSTRUCTION_MEMORY_SIZE_P0 = 24;
    private final int INSTRUCTION_MEMORY_SIZE_P1 = 16;
    private final int INITIAL_MEMORY_P0=256;
    private final int INITIAL_MEMORY_P1=128;

    /**
     * Definition of both processors in the simulation
     */
    Processor processorP0;
    Processor processorP1;

    List<Thread> threadArray = new LinkedList<>();


    public SimulationController() {}

    /**
     * Method in charge of executing the simulation. It starts the execution of the threads simulating the cores.
     * Also creates every object necessary for the simulation(Processors, Directories, Memories).
     * @param numCoresP0 number of cores in P0
     * @param numCachesP0 number of caches in P0
     * @param numCoresP1 number of cores in P1
     * @param numCachesP1 number of caches in P1
     * @param pathP0 path to directory containing P0 "hilillos"
     * @param pathP1 path to directory containing P1 "hilillos"
     * @param slowMode flag to allow slow mode
     * @param quantumSize quantum size for processors
     */
    public void run(int numCoresP0,
                    int numCachesP0,
                    int numCoresP1,
                    int numCachesP1,
                    String pathP0,
                    String pathP1,
                    boolean slowMode,
                    int quantumSize) {

        Directory directory0 = new Directory(16, numCachesP0+numCachesP1);
        Memory memory0 = new Memory(16);

        Directory directory1 = new Directory(8, numCachesP0+numCachesP1);
        Memory memory1 = new Memory(8);



        Processor processorP0 = new Processor(0, numCoresP0, numCachesP0, quantumSize, slowMode, pathP0, memory0, initializeInstructionMemory(0));
        Processor processorP1 = new Processor(1, numCoresP1, numCachesP1, quantumSize, slowMode, pathP1, memory1, initializeInstructionMemory(1));


        processorP0.setDirectory(directory0);
        processorP1.setDirectory(directory1);

        Clock clock = new Clock();
        processorP0.setClock(clock);
        processorP1.setClock(clock);

        Locks locks = new Locks(numCoresP0 + numCoresP1, numCachesP0 + numCachesP1, 2, 2);
        processorP0.setLocks(locks);
        processorP1.setLocks(locks);

        processorP0.setNeigborProcessor(processorP1);
        processorP1.setNeigborProcessor(processorP0);

        for (int i = 0; i < numCoresP0; i++) {
            processorP0.getCores()[i].setMyProcessor(processorP0);
            processorP0.getCores()[i].setCoreID(i);
            Thread t = new Thread(processorP0.getCores()[i],"Thread "+ i+ " P0");
            t.start();
            threadArray.add(t);
        }

        for (int i = 0; i < numCoresP1; i++) {

            processorP1.getCores()[i].setMyProcessor(processorP1);
            processorP1.getCores()[i].setCoreID(i);
            Thread t = new Thread(processorP1.getCores()[i],"Thread "+ i+ " P1");
            t.start();
            threadArray.add(t);
        }

        for(Thread t : threadArray) {
            try {
                t.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        processorP0.getMemory().printMemory();
        processorP1.getMemory().printMemory();
    }

    /**
     * This method initialize the instruction physicalcomponentssimulation.memory, setting a custom size that depends on the
     * local physicalcomponentssimulation.processor id
     */
    private InstructionMemory initializeInstructionMemory(int processorId) {
        InstructionMemory instructionMemory = null;
        if (processorId == 0) {
            instructionMemory = new InstructionMemory(INSTRUCTION_MEMORY_SIZE_P0,INITIAL_MEMORY_P0);
        } else if (processorId == 1) {
            instructionMemory = new InstructionMemory(INSTRUCTION_MEMORY_SIZE_P1,INITIAL_MEMORY_P1);
        } else {
            System.out.println("Invalid physicalcomponentssimulation.processor id");
        }
        return instructionMemory;
    }
}
