package org.example;

public class Main {
    public static void main(String[] args) {

        System.out.println("--- INICIANDO PLATAFORMA STREAMING ---");

        // 1. Instanciar la Plataforma
        Plataforma miPlataforma = new Plataforma();

        // 2. Añadir 5 episodios
        miPlataforma.agregarEpisodio(new Episodio("Episodio 1: Pilot", 45));
        miPlataforma.agregarEpisodio(new Episodio("Episodio 2: El Descubrimiento", 50));
        miPlataforma.agregarEpisodio(new Episodio("Episodio 3: El Conflicto", 40));
        miPlataforma.agregarEpisodio(new Episodio("Episodio 4: El Clímax", 55));
        miPlataforma.agregarEpisodio(new Episodio("Episodio 5: El Desenlace", 60));

        // 3. Medición de tiempo inicial
        long inicio = System.currentTimeMillis();

        // 4. Procesar el catálogo
        miPlataforma.procesarCatalogo();

        // 5. Medición de tiempo final y resultado
        long fin = System.currentTimeMillis();
        long totalSegundos = (fin - inicio) / 1000;

        System.out.println("\n--- PROCESAMIENTO FINALIZADO ---");
        System.out.println("Tiempo total transcurrido: " + totalSegundos + " segundos.");
    }
}