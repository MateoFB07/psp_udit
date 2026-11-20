package com.example;

// ======================================================
// ENUMERADO DE ESTADOS DE DESCARGA (REQUISITO B4)
// ======================================================
//
// OBJETIVO:
// Definir los posibles estados por los que puede pasar
// cada asset gráfico durante su proceso de descarga.

public enum Pildora03_Interbloqueo {

    // La tarea ha sido creada y enviada, pero aún no ha entrado
    // a intentar reservar una plaza en el gestor.
    PENDIENTE,

    // Las 3 plazas están ocupadas, por lo que la tarea
    // está esperando su turno con await().
    EN_ESPERA,

    // La tarea ha conseguido una plaza y está simulando
    // la descarga con Thread.sleep().
    DESCARGANDO,

    // La descarga ha finalizado correctamente.
    DESCARGADO,

    // El hilo fue interrumpido antes de completar la descarga.
    INTERRUMPIDO
}