package physicalcomponentssimulation.core;

import physicalcomponentssimulation.cache.Block;
import physicalcomponentssimulation.cache.DataCache;
import physicalcomponentssimulation.cache.InstructionCache;
import physicalcomponentssimulation.memory.Memory;
import physicalcomponentssimulation.processor.Processor;
import physicalcomponentssimulation.processorsparts.ALU;
import physicalcomponentssimulation.processorsparts.Instruction;
import physicalcomponentssimulation.systemthread.SystemThread;
import physicalcomponentssimulation.directory.Directory;

import java.util.Queue;
import java.util.Scanner;

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

    final private int I = 0;
    final private int C = 1;
    final private int M = 2;

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
            int blockNumber = instruction.getThirdParameter() + context[instruction.getFirsParameter()];
            int blockIndex = blockNumber % 4;
            //Got the cache lock
            if(this.getMyProcessor().getLocks().getCacheMutex()[getCoreID()].tryAcquire()){
                try {
                    cyclesWaitingInThisInstruction++;
                    int tag = dataCache.getTagOfBlock(blockIndex);
                    //Check if data is in cache
                    if (tag == blockNumber) {
                        instructionSucceeded = true;
                        return cyclesWaitingInThisInstruction;
                    }
                    else {//If it is not then select victim
                        int state = dataCache.getStatusBlock(blockIndex);
                        //Check victims state
                        if(state != I){
                            //Try to get lock of victims directory
                            if(getMyProcessor().getLocks().getDirectoryMutex()[ ( dataCache.getTagOfBlock(blockIndex) <= 15 ) ? 0 : 1 ].tryAcquire()){
                                try{
                                    Directory dir;
                                    if(coreID < 2) {
                                        cyclesWaitingInThisInstruction += (dataCache.getTagOfBlock(blockIndex) <= 15) ? 1 : 5;
                                        dir = (dataCache.getTagOfBlock(blockIndex) <= 15) ? getMyProcessor().getDirectory() : getMyProcessor().getNeigborProcessor().getDirectory();
                                    }
                                    else {
                                        cyclesWaitingInThisInstruction += (dataCache.getTagOfBlock(blockIndex) > 15) ? 1 : 5;
                                        dir = (dataCache.getTagOfBlock(blockIndex) > 15) ? getMyProcessor().getDirectory() : getMyProcessor().getNeigborProcessor().getDirectory();
                                    }
                                    if(dataCache.getStatusBlock(blockIndex) == C) {
                                        //Change state of block in cache to invalid
                                        dataCache.setIndexStatus(blockIndex, I);
                                        //Update directory
                                        dir.changeInformation(blockNumber%16, coreID, false);
                                        if(dir.countOfCachesThatContainBlock(blockNumber%16) == 0)
                                            dir.changeState(blockNumber%16,'U');
                                    }
                                    else{//It is modified
                                        if(getMyProcessor().getLocks().getBus()[ ( dataCache.getTagOfBlock(blockIndex) <= 15 ) ? 0 : 1 ].tryAcquire()){
                                            try{
                                                Memory mem;
                                                if(coreID < 2) {
                                                    cyclesWaitingInThisInstruction += (dataCache.getTagOfBlock(blockIndex) <= 15) ? 16 : 40;
                                                    mem = (dataCache.getTagOfBlock(blockIndex) <= 15) ? getMyProcessor().getMemory() : getMyProcessor().getNeigborProcessor().getMemory();
                                                }
                                                else {
                                                    cyclesWaitingInThisInstruction += (dataCache.getTagOfBlock(blockIndex) > 15) ? 16 : 40;
                                                    mem = (dataCache.getTagOfBlock(blockIndex) > 15) ? getMyProcessor().getMemory() : getMyProcessor().getNeigborProcessor().getMemory();
                                                }
                                                //Change block state in cache to invalid
                                                dataCache.setIndexStatus(blockIndex, I);
                                                //Update directory
                                                dir.changeInformation(blockNumber%16, coreID, false);
                                                dir.changeState(blockNumber%16,'U');
                                                //Write modified block to memory
                                                mem.setBlock(blockNumber%16, dataCache.getBlockAtIndex(blockIndex));
                                            }
                                            finally {
                                                getMyProcessor().getLocks().getBus()[ ( dataCache.getTagOfBlock(blockIndex) <= 15 ) ? 0 : 1 ].release();
                                            }
                                        }
                                        else{//If you did not get the memory lock release everything and restart
                                            return cyclesWaitingInThisInstruction;
                                        }
                                    }
                                }
                                finally {
                                    getMyProcessor().getLocks().getDirectoryMutex()[ ( dataCache.getTagOfBlock(blockIndex) <= 15 ) ? 0 : 1 ].release();
                                }
                            }
                            else{//If you did not get the directory lock release everything and restart the instruction
                                return cyclesWaitingInThisInstruction;
                            }
                        }
                    }

                    int wordIndex = ( ( instruction.getThirdParameter() + context[instruction.getFirsParameter()] ) % 16 ) / 4;
                    //Victim is evicted. Now fetch the block and load it to cache
                    if(getMyProcessor().getLocks().getDirectoryMutex()[ ( blockNumber <= 15 ) ? 0 : 1 ].tryAcquire()){
                        try{
                            Directory dir;
                            if(coreID < 2) {
                                cyclesWaitingInThisInstruction += (blockNumber <= 15) ? 1 : 5;
                                dir = (blockNumber <= 15) ? getMyProcessor().getDirectory() : getMyProcessor().getNeigborProcessor().getDirectory();
                            }
                            else {
                                cyclesWaitingInThisInstruction += (blockNumber > 15) ? 1 : 5;
                                dir = (blockNumber > 15) ? getMyProcessor().getDirectory() : getMyProcessor().getNeigborProcessor().getDirectory();
                            }
                            if(getMyProcessor().getLocks().getBus()[ ( blockNumber <= 15 ) ? 0 : 1 ].tryAcquire()){
                                try{
                                    Memory mem;
                                    if(coreID < 2) {
                                        cyclesWaitingInThisInstruction += (blockNumber <= 15) ? 16 : 40;
                                        mem = (blockNumber <= 15) ? getMyProcessor().getMemory() : getMyProcessor().getNeigborProcessor().getMemory();
                                    }
                                    else {
                                        cyclesWaitingInThisInstruction += (blockNumber > 15) ? 16 : 40;
                                        mem = (blockNumber > 15) ? getMyProcessor().getMemory() : getMyProcessor().getNeigborProcessor().getMemory();
                                    }
                                    if(dir.getStateOfBlock(blockNumber%16) == 'M'){
                                        if(getMyProcessor().getLocks().getCacheMutex()[dir.getNumberOfCacheWithModifiedBlock(blockNumber%16)].tryAcquire()){
                                            try {
                                                //TODO add proper delay for accessing cache?
                                                //Get modified block from cache and write to memory
                                                int numberOfCache = dir.getNumberOfCacheWithModifiedBlock(blockNumber%16);
                                                DataCache cache;
                                                if(numberOfCache < 2){//If block is in caches of first processor
                                                    //If I am on the first processor get that data cache of my processor else get that one of my neighbor processor
                                                    cache = (coreID < 2) ? getMyProcessor().getCores()[numberOfCache].getDataCache() : getMyProcessor().getNeigborProcessor().getCores()[numberOfCache].getDataCache();
                                                }
                                                else{//If block is in cache of the second processor
                                                    //If I am on the first processor get that data cache of my processor else get that one of my neighbor processor
                                                    cache = (coreID > 2) ? getMyProcessor().getCores()[0].getDataCache() : getMyProcessor().getNeigborProcessor().getCores()[0].getDataCache();
                                                }
                                                Block target = cache.getBlockAtIndex(blockIndex);
                                                mem.setBlock(blockNumber%16, target);
                                                //Update house directory of target block and cache where it was
                                                //Set cache block to shared
                                                cache.setIndexStatus(blockIndex, C);
                                                //Set state of block in directory to shared and add my cache to the ones that have it
                                                dir.changeState(blockNumber%16, 'C');
                                                dir.changeInformation(blockNumber%16, coreID, true);
                                            }
                                            finally {
                                                getMyProcessor().getLocks().getCacheMutex()[dir.getNumberOfCacheWithModifiedBlock(blockNumber%16)].release();
                                            }
                                        }
                                        else{//If you did not get the cache lock release everything and restart
                                            return cyclesWaitingInThisInstruction;
                                        }
                                    }
                                    else{//Uncached or shared
                                        //Set state of block in directory to shared and add my cache to the ones that have it
                                        dir.changeState(blockNumber%16, 'C');

                                        dir.changeInformation(blockNumber%16, coreID, true);

                                    }
                                    //Cycles symbolize access to memory to fetch the block
                                    if(coreID < 2) {
                                        cyclesWaitingInThisInstruction += (blockNumber <= 15) ? 16 : 40;
                                    }
                                    else {
                                        cyclesWaitingInThisInstruction += (blockNumber > 15) ? 16 : 40;
                                    }
                                    //Fetch the block from memory
                                    Block target = mem.getBlock(blockNumber%16);
                                    //Update our cache with block from memory
                                    dataCache.loadBlock(blockIndex, target);
                                    dataCache.setIndexStatus(blockIndex, C);
                                    //Load word to register from cache
                                    context[instruction.getSecondParameter()] = dataCache.getWord(blockIndex, wordIndex);
                                    instructionSucceeded=true;
                                    return cyclesWaitingInThisInstruction;
                                }
                                finally {
                                    getMyProcessor().getLocks().getBus()[ ( blockNumber <= 15 ) ? 0 : 1 ].release();
                                }
                            }
                            else{//If you did not get the memory lock lock release everything and restart the instruction
                                return cyclesWaitingInThisInstruction;
                            }
                        }
                        finally {
                            getMyProcessor().getLocks().getDirectoryMutex()[ ( blockNumber <= 15 ) ? 0 : 1 ].release();
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

        int actualPC = this.context[32];
        int initialInexThread = assignedSystemThread.getInitIndexInMemory();

        int instructionlocationInMemory = initialInexThread + actualPC;

        Instruction nextInstruction = this.instructionCache.getInstruction(instructionlocationInMemory);
        return nextInstruction;
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        ALU alu = new ALU(this.context);
        boolean slowExecution=this.getMyProcessor().isInSlowExecution();

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
                            this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
                            this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);
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

                                this.getMyProcessor().getClock().increaseCurrentTime();//move on the clock when the third hilillo arrive
                                //release mutex
                                if(slowExecution){
                                    System.out.println("Ejecutando el ciclo número "+this.getMyProcessor().getClock().getCurrentTime());
                                    sc.next();
                                }
                                this.getMyProcessor().getLocks().getBarrierCycleClock().release(this.getMyProcessor().getLocks().getNumCoresWaiting());
                                this.getMyProcessor().getLocks().setNumCoresWaiting(0);
                                this.getMyProcessor().getLocks().getMutexBarrier().release();
                            } else {

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
        System.out.println("TERMINO EL CORE "+this.getCoreID() +"y tardó el último hilillo"+this.assignedSystemThread.getNumCyclesInExecution() +"Del procesador "+getMyProcessor().getProcessorId());
        System.out.println();
    }
}
