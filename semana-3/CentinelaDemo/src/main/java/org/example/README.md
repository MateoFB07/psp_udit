# Reto 1 · Monitor UDITflix

**Módulo:** Programación de Servicios y Procesos (PSP)  
**Autor:** Mateo Fernández  
**Tecnología:** Java (JDK 17/21) + ProcessBuilder (Gestión de Procesos)  
**Entorno de ejecución:** macOS / Linux

---

## 📱 Qué es esta app

Una aplicación de consola en Java que actúa como un **monitor de estado para los servicios de la plataforma UDITflix**.

Analiza un catálogo de contenidos de forma secuencial y comprueba en tiempo real la disponibilidad de cada servidor ejecutando comandos del sistema operativo (`ping`) mediante procesos independientes. Muestra en pantalla el Identificador de Proceso (PID) generado y determina si el servicio está **ACTIVO** o **CAÍDO**.

---

## 🎯 Objetivo del reto

Aplicar los conceptos fundamentales de la **programación concurrente y gestión de procesos en Java**, ejecutando comandos nativos del sistema desde el entorno de ejecución, capturando y limpiando sus flujos de salida (*buffers*), y procesando los códigos de finalización para validar el estado de servicios en red.

---

## 🛠️ Componentes y conceptos utilizados

| Componente / Concepto | Para qué se usa en esta app |
| :--- | :--- |
| **Matriz bidimensional (`String[][]`)** | Estructura de datos para almacenar el catálogo en formato `[Nombre, IP]`. |
| **Bucle `for`** | Recorrido iterativo de la matriz para analizar cada servicio fila por fila. |
| **`ProcessBuilder`** | Instanciación y lanzamiento del proceso nativo externo del sistema (`ping -c 1 <IP>`). |
| **`Process.pid()`** | Extracción del identificador único de proceso (PID) asignado por el sistema operativo. |
| **`BufferedReader` + `InputStreamReader`** | Lectura del flujo de datos del proceso hijo para liberar el *buffer* y evitar bloqueos. |
| **`proceso.waitFor()`** | Sincronización del hilo principal esperando la finalización del proceso externo y retorno de su *exit code*. |
| **Control de excepciones (`try-catch`)** | Captura de errores(`IOException`) y posibles interrupciones del hilo (`InterruptedException`). |

---

## 🚀 Cómo ejecutar el proyecto

1. Clonar el repositorio o abrir la carpeta del proyecto en **IntelliJ IDEA** o **VS Code**.
2. Asegurarse de tener configurado **JDK 11** o superior.
3. Abrir la clase principal `MonitorUDITflix.java` localizada en `src/main/java/org/example/`.
4. Ejecutar el método `main()` directamente desde el IDE o mediante la terminal con:
   ```bash
   javac MonitorUDITflix.java
   java MonitorUDITflix