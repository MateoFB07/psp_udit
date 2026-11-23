
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


// ==========================================================
// PÍLDORA 4: EXECUTOR SERVICE, FUTURE, LOCK Y ATOMICINTEGER
// ==========================================================

public class Pildora04_ExecutorFutureLock {


    // ======================================================
    // 1. CONTADOR PROTEGIDO MEDIANTE LOCK
    // ======================================================

    // Creamos una clase interna llamada ContadorConLock.
    // static permite utilizar esta clase interna sin necesitar
    // crear primero un objeto de la clase principal.
    static class ContadorConLock {

        // Creamos un bloqueo utilizando ReentrantLock.
        // final significa que no cambiaremos la referencia.
        private final Lock cerrojo = new ReentrantLock();

        // Variable que almacena el número de tareas.
        private int valor = 0;


        // Método que aumenta el contador en una unidad.
        public void incrementar() {

            // Solicitamos el bloqueo.
            // Si otro hilo lo tiene, esperamos.
            cerrojo.lock();

            try {

                // SECCIÓN CRÍTICA:
                // Solo el hilo que tiene el bloqueo
                // puede modificar esta variable.
                valor++;

            } finally {

                // Liberamos el bloqueo.
                // Se ejecuta incluso si ocurre una excepción.
                cerrojo.unlock();
            }
        }


        // Método que devuelve el valor del contador.
        public int getValor() {

            // También protegemos la lectura del contador.
            cerrojo.lock();

            try {

                // Devolvemos el valor actual.
                return valor;

            } finally {

                // Liberamos siempre el bloqueo.
                cerrojo.unlock();
            }
        }
    }


    // ======================================================
    // 2. GESTOR DE UNA PLAZA CON LOCK Y CONDITION
    // ======================================================

    // Esta clase simula una plaza que solo puede ocupar
    // un usuario a la vez.
    static class GestorDeUnaPlaza {

        // Creamos un bloqueo justo.
        // true solicita una política de acceso equitativa.
        private final Lock lock = new ReentrantLock(true);

        // Creamos una condición asociada al bloqueo.
        // Sirve para que los hilos puedan esperar y recibir avisos.
        private final Condition plazaDisponible = lock.newCondition();

        // false significa que la plaza está libre.
        // true significa que está ocupada.
        private boolean ocupada = false;


        // Método para reservar la plaza.
        // Recibe el nombre del usuario.
        // throws declara que puede producirse una interrupción.
        public void reservar(String tarea) throws InterruptedException {

            // Solicitamos el bloqueo.
            // Puede interrumpirse mientras esperamos conseguirlo.
            lock.lockInterruptibly();

            try {

                // Mientras la plaza esté ocupada,
                // el usuario debe esperar.
                while (ocupada) {

                    System.out.println(
                            tarea + " espera con Condition.await()"
                    );

                    // Esperamos a que alguien libere la plaza.
                    // await() libera temporalmente el Lock.
                    plazaDisponible.await();
                }

                // La plaza está libre.
                // Ahora la marcamos como ocupada.
                ocupada = true;

                // Informamos de que el usuario ha conseguido la plaza.
                System.out.println(tarea + " obtiene la plaza.");

            } finally {

                // Liberamos el bloqueo.
                // La plaza sigue ocupada, pero ya no mantenemos
                // el Lock mientras el usuario utiliza la plaza.
                lock.unlock();
            }
        }


        // Método para liberar la plaza.
        public void liberar(String tarea) {

            // Solicitamos el bloqueo.
            lock.lock();

            try {

                // Marcamos la plaza como libre.
                ocupada = false;

                // Mostramos un mensaje.
                System.out.println(
                        tarea + " libera la plaza y avisa."
                );

                // Avisamos a todos los hilos que esperan
                // sobre esta condición.
                plazaDisponible.signalAll();

            } finally {

                // Liberamos el bloqueo.
                lock.unlock();
            }
        }
    }


    // ======================================================
    // 3. MÉTODO PRINCIPAL
    // ======================================================

