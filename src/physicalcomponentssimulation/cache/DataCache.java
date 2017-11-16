package physicalcomponentssimulation.cache;

import physicalcomponentssimulation.memory.Memory;

public class DataCache  {


    final int numBlocks=4;
    private Block[] cacheData;
    private int[] tags;
    private int[] validInformation;

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

    public void loadBlock(int blockIndex, Block block){
        cacheData[blockIndex] = block;
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

    public void setIndexStatus(int index, int status){ this.validInformation[index] = status; }

    public Block getBlockAtIndex(int index){return cacheData[index];}

}
