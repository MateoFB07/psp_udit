package org.example;

public class Episodio {

    // ATRIBUTOS
    private String titulo;
    private int duracion;

    // CONSTRUCTOR
    public Episodio(String titulo, int duracion) {
        this.titulo = titulo;
        this.duracion = duracion;
    }

    // MÉTODOS
    public void procesar() {
        System.out.println("Iniciando procesamiento de: " + this.titulo + "...");

        try {
            Thread.sleep(2000); // Pausa simulada de 2 segundos
            System.out.println("Procesado con éxito: " + this.titulo);
        } catch (InterruptedException e) {
            System.out.println("Error al procesar episodio: " + this.titulo);
        }
    }
}