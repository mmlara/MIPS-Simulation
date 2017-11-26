package physicalcomponentssimulation.core;

import physicalcomponentssimulation.cache.Block;
import physicalcomponentssimulation.cache.DataCache;
import physicalcomponentssimulation.cache.InstructionCache;
import physicalcomponentssimulation.directory.Directory;
import physicalcomponentssimulation.memory.Memory;
import physicalcomponentssimulation.processor.Processor;
import physicalcomponentssimulation.processorsparts.ALU;
import physicalcomponentssimulation.processorsparts.Instruction;
import physicalcomponentssimulation.systemthread.SystemThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import static java.lang.System.exit;

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
    private boolean slowExecution;
    private Scanner scanner;
    private List<SystemThread> finishedThreads;


    final private int I = 0;
    final private int C = 1;
    final private int M = 2;


    public Core(Queue<SystemThread> assignedSystemThreads) {
        finishedThreads= new ArrayList<>();
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

    public List<SystemThread> getFinishedThreads() {
        return finishedThreads;
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


    public Boolean evictVictim(int blockNumber, int blockIndex) {

        int currentTag = dataCache.getTagOfBlock(blockIndex);
        Directory dir = getDirectoryOfBlockByBlockNumber(currentTag);
        int cyclesWaitingInThisInstruction;

        if (dataCache.getStatusBlock(blockIndex) == C) {

            //Change state of block in cache to invalid
            dataCache.setIndexStatus(blockIndex, I);

            //Update directory
            dir.changeInformation(currentTag, getCacheNumber(), false);

            if (dir.countOfCachesThatContainBlock(dataCache.getTagOfBlock(blockIndex)) == 0)
                dir.changeState(currentTag, 'U');

        } else {//It is modified
            if (getMyProcessor().getLocks().getBus()[(currentTag <= 15) ? 0 : 1].tryAcquire()) {
                try {
                    Memory memoryOfBlock;

                    if (currentTag <= 15)
                        memoryOfBlock = getProcessor(0).getMemory();
                    else
                        memoryOfBlock = getProcessor(1).getMemory();

                    cyclesWaitingInThisInstruction = (isLocalMemory(currentTag) ? 16 : 40);
                    updateBarrierCycle(cyclesWaitingInThisInstruction);
                    this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
                    this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);

                    //Change block state in cache to invalid
                    dataCache.setIndexStatus(blockIndex, I);

                    //Update directory
                    dir.changeInformation(currentTag, getCacheNumber(), false);
                    dir.changeState(currentTag, 'U');

                    //Write modified block to memory
                    memoryOfBlock.setBlock(currentTag, dataCache.getBlockAtIndex(blockIndex));
                } finally {
                    getMyProcessor().getLocks().getBus()[(currentTag <= 15) ? 0 : 1].release();
                }

            } else {//If you did not get the memory lock release everything and restart
                return false;
            }

        }

        //handleSharedBlock(dir, dataCache.getTagOfBlock(blockIndex), blockIndex);
        //handleModifiedBlock(dir, dataCache.getTagOfBlock(blockIndex), blockIndex);
        return true;
    }

    /**
     * This method execute the load instruction:  Load into cache the requested block
     *
     * @param instruction: Current instruction to execute
     */

    public void executeLoadInstruction(Instruction instruction) {
//        try {
            int blockNumber = (instruction.getThirdParameter() + context[instruction.getFirsParameter()]) / 16;
            int blockIndex = blockNumber % 4;
            int cyclesWaitingInThisInstruction;

            //Got the cache lock
            if (this.getMyProcessor().getLocks().getCacheMutex()[getCacheNumber()].tryAcquire()) {
                try {//TODO hacer barrier cada vez que se obtiene
                    cyclesWaitingInThisInstruction = 1;
                    updateBarrierCycle(cyclesWaitingInThisInstruction);
                    this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
                    this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);
                    int tag = dataCache.getTagOfBlock(blockIndex);

                    //Check if data is in cache
                    if (tag == blockNumber && dataCache.getStatusBlock(blockIndex) != 'I') {
                        int wordIndex = ((instruction.getThirdParameter() + context[instruction.getFirsParameter()]) % 16) / 4;
                        context[instruction.getSecondParameter()] = dataCache.getWord(blockIndex, wordIndex);
                        instructionSucceeded = true;
                        return;

                    } else {//If it is not then select victim
                        int state = dataCache.getStatusBlock(blockIndex);
                        //Check victims state
                        if (state != I) {
                            int victimDirectory = (dataCache.getTagOfBlock(blockIndex) <= 15) ? 0 : 1;
                            if (getMyProcessor().getLocks().getDirectoryMutex()[victimDirectory].tryAcquire()) {
                                try {

                                    if (!evictVictim(blockNumber, blockIndex)) {
                                        return;
                                    }

                                } finally {
                                    getMyProcessor().getLocks().getDirectoryMutex()[victimDirectory].release();
                                }
                            } else {//If you did not get the directory lock release everything and restart the instruction
                                return;
                            }
                        }

                        int wordIndex = ((instruction.getThirdParameter() + context[instruction.getFirsParameter()]) % 16) / 4;
                        //Victim is evicted. Now fetch the block and load it to cache
                        if (getMyProcessor().getLocks().getDirectoryMutex()[(blockNumber <= 15) ? 0 : 1].tryAcquire()) {
                            try {

                                Directory dir = getDirectoryOfBlockByBlockNumber(blockNumber);

                                if (getMyProcessor().getLocks().getBus()[(blockNumber <= 15) ? 0 : 1].tryAcquire()) {
                                    try {
                                        Memory mem;
                                        if (getMyProcessor().getProcessorId() == 0) {
                                            cyclesWaitingInThisInstruction = (blockNumber <= 15) ? 16 : 40;
                                            updateBarrierCycle(cyclesWaitingInThisInstruction);
                                            this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
                                            this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);
                                            mem = (blockNumber <= 15) ? getMyProcessor().getMemory() : getMyProcessor().getNeigborProcessor().getMemory();
                                        } else {
                                            cyclesWaitingInThisInstruction = (blockNumber > 15) ? 16 : 40;
                                            updateBarrierCycle(cyclesWaitingInThisInstruction);
                                            this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
                                            this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);
                                            mem = (blockNumber > 15) ? getMyProcessor().getMemory() : getMyProcessor().getNeigborProcessor().getMemory();
                                        }
                                        if (dir.getStateOfBlock(blockNumber) == 'M') {

                                            int cacheWithModifiedBlock = dir.getNumberOfCacheWithModifiedBlock(blockNumber, getCacheNumber());
                                            if (getMyProcessor().getLocks().getCacheMutex()[cacheWithModifiedBlock].tryAcquire()) {

                                                try {
                                                    //TODO add proper delay for accessing cache?
                                                    //Get modified block from cache and write to memory
                                                    int numberOfCache = dir.getNumberOfCacheWithModifiedBlock(blockNumber, getCacheNumber());
                                                    DataCache cache;
                                                    if (numberOfCache < 2) {//If block is in caches of first processor
                                                        //If I am on the first processor get that data cache of my processor else get that one of my neighbor processor
                                                        cache = getProcessor(0).getCores()[numberOfCache].getDataCache();
                                                    } else {//If block is in cache of the second processor
                                                        //If I am on the first processor get that data cache of my processor else get that one of my neighbor processor
                                                        cache = getProcessor(1).getCores()[0].getDataCache();
                                                    }
                                                    Block target = cache.getBlockAtIndex(blockIndex);
                                                    mem.setBlock(blockNumber, target);
                                                    //Update house directory of target block and cache where it was
                                                    //Set cache block to shared
                                                    cache.setIndexStatus(blockIndex, C);
                                                    //Set state of block in directory to shared and add my cache to the ones that have it
                                                    dir.changeState(blockNumber, 'C');
                                                    dir.changeInformation(blockNumber, getCacheNumber(), true);
                                                } finally {
                                                    getMyProcessor().getLocks().getCacheMutex()[cacheWithModifiedBlock].release();
                                                }
                                            } else {//If you did not get the cache lock release everything and restart
                                                return;
                                            }
                                        } else {//Uncached or shared
                                            //Set state of block in directory to shared and add my cache to the ones that have it
                                            dir.changeState(blockNumber, 'C');

                                            dir.changeInformation(blockNumber, getCacheNumber(), true);

                                        }
                                        //Cycles symbolize access to memory to fetch the block
                                        if (getMyProcessor().getProcessorId() == 0) {
                                            cyclesWaitingInThisInstruction = (blockNumber <= 15) ? 16 : 40;
                                            updateBarrierCycle(cyclesWaitingInThisInstruction);
                                            this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
                                            this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);

                                        } else {
                                            cyclesWaitingInThisInstruction = (blockNumber > 15) ? 16 : 40;
                                            updateBarrierCycle(cyclesWaitingInThisInstruction);
                                            this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
                                            this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);
                                        }
                                        //Fetch the block from memory
                                        Block target = mem.getBlock(blockNumber);
                                        //Update our cache with block from memory
                                        dataCache.loadBlock(blockIndex, blockNumber, target);
                                        dataCache.setIndexStatus(blockIndex, C);
                                        //Load word to register from cache
                                        context[instruction.getSecondParameter()] = dataCache.getWord(blockIndex, wordIndex);
                                        instructionSucceeded = true;
                                        return;
                                    } finally {
                                        getMyProcessor().getLocks().getBus()[(blockNumber <= 15) ? 0 : 1].release();
                                    }
                                } else {//If you did not get the memory lock lock release everything and restart the instruction
                                    return;
                                }
                            } finally {
                                getMyProcessor().getLocks().getDirectoryMutex()[(blockNumber <= 15) ? 0 : 1].release();
                            }
                        } else {//If you did not get the house directory lock release everything and restart the instruction
                            return;
                        }
                    }
                } finally {
                    this.getMyProcessor().getLocks().getCacheMutex()[getCacheNumber()].release();
                }
            }
            //Did not get the cache lock
            else {
                cyclesWaitingInThisInstruction = 1;
                updateBarrierCycle(cyclesWaitingInThisInstruction);
                this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
                this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);
                return;
            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println(instruction.toString());
