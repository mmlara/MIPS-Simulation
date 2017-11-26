/**
 *  Cache Instruction
 *
 * @author Gómez Brayan, Lara Milton, Quirós Esteban
 * @version 1.0
 * @since 25/11/2017
 */

package physicalcomponentssimulation.cache;

import javafx.util.Pair;
import physicalcomponentssimulation.memory.InstructionMemory;
import physicalcomponentssimulation.processorsparts.Instruction;

public class InstructionCache  {

    /**
     * Block's size of cache
     */
    final int numBlocks=4;

    /**
     * Array of Blocks to represent the cache
     */
    private BlockInstruction[] cacheInstruction;

    /**
     * Array to save the current tag in some index of the cache
     */

    private int[] tags;

    /**
     * Reference to the memory that contains all instructions
     */
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

    /**
     * Get Word (instruction)
     * @param index position in the cache
     * @param numWord number of instruction in the index send by param
     * @return instruction from cache
     */
    public Instruction getWord(int index, int numWord) {
        BlockInstruction blockInstruction= cacheInstruction[index];
        Instruction instruction = blockInstruction.getDataBlock()[numWord];
        return instruction;
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

    /**
     * Get one instruction from the cache
     * @param indexInMemory logical memory address of the instruction
     * @return instruction from memory or cache(miss and hit case)
     */
    public Instruction getInstruction(int indexInMemory){
        //index conversion
        indexInMemory=(indexInMemory-this.instructionMemory.getInitialMemmory())/4;
        Instruction instruction;
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

    /**
     * get an Array cache Instruction
     * @return An Array of  instruction Blocks
     */
    public BlockInstruction[] getCacheInstruction() {
        return cacheInstruction;
    }

    /**
     * Get array of tag
     * @return an Array of tags
     */
    public int[] getTags() {
        return tags;
    }
}
