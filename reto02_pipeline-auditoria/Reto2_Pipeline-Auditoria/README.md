# Reto 2 · Pipeline de Auditoría UDITversum (Fase 1)

**Módulo:** 0490 · Programación de Servicios y Procesos  
**Autor/a:** Mateo Fernández Blasco
**Tecnología:** Java + `ProcessBuilder` (procesos del sistema operativo)  
**RA vinculado:** RA1 · Programación de aplicaciones compuestas por varios procesos  


---

## 📺 Qué es esta app

Un programa de consola en Java que ejecuta la **primera fase de una auditoría de red/sistema en UDITversum**. El programa:

1. Lanza **dos comprobaciones de conectividad (`ping`) de manera simultánea**, cada una en su propio proceso independiente del sistema operativo.
2. **Espera** a que ambos procesos finalicen su ejecución.
3. Analiza el **código de salida** (*exit code*) devuelto por cada uno de ellos.
4. Con base en el resultado combinado de ambas pruebas, **evalúa una condición lógica** y abre la herramienta correspondiente del sistema (Bloc de Notas en caso de éxito o Calculadora en caso de fallo/anomalía).

```
        ┌──────────────────────┐
        │   PipelneAuditoria   │
        │        (Java)        │
        └──────────┬───────────┘
                   │ start()          start()
        ┌──────────┴──────────┐    ┌──────────┴──────────┐
        ▼                                                ▼
  ┌───────────┐                                    ┌───────────┐
  │  ping A   │                                    │  ping B   │   ← ejecuciones concurrentes
  └─────┬─────┘                                    └─────┬─────┘
        │ waitFor()                                      │ waitFor()
        └──────────────────────┬─────────────────────────┘
                               ▼
                    ¿códigos de salida (0 / !0)?
                               │
               ┌───────────────┴───────────────┐
               ▼                               ▼
    Bloc de Notas (TextEdit)             Calculadora (Calculadora)
```


<img width="408" height="390" alt="Captura de pantalla 2026-10-08 a las 20 56 42" src="https://github.com/user-attachments/assets/301d9d4c-6168-4051-9d55-3d6a2a715042" />

---


**Con mis palabras, ¿qué me pide el reto?**  
Lanzar dos procesos de red (`ping`) en paralelo, esperar a que ambos terminen, analizar si tuvieron éxito o no mediante sus códigos de retorno, y desencadenar la apertura de una aplicación auxiliar del SO en función del estado final de la red.

**¿Qué parte del Reto 1 voy a reutilizar tal cual?**  
La instanciación de procesos con `ProcessBuilder`, la ejecución de comandos del sistema (`ping`, `com.apple.TextEdit`, `com.apple.Calculadora`) y el bloque de captura de excepciones `try/catch`.

**¿Qué es nuevo respecto al Reto 1 y me da más respeto?**  
La **concurrencia real**: asegurar que los dos pings se ejecuten en paralelo y no en secuencia, sincronizándolos adecuadamente mediante `waitFor()` tras el lanzamiento de ambos.

**Mi plan en 4-5 pasos, en orden:**
1. Crear dos objetos `ProcessBuilder` configurados con los comandos `ping` correspondientes.
2. Iniciar ambos procesos consecutivamente usando `.start()` sin bloquear el hilo principal.
3. Bloquear la ejecución principal invocando `.waitFor()` en ambos objetos `Process` para recuperar sus códigos de salida (`exitValue`).
4. Evaluar los resultados mediante condicionales `if/else`.
5. Iniciar el proceso final (`com.apple.TextEdit` o `com.apple.Calculadora`) según la evaluación.

**Predicciones**

| Escenario | ¿Qué código de salida espero en cada ping? | ¿Qué aplicación se abre? |
|---|---|---|
| Los dos pings a `127.0.0.1` | `0` y `0` | Bloc de Notas (`com.apple.TextEdit`) |
| Un ping válido y otro a una dirección inexistente | `0` y `!= 0` | Calculadora (`com.apple.Calculadora`) |
| Los dos pings a direcciones inexistentes | `!= 0` y `!= 0` | Calculadora (`com.apple.Calculadora`) |

