package hilillo;

import processorsparts.Instruction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Hilillo {
    
    private int[] context;
    private final int contextSize=33;//32 registros + pc;
    private List<Instruction> myInstructions;
    private int idHilillo; //necesario para identificar al hilillo para presentar luego la información.
    private int numCyclesInExecution;//total de ciclos que tardo en ejecutarse (tiempo de CPU)
    private int initialClock=-1;//default -1 para saber cuando se ejecuta por primera vez.
    private int lastClock;
    private int initIndexInMemory;
    private int lastIndexInMemory;
    private int currentCyclesInProcessor;//para saber cuantos ciclos lleva del quantum
    private boolean inMemory;//por si está en memoria y se acaba el quantum para no sacarlo hasta que termine.

    public Hilillo(String path){
        this.numCyclesInExecution=0;
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

    public int getLastClock() {
        return lastClock;
    }

    public int getCurrentCyclesInProcessor() {
        return currentCyclesInProcessor;
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


}

