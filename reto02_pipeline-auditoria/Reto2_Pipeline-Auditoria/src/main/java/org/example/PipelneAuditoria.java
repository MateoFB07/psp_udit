package org.example;

import java.io.IOException;

public class PipelneAuditoria {
    public static void main (String [] args) {
        System.out.println("=============");
        System.out.println("PIPELINE AUDITORÍA");
        System.out.println("==============");

        try {
            System.out.println(("INICIANDO EJECUCIÓN PARALELA..."));
            long inicioParalelo = System.currentTimeMillis();

            // PASO A: Lanzamos ambos procesos antes de esperar a que terminen.
            System.out.println("            -> Lanzando proceso 1 (sin esperar)");
            Process p1 = new ProcessBuilder("ping", "-c", "2", "error.Invalid").start();
            System.out.println("PID: " + p1.pid());
            System.out.println("            -> Lanzando proceso 2 (sin esperar)");
            Process p2 = new ProcessBuilder("ping", "-c", "2", "127.0.0.1").start();
            System.out.println("PID: " + p2.pid());

            // PASO B: Ahora sí, le decimos a Java que recoja los resultados.
            // Como ya están corriendo simultáneamente en el SO, el tiempo de espera se solapa.
            System.out.println("        -> BLOQUEANDO Java para recoger resultados");



            System.out.println("Código de salida proceso 1: " + p1.waitFor());
            System.out.println("Código de salida proceso 2: " + p2.waitFor());

            if (p1.waitFor() == 0 && p2.waitFor() == 0) {
                abrirAplicacion("com.apple.TextEdit");
            } else {
                abrirAplicacion("com.apple.Calculator");
            }

            long finParalelo = System.currentTimeMillis();
            System.out.println("⏱️ TIEMPO TOTAL PARALELO: " + (finParalelo - inicioParalelo) + "ms");

        } catch (IOException e) {
            System.out.println("ERROR al iniciar un proceso: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("ERROR: La espera fue interrumpida de forma inesperada.");
        }
    }

    private static void abrirAplicacion(String identificador) throws IOException, InterruptedException {
        System.out.println("Abriendo aplicación: " + identificador);
        Process proceso = new ProcessBuilder("open", "-b", identificador)
                .inheritIO()
                .start();
        int codigo = proceso.waitFor();
        if (codigo != 0) {
            System.out.println("macOS no pudo abrir la aplicación (código " + codigo + ").");
        }
    }
}
