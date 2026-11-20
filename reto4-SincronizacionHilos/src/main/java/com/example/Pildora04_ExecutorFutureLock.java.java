package com.example;

// ======================================================
// CLASE MODELO: ASSET GRÁFICO (REQUISITO B1)
// ======================================================
//
// OBJETIVO:
// Representar cada recurso gráfico de UDITflix con su
// nombre de archivo y el tiempo simulado de descarga.

public class Asset {

    // private: solo se puede acceder desde esta clase.
    // final: una vez asignado en el constructor, no cambia.
    // String: guarda el nombre del archivo (ej. "portada-home.webp").
    private final String nombre;

    // int: guarda la duración simulada de descarga en milisegundos.
    private final int duracionMs;


    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    // public: permite crear objetos Asset desde otras clases.
    // Recibe el nombre del recurso y su duración en ms.
    public Asset(String nombre, int duracionMs) {

        // Asignamos los parámetros recibidos a los atributos
        // de este objeto usando this.
        this.nombre = nombre;
        this.duracionMs = duracionMs;
    }


    // ==================================================
    // MÉTODOS GETTER (CONSULTA DE DATOS)
    // ==================================================

    // Devuelve el nombre del asset.
    public String getNombre() {
        return nombre;
    }

    // Devuelve los milisegundos que tarda en descargarse.
    public int getDuracionMs() {
        return duracionMs;
    }
}