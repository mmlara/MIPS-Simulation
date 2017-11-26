/**
 * This class represents logical threads. In the current evaluation, is represented "hilillos"
 *
 * @author Gómez Brayan, Lara Milton, Quirós Esteban
 * @version 1.0
 * @since 25/11/2017
 */

/**
 * @implNote When refers to hilillo, it is referring to System Threads(logical threads)
 * which is named in that way because of evaluation details
 * i.e, hilillos = System Threads
 */

package physicalcomponentssimulation.systemthread;

import physicalcomponentssimulation.processorsparts.Instruction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SystemThread {

    private final int contextSize = 32; //final value
    private int idProcessorAsigned = 0; //final value

    /*The next variable represent the context of the thread, which is composed by 32 registers*/
    private int[] context;

    private int pc;
    private int idHilillo; //It keep the id of the thread to be identified later
    private int numCyclesInExecution;//total de ciclos que tardo en ejecutarse (tiempo de CPU)
    private int initialClock;//default -1 para saber cuando se ejecuta por primera vez.
    private int lastClock;
    private int initIndexInMemory;
    private int lastIndexInMemory;
    private int currentCyclesInProcessor;//para saber cuantos ciclos lleva del quantum
    private String myName;
    private List<Instruction> myInstructions;

    /**
     * System thread constructor

     * @param path Location of the hilillo code to be loaded into a thread
     */
    public SystemThread(String path) {
        this.numCyclesInExecution = 0;
        this.initialClock = -1;
        context = new int[contextSize];
        myInstructions = new ArrayList<Instruction>();
        this.loadThread(path);
        System.out.println(myInstructions.toString());
    }

    /**
     * Load a file located in the received path into a array of instructions
     * @param path Location on the hilillo code to be loaded into an instruction array
     */
    private void loadThread(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] instructionParts = line.split(" ");
                Instruction instruction = new Instruction();
                instruction.setOperationCode(Integer.parseInt(instructionParts[0]));
                instruction.setFirsParameter(Integer.parseInt(instructionParts[1]));
                instruction.setSecondParameter(Integer.parseInt(instructionParts[2]));
                instruction.setThirdParameter(Integer.parseInt(instructionParts[3]));
                myInstructions.add(instruction);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * Get the name of the thread. It is represented by the name of the file
     * @return String with the thread name
     */
    public String getMyName() {
        return myName;
    }

    /**
     * Return the context of the current thread
     * @return an array with all registers
     */
    public int[] getContext() {
        return context;
    }

    /**
     * Get the current program counter
     * @return An Integer with the PC value
     */
    public int getPc() {
        return pc;
    }

    /**
     *Set program counter to "pc" value
     * @param pc Value to set program counter
     */
    public void setPc(int pc) {
        this.pc = pc;
    }

    /**
     * Get instructions from the current thread
     * @return An array with the  {@link Instruction} class

     */
    public List<Instruction> getMyInstructions() {
        return myInstructions;
    }

    /**
     * Get the id of the system thread
     * @return An integer which it is the id of the hilillo
     */
    public int getIdHilillo() {
        return idHilillo;
    }

    /**
     * Get the number of cycles in execution of the current hilillo
     * @return An integer which it is the id of the cycles in execution
     */
    public int getNumCyclesInExecution() {
        return numCyclesInExecution;
    }

    /**
     * Get the initial value of the clock
     * @return An integer with the initial clock value
     */
    public int getInitialClock() {
        return initialClock;
    }

    /**
     * Set the initial value to clock
     * @param initialClock Value to assign to the clock
     */
    public void setInitialClock(int initialClock) {
        this.initialClock = initialClock;
    }

    /**
     * Get the number of cycles that the thread have spent in processor
     * @return An integer with the number of cycles in processor
     */
    public int getCurrentCyclesInProcessor() {
        return currentCyclesInProcessor;
    }

    public void setCurrentCyclesInProcessor(int currentCyclesInProcessor) {
        this.currentCyclesInProcessor = currentCyclesInProcessor;
    }

    /**
     * Set the hilillo id
     * @param idHilillo Value to assign to hilillo id
     */
    public void setIdHilillo(int idHilillo) {
        this.idHilillo = idHilillo;
    }

    /**
     *  Set number od cycles in execution
     * @param numCyclesInExecution Value to assign to number of cycles in execution
     */
    public void setNumCyclesInExecution(int numCyclesInExecution) {
        this.numCyclesInExecution = numCyclesInExecution;
    }

    /**
     * Get the index in memory
     * @return Value of the index in memory
     */
    public int getInitIndexInMemory() {
        return initIndexInMemory;
    }

    /**
     * Set the index in memory
     * @param initIndexInMemory Value of the index in memory
     */
    public void setInitIndexInMemory(int initIndexInMemory) {
        this.initIndexInMemory = initIndexInMemory;
    }

    /**
     * Set the value of the last index in memory of the referred System Thread
     * @param lastIndexInMemory Value of the last index in memory
     */
    public void setLastIndexInMemory(int lastIndexInMemory) {
        this.lastIndexInMemory = lastIndexInMemory;
    }

    /**
     * Set the last value of clock
     * @param lastClock Last value of the clock
     */
    public void setLastClock(int lastClock) {
        this.lastClock = lastClock;
    }

    /**
     * Set the id of the the processor in which the thread was assigned
     * @param idProcessorAsigned Identifier of the processor
     */
    public void setIdProcessorAsigned(int idProcessorAsigned) {
        this.idProcessorAsigned = idProcessorAsigned;
    }


    /**
     * Overwrite of the method "toString()" to return all information about the courrent Thread
     * @return String with the information about the referred thread
     */
    @Override
    public String toString() {

        String infoHilillo = "Hilillo " + myName + " con id" + idHilillo + " ejecutado en el Procesador : " + idProcessorAsigned + "\n" +
                "Su ejecución inició en el ciclo de reloj número : " + initialClock + "\n" +
                "Su ejecución tardó : " + numCyclesInExecution + "\n" +
                "El estado de sus registros es el siguiente\n";

        for (int i = 0; i < contextSize; i += 2) {
            infoHilillo += "R" + i + " : " + context[i] + "\t \tR" + (i + 1) + " : " + context[i + 1] + "\n";
        }
        return infoHilillo;
    }

    /**
     * Set the name of the Thread
     * @param myName Value with the Thread name
     */
    public void setMyName(String myName) {
        this.myName = myName;
    }
}

