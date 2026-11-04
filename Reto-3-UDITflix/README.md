# Reto 3 · Motor Concurrente en UDITflix

**Módulo:** 0490 · Programación de Servicios y Procesos (2º DAM)  
**RA vinculado:** RA2 · Programación de aplicaciones multihilo  
**Prueba realizada:** ☑ B · El Optimizador de Descarga Masiva de Assets  
**Autor/a:** Mateo Fernández Blasco  


---

## 📺 1. Resumen del problema

En el backend de la aplicación móvil de UDITflix, la pantalla de inicio de sesión sufría un grave problema de latencia debido a que la precarga de datos críticos (sesión y catálogo) y assets secundarios (portadas y analítica) se realizaba de manera secuencial en un único hilo.

Para solucionar este cuello de botella, he migrado el motor a una arquitectura multihilo asíncrona en Java. Con ella, los 4 módulos se descargan de forma concurrente, asignando prioridades (`MAX_PRIORITY`, `NORM_PRIORITY`, `MIN_PRIORITY`) según la importancia del recurso, y orquestando los hilos mediante una colección dinámica `ArrayList<Thread>` y la sincronización explícita con `.join()`. Esto retiene el acceso al login hasta que la totalidad de las descargas se completa.

---

## 🧠 2. Antes de empezar: planifico (5 min, sin tocar el teclado)

- **¿Qué parte de la base v0 de clase voy a reutilizar tal cual?** Reutilizaré la estructura de las tareas que implementan `Runnable` (`TareaTranscodificacion`/`TareaDescarga`), la generación de latencias aleatorias con `Random` (`1000 + azar.nextInt(3001)`) y la lógica de cálculo de tiempos (`System.currentTimeMillis()`).
- **¿Qué es nuevo y me da más respeto?** La orquestación mediante colecciones dinámicas (`ArrayList<Thread>`) en lugar de variables manuales, separando estrictamente los bucles de arranque (`.start()`) y de sincronización (`.join()`), junto con la asignación explícita de prioridades.
- **Mi plan en 4-5 pasos, en orden:**
  1. Adaptar los nombres de los módulos y configurar las prioridades obligatorias requeridas en la tabla RT1.
  2. Sustituir las variables individuales de hilos por la colección `ArrayList<Thread>`.
  3. Implementar el primer bucle `for-each` para instanciar y arrancar los hilos en paralelo con `.start()`.
  4. Implementar el segundo bucle `for-each` para bloquear la ejecución del hilo `main` mediante `.join()`.
  5. Imprimir la traza de login habilitado y calcular métricas de rendimiento (tiempo total real vs. suma secuencial).

### Predicciones (comprueba al final si acertaste)

| Pregunta | Mi predicción |
|---|---|
| ¿Cuánto tardará el programa si los 4 hilos van **a la vez**? | Tardará aproximadamente lo que dure el hilo más lento (entre 3000 y 4000 ms). |
| ¿Y si se ejecutaran **uno detrás de otro**? | La suma de todas las latencias secuenciales (entre 7000 y 12000 ms). |
| ¿Saldrán los hilos siempre en el mismo orden? ¿Por qué? | No, porque el tiempo de descanso es aleatorio y el planificador del sistema operativo gestiona el tiempo de CPU de forma no determinista. |
| *(Solo Prueba B)* ¿Terminarán antes los hilos de prioridad alta? | No necesariamente, porque al estar dormidos con `Thread.sleep()`, liberan la CPU y prima la latencia por encima de la prioridad. |
| ¿El tiempo total se parecerá a la duración del hilo más lento o a la suma? | Se parecerá casi exactamente a la duración del hilo más lento. |

---

## 🏗️ 3. Arquitectura de la solución

### 3.1 Esquema

```
main ──start()──► Thread [DL-catalogo]  (prio=10) ─┐
     ──start()──► Thread [DL-sesion]    (prio=10) ─┤
     ──start()──► Thread [DL-portadas]  (prio=5)  ─┼──► join() ──► [MAIN] LOGIN HABILITADO
     ──start()──► Thread [DL-analitica] (prio=1)  ─┘
```

### 3.2 Componentes y conceptos

