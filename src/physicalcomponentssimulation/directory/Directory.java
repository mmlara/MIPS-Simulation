/**
 *  Representation Directory of Processor
 *
 * @author Gómez Brayan, Lara Milton, Quirós Esteban
 * @version 1.0
 * @since 25/11/2017
 */
package physicalcomponentssimulation.directory;


import java.util.LinkedList;
import java.util.List;

public class Directory {

    /**
     * Array that contains all status of the blocks in the directory
     */
    private char[] blockStates;

    /**
     * Array's of Array that have the information of the caches with some block
     */
    private Boolean[][] blockInformation;

    /**
     * Number of Blocks that this  directory manage
     */
    private int numBlocks;

    /**
     * Number of caches that this directory manage
     */
    private int numCaches;

    /**
     * Constructor
     * @param numBlocks Number of Blocks that this  directory manage
     * @param numCaches Number of Directories that this directory manage
     */
    public Directory(int numBlocks,int numCaches){

        this.numBlocks=numBlocks;
        this.numCaches=numCaches;
        blockStates= new char[numBlocks];
        blockInformation= new Boolean[numBlocks][numCaches];

        for (int i = 0; i <numBlocks ; i++) {
            blockStates[i]='U';
            for (int j = 0; j <numCaches ; j++) {
                blockInformation[i][j]=false;
            }
        }

    }

    /**
     * Change the information of a Block-Cache in the directory
     * @param numBlock Index of block that your status is changing
     * @param numCache Index of the cache that your status is changing
     * @param newState New Status of the Block-cache in the directory
     */
    public void changeInformation(int numBlock, int numCache, boolean newState){

        blockInformation[numBlock%numBlocks][numCache]=newState;
    }

    /**
     * Change the state of a block
     * @param numBlock Block to change your state
     * @param newState The new state of the block
     */
    public void changeState(int numBlock, char newState){
        blockStates[numBlock%numBlocks] = newState;
    }

    /**
     * Get the state of Some block
     * @param numBlock number of block that want to know your status
     * @return char of the status of this block
     */

    public char getStateOfBlock(int numBlock){
        return blockStates[numBlock %numBlocks];
    }

    /**
     * get the number of caches that share a block
     * @param numBlock number of block that want to know, how many caches share this block
     * @return  number of the caches that share some block
     */
    public int countOfCachesThatContainBlock(int numBlock){
        int x = 0;
        for (Boolean contains : blockInformation[numBlock % numBlocks]) {
            if (contains) {
                x++;
            }
        }
        return x;
    }

    /**
     * get that id of the cache that contains some specific modified block
     * @param blockNumber number of the block that want  to know where is modified
     * @param whois my id number
     * @return cache id with modified block
     */
    public int getNumberOfCacheWithModifiedBlock(int blockNumber, int whois){
        if(blockStates[blockNumber %numBlocks] == 'M'){
            int x = 0;
            for (Boolean state : blockInformation[blockNumber % numBlocks]){
                if(state && x != whois)
                    return x;
                x++;
            }
        }
        return -1;
    }

    /**
     * Get the ids of the caches that share some block
     * @param block to be find in others cache
     * @param cacheMakeQuestion cache id
     * @return list of the id caches that share a block
     */
    public List<Integer> getCachesIdThatShareSomeBlock(int block, int cacheMakeQuestion){
        List<Integer> idCaches= new LinkedList<>();
        for (int i = 0; i <this.numCaches ; i++) {
            if(blockInformation[block%numBlocks][i]==true && i!= cacheMakeQuestion) {
                idCaches.add(i);
            }
        }
        return idCaches;
    }

    /**
     * Change to modified the specific block
     * @param numBlock block to change
     * @param numCache cache to change
     */
    public void changeToModifiedBlock( int numBlock,int numCache){
        for (int i = 0; i <numCaches ; i++) {
            if (i==numCache){
                blockInformation[numBlock%16][i]=true;
            }else{
                blockInformation[numBlock%16][i]=false;
            }
        }

    }
}
