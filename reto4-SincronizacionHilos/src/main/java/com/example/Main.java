package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws Exception {

        // Meto los 7 archivos de la tabla en una lista con su nombre y duración
        List<Asset> lote = List.of(
                new Asset("portada-home.webp", 1300),
                new Asset("logo-udit.svg", 700),
                new Asset("banner-estreno.jpg", 1800),
                new Asset("miniatura-01.webp", 1000),
                new Asset("miniatura-02.webp", 1500),
                new Asset("avatar-default.png", 900),
                new Asset("fondo-player.jpg", 1200)
        );

        // Uso ConcurrentHashMap para guardar los estados sin problemas entre hilos
        ConcurrentHashMap<String, Estado> estados = new ConcurrentHashMap<>();
        // Uso AtomicInteger para contar las completadas sin perder ninguna suma
        AtomicInteger completadas = new AtomicInteger(0);

        // Recorro la lista para poner todos los archivos en PENDIENTE antes de empezar
        for (Asset a : lote) {
            estados.put(a.nombre, Estado.PENDIENTE);
        }

        // Creo mi gestor que va a controlar el límite de 3 plazas
        GestorDescargas gestor = new GestorDescargas(estados);

        // Creo un pool de 7 hilos para lanzar todo a la vez y ver cómo frena el gestor
        ExecutorService pool = Executors.newFixedThreadPool(7);
        // Lista donde voy a ir guardando los Future que me devuelve el pool
        List<Future<String>> futuros = new ArrayList<>();

        System.out.println("=== INICIO DESCARGAS UDITFLIX ===");

        try {
            // Recorro cada archivo del lote para crear su tarea y enviarla al pool
            for (Asset a : lote) {
                // Muestro por consola que envío la tarea
                System.out.println("[ENVÍO] " + a.nombre + " (" + a.duracionMs + " ms)");

                // Uso Callable porque quiero que la tarea me devuelva un texto al acabar
                Callable<String> tarea = () -> {
                    // Esta variable me avisa de si llegué a coger plaza antes de entrar en finally
                    boolean tienePlaza = false;
                    try {
                        // Pido una de las 3 plazas al gestor (si está lleno, espera aquí)
                        gestor.pedirPlaza(a.nombre);
                        // Si paso de esta línea es que ya tengo plaza reservada
                        tienePlaza = true;

                        // Simulo el tiempo que tarda en descargarse el archivo
                        Thread.sleep(a.duracionMs);

                        // Cambio el estado del archivo a DESCARGADO
                        estados.put(a.nombre, Estado.DESCARGADO);
                        // Sumo 1 al contador seguro de descargas terminadas
                        completadas.incrementAndGet();
                        // Devuelvo el texto de resultado para recogerlo luego con Future.get()
                        return a.nombre + " completado";
                    } catch (InterruptedException e) {
                        // Si cortan el hilo, apunto que ha quedado INTERRUMPIDO
                        estados.put(a.nombre, Estado.INTERRUMPIDO);
                        // Vuelvo a marcar el hilo como interrumpido
                        Thread.currentThread().interrupt();
                        // Devuelvo el aviso de interrupción
                        return a.nombre + " interrumpido";
                    } finally {
                        // Suelto la plaza siempre en el finally, pero solo si la llegué a coger
                        if (tienePlaza) {
                            gestor.soltarPlaza(a.nombre);
                        }
                    }
                };

                // Mando la tarea al pool con submit() y guardo su Future en la lista
                futuros.add(pool.submit(tarea));
            }

            System.out.println();

            // Recorro los Future para esperar a que acaben y mostrar su resultado
            for (Future<String> f : futuros) {
                // get() espera a que esa tarea termine y saca el String que devolvió
                System.out.println("[RESULTADO] " + f.get());
            }
        } finally {
            // Cierro el pool para que no acepte más tareas
            pool.shutdown();
            // Espero hasta 10 segundos a que terminen todos los hilos; si no, fuerzo cierre
            if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        }

        // Imprimo el resumen final con todos los datos que piden las pruebas
        System.out.println("\n=== RESUMEN FINAL ===");
        System.out.println("Tareas enviadas: " + lote.size());
        System.out.println("Tareas completadas: " + completadas.get());
        System.out.println("Máximo simultáneo: " + gestor.getMaxActivas());
        System.out.println("Tareas que esperaron: " + gestor.getEnEspera());
        System.out.println("Estados finales: " + estados);
    }
}