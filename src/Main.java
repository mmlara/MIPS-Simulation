import blockinglogicalcomponents.Locks;
import javafx.application.Application;
import javafx.stage.Stage;
import physicalcomponentssimulation.directory.Directory;
import physicalcomponentssimulation.memory.Memory;
import physicalcomponentssimulation.processor.Processor;
import physicalcomponentssimulation.systemthread.SystemThread;
import physicalcomponentssimulation.time.Clock;
import simulationcontroller.SimulationController;


import java.util.Scanner;

public class Main {





    public static void main(String[] args)
    {
        SimulationController simullation = new SimulationController();
        System.out.println(" \t\t Simulación MIPS \n\n");
        String pathHililloP0="DatosHilillos/P0";
        String pathHililloP1="DatosHilillos/P1";
        int quamtun=30;
        boolean slowMode=false;
        Scanner sc = new Scanner(System.in);
        String opciones="1- Ver variables de la simulación\n"+
                        "2- Configurar variables de la simulación\n"+
                        "3- Ejecutar Simulación\n"+
                        "4- Salir";

        int opcion =0;
        do {
            System.out.println(opciones);
            opcion=sc.nextInt();
            switch (opcion){
                case 1:
                    System.out.println("Path de procesador 0 : "+ pathHililloP0);
                    System.out.println("Path de procesador 1 : "+ pathHililloP1);
                    System.out.println("Tamaño del quantum : "+ quamtun);
                    if(slowMode==true){
                        System.out.println("Modo de ejecución lenta activado\n\n");
                    }else {
                        System.out.println("Modo de ejecución lenta desactivado \n\n");
                    }

                    break;

                case 2:
                    String opciones2="Selecione el número según la variable que desea modificar\n\n"+
                            "1- Path Procesador 0 \n"+
                            "2- Path Procesador 1 \n"+
                            "3- Tamaño de Quantum 2 \n"+
                            "4- modo de Ejecución\n"+
                            "5- Volver a menú anterior\n";
                    int opcion2=0;
                    do{
                        System.out.println(opciones2);
                        opcion2= sc.nextInt();
                        switch (opcion2){
                            case 1:
                                System.out.println(" ingrese el nuevo path de P0");
                                pathHililloP0= sc.next();
                                break;
                            case 2:
                                System.out.println(" ingrese el nuevo path de P1");
                                pathHililloP1= sc.next();
                                break;
                            case 3:
                                System.out.println(" ingrese el nuevo tamaño de quantum");
                                quamtun= sc.nextInt();
                                break;
                            case 4:
                                System.out.println(" ingrese el nuevo modo de ejecucion (0 ó 1)");
                                if (sc.nextInt()==0){
                                    slowMode=false;
                                }else {
                                    slowMode=true;
                                }
                                break;

                        }
                    }while(opcion2!=5);


                    break;

                case 3:
                    simullation.run(2,2,1,1,pathHililloP0,pathHililloP1,slowMode,quamtun);

                    break;



                 default:
                    break;

            }

        }while (opcion!=4);


    }
}
