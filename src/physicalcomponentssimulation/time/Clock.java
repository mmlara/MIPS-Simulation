/**
 *  Clock
 *
 * @author Gómez Brayan, Lara Milton, Quirós Esteban
 * @version 1.0
 * @since 25/11/2017
 */
package physicalcomponentssimulation.time;

public class Clock {

    /**
     * Current cycle in the simulation
     */
    private int currentTime;

    /**
     * constructor
     */
    public Clock(){
        this.currentTime=0;
    }

    /**
     * get Current time
     * @return current cycle
     */
    public int getCurrentTime() {
        return currentTime;
    }

    /**
     * Increase in one cycle the clock
     */
    public void increaseCurrentTime() {
        this.currentTime++;
    }
}
