
public class Pildora02_WaitNotify {

    // ======================================================
    // 1. CLASE DEL BUFFER (ESPACIO COMPARTIDO)
    // ======================================================

    // Creamos una clase llamada BufferUnElemento.
    // Representa una caja que solamente puede guardar
    // un dato a la vez.
    static class BufferUnElemento {

        // Creamos una variable Integer llamada dato.
        // Integer permite guardar números enteros o null.
        //
        // null significa que el buffer está vacío.
        // Si tiene un número, significa que está ocupado.
        private Integer dato = null;


        // ==================================================
        // 2. MÉTODO DEL PRODUCTOR: PONER UN DATO
        // ==================================================

        // public: se puede llamar desde otras clases.
        // synchronized: solo un hilo puede ejecutar
        // un método sincronizado de este mismo objeto a la vez.
        // void: no devuelve ningún resultado.
        // poner: nombre del método.
        // int nuevoDato: número que queremos introducir.
        //
        // throws InterruptedException:
        // indica que este método puede producir una excepción
        // si el hilo es interrumpido mientras espera.
        public synchronized void poner(int nuevoDato)
                throws InterruptedException {

            // Comprobamos si el buffer ya contiene un dato.
            //
            // Si dato no es null, significa que está lleno.
            // Mientras esté lleno, el productor debe esperar.
            //
            // Utilizamos while porque debemos volver a
            // comprobar la condición después de despertarnos.
            while (dato != null) {

                // Mostramos un mensaje indicando que
                // el productor no puede introducir otro dato.
                System.out.println(
                        "[PRODUCTOR] Buffer lleno; espera..."
                );

                // wait() hace que el productor espere.
                //
                // IMPORTANTE:
                // Libera temporalmente el monitor del objeto
                // para que otro hilo pueda entrar.
                // Cuando recibe una notificación, debe volver
                // a conseguir el monitor antes de continuar.
                wait();
            }


            // Si llegamos aquí, el buffer está vacío.
            // Guardamos el nuevo número en el buffer.
            dato = nuevoDato;


            // Mostramos qué número acaba de producirse.
            System.out.println(
                    "[PRODUCTOR] Produce: " + nuevoDato
            );


            // Avisamos a todos los hilos que estén esperando
            // sobre este mismo objeto.
            //
            // Por ejemplo, avisamos al consumidor de que
            // ahora hay un dato disponible.
            notifyAll();
        }


        // ==================================================
        // 3. MÉTODO DEL CONSUMIDOR: RECOGER UN DATO
        // ==================================================

        // public: se puede llamar desde otras clases.
        // synchronized: protege el acceso al buffer.
        // int: este método devuelve un número entero.
        // recoger: nombre del método.
        //
        // throws InterruptedException:
        // indica que puede producirse una interrupción
        // mientras el consumidor está esperando.
        public synchronized int recoger()
                throws InterruptedException {

            // Comprobamos si el buffer está vacío.
            //
            // Si dato es null, no hay ningún número
            // que el consumidor pueda recoger.
            //
            // Por eso, mientras esté vacío, debe esperar.
            while (dato == null) {

                // Mostramos un mensaje indicando
                // que el consumidor no puede continuar.
                System.out.println(
                        "[CONSUMIDOR] Buffer vacío; espera..."
                );

                // El consumidor queda esperando.
                // Libera el monitor para que el productor
                // pueda introducir un dato.
                wait();
            }


            // Si llegamos aquí, el buffer contiene un dato.
            // Guardamos ese dato en una variable local.
            //
            // Ejemplo: si dato vale 5, valorRecogido será 5.
            int valorRecogido = dato;


            // Vaciamos el buffer.
            // Ahora vuelve a estar disponible para el productor.
            dato = null;


            // Mostramos el número que acaba de consumir.
            System.out.println(
                    "[CONSUMIDOR] Consume: " + valorRecogido
            );


            // Avisamos a los hilos que están esperando.
            // En este caso, el productor puede continuar
            // porque el buffer vuelve a estar vacío.
            notifyAll();


            // Devolvemos el número que hemos recogido.
            return valorRecogido;
        }
    }


