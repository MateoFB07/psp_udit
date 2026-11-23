
public class Pildora03_Interbloqueo {

    // ======================================================
    // 1. CREAMOS LOS DOS RECURSOS COMPARTIDOS
    // ======================================================

    // Creamos un objeto que representa el recurso CATÁLOGO.
    //
    // private: solo se puede acceder directamente desde esta clase.
    // final: la referencia al objeto no se puede cambiar.
    // Object: es un objeto genérico de Java.
    // new Object(): crea un objeto nuevo.
    //
    // Este objeto servirá como bloqueo para controlar
    // quién puede utilizar el catálogo.
    private final Object recursoCatalogo = new Object();


    // Creamos otro objeto que representa el recurso ALMACÉN.
    //
    // También se utilizará como bloqueo.
    // Los hilos necesitarán conseguir este recurso
    // para poder realizar su trabajo.
    private final Object recursoAlmacen = new Object();


    // ======================================================
    // 2. MÉTODO QUE PROCESA LOS RECURSOS EN ORDEN SEGURO
    // ======================================================

    // private: este método solo se utiliza dentro de esta clase.
    // void: no devuelve ningún resultado.
    // procesarConOrdenSeguro: nombre del método.
    // String nombreTarea: recibe el nombre de la tarea
    // que está realizando el trabajo.
    private void procesarConOrdenSeguro(String nombreTarea) {

        // Entramos en un bloque synchronized utilizando
        // el recurso CATÁLOGO como objeto de bloqueo.
        //
        // Solo un hilo puede tener este monitor a la vez.
        synchronized (recursoCatalogo) {

            // Mostramos que la tarea ha conseguido el catálogo.
            System.out.println(nombreTarea + " obtiene CATALOGO");


            // Hacemos una pequeña pausa para simular
            // que la tarea está utilizando el catálogo.
            pausaCorta();


            // Ahora intentamos conseguir el segundo recurso:
            // el ALMACÉN.
            //
            // IMPORTANTE:
            // Primero conseguimos el catálogo y después
            // el almacén. Todas las tareas siguen este orden.
            synchronized (recursoAlmacen) {

                // Mostramos que la tarea ha conseguido
                // los dos recursos y puede trabajar.
                System.out.println(
                        nombreTarea + " obtiene ALMACEN y trabaja"
                );


                // Simulamos que la tarea está trabajando
                // utilizando ambos recursos.
                pausaCorta();

            } // Aquí se libera automáticamente el monitor
            // del recurso ALMACÉN al salir del bloque.
        } // Aquí se libera automáticamente el monitor
        // del recurso CATÁLOGO al salir del bloque.


        // Cuando termina de utilizar los dos recursos,
        // mostramos que la tarea ha finalizado.
        System.out.println(nombreTarea + " termina.");
    }


    // ======================================================
    // 3. MÉTODO PARA REALIZAR UNA PAUSA
    // ======================================================

    // private: solo se utiliza dentro de esta clase.
    // static: pertenece a la clase y no necesita un objeto
    // para poder ejecutarse.
    // void: no devuelve ningún resultado.
    // pausaCorta: nombre del método.
    private static void pausaCorta() {

        // try permite ejecutar una instrucción
        // que puede producir una excepción.
        try {

            // Hacemos que el hilo espere 300 milisegundos.
            //
            // Esta pausa simula el tiempo que necesita
            // una tarea para utilizar un recurso.
            //
            // También facilita observar el comportamiento
            // de los dos hilos durante la demostración.
            Thread.sleep(300);

        } catch (InterruptedException e) {

            // Si el hilo es interrumpido,
            // recuperamos su estado de interrupción.
            Thread.currentThread().interrupt();
        }
    }


    // ======================================================
    // 4. MÉTODO PRINCIPAL: MAIN
    // ======================================================

