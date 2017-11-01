package physicalcomponentssimulation.cache;

import physicalcomponentssimulation.memory.Memory;

public class DataCache  {


    final int numBlocks=4;
    private Block[] cacheData;
    private int[] tags;
    private boolean[] validInformation;
    Memory shareMemoryAccess;

    /**
     * Constructor default que inicializa los datos de la  caché según el enunciado.
     */
    public DataCache(){
        this.cacheData = new Block[numBlocks];
        tags= new int[numBlocks];
        validInformation= new boolean[numBlocks];
        for (int i = 0; i <numBlocks ; i++) {
            Block b= new Block();
            cacheData[i]= b;
            tags[i]=-1;
            validInformation[i]=false;
        }
    }

    /**
     *
     * @param index índice de la caché del cual se quiere conocer su estado
     * @return estado del bloque en el índice enviado por parámetro
     */
    public boolean itIsAModifiedBlock(int index) {
        return validInformation[index];
    }

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
     * @param index índice  del bloque del cual se desea conocer el estado
     * @return estado de bloque indicado
     */
    public boolean getStatusBlock(int index){
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

    /**
     *
     * @param index índice de la physicalcomponentssimulation.cache del cuál se cargará un dato
     * @return bloque contenido en el índice indica
     */
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
