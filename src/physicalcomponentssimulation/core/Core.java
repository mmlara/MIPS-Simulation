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
        int initialInexThread= assignedSystemThread.getInitIndexInMemory();
        int instructionlocationInMemory= initialInexThread+actualPC;
        Instruction nextInstruction= this.instructionCache.getInstruction(instructionlocationInMemory);
        return nextInstruction;
    }

    @Override
    public void run() {
        ALU alu = new ALU(this.context);

        //If there are still "hilillos" in the queue then keep working
        while(!assignedSystemThreads.isEmpty()) {

            //Get "hilillo", load its context and fetch the first instruction
            this.assignedSystemThread = assignedSystemThreads.poll();

            if (assignedSystemThread.getInitialClock() == -1) {
                assignedSystemThread.setInitialClock(this.myProcessor.getClock().getCurrentTime());//
            }
            this.assignedSystemThread.setCurrentCyclesInProcessor(0);//init execution
            Boolean systemThreadFinished = false;
            this.loadContext();
            Boolean instructionSucceeded = false;
            Instruction instruction = getNextInstruction();
            while ((assignedSystemThread.getCurrentCyclesInProcessor() < this.getMyProcessor().getQuantumSize())&& !systemThreadFinished) {
                int cyclesWaitingInThisInstruction=0;
                //If the quantum has not finished or the instruction has not succeeded then keep executing instructions.
                //If the "hilillo" is done then stop working.
                //while((!quantumFinished || instructionSucceeded) && !systemThreadFinished)
                //If the instruction finished and the quantum has not then fetch another instruction
                if (instructionSucceeded) {
                    instruction = getNextInstruction();
                    instructionSucceeded = false;
                }

                //Load
                if (instruction.getOperationCode() == 35) {
                    //Load Implementation
                    executeStoreInstruction(instruction);
                    instructionSucceeded = true;
                    this.context[32] = +1;
                    //cyclesWaitingInThisInstruction; poner acá lo que acumulemde ciclos tratando de ejecutar esta instrucción
                }
                //Store
                else if (instruction.getOperationCode() == 43) {
                    //Store Implementation
                    executeStoreInstruction(instruction);
                    instructionSucceeded = true;
                    this.context[32] = +1;
                    //cyclesWaitingInThisInstruction; poner acá lo que acumulemde ciclos tratando de ejecutar esta instrucción
                }
                //Fin
                else if (instruction.getOperationCode() == 63) {
                    systemThreadFinished = true;
                    instructionSucceeded = true;
                    System.out.println("Terminó un hilillo");
                    cyclesWaitingInThisInstruction=1;
                } else {
                    alu.executionOperation(instruction);
                    instructionSucceeded = true;
                    this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor()+1);//suma un ciclo en procesador;
                    this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution()+1);//add the total time in execution using the processor
                    cyclesWaitingInThisInstruction=1;
                }

                for (int i = 0; i <cyclesWaitingInThisInstruction ; i++) {
                    try {
                        this.getMyProcessor().getLocks().getMutexBarrier().acquire();//get mutex
                        if (this.getMyProcessor().getLocks().getNumCoresWaiting() == this.getMyProcessor().getLocks().getNumCores() - 1) { //the last core in execution
                            this.getMyProcessor().getLocks().getBarrierCycleClock().release(this.getMyProcessor().getLocks().getNumCoresWaiting());
                            this.getMyProcessor().getLocks().getMutexBarrier().release(); //release mutex
                            this.getMyProcessor().getLocks().setNumCoresWaiting(0);
                        } else {
                            this.getMyProcessor().getLocks().setNumCoresWaiting(this.getMyProcessor().getLocks().getNumCoresWaiting() + 1);
                            this.getMyProcessor().getLocks().getMutexBarrier().release(); //release mutex
                            this.getMyProcessor().getLocks().getBarrierCycleClock().acquire();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                this.getMyProcessor().getClock().increaseCurrentTime();

            }//while end of quantum or end of thread

            if (!systemThreadFinished) {
                this.assignedSystemThreads.add(assignedSystemThread);
                this.saveContext();
            } else {// spend the quantum in processor
                this.saveContext();
                this.getMyProcessor().getFinishedThreads().add(assignedSystemThread);
                this.assignedSystemThread.setLastClock(this.myProcessor.getClock().getCurrentTime());
            }
        }//end while empty queue
    }
}
