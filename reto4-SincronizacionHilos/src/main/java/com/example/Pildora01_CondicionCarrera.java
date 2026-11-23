package org.example;

// ======================================================
// PÍLDORA 1: CONDICIÓN DE CARRERA Y SYNCHRONIZED
// ======================================================
//
// OBJETIVO:
// Aprender qué ocurre cuando varios hilos modifican
// una misma variable al mismo tiempo.
//
// Primero veremos el problema.
// Después veremos cómo solucionarlo con synchronized.

public class Pildora01_CondicionCarrera {

    // ==================================================
    // 1. CLASE DEL CONTADOR COMPARTIDO
    // ==================================================

    // Creamos una clase interna llamada ContadorCompartido.
    // Su función es guardar un número que podrán utilizar
    // varios hilos al mismo tiempo.
    static class ContadorCompartido {

        // Creamos una variable entera llamada valor.
        // private significa que solo podemos acceder
        // directamente a ella desde esta clase.
        // Inicialmente su valor es 0.
        private int valor = 0;


        // ==================================================
        // 2. MÉTODO INCORRECTO: SIN PROTECCIÓN
        // ==================================================

        // public: este método puede utilizarse desde fuera.
        // void: no devuelve ningún resultado.
        // incrementarSinProteccion: nombre del método.
        //
        // IMPORTANTE:
        // No utilizamos synchronized.
        // Por tanto, varios hilos pueden entrar a la vez.
        public void incrementarSinProteccion() {

            // Guardamos el valor actual del contador
            // en una variable local llamada copiaLocal.
            //
            // Ejemplo: si valor es 0, copiaLocal será 0.
            int copiaLocal = valor;

            // Abrimos un bloque try para controlar posibles
            // interrupciones mientras el hilo está esperando.
            try {

                // Hacemos que el hilo espere 100 milisegundos.
                // Esto sirve para que sea más fácil observar
                // el problema durante la demostración.
                Thread.sleep(100);

            } catch (InterruptedException e) {

                // Si el hilo es interrumpido, recuperamos
                // su estado de interrupción.
                Thread.currentThread().interrupt();

                // Salimos del método sin modificar el contador.
                return;
            }

            // Sumamos 1 a la copia local y guardamos
            // el resultado en la variable compartida valor.
            //
            // PROBLEMA:
            // Otro hilo podría haber leído el mismo valor
            // y escribir también su propio resultado.
            valor = copiaLocal + 1;
        }


        // ==================================================
        // 3. MÉTODO CORRECTO: CON SYNCHRONIZED
        // ==================================================

        // public: el método puede utilizarse desde fuera.
        // synchronized: permite que solo un hilo a la vez
        // ejecute este método sobre el mismo objeto.
        // void: no devuelve ningún resultado.
        public synchronized void incrementarSeguro() {

            // Incrementamos el valor del contador en 1.
            //
            // Esta operación queda protegida por synchronized.
            valor++;
        }


        // ==================================================
        // 4. MÉTODO PARA CONSULTAR EL CONTADOR
        // ==================================================

        // public: se puede llamar desde fuera de la clase.
        // int: devuelve un número entero.
        // getValor: nombre del método.
        public int getValor() {

            // Devolvemos el valor actual del contador.
            return valor;
        }
    }


    // ======================================================
    // 5. MÉTODO PRINCIPAL: MAIN
    // ======================================================