    // Es el punto de entrada del programa.
    //
    // throws InterruptedException:
    // permite propagar una posible excepción de interrupción
    // producida al utilizar join().
    public static void main(String[] args)
            throws InterruptedException {


        // ==================================================
        // 5. CREAMOS EL OBJETO PRINCIPAL
        // ==================================================

        // Creamos un objeto de nuestra clase.
        //
        // Este objeto contiene los dos recursos:
        // catálogo y almacén.
        Pildora03_Interbloqueo demo =
                new Pildora03_Interbloqueo();


        // Mostramos un mensaje en la consola.
        System.out.println(
                "SOLUCIÓN SEGURA: mismo orden de adquisición"
        );


        // ==================================================
        // 6. CREAMOS LA PRIMERA TAREA
        // ==================================================

        // Creamos un hilo llamado tareaA.
        //
        // Cuando se ejecute, llamará al método
        // procesarConOrdenSeguro().
        //
        // Le pasamos el nombre TAREA-A para identificarlo.
        Thread tareaA = new Thread(
                () -> demo.procesarConOrdenSeguro("TAREA-A")
        );


        // ==================================================
        // 7. CREAMOS LA SEGUNDA TAREA
        // ==================================================

        // Creamos otro hilo llamado tareaB.
        //
        // Ejecutará exactamente el mismo método que tareaA.
        // La diferencia es que recibirá el nombre TAREA-B.
        Thread tareaB = new Thread(
                () -> demo.procesarConOrdenSeguro("TAREA-B")
        );


        // ==================================================
        // 8. INICIAMOS LOS DOS HILOS
        // ==================================================

        // Iniciamos la primera tarea.
        tareaA.start();

        // Iniciamos la segunda tarea.
        tareaB.start();


        // ==================================================
        // 9. ESPERAMOS A QUE TERMINEN
        // ==================================================

        // El programa principal espera a que termine tareaA.
        tareaA.join();

        // Después espera a que termine tareaB.
        tareaB.join();


        // ==================================================
        // 10. EXPLICAMOS EL CONCEPTO DE INTERBLOQUEO
        // ==================================================

        // Mostramos un título sobre el interbloqueo.
        System.out.println(
                "\nIDEA DEL INTERBLOQUEO (NO ejecutar esta variante):"
        );


        // Explicamos la situación peligrosa.
        // En este ejemplo imaginario, A consigue primero
        // el catálogo y después espera el almacén.
        System.out.println(
                "Tarea A: obtiene Catálogo y espera Almacén."
        );


        // B consigue primero el almacén y después
        // espera el catálogo.
        System.out.println(
                "Tarea B: obtiene Almacén y espera Catálogo."
        );


        // Cada tarea conserva un recurso que necesita la otra.
        // Ninguna puede continuar.
        System.out.println(
                "Cada tarea retiene un recurso que la otra necesita: "
                        + "espera circular."
        );


        // Explicamos la solución utilizada en el programa.
        // Ambas tareas adquieren primero el catálogo
        // y después el almacén.
        System.out.println(
                "Solución aplicada: ambas adquieren Catálogo -> Almacén."
        );


        // ==================================================
        // 11. EXPLICAMOS EL CONCEPTO DE INANICIÓN
        // ==================================================

        // Mostramos un título sobre la inanición.
        System.out.println("\nINANICIÓN:");


        // Explicamos qué sucede cuando un hilo
        // no consigue acceder a un recurso durante mucho tiempo.
        System.out.println(
                "Una tarea puede esperar mucho tiempo si otras "
                        + "obtienen repetidamente el recurso."
        );


        // synchronized no garantiza que los hilos
        // entren en el mismo orden en el que llegaron.
        System.out.println(
                "synchronized no garantiza un orden FIFO de entrada."
        );


        // ReentrantLock(true) permite utilizar una política
        // de acceso más justa para los hilos que esperan.
        System.out.println(
                "ReentrantLock(true) permite solicitar "
                        + "una política de acceso más justa,"
        );


        // Aunque utilicemos una política justa,
        // el sistema operativo sigue influyendo
        // en qué hilo consigue ejecutarse.
        System.out.println(
                "aunque el planificador del sistema operativo "
                        + "sigue influyendo."
        );
    }
}