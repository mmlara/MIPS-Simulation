package physicalcomponentssimulation.memory;


import physicalcomponentssimulation.cache.BlockInstruction;
import physicalcomponentssimulation.processorsparts.Instruction;
import physicalcomponentssimulation.systemthread.SystemThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

//mapea la memoria para instrucciones, cada procesador debe tener una instancia de esta
//y recibe el tamaño por parámetro porque cambia según el procesador
public class InstructionMemory {

    private int instructionMemorySize;
    private int initialMemmory;
    private BlockInstruction[] instructionMemory;

    public BlockInstruction[] getInstructionMemory() {
        return instructionMemory;
    }

    public void setInstructionMemory(BlockInstruction[] instructionMemory) {
        this.instructionMemory = instructionMemory;
    }

    public int getInitialMemmory() {
        return initialMemmory;
    }

    public int getInstructionMemorySize() {
        return instructionMemorySize;
    }

    public InstructionMemory(int instructionMemorySize, int initialMemmory) {
        this.initialMemmory=initialMemmory;
        this.instructionMemorySize = instructionMemorySize;
        instructionMemory= new BlockInstruction[instructionMemorySize];
        for (int i = 0; i <instructionMemorySize ; i++) {
            BlockInstruction blockInstruction = new BlockInstruction();
            instructionMemory[i]=blockInstruction;
        }
    }


    public BlockInstruction getBlockInstruction(int blockInstructionIndex ){
        return instructionMemory[blockInstructionIndex];
    }

    //este método recibe la ubicación real en memoria y no el tag del bloque.
    public void setBlockInstruction(int blockInstructionIndex, BlockInstruction blockInstruction){
        instructionMemory[blockInstructionIndex]=blockInstruction;
    }

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