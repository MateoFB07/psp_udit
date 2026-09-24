package org.example;

// Herramientas nativas para listas elásticas.
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        System.out.println(" --- INICIANDO UDITVERSUM ---");
        // 1. MALA PRÁCTICA: Toda la lógica de negocio tirada al "Main",
        // los dimantes obligan a que la lista sólo acepte episodios.

        ArrayList<Episodio> catalogo = new ArrayList<>();

        catalogo.add(new Episodio("Diseño 3D Intro", 45));
        catalogo.add(new Episodio("Animación", 60));
        catalogo.add(new Episodio("Texturas", 50));

        // 2. MEDICIÓN DE TIEMPO
        // Usamos "long", y no "int", porque los milisegundos son desde 1970, formando un número
        // tan gigantesco que no cabe en la memoria normal.
        long inicio = System.currentTimeMillis();

        // BUCLE FOR EACH (POR CADA EPISODIO DENTRO DEL CATÁLOGO)
        for (Episodio ep : catalogo) {
            ep.procesar(); // AQUÍ OCURRE EL BLOQUEO SECUENCIAL DE 2 SEGUNDOS.
        }

        long fin = System.currentTimeMillis();

        // 4. RESULTADO: Fin menos inicio y dividimos entre mil (segundos)
        long totalSegundos = (fin - inicio) / 1000;
        System.out.println("Tiempo total: " + totalSegundos + " segundos de bloqueo");
    }
}