package com.example;

// Defino los posibles estados por los que pasa cada descarga
public enum Estado {
    PENDIENTE,
    EN_ESPERA,
    DESCARGANDO,
    DESCARGADO,
    INTERRUMPIDO
}