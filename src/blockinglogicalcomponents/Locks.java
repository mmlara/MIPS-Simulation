package blockinglogicalcomponents;

import java.util.concurrent.Semaphore;

public class Locks {

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

    public Locks(int numCaches, int numDirectories, int numBuses){
        this.numCaches=numCaches;
        this.numDirectories=numDirectories;
        this.numBuses=numBuses;
        this.numCoresWaiting=0;

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
    }
}
