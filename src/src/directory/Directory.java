package directory;

public class Directory {

    private char[][] directoryData;

    public Directory(int numBlocks,int numCaches){
        directoryData= new char[numBlocks][numCaches];
        for (int i = 0; i <numBlocks ; i++) {
            for (int j = 0; j <numCaches ; j++) {
                directoryData[i][j]='U';
            }
        }
    }

    public void changeState(int numBlock, int numCache, char newState){
        directoryData[numBlock][numCache]=newState;
    }


    public char getStateOfBlockInOneCache(int numBlock, int numCache){
        return directoryData[numBlock][numCache];
    }
}
