package physicalcomponentssimulation.core;

import physicalcomponentssimulation.cache.DataCache;
import physicalcomponentssimulation.cache.InstructionCache;
import physicalcomponentssimulation.processor.Processor;
import physicalcomponentssimulation.processorsparts.ALU;
import physicalcomponentssimulation.processorsparts.Instruction;
import physicalcomponentssimulation.systemthread.SystemThread;

import java.util.Queue;

public class Core implements Runnable {

    private int[] context;
    private int coreID;
    private final int contextSize = 33;//32 registros + pc;
    private DataCache dataCache;
    private InstructionCache instructionCache;
    private SystemThread assignedSystemThread;//para saber a quien estoy ejecutando
    private Queue<SystemThread> assignedSystemThreads;
    private Processor myProcessor;
    private boolean instructionSucceeded = false;

    public Core(Queue<SystemThread> assignedSystemThreads) {
        this.context = new int[contextSize];
        for (int i = 0; i < contextSize; i++) {
            this.context[i] = 0;
        }
        this.assignedSystemThreads = assignedSystemThreads;
    }

    public void setContext(int[] context) {
        this.context = context;
    }

    public int[] getContext() {
        return this.context;
    }

    public void saveContext() {
        for (int i = 0; i < contextSize - 1; i++) {
            this.assignedSystemThread.getContext()[i] = context[i];
        }
        this.assignedSystemThread.setPc(this.context[32]);
    }