**Predicción de tiempo:** Si cada ping tarda aproximadamente 3 segundos en resolver:
* En **paralelo**, la fase de pings tomará **~3 segundos** en total (corren al mismo tiempo).
* En **secuencial**, la fase de pings tomaría **~6 segundos** (3s + 3s).

---

## 🎯 Objetivo del reto

Dar el salto de **gestionar un proceso tras otro** (Reto 1) a **coordinar varios procesos simultáneos**, aplicando:

- Creación de procesos con `ProcessBuilder` y `.start()`.
- **Ejecución concurrente:** lanzar ambos procesos antes de esperar por la finalización de cualquiera de ellos.
- Sincronización con `.waitFor()` y lectura del **código de salida** (*exit code*).
- **Lógica condicional** (`if` con `&&` / `||`) para tomar decisiones basadas en múltiples estados.
- Gestión de excepciones del sistema con `try/catch` (`IOException`, `InterruptedException`).

---

## 🛠️ Componentes y conceptos utilizados

| Componente / concepto | Para qué se usa en esta app | Con mis palabras |
|---|---|---|
| `ProcessBuilder` | Prepara la orden y parámetros que se enviarán al SO. | El plano o plantilla para crear un proceso nuevo. |
| `start()` | Inicia la ejecución del proceso en el SO de forma asíncrona. | Botón de encendido: lanza el proceso y sigue ejecutando código sin frenar. |
| `Process` | Objeto Java que representa y permite controlar un proceso en ejecución. | El mando a distancia para supervisar el proceso activo. |
| `waitFor()` | Detiene el hilo actual hasta que el proceso asociado finaliza. | La sala de espera: detiene el programa Java hasta que el proceso hijo acaba. |
| Código de salida (`int`) | Valor numérico devuelto por el proceso al finalizar. | El reporte final: `0` indica éxito absoluto y cualquier otro número indica un fallo. |
| `&&` (AND) | Evalúa si dos o más condiciones son verdaderas simultáneamente. | Exigente: obliga a que ambas comprobaciones sean válidas. |
| `\|\|` (OR) | Evalúa si al menos una de las condiciones es verdadera. | Flexible: basta con que una sola comprobación sea válida. |
| `try/catch` | Estructura para capturar y gestionar errores en tiempo de ejecución. | El colchón de seguridad para evitar que el programa falle si el SO no encuentra un ejecutable. |
| `InterruptedException` | Excepción lanzada cuando un hilo en espera es interrumpido. | Alerta por interrupción mientras se esperaba a que `waitFor()` concluyera. |

**¿Cómo se llaman mis dos objetos `Process` y qué lanza cada uno?**
- `p1` (o `procesoPing1`): Ejecuta el comando `ping` hacia el primer host/IP.
- `p2` (o `procesoPing2`): Ejecuta el comando `ping` hacia el segundo host/IP.

---

## 🔀 Secuencial vs paralelo: el corazón de este reto

La diferencia fundamental entre un modelo secuencial y uno concurrente radica en la posición táctica de las llamadas a `.waitFor()`:

**❌ Secuencial** (Incorrecto para este reto):

```java
Process p1 = pb1.start();
int exit1 = p1.waitFor(); // Bloquea hasta que p1 termina (3s)

Process p2 = pb2.start(); // No empieza hasta t=3s
int exit2 = p2.waitFor(); // Bloquea hasta que p2 termina (3s) -> Total ~6s
```

**✅ Paralelo** (Implementado en el proyecto):

```java
Process p1 = pb1.start(); // Inicia p1 inmediatamente
Process p2 = pb2.start(); // Inicia p2 inmediatamente (t=0s)

int exit1 = p1.waitFor(); // Espera a p1
int exit2 = p2.waitFor(); // Espera a p2 -> Total ~3s
```

