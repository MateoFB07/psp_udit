package org.example;

import java.util.ArrayList; // 1. Importación para el ArrayList
import java.util.List;      // 1. Importación para la interfaz List
import java.util.Random;

/**
 * BASE DEL JUEVES (versión que se da al alumnado): 4 hilos creados "a mano".
 *
 * Pasos que se explican en la píldora:
 *   1) Crear las tareas (Runnable).
 *   2) Envolver cada tarea en un Thread con nombre propio.
 *   3) Lanzar con start()   (NUNCA run()).
 *   4) Esperar con join()   (para que main no dé el resultado antes de tiempo).
 *
 * Después de la píldora, el alumnado resuelve los mini-retos A, B y C sobre esta base.
 */
public class MainUDITflix {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("   UDITflix BACKEND · MOTOR DE TRANSCODIFICACIÓN  ");
        System.out.println("==================================================");

        Random azar = new Random();
        // Duración aleatoria entre 1000 y 4000 ms: así el orden de llegada cambia cada vez.
        int d1 = 1000 + azar.nextInt(3001);
        int d2 = 1000 + azar.nextInt(3001);
        int d3 = 1000 + azar.nextInt(3001);
        int d4 = 1000 + azar.nextInt(3001);

        long inicio = System.currentTimeMillis();

        // 1) Tareas (módulos de descarga)
        Runnable tarea1 = new TareaTranscodificacion("catalogo-metadatos", d1); // 1. Nombre módulo
        Runnable tarea2 = new TareaTranscodificacion("sesion-y-perfil", d2);     // 1. Nombre módulo
        Runnable tarea3 = new TareaTranscodificacion("portadas-assets", d3);     // 1. Nombre módulo
        Runnable tarea4 = new TareaTranscodificacion("analiticas", d4);          // 1. Nombre módulo

        // 2) Hilos con nombre
        Thread hilo1 = new Thread(tarea1, "catalogo");  // 1. Nombre hilo
        Thread hilo2 = new Thread(tarea2, "sesion");    // 1. Nombre hilo
        Thread hilo3 = new Thread(tarea3, "portadas");  // 1. Nombre hilo
        Thread hilo4 = new Thread(tarea4, "analitica"); // 1. Nombre hilo

        // 1. RT3: Asignación de prioridades requeridas antes de start()
        hilo1.setPriority(Thread.MAX_PRIORITY);  // 10
        hilo2.setPriority(Thread.MAX_PRIORITY);  // 10
        hilo3.setPriority(Thread.NORM_PRIORITY); // 5
        hilo4.setPriority(Thread.MIN_PRIORITY);  // 1

        // 1. RT4: Creación de la lista dinámica ArrayList<Thread>
        List<Thread> hilos = new ArrayList<>();
        hilos.add(hilo1); // 1.
        hilos.add(hilo2); // 1.
        hilos.add(hilo3); // 1.
        hilos.add(hilo4); // 1.

        // 3) Lanzamiento concurrente
        System.out.println("[MAIN] Descargando " + hilos.size() + " módulos en paralelo...\n"); // 1.

        // 1. Bucle limpio para lanzar todos los hilos de la lista
        for (Thread hilo : hilos) {
            hilo.start();
        }

        // 4) Espera: main se bloquea hasta que cada hilo termina
        // 1. Bucle limpio para esperar con join() a cada hilo de la lista
        for (Thread hilo : hilos) {
            hilo.join();
        }

        long total = System.currentTimeMillis() - inicio; // 1. Descomentado de la base
        int masLento = Math.max(Math.max(d1, d2), Math.max(d3, d4));
        int suma = d1 + d2 + d3 + d4;

        System.out.println("\n==================================================");
        System.out.println(" [MAIN] Todos los módulos listos -> LOGIN HABILITADO"); // 1. Traza de la puerta del login
        System.out.println(" Tiempo total real:          " + total + " ms");
        System.out.println(" Duración del más lento:     " + masLento + " ms");
        System.out.println(" Suma de todas (secuencial): " + suma + " ms");
        System.out.println("==================================================");
    }
}