package blockinglogicalcomponents;

import java.util.concurrent.Semaphore;

public class Locks {

    private int numCores;

    private  Semaphore[] cacheMutex;
    private boolean[] cacheState;//false represent a busy cache
    private int numCaches;

    private Semaphore[] directoryMutex;
    private boolean[] directoryState;
    private int numDirectories;

    private Semaphore[] bus;
    private boolean[] busState;
    private int numBuses;

    private Semaphore mutexBarrier;
    private Semaphore barrierCycleClock;
    private int numCoresWaiting;
    private Semaphore finishedCoresMutex;
    private int finishedCores;
    private boolean allThreadsFinished;

    public Locks(int numCores,int numCaches, int numDirectories, int numBuses){
        this.numCores=numCores;
        this.numCaches=numCaches;
        this.numDirectories=numDirectories;
        this.numBuses=numBuses;
        this.numCoresWaiting=0;
        finishedCores=0;
        allThreadsFinished=false;

        cacheMutex= new Semaphore[numCaches];
        cacheState= new boolean[numCaches];
        for (int i = 0; i <cacheMutex.length ; i++) {
            cacheMutex[i]= new Semaphore(1);
            cacheState[i]=false;
        }

        directoryMutex = new Semaphore[numDirectories];
        directoryState= new boolean[numDirectories];
        for (int i = 0; i <directoryMutex.length ; i++) {
            directoryMutex[i]=new Semaphore(1);
            directoryState[i]=false;
        }

        bus=new Semaphore[numBuses];
        busState= new boolean[numBuses];
        for (int i = 0; i <bus.length ; i++) {
            bus[i]= new Semaphore(1);
            busState[i]=false;
        }

        mutexBarrier= new Semaphore(1);
        barrierCycleClock = new Semaphore(0);
        finishedCoresMutex=new Semaphore(1);
    }

    public int getNumCores() {
        return numCores;
    }

    public Semaphore[] getCacheMutex() {
        return cacheMutex;
    }

    public boolean[] getCacheState() {
        return cacheState;
    }

    public int getNumCaches() {
        return numCaches;
    }

    public Semaphore[] getDirectoryMutex() {
        return directoryMutex;
    }

    public boolean[] getDirectoryState() {
        return directoryState;
    }

    public int getNumDirectories() {
        return numDirectories;
    }

    public Semaphore[] getBus() {
        return bus;
    }

    public boolean[] getBusState() {
        return busState;
    }

    public int getNumBuses() {
        return numBuses;
    }

    public Semaphore getMutexBarrier() {
        return mutexBarrier;
    }

    public Semaphore getBarrierCycleClock() {
        return barrierCycleClock;
    }

    public int getNumCoresWaiting() {
        return numCoresWaiting;
    }

    public void setNumCoresWaiting(int numCoresWaiting) {
        this.numCoresWaiting = numCoresWaiting;
    }

    public Semaphore getFinishedCoresMutex() {
        return finishedCoresMutex;
    }

    public int getFinishedCores() {
        return finishedCores;
    }

    public void setFinishedCores(int finishedCores) {
        this.finishedCores = finishedCores;
    }

    public boolean isAllThreadsFinished() {
        return allThreadsFinished;
    }

    public void setAllThreadsFinished(boolean allThreadsFinished) {
        this.allThreadsFinished = allThreadsFinished;
    }
}
