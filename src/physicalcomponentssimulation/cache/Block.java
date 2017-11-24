package physicalcomponentssimulation.cache;

public class Block {

    final int blockSize =4;
    private int[] dataBlock;


    public Block() {
        this.dataBlock =new int[blockSize] ;
        for (int i = 0; i <blockSize ; i++) {
            dataBlock[i]=0;
        }

    }

    //TODO quitar el mod 4 que tiene el bloque por default
    public Block(int x) {
        this.dataBlock =new int[blockSize] ;
        for (int i = 0; i <blockSize ; i++) {
            dataBlock[i]=x;
        }

    }


    public void setWord(int index, int value ){
        this.dataBlock[index]=value;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public int[] getDataBlock() {
        return dataBlock;
    }

    public void setDataBlock(int[] dataBlock) {
        this.dataBlock = dataBlock;
    }




    @Override
    public String toString() {
        return  this.dataBlock[0]+" "+this.dataBlock[1]+" "+this.dataBlock[2]+" "+this.dataBlock[3]+  "\n";
    }
}