| Componente / concepto | Para qué lo uso en mi programa | Con mis palabras |
|---|---|---|
| `Runnable` | Define **qué** hace el hilo | Es la interfaz que encapsula la tarea o el trabajo a realizar dentro del método `run()`. |
| `Thread` | Define **cómo** se ejecuta (un hilo real del SO) | Es el objeto trabajador de Java que gestiona la ejecución concurrente sobre un hilo real del sistema. |
| `start()` | Crea el hilo y lo pone a ejecutar `run()` | Notifica al sistema operativo para que cree un nuevo hilo de ejecución asíncrono y llame a `run()`. |
| `run()` | Código que ejecuta el hilo | Es el contenedor del código que debe ejecutarse cuando el hilo se pone en marcha. |
| `join()` | Hace que `main` espere a que el hilo termine | Detiene el hilo actual (`main`) hasta que el hilo sobre el que se invoca completa su ciclo de vida. |
| `getState()` | Consulta el estado del ciclo de vida del hilo | Método para inspeccionar en qué fase (NEW, RUNNABLE, TIMED_WAITING, TERMINATED) se encuentra un hilo. |
| `Thread.sleep()` | Simula trabajo; el hilo pasa a `TIMED_WAITING` | Suspende temporalmente la ejecución del hilo liberando el uso de CPU durante los milisegundos indicados. |
| `setPriority()` | Sugiere una prioridad al planificador | Indica al sistema operativo una preferencia (de 1 a 10) sobre qué hilos deberían recibir más tiempo de procesamiento. |
| `setDaemon()` | Marca un hilo como demonio *(si lo has usado)* | Define un hilo en segundo plano que finaliza automáticamente cuando todos los hilos no-demonio concluyen. |

**¿Cómo se llaman mis hilos y qué hace cada uno?**
- `DL-catalogo` (Prioridad 10): Descarga los datos de catálogo, géneros y fichas.
- `DL-sesion` (Prioridad 10): Descarga la validación de tokens de sesión y perfiles de usuario.
- `DL-portadas` (Prioridad 5): Descarga los assets gráficos, portadas y banners visuales.
- `DL-analitica` (Prioridad 1): Envía y descarga la telemetría secundaria de uso de la app.

---

## 🚀 4. Cómo ejecutar el proyecto

1. Clonar el repositorio y abrirlo en IntelliJ IDEA.
2. Esperar a que el proyecto indexe las dependencias y la estructura Java.
3. Ejecutar (▶) la clase principal: `org.example.MainUDITflix`

- **Versión de Java (JDK):** JDK 17 o superior.
- **Sistema operativo y nº de núcleos de mi equipo:** macOS / Windows 11 (8/10 núcleos).
- **Archivos externos necesarios (si los hay):** Ninguno.

---

## 🔍 5. Mientras programo: mi diario de decisiones

| Qué intentaba | Qué pasó realmente | Qué hice / qué aprendí |
|---|---|---|
| Lanzar y esperar a los hilos dinámicamente | Puse el `join()` dentro del mismo bucle que el `start()` | El programa se volvió secuencial. Aprendí que se requiere un bucle para `.start()` y otro distinto para `.join()`. |
| Medir el ahorro de tiempo en el `main` | El tiempo final imprimía 0 ms | La variable `inicio` no estaba restada adecuadamente con el timestamp final tras la llamada a los `.join()`. |

**Mi pregunta-brújula cuando me bloqueo:**
1. ¿Qué espero que haga esta línea?
2. ¿Qué está haciendo realmente? (imprimo valores para comprobarlo)
3. ¿En qué punto exacto se separan las dos respuestas?

---

## 📊 6. Evidencias de ejecución

### 6.1 Traza de consola

```text
==================================================
   UDITflix BACKEND · MOTOR DE TRANSCODIFICACIÓN  
==================================================
[MAIN] Descargando 4 módulos en paralelo...

--> [DL-catalogo] prio=10 latencia=1840 ms
--> [DL-sesion] prio=10 latencia=1120 ms
--> [DL-portadas] prio=5 latencia=3610 ms
--> [DL-analitica] prio=1 latencia=2950 ms
<-- [DL-sesion] LISTO
<-- [DL-catalogo] LISTO
<-- [DL-analitica] LISTO
<-- [DL-portadas] LISTO

==================================================
 [MAIN] Todos los módulos listos -> LOGIN HABILITADO
 Tiempo total real:          3615 ms
 Duración del más lento:     3610 ms
 Suma de todas (secuencial): 9520 ms
==================================================
```

### 6.2 Tabla de ejecuciones

**Prueba B**

| Ejecución | Latencias que tocaron | Orden de finalización de los módulos | Tiempo total | Más lento | Suma |
|---|---|---|---|---|---|
| 1 | 1840, 1120, 3610, 2950 ms | DL-sesion, DL-catalogo, DL-analitica, DL-portadas | 3615 ms | 3610 ms | 9520 ms |
| 2 | 3100, 1400, 1200, 2200 ms | DL-portadas, DL-sesion, DL-analitica, DL-catalogo | 3105 ms | 3100 ms | 7900 ms |
| 3 | 1050, 2900, 2100, 1150 ms | DL-catalogo, DL-analitica, DL-portadas, DL-sesion | 2908 ms | 2900 ms | 7200 ms |

