package physicalcomponentssimulation.processorsparts;



public class ALU {

    Instruction instruction;
    //tabla de instrucciones.
    private final int JR    = 2;
    private final int JAL   = 3;
    private final int BEQZ  = 4;
    private final int BNEZ  = 5;
    private final int DADDI = 8;
    private final int DMUL  = 12;
    private final int DDIV  = 14;
    private final int DADD  = 32;
    private final int DSUB  = 34;
    private final int LW    = 35;
    private final int SW    = 43;
    private final int FIN   = 63;

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public boolean executionOperation(Instruction instruction){
        boolean successOperation=false;

        switch (instruction.getOperationCode()){
         //llenar los cases con las respectivas instrucciones
            case JR:
                break;

            case JAL:
                break;

            case BEQZ:
                break;

            case BNEZ:
                break;

            case DADDI :
                break;

            case DMUL:
                break;

            case DDIV:
                break;

            case DADD:
                break;

            case DSUB:
                break;

            case LW:
                break;

            case SW:
                break;

            case FIN:
            break;


        }
        return successOperation;
    }
}
