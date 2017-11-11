package physicalcomponentssimulation.directory;

import java.util.LinkedList;
import java.util.List;

public class Directory {

    private char[] blockStates;
    private Boolean[][] blockInformation;

    public Directory(int numBlocks,int numCaches){

        blockStates= new char[numBlocks];
        blockInformation= new Boolean[numBlocks][numCaches];

        for (int i = 0; i <numBlocks ; i++) {
            blockStates[i]='U';
            for (int j = 0; j <numCaches ; j++) {
                blockInformation[i][j]=false;
            }
        }

    }

    public void changeInformation(int numBlock, int numCache, boolean newState){
        blockInformation[numBlock][numCache]=newState;
    }

    public void changeState(int numBlock, char newState){
        blockStates[numBlock] = newState;
    }

    public Boolean getStateOfBlockInOneCache(int numBlock, int numCache){
        return blockInformation[numBlock][numCache];
    }

    public char getStateOfBlock(int numBlock){
        return blockStates[numBlock];
    }

    public int countOfCachesThatContainBlock(int numBlock){
        int x = 0;
        for (Boolean contains : blockInformation[numBlock]) {
            if (contains) {
                x++;
            }
        }
        return x;
    }

    public int getNumberOfCacheWithModifiedBlock(int blockNumber){
        if(blockStates[blockNumber] == 'M'){
            int x = 0;
            for (Boolean state : blockInformation[blockNumber]){
                if(state)
                    return x;
                x++;
            }
        }
        return -1;
    }
}
