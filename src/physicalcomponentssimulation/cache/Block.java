/**
 *  Representation memory block
 *
 * @author Gomez Bryan, Lara Milton, Quirós Esteban
 * @version 1.0
 * @since 25/11/2017
 */

package physicalcomponentssimulation.cache;

public class Block {

    final int blockSize =4;
    private int[] dataBlock;

    /**
     * Constructor method
     */
    public Block() {
        this.dataBlock =new int[blockSize] ;
        for (int i = 0; i <blockSize ; i++) {
            dataBlock[i]=0;
        }

    }

    /**
     * Constructor method
     * @param x the value of each word in the block
     */
    public Block(int x) {
        this.dataBlock =new int[blockSize] ;
        for (int i = 0; i <blockSize ; i++) {
            dataBlock[i]=x;
        }

    }

    /**
     * Set the value of a word
     * @param index number of word in the block
     * @param value value to set
     */
    public void setWord(int index, int value ){
        this.dataBlock[index]=value;
    }

    /**
     * Get block size
     * @return an integer with the size og the block
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * Get the block
     * @return an array representing all words in the block
     */
    public int[] getDataBlock() {
        return dataBlock;
    }

    /**
     * Set data on the block
     * @param dataBlock data to set in the block
     */
    public void setDataBlock(int[] dataBlock) {
        this.dataBlock = dataBlock;
    }

    /**
     * Concatenate all words in the block in a string
     * @return a string with the value of the words in the block
     */
    @Override
    public String toString() {
        return  this.dataBlock[0]+" "+this.dataBlock[1]+" "+this.dataBlock[2]+" "+this.dataBlock[3]+  "\n";
    }
}
