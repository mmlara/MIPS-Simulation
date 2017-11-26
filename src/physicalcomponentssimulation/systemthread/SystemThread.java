package physicalcomponentssimulation.systemthread;

import physicalcomponentssimulation.processorsparts.Instruction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SystemThread {
    
    private int[] context;
    private int pc;
    private final int contextSize=32;//32 registros ;
    private List<Instruction> myInstructions;
    private int idHilillo; //necesario para identificar al hilillo para presentar luego la información.
    private int idProcessorAsigned=0;
    private int numCyclesInExecution;//total de ciclos que tardo en ejecutarse (tiempo de CPU)
    private int initialClock;//default -1 para saber cuando se ejecuta por primera vez.
    private int lastClock;
    private int initIndexInMemory;
    private int lastIndexInMemory;
    private int currentCyclesInProcessor;//para saber cuantos ciclos lleva del quantum
    private boolean inMemory;//por si está en memoria y se acaba el quantum para no sacarlo hasta que termine.
    private String myName;

    public SystemThread(String path){
        this.numCyclesInExecution=0;
        this.initialClock=-1;
        context= new int[contextSize];
        myInstructions = new ArrayList<Instruction>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null){
                String[] instructionParts= line.split(" ");
                Instruction instruction = new Instruction();
                instruction.setOperationCode(Integer.parseInt(instructionParts[0]));
                instruction.setFirsParameter(Integer.parseInt(instructionParts[1]));
                instruction.setSecondParameter(Integer.parseInt(instructionParts[2]));
                instruction.setThirdParameter(Integer.parseInt(instructionParts[3]));
                myInstructions.add(instruction);
            }
        }catch (Exception e){
            e.getMessage();
        }
        System.out.println(myInstructions.toString());
    }


    public int[] getContext() {
        return context;
    }

    public int getContextSize() {
        return contextSize;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public List<Instruction> getMyInstructions() {
        return myInstructions;
    }

    public int getIdHilillo() {
        return idHilillo;
    }

    public int getNumCyclesInExecution() {
        return numCyclesInExecution;
    }

    public int getInitialClock() {
        return initialClock;
    }

    public void setInitialClock(int initialClock) {
        this.initialClock = initialClock;
    }

    public int getLastClock() {
        return lastClock;
    }

    public int getCurrentCyclesInProcessor() {
        return currentCyclesInProcessor;
    }

    public void setCurrentCyclesInProcessor(int currentCyclesInProcessor) {
        this.currentCyclesInProcessor = currentCyclesInProcessor;
    }

    public void setIdHilillo(int idHilillo) {
        this.idHilillo = idHilillo;
    }

    public void setNumCyclesInExecution(int numCyclesInExecution) {
        this.numCyclesInExecution = numCyclesInExecution;
    }

    public boolean isInMemory() {
        return inMemory;
    }

    public int getInitIndexInMemory() {
        return initIndexInMemory;
    }

    public void setInitIndexInMemory(int initIndexInMemory) {
        this.initIndexInMemory = initIndexInMemory;
    }

    public int getLastIndexInMemory() {
        return lastIndexInMemory;
    }

    public void setLastIndexInMemory(int lastIndexInMemory) {
        this.lastIndexInMemory = lastIndexInMemory;
    }

    public void setLastClock(int lastClock) {
        this.lastClock = lastClock;
    }

    public int getIdProcessorAsigned() {
        return idProcessorAsigned;
    }

    public void setIdProcessorAsigned(int idProcessorAsigned) {
        this.idProcessorAsigned = idProcessorAsigned;
    }



    @Override
    public String toString() {

        String infoHilillo= "Hilillo "+myName+" con id"+idHilillo+" ejecutado en el Procesador : " +idProcessorAsigned +"\n"+
                "Su ejecución inició en el ciclo de reloj número : "+initialClock+"\n"+
                "Su ejecución tardó : "+numCyclesInExecution+"\n"+
                "El estado de sus registros es el siguiente\n";

        for (int i = 0; i < contextSize; i++) {
            infoHilillo+="R"+i+" : "+context[i]+"\n";
        }
        return infoHilillo;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }
}

