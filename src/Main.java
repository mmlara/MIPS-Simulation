/**
 * Main class
 *
 * @description Main class
 * @author Gómez Brayan, Lara Milton, Quirós Esteban
 * @version 1.0
 * @since 25/11/2017
 */

import simulationcontroller.SimulationController;

import java.util.List;
import java.util.Scanner;

public class Main {

    /**
     * Main method to run the simulation
     *
     * @param args
     */
    public static void main(String[] args) {
        int opcion = 0;
        boolean slowMode = false;

        do {
            int quantum = 30;
            Scanner sc = new Scanner(System.in);
            String pathHililloP0 = "DatosHilillos/P0";
            String pathHililloP1 = "DatosHilillos/P1";
            String opciones = "1- Ver variables de la simulación\n" +
                    "2- Configurar variables de la simulación\n" +
                    "3- Ejecutar Simulación\n" +
                    "4- Salir";
            SimulationController simullation = new SimulationController();
            System.out.println("******************************************");
            System.out.println(" \t\t Simulación MIPS");
            System.out.println("******************************************\n");
            System.out.println(opciones);
            opcion = sc.nextInt();
            switch (opcion) {
                case 1:
                    System.out.println("Path de procesador 0 : " + pathHililloP0);
                    System.out.println("Path de procesador 1 : " + pathHililloP1);
                    System.out.println("Tamaño del quantum : " + quantum);
                    if (slowMode) {
                        System.out.println("Modo de ejecución lenta activado\n\n");
                    } else {
                        System.out.println("Modo de ejecución lenta desactivado \n\n");
                    }

                    break;

                case 2:
                    String opciones2 = "Selecione el número según la variable que desea modificar\n\n" +
                            "1- Path Procesador 0 \n" +
                            "2- Path Procesador 1 \n" +
                            "3- Tamaño de Quantum 2 \n" +
                            "4- modo de Ejecución\n" +
                            "5- Volver a menú anterior\n";
                    int opcion2 = 0;
                    do {
                        System.out.println(opciones2);
                        opcion2 = sc.nextInt();
                        switch (opcion2) {
                            case 1:
                                System.out.println(" ingrese el nuevo path de P0");
                                pathHililloP0 = sc.next();
                                break;
                            case 2:
                                System.out.println(" ingrese el nuevo path de P1");
                                pathHililloP1 = sc.next();
                                break;
                            case 3:
                                System.out.println(" ingrese el nuevo tamaño de quantum");
                                quantum = sc.nextInt();
                                break;
                            case 4:
                                System.out.println(" ingrese el nuevo modo de ejecucion (0 para lento ó 1 para normal)");
                                slowMode = sc.nextInt() == 0;
                                break;

                        }
                    } while (opcion2 != 5);


                    break;

                case 3:
                    simullation.run(2, 2, 1, 1, pathHililloP0, pathHililloP1, slowMode, quantum);
                    break;
                default:
                    break;
            }

        } while (opcion != 4);

    }
}