### 6.3 Solo Prueba B · Análisis breve del planificador

Al observar las 3 ejecuciones, el orden de finalización de los módulos no sigue de forma estricta la jerarquía de prioridades impuesta por `setPriority()`. Por ejemplo, en la ejecución 2 el módulo `DL-portadas` (prio=5) terminó antes que `DL-sesion` (prio=10), y en la ejecución 3 `DL-analitica` (prio=1) finalizó antes que `DL-sesion`.

Esto sucede porque en Java la prioridad es una mera sugerencia dirigida al planificador del sistema operativo. Dado que los hilos están ejecutando una simulación de descarga basada en `Thread.sleep()`, pasan inmediatamente al estado `TIMED_WAITING` y abandonan el uso de la CPU. En consecuencia, el planificador no tiene trabajo de computación que priorizar y el orden de término viene dado de forma exclusiva por la latencia aleatoria asignada.

**¿Acerté en mis predicciones del apartado 2? Explica por qué sí o por qué no:**
Sí, acerté. La ejecución multihilo reduce el tiempo global al valor del hilo más lento (~3.6s vs ~9.5s secuenciales) y las prioridades no afectaron al orden debido al uso de bloqueos por tiempo (`sleep`).

---

## 🧠 7. Preguntas de metacognición técnica (obligatorias)

### 7.1 Preguntas comunes (Prueba A y Prueba B)

**1. ¿Por qué el tiempo total es el del hilo más lento y no la suma de todos?**  
Porque al llamar a `start()`, el hilo principal arranca la ejecución de los 4 hilos en paralelo, permitiendo que sus tiempos de espera transcurran de forma simultánea. El hilo `main` permanece bloqueado en `join()` únicamente hasta que el último hilo (el más lento) completa su tarea.

**2. ¿Qué diferencia real hay entre llamar a `start()` y llamar a `run()`?**  
Invocando `start()`, el entorno de ejecución de Java solicita al sistema operativo la creación de un nuevo hilo de ejecución independiente. Si se llama a `run()`, no se crea ningún hilo nuevo; la tarea se ejecuta de manera síncrona en el hilo actual (`main`), eliminando toda concurrencia y haciendo el programa secuencial.

**3. ¿Qué pasaría si quitaras los `join()`? ¿Y si pusieras el `join()` justo después de cada `start()`?**  
Sin los `join()`, el hilo `main` continuaría inmediatamente su ejecución e imprimiría "LOGIN HABILITADO" mientras los módulos aún se están descargando. Si se colocara un `join()` justo después de cada `start()`, se forzaría al `main` a esperar a que terminase el primer hilo antes de lanzar el segundo, convirtiendo la arquitectura en totalmente secuencial.

**4. ¿Qué diferencias hay entre trabajar con duraciones fijas y con latencias aleatorias?**  
Con duraciones fijas la ejecución es determinista en orden y tiempo. Las latencias aleatorias simulan entornos reales de red donde las respuestas varían, permitiendo probar la resistencia del backend y comprobar que la sincronización con `join()` funciona sin importar qué hilo acabe primero.

**5. El orden en que terminan los hilos puede cambiar entre ejecuciones. ¿A qué se debe? ¿Podrías garantizar un orden concreto?**  
Se debe al no-determinismo del planificador del sistema operativo combinado con las latencias aleatorias. Para garantizar un orden estricto de finalización sin renunciar a la concurrencia, habría que implementar mecanismos de sincronización como semáforos, `CountDownLatch`, o dependencias de ejecución.

**6. ¿Qué problema te costó más depurar y cómo lo resolviste?**  
El problema principal fue asegurar la independencia de los bucles en la colección `ArrayList<Thread>`. Inicialmente metí la lógica de `join()` dentro del mismo bucle de iteración que el `start()`, lo que provocó que el programa volviese a ser secuencial. Lo solucioné revisando la traza y separando la fase de arranque de la fase de espera en dos bucles distintos.

**7. Si en lugar de 4 tareas fueran 500, ¿seguiría teniendo sentido crear un `Thread` por tarea? ¿Qué problema crees que aparecería?**  
No tendría sentido. Crear 500 hilos nativos del sistema operativo generaría un enorme consumo de memoria (overhead por cada stack de hilo) y degradaría el rendimiento por el exceso de cambios de contexto (*context switching*). Para esa escala lo correcto sería emplear un `ExecutorService` (pool de hilos) o hilos virtuales (*Virtual Threads* / Project Loom).