//            exit(1);
//            return;
//        }
    }

    /**
     * @param instruction
     * @return
     */
    public void executeStoreInstruction(Instruction instruction) {
        //       try {
        int blockNumber = (instruction.getThirdParameter() + context[instruction.getFirsParameter()]) / 16;
        int blockIndex = blockNumber % 4;
        int cyclesWaitingInThisInstruction;

        //Got the cache lock
        if (this.getMyProcessor().getLocks().getCacheMutex()[getCacheNumber()].tryAcquire()) {
            try {
                cyclesWaitingInThisInstruction = 1;
                updateBarrierCycle(cyclesWaitingInThisInstruction);
                this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
                this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);
                int tag = dataCache.getTagOfBlock(blockIndex);

                //Check if data is in cache
                if (tag == blockNumber) {
                    int statusBlock = dataCache.getStatusBlock(blockIndex);
                    //Check the block state on data cache

                    if (statusBlock != M) {

                        int directoryID = (blockNumber <= 15) ? 0 : 1;
                        if (getMyProcessor().getLocks().getDirectoryMutex()[directoryID].tryAcquire()) {
                            try {

                                Directory dir = getDirectoryOfBlockByBlockNumber(blockNumber);

                                if (dir.countOfCachesThatContainBlock(blockNumber) > 1) {

                                    if (!handleSharedBlock(dir, blockNumber, blockIndex))
                                        return;

                                    if (statusBlock == I) {

                                        if (!handleModifiedBlock(dir, blockNumber, blockIndex))
                                            return;

                                        if (!loadBlockIntoCache(blockNumber, blockIndex, true, dir))
                                            return;
                                    }
                                }

                                dir.changeInformation(blockNumber, getCacheNumber(), true);
                                dir.changeState(blockNumber, 'M');
                                dir.changeToModifiedBlock(blockNumber,getCacheNumber());

                            } finally {
                                getMyProcessor().getLocks().getDirectoryMutex()[directoryID].release();

                            }
                        } else {
                            return;
                        }
                    }
                    int numWord = (((instruction.getThirdParameter() + context[instruction.getFirsParameter()]) % 16) / 4);
                    dataCache.setWord(blockIndex, numWord, context[instruction.getSecondParameter()]);
                    dataCache.setIndexStatus(blockIndex, M);

                    instructionSucceeded = true;

                } else {
                    int directoryID = (blockNumber <= 15) ? 0 : 1;
                    if (getMyProcessor().getLocks().getDirectoryMutex()[directoryID].tryAcquire()) {
                        try {
                            Directory dir = getDirectoryOfBlockByBlockNumber(blockNumber);

                            if (!loadBlockIntoCache(blockNumber, blockIndex, false, dir))
                                return;

                            if (!handleSharedBlock(dir, blockNumber, blockIndex))
                                return;


                            if (!handleModifiedBlock(dir, blockNumber, blockIndex))
                                return;


                            int numWord = (((instruction.getThirdParameter() + context[instruction.getFirsParameter()]) % 16) / 4);
                            dataCache.setWord(blockIndex, numWord, context[instruction.getSecondParameter()]);
                            dataCache.setIndexStatus(blockIndex, M);
                            dir.changeToModifiedBlock(blockNumber,getCacheNumber());
                            dir.changeState(blockNumber, 'M');

                            instructionSucceeded = true;

                        } finally {
                            getMyProcessor().getLocks().getDirectoryMutex()[directoryID].release();
                        }
                    }
                }

            } finally {
                this.getMyProcessor().getLocks().getCacheMutex()[getCacheNumber()].release();
            }
        }
        return;
