package physicalcomponentssimulation.processorsparts;



public class ALU {

    Instruction instruction;
    private int[] registers;

    //Register that contains the PC
    private int PC = 32;

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

    public ALU(int[] context){
        this.registers = context;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public boolean executionOperation(Instruction instruction){
        boolean successOperation=false;

        switch (instruction.getOperationCode()){
            case JR:
                //Set PC to the value of the first parameter
                registers[PC] = registers[instruction.getFirsParameter()];
                successOperation = true;
                break;

            case JAL:
                //Set register 31 to PC and then set PC = PC + n, where n is the third parameter of instruction.
                registers[31] = registers[PC];
                registers[PC] += instruction.getThirdParameter();
                successOperation = true;
                break;

            case BEQZ:
                //If source register is 0 then PC is equal PC + immediate value * 4
                if(registers[instruction.getFirsParameter()] == 0) {
                    registers[PC] +=  instruction.getThirdParameter()+1;
                }
                else{
                    registers[PC] += 1;
                }
                successOperation = true;
                break;

            case BNEZ:
                //If source register is not 0 then PC is equal PC + immediate value * 4
                if(registers[instruction.getFirsParameter()] != 0) {
                    registers[PC] +=  instruction.getThirdParameter()+1;
                }
                else{
                    registers[PC] += 1;
                }
                successOperation = true;
                break;

            case DADDI :
                //Store the sum of the contents of the source register and the immediate value(third parameter) in the destiny register.
                registers[instruction.getSecondParameter()] = registers[instruction.getFirsParameter()] + instruction.getThirdParameter();
                successOperation = true;
                registers[PC] += 1;
                break;

            case DMUL:
                //Store the sum of the contents of the source register and the immediate value(third parameter) in the destiny register.
                registers[instruction.getThirdParameter()] = registers[instruction.getSecondParameter()] * registers[instruction.getFirsParameter()];
                successOperation = true;
                registers[PC] += 1;
                break;

            case DDIV:
                //Store the sum of the contents of the source register and the immediate value(third parameter) in the destiny register.
                registers[instruction.getThirdParameter()] = registers[instruction.getFirsParameter()] / registers[instruction.getSecondParameter()];
                successOperation = true;
                registers[PC] += 1;
                break;

            case DADD:
                //Store the sum of the contents of the source register and the immediate value(third parameter) in the destiny register.
                registers[instruction.getThirdParameter()] = registers[instruction.getSecondParameter()] + registers[instruction.getFirsParameter()];
                successOperation = true;
                registers[PC] += 1;
                break;

            case DSUB:
                //Store the sum of the contents of the source register and the immediate value(third parameter) in the destiny register.
                registers[instruction.getThirdParameter()] = registers[instruction.getFirsParameter()] - registers[instruction.getSecondParameter()];
                successOperation = true;
                registers[PC] += 1;
                break;

        }
        return successOperation;
    }
}
