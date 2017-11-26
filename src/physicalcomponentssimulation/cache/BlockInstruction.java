/**
 *  Representation memory block instruction
 *
 * @author Gómez Brayan, Lara Milton, Quirós Esteban
 * @version 1.0
 * @since 25/11/2017
 */

package physicalcomponentssimulation.cache;

import physicalcomponentssimulation.processorsparts.Instruction;

public class BlockInstruction {

    /**
     * block size number
     */
    final int blockSize =4;

    /**
     * block Instruction Representation
     */
    private Instruction[] dataBlock;


    /**
     *  BlockInstruction Constructor
     */
    public BlockInstruction(){
        dataBlock= new Instruction[blockSize];
        for (int i = 0; i <blockSize ; i++) {
           Instruction instruction =new Instruction();
            dataBlock[i]=instruction;
        }


    }

    /**
     *  Get a Data Block
     * @return data block
     */
    public Instruction[] getDataBlock() {
        return dataBlock;
    }

    /**
     * Set a data block
     * @param dataBlock to set in the structure
     */
    public void setDataBlock(Instruction[] dataBlock) {
        this.dataBlock = dataBlock;
    }

    /**
     * Set instruction
     * @param numInstruction number of instruction in the block
     * @param instruction instruction to set in the block
     */
    public void setInstruction(int numInstruction, Instruction instruction){
        dataBlock[numInstruction]=instruction;
    }


}


