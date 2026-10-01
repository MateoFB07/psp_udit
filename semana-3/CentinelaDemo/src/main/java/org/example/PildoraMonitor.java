package org.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PildoraMonitor {

    public static void main (String[] args) {
        System.out.println ( " ===== MONITOR UDITFLIX =====");
        System.out.println ("Comprobando servicios...");

        // Try / catch
        // Lanzar un programa externo o esperar a que termine (puede fallar).
        // try -> "intenta hacer esto"
        // catch -> "Si algo sale mal, haz esto en vez de romper el programa"

        try {
            // PASO 1: PREPARAR EL PROCESO (TODAVÍA NO SE EJECUTA)
            // ProcessBuilder es el "encargado" que prepara la orden que le daremos al SO.
            // Es como rellenar un formulario:
            // "ping" es el programa que queremos ejecutar.
            // "-n" es la opción de Windows: número de intentos (n veces).
            // "1", es haz solo un intento (así termina más rápido)
            // "127.0.0.1", es a quién hacemos el ping: nuestro propio ordenador.
            //                      (siempre responde, simula un servicio ACTIVO) --> En MAC no funciona.
            // OJO "-n" solo vale en Windows. En Linux y Mac sería "-c".

            ProcessBuilder pb = new ProcessBuilder(
                    "ping", "-c", "1", "127.0.0.1"

            );

            // PASO 2: UNIR LOS DOS CANALES DE SALIDA:
            // Todo programa tiene dos canales: por los que "habla"
            // Uno es: salida normal (lo que funciona bien)
            // Otro es: salida de error (los fallos).
            pb.redirectErrorStream(true);   // Esto junta en uno solo, leyendo así un único canal, vemos TODO lo que el proceso diga,
            // sea un resultado normal o un error.

            // PASO 3: LANZAR EL PROCESO
            // start() lanza los procesos. Es el botón de "enviar". Ahora sí, el SO va
            // a crear un programa nuevo (ping), que corre
            // por su cuenta con su propia memoria, separando de nuestro programa java.
            // Process es el objeto con el que controlamos ese programa.

            Process proceso = pb.start();

            // PASO 4: MOSTRAR EL PID
            // PID = Process Identifier . es el "DNI" del proceso.

            System.out.println ("PID: " + proceso.pid());

            // PASO 5: PREPARAR LA LECTURA DE LO QUE DICE EL PROCESO


            BufferedReader lector = new BufferedReader (
                    new InputStreamReader (proceso.getInputStream())  // Es el proceso "ping", que escribe
                    // su propia consola que Java no ve. Para escucharlo, nos "conectamos" a su salida con una cadena:
                    //proceso.getInputString() --> la "tubería" por la que sale el
                    // texto del proceso (en bytes).
            );

            // new InputStreamReader (...) --> traduce esos bytes a letras.
            // new BufferedReader (...) --> nos deja leer línea a linea.

            // PASO 6: LEER TODO LO QUE EL PROCESO VA ESCRIBIENDO
            String linea;
            while ((linea = lector.readLine()) !=null) {
                System.out.println(linea);
            }

            // PASO 7: ESPEREAR A QUE EL PROCESO TERMINE
            int codigo = proceso.waitFor(); // Garantiza el código de salida.

            // PASO 8: INTERPRETAR EL RESULTADO

            if (codigo == 0) {
                System.out.println("ESTADO: SERVICIO ACTIVO");
            } else {
                System.out.println("ESTADO: SERVICIO CON ERROR");
            }
        } catch (IOException e) {
            System.out.println("No se pudo lanzar el proceso");
        } catch (InterruptedException e) {
            System.out.println("La ejecución fue interrumpida ");
        }
    }
}