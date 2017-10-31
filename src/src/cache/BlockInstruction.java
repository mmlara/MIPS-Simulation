package cache;

import processorsparts.Instruction;

public class BlockInstruction {
    final int blockSize =4;
    private Instruction[] dataBlock;


    public BlockInstruction(){
        dataBlock= new Instruction[blockSize];
        for (int i = 0; i <blockSize ; i++) {
           Instruction instruction =new Instruction();
            dataBlock[i]=instruction;
        }


    }

    public Instruction[] getDataBlock() {
        return dataBlock;
    }

    public void setDataBlock(Instruction[] dataBlock) {
        this.dataBlock = dataBlock;
    }

    public void setInstruction(int numInstruction, Instruction instruction){
        dataBlock[numInstruction]=instruction;
    }


}