//        } catch (Exception e) {
//            System.out.println(e.getCause());
//            return;
//        }
    }

    public boolean handleSharedBlock(Directory dir, int blockNumber, int blockIndex) {
        List<Integer> idCacheSharedBlock = dir.getCachesIdThatShareSomeBlock(coreID, getCacheNumber());
        int cyclesWaitingInThisInstruction = (isLocalMemory(blockNumber)) ? 1 : 5;
        updateBarrierCycle(cyclesWaitingInThisInstruction);
        this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
        this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);
        for (Integer cacheWithSharedBlock : idCacheSharedBlock) {

            Processor processorCache = (cacheWithSharedBlock < 2) ? getProcessor(0) : getProcessor(1);
            if (processorCache.getLocks().getCacheMutex()[cacheWithSharedBlock].tryAcquire()) {
                try {
                    processorCache.getCores()[cacheWithSharedBlock % 2].getDataCache().setIndexStatus(blockIndex, I);
                    dir.changeInformation(blockNumber, cacheWithSharedBlock, false);
                } finally {
                    processorCache.getLocks().getCacheMutex()[cacheWithSharedBlock].release();
                }
            } else {
                return false;
            }
        }
        if (dir.countOfCachesThatContainBlock(blockNumber) == 0)
            dir.changeState(blockNumber, 'U');

        cyclesWaitingInThisInstruction = (isLocalMemory(blockNumber)) ? 1 : 5;
        updateBarrierCycle(cyclesWaitingInThisInstruction);
        this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
        this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);

        return true;
    }

    public boolean loadBlockIntoCache(int blockNumber, int blockIndex, boolean isLoaded, Directory dir) {
        int idMemoryOfBlock = (blockNumber < 16) ? 0 : 1;

        if (!isLoaded) {

            boolean isEmptyBlock = (dataCache.getTagOfBlock(blockIndex) == -1);

            if (!isEmptyBlock) {

                boolean blocksInSameDirectory = (blockNumber <= 15 && dataCache.getTagOfBlock(blockIndex) <= 15) || (blockNumber > 15 && dataCache.getTagOfBlock(blockIndex) > 15);

                if (blocksInSameDirectory) {

                    if (!evictVictim(blockNumber, blockIndex))
                        return false;

                } else {

                    int victimBlock = dataCache.getTagOfBlock(blockIndex);
                    if (getMyProcessor().getLocks().getDirectoryMutex()[(victimBlock <= 15) ? 0 : 1].tryAcquire()) {
                        try {
                            if (!evictVictim(blockNumber, blockIndex))
                                return false;

                        } finally {
                            getMyProcessor().getLocks().getDirectoryMutex()[(victimBlock <= 15) ? 0 : 1].release();
                        }
                    } else {
                        return false;
                    }
                }
            }
        }

        if (getMyProcessor().getLocks().getBus()[idMemoryOfBlock].tryAcquire()) {
            try {
                Memory memoryOfBlock = getProcessor(idMemoryOfBlock).getMemory();
                dataCache.loadBlock(blockIndex, blockNumber, memoryOfBlock.getBlock(blockNumber));
                int cyclesWaitingInThisInstruction = (isLocalMemory(blockNumber)) ? 16 : 40;
                updateBarrierCycle(cyclesWaitingInThisInstruction);
                this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
                this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);
                dir.changeInformation(blockNumber, getCacheNumber(), true);
                dir.changeState(blockNumber, 'M');
                dir.changeToModifiedBlock(blockNumber,getCacheNumber());


            } finally {
                getMyProcessor().getLocks().getBus()[idMemoryOfBlock].release();
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean handleModifiedBlock(Directory dir, int blockNumber, int blockIndex) {
        int cacheWithModifiedBlock = dir.getNumberOfCacheWithModifiedBlock(blockNumber, getCacheNumber());

        if (cacheWithModifiedBlock >= 0) {
            Processor processorCache = (cacheWithModifiedBlock < 2) ? getProcessor(0) : getProcessor(1);

            if (processorCache.getLocks().getCacheMutex()[cacheWithModifiedBlock].tryAcquire()) {
                try {
                    int idMemoryOfBlock = (blockNumber < 16) ? 0 : 1;
                    if (getMyProcessor().getLocks().getBus()[idMemoryOfBlock].tryAcquire()) {
                        try {
                            Memory memoryOfBlock = getProcessor(idMemoryOfBlock).getMemory();
                            memoryOfBlock.setBlock(blockNumber, processorCache.getCores()[cacheWithModifiedBlock % 2].getDataCache().getBlockAtIndex(blockIndex));
                            int cyclesWaitingInThisInstruction = isLocalMemory(blockNumber) ? 16 : 40;
                            updateBarrierCycle(cyclesWaitingInThisInstruction);
                            this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
                            this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);

                        } finally {
                            getMyProcessor().getLocks().getBus()[idMemoryOfBlock].release();
                        }
                    } else
                        return false;

                    processorCache.getCores()[cacheWithModifiedBlock % 2].getDataCache().setIndexStatus(blockIndex, I);
                    dir.changeInformation(blockNumber, cacheWithModifiedBlock, false);
                    dir.changeState(blockNumber, 'U');
                    int cyclesWaitingInThisInstruction = (isLocalMemory(blockNumber)) ? 1 : 5;
                    updateBarrierCycle(cyclesWaitingInThisInstruction);
                    this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
                    this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);
                } finally {
                    processorCache.getLocks().getCacheMutex()[cacheWithModifiedBlock].release();
                }
            } else {
                return false;
            }
        }
        return true;
    }


    public Directory getDirectoryOfBlockByIndex(int blockIndex) {
        Directory dir;
        int cyclesWaitingInThisInstruction;
        if (myProcessor.getProcessorId() == 0) {
            cyclesWaitingInThisInstruction = (dataCache.getTagOfBlock(blockIndex) <= 15) ? 1 : 5;
            updateBarrierCycle(cyclesWaitingInThisInstruction);
            this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
            this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);
            dir = (dataCache.getTagOfBlock(blockIndex) <= 15) ? getMyProcessor().getDirectory() : getMyProcessor().getNeigborProcessor().getDirectory();
        } else {
            cyclesWaitingInThisInstruction = (dataCache.getTagOfBlock(blockIndex) > 15) ? 1 : 5;
            updateBarrierCycle(cyclesWaitingInThisInstruction);
            this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
            this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);
            dir = (dataCache.getTagOfBlock(blockIndex) > 15) ? getMyProcessor().getDirectory() : getMyProcessor().getNeigborProcessor().getDirectory();
        }
        return dir;
    }

    public Directory getDirectoryOfBlockByBlockNumber(int blockNumber) {
        Directory dir;
        int cyclesWaitingInThisInstruction;
        if (myProcessor.getProcessorId() == 0) {
            cyclesWaitingInThisInstruction = (blockNumber <= 15) ? 1 : 5;
            updateBarrierCycle(cyclesWaitingInThisInstruction);
            this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
            this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);
            dir = (blockNumber <= 15) ? getMyProcessor().getDirectory() : getMyProcessor().getNeigborProcessor().getDirectory();
        } else {
            cyclesWaitingInThisInstruction = (blockNumber > 15) ? 1 : 5;
            updateBarrierCycle(cyclesWaitingInThisInstruction);
            this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
            this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);
            dir = (blockNumber > 15) ? getMyProcessor().getDirectory() : getMyProcessor().getNeigborProcessor().getDirectory();
        }
        return dir;
    }

    public Processor getProcessor(int id) {
        return (this.getMyProcessor().getProcessorId() == id) ? getMyProcessor() : getMyProcessor().getNeigborProcessor();
    }

    public boolean isLocalMemory(int blockNumber) {
        if (getMyProcessor().getProcessorId() == 0) {
            if (blockNumber < 16)
                return true;
            else
                return false;
        } else if (blockNumber > 15)
            return true;
        else
            return false;
    }

    private int getCacheNumber() {
        if (getMyProcessor().getProcessorId() == 0)
            return coreID;
        else
            return 2;
    }

