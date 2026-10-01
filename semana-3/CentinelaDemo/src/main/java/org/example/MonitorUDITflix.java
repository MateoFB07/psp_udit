package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MonitorUDITflix {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("UDITFLIX - CATÁLOGO");
        System.out.println("========================================");

        // REQ 1: Matriz de 2 dimensiones [Nombre del contenido][Dirección IP]
        // Uso 127.0.0.1 para simular servicios ACTIVOS.
        // Uso una IP no válida (192.0.2.r) para simular servicios CAÍDOS.
        String[][] contenidos = {
                {"Series", "192.0.2.r"},       // CAÍDO
                {"Películas", "127.0.0.1"},     // ACTIVO
                {"Documentales", "127.0.0.1"},  // ACTIVO
                {"Anime", "192.0.2.r"},        // CAÍDO
                {"Infantil", "127.0.0.1"}      // ACTIVO
        };

        // REQ: 2: Recorrer todos los contenidos mediante un bucle for
        for (int i = 0; i < contenidos.length; i++) {

            String nombre = contenidos[i][0];
            String ip = contenidos[i][1];

            System.out.println("CONTENIDO " + nombre);

            try {
                // REQ 3: Crear el proceso externo con ProcessBuilder
                // Nota: Uso "-c" para Mac/Linux. En Windows cambiar "-c" por "-n"
                ProcessBuilder pb = new ProcessBuilder("ping", "-c", "1", ip);

                Process proceso = pb.start();

                // REQ 4: Mostrar el PID del proceso
                System.out.println("PID: " + proceso.pid());

                // REQ 5: Leer la salida del proceso con BufferedReader
                BufferedReader lector = new BufferedReader(
                        new InputStreamReader(proceso.getInputStream())
                );

                String linea;
                while ((linea = lector.readLine()) != null) {
                    // Leo el flujo del ping para liberar la salida
                }

                // REQ 6: Esperar a que finalice con waitFor()
                int codigo = proceso.waitFor();

                // REQ 7 y 8: Determinar y mostrar si está ACTIVO o CAÍDO
                if (codigo == 0) {
                    System.out.println("ESTADO: ACTIVO");
                } else {
                    System.out.println("ESTADO: CAÍDO");
                }

            } catch (IOException e) {
                System.out.println("No se pudo lanzar el proceso para");
            } catch (InterruptedException e) {
                System.out.println("La ejecución fue interrumpida para");
            }
        }

        System.out.println("========================================");
        System.out.println("COMPROBACIÓN FINALIZADA");
        System.out.println("========================================");
    }
}