/**
 *  Representation Instruction Memory of Processor
 *
 * @author Gómez Brayan, Lara Milton, Quirós Esteban
 * @version 1.0
 * @since 25/11/2017
 */

package physicalcomponentssimulation.memory;


import physicalcomponentssimulation.cache.BlockInstruction;
import physicalcomponentssimulation.processorsparts.Instruction;
import physicalcomponentssimulation.systemthread.SystemThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


public class InstructionMemory {
    /**
     * Size of the instruction memory
     */
    private int instructionMemorySize;

    /**
     * Logical initial address in the simulation
     */
    private int initialMemmory;

    /**
     * Representation of the instruction memory in the simulation
     */
    private BlockInstruction[] instructionMemory;

    /**
     * Get the initial address of the memory
     * @return int value that represent the logical initial position in the global memory instruction
     */
    public int getInitialMemmory() {
        return initialMemmory;
    }


    /**
     * Constructor
     * @param instructionMemorySize Size of the memory
     * @param initialMemmory Logical initial position in the global memory instruction
     */
    public InstructionMemory(int instructionMemorySize, int initialMemmory) {
        this.initialMemmory=initialMemmory;
        this.instructionMemorySize = instructionMemorySize;
        instructionMemory= new BlockInstruction[instructionMemorySize];
        for (int i = 0; i <instructionMemorySize ; i++) {
            BlockInstruction blockInstruction = new BlockInstruction();
            instructionMemory[i]=blockInstruction;
        }
    }


    /**
     * Get the block instruction in the specific index
     * @param blockInstructionIndex Index to get from the instruction memory
     * @return  A BlockInstruction
     */
    public BlockInstruction getBlockInstruction(int blockInstructionIndex ){
        return instructionMemory[blockInstructionIndex];
    }

    /**
     * Load all the instructions in the logical memory structure
     * @param systemThreads Queue with all instructions from the files send by parameter to the processor
     */
    public void loadInstructionsInMemory(Queue<SystemThread> systemThreads){

        List<Instruction> totalInstructions = new ArrayList<>();
        for (SystemThread systemThread : systemThreads) {
            systemThread.setInitIndexInMemory(this.initialMemmory+(totalInstructions.size()*4));
            systemThread.setPc(this.initialMemmory+(totalInstructions.size()*4));
            List<Instruction> instructions= systemThread.getMyInstructions();
            totalInstructions.addAll(instructions);
            systemThread.setLastIndexInMemory(totalInstructions.size());//este demarca donde empieza el siguiente
        }
        int index =0;
        for (int i = 0; i < totalInstructions.size()-totalInstructions.size()%4; i=i+4) {//suponiendo que siempre son modulo de 4 el número de instrucciones
            BlockInstruction blockInstruction= new BlockInstruction();
            blockInstruction.setInstruction(0,totalInstructions.get(i));
            blockInstruction.setInstruction(1,totalInstructions.get(i+1));
            blockInstruction.setInstruction(2,totalInstructions.get(i+2));
            blockInstruction.setInstruction(3,totalInstructions.get(i+3));
            this.instructionMemory[index]=blockInstruction ;
            index++;
        }

        int residuo=totalInstructions.size()%4;
        int lastIndexload=totalInstructions.size()-residuo;
        if(residuo!=0){
            BlockInstruction blockInstruction = new BlockInstruction();
            for (int i = 0; i <residuo ; i++) {
                blockInstruction.setInstruction(i,totalInstructions.get(lastIndexload+i));
            }
            this.instructionMemory[index]=blockInstruction ;
        }
    }
}