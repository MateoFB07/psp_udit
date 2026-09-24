package org.example;

import java.io.IOException;

public class LanzadorProcesos {
    public static void main(String[] args) {
        System.out.println("Solicitando al SO la creación de un proceso nativo...");

        // 1. ProcessBuilder es el puente entre Java y el Sistema Operativo.
        // En macOS usas 'open -a TextEdit' para lanzar la app.
        ProcessBuilder pb = new ProcessBuilder("open", "-a", "TextEdit");

        try {
            // 2. start() es el gatillo. Pide al planificador del SO que cree el proceso.
            Process proceso = pb.start();
            System.out.println("Proceso en ejecución. PID: " + proceso.pid());

            // 3. waitFor() pausa nuestro hilo de Java hasta que el proceso termine.
            int salida = proceso.waitFor();

            // 0 significa cierre limpio. Cualquier otro número indica error o cierre forzoso.
            System.out.println("El proceso externo ha finalizado con código " + salida);

        } catch (IOException e) {
            System.out.println("Error: el sistema operativo no encuentra el programa: " + e.getMessage());

        } catch (InterruptedException e) { // <-- CORREGIDO: Se añade la 'e' y se abre el bloque con {
            System.out.println("El hilo de Java fue interrumpido mientras esperaba a TextEdit.");
        }
    }
}