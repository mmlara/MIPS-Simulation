/**
 *  Representation memory block
 *
 * @author Gómez Brayan, Lara Milton, Quirós Esteban
 * @version 1.0
 * @since 25/11/2017
 */

package blockinglogicalcomponents;

import java.util.concurrent.Semaphore;

/**
 * Class Locks
 */
public class Locks {

    private int numCores;

    /**
     * @implNote All declared boolean arrays represent the state of a component.
     * False represent a busy component
     * True represent a free component
     */

    private Semaphore[] cacheMutex;
    private Semaphore[] instructionCacheMutex;
    private boolean[] cacheState;
    private int numCaches;

    private Semaphore[] directoryMutex;
    private boolean[] directoryState;
    private int numDirectories;

    private Semaphore[] bus;
    private Semaphore[] busInstructions;
    private boolean[] busState;
    private int numBuses;

    private Semaphore mutexBarrier;
    private Semaphore barrierCycleClock;
    private int numCoresWaiting;
    private Semaphore finishedCoresMutex;
    private int finishedCores;
    private boolean allThreadsFinished;
    private Semaphore queueMutex;

    /**
     * Constructor method
     *
     * @param numCores       number of cores in the simulation
     * @param numCaches      number of caches in the simulation
     * @param numDirectories number of directories in the simulation
     * @param numBuses       number of buses in the simulation
     */
    public Locks(int numCores, int numCaches, int numDirectories, int numBuses) {

        //Set local variables with received arguments
        this.numCores = numCores;
        this.numCaches = numCaches;
        this.numDirectories = numDirectories;
        this.numBuses = numBuses;
        this.numCoresWaiting = 0;
        finishedCores = 0;
        allThreadsFinished = false;

        //Call a local method to create all locks
        this.createLocks();
    }

    /**
     * Call the locks builders to create them
     */
    private void createLocks(){
        this.createCacheLocks();
        this.createDirectoriesLocks();
        this.createBusLocks();
        this.createBarrierLocks();
    }

    /**
     *  Create locks to control simultaneous access to the cache
     */
    private void createCacheLocks() {
        cacheMutex = new Semaphore[numCaches];
        cacheState = new boolean[numCaches];
        for (int i = 0; i < cacheMutex.length; i++) {
            cacheMutex[i] = new Semaphore(1);
            instructionCacheMutex[i] = new Semaphore(1);
            cacheState[i] = false;
        }

    }

    /**
     * Create locks to control simultaneous access to the directories
     */
    private void createDirectoriesLocks() {
        directoryMutex = new Semaphore[numDirectories];
        directoryState = new boolean[numDirectories];
        for (int i = 0; i < directoryMutex.length; i++) {
            directoryMutex[i] = new Semaphore(1);
            directoryState[i] = false;
        }

    }

    /**
     * Create locks to control simultaneous access to the main memory through buses
     */
    private void createBusLocks() {
        bus = new Semaphore[numBuses];
        busInstructions = new Semaphore[numBuses];
        busState = new boolean[numBuses];
        for (int i = 0; i < bus.length; i++) {
            bus[i] = new Semaphore(1);
            busInstructions[i] = new Semaphore(1);
            busState[i] = false;
        }
    }

    /**
     * Create locks to control simultaneous access to the barriers
     */
    private void createBarrierLocks() {
        mutexBarrier = new Semaphore(1);
        barrierCycleClock = new Semaphore(0);
        finishedCoresMutex = new Semaphore(1);
        queueMutex = new Semaphore(1);
    }

    /**
     * Get the number of cores
     * @return number of cores
     */
    public int getNumCores() {
        return numCores;
    }

    /**
     * Get cache locks, implemented as Java Semaphore
     * @return An array with all cache locks
     */
    public Semaphore[] getCacheMutex() {
        return cacheMutex;
    }

    /**
     * Get instruction cache locks, implemented as Java Semaphore
     * @return An array with all instruction cache locks
     */
    public Semaphore[] getInstructionCacheMutex() {
        return instructionCacheMutex;
    }

    /**
     * Get the state of each cache
     * @return cache state as a boolean array
     */
    public boolean[] getCacheState() {
        return cacheState;
    }

    /**
     * Get the number of caches
     * @return number of caches
     */
    public int getNumCaches() {
        return numCaches;
    }

    /**
     * Get directories locks, implemented as Java Semaphore
     * @return An array with all directory locks.
     */
    public Semaphore[] getDirectoryMutex() {
        return directoryMutex;
    }

    /**
     * Get the states of each directory
     * @return An array with all directory states.
     */
    public boolean[] getDirectoryState() {
        return directoryState;
    }

    /**
     * Get the number of directories
     * @return Number of directories as an integer value
     */
    public int getNumDirectories() {
        return numDirectories;
    }

    /**
     * Get bus locks
     * @return An array of bus locks.
     */
    public Semaphore[] getBus() {
        return bus;
    }

    /**
     * Get the state of each bus
     * @return An array with all bus states
     */
    public boolean[] getBusState() {
        return busState;
    }

    /**
     * Get the number of buses
     * @return Number of buses
     */
    public int getNumBuses() {
        return numBuses;
    }

    /**
     * Get the barrier lock
     * @return Barrier lock
     */
    public Semaphore getMutexBarrier() {
        return mutexBarrier;
    }

    /**
     * @return barrier cycle clock lock
     */
    public Semaphore getBarrierCycleClock() {
        return barrierCycleClock;
    }

    /**
     * Get the total of cores waiting
     * @return number of cores waiting
     */
    public int getNumCoresWaiting() {
        return numCoresWaiting;
    }

    /**
     *  Set number of cores waiting
     * @param numCoresWaiting number of cores waiting
     */
    public void setNumCoresWaiting(int numCoresWaiting) {
        this.numCoresWaiting = numCoresWaiting;
    }

    /**
     * Get the lock of the finished cores
     * @return finished cores locks
     */
    public Semaphore getFinishedCoresMutex() {
        return finishedCoresMutex;
    }

    /**
     * Get number of finished cores
     * @return number of cores that have finished
     */
    public int getFinishedCores() {
        return finishedCores;
    }

    /**
     *  Set the number of finished cores
     * @param finishedCores number of cores that have finished
     */
    public void setFinishedCores(int finishedCores) {
        this.finishedCores = finishedCores;
    }

    /**
     *  Get threads state
     * @return true if all threads are finished, false if are not finished yet
     */
    public boolean isAllThreadsFinished() {
        return allThreadsFinished;
    }

    /**
     * Set threads state
     * @param allThreadsFinished true if all threads are finished, false if are not finished yet
     */
    public void setAllThreadsFinished(boolean allThreadsFinished) {
        this.allThreadsFinished = allThreadsFinished;
    }

    /**
     * Set number of cores
     * @param numCores number of cores
     */
    public void setNumCores(int numCores) {
        this.numCores = numCores;
    }

    /**
     * Get the queue mutex
     * @return queue mutex
     */
    public Semaphore getQueueMutex() {
        return queueMutex;
    }

    /**
     *  Get instruction memory lock
     * @return instruction memory locks
     */
    public Semaphore[] getBusInstructions() {
        return busInstructions;
    }
}
