package org.example;

import java.io.IOException;

public class CodigoSalida {
    public static void main(String[] args) {
        System.out.println("=====================");
        System.out.println("          COMPROBACIÓN DEL SERVIDOR           ");
        System.out.println("=====================");

        try {
            // 1. PREPARAMOS EL PROCESO EXTERNO
            // Nota: En macOS / Linux el parámetro para limitar a 1 paquete es "-c" en lugar de "-n"
            ProcessBuilder pb = new ProcessBuilder(
                    "ping",
                    "-n",
                    "1",
                    "8.8.8.8"
            );

            // 2. LANZAMOS EL PROCESO
            Process proceso = pb.start();

            // 3. OBTENEMOS EL PID
            System.out.println("PID: " + proceso.pid());

            // 4. ESPERAMOS A QUE TERMINE Y OBTENEMOS EL CÓDIGO
            int codigoSalida = proceso.waitFor();

            // 5. MOSTRAMOS EL CÓDIGO
            System.out.println("Código de salida: " + codigoSalida);

            // 6. INTERPRETAMOS EL RESULTADO
            if (codigoSalida == 0) {
                System.out.println("ESTADO: ACTIVO");
            } else {
                System.out.println("ESTADO: CAÍDO");
            }

        } catch (IOException e) {
            System.out.println("Error al lanzar el proceso: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("La espera del proceso fue interrumpida: " + e.getMessage());
        }

        System.out.println("=====================");
        System.out.println("FIN DE LA COMPROBACIÓN");
        System.out.println("=====================");
    }
}