/**
 *  Data Cache
 *
 * @author Gómez Brayan, Lara Milton, Quirós Esteban
 * @version 1.0
 * @since 25/11/2017
 */
package physicalcomponentssimulation.cache;

import physicalcomponentssimulation.memory.Memory;

public class DataCache  {


    /**
     * block's size of cache
     */
    final int numBlocks=4;

    /**
     * array of Blocks to represent the cache
     */
    private Block[] cacheData;

    /**
     * array to save the current tag in some index of the cache
     */
    private int[] tags;

    /**
     * array to save the state of some block in the cache
     */
    private int[] validInformation;

    /**
     * tags of the state from some current block in the cache
     */
    final private int I = 0; //invalid
    final private int C = 1; //shared
    final private int M = 2; //modified


    /**
     * Constructor default que inicializa los datos de la  caché según el enunciado.
     */
    public DataCache(){
        this.cacheData = new Block[numBlocks];
        tags= new int[numBlocks];
        validInformation= new int[numBlocks];
        for (int i = 0; i <numBlocks ; i++) {
            Block b= new Block(0);
            cacheData[i]= b;
            tags[i]=-1;
            validInformation[i] = I;
        }
    }

    /**
     *
     * @param index índice de la caché del cual se quiere conocer su estado
     * @return si el bloque en el indice indicado esta modificado
     */
    public boolean isItAModifiedBlock(int index) { return validInformation[index] == M; }

    /**
     *
     * @param index índice de la caché que se desea acceder
     * @param numWord número de palabra respecto a un índice que se desea acceder
     * @return la palabra según el índice y número de palabre indicado
     */
    public int getWord(int index, int numWord) {
        Block block= cacheData[index];
        int word = block.getDataBlock()[numWord];
        return word;
    }

    /**
     *
     * @param index índice de la caché que se desea acceder
     * @param numWord número de palabra respecto a un índice que se desea acceder
     * @param word palabra que se quiere escribir
     */
    public void setWord(int index, int numWord, int word) {
        this.getCacheData()[index].setWord(numWord,word);
    }

    /**
     *
     * @param index índice  del bloque del cual se desea conocer el estado
     * @return estado de bloque indicado
     */
    public int getStatusBlock(int index){
        return this.validInformation[index];
    }

    /**
     *
     * @param index indica el índice del bloque del cual se desea saber su tag
     * @return el tag del índice indicado.
     */
    public int getTagOfBlock(int index){
        return tags[index];
    }

    public void loadBlock(int blockIndex,int blockNumber, Block block){
        cacheData[blockIndex] = block;
        tags[blockIndex] = blockNumber;
    }


    /**
     *  Get number of cache´s block
     * @return int with the number of blocks
     */
    public int getnumBlocks() {
        return numBlocks;
    }

    /**
     *  Get array of blocks
     * @return Array of blocks
     */
    public Block[] getCacheData() {
        return cacheData;
    }

    /**
     * set the Array of block
     * @param cacheData array of Block to set
     */
    public void setCacheData(Block[] cacheData) {
        this.cacheData = cacheData;
    }

    /**
     * set state in some index
     * @param index to be modified
     * @param status the new state in the index send by param
     */
    public void setIndexStatus(int index, int status){ this.validInformation[index] = status; }

    /**
     * get some block by index
     * @param index in array of block
     * @return the block referenced by the index
     */
    public Block getBlockAtIndex(int index){return cacheData[index];}

    /**
     * Method that prints caches content
     */
    public void printCache(){
        for(int i=0;i<numBlocks; i++){
            System.out.println("Indice " + i + " Tag: " + tags[i] + " State: " + validInformation[i] + " Content: " + cacheData[i].toString());
        }
    }

}