    // ======================================================
    // 4. MÉTODO PRINCIPAL: MAIN
    // ======================================================

    // Es el punto de entrada del programa.
    // Aquí creamos el buffer y los dos hilos.
    public static void main(String[] args)
            throws InterruptedException {


        // ==================================================
        // 5. CREAMOS EL BUFFER COMPARTIDO
        // ==================================================

        // Creamos un objeto de tipo BufferUnElemento.
        //
        // Este será el espacio compartido que utilizarán
        // tanto el productor como el consumidor.
        BufferUnElemento buffer = new BufferUnElemento();


        // ==================================================
        // 6. CREAMOS EL HILO CONSUMIDOR
        // ==================================================

        // Creamos un hilo llamado CONSUMIDOR.
        //
        // Su trabajo será recoger los números del buffer.
        Thread consumidor = new Thread(() -> {

            // try permite controlar posibles interrupciones.
            try {

                // Creamos un bucle infinito.
                // El consumidor seguirá recogiendo datos
                // hasta que reciba la señal de finalización.
                while (true) {

                    // Llamamos al método recoger().
                    // El consumidor obtiene el dato del buffer.
                    int valor = buffer.recoger();


                    // Comprobamos si el valor recibido es -1.
                    //
                    // Hemos decidido utilizar -1 como señal
                    // especial para indicar que el productor
                    // ha terminado de fabricar productos.
                    if (valor == -1) {

                        // Mostramos que el consumidor ha terminado.
                        System.out.println(
                                "[CONSUMIDOR] Fin de consumo."
                        );

                        // break finaliza el bucle while.
                        break;
                    }


                    // Simulamos que el consumidor necesita
                    // 250 milisegundos para procesar el producto.
                    //
                    // Durante esta pausa no está utilizando
                    // el método recoger().
                    Thread.sleep(250);
                }

            } catch (InterruptedException e) {

                // Si el hilo es interrumpido,
                // recuperamos su estado de interrupción.
                Thread.currentThread().interrupt();
            }

        }, "CONSUMIDOR");


        // ==================================================
        // 7. CREAMOS EL HILO PRODUCTOR
        // ==================================================

        // Creamos otro hilo llamado PRODUCTOR.
        //
        // Su trabajo será introducir números en el buffer.
        Thread productor = new Thread(() -> {

            // Controlamos posibles interrupciones.
            try {

                // Creamos un bucle que empieza en 1
                // y termina en 5, ambos incluidos.
                //
                // El productor fabricará cinco números:
                // 1, 2, 3, 4 y 5.
                for (int i = 1; i <= 5; i++) {

                    // Introducimos el número actual en el buffer.
                    // Si está lleno, el productor tendrá que esperar.
                    buffer.poner(i);


                    // Simulamos que el productor tarda
                    // 150 milisegundos en preparar otro producto.
                    Thread.sleep(150);
                }


                // Cuando termina de producir los cinco números,
                // introduce -1 para avisar al consumidor
                // de que ya no hay más productos.
                buffer.poner(-1);

            } catch (InterruptedException e) {

                // Si el productor es interrumpido,
                // recuperamos su estado de interrupción.
                Thread.currentThread().interrupt();
            }

        }, "PRODUCTOR");


        // ==================================================
        // 8. INICIAMOS LOS DOS HILOS
        // ==================================================

        // Iniciamos el hilo consumidor.
        consumidor.start();

        // Iniciamos el hilo productor.
        productor.start();


        // ==================================================
        // 9. ESPERAMOS A QUE TERMINEN
        // ==================================================

        // El programa principal espera a que termine
        // completamente el productor.
        productor.join();

        // Después espera a que termine el consumidor.
        consumidor.join();


        // ==================================================
        // 10. MOSTRAMOS EL MENSAJE FINAL
        // ==================================================

        // Cuando los dos hilos han terminado,
        // mostramos este mensaje en la consola.
        System.out.println(
                "[MAIN] Productor y consumidor han terminado."
        );
    }
}