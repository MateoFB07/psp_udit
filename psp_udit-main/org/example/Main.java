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

        // 3. Medición de tiempo inicial (AQUÍ SÍ SE DECLARAN)
        long inicio = System.currentTimeMillis();

        // 4. Procesar el catálogo
        miPlataforma.procesarCatalogo();

        // 5. Medición de tiempo final y resultado
        long fin = System.currentTimeMillis();
        long totalSegundos = (fin - inicio) / 1000;

        System.out.println("\n--- PROCESAMIENTO FINALIZADO ---");
        System.out.println("Tiempo total transcurrido: " + totalSegundos + " segundos.");

        System.out.println("\n--- INICIANDO RETO 02 (NETFLIX UDT) ---");

        // reto 02 - solución
        // 1. Instanciamos nuestro propio gestor:
        Plataforma netflixUdt = new Plataforma();

        // 2. Añadimos el contenido limpiamente
        netflixUdt.agregarEpisodio(new Episodio("Video 1", 30));
        netflixUdt.agregarEpisodio(new Episodio("Video 2", 40));
        netflixUdt.agregarEpisodio(new Episodio("Video 3", 50));
        netflixUdt.agregarEpisodio(new Episodio("Video 4", 60));
        netflixUdt.agregarEpisodio(new Episodio("Video 5", 70));

        // 3. Reutilizamos las variables quitando la palabra 'long'
        inicio = System.currentTimeMillis();

        // Una sola orden dispara todo
        netflixUdt.procesarCatalogo();

        fin = System.currentTimeMillis();
        totalSegundos = (fin - inicio) / 1000;

        System.out.println("\nTiempo total: " + totalSegundos + " segundos");
    }
}