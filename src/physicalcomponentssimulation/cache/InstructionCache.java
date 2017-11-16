package physicalcomponentssimulation.cache;

import javafx.util.Pair;
import physicalcomponentssimulation.memory.InstructionMemory;
import physicalcomponentssimulation.processorsparts.Instruction;

public class InstructionCache  {

    final int numBlocks=4;
    private BlockInstruction[] cacheInstruction;
    private int[] tags;
    InstructionMemory instructionMemory;


    public InstructionCache(){
        cacheInstruction = new BlockInstruction[numBlocks];
        tags= new int[numBlocks];
        for (int i = 0; i <numBlocks ; i++) {
            BlockInstruction blockInstruction = new BlockInstruction();
            cacheInstruction[i]=blockInstruction;
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

    /**
     *
     * @param index indica el índice del bloque del cual se desea saber su tag
     * @return el tag del índice indicado.
     */
    public int getTagOfBlock(int index){
        return tags[index];
    }

    public void setInstructionMemory(InstructionMemory instructionMemory) {
        this.instructionMemory = instructionMemory;
    }


    public Instruction getInstruction(int indexInMemory){
        //index conversion
        indexInMemory=(indexInMemory-this.instructionMemory.getInitialMemmory())/4;
        Instruction instruction=null;
        int cyclesWaiting=0;
        Pair pair=null;
        int blockTag = (indexInMemory)/4;
        int indexInCache =blockTag%4;
        if (blockTag == getTagOfBlock(indexInCache)){//hit case
              int instructionNumberInBlock=indexInMemory%4;
               instruction= getWord(indexInCache,instructionNumberInBlock);

        }else {//miss case;

             this.cacheInstruction[indexInCache]=this.instructionMemory.getBlockInstruction(blockTag);
             this.tags[indexInCache]=blockTag;
             int instructionNumberInBlock=indexInMemory%4;
              instruction=getWord(indexInCache,instructionNumberInBlock);
        }
        return instruction;
    }

    public BlockInstruction[] getCacheInstruction() {
        return cacheInstruction;
    }

    public int[] getTags() {
        return tags;
    }
}
