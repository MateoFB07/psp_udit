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
            Process p1 = new ProcessBuilder("ping", "-c", "3", "-W", "3000", "127.0.0.1").start();
            System.out.println("PID: " + p1.pid());
            System.out.println("            -> Lanzando proceso 2 (sin esperar)");
            Process p2 = new ProcessBuilder("ping", "-c", "3", "-W", "3000", "127.0.2.1").start();
            System.out.println("PID: " + p2.pid());

            // PASO B: Ahora sí, esperamos a que terminen los procesos.
            System.out.println("        -> BLOQUEANDO Java para recoger resultados");
            System.out.println("Código de salida proceso 1: " + p1.waitFor());
            System.out.println("Código de salida proceso 2: " + p2.waitFor());

            // NUEVO: Contador de reintentos del primer ping; empieza en cero.
            int reintentos1 = 0;
            // NUEVO: Repite mientras el último ping haya fallado y queden reintentos.
            while (p1.waitFor() != 0 && reintentos1 < 3) {
                // NUEVO: Suma uno porque vamos a realizar otro intento.
                reintentos1++;
                // NUEVO: Muestra en consola cuál de los tres reintentos se está haciendo.
                System.out.println("Reintento " + reintentos1 + "/3");
                Thread.sleep(1000); // NUEVO: Espera un segundo antes de lanzar el reintento.
                // NUEVO: Lanza otra vez el ping al mismo destino y guarda el proceso nuevo en p1.
                p1 = new ProcessBuilder("ping", "-c", "1", "-W", "3000", "error.Invalid").start();
            }

            // NUEVO: Contador de reintentos del segundo ping; empieza en cero.
            int reintentos2 = 0;
            // NUEVO: Repite mientras el último ping haya fallado y queden reintentos.
            while (p2.waitFor() != 0 && reintentos2 < 3) {
                // NUEVO: Suma uno porque vamos a realizar otro intento.
                reintentos2++;
                // NUEVO: Muestra en consola cuál de los tres reintentos se está haciendo.
                System.out.println("Reintento " + reintentos2 + "/3");
                Thread.sleep(1000); // NUEVO: Espera un segundo antes de lanzar el reintento.
                // NUEVO: Lanza otra vez el ping al mismo destino y guarda el proceso nuevo en p2.
                p2 = new ProcessBuilder("ping", "-c", "1", "-W", "3000", "127.0.2.1").start();
            }

            // NUEVO: Comprueba los resultados finales, después de completar los reintentos.
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
