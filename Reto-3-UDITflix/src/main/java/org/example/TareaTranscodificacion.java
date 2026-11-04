package org.example;

/**
 * UDITflix · Motor concurrente (pre-reto / base v0 del Reto 3)
 *
 * Simula la transcodificación de un vídeo. Implementa Runnable:
 * esta clase define QUÉ hace el hilo; el Thread define CÓMO se ejecuta.
 */
public class TareaTranscodificacion implements Runnable {

    private final String nombreVideo;
    private final int duracionSimuladaMs;

    public TareaTranscodificacion(String nombreVideo, int duracionSimuladaMs) {
        this.nombreVideo = nombreVideo;
        this.duracionSimuladaMs = duracionSimuladaMs;
    }

    @Override
    public void run() {
        String hiloActual = Thread.currentThread().getName();
        int prioridad = Thread.currentThread().getPriority(); // 1. Lectura de la prioridad del hilo

        // 1. Impresión adaptada con prioridad y latencia
        System.out.println("--> [" + hiloActual + "] prio=" + prioridad + " latencia=" + duracionSimuladaMs + " ms");

        try {
            // Simulamos el trabajo. Mientras duerme, el hilo está en TIMED_WAITING.
            Thread.sleep(duracionSimuladaMs);
        } catch (InterruptedException e) {
            System.err.println("!--> [" + hiloActual + "] " + nombreVideo + " CANCELADO (interrumpido).");
            Thread.currentThread().interrupt(); // restaurar el aviso de interrupción
            return; // IMPORTANTE: sin este return se imprimiría "COMPLETADO" aunque se cancelara
        }

        // 1. Impresión de finalización simplificada según la salida orientativa
        System.out.println("<-- [" + hiloActual + "] LISTO");
    }

    public String getNombreVideo() {
        return nombreVideo;
    }

    public int getDuracionSimuladaMs() {
        return duracionSimuladaMs;
    }
}