//TODO cuando se tenga acceso al reloj, preguntar que si es -1 el valor de hilillo.initialClock, en caso de ser así, asignarle el reloj actual.

    public void setAsignedSystemThread(SystemThread systemThread) {
        this.assignedSystemThread = systemThread;
    }

    public Instruction getNextInstruction() {

        int actualPC = this.context[32];
        Instruction pairInstruction = this.instructionCache.getInstruction(actualPC);
        return pairInstruction;
    }

    public Instruction tryLoadNextInstruction() {
        if (getMyProcessor().getLocks().getBusInstructions()[getMyProcessor().getProcessorId()].tryAcquire()) {
            try {
                int actualPC = this.context[32];
                int initialIndexThread = assignedSystemThread.getInitIndexInMemory();
                int indexInMemory = initialIndexThread + actualPC;
                int blockTag = indexInMemory / 4;
                int indexInCache = blockTag % 4;
                if (blockTag < 16) {
                    updateBarrierCycle(16);
                    return this.instructionCache.getInstruction(actualPC);
                } else {
                    updateBarrierCycle(16);
                    return this.instructionCache.getInstruction(actualPC);
                }

            } finally {
                getMyProcessor().getLocks().getBusInstructions()[getMyProcessor().getProcessorId()].release();
            }
        }
        return null;
    }

    @Override
    public void run() {
        scanner = new Scanner(System.in);
        ALU alu = new ALU(this.context);
        slowExecution = this.getMyProcessor().isInSlowExecution();

        //If there are still "hilillos" in the queue then keep working
        int cycleNumber = 0;
        boolean stillAlive = true;
        try {
            while (stillAlive) {
                this.myProcessor.getLocks().getQueueMutex().acquire();
                if (this.myProcessor.getAssignedSystemThreads().isEmpty()) {
                    stillAlive = false;
                    this.getMyProcessor().getLocks().getMutexBarrier().acquire();//get mutex
                    if (this.getMyProcessor().getLocks().getNumCoresWaiting() == this.getMyProcessor().getLocks().getNumCores() - 1) { //the last core in execution
                        this.getMyProcessor().getClock().increaseCurrentTime();//move on the clock when the third hilillo arrive
                        //release mutex
                        this.getMyProcessor().getLocks().setNumCores(this.getMyProcessor().getLocks().getNumCores() - 1);
                        this.getMyProcessor().getLocks().getBarrierCycleClock().release(this.getMyProcessor().getLocks().getNumCoresWaiting());

                        this.getMyProcessor().getLocks().setNumCoresWaiting(0);
                        this.getMyProcessor().getLocks().getMutexBarrier().release();
                        this.myProcessor.getLocks().getQueueMutex().release();
                    } else {
                        System.out.println("falso");
                        this.getMyProcessor().getLocks().setNumCores(this.getMyProcessor().getLocks().getNumCores() - 1);
                        //this.getMyProcessor().getLocks().setNumCoresWaiting(this.getMyProcessor().getLocks().getNumCoresWaiting() + 1);
                        this.getMyProcessor().getLocks().getMutexBarrier().release(); //release mutex
                        this.myProcessor.getLocks().getQueueMutex().release();
                    }

                } else {
                    this.assignedSystemThread = assignedSystemThreads.poll();
                    this.myProcessor.getLocks().getQueueMutex().release();


                    //Get "hilillo", load its context and fetch the first instruction

                    if (assignedSystemThread.getInitialClock() == -1) {
                        assignedSystemThread.setIdProcessorAsigned(this.myProcessor.getProcessorId());
                        assignedSystemThread.setInitialClock(this.myProcessor.getClock().getCurrentTime());//
                    }
                    this.assignedSystemThread.setCurrentCyclesInProcessor(0);//init execution
                    Boolean systemThreadFinished = false;
                    this.loadContext();
                    Instruction instruction = tryLoadNextInstruction();
                    while(instruction == null){
                        instruction = tryLoadNextInstruction();
                        updateBarrierCycle(1);
                    }

                    //If the quantum has not finished or the instruction has not succeeded then keep executing instructions.
                    //If the "hilillo" is done then stop working.
                    while (((assignedSystemThread.getCurrentCyclesInProcessor() < this.getMyProcessor().getQuantumSize()) || !instructionSucceeded) && !systemThreadFinished) {
                        //System.out.println("Número de ciclo " + cycleNumber + " del hilillo " + assignedSystemThread.getIdHilillo() + " estaba ejecutando " + instruction.getOperationCode());
                        cycleNumber++;
                        int cyclesWaitingInThisInstruction = 0;
                        //If the instruction finished and the quantum has not then fetch another instruction
                        if (instructionSucceeded) {
                            instruction = tryLoadNextInstruction();
                            while(instruction == null){
                                instruction = tryLoadNextInstruction();
                                updateBarrierCycle(1);
                            }
                            instructionSucceeded = false;
                        }

                        //Load
                        if (instruction.getOperationCode() == 35) {
                            //Load Implementation
                            executeLoadInstruction(instruction);
                            if (instructionSucceeded)
                                this.context[32] += 4;
                            else {
                                this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + 1);//suma un ciclo en procesador;
                                this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + 1);
                                cyclesWaitingInThisInstruction = 1;
                            }
                            //cyclesWaitingInThisInstruction; poner acá lo que acumulemde ciclos tratando de ejecutar esta instrucción
                        }
                        //Store
                        else if (instruction.getOperationCode() == 43) {
                            //Store Implementation
                            executeStoreInstruction(instruction);
                            if (instructionSucceeded)
                                this.context[32] += 4;
                            else{
                                this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + 1);//suma un ciclo en procesador;
                                this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + 1);
                                cyclesWaitingInThisInstruction = 1;
                            }
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
                        //System.out.println("ejecuta la instrucción hilillo " + this.assignedSystemThread.getIdHilillo() + " en el core " + getCoreID() + " del procesador " + getMyProcessor().getProcessorId());

                        updateBarrierCycle(cyclesWaitingInThisInstruction);
                        this.assignedSystemThread.setCurrentCyclesInProcessor(this.assignedSystemThread.getCurrentCyclesInProcessor() + cyclesWaitingInThisInstruction);//suma un ciclo en procesador;
                        this.assignedSystemThread.setNumCyclesInExecution(this.assignedSystemThread.getNumCyclesInExecution() + cyclesWaitingInThisInstruction);

                        //System.out.println("Se desbloquea el hilillo " + assignedSystemThread.getIdHilillo());
                    }//while end of quantum or end of thread

                    if (!systemThreadFinished) {
                        this.assignedSystemThreads.add(assignedSystemThread);
                        this.saveContext();
                    } else {// spend the quantum in processor
                        this.saveContext();
                        finishedThreads.add(assignedSystemThread);
                        this.assignedSystemThread.setLastClock(this.myProcessor.getClock().getCurrentTime());
                    }
                }
            }//end while still alive
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("*************TERMINO DE EJECUTAR EL CORE " + this.getCoreID() + "*************");
        System.out.println("Información de sus hilillos: ");
        for (int i = 0; i < this.finishedThreads.size(); i++) {
            System.out.println(this.finishedThreads.get(i));

        }
    }

    private void updateBarrierCycle(int cyclesToWait) {
        for (int i = 0; i < cyclesToWait; i++) {
            try {
                this.getMyProcessor().getLocks().getMutexBarrier().acquire();//get mutex
                if (this.getMyProcessor().getLocks().getNumCoresWaiting() == this.getMyProcessor().getLocks().getNumCores() - 1) { //the last core in execution

                    this.getMyProcessor().getClock().increaseCurrentTime();//move on the clock when the third hilillo arrive
                    //release mutex
                    if (slowExecution) {
                        System.out.println("**Ejecutando el ciclo número \n Presione una tecla para continuar" + this.getMyProcessor().getClock().getCurrentTime());
                        scanner.next();
                    }
                    this.getMyProcessor().getLocks().getBarrierCycleClock().release(this.getMyProcessor().getLocks().getNumCoresWaiting());
                    this.getMyProcessor().getLocks().setNumCoresWaiting(0);
                    this.getMyProcessor().getLocks().getMutexBarrier().release();
                } else {

                    this.getMyProcessor().getLocks().setNumCoresWaiting(this.getMyProcessor().getLocks().getNumCoresWaiting() + 1);
                    this.getMyProcessor().getLocks().getMutexBarrier().release(); //release mutex
                    this.getMyProcessor().getLocks().getBarrierCycleClock().acquire();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }
}

