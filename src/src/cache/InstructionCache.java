package cache;

import memory.InstructionMemory;
import processorsparts.Instruction;

public class InstructionCache  {

    final int numBlocks=4;
    private BlockInstruction[] cacheInstruction;
    private int[] tags;
    InstructionMemory instructionMemory;

    public InstructionCache(){
        cacheInstruction = new BlockInstruction[numBlocks];
        tags= new int[numBlocks];
        for (int i = 0; i <numBlocks ; i++) {
            tags[i]=-1;
        }
    }

    public Instruction getWord(int index, int numWord) {
        BlockInstruction blockInstruction= cacheInstruction[index];
        Instruction instruction = blockInstruction.getDataBlock()[numWord];
        return instruction;
    }

    public BlockInstruction loadBlock(int blockIndex) {
        return instructionMemory.getInstructionMemory()[blockIndex];
    }

    public void storeBlock(int blockIndex, BlockInstruction blockInstruction) {
        instructionMemory.getInstructionMemory()[blockIndex]=blockInstruction;
    }
}