### 7.3 Solo Prueba B · ArrayList y prioridades

**11. ¿Qué dificultad tuviste al gestionar el `ArrayList<Thread>`?**  
La principal dificultad radicó en estructurar adecuadamente la orquestación: fue necesario emplear un bucle para recorrer la lista e invocar `start()` en cada elemento, y un bucle posterior independiente para invocar `join()`. Esta estructura permite que añadir un nuevo módulo (por ejemplo, un quinto elemento) requiera únicamente agregar una línea `hilos.add(...)`.

**12. ¿Qué diferencias observaste al asignar prioridades distintas en tu sistema operativo?**  
No se observaron diferencias significativas en el orden de finalización respecto a las prioridades asignadas. Módulos con `MIN_PRIORITY` terminaron antes que módulos con `MAX_PRIORITY` debido a las latencias de `sleep()`.

**13. Si no viste diferencia (o fue pequeña), ¿a qué crees que se debe?**  
Se debe a que los hilos no realizan un trabajo intensivo de CPU, sino que entran en estado `TIMED_WAITING` inmediatamente mediante `Thread.sleep()`. Cuando un hilo está dormido no compite por procesador, por lo que el planificador no aplica las reglas de prioridad de CPU.

**14. ¿Puede fiarse un programador de las prioridades de Java para garantizar que algo se ejecute antes? Justifica tu respuesta.**  
No. En Java las prioridades son sugerencias dependientes de la plataforma, la JVM y el sistema operativo. Nunca deben utilizarse para garantizar el orden de ejecución ni la lógica de negocio; para ello se deben usar herramientas de sincronización explícitas.

**15. *(Si hiciste la ampliación)* ¿Por qué el programa termina aunque el hilo de «Cargando…» nunca acabe por sí mismo?**  
Porque se configura como un hilo demonio mediante `setDaemon(true)`. La Java Virtual Machine (JVM) finaliza automáticamente su ejecución cuando el último hilo que no es demonio (en este caso el hilo `main` tras completar sus `join()`) concluye su tarea.

---

## 🌍 8. Mundo real

Esta forma de trabajar con tareas concurrentes se utiliza en plataformas de streaming como **Netflix**, **Spotify** o **YouTube**, así como en clientes móviles (Flutter/Android/iOS). Cuando un usuario abre la aplicación, el cliente realiza peticiones asíncronas paralelas a microservicios en la nube para obtener el token de usuario, la lista de reproducción, las imágenes de portada y la configuración del reproductor. De la misma manera que en este reto, la pantalla de inicio no se presenta hasta que las dependencias críticas completan su descarga.

---

## 🪞 9. Autoevaluación

| Puedo explicar a un compañero… | 🔴 No | 🟡 Más o menos | 🟢 Sí |
|---|:-:|:-:|:-:|
| Qué es un `Runnable` y qué es un `Thread` | ☐ | ☐ | ☑ |
| Por qué `start()` no espera a que el hilo acabe | ☐ | ☐ | ☑ |
| Qué hace `join()` y dónde lo he puesto | ☐ | ☐ | ☑ |
| Qué estados tiene un hilo y cuándo cambia | ☐ | ☐ | ☑ |
| Por qué el orden de los hilos no es siempre el mismo | ☐ | ☐ | ☑ |
| Qué son las prioridades y qué garantizan (o no) | ☐ | ☐ | ☑ |
| Qué es un hilo demonio | ☐ | ☐ | ☑ |

**Lo que haría diferente si empezara de nuevo:**  
Escribiría primero los test de consola para registrar directamente la diferencia de tiempos entre la versión monohilo y la versión concurrente con `ArrayList`.

**Lo que todavía no tengo claro y quiero preguntar en clase:**  
Cómo gestionar de forma eficiente la captura de excepciones dentro de lambdas al trabajar con colecciones dinámicas de hilos en Java.

---

## 🤝 10. Declaración de autoría y aprendizaje

Este reto **no permite herramientas de IA generativa**. Consulté únicamente: los apuntes de clase, la documentación oficial de Java y la de IntelliJ IDEA.

- [x] Confirmo que he diseñado, programado y depurado este código aplicando mi propio razonamiento, y que puedo explicarlo línea a línea.
- [x] Confirmo que el vídeo de defensa técnica dura menos de 3 minutos y está grabado con mi propia voz.

---

## 📂 11. Estructura del proyecto

```
src/main/java/org/example/
├── MainUDITflix.java             → Clase principal con la orquestación multihilo
└── TareaTranscodificacion.java   → Clase que implementa Runnable para simular las descargas
README.md                         → Documentación técnica y cuaderno metacognitivo
```