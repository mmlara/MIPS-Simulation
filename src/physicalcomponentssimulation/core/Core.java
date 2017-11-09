package physicalcomponentssimulation.core;

import physicalcomponentssimulation.cache.DataCache;
import physicalcomponentssimulation.cache.InstructionCache;
import physicalcomponentssimulation.processor.Processor;
import physicalcomponentssimulation.processorsparts.ALU;
import physicalcomponentssimulation.processorsparts.Instruction;
import physicalcomponentssimulation.systemthread.SystemThread;

import java.util.Queue;

public class Core implements Runnable{

    private int[] context;
    private final int contextSize=33;//32 registros + pc;
    private DataCache dataCache;
    private InstructionCache instructionCache;
    private SystemThread assignedSystemThread;//para saber a quien estoy ejecutando
    private Queue<SystemThread> assignedSystemThreads;
    private Processor myProcessor;

    public Core(Queue<SystemThread> assignedSystemThreads){
        this.context= new int[contextSize];
        for (int i = 0; i <contextSize ; i++) {
            this.context[i]=0;
        }
        this.assignedSystemThreads =  assignedSystemThreads;
    }

    public void setContext(int[] context) {
        this.context = context;
    }

    public int[] getContext() {
        return this.context;
    }

    public void saveContext(){
        for (int i = 0; i <contextSize-1 ; i++) {
            this.assignedSystemThread.getContext()[i]=context[i];
        }
       this.assignedSystemThread.setPc(this.context[32]);
    }

    public void loadContext(){
        for (int i = 0; i <contextSize-1 ; i++) {
            this.context[i]= this.assignedSystemThread.getContext()[i];
        }
        this.context[32]=this.assignedSystemThread.getPc();
    }
    public DataCache getDataCache() {
        return this.dataCache;
    }

    public void setDataCache(DataCache dataCache) {
        this.dataCache = dataCache;
    }

    public InstructionCache getInstructionCache() {
        return this.instructionCache;
    }

    public void setInstructionCache(InstructionCache instructionCache) {
        this.instructionCache = instructionCache;
    }

    public Processor getMyProcessor() {
        return myProcessor;
    }

    public void setMyProcessor(Processor myProcessor) {
        this.myProcessor = myProcessor;
    }

    public void executeLoadInstruction(Instruction instruction){

    }

    public void executeStoreInstruction(Instruction instruction){

    }

    //TODO cuando se tenga acceso al reloj, preguntar que si es -1 el valor de hilillo.initialClock, en caso de ser así, asignarle el reloj actual.

    public void setAsignedSystemThread(SystemThread systemThread){
        this.assignedSystemThread = systemThread;
    }

    public Instruction getNextInstruction(){

        int actualPC=this.assignedSystemThread.getPc();
        actualPC=4;

        int initialInexThread= assignedSystemThread.getInitIndexInMemory();

        int instructionlocationInMemory= initialInexThread +actualPC;
        Instruction nextInstruction= this.instructionCache.getInstruction(instructionlocationInMemory);
        return nextInstruction;
    }

    @Override
    public void run() {
        ALU alu = new ALU(this.context);

        //If there are still "hilillos" in the queue then keep working
        while(!assignedSystemThreads.isEmpty()){

            //Get "hilillo", load its context and fetch the first instruction
            assignedSystemThread = assignedSystemThreads.poll();
            if(assignedSystemThread.getInitialClock()==-1){
                assignedSystemThread.setInitialClock(this.myProcessor.getClock().getCurrentTime());//
            }

            this.loadContext();
            Boolean systemThreadFinished = false;
            Boolean instructionSucceeded = false;
            Instruction instruction = getNextInstruction();

            //If the quantum has not finished or the instruction has not succeeded then keep executing instructions.
            //If the "hilillo" is done then stop working.
            //while((!quantumFinished || instructionSucceeded) && !systemThreadFinished)

                //If the instruction finished and the quantum has not then fetch another instruction
                if(instructionSucceeded){
                    instruction = getNextInstruction();
                    instructionSucceeded = false;
                }

                //Load
                if(instruction.getOperationCode() == 35){
                    //Load Implementation
                    executeStoreInstruction(instruction);
                    instructionSucceeded = true;
                    this.context[32] =+ 1;
                }
                //Store
                else if(instruction.getOperationCode() == 43){
                    //Store Implementation
                    executeStoreInstruction(instruction);
                    instructionSucceeded = true;
                    this.context[32] =+ 1;
                }
                //Fin
                else if(instruction.getOperationCode() == 63){
                    systemThreadFinished = true;
                    instructionSucceeded = true;
                }
                else{
                    alu.executionOperation(instruction);
                    instructionSucceeded = true;
                }

                //barrier.await();

            //}

            //If the "hilillo" did not finish then put it back into the queue
            if(!systemThreadFinished)
                assignedSystemThreads.add(assignedSystemThread);
            saveContext();
        }
    }


}
