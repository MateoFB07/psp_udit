package com.example;

// Clase sencilla para guardar juntos el nombre y el tiempo de cada archivo
public class Asset {
    // Guardo el nombre del archivo (por ejemplo "portada-home.webp")
    String nombre;
    // Guardo cuánto tarda en bajar en milisegundos
    int duracionMs;

    // Constructor para crear cada archivo con sus dos datos
    public Asset(String nombre, int duracionMs) {
        // Asigno el nombre que me pasan al atributo de la clase
        this.nombre = nombre;
        // Asigno la duración que me pasan al atributo de la clase
        this.duracionMs = duracionMs;
    }
}