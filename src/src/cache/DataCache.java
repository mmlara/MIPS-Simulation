package cache;

import memory.Memory;

public class DataCache  {


    final int numBlocks=4;
    private Block[] cacheData;
    private int[] tags;
    private boolean[] validInformation;
    Memory shareMemoryAccess;

    public DataCache(){
        this.cacheData = new Block[numBlocks];
        for (int i = 0; i <numBlocks ; i++) {
            Block b= new Block();
            cacheData[i]= b;
        }

        tags= new int[numBlocks];
        for (int i = 0; i <numBlocks ; i++) {
            tags[i]=-1;
        }

    }

    public boolean itIsAModifiedBlock(int index) {
        return validInformation[index];
    }


    public int getWord(int index, int numWord) {
        Block block= cacheData[index];
        int word = block.getDataBlock()[numWord];
        return word;
    }



    public Block loadBlock(int index) {
        Block block=shareMemoryAccess.getBlock(index);
        cacheData[index%numBlocks]= block;
        return block;
    }


    public void storeBlock(int blockIndex, Block block) {
        shareMemoryAccess.setBlock(blockIndex , block);
    }

    public int getnumBlocks() {
        return numBlocks;
    }

    public Block[] getCacheData() {
        return cacheData;
    }

    public void setCacheData(Block[] cacheData) {
        this.cacheData = cacheData;
    }

    public Memory getShareMemoryAccess() {
        return shareMemoryAccess;
    }

    public void setShareMemoryAccess(Memory shareMemoryAccess) {
        this.shareMemoryAccess = shareMemoryAccess;
    }


}
