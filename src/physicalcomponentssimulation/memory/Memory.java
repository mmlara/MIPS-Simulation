/**
 *  Representation Data Memory of Processor
 *
 * @author Gómez Brayan, Lara Milton, Quirós Esteban
 * @version 1.0
 * @since 25/11/2017
 */

package physicalcomponentssimulation.memory;
import physicalcomponentssimulation.cache.Block;



public class Memory {


    /**
     * Size of the share memory
     */
    public int shareMemorySize;

    /**
     * Representation of the distributed share memory in the simulation
     */
    private Block[] shareMemory;

    /**
     * Constructor
     * @param memorySize Size of the distributed share memory
     */
    public Memory(int memorySize) {
        this.shareMemorySize=memorySize;
        this.shareMemory = new Block[shareMemorySize];
        for (int i = 0; i <shareMemorySize ; i++) {
            Block block = new Block(0);
            shareMemory[i]=block;
        }
    }


    /**
     * Get the block instruction in the specific index
     * @param blockIndex Index to get from the instruction memory
     * @return  A Block of the memory
     */
    public Block getBlock(int blockIndex ){
        blockIndex = blockIndex%shareMemorySize;
        return shareMemory[blockIndex];
    }

    /**
     * Set a block of the memory
     * @param blockIndex Index to set from the instruction memory
     * @param block Block to set in the memory
     */
    public void setBlock(int blockIndex, Block block){
        shareMemory[blockIndex % shareMemorySize]=block;
    }

    /**
     * Method that prints all the content of the memory
     */
    public void printMemory(){
        int x=0;
        for(Block b : shareMemory){
            if(shareMemory.length == 8)
                System.out.print("Block " + (x+16) + ": " + b.toString());
            else
                System.out.print("Block " + (x) + ": " + b.toString());
            x++;
        }
    }
}
