package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Esta clase hace de portero: solo deja pasar a 3 descargas a la vez
public class GestorDescargas {

    // Límite máximo de descargas simultáneas que permite UDITflix
    private final int MAX_PLAZAS = 3;

    // Creo el candado con true para que sea justo y respete el orden de llegada
    private final Lock lock = new ReentrantLock(true);
    // Creo la condición asociada al candado para poder dormir y despertar hilos
    private final Condition hayPlaza = lock.newCondition();

    // Contador de cuántas descargas están ocupando plaza ahora mismo
    private int activas = 0;
    // Contador para guardar el récord de descargas simultáneas que hemos llegado a tener
    private int maxActivas = 0;
    // Mapa compartido donde voy apuntando en qué estado está cada archivo
    private final ConcurrentHashMap<String, Estado> estados;
    // Lista para guardar los nombres de los archivos que tuvieron que hacer cola
    private final List<String> enEspera = new ArrayList<>();

    // Constructor: recibo el mapa de estados desde el Main para poder actualizarlo aquí
    public GestorDescargas(ConcurrentHashMap<String, Estado> estados) {
        this.estados = estados;
    }

    // Método al que llama cada hilo antes de empezar a descargar
    public void pedirPlaza(String nombre) throws InterruptedException {
        // Pido el candado permitiendo que el hilo se pueda interrumpir si hace falta
        lock.lockInterruptibly();
        try {
            // Uso este boolean para no apuntar el mismo archivo dos veces en la lista enEspera
            boolean apuntada = false;

            // Miro si las 3 plazas están llenas (uso while y no if por seguridad al despertar)
            while (activas >= MAX_PLAZAS) {
                // Si es la primera vez que entra a esperar, lo apunto y cambio su estado
                if (!apuntada) {
                    apuntada = true;
                    // Guardo su nombre en la lista de los que han esperado
                    enEspera.add(nombre);
                    // Actualizo su estado a EN_ESPERA en el mapa
                    estados.put(nombre, Estado.EN_ESPERA);
                    // Muestro por consola que este archivo se queda esperando
                    System.out.println("[ESPERA] " + nombre + " esperando plaza (" + activas + "/3)");
                }
                // Duermo el hilo y suelto el candado temporalmente hasta que alguien avise
                hayPlaza.await();
            }

            // Si sale del while es que hay hueco, así que puede ocupar una plaza
            activas++;
            // Si se ha superado el máximo anterior, actualizo el récord
            if (activas > maxActivas) {
                maxActivas = activas;
            }

            // Cambio el estado del archivo a DESCARGANDO
            estados.put(nombre, Estado.DESCARGANDO);
            // Muestro por consola que empieza la descarga
            System.out.println("[INICIO] " + nombre + " descargando (" + activas + "/3)");
        } finally {
            // Suelto el candado siempre aquí para que otros hilos puedan entrar al gestor
            lock.unlock();
        }
    }

    // Método al que llama cada hilo cuando ya ha terminado su descarga
    public void soltarPlaza(String nombre) {
        // Pido el candado para restar la plaza sin que nadie me pise
        lock.lock();
        try {
            // Resto 1 porque esta descarga ya ha dejado su hueco libre
            activas--;
            // Muestro por consola que ha terminado y cuántas plazas siguen ocupadas
            System.out.println("[FIN] " + nombre + " termina y libera plaza (" + activas + "/3)");
            // Aviso a todos los hilos que estén dormidos en await() para que entre el siguiente
            hayPlaza.signalAll();
        } finally {
            // Suelto el candado siempre en el finally
            lock.unlock();
        }
    }

    // Método para consultar al final cuál fue el máximo de descargas a la vez
    public int getMaxActivas() {
        // Protejo la lectura con el candado
        lock.lock();
        try {
            // Devuelvo el valor máximo registrado
            return maxActivas;
        } finally {
            // Suelto el candado
            lock.unlock();
        }
    }

    // Método para consultar al final qué tareas tuvieron que esperar
    public List<String> getEnEspera() {
        // Protejo la lectura con el candado
        lock.lock();
        try {
            // Devuelvo una copia de la lista para que no se pueda modificar desde fuera
            return new ArrayList<>(enEspera);
        } finally {
            // Suelto el candado
            lock.unlock();
        }
    }
}