package physicalcomponentssimulation.directory;

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

    public void changeState(int numBlock, int numCache, boolean newState){
        blockInformation[numBlock][numCache]=newState;
    }


    public Boolean getStateOfBlockInOneCache(int numBlock, int numCache){
        return blockInformation[numBlock][numCache];
    }
}
