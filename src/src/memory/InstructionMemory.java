package memory;


import cache.BlockInstruction;

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
}
