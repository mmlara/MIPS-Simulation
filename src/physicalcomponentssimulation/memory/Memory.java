package physicalcomponentssimulation.memory;
import physicalcomponentssimulation.cache.Block;


//mapea toda la memoria compartida, los dos núcleos deberian tener un puntero a la misma.
//los primeros 16 bloques estan en P0 y los otros 8 en P1
public class Memory {

    public int shareMemorySize;
    private Block[] shareMemory;

    public Memory(int memorySize) {
        this.shareMemorySize=memorySize;
        this.shareMemory = new Block[shareMemorySize];
        for (int i = 0; i <shareMemorySize ; i++) {
            Block block = new Block();
            shareMemory[i]=block;
        }
    }

    public Block getBlock(int blockIndex ){
        return shareMemory[blockIndex];
    }

    //este método recibe la ubicación real en memoria y no el tag del bloque.
    public void setBlock(int blockIndex, Block block){
        shareMemory[blockIndex]=block;
    }
}
