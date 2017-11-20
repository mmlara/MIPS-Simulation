package physicalcomponentssimulation.directory;


import java.util.LinkedList;
import java.util.List;

public class Directory {

    private char[] blockStates;
    private Boolean[][] blockInformation;
    private int numBlocks;
    private int numCaches;
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

    public void changeInformation(int numBlock, int numCache, boolean newState){

        blockInformation[numBlock%numBlocks][numCache]=newState;
    }

    public void changeState(int numBlock, char newState){
        blockStates[numBlock%numBlocks] = newState;
    }

    public Boolean getStateOfBlockInOneCache(int numBlock, int numCache){
        return blockInformation[numBlock%numBlocks][numCache];
    }

    public char getStateOfBlock(int numBlock){
        return blockStates[numBlock %numBlocks];
    }

    public int countOfCachesThatContainBlock(int numBlock){
        int x = 0;
        for (Boolean contains : blockInformation[numBlock % numBlocks]) {
            if (contains) {
                x++;
            }
        }
        return x;
    }

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

    public List<Integer> getCachesIdThatShareSomeBlock(int block, int cacheMakeQuestion){
        List<Integer> idCaches= new LinkedList<>();
        for (int i = 0; i <this.numCaches ; i++) {
            if(blockInformation[block%numBlocks][i]==true && i!= cacheMakeQuestion) {
                idCaches.add(i);
            }
        }
        return idCaches;
    }

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
