package processor;

import core.Core;
import directory.Directory;
import hilillo.Hilillo;
import memory.InstructionMemory;
import memory.Memory;

import java.util.Queue;

public class Processor {

    private int numCores;
    private Core[] cores;
    private  int quantumSize;//cambiar este valor y chequear si se escribe así
    Directory directory;
    private int numTotalCaches;
    private Memory memory;
    private InstructionMemory instructionMemory;
    private String pathDirectoryHilillos;
    private int numHilillosAsigned;
    private Queue<Hilillo> asignedHilillos;//para que sea más eficiente seleccionar al siguiente hilillo en el procesador.

    public Processor(int numCores, int numTotalCaches, int quantumSize, String pathDirectoryHilillos) {
        this.numCores = numCores;
        this.cores= new Core[numCores];
        for (int i = 0; i <numCores ; i++) {
            Core core = new Core();
            cores[i]= core;
        }

        this.numTotalCaches=numTotalCaches;
        this.directory = new Directory(Memory.shareMemorySize, numTotalCaches);
        this.quantumSize=quantumSize;
        this.pathDirectoryHilillos=pathDirectoryHilillos;
    }

    public int getQuantumSize() {
        return quantumSize;
    }

    public void setQuantumSize(int quantumSize) {
        this.quantumSize = quantumSize;
    }

    public int getNumCores() {
        return numCores;
    }

    public void setNumCores(int numCores) {
        this.numCores = numCores;
    }

    public Core[] getCores() {
        return cores;
    }

    public void setCores(Core[] cores) {
        this.cores = cores;
    }

    public void loadHilillos(){
        //Abrir el path enviado por parámetro y meter los hilos en el array
    }
}
