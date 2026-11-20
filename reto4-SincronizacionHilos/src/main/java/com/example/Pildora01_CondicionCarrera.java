package com.example;

import java.util.concurrent.atomic.AtomicInteger;

// ======================================================
// PÍLDORA 1: CONDICIÓN DE CARRERA Y CONTADOR SEGURO (B4)
// ======================================================
//
// OBJETIVO:
// Aprender qué ocurre cuando varios hilos modifican
// una misma variable al mismo tiempo y cómo proteger
// el contador de descargas completadas en UDITflix.

public class Pildora01_CondicionCarrera {

    // ==================================================
    // 1. CLASE DEL CONTADOR COMPARTIDO
    // ==================================================
    static class ContadorCompartido {

        // Variable entera que representa descargas completadas sin protección.
        private int valor = 0;

        // Método incorrecto: sin protección (puede perder descargas).
        public void incrementarSinProteccion() {
            int copiaLocal = valor;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            valor = copiaLocal + 1;
        }

        // Método correcto: protegido con synchronized.
        public synchronized void incrementarSeguro() {
            valor++;
        }

        public int getValor() {
            return valor;
        }
    }

    // ==================================================
    // 2. MÉTODO PRINCIPAL: DEMOSTRACIÓN CON 7 ASSETS
    // ==================================================
    public static void main(String[] args) throws InterruptedException {

        System.out.println("===== DEMO PÍLDORA 1: CONTADOR DE DESCARGAS UDITFLIX =====");

        ContadorCompartido contadorIncorrecto = new ContadorCompartido();
        ContadorCompartido contadorSeguro = new ContadorCompartido();

        // Requisito B4: AtomicInteger para contar descargas completadas
        AtomicInteger completadasAtomico = new AtomicInteger(0);

        // Simulamos las 7 descargas del lote intentando sumar a la vez
        Thread[] hilos = new Thread[7];
        for (int i = 0; i < 7; i++) {
            hilos[i] = new Thread(() -> {
                contadorIncorrecto.incrementarSinProteccion();
                contadorSeguro.incrementarSeguro();
                completadasAtomico.incrementAndGet();
            }, "ASSET-" + (i + 1));
            hilos[i].start();
        }

        for (Thread hilo : hilos) {
            hilo.join();
        }

        System.out.println("Descargas esperadas: 7");
        System.out.println("Sin protección (condición de carrera): " + contadorIncorrecto.getValor());
        System.out.println("Con synchronized: " + contadorSeguro.getValor());
        System.out.println("Con AtomicInteger (usado en UDITflix): " + completadasAtomico.get());
    }
}