    public void loadContext() {
        for (int i = 0; i < contextSize - 1; i++) {
            this.context[i] = this.assignedSystemThread.getContext()[i];
        }
        this.context[32] = this.assignedSystemThread.getPc();
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

    public int getCoreID() {
        return coreID;
    }

    public void setCoreID(int coreID) {
        this.coreID = coreID;
    }

    public int executeLoadInstruction(Instruction instruction) {
        try {
            int cyclesWaitingInThisInstruction = 0;
            //Got the cache lock
            if(this.getMyProcessor().getLocks().getCacheMutex()[getCoreID()].tryAcquire()){
                try {
                    cyclesWaitingInThisInstruction++;
                    //Check if data is in cache
                    if (true/*If it is then read the word and return*/) {
                        instructionSucceeded = true;
                        return cyclesWaitingInThisInstruction;
                    }
                    //If it is not then select victim
                    else {
                        //Check victims state
                        if(true/*If victims state is not invalid then*/){
                            if(true/*If you got the victims directory lock*/){
                                try{
                                    if(true/*If victims directory is local*/)
                                        cyclesWaitingInThisInstruction++;
                                    else
                                        cyclesWaitingInThisInstruction += 5;
                                    if(true/*If victim is shared*/) {
                                        //Change state of block in cache to invalid
                                        //Update directory
                                    }
                                    else{//It is modified
                                        if(true/*If you got the victims memory bus lock*/){
                                            try{
                                                if(true/*If bus is local*/)
                                                    cyclesWaitingInThisInstruction += 16;
                                                else
                                                    cyclesWaitingInThisInstruction += 40;
                                                //Change block state in cache to invalid
                                                //Update directory
                                                //Write modified block to memory
                                            }
                                            finally {
                                                //Release bus lock
                                            }
                                        }
                                        else{//If you did not get the memory lock release everything and restart
                                            return cyclesWaitingInThisInstruction;
                                        }
                                    }
                                }
                                finally {
                                    //Release the victim directories lock
                                }
                            }
                            else{//If you did not get the directory lock release everything and restart the instruction
                                return cyclesWaitingInThisInstruction;
                            }
                        }
                    }

                    //Victim is evicted. Now fetch the block and load it to cache
                    if(true/*If you got the house directory for the target block*/){
                        try{
                            if(true/*If directory is local*/)
                                cyclesWaitingInThisInstruction += 1;
                            else
                                cyclesWaitingInThisInstruction += 5;
                            if(true/*If you got the target block memory bus lock*/){
                                try{
                                    if(true/*If bus is local*/)
                                        cyclesWaitingInThisInstruction += 16;
                                    else
                                        cyclesWaitingInThisInstruction += 40;
                                    if(true/*Target block is modified*/){
                                        if(true/*If you got the lock to the cache with the modified block*/){
                                            try {
                                                if(true/*If directory is local*/)
                                                    cyclesWaitingInThisInstruction += 1;
                                                else
                                                    cyclesWaitingInThisInstruction += 5;
                                                //Get modified block from cache and write to memory
                                                //Update house directory of target block
                                            }
                                            finally {
                                                //Release cache lock
                                            }
                                        }
                                        else{//If you did not get the cache lock release everything and restart
                                            return cyclesWaitingInThisInstruction;
                                        }
                                    }
                                    else{
                                        //Update target block directory accordingly
                                    }
                                    //Cycles symbolize access to memory to fetch the block
                                    if(true/*If bus is local*/)
                                        cyclesWaitingInThisInstruction += 16;
                                    else
                                        cyclesWaitingInThisInstruction += 40;
                                    //Fetch the block from memory
                                    //Update our cache with block from memory
                                    //Load word to register from cache
                                    return cyclesWaitingInThisInstruction;
                                }
                                finally {
                                    //Release bus lock
                                }
                            }
                            else{//If you did not get the memory lock lock release everything and restart the instruction
                                return cyclesWaitingInThisInstruction;
                            }
                        }
                        finally {
                            //Release directory lock
                        }
                    }
                    else{//If you did not get the house directory lock release everything and restart the instruction
                        return cyclesWaitingInThisInstruction;
                    }
                }
                finally{
                    this.getMyProcessor().getLocks().getCacheMutex()[getCoreID()].release();
                }
            }
            //Did not get the cache lock
            else {
                cyclesWaitingInThisInstruction++;
                return cyclesWaitingInThisInstruction;
            }
        }catch (Exception e){e.printStackTrace(); return 1;}
    }

    public int executeStoreInstruction(Instruction instruction) {
        return 0;
    }



    //TODO cuando se tenga acceso al reloj, preguntar que si es -1 el valor de hilillo.initialClock, en caso de ser así, asignarle el reloj actual.

    public void setAsignedSystemThread(SystemThread systemThread) {
        this.assignedSystemThread = systemThread;
    }

    public Instruction getNextInstruction() {

        int actualPC = this.assignedSystemThread.getPc();
        int initialInexThread = assignedSystemThread.getInitIndexInMemory();

        int instructionlocationInMemory = initialInexThread + actualPC;

        Instruction nextInstruction = this.instructionCache.getInstruction(instructionlocationInMemory);
        return nextInstruction;
    }

    @Override
    public void run() {
        ALU alu = new ALU(this.context);

        //If there are still "hilillos" in the queue then keep working
        int cycleNumber = 0;
        boolean stillAlive=true;
        try {

            while (stillAlive) {
                this.myProcessor.getLocks().getQueueMutex().acquire();
                if (this.myProcessor.getAssignedSystemThreads().isEmpty()) {
                    stillAlive = false;
                    this.getMyProcessor().getLocks().getMutexBarrier().acquire();//get mutex
                    if (this.getMyProcessor().getLocks().getNumCoresWaiting() == this.getMyProcessor().getLocks().getNumCores() - 1) { //the last core in execution
                        this.getMyProcessor().getClock().increaseCurrentTime();//move on the clock when the third hilillo arrive
                        //release mutex
                        this.getMyProcessor().getLocks().setNumCores(this.getMyProcessor().getLocks().getNumCores()-1);
                        this.getMyProcessor().getLocks().getBarrierCycleClock().release(this.getMyProcessor().getLocks().getNumCoresWaiting());

                        this.getMyProcessor().getLocks().setNumCoresWaiting(0);
                        this.getMyProcessor().getLocks().getMutexBarrier().release();
                        this.myProcessor.getLocks().getQueueMutex().release();
                    } else {
                        System.out.println("falso");
                        this.getMyProcessor().getLocks().setNumCores(this.getMyProcessor().getLocks().getNumCores()-1);
                        //this.getMyProcessor().getLocks().setNumCoresWaiting(this.getMyProcessor().getLocks().getNumCoresWaiting() + 1);
                        this.getMyProcessor().getLocks().getMutexBarrier().release(); //release mutex
                        this.myProcessor.getLocks().getQueueMutex().release();
                    }

                } else {
                    this.assignedSystemThread = assignedSystemThreads.poll();
                    this.myProcessor.getLocks().getQueueMutex().release();


                    //Get "hilillo", load its context and fetch the first instruction

                    if (assignedSystemThread.getInitialClock() == -1) {
                        assignedSystemThread.setInitialClock(this.myProcessor.getClock().getCurrentTime());//
                    }
                    this.assignedSystemThread.setCurrentCyclesInProcessor(0);//init execution
                    Boolean systemThreadFinished = false;
                    this.loadContext();
                    Instruction instruction = getNextInstruction();

                    //If the quantum has not finished or the instruction has not succeeded then keep executing instructions.
                    //If the "hilillo" is done then stop working.
                    while (((assignedSystemThread.getCurrentCyclesInProcessor() < this.getMyProcessor().getQuantumSize()) || !instructionSucceeded) && !systemThreadFinished) {
                        System.out.println("número de ciclo" + cycleNumber + "del hilillo " + assignedSystemThread.getIdHilillo());
                        cycleNumber++;
                        int cyclesWaitingInThisInstruction = 0;
                        //If the instruction finished and the quantum has not then fetch another instruction
                        if (instructionSucceeded) {
                            instruction = getNextInstruction();
                            instructionSucceeded = false;
                        }

                        //Load
                        if (instruction.getOperationCode() == 35) {
                            //Load Implementation
                            cyclesWaitingInThisInstruction = executeLoadInstruction(instruction);
                            if(instructionSucceeded)
                                this.context[32] += 1;
                            //cyclesWaitingInThisInstruction; poner acá lo que acumulemde ciclos tratando de ejecutar esta instrucción
                        }
                        //Store
                        else if (instruction.getOperationCode() == 43) {
                            //Store Implementation
                            cyclesWaitingInThisInstruction = executeStoreInstruction(instruction);
                            if(instructionSucceeded)
                                this.context[32] += 1;
                            //cyclesWaitingInThisInstruction; poner acá lo que acumulemde ciclos tratando de ejecutar esta instrucción
                        }
                        //Fin
                        else if (instruction.getOperationCode() == 63) {
                            systemThreadFinished = true;
                            instructionSucceeded = true;
                            System.out.println("Terminó el hilillo " + this.assignedSystemThread.getIdHilillo());
                            cyclesWaitingInThisInstruction = 1;
                        } else {
                            alu.executionOperation(instruction);
                            instructionSucceeded = true;
                            this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + 1);//suma un ciclo en procesador;
                            this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + 1);//add the total time in execution using the processor
                            cyclesWaitingInThisInstruction = 1;
                        }
                        System.out.println("ejecuta la instrucción hilillo " + this.assignedSystemThread.getIdHilillo());

                        for (int i = 0; i < cyclesWaitingInThisInstruction; i++) {

                            this.getMyProcessor().getLocks().getMutexBarrier().acquire();//get mutex
                            if (this.getMyProcessor().getLocks().getNumCoresWaiting() == this.getMyProcessor().getLocks().getNumCores() - 1) { //the last core in execution
                                System.out.println("verdadero");
                                this.getMyProcessor().getClock().increaseCurrentTime();//move on the clock when the third hilillo arrive
                                //release mutex
                                this.getMyProcessor().getLocks().getBarrierCycleClock().release(this.getMyProcessor().getLocks().getNumCoresWaiting());
                                this.getMyProcessor().getLocks().setNumCoresWaiting(0);
                                this.getMyProcessor().getLocks().getMutexBarrier().release();
                            } else {
                                System.out.println("falso");
                                this.getMyProcessor().getLocks().setNumCoresWaiting(this.getMyProcessor().getLocks().getNumCoresWaiting() + 1);
                                this.getMyProcessor().getLocks().getMutexBarrier().release(); //release mutex
                                this.getMyProcessor().getLocks().getBarrierCycleClock().acquire();
                            }

                        }

                        System.out.println("Se desbloquea el hilillo " + assignedSystemThread.getIdHilillo());
                    }//while end of quantum or end of thread

                    if (!systemThreadFinished) {
                        this.assignedSystemThreads.add(assignedSystemThread);
                        this.saveContext();
                    } else {// spend the quantum in processor
                        this.saveContext();
                        this.getMyProcessor().getFinishedThreads().add(assignedSystemThread);
                        this.assignedSystemThread.setLastClock(this.myProcessor.getClock().getCurrentTime());
                    }
                }
            }//end while still alive
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("TERMINO EL CORE "+this.getCoreID() +"y tardó el último hilillo"+this.assignedSystemThread.getNumCyclesInExecution());
        System.out.println();
    }
}
