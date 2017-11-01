package physicalcomponentssimulation.core;

import physicalcomponentssimulation.cache.DataCache;
import physicalcomponentssimulation.cache.InstructionCache;
import physicalcomponentssimulation.systemthread.SystemThread;

public class Core {

    private int[] context;
    private final int contextSize=33;//32 registros + pc;
    private DataCache dataCache;
    private InstructionCache instructionCache;
    private SystemThread asignedSystemThread;//para saber a quien estoy ejecutando



    public Core(){
        context= new int[contextSize];
        context[0]=0;//por default R0 siempre tendrá 0 y no será usado como registro destino.
    }

    public void saveContext(){
        for (int i = 0; i <contextSize ; i++) {
            asignedSystemThread.getContext()[i]=context[i];
        }
    }

    public void loadContext(){
        for (int i = 0; i <contextSize ; i++) {
            context[i]= asignedSystemThread.getContext()[i];
        }
    }
    public DataCache getDataCache() {
        return dataCache;
    }

    public void setDataCache(DataCache dataCache) {
        this.dataCache = dataCache;
    }

    public InstructionCache getInstructionCache() {
        return instructionCache;
    }

    public void setInstructionCache(InstructionCache instructionCache) {
        this.instructionCache = instructionCache;
    }

    //TODO cuando se tenga acceso al reloj, preguntar que si es -1 el valor de hilillo.initialClock, en caso de ser así, asignarle el reloj actual.

    public void setAsignedSystemThread(SystemThread systemThread){
        asignedSystemThread = asignedSystemThread;
    }
}