    public static void main(String[] args) throws Exception {


        // ==================================================
        // 4. CREAMOS EL GRUPO DE TRABAJADORES
        // ==================================================

        // Creamos un grupo de tres hilos.
        // Se podrán ejecutar como máximo tres tareas
        // simultáneamente dentro de este grupo.
        ExecutorService pool =
                Executors.newFixedThreadPool(3);


        // Creamos una lista para guardar los resultados.
        // Cada Future representa una tarea enviada.
        List<Future<String>> resultados = new ArrayList<>();


        // Contador que registra las tareas que han terminado.
        AtomicInteger terminadas = new AtomicInteger(0);


        // Mapa que guarda el estado de cada tráiler.
        // La clave será el nombre y el valor será su estado.
        ConcurrentHashMap<String, String> estados =
                new ConcurrentHashMap<>();


        // Creamos nuestro contador protegido mediante Lock.
        ContadorConLock contador = new ContadorConLock();


        // Creamos un array con los cuatro tráileres.
        String[] tareas = {
                "TRAILER-01",
                "TRAILER-02",
                "TRAILER-03",
                "TRAILER-04"
        };


        // ==================================================
        // 5. ENVIAMOS LAS TAREAS AL GRUPO DE TRABAJADORES
        // ==================================================

        try {

            // Recorremos todos los nombres de los tráileres.
            for (String nombre : tareas) {

                // Creamos una tarea Callable.
                // Callable permite ejecutar código y devolver
                // un resultado de tipo String.
                Callable<String> trabajo = () -> {

                    // Guardamos que el tráiler está en proceso.
                    estados.put(nombre, "EN_PROCESO");


                    // Aumentamos el contador de tareas iniciadas.
                    contador.incrementar();


                    // Mostramos qué hilo está procesando el tráiler.
                    System.out.println(
                            "[" + Thread.currentThread().getName()
                                    + "] Procesando " + nombre
                    );


                    // Simulamos que procesar el tráiler
                    // requiere 700 milisegundos.
                    Thread.sleep(700);


                    // Cambiamos el estado del tráiler.
                    estados.put(nombre, "COMPLETADA");


                    // Aumentamos el contador de tareas terminadas.
                    // incrementAndGet() suma uno y devuelve el resultado.
                    terminadas.incrementAndGet();


                    // Devolvemos un mensaje como resultado.
                    return nombre + " procesado correctamente";
                };


                // Enviamos la tarea al grupo de trabajadores.
                // submit() devuelve un Future con su resultado.
                resultados.add(pool.submit(trabajo));
            }


            // ==================================================
            // 6. RECOGEMOS LOS RESULTADOS DE LAS TAREAS
            // ==================================================

            // Recorremos todos los Future guardados.
            for (Future<String> futuro : resultados) {

                // get() espera hasta que la tarea correspondiente
                // termine y devuelve su resultado.
                System.out.println(
                        "[RESULTADO] " + futuro.get()
                );
            }


        } finally {

            // ==================================================
            // 7. CERRAMOS EL GRUPO DE TRABAJADORES
            // ==================================================

            // Indicamos que no se aceptarán nuevas tareas.
            // Las tareas ya enviadas pueden terminar.
            pool.shutdown();


            // Esperamos un máximo de cinco segundos
            // para que termine el grupo de trabajadores.
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {

                // Si no termina a tiempo, solicitamos
                // detener las tareas que estén ejecutándose.
                pool.shutdownNow();
            }
        }


        // ==================================================
        // 8. MOSTRAMOS LOS RESULTADOS
        // ==================================================

        // Mostramos el número de tareas que comenzaron.
        System.out.println(
                "Tareas enviadas: " + contador.getValor()
        );


        // Mostramos cuántas tareas han terminado.
        System.out.println(
                "Tareas terminadas: " + terminadas.get()
        );


        // Mostramos el mapa con los estados de los tráileres.
        System.out.println(
                "Estados: " + estados
        );


        // ==================================================
        // 9. SEGUNDA DEMOSTRACIÓN: LOCK Y CONDITION
        // ==================================================

        System.out.println(
                "\n===== EQUIVALENCIA Lock + Condition ====="
        );


        // Creamos un gestor que controla una única plaza.
        GestorDeUnaPlaza gestorPlaza =
                new GestorDeUnaPlaza();


        // Creamos una tarea que podrán ejecutar dos usuarios.
        Runnable usoPlaza = () -> {

            // Obtenemos el nombre del hilo actual.
            String nombre = Thread.currentThread().getName();


            // Esta variable indica si el usuario
            // ha conseguido reservar la plaza.
            boolean reservada = false;


            try {

                // Intentamos reservar la plaza.
                gestorPlaza.reservar(nombre);


                // Si hemos conseguido reservarla,
                // cambiamos el valor a true.
                reservada = true;


                // Simulamos que el usuario utiliza la plaza
                // durante 500 milisegundos.
                Thread.sleep(500);


            } catch (InterruptedException e) {

                // Si el hilo es interrumpido,
                // recuperamos su estado de interrupción.
                Thread.currentThread().interrupt();


            } finally {

                // Si consiguió reservar la plaza,
                // tiene que liberarla.
                if (reservada) {

                    gestorPlaza.liberar(nombre);
                }
            }
        };


        // Creamos el primer usuario.
        Thread usuario1 =
                new Thread(usoPlaza, "USUARIO-1");


        // Creamos el segundo usuario.
        Thread usuario2 =
                new Thread(usoPlaza, "USUARIO-2");


        // Ponemos en marcha al primer usuario.
        usuario1.start();


        // Ponemos en marcha al segundo usuario.
        usuario2.start();


        // Esperamos a que termine el primer usuario.
        usuario1.join();


        // Esperamos a que termine el segundo usuario.
        usuario2.join();
    }
}