---

## 🔢 Tabla de verdad de mi decisión

Basado en una condición de éxito global requerida (`&&`):

| Código ping A | Código ping B | ¿Ping A OK? | ¿Ping B OK? | Condición (`exit1 == 0 && exit2 == 0`) | Aplicación que abro |
|:---:|:---:|:---:|:---:|:---:|:---:|
| 0 | 0 | Sí | Sí | Verdadero (`true`) | `com.apple.TextEdit` |
| 0 | ≠ 0 | Sí | No | Falso (`false`) | `com.apple.Calculadora` |
| ≠ 0 | 0 | No | Sí | Falso (`false`) | `com.apple.Calculadora` |
| ≠ 0 | ≠ 0 | No | No | Falso (`false`) | `com.apple.Calculadora` |

**¿Cambiaría el resultado de alguna fila si cambiara `&&` por `||`? ¿En cuáles?**  
Sí, cambiaría en las filas 2 y 3. Con `||`, la presencia de al menos un ping exitoso activaría el bloque `true` (`com.apple.TextEdit`), ignorando el fallo de la otra interfaz de red.

---

## 🚀 Cómo ejecutar el proyecto

1. Clonar el repositorio o abrir la carpeta `Reto2_Pipeline-Auditoria` en IntelliJ IDEA.
2. Verificar la estructura en `src/main/java/org/example/`.
3. Ejecutar la clase principal `Main.java` o `PipelneAuditoria.java`.

⚠️ **Dependencia del sistema operativo:**  
Este proyecto está configurado y probado en **Mac**. Los comandos utilizados (`ping -c ...`, `com.apple.Calculator`, `"com.apple.TextEdit"`) corresponden al entorno de comandos de Mac (`Terminal`).

---

## 🔍 Mientras programo: mi diario de decisiones

| Qué intentaba | Qué pasó realmente | Qué hice / qué aprendí |
|---|---|---|
| Lanzar ambos pings al mismo tiempo | El programa tardaba el doble de tiempo | Noté que llamaba a `waitFor()` justo después de lanzar el primer `start()`. Mové todos los `start()` al inicio para lograr paralelismo real. |
| Comprobar resultados | Invocaba `exitValue()` antes de `waitFor()` | Saltaba un `IllegalThreadStateException` porque el proceso aún no terminaba. Aprendí a usar `waitFor()` primero. |
| Ejecutar aplicaciones finales | El proceso secundario abría y bloqueaba la consola | Comprendí que lanzar una interfaz gráfica desde Java crea un proceso independiente en el sistema operativo. |

---

## 🧠 Análisis técnico (preparación para la defensa)

### 1. Secuencial vs paralelo

**¿Qué líneas exactas garantizan que los dos pings se ejecutan a la vez? ¿Qué ocurriría físicamente si pusieras el primer `waitFor()` justo antes de lanzar el segundo `start()`?**  
Las líneas donde se ejecutan consecutivamente `pb1.start()` y `pb2.start()` sin llamadas intermedias de bloqueo. Si se sitúa `p1.waitFor()` antes de `pb2.start()`, el hilo principal se detiene por completo esperando al SO; el segundo ping no existiría en la tabla de procesos del SO hasta que el primero termine.

### 2. El código de salida (exit code)

**¿Qué tipo de dato devuelve `waitFor()`? ¿Qué significa en el estándar de los sistemas operativos que ese valor sea `0` o distinto de `0`?**  
Devuelve un entero primitivo (`int`). En los sistemas operativos tipo POSIX y Mac, un valor `0` indica ejecución limpia/exitosa (*SUCCESS*). Cualquier entero distinto de `0` (ej. `1`, `2`, `-1`) representa una anomalía, error o interrupción no esperada.

### 3. Lógica condicional