    // public: puede ejecutarse desde fuera.
    // static: pertenece a la clase y no necesitamos
    // crear un objeto para ejecutarlo.
    // void: no devuelve ningún resultado.
    // main: punto de entrada del programa.
    // String[] args: permite recibir argumentos externos.
    //
    // throws InterruptedException:
    // indica que el método principal puede propagar
    // una excepción de interrupción.
    public static void main(String[] args) throws InterruptedException {

        // Mostramos un título en la consola.
        System.out.println("===== DEMO 1: CONDICIÓN DE CARRERA =====");


        // ==================================================
        // 6. PRIMERA DEMOSTRACIÓN: EL PROBLEMA
        // ==================================================

        // Creamos un objeto contador.
        // Su valor inicial es 0.
        ContadorCompartido contadorIncorrecto =
                new ContadorCompartido();


        // Creamos el primer hilo.
        //
        // Thread representa un hilo de ejecución.
        // new Thread crea un nuevo hilo.
        //
        // contadorIncorrecto::incrementarSinProteccion
        // indica qué método ejecutará el hilo.
        //
        // "HILO-A" es el nombre que le damos.
        Thread hiloA = new Thread(
                contadorIncorrecto::incrementarSinProteccion,
                "HILO-A"
        );


        // Creamos el segundo hilo.
        // También utilizará el mismo objeto contador.
        // Por eso los dos hilos comparten la variable valor.
        Thread hiloB = new Thread(
                contadorIncorrecto::incrementarSinProteccion,
                "HILO-B"
        );


        // start() inicia la ejecución del primer hilo.
        hiloA.start();

        // start() inicia la ejecución del segundo hilo.
        hiloB.start();


        // join() hace que el programa principal espere
        // hasta que termine el hilo A.
        hiloA.join();

        // Esperamos también a que termine el hilo B.
        hiloB.join();


        // Mostramos cuál debería ser el resultado correcto.
        // Cada hilo suma 1, por tanto esperamos un 2.
        System.out.println("Resultado esperado: 2");


        // Mostramos el resultado que realmente conseguimos.
        System.out.println(
                "Resultado sin protección: "
                        + contadorIncorrecto.getValor()
        );


        // Explicamos que si aparece 1, se ha perdido
        // una de las dos actualizaciones.
        System.out.println(
                "Si aparece 1, se ha perdido una actualización.\n"
        );


        // ==================================================
        // 7. SEGUNDA DEMOSTRACIÓN: LA SOLUCIÓN
        // ==================================================

        // Mostramos el título de la segunda demostración.
        System.out.println(
                "===== DEMO 2: SOLUCIÓN CON synchronized ====="
        );


        // Creamos un nuevo contador.
        // Este comienza otra vez desde 0.
        ContadorCompartido contadorSeguro =
                new ContadorCompartido();


        // Creamos una tarea que ejecutarán los dos hilos.
        //
        // Runnable representa una tarea que puede ejecutar
        // un hilo. No devuelve ningún resultado.
        //
        // () -> indica que estamos utilizando una expresión
        // lambda, es decir, una forma abreviada de escribir
        // una tarea.
        Runnable tarea = () -> {

            // Creamos un bucle que se repetirá 100.000 veces.
            //
            // int i = 0: comenzamos desde cero.
            // i < 100_000: repetimos mientras i sea menor.
            // i++: aumentamos i en uno en cada repetición.
            for (int i = 0; i < 100_000; i++) {

                // En cada repetición llamamos al método seguro.
                // synchronized impide que los dos hilos
                // ejecuten este método a la vez
                // sobre el mismo objeto.
                contadorSeguro.incrementarSeguro();
            }
        };


        // Creamos el tercer hilo y le asignamos la tarea.
        Thread hiloC = new Thread(tarea, "HILO-C");

        // Creamos el cuarto hilo con la misma tarea.
        Thread hiloD = new Thread(tarea, "HILO-D");


        // Iniciamos el tercer hilo.
        hiloC.start();

        // Iniciamos el cuarto hilo.
        hiloD.start();


        // Esperamos a que termine el hilo C.
        hiloC.join();

        // Esperamos a que termine el hilo D.
        hiloD.join();


        // Mostramos el resultado esperado.
        //
        // 100.000 incrementos del hilo C
        // + 100.000 incrementos del hilo D
        // = 200.000.
        System.out.println("Resultado esperado: 200000");


        // Mostramos el resultado real del contador protegido.
        System.out.println(
                "Resultado con synchronized: "
                        + contadorSeguro.getValor()
        );


        // Explicamos que synchronized protege
        // la parte del programa donde se modifica el contador.
        System.out.println(
                "La sección crítica queda protegida "
                        + "por el monitor del objeto."
        );
    }
}