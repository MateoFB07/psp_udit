package org.example;

public class Episodio {

    // ATRIBUTOS: Variables privadas por seguridad (encapsulamiento).
    // Sólo esta clase puede modificar directamente.
    private String titulo;
    private int duracion;

    // CONSTRUCTOR: El molde que ejecuta al hacer un "new Episodio".
    public Episodio(String titulo, int duracion) {
        this.titulo = titulo;
        this.duracion = duracion;
    }

    // MÉTODOS:
    public void procesar() {
        System.out.println("Iniciando procesamiento de: " + this.titulo + "...");

        // El bloque try-catch es OBLIGATORIO en Java al pausar el hilo (Thread).
        // Evita que el programa explote si se interrumpe la pausa.
        try {
            // Pausamos la ejecución en 2000 milisegundos (2 seg).
            Thread.sleep(2000);
            System.out.println("Procesado con éxito: " + this.titulo);
        } catch (InterruptedException e) {
            System.out.println("Error al procesar episodio: " + this.titulo);
        }
    } // Cierra el método procesar()

} // Cierra la clase Episodio