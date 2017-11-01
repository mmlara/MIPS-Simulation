package physicalcomponentssimulation.memory;


import physicalcomponentssimulation.cache.BlockInstruction;
import physicalcomponentssimulation.processorsparts.Instruction;
import physicalcomponentssimulation.systemthread.SystemThread;

import java.util.List;
import java.util.Queue;

//mapea la memoria para instrucciones, cada procesador debe tener una instancia de esta
//y recibe el tamaño por parámetro porque cambia según el procesador
public class InstructionMemory {

    private int instructionMemorySize;
    private BlockInstruction[] instructionMemory;

    public BlockInstruction[] getInstructionMemory() {
        return instructionMemory;
    }

    public void setInstructionMemory(BlockInstruction[] instructionMemory) {
        this.instructionMemory = instructionMemory;
    }

    public InstructionMemory(int instructionMemorySize) {
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
        int index=0;//lleva el indíce de la instrucción que se va llenando en memoria;
        for (SystemThread systemThread : systemThreads) {
            systemThread.setInitIndexInMemory(index);
            List<Instruction> instructions= systemThread.getMyInstructions();
            for (int i = 0; i < instructions.size(); i=i+4) {//suponiendo que siempre son modulo de 4 el número de instrucciones
                BlockInstruction blockInstruction= new BlockInstruction();
                blockInstruction.setInstruction(0,instructions.get(i));
                blockInstruction.setInstruction(1,instructions.get(i+1));
                blockInstruction.setInstruction(2,instructions.get(i+2));
                blockInstruction.setInstruction(3,instructions.get(i+3));
                this.instructionMemory[index]=blockInstruction ;
                index++;
            }
            systemThread.setLastIndexInMemory(index);//este demarca donde empieza el siguiente
        }
    }
}
