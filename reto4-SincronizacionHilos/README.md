# Reto 04 · Prueba B — UDITflix · Gestor de descargas de assets

- **Alumno:** Mateo Fernández Blasco
- **Módulo:** 0490 · Programación de Servicios y Procesos (2.º DAM)
- **Vídeo de defensa técnica (< 3 min):** [Ver demostración en SharePoint](https://365udit-my.sharepoint.com/:v:/g/personal/mateo_fernandez_alumfp_udit_es/IQAlog9ljeO0RIxbzRoklk9KAbhqXgO3MJVV3o5RXj21Dco?nav=eyJyZWZlcnJhbEluZm8iOnsicmVmZXJyYWxBcHAiOiJPbmVEcml2ZUZvckJ1c2luZXNzIiwicmVmZXJyYWxBcHBQbGF0Zm9ybSI6IldlYiIsInJlZmVycmFsTW9kZSI6InZpZXciLCJyZWZlcnJhbFZpZXciOiJNeUZpbGVzTGlua0NvcHkifX0&e=HiJUWq)

---

## 1. Problema y objetivo

En la plataforma **UDITflix**, cada vez que un usuario abre la aplicación o cambia de vista, se solicitan de forma simultánea varios recursos gráficos (portadas, logotipos, banners, miniaturas, avatares y fondos). Si todas estas descargas se inician a la vez sin límite, se satura el ancho de banda disponible y se degrada el rendimiento general.

El objetivo de esta práctica es implementar en Java un **gestor de descargas concurrente** que procese un lote de **7 assets** (con tiempos simulados mediante `Thread.sleep()`), garantizando en todo momento que **nunca haya más de 3 descargas activas de forma simultánea** y manteniendo un registro seguro del estado de cada tarea.

### Captura general

<img width="1308" height="586" alt="Captura de pantalla 2026-11-25 a las 10 44 02" src="https://github.com/user-attachments/assets/79bb905b-20c1-4913-87a9-8289c036d1bf" />


---

## 2. Planificación del desarrollo

Para abordar la solución de forma modular y verificable, dividí el desarrollo en cuatro fases:

1. **Modelado del dominio:** Creación del enumerado `Estado` y la clase `Asset` para encapsular el lote de 7 recursos en una lista (`List<Asset>`) sin duplicar bloques de código.
2. **Diseño del gestor de sincronización:** Implementación de `GestorDescargas` con `ReentrantLock(true)` y una `Condition` (`hayPlaza`) para limitar a 3 las descargas simultáneas y gestionar la cola de espera.
3. **Orquestación concurrente y seguimiento:** Configuración en `Main` del `ExecutorService` junto con `Callable<String>` y `Future<String>`, además de `ConcurrentHashMap` para los estados y `AtomicInteger` para contabilizar las descargas finalizadas.
4. **Pruebas y validación:** Ejecución del programa verificando que el máximo simultáneo nunca supera 3, que todas las plazas se liberan en `finally` y documentando la evidencia obtenida.

---

## 3. Arquitectura del proyecto

El proyecto está organizado bajo el paquete `com.example` separando las responsabilidades en cuatro clases:

```text
reto4-SincronizacionHilos/
├── pom.xml
├── README.md
└── src/main/java/com/example/
    ├── Estado.java           # Enumerado con los estados del ciclo de vida
    ├── Asset.java            # Modelo de datos (nombre y duración en ms)
    ├── GestorDescargas.java  # Control de las 3 plazas con Lock y Condition
    └── Main.java             # Lanzamiento del lote, pool de hilos, seguimiento y resumen