**Escribe aquí la condición `if` exacta que has programado. Explica por qué has utilizado `&&` o `||` para decidir si abrir el Bloc de Notas o la Calculadora.**

```java
if (exitCode1 == 0 && exitCode2 == 0) {
    new ProcessBuilder("com.apple.TextEdit").start();
} else {
    new ProcessBuilder("com.apple.Calculadora").start();
}
```

*Razonamiento:* Se utiliza `&&` para exigir una **auditoría estricta**: la infraestructura se considera completamente funcional únicamente si *ambas* verificaciones responden con éxito (`0`).

### 4. Gestión de excepciones

**Tu código incluye un bloque `try/catch`. Describe una situación real (un fallo del sistema o una mala configuración) que provocaría que tu programa entrase en el `catch` de `IOException`.**  
Ocurre si el ejecutable especificado no existe o no está en la ruta del sistema (`PATH`), por ejemplo, si se escribe equivocadamente `notepadd.exe` o se intentan ejecutar comandos de Windows en una máquina Linux sin el binario instalado.

---

## 🛡️ Preparación para la defensa: ¿sabría hacer esto en directo?

- [x] Cambiar la condición para que se abra la Calculadora **solo si falla uno de los dos pings**.
- [x] Añadir un **tercer ping** en paralelo y que la decisión dependa de los tres.
- [x] Mostrar el **PID** de cada proceso al lanzarlo mediante `process.pid()`.
- [x] Medir y mostrar **cuántos milisegundos** tarda en total el programa (`System.currentTimeMillis()`).
- [x] Hacer que el programa funcione en **Linux** (cambiar `-n` por `-c` y aplicaciones a `gedit`/`gnome-calculator`).
- [x] Provocar a propósito una `IOException` e imprimir la traza de error.
- [x] Explicar qué pasaría si quito el `waitFor()`.

---

## 🧭 Del Reto 1 al Reto 2: cómo di el salto

**¿Qué hacía mi Reto 1 que aquí ya no me sirve tal cual?**  
El Reto 1 procesaba tareas de manera estrictamente secuencial en un único flujo de control. Aquí se requiere desacoplar el lanzamiento de procesos de su sincronización.

**¿Qué he tenido que cambiar para que dos procesos corran simultáneamente?**  
Separar la fase de **inicio** (`start()`) de la fase de **espera/sincronización** (`waitFor()`).

**¿Qué ventaja tiene lanzar en paralelo? ¿Y qué problema nuevo aparece cuando dependo de dos resultados a la vez?**  
*Ventaja:* Optimización de tiempos de ejecución al aprovechar el procesamiento multitarea del SO.  
*Problema:* Mayor complejidad al evaluar el estado global, ya que hay que gestionar múltiples códigos de salida simultáneamente.

---

## 🧠 Qué he aprendido

- **`start()` vs `waitFor()`:** `start()` delega la tarea al SO y continúa, mientras que `waitFor()` congela el hilo de Java hasta recibir la señal de término del proceso.
- **Paralelismo real:** Los procesos se ejecutan concurrentemente en núcleos/procesos independientes administrados por el planificador del SO.
- **Código de salida:** La convención del SO para transmitir el éxito (`0`) o la naturaleza del fallo (`!=0`).
- **`IOException` vs ping fallido:** `IOException` ocurre cuando el SO no puede crear o encontrar el proceso. Un ping fallido es un proceso que se creó correctamente pero cuya lógica interna reportó un error de red (`exit code != 0`).

---

## 📂 Estructura del proyecto

```text
Reto2_Pipeline-Auditoria/
├── .gitignore
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── org/
    │   │       └── example/
    │   │           ├── Main.java
    │   │           └── PipelneAuditoria.java
    │   └── resources/
    └── test/
        └── java/
```

## 🔗 Enlace

GitHub: `https://github.com/MateoFB07/psp_udit/tree/main/reto02_pipeline-auditoria/Reto2_Pipeline-Auditoria`
