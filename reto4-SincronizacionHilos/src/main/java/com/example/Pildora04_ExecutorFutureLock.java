package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// ==========================================================
// PÍLDORA 4 ADAPTADA: GESTOR DE 3 PLAZAS CON LOCK Y CONDITION
// (REQUISITOS B2, B4 Y B5 DE LA PRUEBA B)
// ==========================================================

public class Pildora04_ExecutorFutureLock {

    // Estados posibles de cada asset (Requisito B4)
    public enum EstadoDescarga {
        PENDIENTE,
        EN_ESPERA,
        DESCARGANDO,
        DESCARGADO,
        INTERRUMPIDO
    }

    // Clase modelo para cada recurso gráfico del lote (Requisito B1)
    public static class Asset {
        private final String nombre;
        private final int duracionMs;

        public Asset(String nombre, int duracionMs) {
            this.nombre = nombre;
            this.duracionMs = duracionMs;
        }

        public String getNombre() {
            return nombre;
        }

        public int getDuracionMs() {
            return duracionMs;
        }
    }

    // ======================================================
    // GESTOR DE RECURSO COMPARTIDO (MÁXIMO 3 PLAZAS)
    // ======================================================
    public static class GestorDeTresPlazas {

        // Límite de capacidad exigido por UDITflix: 3 descargas simultáneas
        private static final int MAX_PLAZAS = 3;

        // Creamos un bloqueo justo: true solicita política FIFO (Requisito B2)
        private final Lock lock = new ReentrantLock(true);

        // Creamos una condición asociada al bloqueo
        private final Condition plazaDisponible = lock.newCondition();

        // Contador de plazas ocupadas actualmente (nunca puede pasar de 3)
        private int plazasOcupadas = 0;

        // Registro del máximo simultáneo observado (Requisito B4 y B5)
        private int maximoSimultaneo = 0;

        // Mapa concurrente de estados y lista de tareas que tuvieron que esperar
        private final ConcurrentHashMap<String, EstadoDescarga> estados;
        private final List<String> tareasEnEspera =
                Collections.synchronizedList(new ArrayList<>());

        public GestorDeTresPlazas(ConcurrentHashMap<String, EstadoDescarga> estados) {
            this.estados = estados;
        }

        // Método para reservar una de las 3 plazas
        public void reservar(String nombreAsset) throws InterruptedException {

            // Solicitamos el bloqueo de forma interrumpible
            lock.lockInterruptibly();

            try {
                boolean haEsperado = false;

                // Mientras las 3 plazas estén ocupadas, la tarea debe esperar.
                // Comprobamos la condición siempre con while, nunca con if.
                while (plazasOcupadas >= MAX_PLAZAS) {
                    if (!haEsperado) {
                        haEsperado = true;
                        tareasEnEspera.add(nombreAsset);
                        estados.put(nombreAsset, EstadoDescarga.EN_ESPERA);
                        System.out.println("[ESPERA] " + nombreAsset
                                + " espera con Condition.await() ("
                                + plazasOcupadas + "/" + MAX_PLAZAS + " ocupadas)");
                    }

                    // Esperamos a que se libere una plaza (libera el Lock temporalmente)
                    plazaDisponible.await();
                }

                // Hay plaza libre: la ocupamos
                plazasOcupadas++;
                if (plazasOcupadas > maximoSimultaneo) {
                    maximoSimultaneo = plazasOcupadas;
                }

                estados.put(nombreAsset, EstadoDescarga.DESCARGANDO);
                System.out.println("[INICIO] " + nombreAsset
                        + " obtiene plaza y descarga ("
                        + plazasOcupadas + "/" + MAX_PLAZAS + " activas)");

            } finally {
                // Liberamos siempre el Lock en finally
                lock.unlock();
            }
        }

        // Método para liberar la plaza al terminar la descarga
        public void liberar(String nombreAsset) {

            lock.lock();

            try {
                // Marcamos una plaza menos como ocupada
                plazasOcupadas--;

                System.out.println("[LIBERA] " + nombreAsset
                        + " libera su plaza y avisa ("
                        + plazasOcupadas + "/" + MAX_PLAZAS + " activas)");

                // Avisamos a todos los hilos que esperan sobre esta condición
                plazaDisponible.signalAll();

            } finally {
                // Liberamos siempre el Lock en finally
                lock.unlock();
            }
        }

        public int getMaximoSimultaneo() {
            lock.lock();
            try {
                return maximoSimultaneo;
            } finally {
                lock.unlock();
            }
        }

        public List<String> getTareasEnEspera() {
            return tareasEnEspera;
        }
    }
}