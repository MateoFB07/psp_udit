package org.example;

import java.io.IOException;

public class LanzadorVideo {
    public static void main(String[] args) {
        System.out.println("Solicitando al SO la creación de un proceso nativo...");

        // 1. ProcessBuilder con 'open' para abrir la URL en macOS
        ProcessBuilder pb = new ProcessBuilder("open", "https://youtu.be/yl0mt0Q4Hwg?si=LmO3YxesrIlwza5v");

        try {
            // 2. Ejecuta el comando
            Process proceso = pb.start();
            System.out.println("Proceso en ejecución. PID: " + proceso.pid());
            System.out.println("¡Vídeo lanzado con éxito");

        } catch (IOException e) {
            System.out.println("Error: el sistema operativo no pudo abrir el enlace: " + e.getMessage());
        }
    }
}